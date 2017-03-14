package com.example.xavier.smartcampusdemo.Adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.xavier.smartcampusdemo.Domain.videoShareUnit;
import com.example.xavier.smartcampusdemo.R;

/**
 * Created by Xavier on 11/13/2016.
 * videoShareAdapter
 */

public class videoShareViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context context;
    private String[] mDataList;

    public videoShareViewAdapter(Context context, String[] mDataList) {
        super();
        this.context = context;

        this.mDataList = mDataList;
    }

    @Override
    public videoItemHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.videoshare_item_normal, viewGroup, false);
        return new videoItemHolder(v);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int i) {
//        bindVideoItem(videoShareItem, holder.videoItemTitle, holder.videoItemIcon, holder.videoItemTime);
        videoItemHolder holder1 = (videoItemHolder) holder;
        holder1.videoItemTitle.setText("测试标题");
        holder1.videoItemTime.setText("测试时间");
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

    private class videoItemHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        ImageView videoItemIcon;
        TextView videoItemTitle;
        TextView videoItemTime;

        videoItemHolder(View itemView) {
            super(itemView);
            videoItemIcon = (ImageView) itemView.findViewById(R.id.base_video_item_icon);
            videoItemTitle = (TextView) itemView.findViewById(R.id.base_video_item_title);
            videoItemTime = (TextView) itemView.findViewById(R.id.base_video_item_time);
            itemView.findViewById(R.id.base_video_item_container).setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.base_video_item_container:
                    break;
            }
        }
    }

}
