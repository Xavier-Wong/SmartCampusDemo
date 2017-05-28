package com.example.xavier.smartcampusdemo.fragment.videoShare;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Rect;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.Toast;

import com.example.xavier.smartcampusdemo.R;
import com.example.xavier.smartcampusdemo.activity.SignInActivity;
import com.example.xavier.smartcampusdemo.adapter.videoCommentViewAdapter;
import com.example.xavier.smartcampusdemo.entity.video_reply;
import com.example.xavier.smartcampusdemo.service.VideoItemService;
import com.example.xavier.smartcampusdemo.util.ColorUtils;
import com.example.xavier.smartcampusdemo.util.DisplayUtils;

import java.util.List;

import static android.view.ViewGroup.LayoutParams.WRAP_CONTENT;
import static com.example.xavier.smartcampusdemo.util.NetUtil.NetUtil.isNetworkAvailable;

/**
 * Created by Xavier on 4/30/2017.
 *
 */

public class videoComment extends Fragment implements SwipeRefreshLayout.OnRefreshListener, View.OnClickListener {

    public static int page = 0;
    private static videoCommentViewAdapter adapter;
    EditText et_reply_content;
    ImageView iv_reply_send;
    View popView;
    PopupWindow popWindow;
    LinearLayoutManager mLayoutManager;
    View replyEntry;
    RecyclerView mRecyclerView;
    SwipeRefreshLayout mSwipeRefreshLayout;
    private SharedPreferences sharedPreferences;
    private String vid, uid, content;
    private boolean isLoading = true;
    private Activity activity;
    private String to_send = "";

