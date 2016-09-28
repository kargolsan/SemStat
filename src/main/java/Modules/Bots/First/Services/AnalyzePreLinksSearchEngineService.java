package Modules.Bots.First.Services;

import java.util.List;
import org.jsoup.Jsoup;
import java.io.IOException;
import org.jsoup.nodes.Document;
import java.util.ResourceBundle;
import Application.Services.PropertyService;
import java.util.concurrent.ExecutorService;
import Application.Contracts.Data.IResultModel;
import Application.Controllers.Application.BottomStripController;

/**
 * Created by IntelliJ IDEA.
 * User: Karol Golec
 * Date: 26.09.2016
 * Time: 13:01
 */
public class AnalyzePreLinksSearchEngineService {

    /* @car bot service */
    private BotService bot;

    /* @var resource bundle */
    private ResourceBundle bundle;

    /* @var user agent for jsoup */
    private static final String USER_AGENT = PropertyService.get("user_agent", "Application/Resources/properties.properties");

    /**
     * Constructor
     *
     * @param bot
     * @param bundle
     */
    public AnalyzePreLinksSearchEngineService(BotService bot, ResourceBundle bundle){
        this.bot = bot;
        this.bundle = bundle;
    }

    /**
     * Analyze links from search engine
     *
     * @param executor
     * @param preLinksApiSearchEngine
     * @param keyword
     */
    public void analyze(ExecutorService executor, List<Application.Contracts.SearchEngines.IResultModel>  preLinksApiSearchEngine, String keyword) {

        for (Application.Contracts.SearchEngines.IResultModel link : preLinksApiSearchEngine) {

            if (this.bot.isInterrupted()) return;

            if (this.bot.getParseService().hasDomainExceptInFile(link.getUrl())) continue;

            executor.execute(() -> {

                try {
                    Document html = Jsoup.connect(link.getUrl()).userAgent(USER_AGENT).get();

                    BottomStripController.setStatus(String.format(this.bundle.getString("robot.status.analyzing_website"), html.baseUri()));

                    this.bot.getParseService().htmlToResult(html, keyword);

                    this.bot.getParseService().htmlToLinks(html);

                    this.bot.getParseService().getUrlsAnalyzed().add(link.getUrl());

                } catch (IOException e) {}
            });
        }
    }
}