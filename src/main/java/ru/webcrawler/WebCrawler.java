package ru.webcrawler;

import org.apache.commons.validator.routines.UrlValidator;
import org.jsoup.Jsoup;
import org.jsoup.helper.Validate;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import ru.webcrawler.entity.Page;
import ru.webcrawler.entity.Url;

import java.io.*;
import java.util.*;
import java.util.concurrent.*;

/**
 * Created by isavin on 26.08.2015.
 */
public class WebCrawler {

    static {
//        System.setProperty("http.proxyHost", "TMGHQ.office.finam.ru");
//        System.setProperty("http.proxyPort", "8080");
    }

    public static void main (String[] args) throws IOException {
        Validate.isTrue(args.length == 2, "usage: WebCrawler url depth");
        final String url = args[0];
        final int depth = Integer.valueOf(args[1]);

        final BlockingQueue<Page> pagesQueue = new LinkedBlockingQueue<>();
        new Thread(new Runnable() {

            @Override
            public void run() {
                Parser parser = new Parser(pagesQueue);
                parser.parse(url, depth);
                System.out.println(pagesQueue.size());
            }
        }).start();

        Writer writer = new Writer(pagesQueue);
        try {
            StringBuffer fileName = new StringBuffer(url.replace("https","").replace(":", "-").replace("/", ""));
            writer.writeToDB();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

//        new Thread(new Runnable() {
//            @Override
//            public void run() {
//
//                Writer writer = new Writer(pagesQueue);
//                try {
//                    StringBuffer fileName = new StringBuffer(url.replace(":", "-").replace("/",""));
//                    writer.writeToFile(fileName.toString());
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//            }
//        }).start();
    }
}
