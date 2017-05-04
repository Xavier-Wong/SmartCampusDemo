package com.example.xavier.smartcampusdemo.Adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.xavier.smartcampusdemo.Entity.video_reply;
import com.example.xavier.smartcampusdemo.R;
import com.example.xavier.smartcampusdemo.Util.NetUtil.LoadImageUtil;
import com.facebook.drawee.view.SimpleDraweeView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

import static com.example.xavier.smartcampusdemo.Service.NetService.getIP;

/**
 * Created by Xavier on 4/30/2017.
 *
 */

public class videoCommentViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int TYPE_NORMAL = 0;  //说明是不带有header和footer的
    private static final int TYPE_FOOTER = 1;  //说明是带有Footer的
    private static final int TYPE_ERROR = 2;

    private List<video_reply> videoReplyItems;
    private Context context;

    public videoCommentViewAdapter(Context context, List<video_reply> videoReplyItem) {
        super();
        this.context = context;
        if(videoReplyItems==null) {
            videoReplyItems = new ArrayList<>();
        }
        else {
            this.videoReplyItems = videoReplyItem;
        }
    }

    public void addVideoReplyItems(List<video_reply> newVideoReplyItems) {
        videoReplyItems.addAll(newVideoReplyItems);
    }

    private List<video_reply> getVideoReplyItems() {
        return videoReplyItems;
    }

    public void removeAll() {
        videoReplyItems.clear();
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        if(videoReplyItems.size() >= 10)
            return videoReplyItems.size() + 1;
        return videoReplyItems.size();
    }

    @Override
    public int getItemViewType(int position) {
        if(videoReplyItems.get(videoReplyItems.size()-1).getU_id() == 0) {
            return TYPE_ERROR;
        }
        if (position == getItemCount()-1 && position >= 10){
            //最后一个,应该加载Footer
            return TYPE_FOOTER;
        }
        return TYPE_NORMAL;
    }

    @Override
    public videoCommentHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        if(viewType == TYPE_ERROR) {
            View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_normal_error, viewGroup, false);
            return new videoCommentHolder(v, viewType);
        }
        else if(viewType == TYPE_FOOTER){
            View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.progress_footerview, viewGroup, false);
            return new videoCommentHolder(v, viewType);
        }
        else {
            View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_normal_video_comment, viewGroup, false);
            return new videoCommentHolder(v, viewType);
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        videoCommentHolder vriHolder = (videoCommentHolder) holder;
        if(getItemViewType(position) == TYPE_NORMAL){
            if(vriHolder != null) {
                //这里加载数据的时候要注意，是从position-1开始，因为position==0已经被header占用了
                bindVideoItem(vriHolder, position);
            }
        }
    }


    private void bindVideoItem(videoCommentHolder vriHolder, int position) {
        video_reply videoReplyItem = videoReplyItems.get(position);
        vriHolder.videoReplyItemAuthor.setText(videoReplyItem.getAuthor());
        vriHolder.videoReplyItemTime.setText(videoReplyItem.getTime());
        vriHolder.videoReplyItemContent.setText(videoReplyItem.getContent());
        vriHolder.videoReplyItemAvatar.setImageURI("http://"+getIP()+"/HelloWeb/Upload/Avatar/"+videoReplyItem.getAvatar());
    }

    private class videoCommentHolder extends RecyclerView.ViewHolder {

        SimpleDraweeView videoReplyItemAvatar;
        TextView videoReplyItemAuthor;
        TextView videoReplyItemTime;
        TextView videoReplyItemContent;

        videoCommentHolder(View itemView, int viewType) {
            super(itemView);
            if (viewType == TYPE_FOOTER){
                return;
            }
            if(viewType == TYPE_ERROR) {
                return;
            }
            videoReplyItemAvatar = (SimpleDraweeView) itemView.findViewById(R.id.video_comment_avatar);
            videoReplyItemAuthor = (TextView) itemView.findViewById(R.id.video_comment_author);
            videoReplyItemTime = (TextView) itemView.findViewById(R.id.video_comment_time);
            videoReplyItemContent = (TextView) itemView.findViewById(R.id.video_comment_content);
        }

    }
}
