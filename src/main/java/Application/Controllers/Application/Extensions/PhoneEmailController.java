package Application.Controllers.Application.Extensions;

import Application.Contracts.Bots.IBotController;
import Application.Contracts.Data.ISaveService;
import Application.Controllers.Application.BottomStripController;
import Application.Controllers.Application.LogsController;
import Application.Controllers.Application.SourcesController;
import Application.Models.Application.Log;
import Application.Services.Application.BotService;
import Application.Services.Application.LogService;
import Application.Services.Application.SettingsService;
import Application.Services.LicenseService;
import Application.Services.PropertyService;
import Modules.Bots.First.Controllers.FirstBotControllers;
import Modules.Extensions.PhoneEmail.Models.PhoneEmail;
import Modules.Extensions.PhoneEmail.Services.PhoneEmailService;
import Modules.Extensions.PhoneEmail.Services.TableFactoryService;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;

import java.net.URL;
import java.util.Calendar;
import java.util.Objects;
import java.util.ResourceBundle;

/**
 * Created by IntelliJ IDEA.
 * User: Karol Golec
 * Date: 23.09.2016
 * Time: 22:10
 */
public class PhoneEmailController implements Initializable {

    @FXML
    private TextField mysqlColumnPhone;
    @FXML
    private TextField mysqlColumnEmail;
    @FXML
    private CheckBox active;

    @FXML
    private VBox extension;
    @FXML
    private AnchorPane license;
    @FXML
    private TextArea licenseKey;

    @FXML
    private TableView<PhoneEmail> resultsTable;

    @FXML
    private TableColumn<PhoneEmail, String> domainColumn;

    @FXML
    private TableColumn<PhoneEmail, String> keywordColumn;

    @FXML
    private TableColumn<PhoneEmail, String> phonesColumn;

    @FXML
    private TableColumn<PhoneEmail, String> emailsColumn;

    /* @var Observable list with repairs for table in view*/
    private static ObservableList<PhoneEmail> results;

    /* @var bundle resource */
    private ResourceBundle bundle;

    /**
     * Constructor
     */
    public PhoneEmailController(){
        this.results = FXCollections.observableArrayList();
    }

    /**
     * Called to initialize a controller after its root element has been
     * completely processed.
     */
    @Override
    public void initialize(URL location, ResourceBundle bundle) {
        this.bundle = bundle;

        TableFactoryService tableFactoryService = new TableFactoryService();

        this.domainColumn.setCellValueFactory(new PropertyValueFactory("domain"));
        this.keywordColumn.setCellValueFactory(new PropertyValueFactory("keyword"));
        this.phonesColumn.setCellValueFactory(tableFactoryService.getPhonesFactory());
        this.emailsColumn.setCellValueFactory(tableFactoryService.getEmailsFactory());

        this.resultsTable.setItems(this.results);

        checkLicense();

        fillInterface();
    }

    /**
     * Add info log to list
     *
     * @param result
     */
    public static void addResult(PhoneEmail result){
        Platform.runLater(()->{
           PhoneEmailController.results.add(0, result);
        });
    }

    /**
     * Add license key
     */
    @FXML
    public void addKeyLicense(){
        LicenseService licenseService = new LicenseService(this.bundle);
        licenseService.addLicense(this.licenseKey.getText(), "ext_phone_email");
        checkLicense();
    }

    /**
     * Save settings
     */
    @FXML
    public void save(){
        SettingsService.add("extension.phone_email.active", Objects.toString(this.active.isSelected()));
        SettingsService.add("extension.phone_email.mysqlColumnPhone", this.mysqlColumnPhone.getText());
        SettingsService.add("extension.phone_email.mysqlColumnEmail", this.mysqlColumnEmail.getText());
    }

    /**
     * Fill fields in interface
     */
    private void fillInterface(){
        String getEPA = SettingsService.get("extension.phone_email.active");
        String getECP = SettingsService.get("extension.phone_email.mysqlColumnPhone");
        String getECE = SettingsService.get("extension.phone_email.mysqlColumnEmail");

        getEPA = (getEPA == "") ? "false" : getEPA;

        this.active.setSelected(Boolean.parseBoolean(getEPA));
        this.mysqlColumnPhone.setText(getECP);
        this.mysqlColumnEmail.setText(getECE);
    }

    /**
     * Check license of extension
     */
    private void checkLicense(){
        PhoneEmailService phoneEmailService = new PhoneEmailService(null, null);

        if (phoneEmailService.hasLicense()){
            this.license.setVisible(false);
            this.license.setManaged(false);
            this.extension.setDisable(false);
        } else {
            this.license.setVisible(true);
            this.license.setManaged(true);
            this.extension.setDisable(true);
        }

    }
}