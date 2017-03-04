package com.example.xavier.smartcampusdemo.Entity;

import java.util.Date;
/**
 * forum 实体类
 * Created by Xavier on 2/16/2017.
 */


public class forum{
    private int f_id;
    private String time;
    private String author;
    private String content;
    private String title;
    private int type;
    public void setF_id(int f_id){
        this.f_id=f_id;
    }
    public int getF_id(){
        return f_id;
    }
    public void setTime(String time){
        this.time=time;
    }
    public String getTime(){
        return time;
    }
    public void setAuthor(String author){
        this.author=author;
    }
    public String getAuthor(){
        return author;
    }
    public void setContent(String content){
        this.content=content;
    }
    public String getContent(){
        return content;
    }
    public void setTitle(String title){
        this.title=title;
    }
    public String getTitle(){
        return title;
    }
    public void setType(int type){
        this.type=type;
    }
    public int getType(){
        return type;
    }
}

