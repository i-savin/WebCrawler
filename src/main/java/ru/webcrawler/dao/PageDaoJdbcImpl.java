package ru.webcrawler.dao;

import ru.webcrawler.Settings;
import ru.webcrawler.entity.Page;

import java.sql.*;

/**
 * Created by isavin on 28.08.2015.
 */
public class PageDaoJdbcImpl  implements PageDAO {

    private final static String INSERT_SQL_SCRIPT = "INSERT INTO PUBLIC.PAGES (URL, TEXT) VALUES (?, ?)";

    private String connectionUrl = Settings.getSettingsInstance().get("db.connection.url");//"jdbc:hsqldb:hsql://localhost/xdb";
    private String driverName = Settings.getSettingsInstance().get("db.driver.name");//"org.hsqldb.jdbcDriver";
    private String userName = Settings.getSettingsInstance().get("db.user.name");//"SA";
    private String password = Settings.getSettingsInstance().get("db.password");//"";

    public PageDaoJdbcImpl() throws ClassNotFoundException {
        Class.forName(driverName);
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
}
