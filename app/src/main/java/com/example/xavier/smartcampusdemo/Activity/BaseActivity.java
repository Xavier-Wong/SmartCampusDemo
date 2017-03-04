package com.example.xavier.smartcampusdemo.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.example.xavier.smartcampusdemo.Collector.ActivityCollector;

/**
 * Created by Xavier on 11/13/2016.
 * BaseActivity.java
 */

public class BaseActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d("BaseActivity", getClass().getSimpleName());
        ActivityCollector.addActivity(this);
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
    public void onlyActivity (Class<?> cls) {
        Intent intent = new Intent();
        intent.setClass(this, cls);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }
}
