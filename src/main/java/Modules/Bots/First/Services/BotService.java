package Modules.Bots.First.Services;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
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
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Created by IntelliJ IDEA.
 * User: Karol Golec
 * Date: 23.09.2016
 * Time: 17:11
 */
public class BotService {

    /** @var logger */
    private static final Logger logger = LogManager.getLogger(AnalyzeBotLong.class);

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

    /** @var text file with except domains */
    private static final String EXCEPTS_FILE = "excepts.txt";

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
        this.parseService = new ParseService(this.filtrExtensionsDomainService, this, this.bundle);
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
     * @param sourcePages
     */
    public void start(String keyword, String filtrExtensionsDomain, String sourcePages) {
        clearBot();
        this.parseService.addLinksForAnalyze(this.parseService.extractLinks(sourcePages));

        this.parseService.addExceptDomainsFromFile(getExceptsDomainFromFile());
        this.filtrExtensionsDomainService.setFiltrExtensionsDomain(filtrExtensionsDomain);

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

    /**
     * Add urls to list from file
     * to long analyzed keyword
     *
     * @return
     */
    private List<String> getExceptsDomainFromFile(){
        List<String> urls = new ArrayList<>();

        File f = new File(EXCEPTS_FILE);
        if(!f.exists() || f.isDirectory()) return urls;

        BufferedReader br = null;
        String sCurrentLine;

        try {
            br = new BufferedReader(new FileReader(EXCEPTS_FILE));

            while ((sCurrentLine = br.readLine()) != null) {
                if (sCurrentLine == "") continue;

                if (!sCurrentLine.contains("http")) sCurrentLine = "http://" + sCurrentLine;

                urls.add(sCurrentLine);
            }
        } catch (IOException e) {
            this.logger.error("Błąd podczas odczytywania danych z pliku " + EXCEPTS_FILE, e);
        } finally {
            try {
                if (br != null)br.close();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
        return urls;
    }
}
