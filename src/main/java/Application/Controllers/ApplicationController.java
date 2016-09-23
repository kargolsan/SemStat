package Application.Controllers;

import Application.Controllers.Application.BottomStripController;
import Application.Controllers.Application.LogsController;
import Application.Services.AsyncService;
import Application.Stages.Loader;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;

import java.net.URL;
import java.util.ResourceBundle;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * Created by IntelliJ IDEA.
 * User: Karol Golec
 * Date: 22.09.2016
 * Time: 14:31
 */
public class ApplicationController implements Initializable {

    /**
     * Called to initialize a controller after its root element has been
     * completely processed.
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        Loader.getStage().hide();
    }
}
