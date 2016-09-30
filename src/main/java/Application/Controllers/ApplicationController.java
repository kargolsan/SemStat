package Application.Controllers;

import java.awt.event.ActionEvent;
import java.net.URL;
import java.util.ResourceBundle;

import Application.Controllers.Application.BotController;
import Application.Controllers.Application.Settings.DefaultController;
import Application.Controllers.Application.SourcesController;
import Application.Services.Application.AnalyticsService;
import Application.Services.ApplicationService;
import Application.Services.LicenseService;
import Application.Stages.Loader;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

/**
 * Created by IntelliJ IDEA.
 * User: Karol Golec
 * Date: 22.09.2016
 * Time: 14:31
 */
public class ApplicationController implements Initializable {

    @FXML
    private VBox root;

    @FXML
    private SourcesController sourcesController;

    @FXML
    private BotController botController;

    /* @var bundle resource */
    private ResourceBundle bundle;

    /**
     * Called to initialize a controller after its root element has been
     * completely processed.
     */
    @Override
    public void initialize(URL location, ResourceBundle bundle) {
        Loader.getStage().hide();
        this.bundle = bundle;
        this.botController.setSourcesController(this.sourcesController);

        ApplicationService applicationService = new ApplicationService(bundle);
        applicationService.checkNewVersion();

        AnalyticsService analyticsService = new AnalyticsService();
        analyticsService.send("Application launched");
    }
}
