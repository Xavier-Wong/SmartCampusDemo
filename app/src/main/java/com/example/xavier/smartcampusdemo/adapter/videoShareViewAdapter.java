package com.example.xavier.smartcampusdemo.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.xavier.smartcampusdemo.activity.VideoDetailsActivity;
import com.example.xavier.smartcampusdemo.entity.video;
import com.example.xavier.smartcampusdemo.R;
import com.facebook.drawee.view.SimpleDraweeView;

import java.util.ArrayList;
import java.util.List;

import static com.example.xavier.smartcampusdemo.service.NetService.getAvatarPath;
import static com.example.xavier.smartcampusdemo.service.NetService.getIP;
import static com.example.xavier.smartcampusdemo.service.NetService.getVideoPath;
import static com.example.xavier.smartcampusdemo.util.TimeConvertor.timeToNow;

/**
 * Created by Xavier on 11/13/2016.
 * videoShareAdapter
 */

public class videoShareViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int TYPE_NORMAL = 0;  //说明是不带有header和footer的
    private static final int TYPE_FOOTER = 1;  //说明是带有Footer的
    private static final int TYPE_ERROR = 2;

    private List<video> videoItems;
    private Context context;
    private boolean isEnd = false;
    private boolean isNone = false;

    public videoShareViewAdapter(Context context, List<video> videoItem) {
        super();
        this.context = context;
        if(videoItems==null) {
            videoItems = new ArrayList<>();
        }
        else {
            this.videoItems = videoItem;
        }
    }

    public void addVideoItem (List<video> newVideoItems) {
        videoItems.addAll(newVideoItems);
    }

    private List<video> getVideoItems() {
        return videoItems;
    }

    public void removeAll() {
        this.isEnd = false;
        this.isNone = false;
        videoItems.clear();
        notifyDataSetChanged();
    }

    public void notifyEnd() {
        this.isEnd = true;
    }
    public void notifyNone() {
        this.isNone = true;
    }
    @Override
    public int getItemCount() {
        return videoItems.size() + 1;
    }

    @Override
    public int getItemViewType(int position) {
        if (position == getItemCount()-1 ){
            return TYPE_FOOTER;
        }
        if(videoItems.get(position).getU_id() == 0) {
            return TYPE_ERROR;
        }
        return TYPE_NORMAL;
    }

    @Override
    public videoItemHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        if(viewType == TYPE_ERROR) {
            View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_normal_error, viewGroup, false);
            return new videoItemHolder(v, viewType);
        }
        else if(viewType == TYPE_FOOTER){
            View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.progress_footerview, viewGroup, false);
            return new videoItemHolder(v, viewType);
        }
        else {
            View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_normal_video, viewGroup, false);
            return new videoItemHolder(v, viewType);
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
//        bindVideoItem(videoShareItem, holder.videoItemTitle, holder.videoItemIcon, holder.videoItemTime);
        videoItemHolder viHolder = (videoItemHolder) holder;
        if(getItemViewType(position) == TYPE_NORMAL){
            if(viHolder != null) {
                //这里加载数据的时候要注意，是从position-1开始，因为position==0已经被header占用了
                bindVideoItem(viHolder, position);
            }
        }
        if(getItemViewType(position) == TYPE_FOOTER) {
            if(viHolder != null) {
                if(isEnd) {
                    if(isNone) {
                        viHolder.progressLoad.setVisibility(View.GONE);
                        viHolder.progressEnd.setVisibility(View.GONE);
                        viHolder.progressNone.setVisibility(View.VISIBLE);
                    }
                    else {
                        viHolder.progressLoad.setVisibility(View.GONE);
                        viHolder.progressNone.setVisibility(View.GONE);
                        viHolder.progressEnd.setVisibility(View.VISIBLE);
                    }
                }
                else {
                    viHolder.progressNone.setVisibility(View.GONE);
                    viHolder.progressEnd.setVisibility(View.GONE);
                    viHolder.progressLoad.setVisibility(View.VISIBLE);
                }
            }
        }
    }


    private void bindVideoItem(videoItemHolder viHolder, int position) {
        video videoItem = videoItems.get(position);
        viHolder.videoItemTitle.setText(videoItem.getTitle());
        viHolder.videoItemAuthor.setText(videoItem.getAuthor());
        viHolder.videoItemTime.setText(timeToNow(videoItem.getTime()));
        viHolder.videoItemAvatar.setImageURI(getAvatarPath()+videoItem.getAvatar());
        viHolder.videoItemIcon.setImageURI(getVideoPath()+videoItem.getThumbnail());
    }

    private class videoItemHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        SimpleDraweeView videoItemIcon;
        SimpleDraweeView videoItemAvatar;
        TextView videoItemAuthor;
        TextView videoItemTitle;
        TextView videoItemTime;
        TextView videoItemDuration;

        private ProgressBar progressLoad;
        private LinearLayout progressEnd, progressNone;

        videoItemHolder(View itemView, int viewType) {
            super(itemView);
            if (viewType == TYPE_FOOTER){
                progressLoad = (ProgressBar) itemView.findViewById(R.id.foot_progressbar_load_more);
                progressEnd = (LinearLayout) itemView.findViewById(R.id.foot_progressbar_load_nomore);
                progressNone = (LinearLayout) itemView.findViewById(R.id.foot_progressbar_load_none);
                return;
            }
            if(viewType == TYPE_ERROR) {
                return;
            }
            videoItemIcon = (SimpleDraweeView) itemView.findViewById(R.id.base_video_item_icon);
            videoItemAvatar = (SimpleDraweeView) itemView.findViewById(R.id.video_normal_avatar);
            videoItemAuthor = (TextView) itemView.findViewById(R.id.base_video_item_author);
            videoItemTitle = (TextView) itemView.findViewById(R.id.base_video_item_title);
            videoItemTime = (TextView) itemView.findViewById(R.id.base_video_item_time);
            //videoItemDuration = (TextView) itemView.findViewById(R.id.video_duration);
            itemView.findViewById(R.id.base_video_item_container).setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.base_video_item_container:
                    Intent intent = new Intent(context, VideoDetailsActivity.class);
                    intent.putExtra("url", String.valueOf(getVideoItems().get(getAdapterPosition()).getV_id()));
                    context.startActivity(intent);
                    break;
            }
        }
    }

}
