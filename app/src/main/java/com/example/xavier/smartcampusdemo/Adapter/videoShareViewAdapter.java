package com.example.xavier.smartcampusdemo.Adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.xavier.smartcampusdemo.Activity.VideoDetailsActivity;
import com.example.xavier.smartcampusdemo.Entity.video;
import com.example.xavier.smartcampusdemo.R;
import com.facebook.drawee.view.SimpleDraweeView;

import java.util.ArrayList;
import java.util.List;

import static com.example.xavier.smartcampusdemo.Service.NetService.getIP;

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
        videoItems.clear();
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        if(videoItems.size() >= 10)
            return videoItems.size() + 1;
        return videoItems.size();
    }

    @Override
    public int getItemViewType(int position) {
        if(getVideoItems().get(videoItems.size()-1).getU_id() == 0) {
            return TYPE_ERROR;
        }
        if (position == getItemCount()-1 && position >= 10){
            //最后一个,应该加载Footer
            return TYPE_FOOTER;
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
        videoItemHolder fiHolder = (videoItemHolder) holder;
        if(getItemViewType(position) == TYPE_NORMAL){
            if(fiHolder != null) {
                //这里加载数据的时候要注意，是从position-1开始，因为position==0已经被header占用了
                bindVideoItem(fiHolder, position);
            }
        }
    }


    private void bindVideoItem(videoItemHolder fiHolder, int position) {
        video videoItem = videoItems.get(position);
        fiHolder.videoItemTitle.setText(videoItem.getTitle());
        fiHolder.videoItemAuthor.setText(videoItem.getAuthor());
        fiHolder.videoItemIcon.setImageURI("http://"+getIP()+"/HelloWeb/Upload/Video/"+videoItem.getThumbnail());
    }

    private class videoItemHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        SimpleDraweeView videoItemIcon;
        TextView videoItemAuthor;
        TextView videoItemTitle;
        TextView videoItemDuration;

        videoItemHolder(View itemView, int viewType) {
            super(itemView);
            if (viewType == TYPE_FOOTER){
                return;
            }
            if(viewType == TYPE_ERROR) {
                return;
            }
            videoItemIcon = (SimpleDraweeView) itemView.findViewById(R.id.base_video_item_icon);
            videoItemAuthor = (TextView) itemView.findViewById(R.id.base_video_item_author);
            videoItemTitle = (TextView) itemView.findViewById(R.id.base_video_item_title);
            videoItemDuration = (TextView) itemView.findViewById(R.id.video_duration);
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
