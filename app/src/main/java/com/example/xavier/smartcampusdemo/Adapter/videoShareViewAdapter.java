package com.example.xavier.smartcampusdemo.Adapter;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.xavier.smartcampusdemo.Activity.BaseActivity;
import com.example.xavier.smartcampusdemo.Domain.videoShareUnit;
import com.example.xavier.smartcampusdemo.Fragment.videoShare;
import com.example.xavier.smartcampusdemo.R;
import com.example.xavier.smartcampusdemo.ViewHolder.videoItemHolder;

import java.util.List;

/**
 * Created by Xavier on 11/13/2016.
 * videoShareAdapter
 */

public class videoShareViewAdapter extends RecyclerView.Adapter<videoItemHolder> {

    private String[] mDataList;

    public videoShareViewAdapter(String[] mDataList) {
        this.mDataList = mDataList;
    }

    @Override
    public videoItemHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.cv_videoshare_item, viewGroup, false);
        return new videoItemHolder(v);
    }

    @Override
    public void onBindViewHolder(videoItemHolder holder, int i) {
//        bindVideoItem(videoShareItem, holder.videoItemTitle, holder.videoItemIcon, holder.videoItemTime);

        holder.videoItemTitle.setText("测试标题");
        holder.videoItemTime.setText("测试时间");
    }

    @Override
    public int getItemCount() {
        return mDataList.length;
    }

    @SuppressWarnings("StatementWithEmptyBody")
    private void bindVideoItem(videoShareUnit videoShareItem, TextView videoTitle, ImageView videoIcon, TextView videoTime) {
        if(videoShareItem.getUrl().isEmpty()) {
            if(videoIcon.getVisibility() != View.GONE)
                videoIcon.setVisibility(View.GONE);
        }
        else {
        }
        videoTitle.setText("测试视频");
        videoTime.setText("测试");
    }

}
