package ru.webcrawler.domain;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.webcrawler.entity.Page;
import ru.webcrawler.repository.Repository;

import java.util.concurrent.BlockingQueue;

/**
 * @author isavin
 */
@Component
public class Writer extends Thread {
    private static final Logger logger = LoggerFactory.getLogger(Writer.class);

    private BlockingQueue<Page> pages;

    @Autowired
    private Repository repository;

    public Writer(BlockingQueue<Page> pages) {
        this.pages = pages;
    }

    @Override
    public void run() {
        savePages();
    }

    private void savePages() {
        Page currentPage = null;

        try {
            while (!(currentPage = pages.take()).equals(Page.POISON_PILL_PAGE)) {
                try {
                    repository.save(currentPage);
                    logger.info("Page [{}] was successfully saved", currentPage.getLink());
                } catch (Exception e) {
                    logger.error("Error serializing page [{}]:", currentPage.getLink());
                    logger.error("{}", e);
                }
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        logger.info("All pages from queue was saved");
    }
}
