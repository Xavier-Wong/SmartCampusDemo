package com.example.xavier.smartcampusdemo.service;

import com.example.xavier.smartcampusdemo.entity.blog;
import com.example.xavier.smartcampusdemo.entity.blog_reply;

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

import static com.example.xavier.smartcampusdemo.service.NetService.getIP;
import static com.example.xavier.smartcampusdemo.service.NetService.parseInfo;
import static com.example.xavier.smartcampusdemo.util.JSONUtil.getJsonObject;
import static com.example.xavier.smartcampusdemo.util.JSONUtil.getJsonObjects;
import static com.example.xavier.smartcampusdemo.util.TimeConvertor.stampToDate;

/**
 * Created by Xavier on 3/23/2017.
 *
 */

public class BlogItemService {

    private static StringBuffer httpConnect(int action, String id, String page) {

        StringBuffer temp;
        InputStream is = null;
        HttpURLConnection conn = null;

        try {
            // URL 地址
            String path = "http://" + getIP() + "/HelloWeb/BlogLet?action=" + action + "&bid=" + id + "&page=" + page;

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
            String path = "http://" + getIP() + "/HelloWeb/BlogPublishLet";

            conn = (HttpURLConnection) new URL(path).openConnection();
            conn.setReadTimeout(3000);
            conn.setRequestMethod("POST");
            conn.setDoOutput(true);
            conn.setDoInput(true);
            PrintWriter printWriter = new PrintWriter(conn.getOutputStream());

            if (object instanceof blog) {
                blog blog = (blog) object;
                post = "action=1&uid=" + blog.getU_id() + "&content=" + blog.getContent() + "&img=" + blog.getImg();
            }
            if (object instanceof blog_reply) {
                blog_reply blog_reply = (blog_reply) object;
                post = "action=2&uid=" + blog_reply.getU_id() + "&content=" + blog_reply.getContent() + "&bid=" + blog_reply.getB_id();
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

    private static StringBuffer executeGetBlogDetailsById(String id) {

        StringBuffer temp;
        temp = httpConnect(2, id, "");
        if (temp.toString().equals("invalid"))
            return new StringBuffer("{}");
        return temp;
    }

    private static StringBuffer executeGetBlogByPage(String page) {
        StringBuffer temp;
        temp = httpConnect(1, "", page);
        if (temp.toString().equals("invalid"))
            return new StringBuffer("[]");
        return temp;
    }

    private static StringBuffer executeGetBlogReplyByIdAndPage(String id, String page) {

        StringBuffer temp;
        temp = httpConnect(3, id, page);
        if (temp.toString().equals("invalid"))
            return new StringBuffer("[]");
        return temp;
    }

    public static String executePost(Object entity) {
        String temp;
        temp = httpConnectPost(entity);
        return temp;
    }

    public static List<blog> getBlogItems(Integer page) {
        List<blog> blogItems = new ArrayList<>();
        if(!executeGetBlogByPage(String.valueOf(page)).toString().equals("noResponse")) {
            JSONObject[] jsonObjects = getJsonObjects(executeGetBlogByPage(String.valueOf(page)).toString());
            try {
                if (jsonObjects != null && jsonObjects.length > 0) {
                    for (JSONObject jsonObject : jsonObjects) {
                        blog blogItem = new blog();
                        blogItem.setB_id(jsonObject.getInt("b_id"));
                        blogItem.setContent(jsonObject.getString("content"));
                        blogItem.setU_id(jsonObject.getInt("u_id"));
                        blogItem.setTime(stampToDate(jsonObject.getString("time")));
                        blogItem.setLike(jsonObject.getInt("like"));
                        blogItem.setDislike(jsonObject.getInt("dislike"));
                        blogItem.setAuthor(jsonObject.getString("author"));
                        blogItem.setAvatar(jsonObject.getString("avatar"));
                        blogItem.setImg(jsonObject.getString("img"));
                        blogItem.setReply_count(jsonObject.getInt("reply_count"));
                        blogItems.add(blogItem);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        else {
            blog blogItem = new blog();
            blogItem.setU_id(0);
            blogItems.add(blogItem);
        }
        return blogItems;
    }

    public static blog getBlogDetails(String id) {
        blog blogDetails = null;
        if(!executeGetBlogDetailsById(id).toString().equals("noResponse")) {
            JSONObject jsonObject = getJsonObject(executeGetBlogDetailsById(id).toString());
            try {
                if (jsonObject != null) {
                    blogDetails = new blog();
                    blogDetails.setB_id(jsonObject.getInt("b_id"));
                    blogDetails.setContent(jsonObject.getString("content"));
                    blogDetails.setU_id(jsonObject.getInt("u_id"));
                    blogDetails.setTime(stampToDate(jsonObject.getString("time")));
                    blogDetails.setImg(jsonObject.getString("img"));
                    blogDetails.setAuthor(jsonObject.getString("author"));
                    blogDetails.setAvatar(jsonObject.getString("avatar"));
                    blogDetails.setLike(jsonObject.getInt("like"));
                    blogDetails.setReply_count(jsonObject.getInt("reply_count"));
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        else {
            blogDetails = new blog();
            blogDetails.setU_id(0);
        }
        return blogDetails;
    }

    public static List<blog_reply> getBlogReplyItems(Integer page, String id) {
        List<blog_reply> blogReplyItems = new ArrayList<>();
        if(!executeGetBlogReplyByIdAndPage(id, String.valueOf(page)).toString().equals("noResponse")) {
            JSONObject[] jsonObjects = getJsonObjects(executeGetBlogReplyByIdAndPage(id, String.valueOf(page)).toString());
            try {
                if (jsonObjects != null && jsonObjects.length > 0) {
                    for (JSONObject jsonObject : jsonObjects) {
                        blog_reply blogReplyItem = new blog_reply();
                        blogReplyItem.setB_id(jsonObject.getInt("b_id"));
                        blogReplyItem.setBr_id(jsonObject.getInt("br_id"));
                        blogReplyItem.setU_id(jsonObject.getInt("u_id"));
                        blogReplyItem.setTime(stampToDate(jsonObject.getString("time")));
                        blogReplyItem.setContent(jsonObject.getString("content"));
                        blogReplyItem.setAuthor(jsonObject.getString("author"));
                        blogReplyItem.setAvatar(jsonObject.getString("avatar"));
                        blogReplyItems.add(blogReplyItem);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        else {
            blog_reply blogReplyItem = new blog_reply();
            blogReplyItem.setU_id(0);
            blogReplyItems.add(blogReplyItem);
        }
        return blogReplyItems;
    }

}
