package com.example.xavier.smartcampusdemo.Activity;

import android.app.Activity;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import com.example.xavier.smartcampusdemo.Adapter.blogReplyViewAdapter;
import com.example.xavier.smartcampusdemo.Entity.blog;
import com.example.xavier.smartcampusdemo.Entity.blog_reply;
import com.example.xavier.smartcampusdemo.R;
import com.example.xavier.smartcampusdemo.Service.BlogItemService;
import com.example.xavier.smartcampusdemo.Util.ColorUtils;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.interfaces.DraweeController;
import com.facebook.drawee.view.SimpleDraweeView;

import java.util.ArrayList;
import java.util.List;

import me.imid.swipebacklayout.lib.app.SwipeBackActivity;

import static com.example.xavier.smartcampusdemo.Service.NetService.getIP;
import static com.example.xavier.smartcampusdemo.Util.NetUtil.NetUtil.isNetworkAvailable;

/**
 * Created by Xavier on 5/1/2017.
 */

public class BlogDetailsActivity extends SwipeBackActivity implements SwipeRefreshLayout.OnRefreshListener {

    private SharedPreferences sharedPreferences;
    RecyclerView mRecyclerView;
    blogReplyViewAdapter adapter;
    SwipeRefreshLayout mSwipeRefreshLayout;

    private View loadingRL, loadedRL;

    private Toolbar toolbar;
    private String id;

    private SimpleDraweeView civ_avatar;
    private TextView tv_author;
    private TextView tv_time;
    private TextView tv_content;
    private boolean isLoading = true;
    public static int page = 0;

    List<SimpleDraweeView> sdv_img = new ArrayList<>();

