package Application.Controllers.Application.Settings;

import Application.Services.Application.SettingsService;
import Application.Services.PropertyService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ComboBox;
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
public class BotGeneralController implements Initializable {

    @FXML
    private VBox root;
    @FXML
    private ComboBox<String> saveDataTo;
    @FXML
    private TextField limitSubsitesDomain;
    @FXML
    private TextField extensionsDomain;

    /**
     * Observable list for robot "save data to" option
     */
    public ObservableList<String> optionsSaveDataTo;

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
     * Constructor
     */
    public BotGeneralController() {
        this.optionsSaveDataTo = FXCollections.observableArrayList();
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
        SettingsService.add("robot.limit_sub_site_analyze_domain", this.limitSubsitesDomain.getText());
        SettingsService.add("robot.save_option", this.saveDataTo.getSelectionModel().getSelectedItem().toString());
        SettingsService.add("bot.general.extensions.domain", this.extensionsDomain.getText());
    }

    /**
     * Fill fields in interface
     */
    private void fillInterface(){
        this.optionsSaveDataTo.add(bundle.getString("tabs.settings.bot.general.save_option.to_file"));
        this.optionsSaveDataTo.add(bundle.getString("tabs.settings.bot.general.save_option.to_mysql"));
        this.saveDataTo.setItems(this.optionsSaveDataTo);

        String defaultRLS = PropertyService.get("default_robot_limit_sub_site_analyze_domain", PROPERTIES_FILE);
        String defaultBED = PropertyService.get("default_bot_extensions_domain", PROPERTIES_FILE);

        String getRLS = SettingsService.get("robot.limit_sub_site_analyze_domain");
        String getRSO = SettingsService.get("robot.save_option");
        String getBED = SettingsService.get("bot.general.extensions.domain");

        getRLS = (getRLS == "") ? defaultRLS : getRLS;
        getRSO = (getRSO == "") ? this.bundle.getString("tabs.settings.bot.general.save_option.to_file") : getRSO;
        getBED = (getBED == "") ? defaultBED : getBED;

        this.limitSubsitesDomain.setText(getRLS);
        this.saveDataTo.getSelectionModel().select(getRSO);
        this.extensionsDomain.setText(getBED);
    }
}