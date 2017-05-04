package com.example.xavier.smartcampusdemo.Entity;

import android.graphics.Bitmap;

/**
 * Created by Xavier on 3/22/2017.
 *
 */

public class blog {
    private int b_id;
    private String time;
    private String author;
    private String content;
    private int like;
    private int u_id;
    private int dislike;
    private String avatar;
    private String img;
    public void setImg(String img){
        this.img=img;
    }
    public String getImg(){
        return img;
    }
    public void setAvatar(String avatar){
        this.avatar=avatar;
    }
    public String getAvatar(){
        return avatar;
    }
    public void setU_id(int u_id){
        this.u_id=u_id;
    }
    public int getU_id(){
        return u_id;
    }
    public void setB_id(int b_id){
        this.b_id=b_id;
    }
    public int getB_id(){
        return b_id;
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


