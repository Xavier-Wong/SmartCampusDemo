package com.example.xavier.smartcampusdemo.Util.NetUtil;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

/**
 * Created by Xavier on 11/12/2016.
 */

public class NetUtil {
    private final static String UESRAGENT_PHONE = "User-Agent: Mozilla/5.0 (iPhone; CPU iPhone OS 6_0 like Mac OS X) AppleWebKit/536.26 (KHTML, like Gecko) Version/6.0 Mobile/10A405 Safari/8536.25";

    public static String postAndGetDate(String url){
        String response=null;
        System.out.println(url);
        try{
            HttpPost httpPost=new HttpPost(url);
            httpPost.setHeader("User-Agent", UESRAGENT_PHONE);
            DefaultHttpClient httpClient=new DefaultHttpClient();
            HttpResponse httpResponse=httpClient.execute(httpPost);
            if(httpResponse.getStatusLine().getStatusCode()==200){
                response=EntityUtils.toString(httpResponse.getEntity());
            }
        }catch (Exception e) {
            System.out.println("error ");
            response="connect_error";
            e.printStackTrace();
        }
        return response;
    }

}
