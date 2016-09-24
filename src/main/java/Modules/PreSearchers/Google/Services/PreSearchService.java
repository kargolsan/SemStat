package Modules.PreSearchers.Google.Services;

import Application.Contracts.PreSearches.IPreSearchesService;
import Application.Contracts.PreSearches.IResultModel;
import Application.Controllers.Application.LogsController;
import Application.Services.Application.SettingsService;
import Application.Services.PropertyService;
import com.google.api.client.googleapis.json.GoogleJsonResponseException;
import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpRequestInitializer;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.customsearch.Customsearch;
import com.google.api.services.customsearch.model.Result;
import com.google.api.services.customsearch.model.Search;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: Karol Golec
 * Date: 23.09.2016
 * Time: 10:47
 */
public class PreSearchService implements IPreSearchesService {

    /** @var logger */
    private static final Logger logger = LogManager.getLogger(PreSearchService.class);

    /** @var key api google search custom */
    private String keyApi;

    /** @var id custom engine search google */
    private String cxId;

    /** @var custom search */
    Customsearch custom;

    /** @var application name */
    private static final String APPLICATION_NAME = PropertyService.get("projectName", "Application/Resources/properties.properties");

    /**
     * Constructor
     */
    public PreSearchService(){
        this.keyApi = SettingsService.get("google.api_key");
        this.cxId = SettingsService.get("google.search_engine_id");

        HttpRequestInitializer httpRequestInitializer = new HttpRequestInitializer() {
            @Override
            public void initialize(HttpRequest request) throws IOException {}
        };
        JsonFactory jsonFactory = new JacksonFactory();
        this.custom = new Customsearch.Builder(new NetHttpTransport(), jsonFactory, httpRequestInitializer).setApplicationName(APPLICATION_NAME).build();
    }

    /**
     * Get list results from google search
     *
     * @param query
     * @param page
     */
    public List<IResultModel> get(String query, Integer page){

        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        List<IResultModel> result = new ArrayList<IResultModel>();

        query = String.format("\"%1$s\"", query);

        try {
            Customsearch.Cse.List list = this.custom.cse().list(query);
            list.setCx(this.cxId);
            list.setKey(this.keyApi);
            page = (page == 1) ? 1 : ((page * 10) + 1) -10;
            list.setStart((long) page);
            Search results = list.execute();

            if (! results.containsKey("items")){
                return result;
            }

            for (Result i : results.getItems()){
                IResultModel r = new Modules.PreSearchers.Google.Models.Result();
                r.setTitle(i.getTitle());
                r.setUrl(i.getLink());
                result.add(r);
            }
        }
        catch (GoogleJsonResponseException e) {
            if (e.getDetails().getMessage().contains("Invalid Value")){
                LogsController.warning(String.format("Nie można pobrać danych z wyszukiwarki Google. Parametr zapytania &start=%1$s", (page * 10) + 1));
            } else if (e.getDetails().getMessage().contains("This API requires billing")){
                LogsController.warning(String.format("Wyczerpany dzienny limit zapytań API Google."));
            } else {
                logger.error("Błąd w odpowiedzi od google.", e);
            }
        } catch (IOException ex) {
            logger.error("Błąd podczas pobierania danych z wyszukiwarki google.", ex);
        }
        return result;
    }
}
