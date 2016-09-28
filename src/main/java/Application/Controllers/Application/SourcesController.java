package Application.Controllers.Application;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TextArea;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.*;
import java.net.URL;
import java.util.ResourceBundle;

/**
 * Created by IntelliJ IDEA.
 * User: Karol Golec
 * Date: 23.09.2016
 * Time: 22:10
 */
public class SourcesController implements Initializable {

    /** @var logger */
    private static final Logger logger = LogManager.getLogger(SourcesController.class);

    @FXML
    private TextArea sources;

    @FXML
    private TextArea text;

    @FXML
    private TextArea excepts;

    /** @var bundle resource */
    private ResourceBundle bundle;

    /** @var text file with urls */
    private static final String URLS_FILE = "urls.txt";

    /** @var text file with sources */
    private static final String SOURCES_FILE = "sources.txt";

    /** @var text file with except domains */
    private static final String EXCEPTS_FILE = "excepts.txt";

    /**
     * Get text of field in interface
     *
     * @return text
     */
    public TextArea getText() {
        return text;
    }

    /**
     * Called to initialize a controller after its root element has been
     * completely processed.
     */
    @Override
    public void initialize(URL location, ResourceBundle bundle) {
        this.bundle = bundle;
        loadUrls();
        loadSources();
        loadExcepts();
    }

    /**
     * Save sources
     */
    @FXML
    public void save(){
        saveUrls();
        saveSources();
        saveExcepts();
    }

    /**
     * Load urls from file
     */
    private void loadUrls(){
        File f = new File(URLS_FILE);
        if(!f.exists() || f.isDirectory()) return;

        BufferedReader br = null;
        String sCurrentLine;

        try {
            br = new BufferedReader(new FileReader(URLS_FILE));

            while ((sCurrentLine = br.readLine()) != null) {
                this.sources.appendText(sCurrentLine + System.getProperty("line.separator"));
            }
        } catch (IOException e) {
            logger.error("Błąd podczas odczytywania danych z pliku urls.txt", e);
        } finally {
            try {
                if (br != null)br.close();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }

        this.sources.setScrollTop(0.0);
        this.sources.positionCaret(0);
    }

    /**
     * Load urls from file
     */
    private void loadSources(){
        File f = new File(SOURCES_FILE);
        if(!f.exists() || f.isDirectory()) return;

        BufferedReader br = null;
        String sCurrentLine;

        try {
            br = new BufferedReader(new FileReader(SOURCES_FILE));

            while ((sCurrentLine = br.readLine()) != null) {
                this.text.appendText(sCurrentLine + System.getProperty("line.separator"));
            }
        } catch (IOException e) {
            logger.error("Błąd podczas odczytywania danych z pliku " + SOURCES_FILE, e);
        } finally {
            try {
                if (br != null)br.close();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }

        this.text.setScrollTop(0.0);
        this.text.positionCaret(0);
    }

    /**
     * Load except domain from file
     */
    private void loadExcepts(){
        File f = new File(EXCEPTS_FILE);
        if(!f.exists() || f.isDirectory()) return;

        BufferedReader br = null;
        String sCurrentLine;

        try {
            br = new BufferedReader(new FileReader(EXCEPTS_FILE));

            while ((sCurrentLine = br.readLine()) != null) {
                this.excepts.appendText(sCurrentLine + System.getProperty("line.separator"));
            }
        } catch (IOException e) {
            logger.error("Błąd podczas odczytywania danych z pliku " + EXCEPTS_FILE, e);
        } finally {
            try {
                if (br != null)br.close();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }

        this.excepts.setScrollTop(0.0);
        this.excepts.positionCaret(0);
    }

    /**
     * Save urls to file
     */
    private void saveUrls(){
        PrintWriter out;
        try {
            out = new PrintWriter(new FileOutputStream(new File(URLS_FILE)));
            for (String line : this.sources.getText().split(System.getProperty("line.separator"))){
                if (line !=""){
                    out.println(line);
                }
            }
            if (out != null){
                out.close();
            }
        } catch (IOException e) {
            logger.error("Problem z operacją pliku " + URLS_FILE, e);
        }
    }

    /**
     * Save sources to file
     */
    private void saveSources(){
        PrintWriter out;
        try {
            out = new PrintWriter(new FileOutputStream(new File(SOURCES_FILE)));

            for (String line : this.text.getText().split(System.getProperty("line.separator"))){
                if (line !=""){
                    out.println(line);
                }
            }

            if (out != null){
                out.close();
            }
        } catch (IOException e) {
            logger.error("Problem z operacją pliku " + SOURCES_FILE, e);
        }
    }

    /**
     * Save except domains to file
     */
    private void saveExcepts(){
        PrintWriter out;
        try {
            out = new PrintWriter(new FileOutputStream(new File(EXCEPTS_FILE)));

            for (String line : this.excepts.getText().split(System.getProperty("line.separator"))){
                if (line !=""){
                    out.println(line);
                }
            }

            if (out != null){
                out.close();
            }
        } catch (IOException e) {
            logger.error("Problem z operacją pliku " + EXCEPTS_FILE, e);
        }
    }

}