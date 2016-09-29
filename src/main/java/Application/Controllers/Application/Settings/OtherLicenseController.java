package Application.Controllers.Application.Settings;

import Application.Services.AlertService;
import Application.Services.LicenseService;
import Modules.Extensions.PhoneEmail.Services.PhoneEmailService;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.layout.VBox;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * Created by IntelliJ IDEA.
 * User: Karol Golec
 * Date: 27.09.2016
 * Time: 22:05
 */
public class OtherLicenseController implements Initializable {

    @FXML
    private VBox root;
    @FXML
    private TextArea licenseKey;
    @FXML
    private Label deleteApplication;
    @FXML
    private Label deleteExtPhoneEmail;
    @FXML
    private Label application;
    @FXML
    private Label extPhoneEmail;

    /* @var license service */
    private LicenseService licenseService;

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
        this.licenseService = new LicenseService(this.bundle);
        fillInterface();
    }

    @FXML
    public void addKeyLicense(){
        licenseService.addLicense(this.licenseKey.getText(), "application");
    }

    /**
     * Save settings
     */
    public void save(){

    }

    /**
     * Delete license
     */
    @FXML
    public void deleteApplication(){
        this.licenseService.delete("application");
        this.deleteApplication.setVisible(false);
        this.application.setText(this.bundle.getString("tabs.settings.others.licenses.your_licenses.no_license"));
        AlertService.info(this.bundle.getString("dialog_alert.information"), null, this.bundle.getString("dialog_alert.key_license.deleted"));
    }

    /**
     * Delete license
     */
    @FXML
    public void deleteExtPhoneEmail(){
        this.licenseService.delete("ext_phone_email");
        this.deleteExtPhoneEmail.setVisible(false);
        this.extPhoneEmail.setText(this.bundle.getString("tabs.settings.others.licenses.your_licenses.no_license"));
        AlertService.info(this.bundle.getString("dialog_alert.information"), null, this.bundle.getString("dialog_alert.key_license.deleted"));
    }

    /**
     * Fill fields in interface
     */
    private void fillInterface(){

        String infoBundle = this.bundle.getString("tabs.settings.others.licenses.your_licenses.info");

        if (this.licenseService.hasLicense("application")){
            String username = this.licenseService.getUsername("application");
            String dateEnd = this.licenseService.getDateEnd("application");

            this.application.setText(String.format(infoBundle, username, dateEnd));

        } else {
            this.deleteApplication.setVisible(false);
            this.application.setText(this.bundle.getString("tabs.settings.others.licenses.your_licenses.no_license"));
        }

        if (this.licenseService.hasLicense("ext_phone_email")){
            String username = this.licenseService.getUsername("ext_phone_email");
            String dateEnd = this.licenseService.getDateEnd("ext_phone_email");

            this.extPhoneEmail.setText(String.format(infoBundle, username, dateEnd));
        } else {
            this.deleteExtPhoneEmail.setVisible(false);
            this.extPhoneEmail.setText(this.bundle.getString("tabs.settings.others.licenses.your_licenses.no_license"));
        }
    }
}
