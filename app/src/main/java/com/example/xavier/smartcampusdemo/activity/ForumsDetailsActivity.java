package com.example.xavier.smartcampusdemo.activity;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
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
import com.example.xavier.smartcampusdemo.adapter.techForumDetailsViewAdapter;
import com.example.xavier.smartcampusdemo.entity.forum;
import com.example.xavier.smartcampusdemo.entity.forum_reply;
import com.example.xavier.smartcampusdemo.service.ForumItemService;
import com.example.xavier.smartcampusdemo.util.ColorUtils;
import com.example.xavier.smartcampusdemo.util.DisplayUtils;
import com.facebook.drawee.view.SimpleDraweeView;

import java.util.List;

import me.imid.swipebacklayout.lib.app.SwipeBackActivity;

import static android.view.ViewGroup.LayoutParams.WRAP_CONTENT;
import static com.example.xavier.smartcampusdemo.service.NetService.getAvatarPath;

/**
 * Created by Xavier on 2/27/2017.
 *
 */

public class ForumsDetailsActivity extends SwipeBackActivity implements View.OnClickListener{

    SharedPreferences sharedPreferences;
    View toobar_view;
    TextView toobar_title;
    SimpleDraweeView toobar_avatar;
    EditText et_reply_content;
    ImageView iv_reply_send;
    View popView;
    PopupWindow popWindow;
    Activity activity;
    forum forum;
    private techForumDetailsViewAdapter adapter;
    private Integer page = 0;
    private String fid, uid, content;
    private String to_send = "";
    private boolean isLoading = true;
    private boolean noMore = false;

