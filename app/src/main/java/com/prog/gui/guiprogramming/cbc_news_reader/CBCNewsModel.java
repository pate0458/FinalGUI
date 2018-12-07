package com.prog.gui.guiprogramming.cbc_news_reader;

import java.io.Serializable;

/**
 * <p>Data holder for each row for a list view
 * also useful to use whole row data as a single object</p>
 * */
class CBCNewsModel implements Serializable {
    private String title, link, pubDate, author, category, description;

    CBCNewsModel() {
    }

    String getTitle() {
        return title;
    }

    void setTitle(String title) {
        this.title = title;
    }

    String getLink() {
        return link;
    }

    void setLink(String link) {
        this.link = link;
    }

    String getPubDate() {
        return pubDate;
    }

    void setPubDate(String pubDate) {
        this.pubDate = pubDate;
    }

    String getAuthor() {
        return author;
    }

    void setAuthor(String author) {
        this.author = author;
    }

    String getCategory() {
        return category;
    }

    void setCategory(String category) {
        this.category = category;
    }

    String getDescription() {
        return description;
    }

    void setDescription(String description) {
        this.description = description;
    }
}
