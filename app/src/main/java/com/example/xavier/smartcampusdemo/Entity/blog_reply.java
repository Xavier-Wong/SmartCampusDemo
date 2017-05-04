package com.example.xavier.smartcampusdemo.Entity;

/**
 * Created by Xavier on 5/1/2017.
 *
 */

public class blog_reply {
    private int b_id;
    private int br_id;
    private String time;
    private String author;
    private int u_id;
    private String content;
    private String avatar;
    public void setB_id(int b_id){
        this.b_id=b_id;
    }
    public int getB_id(){
        return b_id;
    }
    public void setBr_id(int br_id){
        this.br_id=br_id;
    }
    public int getBr_id(){
        return br_id;
    }
    public void setTime(String time){
        this.time=time;
    }
    public String getTime(){
        return time;
    }
    public void setU_id(int u_id){
        this.u_id = u_id;
    }
    public void setAuthor(String author){
        this.author=author;
    }
    public String getAuthor(){
        return author;
    }
    public int getU_id(){
        return u_id;
    }
    public void setContent(String content){
        this.content=content;
    }
    public String getContent(){
        return content;
    }
    public void setAvatar(String avatar){
        this.avatar=avatar;
    }
    public String getAvatar(){
        return avatar;
    }
}
