package com.example.xavier.smartcampusdemo.Fragment;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.xavier.smartcampusdemo.Adapter.videoShareViewAdapter;
import com.example.xavier.smartcampusdemo.Domain.videoShareUnit;
import com.example.xavier.smartcampusdemo.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Xavier on 11/7/2016.
 * videoShareFragment
 */

public class videoShare extends Fragment{
    private videoShareViewAdapter adapter;
    private RecyclerView mRecyclerView;
    private List<videoShareUnit> videoItems = new ArrayList<>();
    private String[] titles={"1","2","3","4"};
    private String [] times={"1","2","3","4"};
    private boolean isLoading = true;
    private Activity activity;
    private int page = 0;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.videoshare_fragment, container, false);
        mRecyclerView = (RecyclerView) view.findViewById(R.id.videoShare_rv);
        mRecyclerView.setLayoutManager(new GridLayoutManager(activity, 2));
        mRecyclerView.setHasFixedSize(true);
        adapter = new videoShareViewAdapter(titles);
        mRecyclerView.setAdapter(adapter);
        return view;
    }

}