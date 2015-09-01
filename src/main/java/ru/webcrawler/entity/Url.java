package ru.webcrawler.entity;

public class Url {
    // address
    private String link;
    // link level
    private int depth;

    public Url(String link, int depth) {
        this.link = link;
        this.depth = depth;
    }

    public String getUrl() {
        return link;
    }

    public int getDepth() {
        return depth;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Url)) {
            return false;
        }

        Url url1 = (Url) o;

        return link.equalsIgnoreCase(url1.link);

    }

    @Override
    public int hashCode() {
        return link.hashCode();
    }

    @Override
    public String toString() {
        return link;
    }
}
