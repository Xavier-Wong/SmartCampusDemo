package com.example.xavier.smartcampusdemo.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.xavier.smartcampusdemo.R;
import com.example.xavier.smartcampusdemo.activity.BlogDetailsActivity;
import com.example.xavier.smartcampusdemo.entity.blog;
import com.facebook.drawee.view.SimpleDraweeView;

import java.util.ArrayList;
import java.util.List;

import static com.example.xavier.smartcampusdemo.service.NetService.getAvatarPath;
import static com.example.xavier.smartcampusdemo.service.NetService.getBlogPath;

/**
 * Created by Xavier on 3/23/2017.
 *
 */

public class microBlogViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{

    private static final int TYPE_NORMAL = 0;  //说明是不带有header和footer的
    private static final int TYPE_PIC = 1;
    private static final int TYPE_FOOTER = 2;  //说明是带有Footer的
    private static final int TYPE_ERROR = 3;

    private List<blog> blogItems;
    private Context context;
    private boolean isEnd = false;
    private boolean isNone = false;

    public microBlogViewAdapter(Context context, List<blog> blogItem) {
        super();
        this.context = context;
        if(blogItems==null) {
            blogItems = new ArrayList<>();
        }
        else {
            this.blogItems = blogItem;
        }
    }

    public void addBlogItem (List<blog> newBlogItems) {
        blogItems.addAll(newBlogItems);
    }

    private List<blog> getBlogItems() {
        return blogItems;
    }

    public void removeAll() {
        this.isEnd = false;
        this.isNone = false;
        blogItems.clear();
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
        return blogItems.size() + 1;
    }

    @Override
    public int getItemViewType(int position) {
        if (position == getItemCount()-1){
            //最后一个,应该加载Footer
            return TYPE_FOOTER;
        }
        if(blogItems.get(position).getU_id() == 0) {
            return TYPE_ERROR;
        }
        if(blogItems.get(position).getImg().equals(""))
            return TYPE_NORMAL;
        else
            return TYPE_PIC;
    }

