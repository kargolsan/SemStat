package Modules.Bots.First.Services;

import Application.Contracts.Data.IResultModel;
import Application.Controllers.Application.BottomStripController;
import Application.Controllers.Application.LogsController;
import Application.Services.PropertyService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

/**
 * Created by IntelliJ IDEA.
 * User: Karol Golec
 * Date: 26.09.2016
 * Time: 13:37
 */
public class AnalyzeBotLong {

    /** @var logger */
    private static final Logger logger = LogManager.getLogger(AnalyzeBotLong.class);

    /* @var resource bundle*/
    private ResourceBundle bundle;

    /* @var bot service */
    private BotService bot;

    /* @var text file with urls */
    private static final String TEXT_FILE = "urls.txt";

    /* @var user agent for jsoup */
    private static final String USER_AGENT = PropertyService.get("user_agent", "Application/Resources/properties.properties");

    /**
     * Constructor
     *
     * @param bot
     */
    public AnalyzeBotLong(BotService bot, ResourceBundle bundle){
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

        // blokada dodawania linków do wyników jeżeli domena istnieje w linkach wstępnych

        List<Future<?>> futures = new ArrayList<Future<?>>();

        if (this.bot.isInterrupted()) return;

        this.bot.getParseService().addLinksForAnalyze(addUrlsFromFile(keyword));
        this.bot.getParseService().addExceptDomains(addUrlsFromFile(keyword));

        if (this.bot.getParseService().getUrlsToAnalyze().size() < getMinimalityLinks()){
            LogsController.error(String.format(this.bundle.getString("robot.log.require_minimality_fifteen_urls_for_long_analyzed"), getMinimalityLinks()));
            return;
        }

        while (this.bot.getParseService().getUrlsToAnalyze().size() > 0) {

            String url = this.bot.getParseService().getUrlsToAnalyze().get(0);

            if (url == null) continue;

            this.bot.getParseService().getUrlsAnalyzed().add(url);
            this.bot.getParseService().getUrlsToAnalyze().remove(url);

            if (this.bot.getParseService().hasDomainExceptInFile(url)) continue;

            if (this.bot.isInterrupted()) return;

            Future<?> f = executor.submit(() -> {
                if (this.bot.isInterrupted()) return;
                try {
                    Document doc = Jsoup.connect(url).userAgent(USER_AGENT).get();

                    BottomStripController.setStatus(String.format(this.bundle.getString("robot.status.analyzing_website"), doc.baseUri()));

                    this.bot.getParseService().htmlToResult(doc, keyword);

                    this.bot.getParseService().htmlToLinks(doc);

                } catch (Exception e) { }
            });

            futures.add(f);
            checkLinksIsEmpty(futures);
        }
    }

    /**
     * Add urls to list from file
     * to long analyzed keyword
     *
     * @param keyword
     * @return
     */
    private List<String> addUrlsFromFile(String keyword){
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
     * Check links is empty
     *
     * @param futures
     */
    private void checkLinksIsEmpty(List<Future<?>> futures){

        if (this.bot.getParseService().getUrlsToAnalyze().size() <= 0){
            for(Future<?> future : futures)
                try {
                    future.get();
                    if (this.bot.isInterrupted()) return;
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (ExecutionException e) {
                    e.printStackTrace();
                }
        }
    }

    /**
     * Get minimality urls for urls.txt
     *
     * @return property
     */
    private Integer getMinimalityLinks(){
        String prop = PropertyService.get("minimality_urls_in_file_for_long_analyzed", "Application/Resources/properties.properties");
        return Integer.parseInt(prop);
    }
}
