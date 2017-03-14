package com.example.xavier.smartcampusdemo.Service;

import android.support.annotation.Nullable;

import com.example.xavier.smartcampusdemo.Entity.forum;
import com.example.xavier.smartcampusdemo.Entity.forum_reply;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Properties;

import static com.example.xavier.smartcampusdemo.Util.JSONUtil.getJsonObjects;
import static com.example.xavier.smartcampusdemo.Util.TimeConvertor.stampToDate;

/**
 * Created by Xavier on 11/12/2016.
 *
 */

public class ForumItemService extends NetService{

    private static String httpConnect(int action, String id) {

        String temp = "";
        InputStream is = null;
        HttpURLConnection conn = null;

        try {
            // URL 地址
            String path = "http://" + getIP() + "/HelloWeb/ForumLet?action=" + action + "&fid=" + id;

            conn = (HttpURLConnection) new URL(path).openConnection();
            conn.setConnectTimeout(3000); // 设置超时时间
            conn.setReadTimeout(3000);
            conn.setDoInput(true);
            conn.setRequestMethod("GET"); // 设置获取信息方式
            conn.setRequestProperty("Charset", "UTF-8"); // 设置接收数据编码格式

            if (conn.getResponseCode() == 200) {
                is = conn.getInputStream();
                temp = parseInfo(is).replace("<br>","");
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
        return temp;
    }

    public static String getForumsDetails(String id) {

        String temp = "";
        temp = httpConnect(2, id);
        return temp;
    }

    public static String executeGetForum() {

        String temp = "";
        temp = httpConnect(1, "");
        return temp;
    }

    public static String executeGetForumReply(String id) {

        String temp = "";
        temp = httpConnect(3, id);
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

    public static List<forum_reply> getForumItemReply(Integer page, String id) {
        List<forum_reply> forumReplyItems = new ArrayList<>();
        if(!executeGetForumReply(id).equals("invalid")) {
            JSONObject[] jsonObjects = getJsonObjects(executeGetForumReply(id));
            try {
                if (jsonObjects != null && jsonObjects.length > 0) {
                    for (JSONObject jsonObject : jsonObjects) {
                        if (Integer.parseInt(jsonObject.getString("page")) == page) {
                            forum_reply forumReplyItem = new forum_reply();
                            forumReplyItem.setF_id(jsonObject.getInt("foid"));
                            forumReplyItem.setFr_id(jsonObject.getInt("foreid"));
                            forumReplyItem.setAuthor(jsonObject.getString("author"));
                            forumReplyItem.setTime(stampToDate(jsonObject.getString("time")));
                            forumReplyItem.setContent(jsonObject.getString("content"));
                            forumReplyItems.add(forumReplyItem);
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return forumReplyItems;
    }

    public static List<forum> getForumItem(Integer page) {
        JSONObject[] jsonObjects = getJsonObjects(executeGetForum());
        List<forum> forumItems = new ArrayList<>();
        try {
            if(jsonObjects!=null&&jsonObjects.length>0) {
                for(JSONObject jsonObject : jsonObjects) {
                    if(Integer.parseInt(jsonObject.getString("page"))==page) {
                        forum forumItem = new forum();
                        forumItem.setF_id(jsonObject.getInt("id"));
                        forumItem.setTitle(jsonObject.getString("title"));
                        forumItem.setAuthor(jsonObject.getString("author"));
                        forumItem.setTime(stampToDate(jsonObject.getString("time")));
                        forumItem.setType(jsonObject.getInt("type"));
                        forumItems.add(forumItem);
                    }
                }
            }
        }catch(Exception e) {
            e.printStackTrace();
        }
        return forumItems;
    }

    // 将输入流转化为 String 型
    private static String parseInfo(InputStream inStream) throws Exception {
        byte[] data = read(inStream);
        // 转化为字符串
        return new String(data, "UTF-8");
    }

}
