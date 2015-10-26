package ru.webcrawler.entity;

/**
 * Created by isavin on 27.08.2015.
 */
public class Page {

    public static Page POISON_PILL_PAGE = new Page("", "");

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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Page page = (Page) o;

        if (link == null) {
            if (page.link != null) {
                return false;
            }
        } else {
            if (!link.equals(page.link)) return false;
        }
        return (text == null ? page.text == null : text.equals(page.text));

    }

    @Override
    public int hashCode() {
        int result = link.hashCode();
        result = 31 * result + text.hashCode();
        return result;
    }

    @Override
    public String toString() {
        return "Page{" +
                "link='" + link + '\'' +
                ", text='" + text + '\'' +
                '}';
    }
}
