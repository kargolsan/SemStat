package Modules.Bots.First.Services;

import Application.Contracts.Data.IResultModel;
import Application.Controllers.Application.BotController;
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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by IntelliJ IDEA.
 * User: Karol Golec
 * Date: 28.09.2016
 * Time: 13:21
 */
public class ParseService {

    /* @var counter analyzed websites */
    private Integer countAnalyzed;

    /* @var count websites with keyword*/
    private Integer countFound;

    /* @var domain analyzed */
    private Map<String, Integer> domainsAnalyzedLimit;

    /* @var urls to analyze */
    private List<String> urlsToAnalyze;

    /* @var list data for save */
    private List<IResultModel> resultsToSave;

    /* @var filtr extensions domain service */
    private FiltrExtensionsDomainService filtrExtensionsDomainService;

    /** @var bot service */
    private BotService bot;

    /* @var file with properties of application */
    private static final String PROPERTIES_FILE = "Application/Resources/properties.properties";

    /**
     * Constructor
     */
    public ParseService(FiltrExtensionsDomainService filtrExtensionsDomainService, BotService bot){
        this.filtrExtensionsDomainService = filtrExtensionsDomainService;
        this.bot = bot;

        this.domainsAnalyzedLimit = new HashMap<>();
        this.urlsToAnalyze = new ArrayList<>();
        this.resultsToSave = new ArrayList<>();
    }

    /**
     * Get results to save
     *
     * @return results
     */
    public List<IResultModel> getResultsToSave() {
        return resultsToSave;
    }

    /**
     * Get urls to analyze
     *
     * @return urls
     */
    public List<String> getUrlsToAnalyze() {
        return urlsToAnalyze;
    }

    /**
     * Document of JSOUP to DataModel
     *
     * @param doc
     * @param keyword
     */
    public void htmlToResult(Document doc, String keyword) {

        String domain = getDomainName(doc.baseUri());

        if (!this.filtrExtensionsDomainService.can(domain)) return;

        this.countAnalyzed += 1;
        BotController.setCountAnalyzed(this.countAnalyzed.toString());

        String body = doc.select("body").text();

        if (body.toLowerCase().contains(keyword.toLowerCase())) {

            if (domain == null) return;

            Integer quantity = StringUtils.countMatches(body.toLowerCase(), keyword.toLowerCase());

            IResultModel result = new Data();
            result.setDomain(domain);
            result.setUrl(doc.baseUri());
            result.setQuantity(quantity);
            result.setDate(Calendar.getInstance());
            result.setKeyword(keyword);

            addResultToSave(result);
        }
    }

    /**
     * parse urls from document
     *
     * @param doc
     */
    public void htmlToLinks(Document doc) {

        String domain = getDomainName(doc.baseUri());

        if (!this.filtrExtensionsDomainService.can(domain)) return;

        List<String> links = new ArrayList<>();

        Elements aElements = doc.body().getElementsByTag("a");

        for (Element a : aElements) {

            String href = a.attr("href");

            if (href != "" && href.contains("http://") || href.contains("https://")) {
                if (!href.startsWith("http")) {
                    links.addAll(extractLinks(href));
                } else {
                    links.add(href);
                }
            }
        }

        List<String> linksFromTextBody = extractLinks(doc.body().text());
        List<String> uniqueLinks = new ArrayList<>();

        links.stream().filter(link -> !uniqueLinks.contains(link)).forEach(uniqueLinks::add);

        linksFromTextBody.stream().filter(link -> !uniqueLinks.contains(link)).forEach(uniqueLinks::add);

        addLinksForAnalyze(uniqueLinks);
    }

    /**
     * Add domains
     *
     * @param linksFromHtml
     */
    public void addLinksForAnalyze(List<String> linksFromHtml) {
        for (String link : linksFromHtml) {

            String domain = getDomainName(link);
            if (domain == null) continue;

            if (!this.domainsAnalyzedLimit.containsKey(domain)) this.domainsAnalyzedLimit.put(domain, 0);

            String getRLS = SettingsService.get("robot.limit_sub_site_analyze_domain");
            String defaultRLS = PropertyService.get("default_robot_limit_sub_site_analyze_domain", PROPERTIES_FILE);

            getRLS = (getRLS == "") ? defaultRLS : getRLS;
            Integer limitDomain = Integer.parseInt(getRLS);

            if (this.domainsAnalyzedLimit.get(domain) >= limitDomain) continue;

            this.domainsAnalyzedLimit.put(domain, this.domainsAnalyzedLimit.get(domain) + 1);

            this.urlsToAnalyze.add(link);
        }
    }


    /**
     * Returns a list with all links contained in the input
     */
    public static List<String> extractLinks(String text) {
        List<String> containedUrls = new ArrayList<String>();
        String urlRegex = "((https?|ftp|gopher|telnet|file):((//)|(\\\\))+[\\w\\d:#@%/;$()~_?\\+-=\\\\\\.&]*)";
        Pattern pattern = Pattern.compile(urlRegex, Pattern.CASE_INSENSITIVE);
        Matcher urlMatcher = pattern.matcher(text);

        while (urlMatcher.find()) {
            containedUrls.add(text.substring(urlMatcher.start(0),
                    urlMatcher.end(0)));
        }

        return containedUrls;
    }

    /**
     * add data to list data
     *
     * @param data
     */
    public void addResultToSave(IResultModel data) {
        this.countFound += 1;

        this.resultsToSave.add(data);

        BotController.setCountFound(this.countFound.toString());
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
     * Call from clear bot
     */
    public void clearBot(){
        this.domainsAnalyzedLimit.clear();
        this.urlsToAnalyze.clear();
        this.resultsToSave.clear();
        this.countAnalyzed = 0;
        this.countFound = 0;
    }
}
