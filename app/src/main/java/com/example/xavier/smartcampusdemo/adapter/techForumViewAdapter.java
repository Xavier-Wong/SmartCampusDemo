package com.example.xavier.smartcampusdemo.adapter;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.xavier.smartcampusdemo.activity.ForumsDetailsActivity;
import com.example.xavier.smartcampusdemo.entity.forum;
import com.example.xavier.smartcampusdemo.R;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.interfaces.DraweeController;
import com.facebook.drawee.view.SimpleDraweeView;

import java.util.ArrayList;
import java.util.List;

import static com.example.xavier.smartcampusdemo.service.NetService.getForumPath;
import static com.example.xavier.smartcampusdemo.service.NetService.getIP;
import static com.example.xavier.smartcampusdemo.util.TimeConvertor.timeToNow;

/**
 * Created by Xavier on 11/12/2016.
 * techForumAdapter
 */

public class techForumViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int TYPE_NORMAL = 0;  //说明是不带有header和footer的
    private static final int TYPE_PIC = 1;
    private static final int TYPE_FOOTER = 2;  //说明是带有Footer的
    private static final int TYPE_ERROR = 3;



    private List<forum> forumItems;
    private Context context;
    private boolean isEnd = false;
    private boolean isNone = false;

    public techForumViewAdapter(Context context, List<forum> forumItem) {
        super();
        this.context = context;
        if(forumItems==null) {
            forumItems = new ArrayList<>();
        }
        else {
            this.forumItems = forumItem;
        }
    }

    public void addForumItem (List<forum> newForumItems) {
        forumItems.addAll(newForumItems);
    }

    private List<forum> getForumItems() {
        return forumItems;
    }

    public void removeAll() {
        this.isEnd = false;
        this.isNone = false;
        forumItems.clear();
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
        return forumItems.size() + 1;
    }

    @Override
    public int getItemViewType(int position) {
        if (position == getItemCount()-1){
            //最后一个,应该加载Footer
            return TYPE_FOOTER;
        }
        if(forumItems.get(position).getU_id() == 0) {
            return TYPE_ERROR;
        }
        if(forumItems.get(position).getImg().equals(""))
            return TYPE_NORMAL;
        else
            return TYPE_PIC;
    }

    @Override
    public forumItemHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        if(viewType == TYPE_ERROR) {
            View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_normal_error, viewGroup, false);
            return new forumItemHolder(v, viewType);
        }
        else if(viewType == TYPE_FOOTER){
            View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.progress_footerview, viewGroup, false);
            return new forumItemHolder(v, viewType);
        }
        else if(viewType == TYPE_PIC){
            View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_forum_with_picture, viewGroup, false);
            return new forumItemHolder(v, viewType);
        }else {
            View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_forum_normal, viewGroup, false);
            return new forumItemHolder(v, viewType);
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        forumItemHolder fiHolder = (forumItemHolder) holder;
        if(getItemViewType(position) == TYPE_NORMAL){
            if(fiHolder != null) {
                //这里加载数据的时候要注意，是从position-1开始，因为position==0已经被header占用了
                bindNormalForumItem(fiHolder, position);
            }
        }
        if(getItemViewType(position) == TYPE_PIC){
            if(fiHolder != null) {
                //这里加载数据的时候要注意，是从position-1开始，因为position==0已经被header占用了
                bindPicForumItem(fiHolder, position);
            }
        }
        if(getItemViewType(position) == TYPE_FOOTER) {
            if(fiHolder != null) {
                if(isEnd) {
                    if(isNone) {
                        fiHolder.progressLoad.setVisibility(View.GONE);
                        fiHolder.progressEnd.setVisibility(View.GONE);
                        fiHolder.progressNone.setVisibility(View.VISIBLE);
                    }
                    else {
                        fiHolder.progressLoad.setVisibility(View.GONE);
                        fiHolder.progressNone.setVisibility(View.GONE);
                        fiHolder.progressEnd.setVisibility(View.VISIBLE);
                    }
                }
                else {
                    fiHolder.progressNone.setVisibility(View.GONE);
                    fiHolder.progressEnd.setVisibility(View.GONE);
                    fiHolder.progressLoad.setVisibility(View.VISIBLE);
                }
            }
        }
    }

    private void bindNormalForumItem(forumItemHolder fiHolder, int position) {
        forum forumItem = forumItems.get(position);
        fiHolder.forumItemTitle.setText(forumItem.getTitle());
        fiHolder.forumItemAuthor.setText(forumItem.getAuthor());
        fiHolder.forumItemTime.setText(timeToNow(forumItem.getTime()));
        fiHolder.forumItemReplyCount.setText(String.format("%s个评论", forumItem.getReply_count()));

    }

    private void bindPicForumItem(forumItemHolder fiHolder, int position) {
        forum forumItem = forumItems.get(position);
        fiHolder.forumItemTitle.setText(forumItem.getTitle());
        fiHolder.forumItemAuthor.setText(forumItem.getAuthor());
        fiHolder.forumItemTime.setText(timeToNow(forumItem.getTime()));
        fiHolder.forumItemReplyCount.setText(String.format("%s个评论", forumItem.getReply_count()));
        Uri uri = Uri.parse(getForumPath()+forumItem.getImg());
        if(forumItem.getImg().contains("gif")) {
            fiHolder.forumItemPicTag.setVisibility(View.VISIBLE);
//            DraweeController controller = Fresco.newDraweeControllerBuilder()
//                    .setUri(uri)
//                    .setAutoPlayAnimations(false)
//                    .build();
//            fiHolder.forumItemPic.setController(controller);

            fiHolder.forumItemPic.setImageURI(getForumPath()+forumItem.getImg().split("\\.")[0]+".jpg");
        }
        else
            fiHolder.forumItemPic.setImageURI(getForumPath()+forumItem.getImg());

    }

    private class forumItemHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        private TextView forumItemTitle;
        private TextView forumItemAuthor;
        private TextView forumItemTime;
        private TextView forumItemReplyCount;
        private SimpleDraweeView forumItemPic;
        private ImageView forumItemPicTag;

        private ProgressBar progressLoad;
        private LinearLayout progressEnd, progressNone;

        private forumItemHolder(View itemView, int viewType) {
            super(itemView);
            if (viewType == TYPE_FOOTER){
                progressLoad = (ProgressBar) itemView.findViewById(R.id.foot_progressbar_load_more);
                progressEnd = (LinearLayout) itemView.findViewById(R.id.foot_progressbar_load_nomore);
                progressNone = (LinearLayout) itemView.findViewById(R.id.foot_progressbar_load_none);
            }
            else if(viewType == TYPE_ERROR) {
            }
            else if(viewType == TYPE_PIC) {
                forumItemTitle = (TextView) itemView.findViewById(R.id.forum_wtpic_title);
                forumItemAuthor = (TextView) itemView.findViewById(R.id.forum_wtpic_author);
                forumItemTime = (TextView) itemView.findViewById(R.id.forum_wtpic_time);
                forumItemReplyCount =(TextView) itemView.findViewById(R.id.forum_wtpic_replycount);
                forumItemPic = (SimpleDraweeView) itemView.findViewById(R.id.forum_wtpic_pic);
                forumItemPicTag = (ImageView) itemView.findViewById(R.id.forum_wtpic_pic_tag);
                itemView.findViewById(R.id.forum_wtpic_container).setOnClickListener(this);
            }
            else {
                forumItemTitle = (TextView) itemView.findViewById(R.id.forum_normal_title);
                forumItemAuthor = (TextView) itemView.findViewById(R.id.forum_normal_author);
                forumItemTime = (TextView) itemView.findViewById(R.id.forum_normal_time);
                forumItemReplyCount =(TextView) itemView.findViewById(R.id.forum_normal_replycount);
                itemView.findViewById(R.id.forum_normal_container).setOnClickListener(this);
            }
        }

        @Override
        public void onClick(View v) {
            Intent intent;
            switch (v.getId()) {
                case R.id.forum_normal_container:
                    intent = new Intent(context, ForumsDetailsActivity.class);
                    intent.putExtra("url", String.valueOf(getForumItems().get(getAdapterPosition()).getF_id()));
                    context.startActivity(intent);
                    break;
                case R.id.forum_wtpic_container:
                    intent = new Intent(context, ForumsDetailsActivity.class);
                    intent.putExtra("url", String.valueOf(getForumItems().get(getAdapterPosition()).getF_id()));
                    context.startActivity(intent);
                    break;
            }

        }

    }
}
