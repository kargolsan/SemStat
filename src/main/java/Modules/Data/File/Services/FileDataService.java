package Modules.Data.File.Services;

import Application.Contracts.Data.IResultModel;
import Application.Contracts.Data.ISaveService;
import Application.Controllers.Application.BotController;
import Application.Controllers.Application.LogsController;
import Modules.Data.File.Models.Data;
import Modules.Extensions.PhoneEmail.Services.PhoneEmailService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Joiner;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.ResourceBundle;

/**
 * Created by IntelliJ IDEA.
 * User: Karol Golec
 * Date: 23.09.2016
 * Time: 19:17
 */
public class FileDataService implements ISaveService {

    /** @var logger */
    private static final Logger logger = LogManager.getLogger(FileDataService.class);

    /** @var directory for file */
    private static final String DIR = "data";

    /** @var text file */
    private static final String TEXT_FILE = "data.txt";

    /** @var bundle resource */
    private ResourceBundle bundle;

    /** @var bundle resource */
    private PhoneEmailService phoneEmailService;

    /**
     * Constructor
     *
     * @param bundle
     */
    public FileDataService(ResourceBundle bundle){
        this.bundle = bundle;
        this.phoneEmailService = new PhoneEmailService(null, bundle);
    }

    /**
     * Save data with provider
     *
     * @param data
     */
    @Override
    public void save(List<IResultModel> data) {

        data = removeDuplicateData(data);

        data = deleteExistData(data);

        if (data.size() <= 0) return;
        String keywordAll = data.get(0).getKeyword();

        PrintWriter out;
        Integer counter = 0;
        try {
            createDirectory(DIR);

            out = new PrintWriter(new FileOutputStream(new File(DIR + "\\" +TEXT_FILE), true));
            for (IResultModel s : data){

                List<String> pairs = new ArrayList<>();
                pairs.add(String.format("\"domain\":\"%1$s\"", s.getDomain()));
                pairs.add(String.format("\"url\":\"%1$s\"", s.getUrl()));
                pairs.add(String.format("\"quantity\":\"%1$s\"", Objects.toString(s.getQuantity())));
                pairs.add(String.format("\"date\":\"%1$s\"", Objects.toString(s.getDate().getTimeInMillis())));
                pairs.add(String.format("\"keyword\":\"%1$s\"", s.getKeyword()));

                if (phoneEmailService.access()){
                    s.setPhones((s.getPhones()==null) ? "" : s.getPhones());
                    s.setEmails((s.getEmails()==null) ? "" : s.getEmails());
                    pairs.add(String.format("\"phones\":\"%1$s\"", s.getPhones()));
                    pairs.add(String.format("\"emails\":\"%1$s\"", s.getEmails()));
                }

                out.println(String.format("{%1$s}", Joiner.on(",").join(pairs)));

                counter++;
                BotController.setCountUnique(counter.toString());
            }
            if (out != null){
                out.close();
            }
            LogsController.success(String.format(this.bundle.getString("robot.log.data_saved"), keywordAll));
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
    public List<IResultModel> deleteExistData(List<IResultModel> data) {

        List<IResultModel> fromFile = new ArrayList<IResultModel>();
        List<IResultModel> uniqueData = new ArrayList<IResultModel>();

        fromFile.addAll(getDataFromFile());

        for (IResultModel dt : data){

            String domainData = dt.getDomain();
            String keywordData = dt.getKeyword();

            Boolean add = true;

            for (IResultModel df : fromFile){

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
    public List<IResultModel> getDataFromFile(){
        List<IResultModel> dataFromFile = new ArrayList<IResultModel>();

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
                IResultModel data = mapper.readValue(sCurrentLine, Data.class);
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
    private List<IResultModel> removeDuplicateData(List<IResultModel>  data){

        List<IResultModel> uniqueList = new ArrayList<IResultModel>();

        if (data.size() > 0){
            uniqueList.add(data.get(0));
        }

        for (IResultModel dt : data){

            String domainData = dt.getDomain();
            String keywordData = dt.getKeyword();

            Boolean has = false;

            for (IResultModel du : uniqueList){

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
