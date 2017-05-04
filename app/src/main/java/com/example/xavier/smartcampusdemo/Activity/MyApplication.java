package com.example.xavier.smartcampusdemo.Activity;

import android.app.Application;

import com.facebook.drawee.backends.pipeline.Fresco;

/**
 * Created by Xavier on 5/2/2017.
 */

public class MyApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        Fresco.initialize(this);
    }
}