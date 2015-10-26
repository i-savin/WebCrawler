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
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by isavin on 27.08.2015.
 */
public class Parser {

    private static final Logger logger = LoggerFactory.getLogger(Parser.class);

    private BlockingQueue<Url> linksQueue = new LinkedBlockingQueue<>();
    private AtomicInteger linksCount = new AtomicInteger(0);
    private CopyOnWriteArrayList<Url> visitedLinks = new CopyOnWriteArrayList<>();

    private int threadPoolSize;
    private int connectionTimeout;

    public Parser(int threadPoolSize, int connectionTimeout) {
        this.threadPoolSize = threadPoolSize;
        this.connectionTimeout = connectionTimeout;
    }

    public BlockingQueue<Page> parse(String urlStr, final int depthLimit) {
        final LinkedBlockingQueue<Page> pagesQueue = new LinkedBlockingQueue<>();
        linksQueue.add(new Url(urlStr, 1));
        new Thread(new Runnable() {
            @Override
            public void run() {
                ExecutorService es = Executors.newFixedThreadPool(threadPoolSize);
                Url currentUrl = null;
                try {
                    while ((currentUrl = linksQueue.poll(connectionTimeout, TimeUnit.SECONDS)) != null) {
                        es.execute(new UrlParser(currentUrl, depthLimit, pagesQueue));
                    }
                    es.shutdown();
                    boolean finished = es.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);
                    logger.info("Method finished: {}. URLs number: {}", true, linksCount.get());
                } catch (InterruptedException e) {e.printStackTrace();}
            }
        }).start();
        return pagesQueue;
    }

    private class UrlParser implements Runnable {
        private Url currentUrl;
        private int depthLimit;
        private BlockingQueue<Page> pagesQueue;

        public UrlParser(Url currentUrl, int depthLimit, BlockingQueue<Page> pagesQueue) {
            this.currentUrl = currentUrl;
            this.depthLimit = depthLimit;
            this.pagesQueue = pagesQueue;
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
                Document document = Jsoup.connect(currentUrl.getUrl()).timeout(connectionTimeout * 1000).get();
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
