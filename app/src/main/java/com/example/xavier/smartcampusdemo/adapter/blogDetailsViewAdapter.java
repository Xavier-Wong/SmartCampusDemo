package com.example.xavier.smartcampusdemo.adapter;

import android.content.Context;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.xavier.smartcampusdemo.R;
import com.example.xavier.smartcampusdemo.entity.blog;
import com.example.xavier.smartcampusdemo.entity.blog_reply;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.interfaces.DraweeController;
import com.facebook.drawee.view.SimpleDraweeView;
import com.facebook.imagepipeline.request.ImageRequest;

import java.util.ArrayList;
import java.util.List;

import static com.example.xavier.smartcampusdemo.service.NetService.getAvatarPath;
import static com.example.xavier.smartcampusdemo.service.NetService.getBlogPath;

/**
 * Created by Xavier on 5/2/2017.
 *
 */

public class blogDetailsViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    public static final int FIRST_STICKY_VIEW = 1;
    public static final int HAS_STICKY_VIEW = 2;
    public static final int NONE_STICKY_VIEW = 3;

    final private static int TYPE_HEADER = 0;
    final private static int TYPE_COMMEND = 1;
    final private static int TYPE_FOOTER = 2;

    private Context context;
    private List<blog_reply> blogReplyItems;
    private blog itemDetails;
    private boolean isEnd = false;

    public blogDetailsViewAdapter(Context context, List<blog_reply> blogReplyItem) {
        super();
        this.context = context;
        if(blogReplyItems==null) {
            blogReplyItems = new ArrayList<>();
        }
        else {
            this.blogReplyItems = blogReplyItem;
        }
    }

    public void setDetailsObject (blog itemDetails) {
        blogReplyItems.add(new blog_reply());
        this.itemDetails = itemDetails;
    }

    public void addBlogReplyItems(List<blog_reply> newBlogReplyItems) {
        blogReplyItems.addAll(newBlogReplyItems);
    }

    public void removeAll() {
        this.isEnd = false;
        blogReplyItems.clear();
        notifyDataSetChanged();
    }

    public void notifyEnd() {
        blogReplyItems.add(new blog_reply());
        this.isEnd = true;
    }

    @Override
    public int getItemCount() {
        return blogReplyItems.size();
    }

    @Override
    public int getItemViewType(int position) {
        if(position == 0)
            return TYPE_HEADER;
        if(position == getItemCount() - 1 && isEnd)
            return TYPE_FOOTER;
        return TYPE_COMMEND;
    }


    @Override
    public blogReplyHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        if (viewType == TYPE_HEADER) {
            View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.activity_blog_details_header, viewGroup, false);
            return new blogReplyHolder(v, viewType);
        }
        if (viewType == TYPE_FOOTER) {
            View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.progress_footerview, viewGroup, false);
            return new blogReplyHolder(v, viewType);
        }
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_blog_reply_normal, viewGroup, false);
        return new blogReplyHolder(v, viewType);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        blogReplyHolder holder1 = (blogReplyHolder) holder;
        if(getItemViewType(position) == TYPE_HEADER) {
            holder1.blog_avatar.setImageURI(getAvatarPath()+itemDetails.getAvatar());
            holder1.blog_author.setText(itemDetails.getAuthor());
            holder1.blog_time.setText(itemDetails.getTime());
            holder1.blog_content.setText(itemDetails.getContent());
            if(!itemDetails.getImg().equals("")) {
                holder1.blog_img.setVisibility(View.VISIBLE);
                for(int a = 0; a < itemDetails.getImg().split(";").length; a++) {
                    holder1.blog_img_list.get(a).setVisibility(View.VISIBLE);
                    if(itemDetails.getImg().split(";")[a].contains("gif")) {
                        Uri lowResUri = Uri.parse(getBlogPath() + itemDetails.getImg().split(";")[a].split("\\.")[0] + ".jpg");
                        Uri highResUri = Uri.parse(getBlogPath() + itemDetails.getImg().split(";")[a]);
                        DraweeController controller = Fresco.newDraweeControllerBuilder()
                                .setLowResImageRequest(ImageRequest.fromUri(lowResUri))
                                .setImageRequest(ImageRequest.fromUri(highResUri))
                                .setAutoPlayAnimations(true)
                                .build();
                        holder1.blog_img_list.get(a).setController(controller);
                    }
                    else
                        holder1.blog_img_list.get(a).setImageURI(getBlogPath()+itemDetails.getImg().split(";")[a]);
                }
            }
            else holder1.blog_img.setVisibility(View.GONE);
            holder1.itemView.setTag(FIRST_STICKY_VIEW);
        }
        else if(getItemViewType(position) == TYPE_COMMEND){
            if(position == 1) {
                holder1.forum_divider_img.setVisibility(View.VISIBLE);
                holder1.itemView.setTag(HAS_STICKY_VIEW);
            }
            else {
                holder1.forum_divider_img.setVisibility(View.GONE);
                holder1.itemView.setTag(NONE_STICKY_VIEW);
            }
            blog_reply blogReplyItem = blogReplyItems.get(position);
            holder1.blogReplyCount.setText(String.valueOf(itemDetails.getReply_count()));
            holder1.blogLikeCount.setText(String.valueOf(itemDetails.getLike()));
            holder1.blogReplyItemAvatar.setImageURI(getAvatarPath()+blogReplyItem.getAvatar());
            holder1.blogReplyItemAuthor.setText(blogReplyItem.getAuthor());
            holder1.blogReplyItemContent.setText(blogReplyItem.getContent());
            holder1.blogReplyItemTime.setText(blogReplyItem.getTime());
        }
        else if(getItemViewType(position) == TYPE_FOOTER) {
            if(holder1 != null) {
                if (holder1.progressLoad.getVisibility() == View.VISIBLE) {
                    holder1.progressLoad.setVisibility(View.GONE);
                    holder1.progressEnd.setVisibility(View.VISIBLE);
                }
            }
        }
    }

    private class blogReplyHolder extends RecyclerView.ViewHolder {

        SimpleDraweeView blog_avatar;
        TextView blog_author;
        TextView blog_time;
        TextView blog_content;
        View blog_img;
        SimpleDraweeView blog_img1, blog_img2, blog_img3;
        List<SimpleDraweeView> blog_img_list = new ArrayList<>();

        LinearLayout forum_divider_img;
        SimpleDraweeView blogReplyItemAvatar;
        TextView blogReplyItemAuthor;
        TextView blogReplyItemTime;
        TextView blogReplyItemContent;

        TextView blogReplyCount;
        TextView blogLikeCount;

        private ProgressBar progressLoad;
        private LinearLayout progressEnd;

        blogReplyHolder(View itemView, int viewType) {
            super(itemView);
            if(viewType == TYPE_HEADER) {
                blog_avatar = (SimpleDraweeView) itemView.findViewById(R.id.blog_details_avatar);
                blog_author = (TextView) itemView.findViewById(R.id.blog_details_author);
                blog_time = (TextView) itemView.findViewById(R.id.blog_details_time);
                blog_content = (TextView) itemView.findViewById(R.id.blog_details_content);
                blog_img = itemView.findViewById(R.id.blog_details_img_holder);
                blog_img1 = (SimpleDraweeView) itemView.findViewById(R.id.blog_details_img1);
                blog_img2 = (SimpleDraweeView) itemView.findViewById(R.id.blog_details_img2);
                blog_img3 = (SimpleDraweeView) itemView.findViewById(R.id.blog_details_img3);
                blog_img_list.add(blog_img1);
                blog_img_list.add(blog_img2);
                blog_img_list.add(blog_img3);
            }
            else if (viewType == TYPE_FOOTER){
                progressLoad = (ProgressBar) itemView.findViewById(R.id.foot_progressbar_load_more);
                progressEnd = (LinearLayout) itemView.findViewById(R.id.foot_progressbar_load_noreply);
            }
            else {
                forum_divider_img = (LinearLayout) itemView.findViewById(R.id.blog_reply_sticker);
                blogReplyItemAvatar = (SimpleDraweeView) itemView.findViewById(R.id.blog_comment_avatar);
                blogReplyItemAuthor = (TextView) itemView.findViewById(R.id.blog_comment_author);
                blogReplyItemTime = (TextView) itemView.findViewById(R.id.blog_comment_time);
                blogReplyItemContent = (TextView) itemView.findViewById(R.id.blog_comment_content);
                blogReplyCount = (TextView) itemView.findViewById(R.id.blog_details_comment_count);
                blogLikeCount = (TextView) itemView.findViewById(R.id.blog_details_like_count);
                itemView.findViewById(R.id.blog_reply_container);
            }
        }

    }
}
