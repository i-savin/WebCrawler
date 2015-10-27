package ru.webcrawler.entity;

public class URL {
    public final static URL POISON_PILL_URL = new URL("", -1);

    // address
    private String link;
    // link level
    private int depth;

    public URL(String link, int depth) {
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
        if (!(o instanceof URL)) {
            return false;
        }

        URL url1 = (URL) o;

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
