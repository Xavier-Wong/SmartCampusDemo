package com.example.xavier.smartcampusdemo.Fragment.typeforum;

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

import com.example.xavier.smartcampusdemo.Adapter.techForumViewAdapter;
import com.example.xavier.smartcampusdemo.Entity.forum;
import com.example.xavier.smartcampusdemo.R;
import com.example.xavier.smartcampusdemo.Service.ForumItemService;
import com.example.xavier.smartcampusdemo.Util.ColorUtils;

import java.util.List;

import static com.example.xavier.smartcampusdemo.Fragment.techForum.LIT;
import static com.example.xavier.smartcampusdemo.Util.NetUtil.NetUtil.isNetworkAvailable;

/**
 * Created by Xavier on 11/7/2016.
 * techForumFragment
 */

public class litForum extends Fragment implements SwipeRefreshLayout.OnRefreshListener {

    private static techForumViewAdapter adapter;
    RecyclerView mRecyclerView;
    SwipeRefreshLayout mSwipeRefreshLayout;
    private static boolean isLoading = true;
    private Activity activity;
    public static int page = 0;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.typelitforum_fragment, container, false);
        this.activity = getActivity();
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        mRecyclerView = (RecyclerView) activity.findViewById(R.id.litForum_rv);
        final LinearLayoutManager mLayoutManager = new LinearLayoutManager(activity);
        mSwipeRefreshLayout = (SwipeRefreshLayout) activity.findViewById(R.id.swipe_refresh_litlayout);
        mSwipeRefreshLayout.setColorSchemeColors(ColorUtils.getColor(this,R.color.aliceblue), ColorUtils.getColor(this,R.color.antiquewhite), ColorUtils.getColor(this,R.color.aqua),ColorUtils.getColor(this,R.color.aquamarine));
        mSwipeRefreshLayout.setOnRefreshListener(this);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setHasFixedSize(true);
        adapter = new techForumViewAdapter(activity, null);
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
                    new MyAsyncTaskGetForumItem().execute(page);
                }
            }
        });
        new MyAsyncTaskGetForumItem().execute(page);
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
        new MyAsyncTaskGetForumItem().execute(page);
    }

    private class MyAsyncTaskGetForumItem extends AsyncTask<Integer, String, List<forum>> {
        @Override
        protected List<forum> doInBackground(Integer... pages) {
            return ForumItemService.getForumItems(pages[0], LIT);
        }

        @Override
        protected void onPostExecute(List<forum> forumItems) {
            if(forumItems.size()>0) {
                adapter.addForumItem(forumItems);
                adapter.notifyDataSetChanged();
                mSwipeRefreshLayout.setRefreshing(false);
                page++;
            }
            isLoading = false;
            if(page>0 && forumItems.size()==0) {
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

