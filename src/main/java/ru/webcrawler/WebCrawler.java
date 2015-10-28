package ru.webcrawler;

import org.jsoup.helper.Validate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import ru.webcrawler.domain.Transfer;
import ru.webcrawler.domain.Writer;
import ru.webcrawler.entity.Page;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * Created by isavin on 26.08.2015.
 */
public class WebCrawler {

    private final static Logger logger = LoggerFactory.getLogger(WebCrawler.class);

    static {
//        System.setProperty("http.proxyHost", "TMGHQ.office.finam.ru");
//        System.setProperty("http.proxyPort", "8080");
//        System.setProperty("https.proxyHost", "TMGHQ.office.finam.ru");
//        System.setProperty("https.proxyPort", "8080");
//        System.setProperty("https.proxyUser", "isavin");
//        System.setProperty("https.proxyPassword", "Xfqrjdcrbq777");
    }

    public static void main(String[] args) {
        long start = System.currentTimeMillis();
        Validate.isTrue(args.length == 2, "usage: WebCrawler url depth");
        String link = args[0];
        int depth = Integer.valueOf(args[1]);

        BlockingQueue<Page> pages = new LinkedBlockingQueue<>();

        AnnotationConfigApplicationContext ctx = new AnnotationConfigApplicationContext(WebCrawlerConfig.class);

        Transfer transfer = (Transfer) ctx.getBean("transfer", pages, link, depth);
        Writer writer = (Writer) ctx.getBean("writer", pages);

        Thread transferThread = new Thread(transfer);
        Thread writerThread = new Thread(writer);

        transferThread.start();
        writerThread.start();
        long finish = System.currentTimeMillis();
        logger.info("Crawling finished in [{}] ms", (finish - start));
    }
}