    private CollapsingToolbarLayoutState state;
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == 1) {
                SimpleDraweeView col_avatar = (SimpleDraweeView) findViewById(R.id.forum_collapsing_avatar);
                col_avatar.setImageURI(getAvatarPath() + forum.getAvatar());
                toobar_avatar.setImageURI(getAvatarPath() + forum.getAvatar());
                toobar_title.setText(forum.getAuthor());

                startCollapsing();

                final LinearLayout tvStickyHeaderView = (LinearLayout) findViewById(R.id.forum_reply_sticker);
                tvStickyHeaderView.setOnClickListener(ForumsDetailsActivity.this);

                final LinearLayoutManager mLayoutManager = new LinearLayoutManager(activity);
                adapter = new techForumDetailsViewAdapter(activity, null);

                RecyclerView mRecyclerView = (RecyclerView) findViewById(R.id.techForumDetails_rv);
                mRecyclerView.setLayoutManager(mLayoutManager);
                mRecyclerView.setHasFixedSize(true);
                mRecyclerView.setAdapter(adapter);

                mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {

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

                            if (transViewStatus == techForumDetailsViewAdapter.HAS_STICKY_VIEW) {
                                if (transInfoView.getTop() > 0) {
                                    tvStickyHeaderView.setTranslationY(dealtY);
                                    if (tvStickyHeaderView.getVisibility() == View.VISIBLE)
                                        tvStickyHeaderView.setVisibility(View.GONE);
                                } else {
                                    tvStickyHeaderView.setTranslationY(0);
                                    if (tvStickyHeaderView.getVisibility() == View.GONE)
                                        tvStickyHeaderView.setVisibility(View.VISIBLE);
                                }
                            } else if (transViewStatus == techForumDetailsViewAdapter.NONE_STICKY_VIEW) {
                                tvStickyHeaderView.setTranslationY(0);
                            } else if (transViewStatus == techForumDetailsViewAdapter.FIRST_STICKY_VIEW) {
                                if (tvStickyHeaderView.getVisibility() == View.VISIBLE)
                                    tvStickyHeaderView.setVisibility(View.GONE);
                            }
                        }

                        if ((visibleItemCount + firstVisibleItems) >= totalItemCount && !isLoading) {
                            // 判断点
                            if (!noMore) {
                                isLoading = true;
                                new MyAsyncTaskGetForumReplyItem().execute(page);
                            }
                        }

                    }
                });

                adapter.setDetailsObject(forum);
                new MyAsyncTaskGetForumReplyItem().execute(page);
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
        setContentView(R.layout.activity_forums_details);

        activity = getParent();

        fid = getIntent().getStringExtra("url");
        sharedPreferences = getSharedPreferences("USER_INFO", Context.MODE_PRIVATE);
        uid = sharedPreferences.getString("userID","");

        new InitialThread().start();

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

        toobar_avatar = (SimpleDraweeView) findViewById(R.id.forum_toolbar_avatar);
        toobar_title = (TextView) findViewById(R.id.forum_toolbar_author);
        toobar_view = findViewById(R.id.forum_toolbar_info);

        popView = View.inflate(this, R.layout.activity_forum_reply_popup, null);
        et_reply_content = (EditText) popView.findViewById(R.id.forum_reply_content);
        iv_reply_send = (ImageView) popView.findViewById(R.id.forum_reply_send);

        textChange tc = new textChange();
        et_reply_content.addTextChangedListener(tc);
        FloatingActionButton fab1 = (FloatingActionButton) findViewById(R.id.fab1);

        iv_reply_send.setOnClickListener(ForumsDetailsActivity.this);
        fab1.setOnClickListener(this);

        initPopupWindow();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.fab1:
            case R.id.forum_reply_sticker:
                if (!isLogged()) {
                    new AlertDialog.Builder(ForumsDetailsActivity.this).setTitle("该功能需登录方可使用！")
                            .setIcon(android.R.drawable.ic_dialog_info)
                            .setPositiveButton("立即登录", new DialogInterface.OnClickListener() {

                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    SignInActivity.actionStart(ForumsDetailsActivity.this);
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

    private void startCollapsing() {
        final CollapsingToolbarLayout collapsingToolbarLayout = (CollapsingToolbarLayout) findViewById(R.id.forum_collapsing_layout);
        AppBarLayout app_bar = (AppBarLayout) findViewById(R.id.forum_app_bar);
        app_bar.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                if (verticalOffset == 0) {
                    if (state != CollapsingToolbarLayoutState.EXPANDED) {
                        state = CollapsingToolbarLayoutState.EXPANDED;//修改状态标记为展开
                        collapsingToolbarLayout.setTitle(forum.getAuthor());//设置title为EXPANDED
                    }
                } else if (Math.abs(verticalOffset) >= appBarLayout.getTotalScrollRange()) {
                    collapsingToolbarLayout.setTitle("");//设置title不显示
                    collapsingToolbarLayout.setBackgroundColor(ColorUtils.getColor(ForumsDetailsActivity.this, R.color.customblue));
                    if (state != CollapsingToolbarLayoutState.COLLAPSED) {
                        toobar_view.setVisibility(View.VISIBLE);
                        state = CollapsingToolbarLayoutState.COLLAPSED;//修改状态标记为折叠
                    }
                } else {
                    if (state != CollapsingToolbarLayoutState.INTERNEDIATE) {
                        if(state == CollapsingToolbarLayoutState.COLLAPSED){
                            toobar_view.setVisibility(View.GONE);
                        }
                        collapsingToolbarLayout.setTitle(forum.getAuthor());//设置title为INTERNEDIATE
                        state = CollapsingToolbarLayoutState.INTERNEDIATE;//修改状态标记为中间
                    }
                }
            }
        });
    }

    void refresh() {
        isLoading = true;
        adapter.removeAll();
        noMore = false;
        page = 0;
        new MyAsyncTaskGetForumReplyItem().execute(page);
    }

    private enum CollapsingToolbarLayoutState {
        EXPANDED,
        COLLAPSED,
        INTERNEDIATE
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
                iv_reply_send.setBackgroundColor(ColorUtils.getColor(ForumsDetailsActivity.this, R.color.customblue));
                iv_reply_send.setClickable(true);
            } else {
                iv_reply_send.setBackgroundColor(ColorUtils.getColor(ForumsDetailsActivity.this, R.color.gainsboro));
                iv_reply_send.setClickable(false);
            }
        }
    }

    private class InitialThread extends Thread {

        @Override
        public void run() {
            forum = ForumItemService.getForumItem(fid);
            if (forum.getU_id() != 0) {
                Message msg = new Message();
                msg.what = 1;
                handler.sendMessage(msg);
            }
        }
    }

    private class MyAsyncTaskGetForumReplyItem extends AsyncTask<Integer, String, List<forum_reply>> {
        @Override
        protected List<forum_reply> doInBackground(Integer... pages) {
            return ForumItemService.getForumReplyItems(pages[0], fid);
        }

        @Override
        protected void onPostExecute(List<forum_reply> forumItems) {
            if(forumItems.size()>0) {
                adapter.addForumReplyItem(forumItems);
                adapter.notifyDataSetChanged();
                page ++;
            }
            if(forumItems.size()<10 && !noMore) {
                adapter.notifyEnd();
                noMore = true;
            }
            isLoading = false;
        }

    }

    private class MyAsyncTaskPostReplyItem extends AsyncTask<Integer, String, String> {
        @Override
        protected String doInBackground(Integer... pages) {
            forum_reply forum_reply = new forum_reply();
            forum_reply.setU_id(Integer.parseInt(uid));
            forum_reply.setContent(content);
            forum_reply.setF_id(Integer.parseInt(fid));
            return ForumItemService.executePost(forum_reply);
        }

        @Override
        protected void onPostExecute(String state) {
            if (!state.equals("评论成功")) {
                DisplayUtils.customCenterShortToast(ForumsDetailsActivity.this, "评论失败", 0, 0);
            } else {
                popWindow.dismiss();
                to_send = "";
                refresh();
            }
        }

    }

}
