package Application.Stages;

import Application.Services.LanguageService;
import Application.Services.PropertyService;
import javafx.application.Preloader;
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
public class Loader extends Preloader {

    /** @var logger */
    private static final Logger logger = LogManager.getLogger(Loader.class);

    /** @var this stage */
    private static Stage stage;

    /** Path to view of main stage */
    private static final String VIEW = "/Application/Resources/Views/LoaderView.fxml";

    /** Path to language of main stage */
    private static final String LANGUAGE = "Application/Resources/Languages/loader";

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

        try {
            VBox view = FXMLLoader.load(getClass().getResource(VIEW), BUNDLE);
            Scene scene = new Scene(view);
            primaryStage.initStyle(StageStyle.UNDECORATED);
            primaryStage.setScene(scene);
            primaryStage.getIcons().add(new Image(getClass().getResourceAsStream(ICON)));
            primaryStage.show();
        } catch (Exception ex){
            logger.fatal("Problem podczas wczytywanie stage loader.", ex);
        }
    }
}
