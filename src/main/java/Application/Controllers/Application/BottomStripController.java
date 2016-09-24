package Application.Controllers.Application;

import Application.Stages.Loader;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * Created by IntelliJ IDEA.
 * User: Karol Golec
 * Date: 22.09.2016
 * Time: 15:35
 */
public class BottomStripController implements Initializable {

    @FXML
    private Label status;

    /** @var status */
    private static StringProperty statusProperty = new SimpleStringProperty();

    /**
     * Set status
     *
     * @param status
     */
    public static void setStatus(String status){
        Platform.runLater(()->{
            statusProperty.setValue(status);
        });
    }

    /**
     * Called to initialize a controller after its root element has been
     * completely processed.
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        this.status.textProperty().bind(statusProperty);
    }
}