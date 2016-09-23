package Application.Services;

import javafx.application.Platform;
import java.util.concurrent.Executors;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;

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
    ExecutorService executorSingle = Executors.newSingleThreadExecutor();

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
    public void executorSingle(Runnable funcAsync, Runnable afterAsync) {
        completed.addListener((observable, oldValue, newValue) -> {
            if (newValue == true) {
                afterAsync.run();
            }
        });

        executorSingle.submit(() -> {
            funcAsync.run();
            close(executorSingle);
            completed.setValue(true);
        });
    }

    /**
     * Close executor
     *
     * @param executor
     */
    public void close(ExecutorService executor){
        try {
            executor.shutdown();
            executor.awaitTermination(0, TimeUnit.SECONDS);
        }
        catch (InterruptedException e) {}
        finally {
            executor.shutdownNow();
        }
    }
}
