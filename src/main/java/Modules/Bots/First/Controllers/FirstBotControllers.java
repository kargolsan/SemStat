package Modules.Bots.First.Controllers;

import Application.Contracts.Bots.IBotController;
import Application.Contracts.Data.ISaveService;
import Application.Contracts.SearchEngines.ISearchEngine;
import Application.Controllers.Application.BottomStripController;
import Application.Services.AsyncService;
import Modules.Bots.First.Services.BotService;

import java.util.ResourceBundle;

/**
 * Created by IntelliJ IDEA.
 * User: Karol Golec
 * Date: 26.09.2016
 * Time: 11:16
 */
public class FirstBotControllers implements IBotController {

    /** @var bot service */
    private BotService bot;

    /** @var resource bundle */
    private ResourceBundle bundle;

    /**
     * Constructor
     *
     * @param data
     * @param finallyThreads
     * @param bundle
     */
    public FirstBotControllers(ISaveService data, Runnable finallyThreads, ResourceBundle bundle){
        this.bundle = bundle;
        this.bot = new BotService(data, finallyThreads, bundle);
    }

    /**
     * Rub robot
     *
     * @param keyword
     * @param filtrExtensionsDomain
     */
    @Override
    public void start(String keyword, String filtrExtensionsDomain) {
        BottomStripController.setStatus(String.format(this.bundle.getString("robot.status.bot_running")));

        new AsyncService().single(() -> {
            this.bot.start(keyword, filtrExtensionsDomain);
        });
    }

    /**
     * Stop robot
     */
    @Override
    public void interrupt() {
        this.bot.interrupt();
    }

}
