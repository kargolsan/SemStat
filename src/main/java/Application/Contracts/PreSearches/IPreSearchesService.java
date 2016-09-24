package Application.Contracts.PreSearches;

import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: Karol Golec
 * Date: 23.09.2016
 * Time: 23:18
 */
public interface IPreSearchesService {

    /**
     * Ger search results
     *
     * @param query
     * @param page
     * @return list with results
     */
    List<IResultModel> get(String query, Integer page);
}
