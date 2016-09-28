package Application.Controllers;

import java.net.URL;
import java.util.ResourceBundle;

import Application.Controllers.Application.BotController;
import Application.Controllers.Application.Settings.DefaultController;
import Application.Controllers.Application.SourcesController;
import Application.Stages.Loader;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;

/**
 * Created by IntelliJ IDEA.
 * User: Karol Golec
 * Date: 22.09.2016
 * Time: 14:31
 */
public class ApplicationController implements Initializable {

    @FXML
    private SourcesController sourcesController;

    @FXML
    private BotController botController;

    /**
     * Called to initialize a controller after its root element has been
     * completely processed.
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        Loader.getStage().hide();
        this.botController.setSourcesController(this.sourcesController);
    }
}
