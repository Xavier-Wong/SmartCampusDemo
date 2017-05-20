package com.example.xavier.smartcampusdemo.entity;

/**
 * Created by Xavier on 4/29/2017.
 *
 */

public class video {
    private int v_id;
    private String time;
    private int u_id;
    private String author;
    private String avatar;
    private String content;
    private String title;
    private int like;
    private int dislike;
    private String video_str;
    private String thumbnail;private int reply_count;
    public void setReply_count(int reply_count){
        this.reply_count=reply_count;
    }
    public int getReply_count(){
        return reply_count;
    }
    public void setThumbnail(String thumbnail){
        this.thumbnail=thumbnail;
    }
    public String getThumbnail(){
        return thumbnail;
    }
    public void setVideo_str(String video_str){
        this.video_str=video_str;
    }
    public String getVideo_str(){
        return video_str;
    }
    public void setV_id(int v_id){
        this.v_id=v_id;
    }
    public int getV_id(){
        return v_id;
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
    public void setAvatar(String avatar){
        this.avatar=avatar;
    }
    public String getAvatar(){
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
