package com.example.xavier.smartcampusdemo.Fragment;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.CardView;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.xavier.smartcampusdemo.Activity.ForumsDetailsActivity;
import com.example.xavier.smartcampusdemo.Activity.SignUpActivity;
import com.example.xavier.smartcampusdemo.Activity.VideoDetailsActivity;
import com.example.xavier.smartcampusdemo.Adapter.videoShareViewAdapter;
import com.example.xavier.smartcampusdemo.Domain.videoShareUnit;
import com.example.xavier.smartcampusdemo.R;
import com.example.xavier.smartcampusdemo.Util.OnItemTouchListener;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Xavier on 11/7/2016.
 * videoShareFragment
 */

public class videoShare extends Fragment implements SwipeRefreshLayout.OnRefreshListener {
    videoShareViewAdapter adapter;
    RecyclerView mRecyclerView;

    SwipeRefreshLayout mSwipeRefreshLayout;
    private List<videoShareUnit> videoItems = new ArrayList<>();
    private String[] titles={"1","2","3","4"};
    private boolean isLoading = true;
    private Activity activity;
    private int page = 0;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.videoshare_fragment, container, false);
        this.activity = getActivity();
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        mRecyclerView = (RecyclerView) activity.findViewById(R.id.videoShare_rv);
        mRecyclerView.setLayoutManager(new GridLayoutManager(activity, 2));
        mSwipeRefreshLayout = (SwipeRefreshLayout) activity.findViewById(R.id.swipe_refresh_layout1);
        mSwipeRefreshLayout.setColorSchemeColors(R.color.aliceblue, R.color.antiquewhite, R.color.aqua,R.color.aquamarine);
        mSwipeRefreshLayout.setOnRefreshListener(this);
        mRecyclerView.setHasFixedSize(true);
        adapter = new videoShareViewAdapter(activity, titles);
        mRecyclerView.setAdapter(adapter);
        mRecyclerView.addOnItemTouchListener(new OnItemTouchListener(mRecyclerView) {
            @Override
            public void onItemClick(RecyclerView.ViewHolder vh) {
                    Intent intent = new Intent(activity, VideoDetailsActivity.class);
                    startActivity(intent);
            }
        });
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onRefresh() {
        mSwipeRefreshLayout.setRefreshing(false);
    }
}