package Application.Controllers.Application;

import java.net.URL;
import java.sql.*;
import java.util.ResourceBundle;
import java.util.TimeZone;

import Application.Services.AlertService;
import Application.Services.Application.SettingsService;
import Application.Services.PropertyService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ComboBox;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

/**
 * Created by IntelliJ IDEA.
 * User: Karol Golec
 * Date: 22.09.2016
 * Time: 16:17
 */
public class SettingsController implements Initializable {

    @FXML
    private TextField googleApiKey;

    @FXML
    private TextField googleSearchEngineId;

    @FXML
    private TextField queriesOfKeyword;

    @FXML
    private TextField processorLimitThreads;

    @FXML
    private TextField robotLimitSubSiteAnalyzeDomain;

    @FXML
    private TextField mysqlHost;

    @FXML
    private TextField mysqlPort;

    @FXML
    private TextField mysqlDatabase;

    @FXML
    private TextField mysqlUsername;

    @FXML
    private PasswordField mysqlPassword;

    @FXML
    private TextField mysqlTableName;

    @FXML
    private TextField mysqlColumnDomain;

    @FXML
    private TextField mysqlColumnUrl;

    @FXML
    private TextField mysqlColumnQuantity;

    @FXML
    private TextField mysqlColumnDate;

    @FXML
    private TextField mysqlColumnKeyword;

    @FXML
    private ComboBox<String> robotSaveDataTo;

    /** Observable list for robot save option */
    public ObservableList<String> optionsRobotSaveTo;

    /** @var bundle */
    private ResourceBundle bundle;

    /** @var file with properties of application */
    private static final String PROPERTIES_FILE = "Application/Resources/properties.properties";

    /**
     * Constructor
     */
    public SettingsController(){
        this.optionsRobotSaveTo = FXCollections.observableArrayList();
    }

    /**
     * Called to initialize a controller after its root element has been
     * completely processed.
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        this.bundle = resources;

        this.optionsRobotSaveTo.add(resources.getString("tabs.settings.bot.save_option.to_file"));
        this.optionsRobotSaveTo.add(resources.getString("tabs.settings.bot.save_option.to_mysql"));
        this.robotSaveDataTo.setItems(this.optionsRobotSaveTo);

        fillInterface();
    }

    /**
     * Save all settings
     */
    @FXML
    public void save(){
        SettingsService.add("google.api_key", this.googleApiKey.getText());
        SettingsService.add("google.search_engine_id", this.googleSearchEngineId.getText());
        SettingsService.add("google.queries_of_keyword", this.queriesOfKeyword.getText());
        SettingsService.add("processor.limit_threads", this.processorLimitThreads.getText());
        SettingsService.add("robot.limit_sub_site_analyze_domain", this.robotLimitSubSiteAnalyzeDomain.getText());
        SettingsService.add("robot.save_option", this.robotSaveDataTo.getSelectionModel().getSelectedItem().toString());

        SettingsService.add("mysql.host", this.mysqlHost.getText());
        SettingsService.add("mysql.port", this.mysqlPort.getText());
        SettingsService.add("mysql.database", this.mysqlDatabase.getText());
        SettingsService.add("mysql.username", this.mysqlUsername.getText());
        SettingsService.add("mysql.password", this.mysqlPassword.getText());
        SettingsService.add("mysql.table.name", this.mysqlTableName.getText());
        SettingsService.add("mysql.table.column_domain", this.mysqlColumnDomain.getText());
        SettingsService.add("mysql.table.column_url", this.mysqlColumnUrl.getText());
        SettingsService.add("mysql.table.column_quantity", this.mysqlColumnQuantity.getText());
        SettingsService.add("mysql.table.column_date", this.mysqlColumnDate.getText());
        SettingsService.add("mysql.table.column_keyword", this.mysqlColumnKeyword.getText());

        LogsController.success(this.bundle.getString("tabs.settings.setting_saved"));
        AlertService.info(this.bundle.getString("dialog_alert.information"), null, this.bundle.getString("tabs.settings.setting_saved"));
    }

