package Application.Services;

import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.image.Image;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.stage.Stage;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ResourceBundle;

/**
 * Created by IntelliJ IDEA.
 * User: Karol Golec
 * Date: 27.09.2016
 * Time: 12:44
 */
public class AlertService {

    /**
     * Icon for alert error
     */
    private static final String ICON_ERROR = "/Application/Resources/Assets/Images/Alerts/error_20.png";

    /**
     * Icon for alert warning
     */
    private static final String ICON_WARNING = "/Application/Resources/Assets/Images/Alerts/warning_20.png";

    /**
     * Icon for alert info
     */
    private static final String ICON_INFO = "/Application/Resources/Assets/Images/Alerts/info_20.png";

    /**
     * Show warning alert
     *
     * @param title   of alert
     * @param header  for content
     * @param content body of alert
     */
    public static void error(String title, String header, String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);
        Stage stage = (Stage) alert.getDialogPane().getScene().getWindow();
        stage.getIcons().add(new Image(AlertService.class.getResourceAsStream(ICON_ERROR)));
        alert.showAndWait();
    }

    /**
     * Show warning alert
     *
     * @param title   of alert
     * @param header  for content
     * @param content body of alert
     */
    public static void warning(String title, String header, String content) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);
        Stage stage = (Stage) alert.getDialogPane().getScene().getWindow();
        stage.getIcons().add(new Image(AlertService.class.getResourceAsStream(ICON_WARNING)));
        alert.showAndWait();
    }

    /**
     * Show information alert
     *
     * @param title   of alert
     * @param header  for content
     * @param content body of alert
     */
    public static void info(String title, String header, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);
        Stage stage = (Stage) alert.getDialogPane().getScene().getWindow();
        stage.getIcons().add(new Image(AlertService.class.getResourceAsStream(ICON_INFO)));
        alert.showAndWait();
    }
}
