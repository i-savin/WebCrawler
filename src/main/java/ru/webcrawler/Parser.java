package ru.webcrawler;

import org.apache.commons.validator.routines.UrlValidator;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.webcrawler.entity.Page;
import ru.webcrawler.entity.Url;

import java.io.IOException;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by isavin on 27.08.2015.
 */
public class Parser {

    private static final Logger logger = LoggerFactory.getLogger(Parser.class);

    private static final int THREAD_POOL_SIZE = Integer.parseInt(Settings.getSettingsInstance().get("threadpool.size"));//100
    private static final int CONNECTION_TIMEOUT_IN_SECONDS = Integer.parseInt(Settings.getSettingsInstance().get("connection.timeout"));//100

    private BlockingQueue<Url> linksQueue = new LinkedBlockingQueue<>();
    private AtomicInteger linksCount = new AtomicInteger(0);
    private CopyOnWriteArrayList<Url> visitedLinks = new CopyOnWriteArrayList<>();

    private BlockingQueue<Page> pagesQueue;

    public Parser(BlockingQueue<Page> pagesQueue) {
        this.pagesQueue = pagesQueue;
    }

    public void parse(String urlStr, final int depthLimit) {
        linksQueue.add(new Url(urlStr, 1));
        ExecutorService es = Executors.newFixedThreadPool(THREAD_POOL_SIZE);
        Url currentUrl = null;
        try {
            while ((currentUrl = linksQueue.poll(CONNECTION_TIMEOUT_IN_SECONDS, TimeUnit.SECONDS)) != null) {
                es.execute(new UrlParser(currentUrl, depthLimit));
            }
            es.shutdown();
            es.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);
        } catch (InterruptedException e) {}

        logger.info("Method finished. URLs number: {}", linksCount.get());
    }

    private class UrlParser implements Runnable {
        private Url currentUrl;
        private int depthLimit;

        public UrlParser(Url currentUrl, int depthLimit) {
            this.currentUrl = currentUrl;
            this.depthLimit = depthLimit;
        }

        @Override
        public void run() {
            parseUrl();
        }

        private void parseUrl() {
            UrlValidator urlValidator = new UrlValidator(UrlValidator.ALLOW_2_SLASHES + UrlValidator.ALLOW_LOCAL_URLS);
            synchronized (visitedLinks) {
                if (visitedLinks.contains(currentUrl) || !urlValidator.isValid(currentUrl.getUrl())) {
                    return;
                }
                visitedLinks.addIfAbsent(currentUrl);
            }
            try {
                Document document = Jsoup.connect(currentUrl.getUrl()).timeout(CONNECTION_TIMEOUT_IN_SECONDS * 1000).get();
                pagesQueue.add(new Page(currentUrl.getUrl(), document.body().text()));
                if (currentUrl.getDepth() < depthLimit) {
                    Elements links = document.select("a[href]");
                    for (Element link : links) {
                        String linkStr = link.attr("abs:href");
                        linksQueue.add(new Url(linkStr, currentUrl.getDepth() + 1));
                    }
                }
                linksCount.incrementAndGet();
                logger.info("Processed link: [{}]", currentUrl.getUrl());
            } catch (IOException e) {
                logger.error("Error connecting to URL [{}]: {}", currentUrl.getUrl(), e);
            }
        }
    }
}
