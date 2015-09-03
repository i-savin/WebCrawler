package ru.webcrawler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.webcrawler.entity.Page;

import java.util.concurrent.BlockingQueue;

/**
 * Created by isavin on 26.08.2015.
 */
public class WebCrawler {

    private final static Logger logger = LoggerFactory.getLogger(WebCrawler.class);

    private Parser parser;
    private Writer writer;

    public WebCrawler(Parser parser, Writer writer) {
        this.parser = parser;
        this.writer = writer;
    }

    public void crawl(String url, int depth) {
        BlockingQueue<Page> pagesQueue = parser.parse(url, depth);
        writer.savePagesFromQueue(pagesQueue);
    }
}
