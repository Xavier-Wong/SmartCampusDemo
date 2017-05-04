package com.example.xavier.smartcampusdemo.Adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.xavier.smartcampusdemo.Entity.blog_reply;
import com.example.xavier.smartcampusdemo.R;
import com.facebook.drawee.view.SimpleDraweeView;

import java.util.ArrayList;
import java.util.List;

import static com.example.xavier.smartcampusdemo.Service.NetService.getIP;

/**
 * Created by Xavier on 5/2/2017.
 */

public class blogReplyViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int TYPE_NORMAL = 0;  //说明是不带有header和footer的
    private static final int TYPE_FOOTER = 1;  //说明是带有Footer的
    private static final int TYPE_ERROR = 2;

    private List<blog_reply> blogReplyItems;
    private Context context;

    public blogReplyViewAdapter(Context context, List<blog_reply> blogReplyItem) {
        super();
        this.context = context;
        if(blogReplyItems==null) {
            blogReplyItems = new ArrayList<>();
        }
        else {
            this.blogReplyItems = blogReplyItem;
        }
    }

    public void addBlogReplyItems(List<blog_reply> newBlogReplyItems) {
        blogReplyItems.addAll(newBlogReplyItems);
    }

    private List<blog_reply> getBlogReplyItems() {
        return blogReplyItems;
    }

    public void removeAll() {
        blogReplyItems.clear();
        notifyDataSetChanged();
    }


    @Override
    public int getItemCount() {
        if(blogReplyItems.size() >= 10)
            return blogReplyItems.size() + 1;
        return blogReplyItems.size();
    }

    @Override
    public int getItemViewType(int position) {
        if(blogReplyItems.get(blogReplyItems.size()-1).getU_id() == 0) {
            return TYPE_ERROR;
        }
        if (position == getItemCount()-1 && position >= 10){
            //最后一个,应该加载Footer
            return TYPE_FOOTER;
        }
        return TYPE_NORMAL;
    }


    @Override
    public blogReplyHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        if(viewType == TYPE_ERROR) {
            View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_normal_error, viewGroup, false);
            return new blogReplyHolder(v, viewType);
        }
        else if(viewType == TYPE_FOOTER){
            View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.progress_footerview, viewGroup, false);
            return new blogReplyHolder(v, viewType);
        }
        else {
            View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_blog_reply_normal, viewGroup, false);
            return new blogReplyHolder(v, viewType);
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        blogReplyHolder briHolder = (blogReplyHolder) holder;
        if(getItemViewType(position) == TYPE_NORMAL){
            if(briHolder != null) {
                //这里加载数据的时候要注意，是从position-1开始，因为position==0已经被header占用了
                bindBlogReplyItem(briHolder, position);
            }
        }
    }

    private void bindBlogReplyItem(blogReplyHolder vriHolder, int position) {
        blog_reply blogReplyItem = blogReplyItems.get(position);
        vriHolder.blogReplyItemAuthor.setText(blogReplyItem.getAuthor());
        vriHolder.blogReplyItemTime.setText(blogReplyItem.getTime());
        vriHolder.blogReplyItemContent.setText(blogReplyItem.getContent());
        vriHolder.blogReplyItemAvatar.setImageURI("http://"+getIP()+"/HelloWeb/Upload/Avatar/"+blogReplyItem.getAvatar());
    }

    private class blogReplyHolder extends RecyclerView.ViewHolder {

        SimpleDraweeView blogReplyItemAvatar;
        TextView blogReplyItemAuthor;
        TextView blogReplyItemTime;
        TextView blogReplyItemContent;

        blogReplyHolder(View itemView, int viewType) {
            super(itemView);
            if (viewType == TYPE_FOOTER){
                return;
            }
            if(viewType == TYPE_ERROR) {
                return;
            }
            blogReplyItemAvatar = (SimpleDraweeView) itemView.findViewById(R.id.blog_comment_avatar);
            blogReplyItemAuthor = (TextView) itemView.findViewById(R.id.blog_comment_author);
            blogReplyItemTime = (TextView) itemView.findViewById(R.id.blog_comment_time);
            blogReplyItemContent = (TextView) itemView.findViewById(R.id.blog_comment_content);
            itemView.findViewById(R.id.blog_reply_container);
        }

    }
}
