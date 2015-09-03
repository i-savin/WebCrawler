package ru.webcrawler;

import org.apache.commons.cli.Option;
import org.apache.commons.validator.routines.UrlValidator;
import org.jsoup.Jsoup;
import org.jsoup.helper.Validate;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.webcrawler.entity.Page;
import ru.webcrawler.entity.Url;

import java.io.*;
import java.util.*;
import java.util.concurrent.*;

/**
 * Created by isavin on 26.08.2015.
 */
public class WebCrawler {

    private final static Logger logger = LoggerFactory.getLogger(WebCrawler.class);

    private Parser parser;
    private Writer writer;

    static {
//        System.setProperty("http.proxyHost", "TMGHQ.office.finam.ru");
//        System.setProperty("http.proxyPort", "8080");
    }

    public WebCrawler(Parser parser, Writer writer) {
        this.parser = parser;
        this.writer = writer;
    }

    public void crawl(String url, int depth) {
        BlockingQueue<Page> pagesQueue = parser.parse(url, depth);
        writer.savePagesFromQueue(pagesQueue);
    }
}
