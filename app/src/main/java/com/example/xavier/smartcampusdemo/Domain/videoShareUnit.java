package com.example.xavier.smartcampusdemo.Domain;

/**
 * Created by Xavier on 11/27/2016.
 */

public class videoShareUnit {
    private String title;
    private String url;
    private String time;

    public videoShareUnit() {
        super();
    }

    public videoShareUnit(String title, String url, String time) {
        super();
        this.title = title;
        this.url = url;
        this.time = time;
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

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }
}
