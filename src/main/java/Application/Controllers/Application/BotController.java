package Application.Controllers.Application;

import Application.Contracts.Bots.IBotService;
import Application.Contracts.PreSearches.IPreSearchesService;
import Modules.Bots.First.Services.BotService;
import Modules.Data.File.Services.DataService;
import Modules.PreSearchers.Google.Services.PreSearchService;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;

import java.net.URL;
import java.util.ResourceBundle;

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

    /** @var robot */
    IBotService bot;

    /** @var pre search */
    IPreSearchesService preSearch;

    /**
     * Called to initialize a controller after its root element has been
     * completely processed.
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        this.preSearch = new PreSearchService(); //new BrowserService().getDriver()
        BottomStripController.setStatus("Robot gotowy do pracy");
    }

    /**
     * Run analyze
     */
    @FXML
    public void analyze(){
        this.analyze.setDisable(true);
        this.stop.setDisable(false);

        String k = this.keyword.getText();
        if (k =="") {
            LogsController.warning("Nie wprowadzono słowa kluczowego do analizy.");
        }
        LogsController.info("Rozpoczęto analizę dla słowa kluczowego: " + k);
        this.bot = new BotService(this.preSearch, new DataService());
        this.bot.run(k, ()->{
            analyze.setDisable(false);
            stop.setDisable(true);

        }, ()->{
            analyze.setDisable(false);
            stop.setDisable(true);
        });
    }

    /**
     * Stop analyze
     */
    @FXML
    public void stop(){
        this.bot.callStop();
    }
}
