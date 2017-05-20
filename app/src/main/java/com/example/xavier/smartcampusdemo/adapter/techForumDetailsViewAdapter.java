package com.example.xavier.smartcampusdemo.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.xavier.smartcampusdemo.entity.forum;
import com.example.xavier.smartcampusdemo.entity.forum_reply;
import com.example.xavier.smartcampusdemo.R;
import com.example.xavier.smartcampusdemo.util.NetUtil.LoadImageUtil;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.interfaces.DraweeController;
import com.facebook.drawee.view.SimpleDraweeView;
import com.facebook.imagepipeline.request.ImageRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

import static com.example.xavier.smartcampusdemo.service.NetService.getForumPath;
import static com.example.xavier.smartcampusdemo.service.NetService.getIP;
import static com.example.xavier.smartcampusdemo.util.TimeConvertor.stampToDate;

/**
 * Created by Xavier on 3/7/2017.
 *
 */

public class techForumDetailsViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {


    public static final int FIRST_STICKY_VIEW = 1;
    public static final int HAS_STICKY_VIEW = 2;
    public static final int NONE_STICKY_VIEW = 3;

    final private static int TYPE_HEADER = 0;
    final private static int TYPE_COMMEND = 1;
    final private static int TYPE_FOOTER = 2;

    private Context context;
    private List<forum_reply> forumReplyItems;
    private forum itemDetails;
    private boolean isEnd = false;

    public techForumDetailsViewAdapter(Context context, List<forum_reply> forumReplyItem) {
        super();
        this.context = context;
        if(forumReplyItems==null) {
            forumReplyItems = new ArrayList<>();
        }
        else {
            this.forumReplyItems = forumReplyItem;
        }
    }

    public void setDetailsObject (forum itemDetails) {
        forumReplyItems.add(new forum_reply());
        this.itemDetails = itemDetails;
    }

    public void addForumReplyItem (List<forum_reply> newForumReplyItem) {
        forumReplyItems.addAll(newForumReplyItem);
    }

    public void removeAll() {
        forumReplyItems.clear();
        forumReplyItems.add(new forum_reply());
        notifyDataSetChanged();
    }

    public void notifyEnd() {
        forumReplyItems.add(new forum_reply());
        this.isEnd = true;
    }

    @Override
    public int getItemCount() {
        return forumReplyItems.size();
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
    public forumItemDetailsHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        if (viewType == TYPE_HEADER) {
            View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.activity_forum_details_header, viewGroup, false);
            return new forumItemDetailsHolder(v, viewType);
        }
        if (viewType == TYPE_FOOTER) {
            View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.progress_footerview, viewGroup, false);
            return new forumItemDetailsHolder(v, viewType);
        }
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.activity_forums_reply_info, viewGroup, false);
        return new forumItemDetailsHolder(v, viewType);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        forumItemDetailsHolder holder1 = (forumItemDetailsHolder) holder;
        if(getItemViewType(position) == TYPE_HEADER) {
            holder1.forum_title.setText(itemDetails.getTitle());
            holder1.forum_content.setText(itemDetails.getContent());
            if(!itemDetails.getImg().equals("")) {
                holder1.forum_img.setVisibility(View.VISIBLE);
                for(int a = 0; a < itemDetails.getImg().split(";").length; a++) {
                    holder1.forum_img_list.get(a).setVisibility(View.VISIBLE);
                    if(itemDetails.getImg().split(";")[a].contains("gif")) {
                        Uri lowResUri = Uri.parse(getForumPath()+itemDetails.getImg().split(";")[a].split("\\.")[0]+".jpg");
                        Uri highResUri = Uri.parse(getForumPath()+itemDetails.getImg().split(";")[a]);
                        DraweeController controller = Fresco.newDraweeControllerBuilder()
                                .setLowResImageRequest(ImageRequest.fromUri(lowResUri))
                                .setImageRequest(ImageRequest.fromUri(highResUri))
                                .setAutoPlayAnimations(true)
                                .build();
                        holder1.forum_img_list.get(a).setController(controller);
                    }
                    else
                        holder1.forum_img_list.get(a).setImageURI(getForumPath()+itemDetails.getImg().split(";")[a]);
                }
            }
            else holder1.forum_img.setVisibility(View.GONE);
            holder1.itemView.setTag(FIRST_STICKY_VIEW);
        }
        else if(getItemViewType(position) == TYPE_COMMEND){
            if(position == 1) {
                holder1.forum_divider_img.setVisibility(View.VISIBLE);
                holder1.itemView.setTag(HAS_STICKY_VIEW);
            }
            else{
                holder1.forum_divider_img.setVisibility(View.GONE);
                holder1.itemView.setTag(NONE_STICKY_VIEW);
            }
            forum_reply forumReplyItem = forumReplyItems.get(position);
            holder1.comment_username.setText(forumReplyItem.getAuthor());
            holder1.comment_content.setText(forumReplyItem.getContent());
            holder1.comment_time.setText(forumReplyItem.getTime());
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

    private class forumItemDetailsHolder extends RecyclerView.ViewHolder {

        TextView forum_title;
        TextView forum_content;
        View forum_img;
        SimpleDraweeView forum_img1, forum_img2, forum_img3;
        List<SimpleDraweeView> forum_img_list = new ArrayList<>();
        LinearLayout forum_divider_img;

        TextView comment_username;
        TextView comment_content;
        TextView comment_time;

        private ProgressBar progressLoad;
        private LinearLayout progressEnd;

        private forumItemDetailsHolder(View itemView, int viewType) {
            super(itemView);
            if(viewType == TYPE_HEADER) {
                forum_title = (TextView) itemView.findViewById(R.id.forum_details_title);
                forum_content = (TextView) itemView.findViewById(R.id.forum_details_content);
                forum_img = itemView.findViewById(R.id.forum_details_img_holder);
                forum_img1 = (SimpleDraweeView) itemView.findViewById(R.id.forum_details_img1);
                forum_img2 = (SimpleDraweeView) itemView.findViewById(R.id.forum_details_img2);
                forum_img3 = (SimpleDraweeView) itemView.findViewById(R.id.forum_details_img3);
                forum_img_list.add(forum_img1);
                forum_img_list.add(forum_img2);
                forum_img_list.add(forum_img3);
            }
            else if (viewType == TYPE_FOOTER){
                progressLoad = (ProgressBar) itemView.findViewById(R.id.foot_progressbar_load_more);
                progressEnd = (LinearLayout) itemView.findViewById(R.id.foot_progressbar_load_noreply);
            }
            else {
                forum_divider_img = (LinearLayout) itemView.findViewById(R.id.forum_reply_sticker);
                comment_username = (TextView) itemView.findViewById(R.id.details_reply_author);
                comment_content = (TextView) itemView.findViewById(R.id.details_reply_content);
                comment_time = (TextView) itemView.findViewById(R.id.details_reply_time);
            }
        }

    }

}