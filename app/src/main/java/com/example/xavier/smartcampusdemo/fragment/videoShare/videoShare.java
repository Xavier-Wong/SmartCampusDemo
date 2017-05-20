package com.example.xavier.smartcampusdemo.fragment.videoShare;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.xavier.smartcampusdemo.R;
import com.example.xavier.smartcampusdemo.adapter.videoShareViewAdapter;
import com.example.xavier.smartcampusdemo.entity.video;
import com.example.xavier.smartcampusdemo.service.VideoItemService;
import com.example.xavier.smartcampusdemo.util.ColorUtils;

import java.util.List;

import static com.example.xavier.smartcampusdemo.activity.MainActivity.fab;
import static com.example.xavier.smartcampusdemo.util.NetUtil.NetUtil.isNetworkAvailable;

/**
 * Created by Xavier on 11/7/2016.
 * videoShareFragment
 */

public class videoShare extends Fragment implements SwipeRefreshLayout.OnRefreshListener {
    View layout_loading;

    private static videoShareViewAdapter adapter;
    RecyclerView mRecyclerView;

    LinearLayoutManager mLayoutManager;
    SwipeRefreshLayout mSwipeRefreshLayout;
    private static boolean isLoading = true;
    private Activity activity;
    private static int page = 0;
    private static boolean noMore = false;

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
        mRecyclerView = (RecyclerView) activity.findViewById(R.id.videoShare_rv);
        mLayoutManager = new LinearLayoutManager(activity);
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
                super.onScrolled(recyclerView, dx, dy);
                if (dy >0) {
                    // Scroll Down
                    if (fab.isShown()) {
                        fab.hide();
                    }
                }
                else if (dy <0) {
                    // Scroll Up
                    if (!fab.isShown()) {
                        fab.show();
                    }
                }
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
                    if(!noMore) {
                        isLoading = true;
                        new MyAsyncTaskGetVideoItem().execute(page);
                    }
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
        mSwipeRefreshLayout.setRefreshing(false);
    }

    public static void refresh() {
        adapter.removeAll();
        noMore = false;
        page = 0;
        new MyAsyncTaskGetVideoItem().execute(page);
    }

    private static class MyAsyncTaskGetVideoItem extends AsyncTask<Integer, String, List<video>> {
        @Override
        protected List<video> doInBackground(Integer... pages) {
            return VideoItemService.getVideoItems(pages[0]);
        }

        @Override
        protected void onPostExecute(List<video> videoItems) {
            if(videoItems.size()>0) {
                adapter.addVideoItem(videoItems);
                adapter.notifyDataSetChanged();
                page++;
            }
            if(videoItems.size()<10 && !noMore) {
                adapter.notifyEnd();
                if(page == 0) {
                    adapter.notifyNone();
                    adapter.notifyDataSetChanged();
                }
                noMore = true;
            }
            isLoading = false;
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
    public void onAttach(Context context) {
        super.onAttach(context);
        Log.d(TAG,"onAttach");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG,"onDestroy");
    }

    @Override
    public void onDetach() {
        super.onDetach();
        Log.d(TAG,"onDetach");
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Log.d(TAG,"onViewCreated");
    }

    @Override
    public void onStart() {
        super.onStart();
        Log.d(TAG,"onStart");
    }

    @Override
    public void onStop() {
        super.onStop();
        Log.d(TAG,"onStop");
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