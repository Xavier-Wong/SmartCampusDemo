package com.example.xavier.smartcampusdemo.activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.example.xavier.smartcampusdemo.R;
import com.example.xavier.smartcampusdemo.service.UserInfoService;
import com.example.xavier.smartcampusdemo.service.WebService;
import com.example.xavier.smartcampusdemo.util.DisplayUtils;
import com.example.xavier.smartcampusdemo.util.JSONUtil;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Xavier on 11/7/2016.
 * SignInActivity.java
 */

public class SignInActivity extends BaseActivity implements View.OnClickListener, View.OnTouchListener {

    Button sign_in;
    TextView sign_up;
    Handler handler = new Handler();
    private String info;
    private EditText usrName, usrPwd;
    private Button clear_username;
    private Button show_password;
    private TextWatcher username_watcher, password_watcher;
    private ProgressDialog dialog;

    public static void actionStart(Context context) {
        Intent intent = new Intent(context, SignInActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_sign_in);

        usrName = (EditText) findViewById(R.id.username);
        usrPwd = (EditText) findViewById(R.id.password);
        clear_username = (Button) findViewById(R.id.bt_clear_username);
        show_password = (Button) findViewById(R.id.bt_show_password);

        sign_in = (Button) findViewById(R.id.bt_sign_in);
        sign_up = (TextView) findViewById(R.id.sign_up);

        /*SetListener*/
        clear_username.setOnClickListener(this);
        show_password.setOnTouchListener(this);
        sign_in.setOnClickListener(this);
        sign_up.setOnClickListener(this);

        initWatcher();
        usrName.addTextChangedListener(username_watcher);
        usrPwd.addTextChangedListener(password_watcher);

    }

    private void initWatcher() {
        username_watcher = new TextWatcher() {
            public void afterTextChanged(Editable s) {
                if(s.toString().length()>0) {
                    clear_username.setVisibility(View.VISIBLE);
                }
                else {
                    clear_username.setVisibility(View.INVISIBLE);
                }
            }
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            public void onTextChanged(CharSequence s, int arg1, int before, int count) {}
        };
        password_watcher = new TextWatcher() {
            public void afterTextChanged(Editable s) {
                if(s.toString().length()>0) {
                    show_password.setVisibility(View.VISIBLE);
                }
                else {
                    show_password.setVisibility(View.INVISIBLE);
                }
            }
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            public void onTextChanged(CharSequence s, int arg1, int before, int count) {}
        };
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()) {
            case R.id.bt_sign_in:
                // 提示框
                dialog = new ProgressDialog(this);
                dialog.setMessage("正在登陆，请稍后...");
                dialog.setCancelable(false);
                dialog.show();
                // 创建子线程，分别进行Get和Post传输
                new Thread(new MyThread()).start();
                break;
            case R.id.bt_clear_username:
                usrName.setText("");
                usrPwd.setText("");
                break;
            case R.id.sign_up:
                SignUpActivity.actionStart(SignInActivity.this);
                break;
        }
    }

    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {
        if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
            show_password.setBackgroundResource(R.drawable.ic_password_show);
            usrPwd.setInputType(InputType.TYPE_CLASS_TEXT);
            usrPwd.setSelection(usrPwd.getText().toString().length());
        }
        if (motionEvent.getAction() == MotionEvent.ACTION_UP) {
            show_password.setBackgroundResource(R.drawable.ic_password_show);
            usrPwd.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
            usrPwd.setSelection(usrPwd.getText().toString().length());
        }
        return false;
    }

    private class MyThread implements Runnable {
        @Override
        public void run() {
            info = WebService.executeSignIn(usrName.getText().toString(), usrPwd.getText().toString());
            Log.d("checkcheck",usrName.getText().toString());
            final JSONObject userInfo = JSONUtil.getJsonObject(UserInfoService.getUserByName(usrName.getText().toString()));
            handler.post(new Runnable() {
                @Override
                public void run() {
                    switch (info) {
                        case "noResponse": {
                            dialog.dismiss();
                            DisplayUtils.customBottomShortToast(SignInActivity.this, "服务器未响应，请稍后再试", 0, 100);
                            break;
                        }
                        case "": {
                            DisplayUtils.customBottomShortToast(SignInActivity.this, "用户名或密码错误，请重新输入", 0, 100);
                            dialog.dismiss();
                            break;
                        }
                        default: {
                            SharedPreferences sharedPreferences = getSharedPreferences("USER_INFO", Context.MODE_PRIVATE);
                            SharedPreferences.Editor editor = sharedPreferences.edit();
                            editor.putString("userName", usrName.getText().toString());
                            editor.putString("userAvatar", info);
                            Log.d("checkcheck",info);
                            try {
                                assert userInfo != null;
                                editor.putString("userID", userInfo.getString("u_id"));
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            editor.apply();
                            dialog.dismiss();
                            DisplayUtils.customBottomShortToast(SignInActivity.this, "登陆成功", 0, 100);
                            onlyActivity(MainActivity.class);
                            break;
                        }
                    }
                }
            });
        }
    }
}
