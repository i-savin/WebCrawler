package ru.webcrawler;

/**
 * Created by isavin on 26.08.2015.
 */
public class Url {
    private String url;
    private int depth;

    public Url(String url, int depth) {
        this.url = url;
        this.depth = depth;
    }

    public String getUrl() {
        return url;
    }

    public int getDepth() {
        return depth;
    }
}
