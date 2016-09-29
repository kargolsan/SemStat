package Application.Controllers.Application;

import java.net.URL;
import javafx.fxml.FXML;
import java.util.ResourceBundle;
import javafx.fxml.Initializable;
import javafx.scene.control.TitledPane;
import javafx.scene.layout.AnchorPane;
import Application.Services.AlertService;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import Application.Controllers.Application.Settings.*;
import javafx.scene.layout.HBox;

/**
 * Created by IntelliJ IDEA.
 * User: Karol Golec
 * Date: 22.09.2016
 * Time: 16:17
 */
public class SettingsController implements Initializable {

    @FXML
    private HBox container;
    @FXML
    private TitledPane title;
    @FXML
    private DefaultController defaultController;
    @FXML
    private ApisGoogleController apisGoogleController;
    @FXML
    private ApisYahooController apisYahooController;
    @FXML
    private BotGeneralController botGeneralController;
    @FXML
    private ProcessorGeneralController processorGeneralController;
    @FXML
    private SaveDataFileController saveDataFileController;
    @FXML
    private SaveDataMysqlController saveDataMysqlController;
    @FXML
    private OtherLicenseController otherLicenseController;

    /** @vars visible roots */
    private BooleanProperty visibleDefault = new SimpleBooleanProperty(false);
    private BooleanProperty visibleApisGoogle = new SimpleBooleanProperty(false);
    private BooleanProperty visibleApisYahoo = new SimpleBooleanProperty(false);
    private BooleanProperty visibleBotGeneral = new SimpleBooleanProperty(false);
    private BooleanProperty visibleProcessorGeneral = new SimpleBooleanProperty(false);
    private BooleanProperty visibleSaveDataFile = new SimpleBooleanProperty(false);
    private BooleanProperty visibleSaveDataMysql = new SimpleBooleanProperty(false);
    private BooleanProperty visibleOtherLicense = new SimpleBooleanProperty(false);

    /**
     * @var bundle
     */
    private ResourceBundle bundle;

    /**
     * @var file with properties of application
     */
    private static final String PROPERTIES_FILE = "Application/Resources/properties.properties";

    /**
     * Called to initialize a controller after its root element has been
     * completely processed.
     */
    @Override
    public void initialize(URL location, ResourceBundle bundle) {
        this.bundle = bundle;

        loadVisibleRoots();

        loadVisibleDefaultRoot();
    }

    /**
     * Show root
     */
    @FXML
    public void apisGoogle(){
        hideRoots();
        this.title.setText(this.bundle.getString("tabs.settings.apis.google"));
        this.visibleApisGoogle.set(true);
    }

    /**
     * Show root
     */
    @FXML
    public void apisYahoo(){
        hideRoots();
        this.title.setText(this.bundle.getString("tabs.settings.apis.yahoo"));
        this.visibleApisYahoo.set(true);
    }

    /**
     * Show root
     */
    @FXML
    public void botGeneral(){
        hideRoots();
        this.title.setText(this.bundle.getString("tabs.settings.bot.general"));
        this.visibleBotGeneral.set(true);
    }

    /**
     * Show root
     */
    @FXML
    public void processorGeneral(){
        hideRoots();
        this.title.setText(this.bundle.getString("tabs.settings.processor.general"));
        this.visibleProcessorGeneral.set(true);
    }

    /**
     * Show root
     */
    @FXML
    public void saveDataFile(){
        hideRoots();
        this.title.setText(this.bundle.getString("tabs.settings.save_data.file"));
        this.visibleSaveDataFile.set(true);
    }

    /**
     * Show root
     */
    @FXML
    public void saveDataMysql(){
        hideRoots();
        this.title.setText(this.bundle.getString("tabs.settings.save_data.mysql"));
        this.visibleSaveDataMysql.set(true);
    }

    /**
     * Show root
     */
    @FXML
    public void otherLicense(){
        hideRoots();
        this.title.setText(this.bundle.getString("tabs.settings.other.license"));
        this.visibleOtherLicense.set(true);
    }

    /**
     * Save all settings
     */
    @FXML
    public void save() {
        this.apisGoogleController.save();
        this.apisYahooController.save();
        this.botGeneralController.save();
        this.processorGeneralController.save();
        this.saveDataFileController.save();
        this.saveDataMysqlController.save();
        this.otherLicenseController.save();

        LogsController.success(this.bundle.getString("tabs.settings.setting_saved"));
        AlertService.info(this.bundle.getString("dialog_alert.information"), null, this.bundle.getString("tabs.settings.setting_saved"));
    }

    /**
     * Set visible for roots in container
     */
    private void loadVisibleRoots(){
        this.defaultController.getRoot().visibleProperty().bind(this.visibleDefault);
        this.apisGoogleController.getRoot().visibleProperty().bind(this.visibleApisGoogle);
        this.apisYahooController.getRoot().visibleProperty().bind(this.visibleApisYahoo);
        this.botGeneralController.getRoot().visibleProperty().bind(this.visibleBotGeneral);
        this.processorGeneralController.getRoot().visibleProperty().bind(this.visibleProcessorGeneral);
        this.saveDataFileController.getRoot().visibleProperty().bind(this.visibleSaveDataFile);
        this.saveDataMysqlController.getRoot().visibleProperty().bind(this.visibleSaveDataMysql);
        this.otherLicenseController.getRoot().visibleProperty().bind(this.visibleOtherLicense);

        this.defaultController.getRoot().managedProperty().bind(this.visibleDefault);
        this.apisGoogleController.getRoot().managedProperty().bind(this.visibleApisGoogle);
        this.apisYahooController.getRoot().managedProperty().bind(this.visibleApisYahoo);
        this.botGeneralController.getRoot().managedProperty().bind(this.visibleBotGeneral);
        this.processorGeneralController.getRoot().managedProperty().bind(this.visibleProcessorGeneral);
        this.saveDataFileController.getRoot().managedProperty().bind(this.visibleSaveDataFile);
        this.saveDataMysqlController.getRoot().managedProperty().bind(this.visibleSaveDataMysql);
        this.otherLicenseController.getRoot().managedProperty().bind(this.visibleOtherLicense);
    }

    /**
     * Hide all roots
     */
    private void hideRoots(){
        this.visibleDefault.set(false);
        this.visibleApisGoogle.set(false);
        this.visibleApisYahoo.set(false);
        this.visibleBotGeneral.set(false);
        this.visibleProcessorGeneral.set(false);
        this.visibleSaveDataFile.set(false);
        this.visibleSaveDataMysql.set(false);
        this.visibleOtherLicense.set(false);
    }

    /**
     * Load default visible root
     */
    private void loadVisibleDefaultRoot(){
        this.visibleDefault.set(true);
        this.title.setText(this.bundle.getString("tabs.settings.default.title.settings"));
    }
}
