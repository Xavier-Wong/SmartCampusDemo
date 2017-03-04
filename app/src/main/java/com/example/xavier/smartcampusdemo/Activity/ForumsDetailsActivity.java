package com.example.xavier.smartcampusdemo.Activity;

import android.annotation.SuppressLint;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.xavier.smartcampusdemo.R;
import com.example.xavier.smartcampusdemo.Service.ForumItemService;
import com.example.xavier.smartcampusdemo.Util.JSONUtil;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Xavier on 2/27/2017.
 *
 */

public class ForumsDetailsActivity extends BaseActivity implements OnTouchListener{

    private float startX;
    private float startY;
    private TextView tv, detail_author;
    private View v;
    private RelativeLayout loadingRL;
    private RelativeLayout detailsRL;

    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if(msg.what==1){
                JSONObject forumItem = (JSONObject) msg.obj;
                try {
                    detail_author.setText(forumItem.getString("author"));
                    tv.setText(forumItem.getString("content"));
                    tv.append("\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n123");
                    tv.append("\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n123");
                    tv.append("\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n123");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                loadingRL.setVisibility(View.GONE);
                detailsRL.setVisibility(View.VISIBLE);
            }
        }
    };

    @SuppressLint("SetJavaScriptEnabled")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forums_details);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        v = LayoutInflater.from(getBaseContext()).inflate(R.layout.activity_forums_details, null);

        final String id = getIntent().getStringExtra("url");
        detailsRL = (RelativeLayout)findViewById(R.id.forums_details);
        loadingRL = (RelativeLayout)findViewById(R.id.forums_details_is_loading);
        tv = (TextView) findViewById(R.id.textView);
        detail_author = (TextView) findViewById(R.id.detail_author);
        MyThread myThread = new MyThread(id);
        myThread.start();
        //initContent(id);
        loadingRL.setOnTouchListener(this);
    }

    void initContent(String id) {
        try {
            Message message = new Message();
            message.what = 1;
            handler.handleMessage(message);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if(id ==android.R.id.home){
            finish();
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * 左滑动返回监听
     */
    @Override
    public boolean onTouch(View v, MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                startX = event.getX();
                startY = event.getY();
                break;
            case MotionEvent.ACTION_UP:
                float endX = event.getX();
                float endY = event.getY();
                if(Math.abs(endX-startX)>150&&((endY-startY)==0||Math.abs((endX-startX)/(endY-startY))>2)){
                    finish();
                }
                break;
            default:
                break;
        }
        return false;
    }

    public class MyThread extends Thread {

        private String id;

        MyThread(String id) {
            this.id = id;
        }
        @Override
        public void run() {
            String forum = ForumItemService.getForumsDetails(id);
            JSONObject forumItem = JSONUtil.getJsonObject(forum);
            Message msg = new Message();
            msg.obj = forumItem;
            msg.what = 1;
            handler.sendMessage(msg);
        }
    }
}
