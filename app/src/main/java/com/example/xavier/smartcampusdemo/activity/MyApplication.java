package com.example.xavier.smartcampusdemo.activity;

import android.app.Application;
import android.content.Context;

import com.example.xavier.smartcampusdemo.service.NetService;
import com.example.xavier.smartcampusdemo.service.WebService;
import com.facebook.common.internal.Supplier;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.imagepipeline.cache.MemoryCacheParams;
import com.facebook.imagepipeline.core.ImagePipelineConfig;

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