package com.example.xavier.smartcampusdemo.Fragment;

import android.app.Activity;
import android.graphics.Rect;
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

import com.example.xavier.smartcampusdemo.Adapter.microBlogViewAdapter;
import com.example.xavier.smartcampusdemo.Entity.blog;
import com.example.xavier.smartcampusdemo.R;
import com.example.xavier.smartcampusdemo.Service.BlogItemService;
import com.example.xavier.smartcampusdemo.Util.ColorUtils;

import java.util.List;

import static com.example.xavier.smartcampusdemo.Util.NetUtil.NetUtil.isNetworkAvailable;

/**
 * Created by Xavier on 11/7/2016.
 * microBlogFragment
 */

public class microBlog extends Fragment implements SwipeRefreshLayout.OnRefreshListener {

    LinearLayoutManager mLayoutManager;
    private static microBlogViewAdapter adapter;
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
        View view = inflater.inflate(R.layout.fragment_blog, container, false);
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
        mRecyclerView = (RecyclerView) activity.findViewById(R.id.microBlog_rv);
        mLayoutManager = new LinearLayoutManager(activity);
        mSwipeRefreshLayout = (SwipeRefreshLayout) activity.findViewById(R.id.swipe_refresh_layout2);
        mSwipeRefreshLayout.setColorSchemeColors(ColorUtils.getColor(this,R.color.aliceblue), ColorUtils.getColor(this,R.color.antiquewhite), ColorUtils.getColor(this,R.color.aqua),ColorUtils.getColor(this,R.color.aquamarine));
        mSwipeRefreshLayout.setOnRefreshListener(this);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setHasFixedSize(true);
        adapter = new microBlogViewAdapter(activity, null);
        mRecyclerView.addItemDecoration(new MyItemDecoration());
        mRecyclerView.setNestedScrollingEnabled(false);
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
                    new MyAsyncTaskGetBlogItem().execute(page);
                }
            }
        });
        new MyAsyncTaskGetBlogItem().execute(page);
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
        adapter.removeAll();
        page = 0;
        new MyAsyncTaskGetBlogItem().execute(page);
    }

    private class MyAsyncTaskGetBlogItem extends AsyncTask<Integer, String, List<blog>> {
        @Override
        protected List<blog> doInBackground(Integer... pages) {
            return BlogItemService.getBlogItems(pages[0]);
        }

        @Override
        protected void onPostExecute(List<blog> blogItems) {
            if(blogItems.size()>0) {
                adapter.addBlogItem(blogItems);
                adapter.notifyDataSetChanged();
                mSwipeRefreshLayout.setRefreshing(false);
                page++;
            }
            isLoading = false;
            if(page>0 && blogItems.size()==0) {
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