    /**
     * Fill controls in interface
     * (for load application)
     */
    private void fillInterface(){
        String defaultPLT = PropertyService.get("default_processor_limit_threads", PROPERTIES_FILE);
        String defaultGQK = PropertyService.get("default_google_queries_of_keyword", PROPERTIES_FILE);
        String defaultRLS = PropertyService.get("default_robot_limit_sub_site_analyze_domain", PROPERTIES_FILE);

        String getGAK = SettingsService.get("google.api_key");
        String getGSE = SettingsService.get("google.search_engine_id");
        String getGQK = SettingsService.get("google.queries_of_keyword");
        String getPLT = SettingsService.get("processor.limit_threads");
        String getRLS = SettingsService.get("robot.limit_sub_site_analyze_domain");
        String getRSO = SettingsService.get("robot.save_option");

        getPLT = (getPLT == "") ? defaultPLT : getPLT;
        getGQK = (getGQK == "") ? defaultGQK : getGQK;
        getRLS = (getRLS == "") ? defaultRLS : getRLS;
        getRSO = (getRSO == "") ? this.bundle.getString("tabs.settings.bot.save_option.to_file") : getRSO;

        this.googleApiKey.setText(getGAK);
        this.googleSearchEngineId.setText(getGSE);
        this.queriesOfKeyword.setText(getGQK);
        this.processorLimitThreads.setText(getPLT);
        this.robotLimitSubSiteAnalyzeDomain.setText(getRLS);
        this.robotSaveDataTo.getSelectionModel().select(getRSO);

        this.mysqlHost.setText(SettingsService.get("mysql.host"));
        this.mysqlPort.setText(SettingsService.get("mysql.port"));
        this.mysqlDatabase.setText(SettingsService.get("mysql.database"));
        this.mysqlUsername.setText(SettingsService.get("mysql.username"));
        this.mysqlPassword.setText(SettingsService.get("mysql.password"));
        this.mysqlTableName.setText(SettingsService.get("mysql.table.name"));
        this.mysqlColumnDomain.setText(SettingsService.get("mysql.table.column_domain"));
        this.mysqlColumnUrl.setText(SettingsService.get("mysql.table.column_url"));
        this.mysqlColumnQuantity.setText(SettingsService.get("mysql.table.column_quantity"));
        this.mysqlColumnDate.setText(SettingsService.get("mysql.table.column_date"));
        this.mysqlColumnKeyword.setText(SettingsService.get("mysql.table.column_keyword"));

    }

    /**
     * Test connection with MySQL
     */
    @FXML
    public void mysqlTest(){

        String host = this.mysqlHost.getText();
        String port = this.mysqlPort.getText();
        String database = this.mysqlDatabase.getText();
        String username = this.mysqlUsername.getText();
        String password = this.mysqlPassword.getText();
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
            AlertService.error(this.bundle.getString("dialog_alert.error"), null, this.bundle.getString("robot.log.failed_test_connection_mysql"));
        } finally {
            try {
                if (rs != null) {
                    rs.close();
                }
                if (st != null) {
                    st.close();
                }
                if (con != null) {
                    con.close();
                }
            } catch (SQLException ex) {}
        }

//        this.mysqlHost.getText()
//        SettingsService.get("mysql.host");
//        this.mysqlPort.getText()
//        SettingsService.get("mysql.port");
//        this.mysqlDatabase.getText()
//        SettingsService.get("mysql.database");
//        this.mysqlUsername.getText()
//        SettingsService.get("mysql.username");
//        this.mysqlPassword.getText()
//        SettingsService.get("mysql.password");
//        this.mysqlTableName.getText()
//        SettingsService.get("mysql.table.name");
//        this.mysqlColumnDomain.getText()
//        SettingsService.get("mysql.table.column_domain");
//        this.mysqlColumnUrl.getText()
//        SettingsService.get("mysql.table.column_url");
//        this.mysqlColumnQuantity.getText()
//        SettingsService.get("mysql.table.column_quantity");
//        this.mysqlColumnDate.getText()
//        SettingsService.get("mysql.table.column_date");
//        this.mysqlColumnKeyword.getText()
//        SettingsService.get("mysql.table.column_keyword");
    }
}
