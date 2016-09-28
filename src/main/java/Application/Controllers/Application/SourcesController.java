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

    /** @var bundle resource */
    private ResourceBundle bundle;

    /** @var text file with urls */
    private static final String URLS_FILE = "urls.txt";

    /**
     * Called to initialize a controller after its root element has been
     * completely processed.
     */
    @Override
    public void initialize(URL location, ResourceBundle bundle) {
        this.bundle = bundle;
        loadSources();
    }

    /**
     * Save sources
     */
    @FXML
    public void save(){
        PrintWriter out;
        try {
            out = new PrintWriter(new FileOutputStream(new File(URLS_FILE)));
            for (String line : this.sources.getText().split("\\n")){
                if (line !=""){
                    out.println(line);
                }
            }
            if (out != null){
                out.close();
            }
        } catch (IOException e) {
            logger.error("Problem z operacją pliku urls.txt", e);
        }
    }

    /**
     * Load sources from file
     */
    private void loadSources(){
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
}