    boolean isLogged() {
        sharedPreferences = activity.getSharedPreferences("USER_INFO", Context.MODE_PRIVATE);
        return !sharedPreferences.getAll().isEmpty();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.fragment_video_comment, container, false);
        this.activity = getActivity();
        return view;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.video_reply_entry:
                if (!isLogged()) {
                    new AlertDialog.Builder(activity).setTitle("该功能需登录方可使用！")
                            .setIcon(android.R.drawable.ic_dialog_info)
                            .setPositiveButton("立即登录", new DialogInterface.OnClickListener() {

                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    SignInActivity.actionStart(activity);
                                }
                            })
                            .setNegativeButton("以后再说", new DialogInterface.OnClickListener() {

                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                }
                            }).show();
                } else if (replyEntry.getVisibility() == View.VISIBLE) {
                    replyEntry.setVisibility(View.GONE);
                    showPopupWindow();
                }
                break;
            case R.id.video_reply_send:
                content = et_reply_content.getText().toString();
                new MyAsyncTaskPostReplyItem().execute();
                break;
        }
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        sharedPreferences = activity.getSharedPreferences("USER_INFO", Context.MODE_PRIVATE);
        uid = sharedPreferences.getString("userID", "");

        vid = activity.getIntent().getStringExtra("url");
        replyEntry = activity.findViewById(R.id.video_reply_entry);
        popView = View.inflate(activity, R.layout.video_reply_popup, null);
        initPopupWindow();
        replyEntry.setOnClickListener(this);
        mRecyclerView = (RecyclerView) activity.findViewById(R.id.video_comment_rv);
        mLayoutManager = new LinearLayoutManager(activity);
        mSwipeRefreshLayout = (SwipeRefreshLayout) activity.findViewById(R.id.video_comment_swipe_refresh);
        mSwipeRefreshLayout.setColorSchemeColors(ColorUtils.getColor(this,R.color.aliceblue), ColorUtils.getColor(this,R.color.antiquewhite), ColorUtils.getColor(this,R.color.aqua),ColorUtils.getColor(this,R.color.aquamarine));
        mSwipeRefreshLayout.setOnRefreshListener(this);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setHasFixedSize(true);
        adapter = new videoCommentViewAdapter(activity, null);
        mRecyclerView.addItemDecoration(new videoComment.MyItemDecoration());
        mRecyclerView.setAdapter(adapter);
        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {

            @Override
            public void onScrolled(RecyclerView recyclerView,int dx, int dy) {
                int firstVisibleItems, visibleItemCount, totalItemCount;
                visibleItemCount = mLayoutManager.getChildCount();
                totalItemCount = mLayoutManager.getItemCount();
                firstVisibleItems = mLayoutManager.findFirstVisibleItemPosition();

                if(!isNetworkAvailable(activity)) {
                    Toast toast = Toast.makeText(activity,"网络连接错误",Toast.LENGTH_SHORT);
                    toast.setGravity(Gravity.BOTTOM, 0, 100);
                    toast.show();
                    adapter.notifyItemRemoved(adapter.getItemCount());
                }
                else if ((visibleItemCount + firstVisibleItems) >= totalItemCount && !isLoading && adapter.getItemCount()>=10) {
                    // 判断点
                    isLoading = true;
                    new MyAsyncTaskGetVideoCommentItem().execute(page);
                }
            }
        });
        et_reply_content = (EditText) popView.findViewById(R.id.video_reply_content);
        iv_reply_send = (ImageView) popView.findViewById(R.id.video_reply_send);
        iv_reply_send.setOnClickListener(this);
        textChange tc = new textChange();
        et_reply_content.addTextChangedListener(tc);
        adapter.removeAll();
        page = 0;
        new MyAsyncTaskGetVideoCommentItem().execute(page);
        super.onActivityCreated(savedInstanceState);
    }

    void initPopupWindow() {
        int width = getResources().getDisplayMetrics().widthPixels;
        int height = getResources().getDisplayMetrics().heightPixels;

        popWindow = new PopupWindow(popView, width, WRAP_CONTENT);
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
                replyEntry.setVisibility(View.VISIBLE);
            }
        });
    }

    private void showPopupWindow() {
        View parent = ((ViewGroup) activity.findViewById(android.R.id.content)).getChildAt(0);

        et_reply_content.setText(to_send);
        et_reply_content.requestFocus();
        et_reply_content.setSelection(et_reply_content.getText().length());

        popWindow.showAtLocation(parent, Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0);
    }

    @Override
    public void onRefresh() {
        if(!isNetworkAvailable(activity)) {
            Toast toast = Toast.makeText(activity,"网络连接错误",Toast.LENGTH_SHORT);
            toast.setGravity(Gravity.BOTTOM, 0, 100);
            toast.show();
        }
        else{
            refresh();
        }
    }

    public void refresh() {
        isLoading = true;
        adapter.removeAll();
        page = 0;
        new MyAsyncTaskGetVideoCommentItem().execute(page);
    }

    private class MyItemDecoration extends RecyclerView.ItemDecoration {
        /**
         * @param outRect 边界
         * @param view    recyclerView ItemView
         * @param parent  recyclerView
         * @param state   recycler 内部数据管理
         */
        @Override
        public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
            //设定底部边距为1px
            outRect.set(0, 0, 0, 20);
        }
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
                iv_reply_send.setBackgroundColor(ColorUtils.getColor(activity, R.color.customblue));
                iv_reply_send.setClickable(true);
            } else {
                iv_reply_send.setBackgroundColor(ColorUtils.getColor(activity, R.color.gainsboro));
                iv_reply_send.setClickable(false);
            }
        }
    }

    private class MyAsyncTaskGetVideoCommentItem extends AsyncTask<Integer, String, List<video_reply>> {
        @Override
        protected List<video_reply> doInBackground(Integer... pages) {
            return VideoItemService.getVideoReplyItems(pages[0], vid);
        }

        @Override
        protected void onPostExecute(List<video_reply> videoCommentItems) {
            if(videoCommentItems.size()>0) {
                adapter.addVideoReplyItems(videoCommentItems);
                adapter.notifyDataSetChanged();
                page++;
            }
            mSwipeRefreshLayout.setRefreshing(false);
            isLoading = false;
            if(page>1 && videoCommentItems.size()==0) {
                adapter.notifyItemRemoved(adapter.getItemCount());
            }
        }
    }

    private class MyAsyncTaskPostReplyItem extends AsyncTask<Integer, String, String> {
        @Override
        protected String doInBackground(Integer... pages) {
            video_reply video_reply = new video_reply();
            video_reply.setU_id(Integer.parseInt(uid));
            video_reply.setContent(content);
            video_reply.setV_id(Integer.parseInt(vid));
            return VideoItemService.executePost(video_reply);
        }

        @Override
        protected void onPostExecute(String state) {
            if (!state.equals("评论成功")) {
                DisplayUtils.customCenterShortToast(activity, "评论失败", 0, 0);
            } else {
                popWindow.dismiss();
                to_send = "";
                refresh();
            }
        }

    }
}
