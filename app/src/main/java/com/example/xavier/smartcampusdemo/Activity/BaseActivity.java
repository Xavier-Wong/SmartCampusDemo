package com.example.xavier.smartcampusdemo.Activity;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.example.xavier.smartcampusdemo.Collector.ActivityCollector;

/**
 * Created by Xavier on 11/13/2016.
 * BaseActivity
 */

public class BaseActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d("BaseActivity", getClass().getSimpleName());
        ActivityCollector.addActivity(this);
        ActivityCompat.requestPermissions(BaseActivity.this,
                new String[]{Manifest.permission.CAMERA,Manifest.permission.WRITE_EXTERNAL_STORAGE},
                1);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ActivityCollector.removeActivity(this);
    }

//    public void goToActivity (Class<?> cls) {
//        Intent intent = new Intent();
//        intent.setClass(this, cls);
//        startActivity(intent);
//    }
//
    /*
    启动指定活动并清除其余所有活动
     */
    public void onlyActivity (Class<?> cls) {
        Intent intent = new Intent();
        intent.setClass(this, cls);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }
}
