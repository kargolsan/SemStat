package Application.Contracts.Data;

import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: Karol Golec
 * Date: 23.09.2016
 * Time: 19:12
 */
public interface IDataService {

    /**
     * Save data with provider
     *
     * @param data
     */
    void save(List<IDataModel> data);

    /**
     * Check is exist domain with keyword
     *
     * @param data
     */
    Boolean exist(IDataModel data);
}
