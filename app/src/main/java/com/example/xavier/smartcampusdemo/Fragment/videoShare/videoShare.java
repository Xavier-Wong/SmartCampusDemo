package com.example.xavier.smartcampusdemo.Fragment.videoShare;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Layout;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.xavier.smartcampusdemo.Adapter.videoShareViewAdapter;
import com.example.xavier.smartcampusdemo.Entity.video;
import com.example.xavier.smartcampusdemo.R;
import com.example.xavier.smartcampusdemo.Service.VideoItemService;
import com.example.xavier.smartcampusdemo.Util.ColorUtils;

import java.util.List;

import static com.example.xavier.smartcampusdemo.Util.NetUtil.NetUtil.isNetworkAvailable;

/**
 * Created by Xavier on 11/7/2016.
 * videoShareFragment
 */

public class videoShare extends Fragment implements SwipeRefreshLayout.OnRefreshListener {
    View layout_loading;

    videoShareViewAdapter adapter;
    RecyclerView mRecyclerView;

    GridLayoutManager mLayoutManager;
    SwipeRefreshLayout mSwipeRefreshLayout;
    private boolean isLoading = true;
    private Activity activity;
    private int page = 0;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.fragment_video, container, false);
        this.activity = getActivity();
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        layout_loading = activity.findViewById(R.id.fragment_video_loading);
        mRecyclerView = (RecyclerView) activity.findViewById(R.id.videoShare_rv);
        mLayoutManager = new GridLayoutManager(activity, 2);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mSwipeRefreshLayout = (SwipeRefreshLayout) activity.findViewById(R.id.swipe_refresh_layout1);
        mSwipeRefreshLayout.setColorSchemeColors(ColorUtils.getColor(this,R.color.aliceblue), ColorUtils.getColor(this,R.color.antiquewhite), ColorUtils.getColor(this,R.color.aqua),ColorUtils.getColor(this,R.color.aquamarine));
        mSwipeRefreshLayout.setOnRefreshListener(this);
        mRecyclerView.setHasFixedSize(true);
        adapter = new videoShareViewAdapter(activity, null);
        mRecyclerView.setAdapter(adapter);
        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {

            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }

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
                    new MyAsyncTaskGetVideoItem().execute(page);
                }
            }
        });
        new MyAsyncTaskGetVideoItem().execute(page);
        super.onActivityCreated(savedInstanceState);
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
        adapter.removeAll();
        page = 0;
        new MyAsyncTaskGetVideoItem().execute(page);
    }

    private class MyAsyncTaskGetVideoItem extends AsyncTask<Integer, String, List<video>> {
        @Override
        protected List<video> doInBackground(Integer... pages) {
            return VideoItemService.getVideoItems(pages[0]);
        }

        @Override
        protected void onPostExecute(List<video> videoItems) {
            if(videoItems.size()>0) {
                Log.d("visibilityyyyy", layout_loading.getVisibility() +"and" + View.VISIBLE);
                if(layout_loading.getVisibility() != View.INVISIBLE) {
                    layout_loading.setVisibility(View.INVISIBLE);
                    mRecyclerView.setVisibility(View.VISIBLE);
                }
                adapter.addVideoItem(videoItems);
                adapter.notifyDataSetChanged();
                page++;
            }

            mSwipeRefreshLayout.setRefreshing(false);
            isLoading = false;
            if(page>0 && videoItems.size()==0) {
                adapter.notifyItemRemoved(adapter.getItemCount());
            }
        }
    }

    String TAG = "FragmentCheck"+getClass().getSimpleName();
    boolean isDestroyView = false;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        page = 0;
        Log.d(TAG,"onCreate");
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        Log.d(TAG,"onDestroyView");
        isDestroyView = true;
        adapter.removeAll();
        page = 0;
    }
}