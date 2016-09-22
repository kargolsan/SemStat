package Application.Services;

import javafx.application.Platform;
import java.util.concurrent.Executors;
import java.util.concurrent.ExecutorService;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;

/**
 * Created by IntelliJ IDEA.
 * User: Karol Golec
 * Date: 22.09.2016
 * Time: 14:19
 */
public class AsyncService {

    /**
     * Task asynchronous
     */
    ExecutorService task = Executors.newSingleThreadExecutor();

    /**
     * Set true if function asynchronous is completed
     */
    BooleanProperty completed = new SimpleBooleanProperty(false);

    /**
     * Run async task with task after completed async
     *
     * @param funcAsync function asynchronous
     * @param afterAsync function synchronous after completed async function
     */
    public void run(Runnable funcAsync, Runnable afterAsync) {
        completed.addListener((observable, oldValue, newValue) -> {
            if (newValue == true) {
                Platform.runLater(() -> {
                    afterAsync.run();
                });
            }
        });

        task.execute(() -> {
            Platform.runLater(() -> {
                funcAsync.run();
                completed.setValue(true);
            });
        });
    }
}
