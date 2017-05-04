package com.example.xavier.smartcampusdemo.Activity;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.xavier.smartcampusdemo.Adapter.techForumDetailsViewAdapter;
import com.example.xavier.smartcampusdemo.Entity.forum_reply;
import com.example.xavier.smartcampusdemo.R;
import com.example.xavier.smartcampusdemo.Service.ForumItemService;
import com.example.xavier.smartcampusdemo.Util.JSONUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import me.imid.swipebacklayout.lib.app.SwipeBackActivity;

import static com.example.xavier.smartcampusdemo.Service.NetService.getIP;

/**
 * Created by Xavier on 2/27/2017.
 *
 */

public class ForumsDetailsActivity extends SwipeBackActivity{

    private Toolbar toolbar;
    private techForumDetailsViewAdapter adapter;
    private SharedPreferences sharedPreferences;

    RecyclerView mRecyclerView;
    private RelativeLayout loadingRL;
    private View replywindowL;

    private Integer page = 1;
    private String id = "";
    private boolean isLoading = true;
    private String r_uid, r_content, r_fid;
    private List<forum_reply> forumReplyItems= new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forums_details);

        //fetch current forum's id
        id = getIntent().getStringExtra("url");

        //configuration of toolbar
        toolbar = (Toolbar) findViewById(R.id.forums_details_toolbar);
        assert toolbar != null;
        toolbar.setTitle("");
        toolbar.setTitleTextColor(Color.BLACK);
        if (toolbar != null) {
            setSupportActionBar(toolbar);
        }
        toolbar.setNavigationIcon(R.drawable.ic_back);
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

        loadingRL = (RelativeLayout) findViewById(R.id.forums_details_is_loading);
        replywindowL = findViewById(R.id.techforum_reply_window);

        //Initial data of the forum details
        InitialThread initThread = new InitialThread(id);
        initThread.start();
    }

    private class InitialThread extends Thread {

        private String id;

        InitialThread(String id) {
            this.id = id;
        }

        @Override
        public void run() {
            String forum = ForumItemService.getForumsDetails(id).toString();
            JSONObject jsonObject = JSONUtil.getJsonObject(forum);
            try {
                //data succeed
                assert jsonObject != null;
                jsonObject.getString("u_id");
                Message msg = new Message();
                msg.obj = jsonObject;
                msg.what = 1;
                handler.sendMessage(msg);
            }catch (Exception e) {
                //data failed
                Message msg = new Message();
                msg.what = 0;
                handler.sendMessage(msg);
                e.printStackTrace();
            }
        }
    }

    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if(msg.what == 0 ) {
                //show retry button when loading failed
                final ProgressBar pb = (ProgressBar) findViewById(R.id.forums_details_progress_bar);
                final TextView tv = (TextView) findViewById(R.id.forums_details_progress_text);
                final Button btn = (Button) findViewById(R.id.retry);
                assert pb != null;
                assert tv != null;
                assert btn != null;
                btn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        pb.setVisibility(View.VISIBLE);
                        tv.setVisibility(View.VISIBLE);
                        btn.setVisibility(View.INVISIBLE);
                        InitialThread myThread = new InitialThread(id);
                        myThread.start();
                    }
                });

                pb.setVisibility(View.INVISIBLE);
                tv.setVisibility(View.INVISIBLE);
                btn.setVisibility(View.VISIBLE);
            }

            if(msg.what == 1) {

                //hide loading layout
                replywindowL.setVisibility(View.VISIBLE);
                loadingRL.setVisibility(View.INVISIBLE);

                //loading data
                new MyAsyncTaskGetForumReplyItem().execute(page);
                final JSONObject forumItem = (JSONObject) msg.obj;

                final LinearLayoutManager mLayoutManager = new LinearLayoutManager(getParent());
                adapter = new techForumDetailsViewAdapter(getParent(), null);
                adapter.setDetailsObject(forumItem);
                adapter.addForumReplyItem(forumReplyItems);
                adapter.notifyDataSetChanged();

                mRecyclerView = (RecyclerView) findViewById(R.id.techForumDetails_rv);
                assert mRecyclerView != null;
                mRecyclerView.setLayoutManager(mLayoutManager);
                mRecyclerView.setHasFixedSize(true);
                mRecyclerView.setAdapter(adapter);
                mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {

                    @Override
                    public void onScrolled(RecyclerView recyclerView,int dx, int dy) {
                        int firstVisibleItems, visibleItemCount, totalItemCount;

                        visibleItemCount = mLayoutManager.getChildCount();
                        totalItemCount = mLayoutManager.getItemCount();
                        firstVisibleItems = mLayoutManager.findFirstVisibleItemPosition();

                        if ((visibleItemCount + firstVisibleItems) >= totalItemCount && !isLoading) {
                            // 判断点
                            isLoading = true;
                            new MyAsyncTaskGetForumReplyItem().execute(page);
                        }

                        if (firstVisibleItems == 0 || firstVisibleItems == 1) {
                            toolbar.setTitle("");
                        }
                        else {
                            try {
                                toolbar.setTitle(forumItem.getString("author"));
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                });

                //initial comment area
                final EditText reply_commit_edt = (EditText) findViewById(R.id.forum_reply_content);
                Button reply_commit_btn = (Button) findViewById(R.id.forum_reply_commit);
                assert reply_commit_edt != null;
                assert reply_commit_btn != null;
                reply_commit_btn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(reply_commit_edt.getText().toString().isEmpty()) {
                            Toast toast = Toast.makeText(ForumsDetailsActivity.this, "不能发布空回复" ,Toast.LENGTH_SHORT);
                            toast.setGravity(Gravity.CENTER, 0, 0);
                            toast.show();
                        }
                        else {
                            reply_commit_edt.clearFocus();
                            InputMethodManager imm = (InputMethodManager) ForumsDetailsActivity.this.getSystemService(Context.INPUT_METHOD_SERVICE);
                            imm.hideSoftInputFromWindow(reply_commit_edt.getWindowToken(), 0);
                            Toast toast = Toast.makeText(ForumsDetailsActivity.this, "发布成功" ,Toast.LENGTH_SHORT);
                            toast.setGravity(Gravity.CENTER, 0, 0);
                            toast.show();
                            sharedPreferences = getSharedPreferences("USER_INFO", Context.MODE_PRIVATE);
                            r_uid = sharedPreferences.getString("userID","");
                            r_content = reply_commit_edt.getText().toString();
                            r_fid = id;
                            new Thread(new ReplyThread()).start();
                            adapter.removeAll();
                            page=1;
                            new MyAsyncTaskGetForumReplyItem().execute(page);
                            reply_commit_edt.setText("");
                        }
                    }
                });
            }
        }
    };

    private class ReplyThread implements Runnable {

        @Override
        public void run() {
            Properties properties = System.getProperties();
            properties.list(System.out);
            String url = "http://" + getIP() + "/HelloWeb/ForumPublishLet";
            try {
                HttpURLConnection conn = (HttpURLConnection) new URL(url).openConnection();
                conn.setReadTimeout(3000);
                conn.setRequestMethod("POST");

                conn.setDoOutput(true);
                conn.setDoInput(true);
                PrintWriter printWriter = new PrintWriter(conn.getOutputStream());
                String post = "action=2&uid="+r_uid+"&content="+r_content+"&fid="+r_fid;
                printWriter.write(post);
                printWriter.flush();
                BufferedInputStream bis = new BufferedInputStream(conn.getInputStream());
                ByteArrayOutputStream bos = new ByteArrayOutputStream();
                int len;
                byte[] arr = new byte[1024];
                while ((len=bis.read(arr))!= -1) {
                    bos.write(arr,0,len);
                    bos.flush();
                }
                bos.close();

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private class MyAsyncTaskGetForumReplyItem extends AsyncTask<Integer, String, List<forum_reply>> {
        @Override
        protected List<forum_reply> doInBackground(Integer... pages) {
            return ForumItemService.getForumReplyItems(pages[0], id);
        }

        @Override
        protected void onPostExecute(List<forum_reply> forumItems) {
            if(forumItems.size()>0) {
                adapter.addForumReplyItem(forumItems);
                adapter.notifyDataSetChanged();
                page ++;
            }

            isLoading = false;
            if(page>1 && forumItems.size()==0) {
                adapter.notifyItemRemoved(adapter.getItemCount());
            }
        }

    }
}
