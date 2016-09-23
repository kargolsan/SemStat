package Application.Controllers.Application;

import java.net.URL;
import java.util.Calendar;
import java.util.ResourceBundle;

import Application.Models.Application.Log;
import Application.Services.Application.LogService;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

/**
 * Created by IntelliJ IDEA.
 * User: Karol Golec
 * Date: 22.09.2016
 * Time: 16:17
 */
public class LogsController implements Initializable {

    /** @var log service */
    private LogService logService;

    @FXML
    private TableView<Log> logsTable;

    @FXML
    private TableColumn<Log, Log.Level> levelColumn;

    @FXML
    private TableColumn<Log, String> messageColumn;

    @FXML
    private TableColumn<Log, Calendar> dateColumn;

    /**
     * Observable list with repairs for table in view
     */
    private static ObservableList<Log> logs;


    /**
     * Constructor
     */
    public LogsController(){
        this.logs = FXCollections.observableArrayList();
    }

    /**
     * Called to initialize a controller after its root element has been
     * completely processed.
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        this.logService = new LogService(resources);

        this.levelColumn.setCellValueFactory(this.logService.getLevelFactory());
        this.messageColumn.setCellValueFactory(new PropertyValueFactory("message"));
        this.dateColumn.setCellValueFactory(this.logService.getDateFactory());

        this.logsTable.setRowFactory(logService.getRowFactory());
        this.logsTable.setItems(this.logs);
    }

    /**
     * Add info log to list
     *
     * @param message
     */
    public static void info(String message){
        Platform.runLater(()->{
            Log log = new Log(Log.Level.INFO , message, Calendar.getInstance());
            logs.add(0, log);
        });
    }

    /**
     * Add success log to list
     *
     * @param message
     */
    public static void success(String message){
        Platform.runLater(()->{
            Log log = new Log(Log.Level.SUCCESS , message, Calendar.getInstance());
            logs.add(0, log);
        });
    }

    /**
     * Add error log to list
     *
     * @param message
     */
    public static void error(String message){
        Platform.runLater(()->{
            Log log = new Log(Log.Level.ERROR , message, Calendar.getInstance());
            logs.add(0, log);
        });
    }

    /**
     * Add error log to list
     *
     * @param message
     */
    public static void warning(String message){
        Platform.runLater(()->{
            Log log = new Log(Log.Level.WARNING , message, Calendar.getInstance());
            logs.add(0, log);
        });
    }
}
