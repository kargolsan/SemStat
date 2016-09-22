package Application.Services;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * Created by IntelliJ IDEA.
 * User: Karol Golec
 * Date: 22.09.2016
 * Time: 12:07
 */
public class PropertyService {

    /** @var logger */
    private static final Logger logger = LogManager.getLogger(PropertyService.class);

    /**
     * Get property from file properties  in resources
     *
     * @param property
     * @param file
     * @return value of property
     */
    public static String get(String property, String file){
        Properties prop = new Properties();
        InputStream input = null;
        String result = null;
        try {

            input = PropertyService.class.getClassLoader().getResourceAsStream(file);
            prop.load(input);
            result = prop.getProperty(property);
        } catch (Exception ex) {
            logger.fatal("Problem podczas wczytywanie właściwości z pliku properties.", ex);
        } finally{
            if(input!=null){
                try {
                    input.close();
                } catch (IOException e) {
                    logger.error(e);
                }
            }
        }
        return result;
    }
}
