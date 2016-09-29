package Application.Controllers.Application.Extensions;

import Application.Contracts.Bots.IBotController;
import Application.Contracts.Data.ISaveService;
import Application.Controllers.Application.BottomStripController;
import Application.Controllers.Application.LogsController;
import Application.Controllers.Application.SourcesController;
import Application.Services.Application.BotService;
import Application.Services.Application.SettingsService;
import Application.Services.PropertyService;
import Modules.Bots.First.Controllers.FirstBotControllers;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

import java.net.URL;
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

    /* @var bundle resource */
    private ResourceBundle bundle;

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
}