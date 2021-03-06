package com.example.xavier.smartcampusdemo.activity;

import android.app.Application;

import com.example.xavier.smartcampusdemo.service.NetService;
import com.facebook.drawee.backends.pipeline.Fresco;


/**
 * Created by Xavier on 5/2/2017.
 */

public class MyApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        Fresco.initialize(this);
        new Thread(new Runnable() {
            @Override
            public void run() {
                NetService.getSessionId();
            }
        }).start();
    }
}