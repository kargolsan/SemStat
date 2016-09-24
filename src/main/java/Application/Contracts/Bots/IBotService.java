package Application.Contracts.Bots;

/**
 * Created by IntelliJ IDEA.
 * User: Karol Golec
 * Date: 23.09.2016
 * Time: 22:35
 */
public interface IBotService {

    /**
     * Rub robot
     *
     * @param keyWords
     * @param afterEnd
     * @param afterStop
     */
    void run(String keyWords, Runnable afterEnd, Runnable afterStop);

    /**
     * Stop robot
     */
    void callStop();
}
