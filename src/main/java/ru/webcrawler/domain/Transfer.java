package ru.webcrawler.domain;

import org.apache.commons.validator.routines.UrlValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.webcrawler.entity.Page;
import ru.webcrawler.entity.URL;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.*;

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
        this.executorService = Executors.newFixedThreadPool(50);
        this.urls.add(new URL(urlString, 0));
    }

    @Override
    public void run() {
        transfer();
    }

    private void transfer() {
        URL currentUrl = null;

        try {
            while (!(currentUrl = urls.take()).equals(URL.POISON_PILL_URL)) { //TODO как остановить
                if (visitedLinks.add(currentUrl.getUrl()) &&
                        URL_VALIDATOR.isValid(currentUrl.getUrl())) {
                    logger.info("Processing URL [{}], URL depth [{}], target depth [{}]...", currentUrl.getUrl(), currentUrl.getDepth(), depth);
                    if (currentUrl.getDepth() < depth) {
                        executorService.execute(new Parser(pages, urls, currentUrl));
                    } else {
                        logger.info("Depth limit exceeds");
                        urls.add(URL.POISON_PILL_URL);
                    }
                }
            }
            //TODO слишком рано...
            logger.info("Transfer cycle finished");
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        try {
            executorService.awaitTermination(1, TimeUnit.HOURS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        try {
            pages.put(Page.POISON_PILL_PAGE);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        logger.info("Transfer finished, pages.size(): [{}]", pages.size());
    }
}
