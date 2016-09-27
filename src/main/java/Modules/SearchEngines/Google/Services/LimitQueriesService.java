package Modules.SearchEngines.Google.Services;

import Application.Contracts.SearchEngines.ILimitQueriesService;
import Application.Services.Application.SettingsService;
import Application.Services.PropertyService;

/**
 * Created by IntelliJ IDEA.
 * User: Karol Golec
 * Date: 26.09.2016
 * Time: 11:41
 */
public class LimitQueriesService implements ILimitQueriesService {

    /** @var file with properties of application */
    private static final String PROPERTIES_FILE = "Application/Resources/properties.properties";

    /**
     * Get limit queries of keyword
     * for search engine
     *
     * @return limit queries of keyword
     */
    @Override
    public Integer getLimitQueries() {
        String defaultGQK = PropertyService.get("default_google_queries_of_keyword", PROPERTIES_FILE);

        String queriesOfKeyword = SettingsService.get("google.queries_of_keyword");
        queriesOfKeyword = (queriesOfKeyword == "") ? defaultGQK : queriesOfKeyword;

        return Integer.parseInt(queriesOfKeyword);
    }
}
