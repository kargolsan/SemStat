package Application.Controllers.Application.Settings;

import Application.Services.Application.SettingsService;
import Application.Services.PropertyService;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * Created by IntelliJ IDEA.
 * User: Karol Golec
 * Date: 27.09.2016
 * Time: 22:05
 */
public class ProcessorGeneralController implements Initializable {

    @FXML
    private VBox root;

    @FXML
    private TextField limitThreads;

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
        SettingsService.add("processor.limit_threads", this.limitThreads.getText());
    }

    /**
     * Fill fields in interface
     */
    private void fillInterface(){
        String defaultPLT = PropertyService.get("default_processor_limit_threads", PROPERTIES_FILE);

        String getPLT = SettingsService.get("processor.limit_threads");

        getPLT = (getPLT == "") ? defaultPLT : getPLT;

        this.limitThreads.setText(getPLT);
    }
}
