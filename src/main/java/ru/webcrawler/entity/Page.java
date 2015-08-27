package ru.webcrawler.entity;

/**
 * Created by isavin on 27.08.2015.
 */
public class Page {

    private String link;
    private String text;

    public Page(String link, String text) {
        this.link = link.toLowerCase();
        this.text = text;
    }

    public String getLink() {
        return link;
    }

    public String getText() {
        return text;
    }
}
