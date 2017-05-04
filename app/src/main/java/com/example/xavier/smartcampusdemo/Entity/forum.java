package com.example.xavier.smartcampusdemo.Entity;

import android.graphics.Bitmap;

/**
 * forum 实体类
 * Created by Xavier on 2/16/2017.
 */


public class forum{
    private int f_id;
    private String time;
    private int u_id;
    private String author;
    private Bitmap avatar;
    private String content;
    private String title;
    private int type;
    private int like;
    private int dislike;
    private String img;
    private int reply_count;
    public void setReply_count(int reply_count){
        this.reply_count=reply_count;
    }
    public int getReply_count(){
        return reply_count;
    }
    public void setImg(String img) {
        this.img = img;
    }
    public String getImg() {
        return this.img;
    }
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
    public void setU_id(int u_id){
        this.u_id=u_id;
    }
    public int getU_id(){
        return u_id;
    }
    public void setAuthor(String author){
        this.author=author;
    }
    public String getAuthor(){
        return author;
    }
    public void setAvatar(Bitmap avatar){
        this.avatar=avatar;
    }
    public Bitmap getAvatar(){
        return avatar;
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
    public void setLike(int like){
        this.like=like;
    }
    public int getLike(){
        return like;
    }
    public void setDislike(int dislike){
        this.dislike=dislike;
    }
    public int getDislike(){
        return dislike;
    }
}

