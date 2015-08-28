package ru.webcrawler;

import ru.webcrawler.dao.DaoException;
import ru.webcrawler.dao.PageDAO;
import ru.webcrawler.dao.PageDAOFileImpl;
import ru.webcrawler.dao.PageDaoJdbcImpl;
import ru.webcrawler.entity.Page;

import java.io.IOException;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;

/**
 * Created by isavin on 28.08.2015.
 */
public class Writer {
    private BlockingQueue<Page> pagesQueue;

    public Writer(BlockingQueue<Page> pagesQueue) {
        this.pagesQueue = pagesQueue;
    }

    public void writeToFile(String fileName) throws IOException {
        PageDAO pageDAO = new PageDAOFileImpl(fileName);
        Page currentPage = null;
        try {
            while ((currentPage = pagesQueue.poll(10, TimeUnit.SECONDS)) != null) {
                try {
                    pageDAO.create(currentPage);
                    System.out.println("Page " + currentPage.getLink() + " successfully saved to file");
                } catch (DaoException e) {
                    e.printStackTrace();
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
                    pageDAO.create(currentPage);
                    System.out.println("Page " + currentPage.getLink() + " successfully saved to DB");
                } catch (DaoException e) {
                    e.printStackTrace();
                }
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
