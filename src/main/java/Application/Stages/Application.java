package Application.Stages;

import Application.Services.AsyncService;
import Application.Services.LanguageService;
import Application.Services.LicenseService;
import Application.Services.PropertyService;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ResourceBundle;

/**
 * Created by IntelliJ IDEA.
 * User: Karol Golec
 * Date: 22.09.2016
 * Time: 11:52
 */
public class Application extends javafx.application.Application {

    /** @var logger */
    private static final Logger logger = LogManager.getLogger(Loader.class);

    /** @var this stage */
    private static Stage stage;

    /** Path to view of main stage */
    private static final String VIEW = "/Application/Resources/Views/ApplicationView.fxml";

    /** Path to language of main stage */
    private static final String LANGUAGE = "Application/Resources/Languages/application";

    /** Icon of stage */
    private static final String ICON = PropertyService.get("iconApp", "Application/Resources/properties.properties");

    /* @var resource bundle */
    private static final ResourceBundle BUNDLE = LanguageService.getBundle(LANGUAGE);

    /**
     * Get this static stage
     *
     * @return stage
     */
    public static Stage getStage() {
        return stage;
    }

    /**
     * The main entry point for all JavaFX applications.
     * The start method is called after the init method has returned,
     * and after the system is ready for the application to begin running.
     */
    @Override
    public void start(Stage primaryStage) throws Exception {
        this.stage = primaryStage;

        String title = PropertyService.get("title", "Application/Resources/properties.properties");
        String version = PropertyService.get("version", "Application/Resources/properties.properties");

        try {
            VBox view = FXMLLoader.load(getClass().getResource(VIEW), BUNDLE);
            Scene scene = new Scene(view);
            primaryStage.setScene(scene);
            primaryStage.getIcons().add(new Image(getClass().getResourceAsStream(ICON)));
            primaryStage.setTitle(String.format("%1$s - v.%2$s", title, version));

            LicenseService licenseService = new LicenseService(null);
            if (!licenseService.hasLicense("application")){
                primaryStage.setTitle(primaryStage.getTitle() + " - " + this.BUNDLE.getString("application.non_commercial"));
            }

            primaryStage.show();
        } catch (Exception ex){
            logger.fatal("Problem podczas wczytywanie stage application.", ex);
        }
    }
}
