package com.example.xavier.smartcampusdemo.activity;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.daimajia.numberprogressbar.NumberProgressBar;
import com.example.xavier.smartcampusdemo.R;
import com.example.xavier.smartcampusdemo.adapter.SpinnerArrayAdapter;
import com.example.xavier.smartcampusdemo.entity.forum;
import com.example.xavier.smartcampusdemo.fragment.techForum;
import com.example.xavier.smartcampusdemo.service.ForumItemService;
import com.example.xavier.smartcampusdemo.service.WebService;
import com.example.xavier.smartcampusdemo.util.AutoGenerate;
import com.example.xavier.smartcampusdemo.util.NetUtil.UploadFileUtil;

import java.io.File;
import java.io.IOException;

import zhangphil.iosdialog.widget.ActionSheetDialog;

/**
 * Created by Xavier on 4/1/2017.
 *
 */

public class ForumPublishActivity extends BaseActivity implements View.OnClickListener {

    SharedPreferences sharedPreferences;
    /*提交*/
    TextView publish_commit;
    /*文本输入*/
    EditText publish_title;
    EditText publish_content;
    String imageFilePath, //拍照储存路径
            filePath; //上传照片本地路径
    String autoGenName; //自动生成名字
    String img0, img1, img2; //每张图片文件名
    ImageView iv_photo, iv_photo1, iv_photo2;
    View progress, progress1, progress2;
    NumberProgressBar progressBar, progressBar1, progressBar2;
    Activity activity;
    Toolbar toolbar;
    private String uid, content, title, type, img;
    /*图片上传*/
    private int index; //选中第几框
    /*类型选择*/
    private SpinnerArrayAdapter mAdapter;
    private Spinner mSpinner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.techforum_publish);

        activity = this;

        sharedPreferences = getSharedPreferences("USER_INFO", Context.MODE_PRIVATE);
        uid = sharedPreferences.getString("userID", "");

        toolbar = (Toolbar) findViewById(R.id.forum_publish_toolbar);
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

        publish_title = (EditText) findViewById(R.id.forum_publish_title);
        publish_content = (EditText) findViewById(R.id.forum_publish_content);
        publish_commit = (TextView) findViewById(R.id.forum_publish_commit);

        iv_photo = (ImageView) findViewById(R.id.forum_publish_photo_thumbnail);
        iv_photo1 = (ImageView) findViewById(R.id.forum_publish_photo_thumbnail1);
        iv_photo2 = (ImageView) findViewById(R.id.forum_publish_photo_thumbnail2);

        progress = findViewById(R.id.forum_pic_progress);
        progress1 = findViewById(R.id.forum_pic_progress1);
        progress2 = findViewById(R.id.forum_pic_progress2);

        progressBar = (NumberProgressBar) findViewById(R.id.forum_pic_progress_bar);
        progressBar1 = (NumberProgressBar) findViewById(R.id.forum_pic_progress_bar1);
        progressBar2 = (NumberProgressBar) findViewById(R.id.forum_pic_progress_bar2);

        mSpinner = (Spinner) findViewById(R.id.spinner);
        String[] mStringArray = getResources().getStringArray(R.array.forumTypes);
        mAdapter = new SpinnerArrayAdapter(this, mStringArray);
        mSpinner.setAdapter(mAdapter);

        textChange tc = new textChange();
        publish_title.addTextChangedListener(tc);
        publish_content.addTextChangedListener(tc);

        iv_photo.setOnClickListener(this);
        iv_photo1.setOnClickListener(this);
        iv_photo2.setOnClickListener(this);
        publish_commit.setOnClickListener(this);

        iv_photo1.setClickable(false);
        iv_photo2.setClickable(false);
        publish_commit.setClickable(false);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.forum_publish_photo_thumbnail:
                index = 0;
                choose_photo();
                break;
            case R.id.forum_publish_photo_thumbnail1:
                index = 1;
                choose_photo();
                break;
            case R.id.forum_publish_photo_thumbnail2:
                index = 2;
                choose_photo();
                break;
            case R.id.forum_publish_commit:
                content = publish_content.getText().toString();
                title = publish_title.getText().toString();
                type = String.valueOf(mAdapter.getPosition(mSpinner.getSelectedItem().toString())+1);
                img = "";
                if(img0 != null)
                    img = img + img0 +";";
                if(img1 != null)
                    img = img + img1 +";";
                if(img2 != null)
                    img = img + img2 +";";
                new MyAsyncTaskPostForumItem().execute();
                break;

        }
    }

    public void choose_photo() {
        new ActionSheetDialog(ForumPublishActivity.this).builder().setTitle("上传图片")
                .setCancelable(false).setCanceledOnTouchOutside(true)
                .addSheetItem("拍照上传", ActionSheetDialog.SheetItemColor.Blue, new ActionSheetDialog.OnSheetItemClickListener() {

                    @Override
                    public void onClick(int which) {
                        imageFilePath = Environment.getExternalStorageDirectory().getAbsolutePath()+"/temp";
                        File temp = new File(imageFilePath);
                        if(!temp.exists())
                            temp.mkdirs();
                        imageFilePath = imageFilePath + "/tempForum.jpg";
                        temp = new File(imageFilePath);
                        if(temp.exists())
                            temp.delete();
                        Uri imageFileUri = Uri.fromFile(temp);//获取文件的Uri
                        Intent intent1 = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);//跳转到相机Activity
                        intent1.putExtra(android.provider.MediaStore.EXTRA_OUTPUT, imageFileUri);//告诉相机拍摄完毕输出图片到指定的Uri
                        startActivityForResult(intent1, 102);
                    }
                }).addSheetItem("相册选择", ActionSheetDialog.SheetItemColor.Blue, new ActionSheetDialog.OnSheetItemClickListener() {
            @Override
            public void onClick(int which) {
                // 相册选取
                Intent intent2 = new Intent(Intent.ACTION_GET_CONTENT);
                intent2.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
                startActivityForResult(intent2, 103);
            }
        }).show();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch(requestCode){
            case 102:
                if (resultCode == Activity.RESULT_OK) {
                    Bitmap bmp = BitmapFactory.decodeFile(imageFilePath);
                    autoGenName = AutoGenerate.genName("forum", "jpg");

                    photoSelected(bmp);

                    filePath = imageFilePath;

                    new MyAsyncTaskUploadPicture().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, 0);
                    new MyAsyncTaskPostProgress().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                }
                break;
            case 103:
                if (resultCode == Activity.RESULT_OK) {
                    Bitmap bmp = null;
                    // 外界的程序访问ContentProvider所提供数据 可以通过ContentResolver接口
                    ContentResolver resolver = getContentResolver();
                    Uri originalUri = data.getData(); // 获得图片的uri

                    try {
                        bmp = MediaStore.Images.Media.getBitmap(resolver, originalUri); // 显得到bitmap图片
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    // 这里开始的第二部分，获取图片的路径：
                    Cursor cursor = activity.getContentResolver().query(originalUri, null, null, null, null);
                    String path = "";
                    if (cursor != null) {
                        cursor.moveToFirst();
                        String document_id = cursor.getString(0);
                        document_id = document_id.substring(document_id.lastIndexOf(":") + 1);
                        cursor.close();
                        cursor = activity.getContentResolver().query(
                                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                                null, MediaStore.Images.Media._ID + " = ? ", new String[]{document_id}, null);
                        if (cursor != null) {
                            cursor.moveToFirst();
                            path = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
                            cursor.close();
                        }
                    }

                    File file = new File(path);
                    String extension = file.getName().split("\\.")[1];
                    autoGenName = AutoGenerate.genName("forum", extension);

                    photoSelected(bmp);

                    filePath = path;
                    if(extension.contains("gif")) {
                        new MyAsyncTaskUploadPicture().execute(2);
                    }
                    new MyAsyncTaskUploadPicture().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, 0);
                    new MyAsyncTaskPostProgress().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                }
                break;
        }
        super.onActivityResult(requestCode,resultCode,data);
    }

    void photoSelected (Bitmap bmp) {
        switch (index) {
            case 1:
                iv_photo1.setImageBitmap(bmp);
                img1 = autoGenName;
                iv_photo2.setClickable(true);
                iv_photo2.setImageResource(R.drawable.ic_control_point_black_80dp);
                progress1.setVisibility(View.VISIBLE);
                break;
            case 2:
                iv_photo2.setImageBitmap(bmp);
                img2 = autoGenName;
                progress2.setVisibility(View.VISIBLE);
                break;
            default:
                iv_photo.setImageBitmap(bmp);
                img0 = autoGenName;
                iv_photo1.setClickable(true);
                iv_photo1.setImageResource(R.drawable.ic_control_point_black_80dp);
                progress.setVisibility(View.VISIBLE);
                break;
        }
    }

    private void refresh() {
        finish();
        techForum.forumTypeSelector.getTabAt(Integer.parseInt(type) - 1).select();
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
            if (pt && pc) {
                publish_commit.setTextColor(ContextCompat.getColor(getBaseContext(), R.color.colorPrimary));
                publish_commit.setClickable(true);
            } else {
                publish_commit.setTextColor(ContextCompat.getColor(getBaseContext(), R.color.gainsboro));
                publish_commit.setClickable(false);
            }
        }
    }

    private class MyAsyncTaskUploadPicture extends AsyncTask<Integer, String, String> {
        @Override
        protected String doInBackground(Integer... params) {
            File file = new File(filePath); //这里的path就是那个地址的全局变量
            return UploadFileUtil.uploadImageFile(file, params[0], autoGenName.split("\\.")[0]);
        }

        @Override
        protected void onPostExecute(String result) {
            if (!result.equals("上传成功！")) {
                Toast toast = Toast.makeText(ForumPublishActivity.this, result, Toast.LENGTH_SHORT);
                toast.setGravity(Gravity.BOTTOM, 0, 0);
                toast.show();
            }
        }

    }

    private class MyAsyncTaskPostForumItem extends AsyncTask<Integer, String, String> {
        @Override
        protected String doInBackground(Integer... pages) {
            forum forum = new forum();
            forum.setU_id(Integer.parseInt(uid));
            forum.setContent(content);
            forum.setTitle(title);
            forum.setType(Integer.parseInt(type));
            forum.setImg(img);
            return ForumItemService.executePost(forum);
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
            String progressStr = WebService.getFileUploadProgress();
            int progress;
            while (progressStr.equals("pending")) {
                progressStr = WebService.getFileUploadProgress();
            }
            publishProgress(0);
            while (!progressStr.equals("finished")) {
                progressStr = WebService.getFileUploadProgress();
                if (progressStr.contains("%")) {
                    progress = Integer.parseInt(progressStr.substring(0, progressStr.indexOf("%")));
                    publishProgress(progress);
                }
            }
            publishProgress(100);
            return null;
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            switch (index) {
                case 1:
                    progressBar1.setProgress(values[0]);
                    break;
                case 2:
                    progressBar2.setProgress(values[0]);
                    break;
                case 0:
                    progressBar.setProgress(values[0]);
                    break;
            }
        }
    }
}
