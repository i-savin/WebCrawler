package ru.webcrawler.domain;

import org.apache.commons.validator.routines.UrlValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.webcrawler.entity.Page;
import ru.webcrawler.entity.URL;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * Created by isavin on 27.10.15.
 */
public class Transfer implements Runnable {
    private final static Logger logger = LoggerFactory.getLogger(Transfer.class);
    private final static UrlValidator URL_VALIDATOR = new UrlValidator(UrlValidator.ALLOW_2_SLASHES + UrlValidator.ALLOW_LOCAL_URLS);

    private BlockingQueue<Page> pages;
    private BlockingQueue<URL> urls;
    private Set<String> visitedLinks;
    private int depth;
    private ExecutorService executorService;

    public Transfer(BlockingQueue<Page> pages, String urlString, int depth) {
        this.pages = pages;
        this.depth = depth;
        this.urls = new LinkedBlockingQueue<>();
        this.visitedLinks = new HashSet<>();
        this.executorService = Executors.newFixedThreadPool(5);
        this.urls.add(new URL(urlString, 0));
    }

    @Override
    public void run() {
        transfer();
    }

    private void transfer() {
        URL currentUrl = null;

        try {
            while ((currentUrl = urls.take()) != null) { //TODO как остановить
                if (visitedLinks.add(currentUrl.getUrl()) &&
                        URL_VALIDATOR.isValid(currentUrl.getUrl())) {
                    logger.info("Processing URL [{}]...", currentUrl.getUrl());
                    if (currentUrl.getDepth() <= depth) {
                        executorService.execute(new Parser(pages, urls, currentUrl));
                    } else {
                        logger.info("Depth limit exceeds");
                        urls.add(null);
                    }
                }
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
