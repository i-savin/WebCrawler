package ru.webcrawler;

import org.jsoup.helper.Validate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import ru.webcrawler.entity.Page;

import java.io.IOException;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * Created by isavin on 03.09.2015.
 */
public class WebCrawlerSpring {
    private final static Logger logger = LoggerFactory.getLogger(WebCrawler.class);

    static {
//        System.setProperty("http.proxyHost", "TMGHQ.office.finam.ru");
//        System.setProperty("http.proxyPort", "8080");
    }

    public static void main (String[] args) throws IOException {
        Validate.isTrue(args.length == 2, "usage: WebCrawler url depth");
        final String url = args[0];
        final int depth = Integer.valueOf(args[1]);

        final ApplicationContext ctx =
                new ClassPathXmlApplicationContext("context.xml");

        WebCrawler webCrawler = (WebCrawler)ctx.getBean("crawler");
        webCrawler.crawl(url, depth);
        logger.info("Link {} has been processed for depth {}", url, depth);
    }
}
