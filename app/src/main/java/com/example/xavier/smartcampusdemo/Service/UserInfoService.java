package com.example.xavier.smartcampusdemo.Service;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.SocketTimeoutException;
import java.net.URL;

import static com.example.xavier.smartcampusdemo.Service.NetService.getIP;
import static com.example.xavier.smartcampusdemo.Service.NetService.parseInfo;

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
            }
        } catch (SocketTimeoutException ste) {
            temp="sockettimeoutalert";
            ste.printStackTrace();
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

    public static String getUserById(String id) {
        String temp;
        temp = httpConnect(1, id, "");
        if (temp.equals(""))
            return "{}";
        return temp;
    }

    public static String getUserByName(String name) {
        String temp;
        temp = httpConnect(2, "", name);
        if (temp.equals(""))
            return "{}";
        return temp;
    }
}
