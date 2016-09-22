package Application.Controllers;

import Application.Services.PropertyService;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * Created by IntelliJ IDEA.
 * User: Karol Golec
 * Date: 22.09.2016
 * Time: 14:03
 */
public class LoaderController implements Initializable {

    @FXML
    private Label title;

    @FXML
    private Label version;

    /**
     * Called to initialize a controller after its root element has been
     * completely processed.
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        this.title.setText(PropertyService.get("title", "Application/Resources/properties.properties"));
        this.version.setText(PropertyService.get("version", "Application/Resources/properties.properties"));
    }
}
