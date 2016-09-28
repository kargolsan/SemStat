package Modules.Bots.First.Services;

import java.util.*;
import java.net.URI;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import java.util.concurrent.Executors;
import java.util.concurrent.ExecutorService;
import Application.Contracts.Data.ISaveService;
import Application.Contracts.SearchEngines.ISearchEngine;
import Application.Controllers.Application.LogsController;
import Application.Controllers.Application.BottomStripController;
import Modules.SearchEngines.Google.Services.GoogleSearchEngineService;

/**
 * Created by IntelliJ IDEA.
 * User: Karol Golec
 * Date: 23.09.2016
 * Time: 17:11
 */
public class BotService {

    /* @var interrupt analyzer */
    private Boolean interrupt = false;

    /* @var data save service */
    private ISaveService saveService;

    /* @var runnable for finally threads */
    private Runnable finishRunnable;

    /* @var filtr extensions domain service */
    private FiltrExtensionsDomainService filtrExtensionsDomainService;

    /* @var processor service */
    private ProcessorService processorService;

    /* @var parse service */
    private ParseService parseService;

    /* @var analyze search engine service */
    private AnalyzePreLinksSearchEngineService analyzePreLinksSearchEngineService;

    /* @var analyze long service */
    private AnalyzeBotLong analyzeBotLong;

    /* @var resource bundle */
    private ResourceBundle bundle;

    /**
     * Constructor
     *
     * @param saveService
     * @param finishRunnable
     * @param bundle
     */
    public BotService(ISaveService saveService, Runnable finishRunnable, ResourceBundle bundle) {
        this.saveService = saveService;
        this.finishRunnable = finishRunnable;
        this.bundle = bundle;

        this.filtrExtensionsDomainService = new FiltrExtensionsDomainService();
        this.parseService = new ParseService(this.filtrExtensionsDomainService, this);
        this.processorService = new ProcessorService();
        this.analyzePreLinksSearchEngineService = new AnalyzePreLinksSearchEngineService(this, this.bundle);
        this.analyzeBotLong = new AnalyzeBotLong(this, this.bundle);
    }

    /**
     * Ger parse service
     *
     * @return parse service
     */
    public ParseService getParseService() {
        return parseService;
    }

    /**
     * Rub bot from controller
     *
     * @param keyword
     * @param filtrExtensionsDomain
     */
    public void start(String keyword, String filtrExtensionsDomain) {
        this.filtrExtensionsDomainService.setFiltrExtensionsDomain(filtrExtensionsDomain);
        clearBot();
        runAutomationBot(getPreLinksApiSearchEngine(keyword), keyword);
    }

    /**
     * Run automation bot
     *
     * @param preLinksApiSearchEngine
     * @param keyword
     */
    private void runAutomationBot(List<Application.Contracts.SearchEngines.IResultModel> preLinksApiSearchEngine, String keyword) {

        ExecutorService executor = Executors.newFixedThreadPool(processorService.getLimitThreads());

        this.analyzePreLinksSearchEngineService.analyze(executor, preLinksApiSearchEngine, keyword);

        executor.shutdown();

        while (!executor.isTerminated()) {
        }

        ExecutorService executorLong = Executors.newFixedThreadPool(processorService.getLimitThreads());

        this.analyzeBotLong.start(executorLong, keyword);

        executorLong.shutdown();

        while (!executorLong.isTerminated()) {
        }

        finish(keyword);
    }

    /**
     * Interrupt robot
     */
    public void interrupt() {
        this.interrupt = true;
    }

    /**
     * Check bot is interrupted
     *
     * @return true if interrupted or false if don't interrupted
     */
    public Boolean isInterrupted() {
        return this.interrupt;
    }

    /**
     * Finish analyze
     *
     * @param keyword
     */
    private void finish(String keyword) {
        BottomStripController.setStatus(String.format(this.bundle.getString("robot.status.data_saving"), keyword));

        this.saveService.save(this.parseService.getResultsToSave());
        this.finishRunnable.run();

        BottomStripController.setStatus(String.format(this.bundle.getString("robot.status.job_finished"), keyword));
        LogsController.success(String.format(this.bundle.getString("robot.log.job_finished"), keyword));
    }


    /**
     * Clear bot
     */
    private void clearBot(){
        this.interrupt = false;
        this.parseService.clearBot();
    }

    /**
     * Get link from API Search Engine
     *
     * @param keyword
     * @return
     */
    private List<Application.Contracts.SearchEngines.IResultModel> getPreLinksApiSearchEngine(String keyword){

        ResultsSearchEngineService resultsSearchEngineService = new ResultsSearchEngineService();

        ISearchEngine searchEngineGoogle = new GoogleSearchEngineService();

        return resultsSearchEngineService.getResultsSearchEngine(
                keyword,
                searchEngineGoogle,
                this.interrupt,
                this.bundle
        );
    }
}
