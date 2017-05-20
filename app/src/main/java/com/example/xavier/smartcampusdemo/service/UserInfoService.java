package com.example.xavier.smartcampusdemo.service;

import com.example.xavier.smartcampusdemo.entity.user;

import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.SocketTimeoutException;
import java.net.URL;

import static com.example.xavier.smartcampusdemo.service.NetService.getIP;
import static com.example.xavier.smartcampusdemo.service.NetService.parseInfo;
import static com.example.xavier.smartcampusdemo.util.JSONUtil.getJsonObject;

/**
 * Created by Xavier on 4/5/2017.
 *
 */

public class UserInfoService {

    private static String httpConnect(int action, String id, String name) {

        String temp = "";
        InputStream is = null;
        HttpURLConnection conn = null;

        try {
            // URL 地址
            String path = "http://" + getIP() + "/HelloWeb/UserLet?action=" + action + "&uid=" + id + "&username=" + name;

            conn = (HttpURLConnection) new URL(path).openConnection();
            conn.setConnectTimeout(5000); // 设置超时时间
            conn.setReadTimeout(5000);
            conn.setDoInput(true);
            conn.setRequestMethod("GET"); // 设置获取信息方式
            conn.setRequestProperty("Charset", "UTF-8"); // 设置接收数据编码格式

            if (conn.getResponseCode() == 200) {
                is = conn.getInputStream();
                temp = parseInfo(is).replace("<br>", "");
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
        return "noResponse";
    }

    private static String executeGetUserById(String id) {
        String temp;
        temp = httpConnect(1, id, "");
        if (temp.equals("invalid"))
            return "{}";
        return temp;
    }

    public static String getUserByName(String name) {
        String temp;
        temp = httpConnect(2, "", name);
        if (temp.equals("invalid"))
            return "{}";
        return temp;
    }

    public static user getUserById(String id) {
        user user = null;
        if(!executeGetUserById(id).equals("noResponse")) {
            JSONObject jsonObject = getJsonObject(executeGetUserById(id));
            try {
                if (jsonObject != null) {
                    user = new user();
                    user.setUsername(jsonObject.getString("uname"));
                    user.setS_Id(jsonObject.getInt("s_id"));
                    user.setTel(jsonObject.getString("tel"));
                    user.setAvatar(jsonObject.getString("avatar"));
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        else {

        }
        return user;
    }
}