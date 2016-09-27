package Application.Contracts.SearchEngines;

/**
 * Created by IntelliJ IDEA.
 * User: Karol Golec
 * Date: 26.09.2016
 * Time: 11:37
 */
public interface ILimitQueriesService {

    /**
     * Get limit queries of keyword
     * for search engine
     *
     * @return limit queries of keyword
     */
    Integer getLimitQueries();
}
