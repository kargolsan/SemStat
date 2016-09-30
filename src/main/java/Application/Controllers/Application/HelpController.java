package Application.Controllers.Application;

import Application.Services.Application.HelpService;
import Application.Services.PropertyService;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TextArea;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.*;
import java.net.URL;
import java.util.ResourceBundle;

/**
 * Created by IntelliJ IDEA.
 * User: Karol Golec
 * Date: 23.09.2016
 * Time: 22:10
 */
public class HelpController implements Initializable {

    /** @var logger */
    private static final Logger logger = LogManager.getLogger(HelpController.class);

    @FXML
    private WebView browser;

    /* @var browser engine */
    private WebEngine webEngine;

    /* @var help service */
    private HelpService helpService;

    /**
     * Called to initialize a controller after its root element has been
     * completely processed.
     */
    @Override
    public void initialize(URL location, ResourceBundle bundle) {
        System.setProperty("sun.net.http.allowRestrictedHeaders", "false");
        this.webEngine = browser.getEngine();
        this.helpService = new HelpService(this.webEngine);
        home();
    }

    /**
     * Go to home
     */
    @FXML
    public void home(){
        String index = String.format("/Help/%1$s/index.html", "pl");
        String url = getClass().getResource(index).toExternalForm();
        this.webEngine.load(url);
        this.webEngine.reload();
    }

    /**
     * Go back
     */
    @FXML
    public void back(){
        this.helpService.goBack();
    }

    /**
     * Go forward
     */
    @FXML
    public void forward(){
        this.helpService.goForward();
    }
}