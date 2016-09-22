package Application.Controllers;

import Application.Stages.Application;
import Application.Stages.Loader;
import com.sun.javafx.application.LauncherImpl;

/**
 * Created by IntelliJ IDEA.
 * User: Karol Golec
 * Date: 22.09.2016
 * Time: 11:50
 */
public class RunController {
    /**
     * Launch application stage with loader
     *
     * @param args arguments when the program starts
     */
    public static void runApplication(String[] args){
        LauncherImpl.launchApplication(Application.class, Loader.class, args);
    }
}
