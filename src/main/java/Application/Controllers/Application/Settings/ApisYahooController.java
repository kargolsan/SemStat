package Application.Controllers.Application.Settings;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.layout.VBox;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * Created by IntelliJ IDEA.
 * User: Karol Golec
 * Date: 27.09.2016
 * Time: 22:05
 */
public class ApisYahooController implements Initializable {

    @FXML
    private VBox root;

    /**
     * @var file with properties of application
     */
    private static final String PROPERTIES_FILE = "Application/Resources/properties.properties";

    /**
     * @var bundle
     */
    private ResourceBundle bundle;

    /**
     * Get root
     *
     * @return root
     */
    public VBox getRoot() {
        return root;
    }

    /**
     * Called to initialize a controller after its root element has been
     * completely processed.
     */
    @Override
    public void initialize(URL location, ResourceBundle bundle) {
        this.bundle = bundle;
        fillInterface();
    }

    /**
     * Save settings
     */
    public void save(){

    }

    /**
     * Fill fields in interface
     */
    private void fillInterface(){

    }
}
