package com.example.xavier.smartcampusdemo.domain;

/**
 * Created by Xavier on 11/12/2016.
 */

public class techForumUnit {
    private String title;
    private String url;
    private String source;
    private String date;

    public techForumUnit () {
        super();
    }

    public techForumUnit (String title, String url, String photoUrl, String source,
                String date) {
        super();
        this.title = title;
        this.url = url;
        this.source = source;
        this.date = date;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}
