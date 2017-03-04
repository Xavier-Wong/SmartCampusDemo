package com.example.xavier.smartcampusdemo.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import org.json.JSONArray;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import com.example.xavier.smartcampusdemo.Util.NetUtil.NetUtil;
/**
 * Created by Xavier on 11/12/2016.
 */

/**
 * 获取新闻服务类的父类，提供一些方法获取数据
 * @author linpeng123l
 *
 */
class NetService {

    static String getIP() {
        return "138.197.201.173:8080";
    }

    public final static  String UESRAGENT_PHONE = "Mozilla/5.0 (iPhone; CPU iPhone OS 6_0 like Mac OS X) AppleWebKit/536.26 (KHTML, like Gecko) Version/6.0 Mobile/10A405 Safari/8536.25";

    /**
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

    static byte[] read(InputStream inStream) throws Exception {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024];
        int len = 0;
        while ((len = inStream.read(buffer)) != -1) {
            outputStream.write(buffer, 0, len);
        }
        inStream.close();
        return outputStream.toByteArray();
    }
}
