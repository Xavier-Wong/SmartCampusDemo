package com.example.xavier.smartcampusdemo.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.ViewGroup;

/**
 * Created by Xavier on 5/13/2017.
 */

public class videoInfoViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int TYPE_AUTHOR = 0;  //说明是不带有header和footer的
    private static final int TYPE_TITLE = 1;  //说明是带有Footer的
    private static final int TYPE_CONTENT = 2;

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return null;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return 0;
    }
}
