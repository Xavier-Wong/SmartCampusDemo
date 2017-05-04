package com.example.xavier.smartcampusdemo.Service;

import com.example.xavier.smartcampusdemo.Util.NetUtil.NetUtil;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
/**
 * Created by Xavier on 11/12/2016.
 *
 */

/**
 * 获取新闻服务类的父类，提供一些方法获取数据
 * @author linpeng123l
 *
 */
public class NetService {

    public static String getIP() {
        return "192.168.1.214:8080";
//        return "10.0.2.2:8080";
//        return "xavier-hzw.me:8080";
    }

    /*
     * 通过URL请求得到json数组
     * @param url
     * @return
     */
    public static JSONObject[] getJsonObjectsByUrl(String url){
        try {
            String response = NetUtil.postAndGetDate(url).replace("&quot;", "\'");
            JSONArray jsonArray = new JSONArray(response);
            JSONObject[] jsonObjects = new JSONObject[jsonArray.length()];
            for(int i = 0;i<jsonArray.length();i++){
                jsonObjects[i] = jsonArray.getJSONObject(i);
            }
            return jsonObjects;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private static byte[] read(InputStream inStream) throws Exception {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024];
        int len = 0;
        while ((len = inStream.read(buffer)) != -1) {
            outputStream.write(buffer, 0, len);
        }
        inStream.close();
        return outputStream.toByteArray();
    }

    // 将输入流转化为 String 型
    static String parseInfo(InputStream inStream) throws Exception {
        byte[] data = read(inStream);
        // 转化为字符串
        return new String(data, "UTF-8");
    }
}
