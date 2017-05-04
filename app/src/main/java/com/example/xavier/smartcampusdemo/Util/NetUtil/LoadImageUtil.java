package com.example.xavier.smartcampusdemo.Util.NetUtil;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by Xavier on 4/4/2017.
 *
 */

public class LoadImageUtil {
    public static String sendGet(String path){
        String content=null;
        try{
            //设置访问的url
            URL url=new URL(path);
            //打开请求
            HttpURLConnection httpURLConnection=(HttpURLConnection) url.openConnection();
            //设置请求的信息
            httpURLConnection.setRequestMethod("GET");
            //设置请求是否超时
            httpURLConnection.setConnectTimeout(5000);
            //判断服务器是否响应成功
            if(httpURLConnection.getResponseCode()==200){
                //获取响应的输入流对象
                InputStream is=httpURLConnection.getInputStream();
                byte data[]=StreamTools.isTodata(is);
                //把转换成字符串
                content=new String(data);
                //内容编码方式
                if(content.contains("gb2312")){
                    content=new String(data,"gb2312");
                }
                return content;
            }
            //断开连接
            httpURLConnection.disconnect();
        }catch(Exception e){
            e.printStackTrace();
        }
        return "noResponse";
    }
    public static Bitmap sendGets(String path){
        Bitmap bitmap=null;
        try{
            //设置访问的url
            URL url=new URL(path);
            //打开请求
            HttpURLConnection httpURLConnection=(HttpURLConnection) url.openConnection();
            //设置请求的信息
            httpURLConnection.setRequestMethod("GET");
            //设置请求是否超时
            httpURLConnection.setConnectTimeout(5000);
            //判断服务器是否响应成功
            if(httpURLConnection.getResponseCode()==200){
                //获取响应的输入流对象
                InputStream is=httpURLConnection.getInputStream();
                //直接把is的流转换成Bitmap对象
                bitmap= BitmapFactory.decodeStream(is);

                return bitmap;
            }
            //断开连接
            httpURLConnection.disconnect();
        }catch(Exception e){
            e.printStackTrace();
        }
        return null;
    }
    private static class StreamTools {
        static byte[] isTodata(InputStream is) throws IOException {
            //字节输出流
            ByteArrayOutputStream bops=new ByteArrayOutputStream();
            //读取数据的缓冲区
            byte buffer[]=new byte[1024];
            //读取记录的长度
            int len=0;
            while((len=is.read(buffer))!=-1){
                bops.write(buffer, 0, len);
            }
            //把读取的内容转换成byte数组
            byte data[]=bops.toByteArray();
            return data;
        }
    }

}
