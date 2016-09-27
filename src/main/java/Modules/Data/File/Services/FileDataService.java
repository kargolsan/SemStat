package Modules.Data.File.Services;

import Application.Contracts.Data.IDataModel;
import Application.Contracts.Data.IDataService;
import Application.Controllers.Application.BotController;
import Modules.Data.File.Models.Data;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.org.apache.xpath.internal.operations.Bool;
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
public class FileDataService implements IDataService {

    /** @var logger */
    private static final Logger logger = LogManager.getLogger(FileDataService.class);

    /** @var directory for file */
    private static final String DIR = "data";

    /** @var text file */
    private static final String TEXT_FILE = "data.txt";

    /**
     * Save data with provider
     *
     * @param data
     */
    @Override
    public void save(List<IDataModel> data) {

        data = removeDuplicateData(data);
        data = deleteExistData(data);

        PrintWriter out;
        Integer counter = 0;
        try {
            createDirectory(DIR);

            out = new PrintWriter(new FileOutputStream(new File(DIR + "\\" +TEXT_FILE), true));
            for (IDataModel s : data){

                String domain = s.getDomain();
                String url = s.getUrl();
                String quantity = Objects.toString(s.getQuantity());
                String date = Objects.toString(s.getDate().getTimeInMillis());
                String keyword = s.getKeyword();

                out.println(String.format("{\"domain\":\"%1$s\",\"url\":\"%2$s\",\"quantity\":\"%3$s\",\"date\":\"%4$s\",\"keyword\":\"%5$s\"}",
                        domain, url, quantity, date, keyword));

                counter++;
                BotController.setSavedUniqueDomainsProperty(counter.toString());
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
     * @return
     */
    public List<IDataModel> deleteExistData(List<IDataModel> data) {

        List<IDataModel> fromFile = new ArrayList<IDataModel>();
        List<IDataModel> uniqueData = new ArrayList<IDataModel>();

        fromFile.addAll(getDataFromFile());

        for (IDataModel dt : data){

            String domainData = dt.getDomain();
            String keywordData = dt.getKeyword();

            Boolean add = true;

            for (IDataModel df : fromFile){

                String domainInFile = df.getDomain();
                String keywordInFile = df.getKeyword();

                if (domainInFile.equals(domainData) && keywordInFile.equals(keywordData)){
                    add = false;
                }
            }
            if (add){
                uniqueData.add(dt);
            }
        }
        return uniqueData;
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
    public List<IDataModel> getDataFromFile(){
        List<IDataModel> dataFromFile = new ArrayList<IDataModel>();

        BufferedReader br = null;
        try {
            File f = new File(DIR + "\\" + TEXT_FILE);
            if(!f.exists() || f.isDirectory()) {
                return dataFromFile;
            }
            String sCurrentLine;
            br = new BufferedReader(new FileReader(DIR + "\\" + TEXT_FILE));
            while ((sCurrentLine = br.readLine()) != null) {
                ObjectMapper mapper = new ObjectMapper();
                IDataModel data = mapper.readValue(sCurrentLine, Data.class);
                dataFromFile.add(data);
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
        return dataFromFile;
    }

    /**
     * Remove duplicate items
     *
     * @param data
     * @return items
     */
    private List<IDataModel> removeDuplicateData(List<IDataModel>  data){

        List<IDataModel> uniqueList = new ArrayList<IDataModel>();

        if (data.size() > 0){
            uniqueList.add(data.get(0));
        }

        for (IDataModel dt : data){

            String domainData = dt.getDomain();
            String keywordData = dt.getKeyword();

            Boolean has = false;

            for (IDataModel du : uniqueList){

                String domainUnique = du.getDomain();
                String keywordUnique = du.getKeyword();

                if (domainData.equals(domainUnique) && keywordData.equals(keywordUnique)){
                    has = true;
                }
            }

            if (!has){
                uniqueList.add(dt);
            }
        }

        return uniqueList;
    }
}