    private TextView tv_comment_count;
    private TextView tv_like_count;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_blog_details);

        id = getIntent().getStringExtra("bid");

        toolbar = (Toolbar) findViewById(R.id.blog_details_toolbar);
        assert toolbar != null;
        toolbar.setTitle("");
        if (toolbar != null) {
            setSupportActionBar(toolbar);
        }
        toolbar.setNavigationIcon(R.drawable.ic_navigation_back_black);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        final int sdk = Build.VERSION.SDK_INT;
        Window window = getWindow();
        WindowManager.LayoutParams params = window.getAttributes();
        if (sdk >= Build.VERSION_CODES.KITKAT) {
            int bits = 0;    // 设置透明状态栏
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.KITKAT) {
                bits = WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS;
            }
            if ((params.flags & bits) == 0) {
                params.flags |= bits;
                window.setAttributes(params);
            }
        }

        loadingRL = findViewById(R.id.blog_details_loading);
        civ_avatar = (SimpleDraweeView) findViewById(R.id.blog_details_avatar);
        tv_author = (TextView) findViewById(R.id.blog_details_author);
        tv_time = (TextView) findViewById(R.id.blog_details_time);
        tv_content = (TextView) findViewById(R.id.blog_details_content);
        SimpleDraweeView sdv_img1 = (SimpleDraweeView) findViewById(R.id.blog_details_img1);
        SimpleDraweeView sdv_img2 = (SimpleDraweeView) findViewById(R.id.blog_details_img2);
        SimpleDraweeView sdv_img3 = (SimpleDraweeView) findViewById(R.id.blog_details_img3);
        sdv_img.clear();
        sdv_img.add(sdv_img1);
        sdv_img.add(sdv_img2);
        sdv_img.add(sdv_img3);
        tv_comment_count = (TextView) findViewById(R.id.blog_details_comment_count);
        tv_like_count = (TextView) findViewById(R.id.blog_details_like_count);

        mSwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.blog_reply_refresh);
        mSwipeRefreshLayout.setColorSchemeColors(ColorUtils.getColor(this,R.color.aliceblue), ColorUtils.getColor(this,R.color.antiquewhite), ColorUtils.getColor(this,R.color.aqua),ColorUtils.getColor(this,R.color.aquamarine));
        mSwipeRefreshLayout.setOnRefreshListener(this);
        new InitialThread(id).start();
    }

    @Override
    public void onRefresh() {
        if(!isNetworkAvailable(this)) {
            Toast toast = Toast.makeText(this,"网络连接错误",Toast.LENGTH_SHORT);
            toast.setGravity(Gravity.BOTTOM, 0, 100);
            toast.show();
        }
        else{
            refresh();
        }
    }

    private class InitialThread extends Thread {

        private String id;

        InitialThread(String id) {
            this.id = id;
        }

        @Override
        public void run() {
            blog blog = BlogItemService.getBlogDetails(id);
            if(blog.getU_id() != 0) {
                Message msg = new Message();
                msg.obj = blog;
                msg.what = 1;
                handler.sendMessage(msg);
            }
            else {
                Message msg = new Message();
                msg.what = 0;
                handler.sendMessage(msg);
            }
        }
    }

    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if(msg.what == 0) {
                //show retry button when loading failed
//                final ProgressBar pb = (ProgressBar) findViewById(R.id.forums_details_progress_bar);
//                final TextView tv = (TextView) findViewById(R.id.forums_details_progress_text);
//                final Button btn = (Button) findViewById(R.id.retry);
//                assert pb != null;
//                assert tv != null;
//                assert btn != null;
//                btn.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        pb.setVisibility(View.VISIBLE);
//                        tv.setVisibility(View.VISIBLE);
//                        btn.setVisibility(View.INVISIBLE);
//                        InitialThread myThread = new InitialThread(id);
//                        myThread.start();
//                    }
//                });
//
//                pb.setVisibility(View.INVISIBLE);
//                tv.setVisibility(View.INVISIBLE);
//                btn.setVisibility(View.VISIBLE);
            }

            if(msg.what == 1) {

                //hide loading layout
                loadingRL.setVisibility(View.INVISIBLE);
//                loadedRL.setVisibility(View.VISIBLE);

                //loading data
                final blog blog = (blog) msg.obj;

                tv_author.setText(blog.getAuthor());
                tv_time.setText(blog.getTime());
                tv_content.setText(blog.getContent());

                civ_avatar.setImageURI("http://"+getIP()+"/HelloWeb/Upload/Avatar/"+blog.getAvatar());
                if(blog.getImg().contains(";")) {
                    Uri uri;
                    for(int a = 0; a < blog.getImg().split(";").length; a++) {
                        if(blog.getImg().split(";")[a].contains("gif")) {
                            uri = Uri.parse("http://" + getIP() + "/HelloWeb/Upload/Blog/" + blog.getImg().split(";")[a]);
                            DraweeController controller = Fresco.newDraweeControllerBuilder()
                                    .setUri(uri)
                                    .setAutoPlayAnimations(true)
                                    .build();
                            sdv_img.get(a).setController(controller);
                        }
                        else {
                            uri = Uri.parse("http://" + getIP() + "/HelloWeb/Upload/Blog/" + blog.getImg().split(";")[a]);
                            sdv_img.get(a).setImageURI(uri);
                        }
                    }
                }


                final LinearLayoutManager mLayoutManager = new LinearLayoutManager(getParent());
                adapter = new blogReplyViewAdapter(getParent(), null);

                mRecyclerView = (RecyclerView) findViewById(R.id.blog_reply_rv);
                assert mRecyclerView != null;
                mRecyclerView.setLayoutManager(mLayoutManager);
                mRecyclerView.setHasFixedSize(true);
                mRecyclerView.setAdapter(adapter);
                mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
//
                    @Override
                    public void onScrolled(RecyclerView recyclerView,int dx, int dy) {
                        int firstVisibleItems, visibleItemCount, totalItemCount;
                        visibleItemCount = mLayoutManager.getChildCount();
                        totalItemCount = mLayoutManager.getItemCount();
                        firstVisibleItems = mLayoutManager.findFirstVisibleItemPosition();

                        if(!isNetworkAvailable(getBaseContext())) {
                            Toast toast = Toast.makeText(getBaseContext(),"网络连接错误",Toast.LENGTH_SHORT);
                            toast.setGravity(Gravity.BOTTOM, 0, 100);
                            toast.show();
                            adapter.notifyItemRemoved(adapter.getItemCount());
                        }
                        else if ((visibleItemCount + firstVisibleItems) >= totalItemCount && !isLoading && adapter.getItemCount()>=10) {
                            // 判断点
                            isLoading = true;
                            new MyAsyncTaskGetBlogCommentItem().execute(page);
                        }
                    }
                });
                new MyAsyncTaskGetBlogCommentItem().execute(page);

            }
        }
    };

    public void refresh() {
        adapter.removeAll();
        page = 0;
        new MyAsyncTaskGetBlogCommentItem().execute(page);
    }

    private class MyAsyncTaskGetBlogCommentItem extends AsyncTask<Integer, String, List<blog_reply>> {
        @Override
        protected List<blog_reply> doInBackground(Integer... pages) {
            return BlogItemService.getBlogReplyItems(pages[0], id);
        }

        @Override
        protected void onPostExecute(List<blog_reply> blogReplyItems) {
            if(blogReplyItems.size()>0) {
                adapter.addBlogReplyItems(blogReplyItems);
                adapter.notifyDataSetChanged();
                page++;
            }
            mSwipeRefreshLayout.setRefreshing(false);
            isLoading = false;
            if(page>1 && blogReplyItems.size()==0) {
                adapter.notifyItemRemoved(adapter.getItemCount());
            }
        }
    }

}
