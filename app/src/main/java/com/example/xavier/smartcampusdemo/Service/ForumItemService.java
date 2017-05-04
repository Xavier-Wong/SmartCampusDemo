package com.example.xavier.smartcampusdemo.Service;

import com.example.xavier.smartcampusdemo.Entity.forum;
import com.example.xavier.smartcampusdemo.Entity.forum_reply;

import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import static com.example.xavier.smartcampusdemo.Util.JSONUtil.getJsonObjects;
import static com.example.xavier.smartcampusdemo.Util.TimeConvertor.stampToDate;

/**
 * Created by Xavier on 11/12/2016.
 *
 */

public class ForumItemService extends NetService{

    private static StringBuffer httpConnect(int action, String id, String type, String page) {

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

    public static StringBuffer getForumsDetails(String id) {

        StringBuffer temp;
        temp = httpConnect(2, id, "", "");
        if (temp.toString().equals("invalid"))
            return new StringBuffer("{}");
        return temp;
    }

    private static StringBuffer executeGetForumByTypeAndPage(String type, String page) {

        StringBuffer temp;
        temp = httpConnect(1, "", type, page);
        if (temp.toString().equals("invalid"))
            return new StringBuffer("[]");
        return temp;
    }

    private static StringBuffer executeGetForumReplyByIdAndPage(String id, String page) {

        StringBuffer temp;
        temp = httpConnect(3, id, "", page);
        if (temp.toString().equals("invalid"))
            return new StringBuffer("[]");
        return temp;
    }

//    public static String publishForum() {
//        Properties properties = System.getProperties();
//        properties.list(System.out);
//        String url = "http://" + getIP() + "/HelloWeb/ForumPublishLet";
//        try {
//            URL httpUrl = new URL(url);
//            HttpURLConnection conn = (HttpURLConnection) httpUrl.openConnection();
//            conn.setReadTimeout(3000);
//            conn.setRequestMethod("POST");
//
//            OutputStream out = conn.getOutputStream();
//            String request = "author="+author+"&content="+content+"&title="+title+"&type="+type;
//            out.write(request.getBytes());
//            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
//            String str;
//            StringBuffer sb = new StringBuffer();
//            while((str = bufferedReader.readLine())!= null){
//                sb.append(str);
//            }
//        } catch (MalformedURLException e) {
//            e.printStackTrace();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }

    public static List<forum_reply> getForumReplyItems(Integer page, String id) {
        List<forum_reply> forumReplyItems = new ArrayList<>();
        if(!executeGetForumReplyByIdAndPage(id, String.valueOf(page)).toString().equals("noResponse")) {
            JSONObject[] jsonObjects = getJsonObjects(executeGetForumReplyByIdAndPage(id, String.valueOf(page)).toString());
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
        if(!executeGetForumByTypeAndPage(String.valueOf(type), String.valueOf(page)).toString().equals("noResponse")) {
            JSONObject[] jsonObjects = getJsonObjects(executeGetForumByTypeAndPage(String.valueOf(type), String.valueOf(page)).toString());
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

}
