package com.example.xavier.smartcampusdemo.Fragment;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

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

public class techForum extends Fragment {
    private techForumViewAdapter adapter;
    RecyclerView mRecyclerView;
    private boolean isLoading = true;
    private Activity activity;
    private int page = 0;

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
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setHasFixedSize(true);
        adapter = new techForumViewAdapter(activity, null);
        mRecyclerView.setAdapter(adapter);
        mRecyclerView.addOnItemTouchListener(new OnItemTouchListener(mRecyclerView) {
            @Override
            public void onItemClick(RecyclerView.ViewHolder vh) {
                if(vh.getItemViewType()==2) {
                    Intent intent = new Intent(activity, ForumsDetailsActivity.class);
                    intent.putExtra("url", String.valueOf(adapter.getForumItems().get(vh.getAdapterPosition()).getF_id()));
                    startActivity(intent);
                }
            }
        });
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

                if ((visibleItemCount + firstVisibleItems) >= totalItemCount && !isLoading) {
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

    private void setHeaderView(RecyclerView view){
        View header = LayoutInflater.from(activity).inflate(R.layout.progress_footerview, view, false);
        adapter.setHeaderView(header);
    }

    private void setFooterView(RecyclerView view){
        View footer = LayoutInflater.from(activity).inflate(R.layout.progress_footerview, view, false);
        adapter.setFooterView(footer);
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
        }

    }
}

