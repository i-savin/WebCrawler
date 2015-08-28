package ru.webcrawler.dao;

import ru.webcrawler.entity.Page;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.*;

/**
 * Created by isavin on 28.08.2015.
 */
public class PageDaoJdbcImpl  implements PageDAO {

    private String connectionTemplate = "jdbc:hsqldb:hsql://localhost/xdb";
    private String driverName = "org.hsqldb.jdbcDriver";
    private String userName = "SA";
    private String password = "";

    public PageDaoJdbcImpl() throws ClassNotFoundException {
        Class.forName(driverName);
    }

    @Override
    public void create(Page page) throws DaoException {
        try (Connection connection = DriverManager.getConnection(connectionTemplate, userName, password);

             PreparedStatement ps = connection.prepareStatement("INSERT INTO PUBLIC.PAGES (URL, TEXT) VALUES (?, ?)");) {

            ps.setString(1, page.getLink());
            ps.setString(2, page.getText());
            ps.executeUpdate();
            connection.commit();

        } catch (SQLException e) {
            throw new DaoException("Error writing page " + page.getLink() + " to DB:", e);
        }
    }
}
