package com.example.xavier.smartcampusdemo.Entity;

/**
 * Created by Xavier on 4/30/2017.
 *
 */

public class video_reply {
    private int v_id;
    private int vr_id;
    private String time;
    private String author;
    private String avatar;
    private int u_id;
    private String content;
    public void setV_id(int v_id){
        this.v_id=v_id;
    }
    public int getV_id(){
        return v_id;
    }
    public void setVr_id(int vr_id){
        this.vr_id=vr_id;
    }
    public int getVr_id(){
        return vr_id;
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
    public void setAvatar(String avatar){
        this.avatar=avatar;
    }
    public String getAvatar(){
        return avatar;
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
}
