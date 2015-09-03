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

    private int connectionTimeout;
    private PageDAO pageDao;

    public Writer(int connectionTimeout) {
        this.connectionTimeout = connectionTimeout;
    }

    public void savePagesFromQueue(BlockingQueue<Page> pagesQueue) {
        Page currentPage = null;
        try {
            while ((currentPage = pagesQueue.poll(connectionTimeout, TimeUnit.SECONDS)) != null) {
                try {
                    pageDao.save(currentPage);
                } catch (DaoException e) {
                    logger.error("Error serializing page [{}]: {}", currentPage.getLink(), e);
                }
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void setPageDao(PageDAO pageDao) {
        this.pageDao = pageDao;
    }
}
