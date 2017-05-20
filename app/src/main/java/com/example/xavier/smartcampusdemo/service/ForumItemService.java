package com.example.xavier.smartcampusdemo.service;

import com.example.xavier.smartcampusdemo.entity.forum;
import com.example.xavier.smartcampusdemo.entity.forum_reply;

import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import static com.example.xavier.smartcampusdemo.util.JSONUtil.getJsonObject;
import static com.example.xavier.smartcampusdemo.util.JSONUtil.getJsonObjects;
import static com.example.xavier.smartcampusdemo.util.TimeConvertor.stampToDate;

/**
 * Created by Xavier on 11/12/2016.
 *
 */

public class ForumItemService extends NetService{

    private static StringBuffer httpConnectGet(int action, String id, String type, String page) {

        StringBuffer temp;
        InputStream is = null;
        HttpURLConnection conn = null;

        try {
            // URL 地址
            String path = "http://" + getIP() + "/HelloWeb/ForumLet?action=" + action + "&fid=" + id + "&type=" + type + "&page=" + page;

            conn = (HttpURLConnection) new URL(path).openConnection();
            conn.setConnectTimeout(5000); // 设置超时时间
            conn.setReadTimeout(5000);
            conn.setDoInput(true);
            conn.setRequestMethod("GET"); // 设置获取信息方式
            conn.setRequestProperty("Charset", "UTF-8"); // 设置接收数据编码格式

            if (conn.getResponseCode() == 200) {
                is = conn.getInputStream();
                temp = new StringBuffer(parseInfo(is).replace("<br>", ""));
                return temp;
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            // 意外退出时进行连接关闭保护
            if (conn != null) {
                conn.disconnect();
            }
            if (is != null) {
                try {
                    is.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

        }
        return new StringBuffer("noResponse");
    }

    private static String httpConnectPost(Object object) {

        String post = "";
        HttpURLConnection conn;
        String out;

        try {
            String path = "http://" + getIP() + "/HelloWeb/ForumPublishLet";

            conn = (HttpURLConnection) new URL(path).openConnection();
            conn.setReadTimeout(3000);
            conn.setRequestMethod("POST");
            conn.setDoOutput(true);
            conn.setDoInput(true);
            PrintWriter printWriter = new PrintWriter(conn.getOutputStream());

            if(object instanceof forum) {
                forum forum = (forum) object;
                post = "action=1&uid=" +forum.getU_id()+ "&content=" + forum.getContent() + "&title=" + forum.getTitle() + "&type=" + forum.getType() + "&img=" + forum.getImg();
            }
            if(object instanceof forum_reply) {
                forum_reply forum_reply = (forum_reply) object;
                post = "action=2&uid=" + forum_reply.getU_id() + "&content=" + forum_reply.getContent() + "&fid=" + forum_reply.getF_id();
            }
            printWriter.write(post);
            printWriter.flush();
            BufferedInputStream bis = new BufferedInputStream(conn.getInputStream());
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            int len;
            byte[] arr = new byte[1024];
            while ((len=bis.read(arr))!= -1) {
                bos.write(arr,0,len);
                bos.flush();
            }
            out = new String(bos.toByteArray(), "utf-8");
            bos.close();
            return out;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "noResponse";
    }

    private static StringBuffer executeGetForumById(String id) {

        StringBuffer temp;
        temp = httpConnectGet(2, id, "", "");
        if (temp.toString().equals("invalid"))
            return new StringBuffer("{}");
        return temp;
    }

    private static StringBuffer executeGetForumsByTypeAndPage(String type, String page) {

        StringBuffer temp;
        temp = httpConnectGet(1, "", type, page);
        if (temp.toString().equals("invalid"))
            return new StringBuffer("[]");
        return temp;
    }

    private static StringBuffer executeGetForumRepliesByIdAndPage(String id, String page) {

        StringBuffer temp;
        temp = httpConnectGet(3, id, "", page);
        if (temp.toString().equals("invalid"))
            return new StringBuffer("[]");
        return temp;
    }

    public static String executePost(Object entity) {

        String temp;
        temp = httpConnectPost(entity);
        return temp;
    }

    public static List<forum_reply> getForumReplyItems(Integer page, String id) {
        List<forum_reply> forumReplyItems = new ArrayList<>();
        if(!executeGetForumRepliesByIdAndPage(id, String.valueOf(page)).toString().equals("noResponse")) {
            JSONObject[] jsonObjects = getJsonObjects(executeGetForumRepliesByIdAndPage(id, String.valueOf(page)).toString());
            try {
                if (jsonObjects != null && jsonObjects.length > 0) {
                    for (JSONObject jsonObject : jsonObjects) {
                        forum_reply forumReplyItem = new forum_reply();
                        forumReplyItem.setF_id(jsonObject.getInt("f_id"));
                        forumReplyItem.setFr_id(jsonObject.getInt("fr_id"));
                        forumReplyItem.setU_id(jsonObject.getInt("u_id"));
                        forumReplyItem.setTime(stampToDate(jsonObject.getString("time")));
                        forumReplyItem.setContent(jsonObject.getString("content"));
                        forumReplyItem.setAuthor(jsonObject.getString("author"));
                        forumReplyItem.setAvatar(jsonObject.getString("avatar"));
                        forumReplyItems.add(forumReplyItem);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        else {
            forum_reply forumReplyItem = new forum_reply();
            forumReplyItem.setU_id(0);
            forumReplyItems.add(forumReplyItem);
        }
        return forumReplyItems;
    }

    public static List<forum> getForumItems(Integer page, Integer type) {
        List<forum> forumItems = new ArrayList<>();
        if(!executeGetForumsByTypeAndPage(String.valueOf(type), String.valueOf(page)).toString().equals("noResponse")) {
            JSONObject[] jsonObjects = getJsonObjects(executeGetForumsByTypeAndPage(String.valueOf(type), String.valueOf(page)).toString());
            try {
                if (jsonObjects != null && jsonObjects.length > 0) {
                    for (JSONObject jsonObject : jsonObjects) {
                        forum forumItem = new forum();
                        forumItem.setF_id(jsonObject.getInt("f_id"));
                        forumItem.setTitle(jsonObject.getString("title"));
                        forumItem.setU_id(jsonObject.getInt("u_id"));
                        forumItem.setTime(stampToDate(jsonObject.getString("time")));
                        forumItem.setAuthor(jsonObject.getString("author"));
                        forumItem.setType(jsonObject.getInt("type"));
                        forumItem.setImg(jsonObject.getString("img"));
                        forumItem.setReply_count(jsonObject.getInt("reply_count"));
                        forumItems.add(forumItem);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        else {
            forum forumItem = new forum();
            forumItem.setU_id(0);
            forumItems.add(forumItem);
        }
        return forumItems;
    }

    public static forum getForumItem(String id) {
        forum forumItem = null;
        if(!executeGetForumById(id).toString().equals("noResponse")) {
            JSONObject jsonObject = getJsonObject(executeGetForumById(id).toString());
            try {
                if (jsonObject != null) {
                    forumItem = new forum();
                    forumItem.setF_id(jsonObject.getInt("f_id"));
                    forumItem.setTitle(jsonObject.getString("title"));
                    forumItem.setU_id(jsonObject.getInt("u_id"));
                    forumItem.setContent(jsonObject.getString("content"));
                    forumItem.setTime(stampToDate(jsonObject.getString("time")));
                    forumItem.setAuthor(jsonObject.getString("author"));
                    forumItem.setType(jsonObject.getInt("type"));
                    forumItem.setLike(jsonObject.getInt("like"));
                    forumItem.setAvatar(jsonObject.getString("avatar"));
                    forumItem.setDislike(jsonObject.getInt("dislike"));
                    forumItem.setImg(jsonObject.getString("img"));
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        else {
            forumItem = new forum();
            forumItem.setU_id(0);
        }
        return forumItem;
    }

}
