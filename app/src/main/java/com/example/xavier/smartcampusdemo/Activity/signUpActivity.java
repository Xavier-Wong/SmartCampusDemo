package com.example.xavier.smartcampusdemo.activity;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Toast;

import com.example.xavier.smartcampusdemo.R;
import com.example.xavier.smartcampusdemo.entity.user;
import com.example.xavier.smartcampusdemo.service.WebService;
import com.example.xavier.smartcampusdemo.util.DisplayUtils;
import com.example.xavier.smartcampusdemo.util.NetUtil.UploadFileUtil;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

import de.hdodenhof.circleimageview.CircleImageView;
import zhangphil.iosdialog.widget.ActionSheetDialog;

/**
 * Created by Xavier on 11/7/2016.
 *
 */

public class SignUpActivity extends BaseActivity implements View.OnClickListener {

    String imageFilePath, filePath, availableName = "";
    Handler handler = new Handler();
    private String info;
    private RadioButton rdMale, rdFemale;
    private EditText usrName, usrPwd, confirm_usrPws, usrEml, stuId, tel;
    private CircleImageView register_avatar;

    public static boolean isNumeric(String str) {
        for (int i = str.length(); --i >= 0; ) {
            if (!Character.isDigit(str.charAt(i))) {
                return false;
            }
        }
        return true;
    }

    public static void actionStart(Context context) {
        Intent intent = new Intent(context, SignUpActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_sign_up);

        rdMale = (RadioButton) findViewById(R.id.male_button);
        rdFemale = (RadioButton) findViewById(R.id.female_button);
        rdFemale.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked) {
                    rdMale.setChecked(false);
                }
            }
        });
        rdMale.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked) {
                    rdFemale.setChecked(false);
                }
            }
        });

        register_avatar = (CircleImageView) findViewById(R.id.register_avatar);
        usrName = (EditText) findViewById(R.id.username);
        usrPwd = (EditText) findViewById(R.id.password);
        confirm_usrPws = (EditText) findViewById(R.id.conpassword);
        usrEml = (EditText) findViewById(R.id.email);
        stuId = (EditText) findViewById(R.id.sid);
        tel = (EditText) findViewById(R.id.tel);
        Button sign_up = (Button) findViewById(R.id.bt_sign_up);

        assert sign_up != null;
        register_avatar.setOnClickListener(this);
        sign_up.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.register_avatar:
                choose_avatar();
                break;
            case R.id.bt_sign_up:
                if(usrName.getText().toString().equals("")) {
                    usrName.requestFocus();
                    DisplayUtils.customTopShortToast(this, "账号不能为空", 0, 200);
                }
                else if(usrPwd.getText().toString().equals("")) {
                    usrPwd.requestFocus();
                    DisplayUtils.customTopShortToast(this, "密码不能为空", 0, 200);
                }
                else if(!confirm_usrPws.getText().toString().equals(usrPwd.getText().toString())) {
                    confirm_usrPws.requestFocus();
                    DisplayUtils.customTopShortToast(this, "两次密码不匹配", 0, 200);
                }
                else if(usrEml.getText().toString().equals("")) {
                    usrEml.requestFocus();
                    DisplayUtils.customTopShortToast(this, "邮箱不能为空", 0, 200);
                }
                else if(stuId.getText().toString().equals("")) {
                    stuId.requestFocus();
                    DisplayUtils.customTopShortToast(this, "学号不能为空", 0, 200);
                }
                else if(!isNumeric(stuId.getText().toString())) {
                    stuId.requestFocus();
                    DisplayUtils.customTopShortToast(this, "学号格式错误", 0, 200);
                }
                else if(!isNumeric(tel.getText().toString())) {
                    tel.requestFocus();
                    DisplayUtils.customTopShortToast(this, "电话号码格式错误", 0, 200);
                }
                else {
                    new Thread(new MyThread()).start();
                }
                break;
        }
    }

    public void choose_avatar() {
        new ActionSheetDialog(SignUpActivity.this).builder().setTitle("上传头像")
                .setCancelable(false).setCanceledOnTouchOutside(false)
                .addSheetItem("拍照上传", ActionSheetDialog.SheetItemColor.Blue, new ActionSheetDialog.OnSheetItemClickListener() {

                    @Override
                    public void onClick(int which) {
                        // 拍照
                        //设置图片的保存路径,作为全局变量
                        imageFilePath = Environment.getExternalStorageDirectory().getAbsolutePath()+"/temp";
                        File temp = new File(imageFilePath);
                        if(!temp.exists())
                            temp.mkdirs();
                        imageFilePath = imageFilePath + "/tempAvatar.jpg";
                        temp = new File(imageFilePath);
                        if(temp.exists())
                            temp.delete();
                        Uri imageFileUri = Uri.fromFile(temp);//获取文件的Uri
                        Intent intent1 = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);//跳转到相机Activity
                        intent1.putExtra(android.provider.MediaStore.EXTRA_OUTPUT, imageFileUri);//告诉相机拍摄完毕输出图片到指定的Uri
                        startActivityForResult(intent1, 102);
                    }
                })
                .addSheetItem("相册选择", ActionSheetDialog.SheetItemColor.Blue, new ActionSheetDialog.OnSheetItemClickListener() {
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
                    String avatarId = UUID.randomUUID().toString();
                    availableName =  "avatar_" + avatarId + extension;
                    register_avatar.setImageBitmap(bmp);
                    filePath = imageFilePath;
                    new Thread(new UploadThread()).start();
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

                    // 好像是android多媒体数据库的封装接口，具体的看Android文档
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
                    String avatarId = UUID.randomUUID().toString();
                    availableName =  "avatar_"+avatarId+extension;

                    register_avatar.setImageBitmap(bm);
                    filePath = path;
                    new Thread(new UploadThread()).start();
                }
                break;
        }
        super.onActivityResult(requestCode,resultCode,data);
    }

    private class UploadThread implements Runnable {

        @Override
        public void run() {
            if(filePath != null) {
                File file = new File(filePath); //这里的path就是那个地址的全局变量
                String result = UploadFileUtil.uploadImageFile(file, 0, availableName.split("\\.")[0]);
                Toast toast = Toast.makeText(SignUpActivity.this,result,Toast.LENGTH_SHORT);
                toast.setGravity(Gravity.BOTTOM,0,0);
                toast.show();
            }
        }
    }

    private class MyThread implements Runnable {
        @Override
        public void run() {
            Integer sex;
            sex = rdMale.isChecked()?1:0;
            user user = new user();
            user.setUsername(usrName.getText().toString());
            user.setPassword(usrPwd.getText().toString());
            if(stuId.getText().toString().equals("")) {
                user.setS_Id(0);
            }
            else
                user.setS_Id(Integer.parseInt(stuId.getText().toString()));
            user.setSex(sex);
            user.setEmail(usrEml.getText().toString());
            user.setTel(tel.getText().toString());
            user.setAvatar(availableName);
            info = WebService.executeSignUp(user);
            handler.post(new Runnable() {
                @Override
                public void run() {
                    switch (info) {
                        case "noResponse":
                            DisplayUtils.customBottomShortToast(SignUpActivity.this, "服务器未响应，请稍后再试", 0, 100);
                            break;
                        case "注册成功":
                            DisplayUtils.customBottomShortToast(SignUpActivity.this, "注册成功", 0, 100);
                            finish();
                            break;
                        case "注册失败":
                            DisplayUtils.customBottomShortToast(SignUpActivity.this, "注册失败", 0, 100);
                            break;
                    }

                }
            });
        }
    }
}
