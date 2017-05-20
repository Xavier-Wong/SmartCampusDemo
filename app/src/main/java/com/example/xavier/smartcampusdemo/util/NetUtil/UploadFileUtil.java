package com.example.xavier.smartcampusdemo.util.NetUtil;

import android.os.Environment;
import android.util.Log;

import com.example.xavier.smartcampusdemo.service.NetService;
import com.example.xavier.smartcampusdemo.util.TransImage;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.UUID;

import static com.example.xavier.smartcampusdemo.service.NetService.getIP;

/**
 *
 * 上传工具类
 * @author spring sky
 * Email:vipa1888@163.com
 * QQ:840950105
 * MyName:石明政
 */
public class UploadFileUtil {
    private static final int TIME_OUT = 10*1000;   //超时时间
    private static final String CHARSET = "utf-8"; //设置编码
    /**
     * android上传文件到服务器
     * @param oriFile  需要上传的文件
     * @param secondName  重新命名
     * @return  返回响应的内容
     */
    public static String uploadImageFile(File oriFile, Integer type, String secondName){
        String RequestURL = "http://" + getIP() + "/HelloWeb/UploadFileLet";
        String toFile = Environment.getExternalStorageDirectory().getAbsolutePath()+"/tempp/"+oriFile.getName();
        String toJpgFile = Environment.getExternalStorageDirectory().getAbsolutePath()+"/tempp/"+oriFile.getName().split("\\.")[0]+".jpg";
        File file;
        if(oriFile.getName().contains("jpg"))
            file = TransImage.compressImage(oriFile.getPath(),toFile,20);
        else if(oriFile.getName().contains("gif") && type == 2)
            file = TransImage.compressImage(oriFile.getPath(),toJpgFile,20);
        else
            file = oriFile;
        String result = null;
        String BOUNDARY =  UUID.randomUUID().toString();  //边界标识   随机生成
        String PREFIX = "--" , LINE_END = "\r\n";
        String CONTENT_TYPE = "multipart/form-data";   //内容类型
        try {
            URL url = new URL(RequestURL);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setReadTimeout(TIME_OUT);
            conn.setConnectTimeout(TIME_OUT);
            conn.setDoInput(true);  //允许输入流
            conn.setDoOutput(true); //允许输出流
            conn.setUseCaches(true);  //不允许使用缓存
            conn.setRequestMethod("POST");  //请求方式
            conn.setRequestProperty("Charset", CHARSET);  //设置编码
            conn.setRequestProperty("connection", "keep-alive");
            conn.setRequestProperty("cookie", NetService.sessionid);
            conn.setRequestProperty("Content-Type", CONTENT_TYPE + ";boundary=" + BOUNDARY);
            conn.connect();

            if(file!=null){
                /*
                  当文件不为空，把文件包装并且上传
                 */

                OutputStream outputSteam = conn.getOutputStream();
                DataOutputStream dos = new DataOutputStream(outputSteam);

                String[] params = {"\"docType\""};
                String[] values = {secondName};
                for(int i=0;i<params.length;i++){
                    //添加分割边界
                    StringBuilder sb = new StringBuilder();
                    sb.append(PREFIX);
                    sb.append(BOUNDARY);
                    sb.append(LINE_END);

                    sb.append("Content-Disposition: form-data; name=" + params[i] + LINE_END);
                    sb.append(LINE_END);
                    sb.append(values[i]);
                    sb.append(LINE_END);
                    dos.write(sb.toString().getBytes());
                }

                StringBuilder sb = new StringBuilder();
                sb.append(PREFIX);
                sb.append(BOUNDARY);
                sb.append(LINE_END);
                /*
                  这里重点注意：
                  name里面的值为服务器端需要key   只有这个key 才可以得到对应的文件
                  filename是文件的名字，包含后缀名的   比如:abc.png
                 */

                sb.append("Content-Disposition: form-data; name=\"data\";filename=" + "\"" + file.getName() + "\"" + LINE_END);
                sb.append("Content-Type: application/octet-stream; charset=" + CHARSET).append(LINE_END);
                sb.append(LINE_END);
                dos.write(sb.toString().getBytes());
                InputStream is = new FileInputStream(file);
                byte[] bytes = new byte[1024];
                int len;
                while((len=is.read(bytes))!=-1){
                    dos.write(bytes, 0, len);
                }
                is.close();
                dos.write(LINE_END.getBytes());
                byte[] end_data = (PREFIX+BOUNDARY+PREFIX+LINE_END).getBytes();
                dos.write(end_data);
                dos.flush();
                /*
                  获取响应码  200=成功
                  当响应成功，获取响应的流
                 */
                int res = conn.getResponseCode();
                if(res==200){
                    BufferedInputStream bis = new BufferedInputStream(conn.getInputStream());
                    ByteArrayOutputStream bos = new ByteArrayOutputStream();
                    byte[] arr = new byte[1024];
                    while ((len=bis.read(arr))!= -1) {
                        bos.write(arr,0,len);
                        bos.flush();
                    }
                    String out = new String(bos.toByteArray(), "utf-8");
                    bos.close();
                    return out;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "noResponse";
    }
}