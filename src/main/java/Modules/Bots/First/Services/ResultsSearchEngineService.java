package Modules.Bots.First.Services;

import Application.Contracts.SearchEngines.IResultModel;
import Application.Contracts.SearchEngines.ISearchEngine;
import Application.Controllers.Application.BottomStripController;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

/**
 * Created by IntelliJ IDEA.
 * User: Karol Golec
 * Date: 26.09.2016
 * Time: 11:57
 */
public class ResultsSearchEngineService {

    /**
     * Get results from search engine
     *
     * @param keywords
     * @param searchEngine
     * @param interrupt
     * @param bundle
     * @return results from search engine
     */
    public List<IResultModel> getResultsSearchEngine(String keywords, ISearchEngine searchEngine, Boolean interrupt, ResourceBundle bundle) {

        Integer limitQueries = searchEngine.getLimit().getLimitQueries();

        List<IResultModel> resultsSearchEngine = new ArrayList<>();

        Integer countQuery = 0;

        while(true) {
            if (limitQueries <= countQuery) break;

            List<IResultModel> queryResult = searchEngine.get(keywords, countQuery);

            if (queryResult.size() == 0 || interrupt == true) break;

            resultsSearchEngine.addAll(queryResult);

            countQuery++;

            BottomStripController.setStatus(String.format(bundle.getString("robot.status.query_number_to_search_engine"), countQuery));
        }

        return resultsSearchEngine;
    }
}
