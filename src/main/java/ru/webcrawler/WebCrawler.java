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
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
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

        BlockingQueue<Page> pagesQueue = new LinkedBlockingQueue<>();
        Parser parser = new Parser(pagesQueue);
        parser.parse(url, depth);
        System.out.println(pagesQueue.size());
    }



    private static void parse(String urlStr, int limit) throws IOException {
        Url url = new Url(urlStr, 1);
        Queue<Url> linksQueue = new LinkedList<Url>();
        linksQueue.add(url);
        List<String> visitedLinks = new ArrayList<String>();
        UrlValidator urlValidator = new UrlValidator(UrlValidator.ALLOW_2_SLASHES + UrlValidator.ALLOW_LOCAL_URLS);
        int number = 0;
        File file = new File("links.txt");
        if (!file.exists()) {
            file.createNewFile();
        }

//        try (BufferedWriter bw = new BufferedWriter(new FileWriter(file))) {

            while (linksQueue.size() > 0) {
                Url currentUrl = linksQueue.poll();
                if (visitedLinks.contains(currentUrl.getUrl())) {
                    continue;
                }

//            if (currentUrl.getDepth() > limit) {
//                continue;
//            }

                if (!urlValidator.isValid(currentUrl.getUrl())) {
//                    System.err.println("URL is incorrect: " + currentUrl.getUrl());
//                    bw.append("URL is incorrect: " + currentUrl.getUrl());
                    continue;
                }

//                System.out.println(currentUrl.getUrl());
//                bw.append(currentUrl.getUrl());
//                bw.newLine();

                try {
                    Document document = Jsoup.connect(currentUrl.getUrl()).timeout(10 * 1000).get();
                    Elements links = document.select("a[href]");
                    if (currentUrl.getDepth() < limit) {
                        for (Element link : links) {
                            String linkStr = link.attr("abs:href");
                            linksQueue.add(new Url(linkStr, currentUrl.getDepth() + 1));
                        }
                    }
                } catch (IOException e) {
//                    System.err.println("Error connecting to: " + currentUrl.getUrl());
//                    bw.append("Error connecting to: " + currentUrl.getUrl());
//                    bw.newLine();
                }
                number++;
                visitedLinks.add(currentUrl.getUrl());
                System.out.println(currentUrl.getUrl());
//                bw.flush();
//            }
        }
        System.out.println("Method finished. URLs number: " + number);
    }

    private static void parseConcurrently(String urlStr, final int limit, ExecutorService es) throws IOException {
        long startTime = System.currentTimeMillis();
        Url url = new Url(urlStr, 1);
        final Queue<Url> linksQueue = new ConcurrentLinkedQueue<Url>();
        linksQueue.add(url);
        final List<String> visitedLinks = new CopyOnWriteArrayList<>();
        final UrlValidator urlValidator = new UrlValidator(UrlValidator.ALLOW_2_SLASHES + UrlValidator.ALLOW_LOCAL_URLS);
        int number = 0;
        final File file = new File("links1.txt");
        if (!file.exists()) {
            file.createNewFile();
        }

        while (linksQueue.size() > 0) {
            final Url currentUrl = linksQueue.poll();
            es.execute(new Runnable() {
                @Override
                public void run() {
                    if (visitedLinks.contains(currentUrl.getUrl())) {
                        return;
                    }
                    if (!urlValidator.isValid(currentUrl.getUrl())) {
                        return;
                    }
                    try {
                        Document document = Jsoup.connect(currentUrl.getUrl()).timeout(10 * 1000).get();
                        Elements links = document.select("a[href]");
                        if (currentUrl.getDepth() < limit) {
                            for (Element link : links) {
                                String linkStr = link.attr("abs:href");
                                linksQueue.add(new Url(linkStr, currentUrl.getDepth() + 1));
                            }
                        }
                        try (BufferedWriter bw = new BufferedWriter(new FileWriter(file))) {
                            bw.write(document.body().text());
                            bw.flush();
                        }
                    } catch (IOException e) {
                        //                    System.err.println("Error connecting to: " + currentUrl.getUrl());
                        //                            bw.append("Error connecting to: " + currentUrl.getUrl());
                        //                            bw.newLine();
                    }
                    visitedLinks.add(currentUrl.getUrl());
                    System.out.println(currentUrl.getUrl());
                }
            });
            if (linksQueue.size() == 0) {
                try {
                    Thread.sleep(10000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            number++;
        }
        es.shutdown();
        System.out.println("Method finished. URLs number: " + number);
        System.out.println("Finished: " + (System.currentTimeMillis() - startTime));
    }

    private static void print(String msg, Object... args) {
        System.out.println(String.format(msg, args));
    }
}
