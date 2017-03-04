package com.example.xavier.smartcampusdemo.Activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.xavier.smartcampusdemo.R;
import com.example.xavier.smartcampusdemo.Service.WebService;

/**
 * Created by Xavier on 11/7/2016.
 * SignInActivity.java
 */

public class SignInActivity extends BaseActivity implements View.OnClickListener, View.OnTouchListener {

    private String info;
    private EditText username, password;
    private Button clear_username, show_password;
    private TextView sign_up;
    private TextWatcher username_watcher, password_watcher;
    private static Handler handler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_main);

        /*EditText Initiate*/
        username = (EditText) findViewById(R.id.username);
        password = (EditText) findViewById(R.id.password);

        /*Button Initiate*/
        clear_username = (Button) findViewById(R.id.bt_clear_username);
//        clear_password = (Button) findViewById(R.id.bt_pwd_clear);
        show_password = (Button) findViewById(R.id.bt_show_password);
        Button sign_in = (Button) findViewById(R.id.bt_sign_in);

        sign_up = (TextView) findViewById(R.id.sign_up);

        /*SetListener*/
        clear_username.setOnClickListener(this);
//        clear_password.setOnClickListener(this);
        show_password.setOnTouchListener(this);
        sign_in.setOnClickListener(this);
        sign_up.setOnClickListener(this);

        initWatcher();
        username.addTextChangedListener(username_watcher);
        password.addTextChangedListener(password_watcher);

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
//                    clear_password.setVisibility(View.VISIBLE);
                    show_password.setVisibility(View.VISIBLE);
                }
                else {
//                    clear_password.setVisibility(View.INVISIBLE);
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
//            // 提示框
//            dialog = new ProgressDialog(this);
//            dialog.setTitle("提示");
//            dialog.setMessage("正在登陆，请稍后...");
//            dialog.setCancelable(false);
//            dialog.show();
                // 创建子线程，分别进行Get和Post传输
                new Thread(new MyThread()).start();
                break;
            case R.id.bt_clear_username:
                username.setText("");
                password.setText("");
                break;
            case R.id.sign_up:
                SignUpActivity.actionStart(SignInActivity.this);
                break;
//            case R.id.bt_pwd_clear:
//                password.setText("");
//                break;
        }
    }

    public class MyThread implements Runnable {
        @Override
        public void run() {
            info = WebService.executeSignIn(username.getText().toString(), password.getText().toString());
            // info = WebServicePost.executeHttpPost(username.getText().toString(), password.getText().toString());
            handler.post(new Runnable() {
                @Override
                public void run() {
                    if(info.equals("登录成功")) {
                        SharedPreferences sharedPreferences = getSharedPreferences("usrName", Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putString("username", username.getText().toString());
                        editor.apply();
                        Toast toast = Toast.makeText(SignInActivity.this, "登陆成功", Toast.LENGTH_SHORT);
                        toast.setGravity(Gravity.BOTTOM, 0, 0);
                        toast.show();
                        onlyActivity(MainActivity.class);
                    }
                    else if(info.equals("登录失败")) {
                        Toast toast = Toast.makeText(SignInActivity.this, "用户名或者密码错误，请重新输入", Toast.LENGTH_SHORT);
                        toast.setGravity(Gravity.BOTTOM, 0, 0);
                        toast.show();
                    }

                }
            });
        }
    }

    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {
        if(motionEvent.getAction()==MotionEvent.ACTION_DOWN){
            show_password.setBackgroundResource(R.drawable.ic_password_show);
            password.setInputType(InputType.TYPE_CLASS_TEXT);
            password.setSelection(password.getText().toString().length());
        }
        if(motionEvent.getAction()==MotionEvent.ACTION_UP){
            show_password.setBackgroundResource(R.drawable.ic_password_show);
            password.setInputType(InputType.TYPE_CLASS_TEXT|InputType.TYPE_TEXT_VARIATION_PASSWORD);
            password.setSelection(password.getText().toString().length());
        }
        return false;
    }


    public static void actionStart(Context context) {
        Intent intent = new Intent(context, SignInActivity.class);
        context.startActivity(intent);
    }
}
