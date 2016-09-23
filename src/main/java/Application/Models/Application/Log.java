package Application.Models.Application;

import java.util.Calendar;

/**
 * Created by IntelliJ IDEA.
 * User: Karol Golec
 * Date: 22.09.2016
 * Time: 16:37
 */
public class Log {
    public enum Level {
        SUCCESS, ERROR, WARNING, INFO, FATAL
    }

    private Level level;

    private String message;

    private Calendar date;

    public Log(Level level, String message, Calendar date){
        this.level = level;
        this.message = message;
        this.date = date;
    }

    public Level getLevel() {
        return level;
    }

    public void setLevel(Level level) {
        this.level = level;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Calendar getDate() {
        return date;
    }

    public void setDate(Calendar date) {
        this.date = date;
    }
}
