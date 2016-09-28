package Application.Contracts.Bots;

/**
 * Created by IntelliJ IDEA.
 * User: Karol Golec
 * Date: 23.09.2016
 * Time: 22:35
 */
public interface IBotController {

    /**
     * Rub robot
     *
     * @param keyword
     * @param filtrExtensionsDomain
     */
    void start(String keyword, String filtrExtensionsDomain);

    /**
     * Interrupt robot
     */
    void interrupt();
}
