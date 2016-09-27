package Modules.Bots.First.Services;

import Application.Contracts.Data.IDataModel;
import Application.Contracts.SearchEngines.IResultModel;
import Application.Controllers.Application.BotController;
import Application.Controllers.Application.BottomStripController;
import Application.Controllers.Application.LogsController;
import Application.Services.PropertyService;
import Modules.Data.File.Models.Data;
import Modules.Data.File.Services.FileDataService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by IntelliJ IDEA.
 * User: Karol Golec
 * Date: 26.09.2016
 * Time: 13:37
 */
public class AnalyzeLong {

    /** @var logger */
    private static final Logger logger = LogManager.getLogger(AnalyzeLong.class);

    /**
     * @var resource bundle
     */
    private ResourceBundle bundle;

    /** @var bot service */
    private BotService bot;

    /** @var text file with urls */
    private static final String TEXT_FILE = "urls.txt";

    /**
     * Constructor
     *
     * @param bot
     */
    public AnalyzeLong(BotService bot, ResourceBundle bundle){
        this.bot = bot;
        this.bundle = bundle;
    }

    /**
     * Analyze long websites
     *
     * @param executor
     * @param keyword
     */
    public void start(ExecutorService executor, String keyword) {

        List<String> urlsToAnalyzed = new ArrayList<>();

        if (this.bot.isInterrupted()) return;

        String userAgent = PropertyService.get("user_agent", "Application/Resources/properties.properties");

        urlsToAnalyzed.addAll(addDomainsFromFile(keyword));
        urlsToAnalyzed.addAll(this.bot.getUrlsToAnalyze());

        if (urlsToAnalyzed.size() < getMinimalityUrls()){
            LogsController.error(String.format(this.bundle.getString("robot.log.require_minimality_fifteen_urls_for_long_analyzed"), getMinimalityUrls()));
            return;
        }

        while (urlsToAnalyzed.size() > 0) {

            String url = urlsToAnalyzed.get(0);
            urlsToAnalyzed.remove(0);

//            Integer counterUrlsToAnalyze = this.bot.getUrlsToAnalyze().size()+ urlsToAnalyzed.size();
//            if (counterUrlsToAnalyze > 0) {
//                BotController.setUrlsToAnalyzeProperty(Objects.toString(counterUrlsToAnalyze));
//            }

            if (this.bot.isInterrupted()) return;

            executor.execute(() -> {

                if (this.bot.isInterrupted()) return;

                try {
                    Document doc = Jsoup.connect(url).userAgent(userAgent).get();

                    BottomStripController.setStatus(String.format(this.bundle.getString("robot.status.analyzing_website"), doc.baseUri()));

                    IDataModel data = this.bot.docToDataModel(doc, keyword);

                    if (data != null){
                        this.bot.addToListData(data);
                    }

                    List<String> urls = this.bot.docToUrls(doc);

                    this.bot.addUrlsForAnalyze(urls);

                } catch (Exception e) { }


            });
            if (urlsToAnalyzed.size() <= 0){
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                urlsToAnalyzed.addAll(this.bot.getUrlsToAnalyze());
                this.bot.getUrlsToAnalyze().clear();

            }


        }
    }

    /**
     * Add urls to list from file
     * to long analyzed keyword
     *
     * @param keyword
     * @return
     */
    private List<String> addDomainsFromFile(String keyword){
        List<String> urls = new ArrayList<>();

        File f = new File(TEXT_FILE);
        if(!f.exists() || f.isDirectory()) return urls;

        BufferedReader br = null;
        String sCurrentLine;

        try {
            br = new BufferedReader(new FileReader(TEXT_FILE));

            while ((sCurrentLine = br.readLine()) != null) {
                if (sCurrentLine == "") continue;

                if (!sCurrentLine.contains("http")) sCurrentLine = "http://" + sCurrentLine;
                sCurrentLine = sCurrentLine.replace("{{keyword}}", keyword);

                urls.add(sCurrentLine);
            }

            this.bot.addUrlsForAnalyze(urls);
        } catch (IOException e) {
            logger.error("Błąd podczas odczytywania danych z pliku urls.txt", e);
        } finally {
            try {
                if (br != null)br.close();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
        return urls;
    }


    /**
     * Get minimality urls for urls.txt
     *
     * @return property
     */
    private Integer getMinimalityUrls(){
        String prop = PropertyService.get("minimality_urls_in_file_for_long_analyzed", "Application/Resources/properties.properties");
        return Integer.parseInt(prop);
    }
}
