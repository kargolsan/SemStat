package Modules.Bots.First.Services;

import Application.Contracts.Bots.IBotService;
import Application.Contracts.PreSearches.IPreSearchesService;
import Application.Contracts.PreSearches.IResultModel;
import Application.Contracts.Data.IDataModel;
import Application.Contracts.Data.IDataService;
import Application.Controllers.Application.BottomStripController;
import Application.Controllers.Application.LogsController;
import Application.Services.Application.SettingsService;
import Application.Services.AsyncService;
import Modules.Data.File.Models.Data;
import org.apache.commons.lang3.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by IntelliJ IDEA.
 * User: Karol Golec
 * Date: 23.09.2016
 * Time: 17:11
 */
public class BotService implements IBotService {

    /** @var call stop crawler */
    private Boolean stop = false;

    /** @var data to save */
    private List<IDataModel> dataToSave;

    /** @var save service */
    private IPreSearchesService preSearch;

    /** @var save service */
    private IDataService save;

    /** @var runnable after stop */
    Runnable afterStop;

    /** @var runnable after end */
    Runnable afterEnd;

    /**
     * Stop robot
     */
    public void callStop() {
        this.stop = true;
    }

    /**
     * Constructor
     *
     * @param preSearch
     * @param save
     */
    public BotService(IPreSearchesService preSearch, IDataService save){
        this.dataToSave = new ArrayList<IDataModel>();
        this.preSearch = preSearch;
        this.save = save;
    }

    /**
     * Rub robot
     *
     * @param keyWords
     * @param afterEnd
     * @param afterStop
     */
    public void run(String keyWords, Runnable afterEnd, Runnable afterStop) {
        this.afterEnd = afterEnd;
        this.afterStop = afterStop;

        BottomStripController.setStatus(String.format("Uruchamianie robota..."));

        AsyncService async = new AsyncService();
        async.single(() -> {
            List<IResultModel> result = getPreUrls(keyWords);

            LogsController.info("Zakończono zbieranie wstępnych adresów internetowych.");

            if (this.stop == true){
                stop();
                return;
            }

            if (result.size() == 0){
                LogsController.warning("Nie pobrano wstępnych adresów stron. Proszę odczekać kilka minut lub zmienić silnik wstępnego szukania.");
                stop();
                return;
            }
            crawler(result, keyWords);
        }, () -> {});
    }

    /**
     * Ger pre urls from search engine
     *
     * @param keyWords
     * @return list pre urls
     */
    private List<IResultModel> getPreUrls(String keyWords) {
        String lpu = SettingsService.get("bot.limit_pre_urls");
        lpu = (lpu == "") ? "999" : lpu;

        Integer limitPerUrls = 999;
        try {
            limitPerUrls = Integer.parseInt(lpu);
        } catch(Exception e){}

        List<IResultModel> result = new ArrayList<IResultModel>();
        Integer page = 1;

        while(true) {
            List<IResultModel> r = this.preSearch.get(keyWords, page);

            if (r.size() == 0 || this.stop == true){
                break;
            } else if (limitPerUrls <=result.size()){
                break;
            }
            result.addAll(r);
            page++;
            BottomStripController.setStatus(String.format("Zbieranie wstępnych adresów storn internetowych. Zebrano %1$s", result.size()));
        }

        return result;
    }

    /**
     * Crawler on websites
     *
     * @param urls
     * @param keyWord
     */
    private void crawler(List<IResultModel> urls, String keyWord) {
        Integer threadLimit = getLimitThreads();
        threadLimit = (threadLimit < 4) ? 2 : threadLimit -2;

        ExecutorService executor = Executors.newFixedThreadPool(threadLimit);

        Integer counter = 1;
        for (IResultModel u : urls) {
            if (this.stop == true){
                stop();
                return;
            }
            executor.execute(() -> {

                try {
                    Document doc = Jsoup.connect(u.getUrl()).get();
                    BottomStripController.setStatus(String.format("Sprawdzanie strony internetowej: %1$s", doc.baseUri()));

                    addToSave(doc, keyWord);

                } catch (IOException e) {}

            });
        }
        executor.shutdown();
        while (!executor.isTerminated()) {
        }
        finish();
    }

    /**
     * Get limit threads of processor
     *
     * @return quantity threads
     */
    private Integer getLimitThreads() {
        String lt = SettingsService.get("processor.limit_threads");
        if (lt == "") return 10;

        try {
            return Integer.parseInt(lt);
        } catch (Exception ex) {
        }
        return 10;
    }

    /**
     * Save result
     *
     * @param doc
     */
    private void addToSave(Document doc, String keyWord){
        String text = doc.select("body").text();

        if (text.toLowerCase().contains(keyWord.toLowerCase())){
            try {
                Integer quantity = StringUtils.countMatches(text.toLowerCase(), keyWord.toLowerCase());
                IDataModel data = new Data();
                data.setDomain(getDomainName(doc.baseUri()));
                data.setUrl(doc.baseUri());
                data.setQuantity(quantity);
                data.setDate(Calendar.getInstance());
                data.setKeyword(keyWord);
                this.dataToSave.add(data);
            } catch (URISyntaxException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Get domain from url
     *
     * @param url
     * @return
     * @throws URISyntaxException
     */
    public String getDomainName(String url) throws URISyntaxException {
        URI uri = new URI(url);
        String domain = uri.getHost();
        return domain.startsWith("www.") ? domain.substring(4) : domain;
    }

    /**
     * Finish analyze
     */
    private void finish(){
        BottomStripController.setStatus("Rozpoczynanie zapisywania danych");
        this.save.save(this.dataToSave);
        this.afterEnd.run();
        BottomStripController.setStatus("Zakończono pracę robota");
        LogsController.success("Zakończono pracę robota.");
    }

    /**
     * Stop analyze
     */
    private void stop(){
        this.stop = true;
        BottomStripController.setStatus("Działanie robota przerwane");
        LogsController.info("Działanie robota przerwane.");
        this.afterStop.run();
    }
}
