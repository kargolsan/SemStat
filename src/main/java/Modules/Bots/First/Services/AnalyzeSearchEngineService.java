package Modules.Bots.First.Services;

import Application.Contracts.Data.IDataModel;
import Application.Contracts.SearchEngines.IResultModel;
import Application.Controllers.Application.BottomStripController;
import Application.Services.PropertyService;
import Modules.Data.File.Models.Data;
import org.apache.commons.lang3.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Calendar;
import java.util.List;
import java.util.ResourceBundle;
import java.util.concurrent.ExecutorService;

/**
 * Created by IntelliJ IDEA.
 * User: Karol Golec
 * Date: 26.09.2016
 * Time: 13:01
 */
public class AnalyzeSearchEngineService {

    /** @car bot service */
    private BotService bot;

    /**
     * @var resource bundle
     */
    private ResourceBundle bundle;

    /**
     * Constructor
     *
     * @param bot
     * @param bundle
     */
    public AnalyzeSearchEngineService(BotService bot, ResourceBundle bundle){
        this.bot = bot;
        this.bundle = bundle;
    }

    /**
     * Analyze websites from search engine
     *
     * @param executor
     * @param webSitesSearchEngine
     * @param keyword
     */
    public void analyzeWebSitesSearchEngine(ExecutorService executor, List<IResultModel>  webSitesSearchEngine, String keyword) {
        String userAgent = PropertyService.get("user_agent", "Application/Resources/properties.properties");

        for (IResultModel website : webSitesSearchEngine) {

            if (this.bot.isInterrupted()) return;

            executor.execute(() -> {

                try {

                    Document doc = Jsoup.connect(website.getUrl()).userAgent(userAgent).get();
                    BottomStripController.setStatus(String.format(this.bundle.getString("robot.status.analyzing_website"), doc.baseUri()));

                    IDataModel data = this.bot.docToDataModel(doc, keyword);

                    List<String> urls = this.bot.docToUrls(doc);

                    if (urls != null){
                        this.bot.addUrlsForAnalyze(urls);
                    }

                    if (data != null){
                        this.bot.addToListData(data);
                    }

                } catch (IOException e) {}
            });
        }
    }
}
