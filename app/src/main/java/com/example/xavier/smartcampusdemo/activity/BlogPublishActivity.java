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
import android.widget.TextView;
import android.widget.Toast;

import com.example.xavier.smartcampusdemo.R;
import com.example.xavier.smartcampusdemo.fragment.microBlog;
import com.example.xavier.smartcampusdemo.util.NetUtil.UploadFileUtil;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Properties;
import java.util.UUID;

import zhangphil.iosdialog.widget.ActionSheetDialog;

import static com.example.xavier.smartcampusdemo.service.NetService.getIP;

/**
 * Created by Xavier on 4/5/2017.
 *
 */

public class BlogPublishActivity extends BaseActivity{

    private boolean isChose = false, isChose1 = false, isChose2 = false;
    String imageFilePath, filePath, availableName;
    private String p_uid, p_content, p_img, img0, img1, img2;
    SharedPreferences sharedPreferences;
    TextView publish_commit;
    EditText publish_content;
    ImageView iv_photo, iv_photo1, iv_photo2;
    private int index;

    Toolbar toolbar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.microblog_publish);
        toolbar = (Toolbar) findViewById(R.id.blog_publish_toolbar);
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

        publish_content = (EditText) findViewById(R.id.blog_publish_content);
        publish_commit = (TextView) findViewById(R.id.blog_publish_commit);
        iv_photo = (ImageView) findViewById(R.id.blog_publish_photo_thumbnail);
        iv_photo1 = (ImageView) findViewById(R.id.blog_publish_photo_thumbnail1);
        iv_photo2 = (ImageView) findViewById(R.id.blog_publish_photo_thumbnail2);
        iv_photo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                index = 0;
                choose_photo(index);
            }
        });
        iv_photo1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                index = 1;
                choose_photo(index);
            }
        });
        iv_photo2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                index = 2;
                choose_photo(index);
            }
        });
        iv_photo1.setClickable(false);
        iv_photo2.setClickable(false);
        publish_content.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @TargetApi(Build.VERSION_CODES.M)
            @Override
            public void afterTextChanged(Editable s) {
                if(s.toString().length()>0) {
                    publish_commit.setTextColor(ContextCompat.getColor(getBaseContext(), R.color.colorPrimary));
                    publish_commit.setClickable(true);
                }
                else {
                    publish_commit.setTextColor(ContextCompat.getColor(getBaseContext(), R.color.gainsboro));
                    publish_commit.setClickable(false);
                }
            }
        });
        publish_commit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sharedPreferences = getSharedPreferences("USER_INFO", Context.MODE_PRIVATE);
                p_uid = sharedPreferences.getString("userID","");
                p_content = publish_content.getText().toString();
                p_img = "";
                if(img0 != null)
                    p_img = p_img + img0 +";";
                if(img1 != null)
                    p_img = p_img + img1 +";";
                if(img2 != null)
                    p_img = p_img + img2 +";";
                new Thread(new MyThread()).start();
                refresh();
            }
        });
    }

    public void choose_photo(final int index) {
        new ActionSheetDialog(BlogPublishActivity.this).builder().setTitle("上传图片")
                .setCancelable(false).setCanceledOnTouchOutside(true)
                .addSheetItem("拍照上传", ActionSheetDialog.SheetItemColor.Blue, new ActionSheetDialog.OnSheetItemClickListener() {

                    @Override
                    public void onClick(int which) {
                        imageFilePath = Environment.getExternalStorageDirectory().getAbsolutePath()+"/temp";
                        File temp = new File(imageFilePath);
                        if(!temp.exists())
                            temp.mkdirs();
                        imageFilePath = imageFilePath + "/tempBlog.jpg";
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
                    File file = new File(imageFilePath);
                    String extension = file.getName().substring(file.getName().lastIndexOf("."));
                    Bitmap bmp = BitmapFactory.decodeFile(imageFilePath);
                    String blogId = UUID.randomUUID().toString();
                    availableName =  "blog_" + blogId + extension;
                    switch (index) {
                        case 1:
                            iv_photo1.setImageBitmap(bmp);
                            img1 = availableName;
                            isChose1 =true;
                            iv_photo2.setClickable(true);
                            iv_photo2.setImageResource(R.drawable.ic_control_point_black_80dp);
                            break;
                        case 2:
                            iv_photo2.setImageBitmap(bmp);
                            img2 = availableName;
                            isChose2 =true;
                            break;
                        default:
                            iv_photo.setImageBitmap(bmp);
                            img0 = availableName;
                            isChose =true;
                            iv_photo1.setClickable(true);
                            iv_photo1.setImageResource(R.drawable.ic_control_point_black_80dp);
                            break;
                    }
                    filePath = imageFilePath;
                    new Thread(new UploadThread(0)).start();
                }
                break;
            case 103:
                if (resultCode == Activity.RESULT_OK) {
                    Bitmap bm = null;
                    // 外界的程序访问ContentProvider所提供数据 可以通过ContentResolver接口
                    ContentResolver resolver = getContentResolver();
                    Uri originalUri = data.getData(); // 获得图片的uri

                    try {
                        bm = MediaStore.Images.Media.getBitmap(resolver, originalUri); // 显得到bitmap图片
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    // 这里开始的第二部分，获取图片的路径：

                    String[] proj = {MediaStore.Images.Media.DATA};
                    @SuppressWarnings("deprecation")
                    Cursor cursor = managedQuery(originalUri, proj, null, null, null);
                    // 按我个人理解 这个是获得用户选择的图片的索引值
                    int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
                    // 将光标移至开头 ，这个很重要，不小心很容易引起越界
                    cursor.moveToFirst();
                    // 最后根据索引值获取图片路径
                    String path = cursor.getString(column_index);
                    File file = new File(path);
                    String extension = file.getName().substring(file.getName().lastIndexOf("."));
                    String blogId = UUID.randomUUID().toString();
                    availableName =  "blog_"+blogId+extension;

                    switch (index) {
                        case 1:
                            iv_photo1.setImageBitmap(bm);
                            img1 = availableName;
                            isChose1 =true;
                            iv_photo2.setClickable(true);
                            iv_photo2.setImageResource(R.drawable.ic_control_point_black_80dp);
                            break;
                        case 2:
                            iv_photo2.setImageBitmap(bm);
                            img2 = availableName;
                            isChose2 =true;
                            break;
                        default:
                            iv_photo.setImageBitmap(bm);
                            img0 = availableName;
                            isChose =true;
                            iv_photo1.setClickable(true);
                            iv_photo1.setImageResource(R.drawable.ic_control_point_black_80dp);
                            break;
                    }

                    filePath = path;
                    if(extension.contains("gif")) {
                        new Thread(new UploadThread(2)).start();
                    }
                    new Thread(new UploadThread(0)).start();
                }
                break;
        }
        super.onActivityResult(requestCode,resultCode,data);
    }

    private class UploadThread implements Runnable {
        Integer type;

        UploadThread(Integer type) {
            this.type = type;
        }
        @Override
        public void run() {
            if(filePath != null) {
                File file = new File(filePath); //这里的path就是那个地址的全局变量
                String result = UploadFileUtil.uploadImageFile(file, type, availableName.split("\\.")[0]);
                Toast toast = Toast.makeText(BlogPublishActivity.this,result,Toast.LENGTH_SHORT);
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
            String url = "http://" + getIP() + "/HelloWeb/BlogPublishLet";
            try {
                HttpURLConnection conn = (HttpURLConnection) new URL(url).openConnection();
                conn.setReadTimeout(3000);
                conn.setRequestMethod("POST");

                conn.setDoOutput(true);
                conn.setDoInput(true);
                PrintWriter printWriter = new PrintWriter(conn.getOutputStream());
                String post = "action=1&uid="+p_uid+"&content="+p_content+"&img="+p_img;
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
        microBlog.refresh();
        finish();
//        Intent intent = new Intent(BlogPublishActivity.this, MainActivity.class);
//        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//        intent.putExtra("isPublished",true);
//        startActivity(intent);
    }
}
