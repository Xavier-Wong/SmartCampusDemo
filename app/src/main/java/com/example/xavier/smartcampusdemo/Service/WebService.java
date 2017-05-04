package com.example.xavier.smartcampusdemo.Service;

import com.example.xavier.smartcampusdemo.Entity.user;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Properties;

/**
 * Created by Xavier on 11/21/2016.
 * WebService
 */

public class WebService extends NetService{

    // 通过Get方式获取HTTP服务器数据
    public static String executeSignIn(String username, String password) {

        HttpURLConnection conn = null;
        String out;
        Properties properties = System.getProperties();
        properties.list(System.out);
        try {
            // 用户名 密码
            // URL 地址
            String path = "http://" + getIP() + "/HelloWeb/LogLet";

            conn = (HttpURLConnection) new URL(path).openConnection();
            conn.setReadTimeout(3000);
            conn.setRequestMethod("POST");
            conn.setDoOutput(true);
            conn.setDoInput(true);
            PrintWriter printWriter = new PrintWriter(conn.getOutputStream());
            String post = "username="+username+"&password="+password;
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
        } finally {
            // 意外退出时进行连接关闭保护
            if (conn != null) {
                conn.disconnect();
            }

        }
        return "noResponse";
    }

    public static String executeSignUp(user user) {

        HttpURLConnection conn = null;
        String out;

        try {
            // 用户名 密码
            // URL 地址
            String path = "http://" + getIP() + "/HelloWeb/RegLet";

            conn = (HttpURLConnection) new URL(path).openConnection();
            conn.setReadTimeout(3000);
            conn.setRequestMethod("POST");
            conn.setDoOutput(true);
            conn.setDoInput(true);
            PrintWriter printWriter = new PrintWriter(conn.getOutputStream());
            String post = "username=" + user.getUsername() + "&password=" + user.getPassword() + "&sid=" + String.valueOf(user.getS_Id()) + "&tel=" + user.getTel() + "&avatar=" + user.getAvatar() +"&sex=" + String.valueOf(user.getSex()) + "&email=" + user.getEmail();
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
        }catch (Exception e) {
            e.printStackTrace();
        } finally {
            // 意外退出时进行连接关闭保护
            if (conn != null) {
                conn.disconnect();
            }

        }
        return "noResponse";
    }
}
