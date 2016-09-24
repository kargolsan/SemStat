package Application.Controllers.Application;

import java.net.URL;
import java.util.ResourceBundle;
import Application.Services.Application.SettingsService;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TextField;

/**
 * Created by IntelliJ IDEA.
 * User: Karol Golec
 * Date: 22.09.2016
 * Time: 16:17
 */
public class SettingsController implements Initializable {

    @FXML
    private TextField googleApiKey;

    @FXML
    private TextField googleSearchEngineId;

    @FXML
    private TextField processorLimitThreads;

    @FXML
    private TextField botLimitPerUrls;

    /** @var bundle */
    private ResourceBundle bundle;

    /**
     * Called to initialize a controller after its root element has been
     * completely processed.
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        this.bundle = resources;
        this.googleApiKey.setText(SettingsService.get("google.api_key"));
        this.googleSearchEngineId.setText(SettingsService.get("google.search_engine_id"));
        String plt = SettingsService.get("processor.limit_threads");
        String blpu = SettingsService.get("bot.limit_pre_urls");
        plt = (plt == "") ? "10" : plt;
        blpu = (blpu == "") ? "999" : blpu;
        this.processorLimitThreads.setText(plt);
        this.botLimitPerUrls.setText(blpu);
    }

    /**
     * Save all settings
     */
    @FXML
    public void save(){
        SettingsService.add("google.api_key", this.googleApiKey.getText());
        SettingsService.add("google.search_engine_id", this.googleSearchEngineId.getText());
        SettingsService.add("processor.limit_threads", this.processorLimitThreads.getText());
        SettingsService.add("bot.limit_pre_urls", this.botLimitPerUrls.getText());
        LogsController.success(this.bundle.getString("tabs.settings.setting_saved"));
    }
}
