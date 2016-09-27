package Modules.Bots.First.Services;

import Application.Contracts.SearchEngines.ISearchEngine;
import Application.Contracts.SearchEngines.IResultModel;
import Application.Contracts.Data.IDataModel;
import Application.Contracts.Data.IDataService;
import Application.Controllers.Application.BotController;
import Application.Controllers.Application.BottomStripController;
import Application.Controllers.Application.LogsController;
import Application.Services.Application.SettingsService;
import Application.Services.PropertyService;
import Modules.Data.File.Models.Data;
import org.apache.commons.lang3.StringUtils;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by IntelliJ IDEA.
 * User: Karol Golec
 * Date: 23.09.2016
 * Time: 17:11
 */
public class BotService {

    /**
     * @var interrupt analyzer
     */
    private Boolean interrupt = false;

    /**
     * @var domain analyzed
     */
    private Map<String, Integer> domainsAnalyzed;

    /** @cvar urls to analyze */
    private List<String> urlsToAnalyze;

    /**
     * @var list data for save
     */
    private List<IDataModel> listData;

    /**
     * @var search engine
     */
    private ISearchEngine searchEngine;

    /**
     * @var data save service
     */
    private IDataService data;

    /**
     * @var runnable for finally threads
     */
    Runnable finallyThreads;

    /**
     * @var processor service
     */
    ProcessorService processor;

    /**
     * @var analyze search engine service
     */
    AnalyzeSearchEngineService analyzeSearchEngine;

    /**
     * @var analyze long service
     */
    AnalyzeLong analyzeLong;

    /**
     * @var resource bundle
     */
    private ResourceBundle bundle;

    /**
     * @var results search engine
     */
    ResultsSearchEngineService resultsSearchEngine;

    /** @var counter analyzed websites */
    Integer countAnalyzeWebsites;

    /** @var count websites with keyword */
    Integer countWebsitesWithKeyword;

    /**
     * @var file with properties of application
     */
    private static final String PROPERTIES_FILE = "Application/Resources/properties.properties";

    /**
     * Get urls to analyze
     *
     * @return urls
     */
    public List<String> getUrlsToAnalyze() {
        return urlsToAnalyze;
    }

    /**
     * Constructor
     *
     * @param searchEngine
     * @param data
     * @param finallyThreads
     * @param bundle
     */
    public BotService(ISearchEngine searchEngine, IDataService data, Runnable finallyThreads, ResourceBundle bundle) {
        this.listData = new ArrayList<>();
        this.urlsToAnalyze = new ArrayList<>();
        this.domainsAnalyzed = new HashMap<String, Integer>();

        this.searchEngine = searchEngine;
        this.data = data;
        this.finallyThreads = finallyThreads;
        this.bundle = bundle;

        this.resultsSearchEngine = new ResultsSearchEngineService();
        this.processor = new ProcessorService();
        this.analyzeSearchEngine = new AnalyzeSearchEngineService(this, this.bundle);
        this.analyzeLong = new AnalyzeLong(this, this.bundle);
    }

    /**
     * Rub robot
     *
     * @param keyword
     */
    public void start(String keyword) {

        this.interrupt = false;

        this.listData.clear();
        this.domainsAnalyzed.clear();
        this.urlsToAnalyze.clear();

        this.countAnalyzeWebsites = 0;
        this.countWebsitesWithKeyword = 0;

        List<IResultModel> webSitesSearchEngine = this.resultsSearchEngine.getResultsSearchEngine(keyword, this.searchEngine, this.interrupt, this.bundle);

        automationBot(webSitesSearchEngine, keyword);
    }

    /**
     * Run automation bot
     *
     * @param webSitesSearchEngine
     * @param keyword
     */
    private void automationBot(List<IResultModel> webSitesSearchEngine, String keyword) {

        ExecutorService executor = Executors.newFixedThreadPool(processor.getLimitThreads());

        this.analyzeSearchEngine.analyzeWebSitesSearchEngine(executor, webSitesSearchEngine, keyword);

        executor.shutdown();

        while (!executor.isTerminated()) {
        }

        ExecutorService executorLong = Executors.newFixedThreadPool(processor.getLimitThreads());

        this.analyzeLong.start(executorLong, keyword);

        executorLong.shutdown();

        while (!executorLong.isTerminated()) {
        }

        finish(keyword);
    }

    /**
     * add data to list data
     *
     * @param data
     */
    public void addToListData(IDataModel data) {
        this.countWebsitesWithKeyword += 1;
        BotController.setQuantityWebsitesWithKeywordProperty(this.countWebsitesWithKeyword.toString());

        this.listData.add(data);
    }

