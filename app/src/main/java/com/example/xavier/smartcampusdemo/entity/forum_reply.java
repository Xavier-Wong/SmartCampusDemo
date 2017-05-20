package com.example.xavier.smartcampusdemo.entity;

/**
 * forum_reply 实体类
 * Created by Xavier on 2/16/2017.
 */


public class forum_reply{
    private int f_id;
    private int fr_id;
    private String time;
    private String author;
    private int u_id;
    private String content;
    private String avatar;
    public void setF_id(int f_id){
        this.f_id=f_id;
    }
    public int getF_id(){
        return f_id;
    }
    public void setFr_id(int fr_id){
        this.fr_id=fr_id;
    }
    public int getFr_id(){
        return fr_id;
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
