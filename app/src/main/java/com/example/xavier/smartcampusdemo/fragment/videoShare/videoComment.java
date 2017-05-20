package com.example.xavier.smartcampusdemo.fragment.videoShare;

import android.app.Activity;
import android.graphics.Rect;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.xavier.smartcampusdemo.adapter.videoCommentViewAdapter;
import com.example.xavier.smartcampusdemo.entity.video_reply;
import com.example.xavier.smartcampusdemo.R;
import com.example.xavier.smartcampusdemo.service.VideoItemService;
import com.example.xavier.smartcampusdemo.util.ColorUtils;

import java.util.List;

import static com.example.xavier.smartcampusdemo.util.NetUtil.NetUtil.isNetworkAvailable;

/**
 * Created by Xavier on 4/30/2017.
 *
 */

public class videoComment extends Fragment implements SwipeRefreshLayout.OnRefreshListener {
    String id ="";
    LinearLayoutManager mLayoutManager;
    private static videoCommentViewAdapter adapter;
    RecyclerView mRecyclerView;
    SwipeRefreshLayout mSwipeRefreshLayout;
    private boolean isLoading = true;
    private Activity activity;
    public static int page = 0;
    protected boolean isVisible;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.fragment_video_comment, container, false);
        this.activity = getActivity();
        return view;
    }
    private class MyItemDecoration extends RecyclerView.ItemDecoration {
        /**
         *
         * @param outRect 边界
         * @param view recyclerView ItemView
         * @param parent recyclerView
         * @param state recycler 内部数据管理
         */
        @Override
        public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
            //设定底部边距为1px
            outRect.set(0, 0, 0, 20);
        }
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        id = activity.getIntent().getStringExtra("url");
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
        adapter.removeAll();
        page = 0;
        new MyAsyncTaskGetVideoCommentItem().execute(page);
        super.onActivityCreated(savedInstanceState);
    }



    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if(getUserVisibleHint()) {
            isVisible = true;
            onVisible();
        } else {
            isVisible = false;
            onInvisible();
        }
    }
    protected void onVisible(){
    }
    protected void onInvisible(){}

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

    private class MyAsyncTaskGetVideoCommentItem extends AsyncTask<Integer, String, List<video_reply>> {
        @Override
        protected List<video_reply> doInBackground(Integer... pages) {
            return VideoItemService.getVideoReplyItems(pages[0], id);
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
}
