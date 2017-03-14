package com.example.xavier.smartcampusdemo.Fragment;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Rect;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.xavier.smartcampusdemo.Activity.ForumsDetailsActivity;
import com.example.xavier.smartcampusdemo.Adapter.techForumViewAdapter;
import com.example.xavier.smartcampusdemo.Entity.forum;
import com.example.xavier.smartcampusdemo.R;
import com.example.xavier.smartcampusdemo.Service.ForumItemService;
import com.example.xavier.smartcampusdemo.Util.OnItemTouchListener;

import java.util.List;

/**
 * Created by Xavier on 11/7/2016.
 * techForumFragment
 */

public class techForum extends Fragment implements SwipeRefreshLayout.OnRefreshListener {
    private static techForumViewAdapter adapter;
    RecyclerView mRecyclerView;
    SwipeRefreshLayout mSwipeRefreshLayout;
    private boolean isLoading = true;
    private Activity activity;
    private static int page = 0;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.techforum_fragment, container, false);
        this.activity = getActivity();
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        mRecyclerView = (RecyclerView) activity.findViewById(R.id.techForum_rv);
        final LinearLayoutManager mLayoutManager = new LinearLayoutManager(activity);
        mSwipeRefreshLayout = (SwipeRefreshLayout) activity.findViewById(R.id.swipe_refresh_layout);
        mSwipeRefreshLayout.setColorSchemeColors(R.color.aliceblue, R.color.antiquewhite, R.color.aqua,R.color.aquamarine);
        mSwipeRefreshLayout.setOnRefreshListener(this);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setHasFixedSize(true);
        adapter = new techForumViewAdapter(activity, null);
        mRecyclerView.setAdapter(adapter);
//        mRecyclerView.setOnTouchListener(new View.OnTouchListener() {
//            float x;
//            float y;
//            @Override
//            public boolean onTouch(View v, MotionEvent event) {
//                switch (event.getAction()) {
//                    case MotionEvent.ACTION_DOWN:
//                        x = event.getX();
//                        y = event.getY();
//                        break;
//                    case MotionEvent.ACTION_UP:
//                        if(x == event.getX() && y == event.getY()) {
//                            View childe = mRecyclerView.findChildViewUnder(event.getX(), event.getY());
//                            if (childe != null) {
//                                RecyclerView.ViewHolder vh = mRecyclerView.getChildViewHolder(childe);
//                                if (vh.getItemViewType() == 2) {
//                                    Intent intent = new Intent(activity, ForumsDetailsActivity.class);
//                                    intent.putExtra("url", String.valueOf(adapter.getForumItems().get(vh.getAdapterPosition()).getF_id()));
//                                    startActivity(intent);
//                                }
//                            }
//                        }
//                        break;
//                }
//                return false;
//            }
//        });
//        mRecyclerView.addOnItemTouchListener(new OnItemTouchListener(mRecyclerView) {
//            @Override
//            public void onItemClick(RecyclerView.ViewHolder vh) {
//                if(vh.getItemViewType()==2) {
//                    Intent intent = new Intent(activity, ForumsDetailsActivity.class);
//                    intent.putExtra("url", String.valueOf(adapter.getForumItems().get(vh.getAdapterPosition()).getF_id()));
//                    startActivity(intent);
//                }
//            }
//        });
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
                else if ((visibleItemCount + firstVisibleItems) >= totalItemCount && !isLoading) {
                    // 判断点
                    isLoading = true;
                    new MyAsyncTaskGetForumItem().execute(page);
                }
            }
        });
        setFooterView(mRecyclerView);
        new MyAsyncTaskGetForumItem().execute(page);
        super.onActivityCreated(savedInstanceState);
    }

    private void setFooterView(RecyclerView view){
        View footer = LayoutInflater.from(activity).inflate(R.layout.progress_footerview, view, false);
        adapter.setFooterView(footer);
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
        page = 0;
    }

    public class MyAsyncTaskGetForumItem extends AsyncTask<Integer, String, List<forum>> {
        @Override
        protected List<forum> doInBackground(Integer... pages) {
            return ForumItemService.getForumItem(pages[0]);
        }

        @Override
        protected void onPostExecute(List<forum> forumItems) {
            if(forumItems.size()>0) {
                adapter.addForumItem(forumItems);
                adapter.notifyDataSetChanged();
                page++;
            }
            isLoading = false;
            if(page>0 && forumItems.size()==0) {
                adapter.notifyItemRemoved(adapter.getItemCount());
                Toast toast = Toast.makeText(activity,"没有更多了",Toast.LENGTH_SHORT);
                toast.setGravity(Gravity.BOTTOM, 0, 100);
                toast.show();
            }
        }

    }

    public static boolean isNetworkAvailable(Context context) {
        ConnectivityManager connectivity = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivity != null) {
            NetworkInfo info = connectivity.getActiveNetworkInfo();
            if (info != null && info.isConnected())
            {
                // 当前网络是连接的
                if (info.getState() == NetworkInfo.State.CONNECTED)
                {
                    // 当前所连接的网络可用
                    return true;
                }
            }
        }
        return false;
    }

    class CheckNewItemsThread extends Thread {
        int loop,count=0;
        @Override
        public void run() {
            Toast toast1 = Toast.makeText(getContext(), "没有新的论坛", Toast.LENGTH_SHORT);
            toast1.setGravity(Gravity.BOTTOM, 0 ,0);
            toast1.show();
//            for(loop = 0 ; ;) {
//                if (adapter.getForumItems().get(0)==ForumItemService.getForumItem(0).get(0)) {
//                    Toast toast = Toast.makeText(activity, "没有新的论坛", Toast.LENGTH_SHORT);
//                    toast.setGravity(Gravity.BOTTOM, 0 ,0);
//                    toast.show();
//                    break;
//                }
//                else {
//                    Toast toast = Toast.makeText(activity, "有新的论坛", Toast.LENGTH_SHORT);
//                    toast.setGravity(Gravity.BOTTOM, 0 ,0);
//                    toast.show();
//                    loop++;
//                    if (adapter.getForumItems().get(loop*10)==ForumItemService.getForumItem(loop).get(0)) {
//                        for(int i=0;i<10;i++) {
//                            if(adapter.getForumItems().get(i+(loop-1)*10) != ForumItemService.getForumItem(loop-1).get(i)) {
//                                count++;
//                            }
//                        }
//                        break;
//                    }
//                }
//                count=(loop-1)*10+count;
//                Toast toast = Toast.makeText(getContext(), "有"+count+"条新的论坛", Toast.LENGTH_SHORT);
//                toast.setGravity(Gravity.BOTTOM, 0 ,0);
//                toast.show();
//            }

        }
    }
}

