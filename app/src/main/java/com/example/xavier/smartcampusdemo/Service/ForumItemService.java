package com.example.xavier.smartcampusdemo.Service;

import android.support.annotation.Nullable;

import com.example.xavier.smartcampusdemo.Entity.forum;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import static com.example.xavier.smartcampusdemo.Util.JSONUtil.getJsonObjects;

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

    public static List<forum> getForumItem(int page) {
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

    private static String stampToDate(String s){
        String res;
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.CHINA);
        long lt = Long.valueOf(s);
        Date date = new Date(lt);
        res = simpleDateFormat.format(date);
        return res;
    }

    // 将输入流转化为 String 型
    private static String parseInfo(InputStream inStream) throws Exception {
        byte[] data = read(inStream);
        // 转化为字符串
        return new String(data, "UTF-8");
    }


}
