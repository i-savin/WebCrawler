package ru.webcrawler;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.webcrawler.dao.DaoException;
import ru.webcrawler.dao.PageDAO;
import ru.webcrawler.dao.PageDaoFileImpl;
import ru.webcrawler.dao.PageDaoJdbcImpl;
import ru.webcrawler.entity.Page;

import java.io.IOException;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;

/**
 * Created by isavin on 28.08.2015.
 */
public class Writer {
    private static final Logger logger = LoggerFactory.getLogger(Writer.class);
    private static final int CONNECTION_TIMEOUT_IN_SECONDS = Integer.parseInt(Settings.getSettingsInstance().get("connection.timeout"));//100
    private BlockingQueue<Page> pagesQueue;

    public Writer(BlockingQueue<Page> pagesQueue) {
        this.pagesQueue = pagesQueue;
    }

    public void writeToFile(String fileName) throws IOException {
        PageDAO pageDAO = new PageDaoFileImpl(fileName);
        Page currentPage = null;
        try {
            while ((currentPage = pagesQueue.poll(CONNECTION_TIMEOUT_IN_SECONDS, TimeUnit.SECONDS)) != null) {
                try {
                    pageDAO.save(currentPage);
                    logger.info("Page [{}] successfully saved to file", currentPage.getLink());
                } catch (DaoException e) {
                    logger.error("Error serializing page [{}]: {}", currentPage.getLink(), e);
                }
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void writeToDB() throws ClassNotFoundException {
        String connectionUrl = Settings.getSettingsInstance().get("db.connection.url");//"jdbc:hsqldb:hsql://localhost/xdb";
        String driverName = Settings.getSettingsInstance().get("db.driver.name");//"org.hsqldb.jdbcDriver";
        String userName = Settings.getSettingsInstance().get("db.user.name");//"SA";
        String password = Settings.getSettingsInstance().get("db.password");//"";

        PageDAO pageDAO = new PageDaoJdbcImpl(connectionUrl, driverName, userName, password);
        Page currentPage = null;
        try {
            while ((currentPage = pagesQueue.poll(CONNECTION_TIMEOUT_IN_SECONDS, TimeUnit.SECONDS)) != null) {
                try {
                    pageDAO.save(currentPage);
                    logger.info("Page [{}] successfully saved to DB", currentPage.getLink());
                } catch (DaoException e) {
                    logger.error("Error serializing page [{}]: {}", currentPage.getLink(), e.getCause());
                }
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
