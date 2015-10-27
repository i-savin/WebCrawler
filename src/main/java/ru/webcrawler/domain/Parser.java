package ru.webcrawler.domain;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.webcrawler.entity.Page;
import ru.webcrawler.entity.URL;

import java.io.IOException;
import java.util.concurrent.BlockingQueue;

/**
 * Created by isavin on 15.10.15.
 */
public class Parser implements Runnable {
    private final static Logger logger = LoggerFactory.getLogger(Parser.class);

    private BlockingQueue<Page> pages;
    private BlockingQueue<URL> links;
    private URL currentUrl;

    public Parser(BlockingQueue<Page> pages, BlockingQueue<URL> links, URL currentUrl) {
        this.pages = pages;
        this.links = links;
        this.currentUrl = currentUrl;
    }

    @Override
    public void run() {
        parse();
    }

    private void parse() {
        try {
            Document document = Jsoup.connect(currentUrl.getUrl()).timeout(1000).get();
            pages.add(new Page(currentUrl.getUrl(), document.body().text()));
            Elements linkElements = document.select("a[href]");
            for (Element linkElement : linkElements) {
                String linkStr = linkElement.attr("abs:href");
                links.add(new URL(linkStr, currentUrl.getDepth() + 1));
            }
            logger.info("Processed link: [{}]", currentUrl.getUrl());
        } catch (IOException e) {
            logger.error("Error connecting to URL [{}]: {}", currentUrl.getUrl(), e);
        }
    }
}
