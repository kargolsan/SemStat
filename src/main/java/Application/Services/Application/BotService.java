package Application.Services.Application;

import Application.Contracts.Data.IDataService;
import Modules.Data.File.Services.FileDataService;
import Modules.Data.MySQL.Services.MySQLDataService;

import java.util.ResourceBundle;

/**
 * Created by IntelliJ IDEA.
 * User: Karol Golec
 * Date: 27.09.2016
 * Time: 10:33
 */
public class BotService {

    /** @var bundle resource */
    private ResourceBundle bundle;

    /**
     * Constructor
     *
     * @param bundle
     */
    public BotService(ResourceBundle bundle){
        this.bundle = bundle;
    }

    /**
     * Choose save data provider
     *
     * @param bundle
     * @return
     */
    public IDataService getProviderDataService(ResourceBundle bundle){
        String currentOpt = SettingsService.get("robot.save_option");

        String optToFile = this.bundle.getString("tabs.settings.bot.save_option.to_file");
        String optToMySQL = this.bundle.getString("tabs.settings.bot.save_option.to_mysql");

        if (currentOpt.equals(optToFile)) {
            return new FileDataService(bundle);
        } else if (currentOpt.equals(optToMySQL)){
            return new MySQLDataService(bundle);
        }

        return new FileDataService(bundle);
    }
}
