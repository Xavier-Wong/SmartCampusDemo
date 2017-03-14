package com.example.xavier.smartcampusdemo.Activity;

import android.annotation.SuppressLint;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.example.xavier.smartcampusdemo.Adapter.techForumDetailsViewAdapter;
import com.example.xavier.smartcampusdemo.Entity.forum_reply;
import com.example.xavier.smartcampusdemo.R;
import com.example.xavier.smartcampusdemo.Service.ForumItemService;
import com.example.xavier.smartcampusdemo.Util.JSONUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import static com.example.xavier.smartcampusdemo.R.string.isLoading;
import static java.util.Collections.addAll;

/**
 * Created by Xavier on 2/27/2017.
 *
 */

public class ForumsDetailsActivity extends BaseActivity implements OnTouchListener{

    ActionBar actionBar;
    techForumDetailsViewAdapter adapter;
    private float startX;
    private float startY;
    String id = "";
    List<forum_reply> forumReplyItems= new ArrayList<>();
    RecyclerView mRecyclerView;
    private RelativeLayout loadingRL;
    int page = 0;
    private boolean isLoading = true;

    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if(msg.what==1){
                final JSONObject forumItem = (JSONObject) msg.obj;
                mRecyclerView = (RecyclerView) findViewById(R.id.techForumDetails_rv);
                final LinearLayoutManager mLayoutManager = new LinearLayoutManager(getParent());
                mRecyclerView.setLayoutManager(mLayoutManager);
                mRecyclerView.setHasFixedSize(true);
                adapter = new techForumDetailsViewAdapter(getParent(), null);
                mRecyclerView.setAdapter(adapter);
                adapter.setDetailsObject(forumItem);
                adapter.addForumReplyItem(forumReplyItems);
                adapter.notifyDataSetChanged();
                mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
                    @Override
                    public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                        super.onScrollStateChanged(recyclerView, newState);
                    }

                    @Override
                    public void onScrolled(RecyclerView recyclerView,int dx, int dy) {
                        int firstVisibleItems;

                        firstVisibleItems = mLayoutManager.findFirstVisibleItemPosition();

                        if (firstVisibleItems == 0 || firstVisibleItems == 1) {
                            actionBar.setTitle("");
                        }
                        else {
                            try {
                                actionBar.setTitle(forumItem.getString("author"));
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                });

                loadingRL.setVisibility(View.GONE);
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forums_details);
        actionBar = getSupportActionBar();
        assert actionBar != null;
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle("");

        id = getIntent().getStringExtra("url");
        loadingRL = (RelativeLayout)findViewById(R.id.forums_details_is_loading);
        MyThread myThread = new MyThread(id);
        myThread.start();
        new MyAsyncTaskGetForumReplyItem().execute(page);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case android.R.id.home:
                finish();
                break;
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

    public class MyAsyncTaskGetForumReplyItem extends AsyncTask<Integer, String, List<forum_reply>> {
        @Override
        protected List<forum_reply> doInBackground(Integer... pages) {
            return ForumItemService.getForumItemReply(pages[0], id);
        }

        @Override
        protected void onPostExecute(List<forum_reply> forumItems) {
            if(forumItems.size()>0) {
                adapter.addForumReplyItem(forumItems);
                adapter.notifyDataSetChanged();
                page++;
            }
            isLoading = false;
//            if(page>0 && forumItems.size()==0) {
//                adapter.notifyItemRemoved(adapter.getItemCount());
//                Toast toast = Toast.makeText(ForumsDetailsActivity.this,"没有更多了",Toast.LENGTH_SHORT);
//                toast.setGravity(Gravity.BOTTOM, 0, 100);
//                toast.show();
//            }
        }

    }
}
