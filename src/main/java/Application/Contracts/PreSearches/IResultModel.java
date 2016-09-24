package Application.Contracts.PreSearches;

/**
 * Created by IntelliJ IDEA.
 * User: Karol Golec
 * Date: 23.09.2016
 * Time: 23:20
 */
public interface IResultModel {
    String getTitle();

    void setTitle(String title);

    String getUrl();

    void setUrl(String url);
}
