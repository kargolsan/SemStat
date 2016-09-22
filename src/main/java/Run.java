import Application.Controllers.RunController;
import Application.Services.PropertyService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.InputStream;
import java.util.Properties;

/**
 * Created by IntelliJ IDEA.
 * User: Karol Golec
 * Date: 22.09.2016
 * Time: 11:49
 */
public class Run {

    /** @var logger */
    private static final Logger logger = LogManager.getLogger(Run.class);

    /**
     * Main function for application
     *
     * @param args arguments when the program starts
     */
    public static void main(String[] args) {
        RunController.runApplication(args);
    }
}
