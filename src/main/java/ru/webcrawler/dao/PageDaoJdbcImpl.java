package ru.webcrawler.dao;

import ru.webcrawler.Settings;
import ru.webcrawler.entity.Page;

import java.sql.*;

/**
 * Created by isavin on 28.08.2015.
 */
public class PageDaoJdbcImpl  implements PageDAO {

    private final static String INSERT_SQL_SCRIPT = "INSERT INTO PUBLIC.PAGES (URL, TEXT) VALUES (?, ?)";

    private String connectionUrl; //= Settings.getSettingsInstance().get("db.connection.url");//"jdbc:hsqldb:hsql://localhost/xdb";
    private String driverName;// = Settings.getSettingsInstance().get("db.driver.name");//"org.hsqldb.jdbcDriver";
    private String userName;// = Settings.getSettingsInstance().get("db.user.name");//"SA";
    private String password;// = Settings.getSettingsInstance().get("db.password");//"";

    public PageDaoJdbcImpl(String connectionUrl, String driverName, String userName, String password) throws ClassNotFoundException {
        this.connectionUrl = connectionUrl;
        this.driverName = driverName;
        this.userName = userName;
        this.password = password;
        Class.forName(this.driverName);
    }

    @Override
    public void save(Page page) throws DaoException {
        try (Connection connection = DriverManager.getConnection(connectionUrl, userName, password);
            PreparedStatement ps = connection.prepareStatement(INSERT_SQL_SCRIPT);) {
            ps.setString(1, page.getLink());
            ps.setString(2, page.getText());
            ps.executeUpdate();
            connection.commit();
        } catch (SQLException e) {
            throw new DaoException("Error writing page " + page.getLink() + " to DB:", e);
        }
    }

    public void setConnectionUrl(String connectionUrl) {
        this.connectionUrl = connectionUrl;
    }

    public void setDriverName(String driverName) {
        this.driverName = driverName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