    @Override
    public blogItemHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        if(viewType == TYPE_ERROR) {
            View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_normal_error, viewGroup, false);
            return new blogItemHolder(v, viewType);
        }
        else if(viewType == TYPE_FOOTER){
            View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.progress_footerview, viewGroup, false);
            return new blogItemHolder(v, viewType);
        }else if(viewType == TYPE_PIC){
            View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_blog_with_picture, viewGroup, false);
            return new blogItemHolder(v, viewType);
        }else {
            View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_blog_normal, viewGroup, false);
            return new blogItemHolder(v, viewType);
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        blogItemHolder biHolder = (blogItemHolder) holder;
        if(getItemViewType(position) == TYPE_NORMAL){
            if(biHolder != null) {
                //这里加载数据的时候要注意，是从position-1开始，因为position==0已经被header占用了
                bindBlogItem(biHolder,position);
            }
        }
        if(getItemViewType(position) == TYPE_PIC){
            if(biHolder != null) {
                //这里加载数据的时候要注意，是从position-1开始，因为position==0已经被header占用了
                bindPicBlogItem(biHolder, position);
            }
        }
        if(getItemViewType(position) == TYPE_FOOTER) {
            if(biHolder != null) {
                if(isEnd) {
                    if(isNone) {
                        biHolder.progressLoad.setVisibility(View.GONE);
                        biHolder.progressEnd.setVisibility(View.GONE);
                        biHolder.progressNone.setVisibility(View.VISIBLE);
                    }
                    else {
                        biHolder.progressLoad.setVisibility(View.GONE);
                        biHolder.progressNone.setVisibility(View.GONE);
                        biHolder.progressEnd.setVisibility(View.VISIBLE);
                    }
                }
                else {
                    biHolder.progressNone.setVisibility(View.GONE);
                    biHolder.progressEnd.setVisibility(View.GONE);
                    biHolder.progressLoad.setVisibility(View.VISIBLE);
                }
            }
        }
    }

    private void bindBlogItem(blogItemHolder biHolder, int position) {
        //这里加载数据的时候要注意，是从position-1开始，因为position==0已经被header占用了
        blog blogItem = blogItems.get(position);

        biHolder.blogItemAvatar.setImageURI(getAvatarPath()+blogItem.getAvatar());
        biHolder.blogItemAuthor.setText(blogItem.getAuthor());
        biHolder.blogItemContent.setText(blogItem.getContent());
        biHolder.blogItemTime.setText(blogItem.getTime());
        biHolder.blogItemLike.setText(String.valueOf(blogItem.getLike()));
        biHolder.blogItemDisLike.setText(String.valueOf(blogItem.getReply_count()));

    }

    private void bindPicBlogItem(blogItemHolder biHolder, int position) {
        //这里加载数据的时候要注意，是从position-1开始，因为position==0已经被header占用了
        blog blogItem = blogItems.get(position);

        biHolder.blogItemAvatar.setImageURI(getAvatarPath()+blogItem.getAvatar());
        biHolder.blogItemAuthor.setText(blogItem.getAuthor());
        biHolder.blogItemContent.setText(blogItem.getContent());
        biHolder.blogItemTime.setText(blogItem.getTime());
        biHolder.blogItemLike.setText(String.valueOf(blogItem.getLike()));
        biHolder.blogItemDisLike.setText(String.valueOf(blogItem.getReply_count()));
        for(int a = 0; a < blogItem.getImg().split(";").length; a++) {
            biHolder.sdv_img.get(a).setVisibility(View.VISIBLE);
            if(blogItem.getImg().split(";")[a].contains("gif")) {
                biHolder.sdv_img_tag.get(a).setVisibility(View.VISIBLE);
                biHolder.sdv_img.get(a).setImageURI(getBlogPath()+blogItem.getImg().split(";")[a].split("\\.")[0]+".jpg");
            }
            else {
                biHolder.sdv_img_tag.get(a).setVisibility(View.INVISIBLE);
                biHolder.sdv_img.get(a).setImageURI(getBlogPath() + blogItem.getImg().split(";")[a]);
            }
        }
        if(blogItem.getImg().split(";").length == 1) {
            biHolder.sdv_img.get(1).setImageURI("");
            biHolder.sdv_img.get(2).setImageURI("");
        }
        else if(blogItem.getImg().split(";").length == 2) {
            biHolder.sdv_img.get(2).setImageURI("");
        }
    }

    private class blogItemHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        List<SimpleDraweeView> sdv_img = new ArrayList<>();
        List<ImageView> sdv_img_tag = new ArrayList<>();
        private SimpleDraweeView blogItemAvatar;
        private TextView blogItemAuthor;
        private TextView blogItemTime;
        private TextView blogItemContent;
        private TextView blogItemLike;
        private TextView blogItemDisLike;
        private SimpleDraweeView blogImg1;
        private SimpleDraweeView blogImg2;
        private SimpleDraweeView blogImg3;
        private ImageView blogImgTag1;
        private ImageView blogImgTag2;
        private ImageView blogImgTag3;

        private ProgressBar progressLoad;
        private LinearLayout progressEnd, progressNone;


        private blogItemHolder(View itemView, int viewType) {
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
            if(viewType == TYPE_PIC) {
                blogItemAvatar = (SimpleDraweeView) itemView.findViewById(R.id.blog_wtpic_avatar);
                blogItemAuthor = (TextView) itemView.findViewById(R.id.blog_wtpic_author);
                blogItemTime = (TextView) itemView.findViewById(R.id.blog_wtpic_time);
                blogItemContent = (TextView) itemView.findViewById(R.id.blog_wtpic_content);
                blogItemLike = (TextView) itemView.findViewById(R.id.blog_wtpic_like);
                blogItemDisLike = (TextView) itemView.findViewById(R.id.blog_wtpic_dislike);
                blogImg1 = (SimpleDraweeView) itemView.findViewById(R.id.blog_img1);
                blogImg2 = (SimpleDraweeView) itemView.findViewById(R.id.blog_img2);
                blogImg3 = (SimpleDraweeView) itemView.findViewById(R.id.blog_img3);
                blogImgTag1 = (ImageView) itemView.findViewById(R.id.blog_wtpic_pic_tag1);
                blogImgTag2 = (ImageView) itemView.findViewById(R.id.blog_wtpic_pic_tag2);
                blogImgTag3 = (ImageView) itemView.findViewById(R.id.blog_wtpic_pic_tag3);
                sdv_img.clear();
                sdv_img.add(blogImg1);
                sdv_img.add(blogImg2);
                sdv_img.add(blogImg3);
                sdv_img_tag.add(blogImgTag1);
                sdv_img_tag.add(blogImgTag2);
                sdv_img_tag.add(blogImgTag3);
                itemView.findViewById(R.id.blog_wtpic_container).setOnClickListener(this);
            }
            else {
                blogItemAvatar = (SimpleDraweeView) itemView.findViewById(R.id.blog_normal_avatar);
                blogItemAuthor = (TextView) itemView.findViewById(R.id.blog_normal_author);
                blogItemTime = (TextView) itemView.findViewById(R.id.blog_normal_time);
                blogItemContent = (TextView) itemView.findViewById(R.id.blog_normal_content);
                blogItemLike = (TextView) itemView.findViewById(R.id.blog_normal_like);
                blogItemDisLike = (TextView) itemView.findViewById(R.id.blog_normal_dislike);
                itemView.findViewById(R.id.blog_normal_container).setOnClickListener(this);
            }
        }

        @Override
        public void onClick(View v) {
            Intent intent;
            switch (v.getId()) {
                case R.id.blog_normal_container:
                case R.id.blog_wtpic_container:
                    intent = new Intent(context, BlogDetailsActivity.class);
                    intent.putExtra("bid", String.valueOf(getBlogItems().get(getAdapterPosition()).getB_id()));
                    context.startActivity(intent);
                    break;
            }

        }

    }

}
