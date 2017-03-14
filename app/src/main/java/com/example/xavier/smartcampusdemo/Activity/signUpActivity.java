package com.example.xavier.smartcampusdemo.Activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.xavier.smartcampusdemo.R;
import com.example.xavier.smartcampusdemo.Service.WebService;

/**
 * Created by Xavier on 11/7/2016.
 */

public class SignUpActivity extends BaseActivity implements View.OnClickListener {

    private String info;
    private EditText username,password,conpassword,sid,tel;
    private Button sign_up;
    private static Handler handler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.signup_main);

        username = (EditText) findViewById(R.id.username);
        password = (EditText) findViewById(R.id.password);
        conpassword = (EditText) findViewById(R.id.conpassword);
        sid = (EditText) findViewById(R.id.sid);
        tel = (EditText) findViewById(R.id.tel);
        sign_up = (Button) findViewById(R.id.bt_sign_up);

        sign_up.setOnClickListener(this);

    }
    public static void actionStart(Context context) {
        Intent intent = new Intent(context, SignUpActivity.class);
        context.startActivity(intent);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.bt_sign_up:
                new Thread(new MyThread()).start();
        }
    }

    public class MyThread implements Runnable {
        @Override
        public void run() {
            info = WebService.executeSignUp(username.getText().toString(), password.getText().toString(), sid.getText().toString(), tel.getText().toString());
            handler.post(new Runnable() {
                @Override
                public void run() {
                    if(info.equals("注册成功")) {
                        Toast toast = Toast.makeText(SignUpActivity.this, "注册成功", Toast.LENGTH_SHORT);
                        toast.setGravity(Gravity.BOTTOM, 0, 0);
                        toast.show();
                        finish();
                    }
                    else if(info.equals("注册失败")) {
                        Toast toast = Toast.makeText(SignUpActivity.this, "注册失败", Toast.LENGTH_SHORT);
                        toast.setGravity(Gravity.BOTTOM, 0, 0);
                        toast.show();
                    }

                }
            });
        }
    }
}
