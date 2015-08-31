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
    private BlockingQueue<Page> pagesQueue;

    public Writer(BlockingQueue<Page> pagesQueue) {
        this.pagesQueue = pagesQueue;
    }

    public void writeToFile(String fileName) throws IOException {
        PageDAO pageDAO = new PageDaoFileImpl(fileName);
        Page currentPage = null;
        try {
            while ((currentPage = pagesQueue.poll(10, TimeUnit.SECONDS)) != null) {
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
        PageDAO pageDAO = new PageDaoJdbcImpl();
        Page currentPage = null;
        try {
            while ((currentPage = pagesQueue.poll(10, TimeUnit.SECONDS)) != null) {
                try {
                    pageDAO.save(currentPage);
                    logger.info("Page [{}] successfully saved to DB", currentPage.getLink());
                } catch (DaoException e) {
                    logger.error("Error serializing page [{}]: {}", currentPage.getLink(), e);
                }
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
