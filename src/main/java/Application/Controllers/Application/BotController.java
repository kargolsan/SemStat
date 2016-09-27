package Application.Controllers.Application;

import java.net.URL;

import Application.Services.Application.BotService;
import Modules.Bots.First.Controllers.FirstBotControllers;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.fxml.FXML;
import java.util.ResourceBundle;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import Application.Contracts.Bots.IBotController;
import Application.Contracts.Data.IDataService;
import Modules.Data.File.Services.FileDataService;
import Application.Contracts.SearchEngines.ISearchEngine;
import Modules.SearchEngines.Google.Services.GoogleSearchEngineService;

/**
 * Created by IntelliJ IDEA.
 * User: Karol Golec
 * Date: 23.09.2016
 * Time: 22:10
 */
public class BotController implements Initializable {

    @FXML
    private TextField keyword;

    @FXML
    private Button analyze;

    @FXML
    private Button stop;

    @FXML
    private Label quantityWebsitesWithKeyword;

    @FXML
    private Label savedUniqueDomains;

    @FXML
    private Label analyzedWebsites;

    /** @var bot service application */
    private BotService botService;

//    @FXML
//    private Label urlsToAnalyze;

    /** @var property information */
    private static StringProperty quantityWebsitesWithKeywordProperty = new SimpleStringProperty();

    /** @var property information */
    private static StringProperty savedUniqueDomainsProperty = new SimpleStringProperty();

    /** @var property information */
    private static StringProperty analyzedWebsitesProperty = new SimpleStringProperty();

    /** @var property information */
//    private static StringProperty urlsToAnalyzeProperty = new SimpleStringProperty();

    /** @var bundle resource */
    private ResourceBundle bundle;

    /** @var robot */
    IBotController bot;

    /** @var pre search */
    ISearchEngine searchEngine;

    /** @var data service for ready and save analyzed data */
    IDataService data;

    /**
     * Called to initialize a controller after its root element has been
     * completely processed.
     */
    @Override
    public void initialize(URL location, ResourceBundle bundle) {
        this.bundle = bundle;

        this.botService = new BotService(bundle);

        this.quantityWebsitesWithKeyword.textProperty().bind(quantityWebsitesWithKeywordProperty);
        this.savedUniqueDomains.textProperty().bind(savedUniqueDomainsProperty);
        this.analyzedWebsites.textProperty().bind(analyzedWebsitesProperty);
        //this.urlsToAnalyze.textProperty().bind(urlsToAnalyzeProperty);

        this.searchEngine = new GoogleSearchEngineService();

        BottomStripController.setStatus(this.bundle.getString("robot.status.ready_to_job"));
    }

    /**
     * Run analyze
     * (from button interface)
     */
    @FXML
    public void analyze(){

        this.data = this.botService.getProviderDataService();
        this.bot = new FirstBotControllers(this.searchEngine, this.data, ()->{
            analyze.setDisable(false);
            stop.setDisable(true);
        }, bundle);

        setAnalyzedWebsitesProperty("0");
        setQuantityWebsitesWithKeywordProperty("0");
        setSavedUniqueDomainsProperty("0");
        //setUrlsToAnalyzeProperty("0");

        String keyword = this.keyword.getText();

        this.analyze.setDisable(true);
        this.stop.setDisable(false);

        if (keyword =="") LogsController.warning(this.bundle.getString("robot.log.empty_keyword_field_in_interface"));
        LogsController.info(String.format(this.bundle.getString("robot.log.start_analyze_keyword"), keyword));

        this.bot.start(keyword);
    }

    /**
     * Stop analyze
     */
    @FXML
    public void stop(){
        this.bot.interrupt();
    }

    /**
     * Set information in interface
     *
     * @param quantityWebsitesWithKeywordProperty
     */
    public static void setQuantityWebsitesWithKeywordProperty(String quantityWebsitesWithKeywordProperty) {
        Platform.runLater(()->{
            BotController.quantityWebsitesWithKeywordProperty.set(quantityWebsitesWithKeywordProperty);
        });
    }

    /**
     * Set information in interface
     *
     * @param savedUniqueDomainsProperty
     */
    public static void setSavedUniqueDomainsProperty(String savedUniqueDomainsProperty) {
        Platform.runLater(()->{
            BotController.savedUniqueDomainsProperty.set(savedUniqueDomainsProperty);
        });
    }

    /**
     * Set information in interface
     *
     * @param analyzedWebsitesProperty
     */
    public static void setAnalyzedWebsitesProperty(String analyzedWebsitesProperty) {
        Platform.runLater(()->{
            BotController.analyzedWebsitesProperty.set(analyzedWebsitesProperty);
        });
    }

//    public static void setUrlsToAnalyzeProperty(String urlsToAnalyzeProperty) {
//        Platform.runLater(()->{
//            BotController.urlsToAnalyzeProperty.set(urlsToAnalyzeProperty);
//        });
//    }
}