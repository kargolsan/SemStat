package Modules.Data.MySQL.Services;

import Application.Contracts.Data.IResultModel;
import Application.Contracts.Data.ISaveService;
import Application.Controllers.Application.BotController;
import Application.Controllers.Application.LogsController;
import Application.Services.Application.SettingsService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.*;
import java.util.*;

/**
 * Created by IntelliJ IDEA.
 * User: Karol Golec
 * Date: 23.09.2016
 * Time: 19:17
 */
public class MySQLDataService implements ISaveService {

    /**
     * @var logger
     */
    private static final Logger logger = LogManager.getLogger(MySQLDataService.class);

    /**
     * @vars of database
     */
    private String host;
    private String port;
    private String database;
    private String username;
    private String password;
    private String tableName;
    private String columnDomain;
    private String columnUrl;
    private String columnQuantity;
    private String columnDate;
    private String columnKeyword;

    /**
     * @var bundle resource
     */
    private ResourceBundle bundle;

    /**
     * Constructor
     *
     * @param bundle
     */
    public MySQLDataService(ResourceBundle bundle) {
        this.bundle = bundle;
        fillPropertiesMySQL();
    }

    /**
     * Save data with provider
     *
     * @param data
     */
    @Override
    public void save(List<IResultModel> data) {

        data = removeDuplicateData(data);

        if (data.size() <= 0) return;
        String keywordAll = data.get(0).getKeyword();

        if (this.host.contains("http://")) this.host = this.host.replace("http://", "");
        if (this.host.contains("https://")) this.host = this.host.replace("https://", "");

        Connection conn = null;
        Statement st = null;
        ResultSet rs = null;
        String timezone = TimeZone.getDefault().getID();

        String connectionString = String.format("jdbc:mysql://%1$s:%2$s/%3$s?useLegacyDatetimeCode=false&serverTimezone=%4$s&useUnicode=true&characterEncoding=UTF-8&autoReconnect=true&failOverReadOnly=false&maxReconnects=5", this.host, this.port, this.database, timezone);

        Integer counter = 0;

        try {

            conn = DriverManager.getConnection(connectionString, this.username, this.password);

            for (IResultModel item : data) {
                if (checkExistData(item, conn)) continue;
                addDataToDatabase(item, conn);
                counter++;
                BotController.setCountUnique(counter.toString());
            }

            LogsController.success(String.format(this.bundle.getString("robot.log.data_saved"), keywordAll));

        } catch (SQLException ex) {
            LogsController.error(String.format(this.bundle.getString("robot.log.failed_mysql"), ex.getMessage()));
        } finally {
            try {
                if (rs != null) {
                    rs.close();
                }
                if (st != null) {
                    st.close();
                }
                if (conn != null) {
                    conn.close();
                }
            } catch (SQLException ex) {
            }
        }
    }

    /**
     * Remove duplicate items
     *
     * @param data
     * @return items
     */
    private List<IResultModel> removeDuplicateData(List<IResultModel> data) {

        List<IResultModel> uniqueList = new ArrayList<IResultModel>();

        if (data.size() > 0) {
            uniqueList.add(data.get(0));
        }

        for (IResultModel dt : data) {

            String domainData = dt.getDomain();
            String keywordData = dt.getKeyword();

            Boolean has = false;

            for (IResultModel du : uniqueList) {

                String domainUnique = du.getDomain();
                String keywordUnique = du.getKeyword();

                if (domainData.equals(domainUnique) && keywordData.equals(keywordUnique)) {
                    has = true;
                }
            }

            if (!has) {
                uniqueList.add(dt);
            }
        }

        return uniqueList;
    }

    /**
     * Fill properties MySQL
     */
    private void fillPropertiesMySQL() {
        this.host = SettingsService.get("mysql.host");
        this.port = SettingsService.get("mysql.port");
        this.database = SettingsService.get("mysql.database");
        this.username = SettingsService.get("mysql.username");
        this.password = SettingsService.get("mysql.password");
        this.tableName = SettingsService.get("mysql.table.name");
        this.columnDomain = SettingsService.get("mysql.table.column_domain");
        this.columnUrl = SettingsService.get("mysql.table.column_url");
        this.columnQuantity = SettingsService.get("mysql.table.column_quantity");
        this.columnDate = SettingsService.get("mysql.table.column_date");
        this.columnKeyword = SettingsService.get("mysql.table.column_keyword");
    }

    /**
     * Check exist data in database
     *
     * @param data
     * @param conn
     * @return true if exist or false if not exist
     * @throws SQLException
     */
    private Boolean checkExistData(IResultModel data, Connection conn) throws SQLException {

        String queryCheck = String.format("SELECT count(*) from %1$s WHERE %2$s = ? AND %3$s = ?", this.tableName, this.columnDomain, this.columnKeyword);

        PreparedStatement ps = conn.prepareStatement(queryCheck);
        ps.setString(1, data.getDomain());
        ps.setString(2, data.getKeyword());

        ResultSet resultSet = ps.executeQuery();

        if (resultSet.next()) {
            if (resultSet.getInt(1) > 0) {
                return true;
            }
        }
        return false;
    }

    /**
     * Add data to database
     *
     * @param data
     * @param conn
     * @throws SQLException
     */
    private void addDataToDatabase(IResultModel data, Connection conn) throws SQLException {
        String query = String.format(" insert into %1$s (%2$s, %3$s, %4$s, %5$s, %6$s)"
                + " values (?, ?, ?, ?, ?)", this.tableName, this.columnDomain, this.columnUrl, this.columnQuantity, this.columnDate, this.columnKeyword);

        // create the mysql insert prepared statement
        PreparedStatement preparedStmt = conn.prepareStatement(query);
        preparedStmt.setString(1, data.getDomain());
        preparedStmt.setString(2, data.getUrl());
        preparedStmt.setInt(3, data.getQuantity());
        Timestamp timestamp = new Timestamp(data.getDate().getTimeInMillis());
        preparedStmt.setTimestamp(4, timestamp);
        preparedStmt.setString(5, data.getKeyword());

        // execute the prepared statement
        preparedStmt.execute();
    }
}