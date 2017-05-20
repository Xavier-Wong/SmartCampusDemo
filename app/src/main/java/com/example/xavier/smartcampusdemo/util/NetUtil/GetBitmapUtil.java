package com.example.xavier.smartcampusdemo.util.NetUtil;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by Xavier on 11/12/2016.
 * GetBitMap
 */

public class GetBitmapUtil {

    public static Bitmap getBitmapByUrl(String url){
        Bitmap bitmap = null;
        try {
            URL url2=new URL(url);
            HttpURLConnection httpURLConnection=(HttpURLConnection) url2.openConnection();
            httpURLConnection.setReadTimeout(3000);
            int code=httpURLConnection.getResponseCode();
            if(code==200){
                bitmap= BitmapFactory.decodeStream(httpURLConnection.getInputStream());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return bitmap;
    }
}