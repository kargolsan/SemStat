package Application.Contracts.Extensions;

import Application.Contracts.Data.IResultModel;
import org.jsoup.nodes.Document;

import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: Karol Golec
 * Date: 28.09.2016
 * Time: 20:16
 */
public interface IExtension {

    /**
     * Check extension can be used
     *
     * @return true if can or false if can not
     */
    Boolean access();

    /**
     * Analyze in extension
     *
     * @param doc
     */
    void analyze(Document doc);

    /**
     * Clear data in extension
     */
    void clear();

    /**
     * Call if bot finished
     */
    void finish();

    /**
     * Run before save for modification results
     *
     * @param resultsToSave
     * @return resultsToSave
     */
    List<IResultModel> beforeSaved(List<IResultModel> resultsToSave);
}
