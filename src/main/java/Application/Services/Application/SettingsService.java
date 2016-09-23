package Application.Services.Application;

import java.util.prefs.Preferences;
import Application.Services.PropertyService;

/**
 * Created by IntelliJ IDEA.
 * User: Karol Golec
 * Date: 23.09.2016
 * Time: 09:48
 */
public class SettingsService {

    /** @var project name */
    private static final String PROJECT_NAME = PropertyService.get("projectName", "Application/Resources/properties.properties");

    /**
     * Add new user setting
     *
     * @param key
     * @param value
     */
    public static void add(String key, String value){
        Preferences prefs = Preferences.userNodeForPackage(SettingsService.class);
        prefs.put(PROJECT_NAME + key, value);
    }

    /**
     * Get user setting
     *
     * @param key
     * @return
     */
    public static String get(String key){
        Preferences prefs = Preferences.userNodeForPackage(SettingsService.class);
        return prefs.get(PROJECT_NAME + key, "");
    }

    /**
     * Remove user setting
     *
     * @param key
     */
    public static void remove(String key){
        Preferences prefs = Preferences.userNodeForPackage(SettingsService.class);
        prefs.remove(PROJECT_NAME + key);
    }
}
