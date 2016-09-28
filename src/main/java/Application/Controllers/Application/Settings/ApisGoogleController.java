package Application.Controllers.Application.Settings;

import java.net.URL;
import javafx.fxml.FXML;
import javafx.scene.layout.VBox;
import java.util.ResourceBundle;
import javafx.fxml.Initializable;
import javafx.scene.control.TextField;
import Application.Services.PropertyService;
import Application.Services.Application.SettingsService;

/**
 * Created by IntelliJ IDEA.
 * User: Karol Golec
 * Date: 27.09.2016
 * Time: 22:05
 */
public class ApisGoogleController implements Initializable {

    @FXML
    private VBox root;

    @FXML
    private TextField apiKey;

    @FXML
    private TextField searchEngineId;

    @FXML
    private TextField limitQueriesKeyword;

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
        SettingsService.add("google.api_key", this.apiKey.getText());
        SettingsService.add("google.search_engine_id", this.searchEngineId.getText());
        SettingsService.add("google.queries_of_keyword", this.limitQueriesKeyword.getText());
    }

    /**
     * Fill fields in interface
     */
    private void fillInterface(){
        String defaultGQK = PropertyService.get("default_google_queries_of_keyword", PROPERTIES_FILE);

        String getGAK = SettingsService.get("google.api_key");
        String getGSE = SettingsService.get("google.search_engine_id");
        String getGQK = SettingsService.get("google.queries_of_keyword");

        getGQK = (getGQK == "") ? defaultGQK : getGQK;

        this.apiKey.setText(getGAK);
        this.searchEngineId.setText(getGSE);
        this.limitQueriesKeyword.setText(getGQK);
    }
}