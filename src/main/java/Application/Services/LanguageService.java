package Application.Services;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.UnsupportedEncodingException;
import java.util.Enumeration;
import java.util.Locale;
import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;

/**
 * Created by IntelliJ IDEA.
 * User: Karol Golec
 * Date: 22.09.2016
 * Time: 13:41
 */
public class LanguageService {

    /** @var logger */
    private static final Logger logger = LogManager.getLogger(PropertyService.class);

    /**
     * Gets the unicode friendly resource bundle
     *
     * @param baseBundle
     * @see ResourceBundle#getBundle(String)
     * @return Unicode friendly resource bundle
     */
    public static ResourceBundle getBundle(String baseBundle) {

        Locale locale = Locale.getDefault();
        ResourceBundle resource;

        try {
            resource = createUtf8PropertyResourceBundle(
                    ResourceBundle.getBundle(baseBundle, locale));
            return resource;
        } catch (Exception ex)
        {
            logger.fatal("Problem podczas pobierania resource bundle.", ex);
        }

        return null;
    }

    /**
     * Creates unicode friendly {@link PropertyResourceBundle} if possible.
     *
     * @param bundle
     * @return Unicode friendly property resource bundle
     */
    private static ResourceBundle createUtf8PropertyResourceBundle(
            final ResourceBundle bundle) {
        if (!(bundle instanceof PropertyResourceBundle)) {
            return bundle;
        }
        return new Utf8PropertyResourceBundle((PropertyResourceBundle) bundle);
    }

    /**
     * Resource Bundle that does the hard work
     */
    private static class Utf8PropertyResourceBundle extends ResourceBundle {

        /**
         * Bundle with unicode data
         */
        private final PropertyResourceBundle bundle;

        /**
         * Initializing constructor
         *
         * @param bundle
         */
        private Utf8PropertyResourceBundle(final PropertyResourceBundle bundle) {
            this.bundle = bundle;
        }

        @Override
        @SuppressWarnings("unchecked")
        public Enumeration getKeys() {
            return bundle.getKeys();
        }

        @Override
        protected Object handleGetObject(final String key) {
            final String value = bundle.getString(key);
            if (value == null)
                return null;
            try {
                return new String(value.getBytes("ISO-8859-1"), "UTF-8");
            } catch (final UnsupportedEncodingException e) {
                throw new RuntimeException("Encoding not supported", e);
            }
        }
    }
}