    /**
     * Finish analyze
     *
     * @param keyword
     */
    private void finish(String keyword) {
        BottomStripController.setStatus(String.format(this.bundle.getString("robot.status.data_saving"), keyword));

        this.data.save(this.listData);
        this.finallyThreads.run();

        LogsController.success(String.format(this.bundle.getString("robot.log.data_saved"), keyword));
        BottomStripController.setStatus(String.format(this.bundle.getString("robot.status.job_finished"), keyword));
        LogsController.success(String.format(this.bundle.getString("robot.log.job_finished"), keyword));
    }

    /**
     * Stop analyze
     *
     * @param keyword
     */
    private void stop(String keyword) {

//        if (this.listData.size() > 0) {
//            this.data.save(this.listData);
//            this.listData.clear();
//            LogsController.success(String.format(this.bundle.getString("robot.log.saved_analyzed_data_interrupted"), keyword));
//        }

//        BottomStripController.setStatus(String.format(this.bundle.getString("robot.status.bot_interrupted"), keyword));
//        LogsController.info(String.format(this.bundle.getString("robot.log.bot_interrupted"), keyword));

//        this.domainsAnalyzed.clear();
//        this.finallyThreads.run();
    }


    /**
     * Interrupt robot
     */
    public void interrupt() {
        this.interrupt = true;
    }

    /**
     * Check bot is interrupted
     *
     * @return true if interrupted or false if don't interrupted
     */
    public Boolean isInterrupted() {
        if (this.interrupt == true) {
            return true;
        }

        return false;
    }

    /**
     * Document of JSOUP to DataModel
     *
     * @param doc
     * @param keyword
     */
    public IDataModel docToDataModel(Document doc, String keyword) {

        this.countAnalyzeWebsites += 1;
        BotController.setAnalyzedWebsitesProperty(this.countAnalyzeWebsites.toString());

        String text = doc.select("body").text();
        if (text.toLowerCase().contains(keyword.toLowerCase())) {
            if (getDomainName(doc.baseUri()) == null) return null;

            Integer quantity = StringUtils.countMatches(text.toLowerCase(), keyword.toLowerCase());
            IDataModel data = new Data();
            data.setDomain(getDomainName(doc.baseUri()));
            data.setUrl(doc.baseUri());
            data.setQuantity(quantity);
            data.setDate(Calendar.getInstance());
            data.setKeyword(keyword);

            return data;
        }

        return null;
    }

    /**
     * parse urls from document
     *
     * @param doc
     */
    public List<String> docToUrls(Document doc) {

        List<String> results = new ArrayList<>();

        Elements links = doc.body().getElementsByTag("a");

        for (Element link : links) {
            String href = link.attr("href");
            if (href != "" && href.contains("http://") || href.contains("https://")) {
                if (!href.startsWith("http")){
                    List<String> extractedUrls = extractUrls(href);
                    for (String url : extractedUrls)
                    {
                        results.add(url);
                    }
                } else {
                    results.add(href);
                }

            }
        }

        return results;
    }

    /**
     * Get domain from url
     *
     * @param url
     * @return
     */
    public String getDomainName(String url) {
        URI uri = null;
        try {
            uri = new URI(url);
            String domain = uri.getHost();
            return domain.startsWith("www.") ? domain.substring(4) : domain;
        } catch (URISyntaxException e) {
        }
        return null;
    }

    /**
     * Add domains
     *
     * @param urlsFromDoc
     */
    public void addUrlsForAnalyze(List<String> urlsFromDoc) {
        for (String urlFromDoc : urlsFromDoc) {

            String domainFromDoc = getDomainName(urlFromDoc);
            if (domainFromDoc == null) continue;

            if (!this.domainsAnalyzed.containsKey(domainFromDoc)) this.domainsAnalyzed.put(domainFromDoc, 0);

            String getRLS = SettingsService.get("robot.limit_sub_site_analyze_domain");
            String defaultRLS = PropertyService.get("default_robot_limit_sub_site_analyze_domain", PROPERTIES_FILE);

            getRLS = (getRLS == "") ? defaultRLS : getRLS;
            Integer limitDomain = Integer.parseInt(getRLS);

            if (this.domainsAnalyzed.get(domainFromDoc) >= limitDomain) continue;

            this.domainsAnalyzed.put(domainFromDoc, this.domainsAnalyzed.get(domainFromDoc) + 1);

            this.urlsToAnalyze.add(urlFromDoc);
        }
    }

    /**
     * Returns a list with all links contained in the input
     */
    public static List<String> extractUrls(String text)
    {
        List<String> containedUrls = new ArrayList<String>();
        String urlRegex = "((https?|ftp|gopher|telnet|file):((//)|(\\\\))+[\\w\\d:#@%/;$()~_?\\+-=\\\\\\.&]*)";
        Pattern pattern = Pattern.compile(urlRegex, Pattern.CASE_INSENSITIVE);
        Matcher urlMatcher = pattern.matcher(text);

        while (urlMatcher.find())
        {
            containedUrls.add(text.substring(urlMatcher.start(0),
                    urlMatcher.end(0)));
        }

        return containedUrls;
    }
}
