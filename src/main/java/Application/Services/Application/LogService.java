package Application.Services.Application;

import Application.Models.Application.Log;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.util.Callback;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.ResourceBundle;

/**
 * Created by IntelliJ IDEA.
 * User: Karol Golec
 * Date: 22.09.2016
 * Time: 18:16
 */
public class LogService {

    /** @var bundle */
    private ResourceBundle bundle;

    /**
     * Constructor
     *
     * @param bundle
     */
    public LogService(ResourceBundle bundle){
        this.bundle = bundle;
    }

    /**
     * Factory value for level log
     *
     * @return value factory
     */
    public Callback<TableColumn.CellDataFeatures<Log, Log.Level>, ObservableValue<Log.Level>> getLevelFactory(){
        return new Callback<TableColumn.CellDataFeatures<Log, Log.Level>, ObservableValue<Log.Level>>() {
            public ObservableValue<Log.Level> call(TableColumn.CellDataFeatures<Log, Log.Level> log) {
                String value = "";
                switch (log.getValue().getLevel()) {
                    case INFO:
                        value = bundle.getString("logs.level.info");
                        break;

                    case SUCCESS:
                        value = bundle.getString("logs.level.success");
                        break;

                    case ERROR:
                        value = bundle.getString("logs.level.error");
                        break;

                    case WARNING:
                        value = bundle.getString("logs.level.warning");
                        break;
                }
                return new ReadOnlyObjectWrapper(value);
            }
        };
    }

    /**
     * Factory value for date log
     *
     * @return value factory
     */
    public Callback<TableColumn.CellDataFeatures<Log, Calendar>, ObservableValue<Calendar>> getDateFactory(){
        return new Callback<TableColumn.CellDataFeatures<Log, Calendar>, ObservableValue<Calendar>>() {
            public ObservableValue<Calendar> call(TableColumn.CellDataFeatures<Log, Calendar> log) {

                Calendar date = log.getValue().getDate();
                SimpleDateFormat df = new SimpleDateFormat("HH:mm:ss - d MMMM yyyy", Locale.getDefault());

                String dateFormat = df.format(date.getTime());
                return new ReadOnlyObjectWrapper(dateFormat);
            }
        };
    }

    /**
     * Row factory for colorized item in table view
     *
     * @return
     */
    public Callback<TableView<Log>, TableRow<Log>> getRowFactory(){
       return new Callback<TableView<Log>, TableRow<Log>>() {
            @Override public TableRow<Log> call(TableView<Log> param) {
                return new TableRow<Log>() {
                    @Override protected void updateItem(Log item, boolean empty) {
                        super.updateItem(item, empty);

                        if (empty) return;

                        getStyleClass().remove("success");
                        getStyleClass().remove("error");
                        getStyleClass().remove("warning");
                        getStyleClass().remove("info");

                        switch(item.getLevel()){
                            case SUCCESS:
                                getStyleClass().add("success");
                                break;
                            case ERROR:
                                getStyleClass().add("error");
                                break;
                            case WARNING:
                                getStyleClass().add("warning");
                                break;
                            case INFO:
                                getStyleClass().add("info");
                                break;
                        }
                    }
                };
            }
        };
    }
}
