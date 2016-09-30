package Application.Controllers.Application;

import java.net.URL;

import Application.Services.Application.AnalyticsService;
import Application.Services.Application.SettingsService;
import Application.Services.PropertyService;
import javafx.fxml.FXML;
import java.util.ResourceBundle;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.Button;
import javafx.application.Platform;
import javafx.scene.control.TextField;
import javafx.beans.property.StringProperty;
import Application.Contracts.Data.ISaveService;
import Application.Contracts.Bots.IBotController;
import javafx.beans.property.SimpleStringProperty;
import Application.Services.Application.BotService;
import Modules.Bots.First.Controllers.FirstBotControllers;
import Modules.SearchEngines.Google.Services.GoogleSearchEngineService;

/**
 * Created by IntelliJ IDEA.
 * User: Karol Golec
 * Date: 23.09.2016
 * Time: 22:10
 */
public class BotController implements Initializable {

    @FXML
    private SourcesController sourcesController;
    @FXML
    private TextField keyword;
    @FXML
    private TextField extensionsDomain;
    @FXML
    private Button analyze;
    @FXML
    private Button stop;
    @FXML
    private Label countFound;
    @FXML
    private Label countUnique;
    @FXML
    private Label countAnalyzed;

    /* @var bot service application */
    private BotService botService;

    /* @var property count found */
    private static StringProperty countFoundProperty = new SimpleStringProperty();

    /* @var property count unique */
    private static StringProperty countUniqueProperty = new SimpleStringProperty();

    /* @var property count analyzed */
    private static StringProperty countAnalyzedProperty = new SimpleStringProperty();

    /* @var bundle resource */
    private ResourceBundle bundle;

    /* @var file with properties of application */
    private static final String PROPERTIES_FILE = "Application/Resources/properties.properties";

    /* @var bot */
    IBotController bot;

    public void setSourcesController(SourcesController sourcesController){
        this.sourcesController = sourcesController;
    }

    /**
     * Called to initialize a controller after its root element has been
     * completely processed.
     */
    @Override
    public void initialize(URL location, ResourceBundle bundle) {
        this.bundle = bundle;
        this.botService = new BotService(bundle);

        this.countFound.textProperty().bind(countFoundProperty);
        this.countUnique.textProperty().bind(countUniqueProperty);
        this.countAnalyzed.textProperty().bind(countAnalyzedProperty);

        fillInterface();

        resetDisplayResults();

        BottomStripController.setStatus(this.bundle.getString("robot.status.ready_to_job"));
    }

    /**
     * Run analyze
     * (from button interface)
     */
    @FXML
    public void analyze(){

        AnalyticsService analyticsService = new AnalyticsService();
        analyticsService.send("Bot launched");

        String keyword = this.keyword.getText();

        this.bot = buildBot();

        resetDisplayResults();

        if (keyword =="") LogsController.warning(this.bundle.getString("robot.log.empty_keyword_field_in_interface"));

        LogsController.info(String.format(this.bundle.getString("robot.log.start_analyze_keyword"), keyword));

        this.analyze.setDisable(true);
        this.stop.setDisable(false);

        this.bot.start(keyword, this.extensionsDomain.getText(), this.sourcesController.getText().getText());
    }

    /**
     * Stop analyze
     */
    @FXML
    public void stop(){
        this.stop.setText(this.bundle.getString("tabs.bot.wait"));
        this.stop.setDisable(true);
        this.bot.interrupt();
    }

    /**
     * Set found websites
     *
     * @param value
     */
    public static void setCountFound(String value) {
        Platform.runLater(()->{
            BotController.countFoundProperty.set(value);
        });
    }

    /**
     * Set count unique domains
     *
     * @param value
     */
    public static void setCountUnique(String value) {
        Platform.runLater(()->{
            BotController.countUniqueProperty.set(value);
        });
    }

    /**
     * Set count analyzed websites
     *
     * @param value
     */
    public static void setCountAnalyzed(String value) {
        Platform.runLater(()->{
            BotController.countAnalyzedProperty.set(value);
        });
    }

    /**
     * Build bot
     *
     * @return bot controller
     */
    private IBotController buildBot(){
        ISaveService saveService = this.botService.getProviderDataService(this.bundle);
        return new FirstBotControllers(saveService, ()->{
            Platform.runLater(()->{
                this.stop.setText(this.bundle.getString("tabs.bot.break"));
            });
            this.analyze.setDisable(false);
            this.stop.setDisable(true);
        }, this.bundle);
    }

    /**
     * Reset display results of bot
     */
    private void resetDisplayResults(){
        setCountAnalyzed("0");
        setCountFound("0");
        setCountUnique("0");
    }

    /**
     * Fill fields in interface
     */
    private void fillInterface(){
        String defaultBED = PropertyService.get("default_bot_extensions_domain", PROPERTIES_FILE);

        String getBED = SettingsService.get("bot.general.extensions.domain");

        getBED = (getBED == "") ? defaultBED : getBED;

        this.extensionsDomain.setText(getBED);
    }
}