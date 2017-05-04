package com.example.xavier.smartcampusdemo.Activity;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.xavier.smartcampusdemo.R;
import com.example.xavier.smartcampusdemo.Util.NetUtil.UploadFileUtil;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Properties;
import java.util.UUID;

import zhangphil.iosdialog.widget.ActionSheetDialog;

import static com.example.xavier.smartcampusdemo.Service.NetService.getIP;


/**
 * Created by Xavier on 4/10/2017.
 *
 */

public class VideoPublishActivity extends BaseActivity implements View.OnClickListener{

    private String path;
    String videoFilePath, filePath, availableName;
    String p_uid, p_content, p_video, p_title;

    SharedPreferences sharedPreferences;
    TextView publish_commit;
    TextView choose_upload;
    EditText publish_title;
    EditText publish_content;

    Toolbar toolbar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.videoshare_publish);
        toolbar = (Toolbar) findViewById(R.id.video_publish_toolbar);
        assert toolbar != null;
        toolbar.setTitle("");
        if (toolbar != null) {
            setSupportActionBar(toolbar);
        }
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        final int sdk = Build.VERSION.SDK_INT;
        Window window = getWindow();
        WindowManager.LayoutParams params = window.getAttributes();
        if (sdk >= Build.VERSION_CODES.KITKAT) {
            int bits = 0;    // 设置透明状态栏
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.KITKAT) {
                bits = WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS;
            }
            if ((params.flags & bits) == 0) {
                params.flags |= bits;
                window.setAttributes(params);
            }
        }

        publish_title = (EditText) findViewById(R.id.video_publish_title);
        publish_content = (EditText) findViewById(R.id.video_publish_content);
        publish_commit = (TextView) findViewById(R.id.video_publish_commit);
        choose_upload = (TextView) findViewById(R.id.video_publish_upload);
        choose_upload.setOnClickListener(this);

        textChange tc = new textChange();
        publish_title.addTextChangedListener(tc);
        publish_content.addTextChangedListener(tc);

        publish_commit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sharedPreferences = getSharedPreferences("USER_INFO", Context.MODE_PRIVATE);
                p_uid = sharedPreferences.getString("userID","");
                p_title = publish_title.getText().toString();
                p_content = publish_content.getText().toString();
                p_video = availableName;
                new Thread(new MyThread()).start();
                refresh();
            }
        });
    }

    private class textChange implements TextWatcher {

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }

        @TargetApi(Build.VERSION_CODES.M)
        @Override
        public void afterTextChanged(Editable s) {
            boolean pt = publish_title.getText().length() > 0;
            boolean pc = publish_content.getText().length() > 0;
            if(pt&&pc) {
                publish_commit.setTextColor(getColor(R.color.colorPrimary));
                publish_commit.setClickable(true);
            }
            else {
                publish_commit.setTextColor(getColor(R.color.gainsboro));
                publish_commit.setClickable(false);
            }
        }
    }
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.video_publish_upload:
                choose_video();
        }
    }

    public void choose_video() {
        new ActionSheetDialog(VideoPublishActivity.this).builder().setTitle("上传视频")
                .setCancelable(false).setCanceledOnTouchOutside(true)
                .addSheetItem("录制上传", ActionSheetDialog.SheetItemColor.Blue, new ActionSheetDialog.OnSheetItemClickListener() {

                    @Override
                    public void onClick(int which) {
                        videoFilePath = Environment.getExternalStorageDirectory().getAbsolutePath()+"/temp";
                        File temp = new File(videoFilePath);
                        if(!temp.exists())
                            temp.mkdirs();
                        videoFilePath = videoFilePath + "/tempVideo.mp4";
                        temp = new File(videoFilePath);
                        if(temp.exists())
                            temp.delete();
                        Uri videoFileUri = Uri.fromFile(temp);//获取文件的Uri
                        Intent intent1 = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);//跳转到相机Activity
                        intent1.putExtra(android.provider.MediaStore.EXTRA_OUTPUT, videoFileUri);//告诉相机拍摄完毕输出图片到指定的Uri
                        startActivityForResult(intent1, 102);
                    }
                }).addSheetItem("本地上传", ActionSheetDialog.SheetItemColor.Blue, new ActionSheetDialog.OnSheetItemClickListener() {
            @Override
            public void onClick(int which) {
                // 相册选取
                Intent intent2 = new Intent(Intent.ACTION_GET_CONTENT);
                intent2.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "video/*");
                startActivityForResult(intent2, 103);
            }
        }).show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case 102:
                if (resultCode == Activity.RESULT_OK) {
                    File file = new File(videoFilePath);
                    String extension = file.getName().substring(file.getName().lastIndexOf("."));
                    String videoId = UUID.randomUUID().toString();
                    availableName =  "video_" + videoId + extension;
                    filePath = videoFilePath;
                    new Thread(new UploadThread()).start();
                }
                break;
            case 103:
                if (resultCode == Activity.RESULT_OK) {
                    Uri originalUri = data.getData(); // 获得图片的uri
                    // 这里开始的第二部分，获取图片的路径：

                    String[] proj = {MediaStore.Video.Media.DATA};

                    // 好像是android多媒体数据库的封装接口，具体的看Android文档
                    @SuppressWarnings("deprecation")
                    Cursor cursor = managedQuery(originalUri, proj, null, null, null);
                    // 按我个人理解 这个是获得用户选择的图片的索引值
                    int column_index = cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DATA);
                    // 将光标移至开头 ，这个很重要，不小心很容易引起越界
                    cursor.moveToFirst();
                    // 最后根据索引值获取图片路径
                    String path = cursor.getString(column_index);
                    File file = new File(path);
                    String extension = file.getName().substring(file.getName().lastIndexOf("."));
                    String videoId = UUID.randomUUID().toString();
                    availableName =  "video_"+videoId+extension;
                    filePath = path;
                    new Thread(new UploadThread()).start();

                }
                break;
        }
    }

    private class UploadThread implements Runnable {

        @Override
        public void run() {
            if(filePath != null) {
                File file = new File(filePath); //这里的path就是那个地址的全局变量
                String result = UploadFileUtil.uploadImageFile(file, 0, availableName.split("\\.")[0]);
                Toast toast = Toast.makeText(VideoPublishActivity.this,result,Toast.LENGTH_SHORT);
                toast.setGravity(Gravity.BOTTOM,0,0);
                toast.show();
            }
        }
    }

    private class MyThread implements Runnable {

        @Override
        public void run() {
            Properties properties = System.getProperties();
            properties.list(System.out);
            String url = "http://" + getIP() + "/HelloWeb/VideoPublishLet";
            try {
                HttpURLConnection conn = (HttpURLConnection) new URL(url).openConnection();
                conn.setReadTimeout(3000);
                conn.setRequestMethod("POST");

                conn.setDoOutput(true);
                conn.setDoInput(true);
                PrintWriter printWriter = new PrintWriter(conn.getOutputStream());
                String post = "action=1&uid="+p_uid+"&title="+p_title+"&content="+p_content+"&video="+p_video;
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
                bos.close();

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void refresh() {
        finish();
//        Intent intent = new Intent(BlogPublishActivity.this, MainActivity.class);
//        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//        intent.putExtra("isPublished",true);
//        startActivity(intent);
    }
}
