package Modules.Data.File.Services;

import Application.Contracts.Data.IDataModel;
import Application.Contracts.Data.IDataService;
import Modules.Data.File.Models.Data;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Created by IntelliJ IDEA.
 * User: Karol Golec
 * Date: 23.09.2016
 * Time: 19:17
 */
public class DataService implements IDataService {

    /** @var logger */
    private static final Logger logger = LogManager.getLogger(DataService.class);

    /** @var directory for file */
    private static final String DIR = "data";

    /** @var text file */
    private static final String TEXT_FILE = "data.txt";

    /** @var da before save */
    List<IDataModel> dataBeforeSave;

    /**
     * Constructor
     */
    public DataService(){
        this.dataBeforeSave = new ArrayList<IDataModel>();
    }

    /**
     * Save data with provider
     *
     * @param data
     */
    @Override
    public void save(List<IDataModel> data) {
        setDataBeforeSave();
        PrintWriter out;
        try {
            createDirectory(DIR);

            out = new PrintWriter(new FileOutputStream(new File(DIR + "\\" +TEXT_FILE), true));
            for (IDataModel s : data){
                if (exist(s)) continue;

                String domain = s.getDomain();
                String url = s.getUrl();
                String quantity = Objects.toString(s.getQuantity());
                String date = Objects.toString(s.getDate().getTimeInMillis());
                String keyword = s.getKeyword();

                out.println(String.format("{\"domain\":\"%1$s\",\"url\":\"%2$s\",\"quantity\":\"%3$s\",\"date\":\"%4$s\",\"keyword\":\"%5$s\"}",
                        domain, url, quantity, date, keyword));
            }
            if (out != null){
                out.close();
            }
        } catch (IOException e) {
            logger.error("Problem z utworzeniem pliku z pobranymi danymi", e);
        }
    }

    /**
     * Check is exist domain with keyword
     *
     * @param data
     */
    @Override
    public Boolean exist(IDataModel data) {
        for (IDataModel d : this.dataBeforeSave){
            Boolean con1 = data.getDomain() == d.getDomain();
            Boolean con2 = data.getKeyword() == d.getKeyword();

            if (con1 && con2){
                return true;
            }
        }
        return false;
    }

    /**
     * Create directory
     *
     * @param dirName
     */
    private void createDirectory(String dirName){
        if (!Files.isDirectory(Paths.get(dirName))){
            File dir = new File(dirName);
            dir.mkdir();
        }
    }

    /**
     * Set data before save
     */
    public void setDataBeforeSave(){
        this.dataBeforeSave.clear();
        BufferedReader br = null;
        try {
            File f = new File(DIR + "\\" + TEXT_FILE);
            if(!f.exists() || f.isDirectory()) {
                return;
            }
            String sCurrentLine;
            br = new BufferedReader(new FileReader(DIR + "\\" + TEXT_FILE));
            while ((sCurrentLine = br.readLine()) != null) {
                ObjectMapper mapper = new ObjectMapper();
                IDataModel data = mapper.readValue(sCurrentLine, Data.class);
                this.dataBeforeSave.add(data);
            }
        } catch (IOException e) {
            logger.error("Błąd podczas odczytywania danych z pliku", e);
        } finally {
            try {
                if (br != null)br.close();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }
}
