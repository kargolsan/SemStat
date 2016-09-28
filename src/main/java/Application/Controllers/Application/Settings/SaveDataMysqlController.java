package Application.Controllers.Application.Settings;

import Application.Controllers.Application.LogsController;
import Application.Services.AlertService;
import Application.Services.Application.SettingsService;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;

import java.net.URL;
import java.sql.*;
import java.util.ResourceBundle;
import java.util.TimeZone;

/**
 * Created by IntelliJ IDEA.
 * User: Karol Golec
 * Date: 27.09.2016
 * Time: 22:05
 */
public class SaveDataMysqlController implements Initializable {

    @FXML
    private VBox root;

    @FXML
    private TextField host;
    @FXML
    private TextField port;
    @FXML
    private TextField database;
    @FXML
    private TextField username;
    @FXML
    private PasswordField password;
    @FXML
    private TextField tableName;
    @FXML
    private TextField columnDomain;
    @FXML
    private TextField columnUrl;
    @FXML
    private TextField columnQuantity;
    @FXML
    private TextField columnDate;
    @FXML
    private TextField columnKeyword;

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
        SettingsService.add("mysql.host", this.host.getText());
        SettingsService.add("mysql.port", this.port.getText());
        SettingsService.add("mysql.database", this.database.getText());
        SettingsService.add("mysql.username", this.username.getText());
        SettingsService.add("mysql.password", this.password.getText());
        SettingsService.add("mysql.table.name", this.tableName.getText());
        SettingsService.add("mysql.table.column_domain", this.columnDomain.getText());
        SettingsService.add("mysql.table.column_url", this.columnUrl.getText());
        SettingsService.add("mysql.table.column_quantity", this.columnQuantity.getText());
        SettingsService.add("mysql.table.column_date", this.columnDate.getText());
        SettingsService.add("mysql.table.column_keyword", this.columnKeyword.getText());

    }

    /**
     * Fill fields in interface
     */
    private void fillInterface(){
        this.host.setText(SettingsService.get("mysql.host"));
        this.port.setText(SettingsService.get("mysql.port"));
        this.database.setText(SettingsService.get("mysql.database"));
        this.username.setText(SettingsService.get("mysql.username"));
        this.password.setText(SettingsService.get("mysql.password"));
        this.tableName.setText(SettingsService.get("mysql.table.name"));
        this.columnDomain.setText(SettingsService.get("mysql.table.column_domain"));
        this.columnUrl.setText(SettingsService.get("mysql.table.column_url"));
        this.columnQuantity.setText(SettingsService.get("mysql.table.column_quantity"));
        this.columnDate.setText(SettingsService.get("mysql.table.column_date"));
        this.columnKeyword.setText(SettingsService.get("mysql.table.column_keyword"));
    }

    /**
     * Test connection with MySQL
     */
    @FXML
    public void test() {

        String host = this.host.getText();
        String port = this.port.getText();
        String database = this.database.getText();
        String username = this.username.getText();
        String password = this.password.getText();
        String timezone = TimeZone.getDefault().getID();

        if (host.contains("http://")) host = host.replace("http://", "");
        if (host.contains("https://")) host = host.replace("https://", "");

        Connection con = null;
        Statement st = null;
        ResultSet rs = null;
        String connectionString = String.format("jdbc:mysql://%1$s:%2$s/%3$s?useLegacyDatetimeCode=false&serverTimezone=%4$s", host, port, database, timezone);

        try {
            con = DriverManager.getConnection(connectionString, username, password);
            st = con.createStatement();
            rs = st.executeQuery("select * from information_schema.tables");
            LogsController.success(this.bundle.getString("robot.log.success_test_connection_mysql"));
            AlertService.info(this.bundle.getString("dialog_alert.information"), null, this.bundle.getString("robot.log.success_test_connection_mysql"));
        } catch (SQLException ex) {
            LogsController.error(String.format(this.bundle.getString("robot.log.failed_test_connection_mysql"), ex.getMessage()));
            AlertService.error(this.bundle.getString("dialog_alert.error"), null, String.format(this.bundle.getString("robot.log.failed_test_connection_mysql"), ex.getMessage()));
        } finally {
            try {
                if (rs != null) rs.close();
                if (st != null) st.close();
                if (con != null) con.close();
            } catch (SQLException ex) {}
        }
    }
}
