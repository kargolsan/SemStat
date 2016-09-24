package Modules.PreSearchers.MonsterCrawler.Services;

import Application.Contracts.PreSearches.IPreSearchesService;
import Application.Contracts.PreSearches.IResultModel;
import Modules.PreSearchers.MonsterCrawler.Models.Result;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: Karol Golec
 * Date: 23.09.2016
 * Time: 16:08
 */
public class PreSearchService implements IPreSearchesService {

    /** @var driver */
    WebDriver driver;

    /** @var logger */
    private static final Logger logger = LogManager.getLogger(PreSearchService.class);

    /**
     * Constructor
     *
     * @param driver
     */
    public PreSearchService(WebDriver driver){
        this.driver = driver;
    }

    /**
     * Ger search results
     *
     * @param query
     * @param page
     * @return list with results
     */
    public List<IResultModel> get(String query, Integer page){
        List<IResultModel> result = new ArrayList<IResultModel>();

        String url = String.format("http://search.monstercrawler.com/search/web?q=\"%1$s\"&p=%2$s", query, page);
        this.driver.get(url);

        try {
            String html = this.driver.findElement(By.tagName("html")).getAttribute("outerHTML");
            Document doc = Jsoup.parse(html);
            Elements searchResults = doc.select("#insp_result_main_0 span");
            for (Element s : searchResults){
                if (s == null) continue;
                IResultModel r = new Result();
                Element title = s.select(".resultTitlePane a").first();
                r.setTitle(title.text());
                r.setUrl(title.attr("href"));
                result.add(r);
            }
        } catch (Exception e) {}
        return result;
    }
}
