package com.example.xavier.smartcampusdemo.service;

import com.example.xavier.smartcampusdemo.entity.video;
import com.example.xavier.smartcampusdemo.entity.video_reply;

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

import static com.example.xavier.smartcampusdemo.util.JSONUtil.getJsonObject;
import static com.example.xavier.smartcampusdemo.util.JSONUtil.getJsonObjects;
import static com.example.xavier.smartcampusdemo.util.TimeConvertor.stampToDate;

/**
 * Created by Xavier on 4/29/2017.
 */

public class VideoItemService extends NetService {

    private static StringBuffer httpConnectGet(int action, String id, String page) {

        StringBuffer temp;
        InputStream is = null;
        HttpURLConnection conn = null;

        try {
            // URL 地址
            String path = "http://" + getIP() + "/HelloWeb/VideoLet?action=" + action + "&vid=" + id+ "&page=" + page;

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
            String path = "http://" + getIP() + "/HelloWeb/VideoPublishLet";

            conn = (HttpURLConnection) new URL(path).openConnection();
            conn.setReadTimeout(3000);
            conn.setRequestMethod("POST");
            conn.setDoOutput(true);
            conn.setDoInput(true);
            PrintWriter printWriter = new PrintWriter(conn.getOutputStream());

            if(object instanceof video) {
                video video = (video) object;
                post = "action=1&uid=" + video.getU_id() + "&content=" + video.getContent() + "&title=" + video.getTitle() + "&video=" + video.getVideo_str();
            }
            if(object instanceof video_reply) {
                video_reply video_reply = (video_reply) object;
                post = "action=2&uid=" + video_reply.getU_id() + "&content=" + video_reply.getContent() + "&vid=" + video_reply.getV_id();
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

    private static StringBuffer executeGetVideo(String page) {

        StringBuffer temp;
        temp = httpConnectGet(1, "", page);
        if (temp.toString().equals("invalid"))
            return new StringBuffer("[]");
        return temp;
    }

    private static StringBuffer executeGetVideoById(String id) {

        StringBuffer temp;
        temp = httpConnectGet(2, id, "");
        if (temp.toString().equals("invalid"))
            return new StringBuffer("{}");
        return temp;
    }

    private static StringBuffer executeGetVideoReplyByIdAndPage(String id, String page) {

        StringBuffer temp;
        temp = httpConnectGet(3, id, page);
        if (temp.toString().equals("invalid"))
            return new StringBuffer("[]");
        return temp;
    }

    public static String executePost(Object entity) {

        String temp;
        temp = httpConnectPost(entity);
        return temp;
    }

    public static List<video> getVideoItems(Integer page) {
        List<video> videoItems = new ArrayList<>();
        if(!executeGetVideo(String.valueOf(page)).toString().equals("noResponse")) {
            JSONObject[] jsonObjects = getJsonObjects(executeGetVideo(String.valueOf(page)).toString());
            try {
                if (jsonObjects != null && jsonObjects.length > 0) {
                    for (JSONObject jsonObject : jsonObjects) {
                        video videoItem = new video();
                        videoItem.setV_id(jsonObject.getInt("v_id"));
                        videoItem.setTitle(jsonObject.getString("title"));
                        videoItem.setU_id(jsonObject.getInt("u_id"));
                        videoItem.setTime(stampToDate(jsonObject.getString("time")));
                        videoItem.setAuthor(jsonObject.getString("author"));
                        videoItem.setAvatar(jsonObject.getString("avatar"));
                        videoItem.setThumbnail(jsonObject.getString("thumbnail"));
                        videoItem.setReply_count(jsonObject.getInt("reply_count"));
                        videoItems.add(videoItem);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        else {
            video videoItem = new video();
            videoItem.setU_id(0);
            videoItems.add(videoItem);
        }
        return videoItems;
    }


    public static video getVideoById(String id) {
        video videoItem = null;
        if(!executeGetVideoById(id).toString().equals("noResponse")) {
            JSONObject jsonObject = getJsonObject(executeGetVideoById(id).toString());
            try {
                if (jsonObject != null) {
                    videoItem = new video();
                    videoItem.setVideo_str(jsonObject.getString("video"));
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        else {
            videoItem = new video();
            videoItem.setU_id(0);
        }
        return videoItem;
    }

    public static video getVideoItem(String id) {
        video videoItem = null;
        if(!executeGetVideoById(id).toString().equals("noResponse")) {
            JSONObject jsonObject = getJsonObject(executeGetVideoById(id).toString());
            try {
                if (jsonObject != null) {
                    videoItem = new video();
                    videoItem.setTitle(jsonObject.getString("title"));
                    videoItem.setU_id(jsonObject.getInt("u_id"));
                    videoItem.setTime(stampToDate(jsonObject.getString("time")));
                    videoItem.setLike(jsonObject.getInt("like"));
                    videoItem.setDislike(jsonObject.getInt("dislike"));
                    videoItem.setContent(jsonObject.getString("content"));
                    videoItem.setAuthor(jsonObject.getString("author"));
                    videoItem.setAvatar(jsonObject.getString("avatar"));
                    videoItem.setVideo_str(jsonObject.getString("video"));
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        else {
            videoItem = new video();
            videoItem.setU_id(0);
        }
        return videoItem;
    }

    public static List<video_reply> getVideoReplyItems(Integer page, String id) {
        List<video_reply> videoReplyItems = new ArrayList<>();
        if(!executeGetVideoReplyByIdAndPage(id, String.valueOf(page)).toString().equals("noResponse")) {
            JSONObject[] jsonObjects = getJsonObjects(executeGetVideoReplyByIdAndPage(id, String.valueOf(page)).toString());
            try {
                if (jsonObjects != null && jsonObjects.length > 0) {
                    for (JSONObject jsonObject : jsonObjects) {
                        video_reply videoReplyItem = new video_reply();
                        videoReplyItem.setV_id(jsonObject.getInt("v_id"));
                        videoReplyItem.setVr_id(jsonObject.getInt("vr_id"));
                        videoReplyItem.setU_id(jsonObject.getInt("u_id"));
                        videoReplyItem.setTime(stampToDate(jsonObject.getString("time")));
                        videoReplyItem.setContent(jsonObject.getString("content"));
                        videoReplyItem.setAuthor(jsonObject.getString("author"));
                        videoReplyItem.setAvatar(jsonObject.getString("avatar"));
                        videoReplyItems.add(videoReplyItem);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        else {
            video_reply videoReplyItem = new video_reply();
            videoReplyItem.setU_id(0);
            videoReplyItems.add(videoReplyItem);
        }
        return videoReplyItems;
    }
}
