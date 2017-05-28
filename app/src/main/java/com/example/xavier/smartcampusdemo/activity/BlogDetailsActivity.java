package com.example.xavier.smartcampusdemo.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.example.xavier.smartcampusdemo.R;
import com.example.xavier.smartcampusdemo.adapter.blogDetailsViewAdapter;
import com.example.xavier.smartcampusdemo.entity.blog;
import com.example.xavier.smartcampusdemo.entity.blog_reply;
import com.example.xavier.smartcampusdemo.service.BlogItemService;
import com.example.xavier.smartcampusdemo.util.ColorUtils;
import com.example.xavier.smartcampusdemo.util.DisplayUtils;

import java.util.List;

import me.imid.swipebacklayout.lib.app.SwipeBackActivity;

import static android.view.ViewGroup.LayoutParams.WRAP_CONTENT;

/**
 * Created by Xavier on 5/1/2017.
 *
 */

public class BlogDetailsActivity extends SwipeBackActivity implements View.OnClickListener {

    private static blogDetailsViewAdapter adapter;
    blog blog;
    EditText et_reply_content;
    ImageView iv_reply_send;
    View popView;
    PopupWindow popWindow;
    Activity activity;
    private SharedPreferences sharedPreferences;
    private Toolbar toolbar;
    private String bid, uid, content;
    private String to_send = "";
    private boolean isLoading = true;
    private boolean noMore = false;
    private Integer page = 0;
    private TextView tv_comment_count;
    private TextView tv_like_count;
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == 1) {
                tv_comment_count.setText(String.valueOf(blog.getReply_count()));
                tv_like_count.setText(String.valueOf(blog.getLike()));
                final LinearLayout tvStickyHeaderView = (LinearLayout) findViewById(R.id.blog_reply_sticker);
                //tvStickyHeaderView.setOnClickListener(BlogDetailsActivity.this);

//                tv_author.setText(blog.getAuthor());
//                tv_time.setText(blog.getTime());
//                tv_content.setText(blog.getContent());
//
//                civ_avatar.setImageURI("http://"+getIP()+"/HelloWeb/Upload/Avatar/"+blog.getAvatar());
//                if(blog.getImg().contains(";")) {
//                    Uri uri;
//                    for(int a = 0; a < blog.getImg().split(";").length; a++) {
//                        if(blog.getImg().split(";")[a].contains("gif")) {
//                            uri = Uri.parse("http://" + getIP() + "/HelloWeb/Upload/Blog/" + blog.getImg().split(";")[a]);
//                            DraweeController controller = Fresco.newDraweeControllerBuilder()
//                                    .setUri(uri)
//                                    .setAutoPlayAnimations(true)
//                                    .build();
//                            sdv_img.get(a).setController(controller);
//                        }
//                        else {
//                            uri = Uri.parse("http://" + getIP() + "/HelloWeb/Upload/Blog/" + blog.getImg().split(";")[a]);
//                            sdv_img.get(a).setImageURI(uri);
//                        }
//                    }
//                }

                final LinearLayoutManager mLayoutManager = new LinearLayoutManager(activity);
                adapter = new blogDetailsViewAdapter(activity, null);

                RecyclerView mRecyclerView = (RecyclerView) findViewById(R.id.blog_reply_rv);
                mRecyclerView.setLayoutManager(mLayoutManager);
                mRecyclerView.setHasFixedSize(true);
                mRecyclerView.setAdapter(adapter);

                mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
                    //
                    @Override
                    public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                        int firstVisibleItems, visibleItemCount, totalItemCount;
                        visibleItemCount = mLayoutManager.getChildCount();
                        totalItemCount = mLayoutManager.getItemCount();
                        firstVisibleItems = mLayoutManager.findFirstVisibleItemPosition();

                        View stickyInfoView = recyclerView.findChildViewUnder(
                                tvStickyHeaderView.getMeasuredWidth() / 2, 5);

                        if (stickyInfoView != null && stickyInfoView.getContentDescription() != null) {

                        }

                        View transInfoView = recyclerView.findChildViewUnder(
                                tvStickyHeaderView.getMeasuredWidth() / 2, tvStickyHeaderView.getMeasuredHeight() + 1);

                        if (transInfoView != null && transInfoView.getTag() != null) {

                            int transViewStatus = (int) transInfoView.getTag();
                            int dealtY = transInfoView.getTop() - tvStickyHeaderView.getMeasuredHeight();

                            if (transViewStatus == blogDetailsViewAdapter.HAS_STICKY_VIEW) {
                                if (transInfoView.getTop() > 0) {
                                    tvStickyHeaderView.setTranslationY(dealtY);
                                    if (tvStickyHeaderView.getVisibility() == View.VISIBLE)
                                        tvStickyHeaderView.setVisibility(View.GONE);
                                } else {
                                    tvStickyHeaderView.setTranslationY(0);
                                    if (tvStickyHeaderView.getVisibility() == View.GONE)
                                        tvStickyHeaderView.setVisibility(View.VISIBLE);
                                }
                            } else if (transViewStatus == blogDetailsViewAdapter.NONE_STICKY_VIEW) {
                                tvStickyHeaderView.setTranslationY(0);
                            } else if (transViewStatus == blogDetailsViewAdapter.FIRST_STICKY_VIEW) {
                                if (tvStickyHeaderView.getVisibility() == View.VISIBLE)
                                    tvStickyHeaderView.setVisibility(View.GONE);
                            }
                        }

                        if ((visibleItemCount + firstVisibleItems) >= totalItemCount && !isLoading) {
                            // 判断点
                            if (!noMore) {
                                isLoading = true;
                                new MyAsyncTaskGetBlogCommentItem().execute(page);
                            }
                        }
                    }
                });
                adapter.setDetailsObject(blog);
                new MyAsyncTaskGetBlogCommentItem().execute(page);
            }
        }
    };

    boolean isLogged() {
        sharedPreferences = getSharedPreferences("USER_INFO", Context.MODE_PRIVATE);
        return !sharedPreferences.getAll().isEmpty();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_blog_details);

        activity = getParent();
        bid = getIntent().getStringExtra("bid");
        sharedPreferences = getSharedPreferences("USER_INFO", Context.MODE_PRIVATE);
        uid = sharedPreferences.getString("userID","");

        new InitialThread().start();

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

        popView = View.inflate(this, R.layout.activity_forum_reply_popup, null);
        tv_comment_count = (TextView) findViewById(R.id.blog_details_comment_count);
        tv_like_count = (TextView) findViewById(R.id.blog_details_like_count);
        et_reply_content = (EditText) popView.findViewById(R.id.forum_reply_content);
        iv_reply_send = (ImageView) popView.findViewById(R.id.forum_reply_send);

        textChange tc = new textChange();
        et_reply_content.addTextChangedListener(tc);
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab3);
        iv_reply_send.setOnClickListener(this);
        fab.setOnClickListener(this);

        initPopupWindow();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.fab3:
            case R.id.blog_reply_sticker:
                if (!isLogged()) {
                    new AlertDialog.Builder(BlogDetailsActivity.this).setTitle("该功能需登录方可使用！")
                            .setIcon(android.R.drawable.ic_dialog_info)
                            .setPositiveButton("立即登录", new DialogInterface.OnClickListener() {

                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    SignInActivity.actionStart(BlogDetailsActivity.this);
                                }
                            })
                            .setNegativeButton("以后再说", new DialogInterface.OnClickListener() {

                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                }
                            }).show();
                }
                else showPopupWindow();
                break;
            case R.id.forum_reply_send:
                content = et_reply_content.getText().toString();
                new MyAsyncTaskPostReplyItem().execute();
                break;
        }
    }

    void initPopupWindow() {
        int width = getResources().getDisplayMetrics().widthPixels;
        int height = getResources().getDisplayMetrics().heightPixels;

        popWindow = new PopupWindow(popView,width,WRAP_CONTENT);
        popWindow.setAnimationStyle(R.style.popup_window_anim);
        popWindow.setFocusable(true);
        popWindow.setOutsideTouchable(false);// 设置允许在外点击消失
        popWindow.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        ColorDrawable dw = new ColorDrawable(0x30000000);
        popWindow.setBackgroundDrawable(dw);
        popWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                to_send = et_reply_content.getText().toString();
            }
        });
    }

    private void showPopupWindow() {
        View parent = ((ViewGroup) this.findViewById(android.R.id.content)).getChildAt(0);

        et_reply_content.setText(to_send);
        et_reply_content.requestFocus();
        et_reply_content.setSelection(et_reply_content.getText().length());

        popWindow.showAtLocation(parent, Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0);
    }

    void refresh() {
        tv_comment_count.setText(String.valueOf(blog.getReply_count()));
        tv_like_count.setText(String.valueOf(blog.getLike()));
        isLoading = true;
        adapter.removeAll();
        adapter.setDetailsObject(blog);
        noMore = false;
        page = 0;
        new MyAsyncTaskGetBlogCommentItem().execute(page);
    }

    private class textChange implements TextWatcher {

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
        }

        @Override
        public void afterTextChanged(Editable s) {
            if (s.toString().length() > 0) {
                iv_reply_send.setBackgroundColor(ColorUtils.getColor(BlogDetailsActivity.this, R.color.customblue));
                iv_reply_send.setClickable(true);
            } else {
                iv_reply_send.setBackgroundColor(ColorUtils.getColor(BlogDetailsActivity.this, R.color.gainsboro));
                iv_reply_send.setClickable(false);
            }
        }
    }

    private class InitialThread extends Thread {

        @Override
        public void run() {
            blog = BlogItemService.getBlogDetails(bid);
            if(blog.getU_id() != 0) {
                Message msg = new Message();
                msg.what = 1;
                handler.sendMessage(msg);
            }
        }
    }

    private class MyAsyncTaskGetBlogCommentItem extends AsyncTask<Integer, String, List<blog_reply>> {
        @Override
        protected List<blog_reply> doInBackground(Integer... pages) {
            return BlogItemService.getBlogReplyItems(pages[0], bid);
        }

        @Override
        protected void onPostExecute(List<blog_reply> blogReplyItems) {
            if(blogReplyItems.size()>0) {
                adapter.addBlogReplyItems(blogReplyItems);
                adapter.notifyDataSetChanged();
                page++;
            }
            if(blogReplyItems.size()<10 && !noMore) {
                adapter.notifyEnd();
                noMore = true;
                if(blogReplyItems.size() == 0)
                    adapter.notifyDataSetChanged();
            }
            isLoading = false;
        }
    }

    private class MyAsyncTaskPostReplyItem extends AsyncTask<Integer, String, String> {
        @Override
        protected String doInBackground(Integer... pages) {
            blog_reply blog_reply = new blog_reply();
            blog_reply.setU_id(Integer.parseInt(uid));
            blog_reply.setContent(content);
            blog_reply.setB_id(Integer.parseInt(bid));
            String state = BlogItemService.executePost(blog_reply);
            if(state.equals("评论成功"))
                blog = BlogItemService.getBlogDetails(bid);
            return state;
        }

        @Override
        protected void onPostExecute(String state) {
            if (!state.equals("评论成功")) {
                DisplayUtils.customCenterShortToast(BlogDetailsActivity.this, "评论失败", 0, 0);
            } else {
                popWindow.dismiss();
                to_send = "";
                refresh();
            }
        }

    }
}
