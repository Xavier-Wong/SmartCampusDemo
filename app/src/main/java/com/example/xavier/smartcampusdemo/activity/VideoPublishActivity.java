package com.example.xavier.smartcampusdemo.activity;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.xavier.smartcampusdemo.R;
import com.example.xavier.smartcampusdemo.entity.video;
import com.example.xavier.smartcampusdemo.fragment.videoShare.videoShare;
import com.example.xavier.smartcampusdemo.service.VideoItemService;
import com.example.xavier.smartcampusdemo.service.WebService;
import com.example.xavier.smartcampusdemo.util.ColorUtils;
import com.example.xavier.smartcampusdemo.util.NetUtil.UploadFileUtil;
import com.example.xavier.smartcampusdemo.util.UIUtils;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Properties;
import java.util.UUID;

import zhangphil.iosdialog.widget.ActionSheetDialog;

import static com.example.xavier.smartcampusdemo.service.NetService.getIP;


/**
 * Created by Xavier on 4/10/2017.
 *
 */

public class VideoPublishActivity extends BaseActivity implements View.OnClickListener{

    private String path;
    String videoFilePath, filePath, availableName;
    private String uid, content, title, video_str;
    ProgressBar videoProgress;

    SharedPreferences sharedPreferences;
    TextView publish_commit;
    TextView choose_upload;
    EditText publish_title;
    EditText publish_content;

    Activity activity;
    Toolbar toolbar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.videoshare_publish);
        activity = this;
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
        videoProgress = (ProgressBar) findViewById(R.id.video_upload_progress);
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
                uid = sharedPreferences.getString("userID","");
                title = publish_title.getText().toString();
                content = publish_content.getText().toString();
                video_str = availableName;
                new MyAsyncTaskPostVideoItem().execute();
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
                publish_commit.setTextColor(ContextCompat.getColor(getBaseContext(), R.color.colorPrimary));
                publish_commit.setClickable(true);
            }
            else {
                publish_commit.setTextColor(ContextCompat.getColor(getBaseContext(), R.color.gainsboro));
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
                    new MyAsyncTaskUploadVideo().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                    videoProgress.setProgress(0);
                    new MyAsyncTaskPostProgress().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                }
                break;
            case 103:
                if (resultCode == Activity.RESULT_OK) {
                    Uri originalUri = data.getData(); // 获得图片的uri

                    // 好像是android多媒体数据库的封装接口，具体的看Android文档
                    Cursor cursor = activity.getContentResolver().query(originalUri, null, null, null, null);
                    String path = null;
                    if (cursor != null) {
                        cursor.moveToFirst();
                        String document_id = cursor.getString(0);
                        document_id = document_id.substring(document_id.lastIndexOf(":") + 1);
                        cursor.close();
                        cursor = activity.getContentResolver().query(
                                android.provider.MediaStore.Video.Media.EXTERNAL_CONTENT_URI,
                                null, MediaStore.Video.Media._ID + " = ? ", new String[]{document_id}, null);
                        if (cursor != null) {
                            cursor.moveToFirst();
                            path = cursor.getString(cursor.getColumnIndex(MediaStore.Video.Media.DATA));
                            cursor.close();
                        }
                    }
                    // 按我个人理解 这个是获得用户选择的图片的索引值
//                    int column_index = cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DATA);
//                    // 将光标移至开头 ，这个很重要，不小心很容易引起越界
//                    cursor.moveToFirst();
//                    // 最后根据索引值获取图片路径
//                    String path = cursor.getString(column_index);
                    File file = new File(path);
                    String extension = file.getName().substring(file.getName().lastIndexOf("."));
                    String videoId = UUID.randomUUID().toString();
                    availableName = "video_"+videoId+extension;
                    filePath = path;
                    new MyAsyncTaskUploadVideo().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                    videoProgress.setProgress(0);
                    new MyAsyncTaskPostProgress().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

                }
                break;
        }
    }


    private void refresh() {
        videoShare.refresh();
        finish();
    }

    private class MyAsyncTaskUploadVideo extends AsyncTask<Integer, String, String> {
        @Override
        protected String doInBackground(Integer... params) {
            File file = new File(filePath); //这里的path就是那个地址的全局变量
            try {
                FileInputStream fis = new FileInputStream(file);
                int fileLen = fis.available();
                if(fileLen >= 100*1024*1024) {
                    return "视频文件超过100M,请重新选择";
                }
                Log.i("filelen", fileLen+"");
            } catch (IOException e) {
                e.printStackTrace();
            }
            return UploadFileUtil.uploadImageFile(file, 0, availableName.split("\\.")[0]);
        }

        @Override
        protected void onPostExecute(String result) {
            if(result.equals("上传成功！")) {
                Toast toast = Toast.makeText(VideoPublishActivity.this, result, Toast.LENGTH_SHORT);
                toast.setGravity(Gravity.BOTTOM, 0, 0);
                toast.show();
            }
            else {
                UIUtils.customCenterShortToast(VideoPublishActivity.this, result, 0, 0);
            }
        }

    }

    private class MyAsyncTaskPostVideoItem extends AsyncTask<Integer, String, String> {
        @Override
        protected String doInBackground(Integer... pages) {
            video video = new video();
            video.setU_id(Integer.parseInt(uid));
            video.setContent(content);
            video.setTitle(title);
            video.setVideo_str(video_str);
            return VideoItemService.executePost(video);
        }

        @Override
        protected void onPostExecute(String state) {
            if(state.equals("发布成功")) {
                refresh();
            }
        }
    }

    private class MyAsyncTaskPostProgress extends AsyncTask<Integer, Integer, Integer> {
        @Override
        protected Integer doInBackground(Integer... pages) {
            String progressstr;
            int progress = 0;
            while(progress<100) {
                progressstr = WebService.getFileUploadProgress();
                if(!progressstr.equals(""))
                    progress = Integer.parseInt(progressstr.substring(0, progressstr.indexOf("%")));
                publishProgress(progress);
            }
            return null;
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
            Log.i("progress", values[0]+"");
            videoProgress.setProgress(values[0]);
        }


    }
}
