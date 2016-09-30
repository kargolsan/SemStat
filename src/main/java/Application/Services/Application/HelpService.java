package Application.Services.Application;

import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebHistory;

/**
 * Created by IntelliJ IDEA.
 * User: Karol Golec
 * Date: 30.09.2016
 * Time: 09:31
 */
public class HelpService {

    /* @var web engine */
    private WebEngine webEngine;

    /**
     * Constructor
     *
     * @param webEngine
     */
    public HelpService(WebEngine webEngine){
        this.webEngine = webEngine;
    }

    /**
     * Go back
     */
    public void goBack()
    {
        WebHistory history = this.webEngine.getHistory();
        ObservableList<WebHistory.Entry> entryList = history.getEntries();
        int currentIndex = history.getCurrentIndex();

        Platform.runLater(() ->
        {
            history.go(entryList.size() > 1
                    && currentIndex > 0
                    ? -1
                    : 0);
        });
    }

    /**
     * Go forward
     */
    public void goForward()
    {
        WebHistory history = this.webEngine.getHistory();
        ObservableList<WebHistory.Entry> entryList = history.getEntries();
        int currentIndex = history.getCurrentIndex();

        Platform.runLater(() ->
        {
            history.go(entryList.size() > 1
                    && currentIndex < entryList.size() - 1
                    ? 1
                    : 0);
        });
    }
}
