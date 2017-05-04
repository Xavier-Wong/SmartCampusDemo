package com.example.xavier.smartcampusdemo.Adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.xavier.smartcampusdemo.Entity.forum_reply;
import com.example.xavier.smartcampusdemo.R;
import com.example.xavier.smartcampusdemo.Util.NetUtil.LoadImageUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

import static com.example.xavier.smartcampusdemo.Service.NetService.getIP;
import static com.example.xavier.smartcampusdemo.Util.TimeConvertor.stampToDate;

/**
 * Created by Xavier on 3/7/2017.
 *
 */

public class techForumDetailsViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    final private static int TYPE_TITLE = 0;
    final private static int TYPE_AUTHOR = 1;
    final private static int TYPE_CONTENT = 2;
    final private static int TYPE_COMMEND = 3;
    final private static int TYPE_FOOTER = 4;

    private Context context;
    private List<forum_reply> forumReplyItems;
    private JSONObject itemDetails;

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

    public void setDetailsObject (JSONObject itemDetails) {
        this.itemDetails = itemDetails;
    }
    private JSONObject getDetailsObject() {
        return itemDetails;
    }

    public void addForumReplyItem (List<forum_reply> newForumReplyItem) {
        forumReplyItems.addAll(newForumReplyItem);
    }

    public void removeAll() {
        forumReplyItems.clear();
        notifyDataSetChanged();
    }
    @Override
    public int getItemCount() {
        return forumReplyItems.size()+3;
    }

    @Override
    public int getItemViewType(int position) {
        if (position == getItemCount()-1 && position >= 13){
            //最后一个,应该加载Footer
            return TYPE_FOOTER;
        }
        if(position == 0)
            return TYPE_TITLE;
        else if (position == 1)
            return TYPE_AUTHOR;
        else if (position == 2)
            return TYPE_CONTENT;
        else
            return TYPE_COMMEND;
    }

    @Override
    public forumItemDetailsHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        if (viewType == TYPE_TITLE) {
            View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.activity_forums_title_info, viewGroup, false);
            return new forumItemDetailsHolder(v, viewType);
        }
        if(viewType == TYPE_FOOTER){
            View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.progress_footerview, viewGroup, false);
            return new forumItemDetailsHolder(v, viewType);
        }
        if (viewType == TYPE_AUTHOR) {
            View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.activity_forums_author_info, viewGroup, false);
            return new forumItemDetailsHolder(v, viewType);
        }
        if (viewType == TYPE_CONTENT) {
            View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.activity_forums_content_info, viewGroup, false);
            return new forumItemDetailsHolder(v, viewType);
        }
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.activity_forums_reply_info, viewGroup, false);
        return new forumItemDetailsHolder(v, viewType);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        forumItemDetailsHolder holder1 = (forumItemDetailsHolder) holder;
        if(getItemViewType(position) == TYPE_TITLE) {
            try {
                holder1.title.setText(getDetailsObject().getString("title"));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        else if(getItemViewType(position) == TYPE_AUTHOR) {
            try {
                holder1.author.setText(getDetailsObject().getString("author"));
                holder1.time.setText(stampToDate(getDetailsObject().getString("time")));
                holder1.setU_id(getDetailsObject().getInt("u_id"));
                holder1.setAvatar_str(getDetailsObject().getString("avatar"));
                new MyAsyncTaskGetUserAvatar().execute(holder1);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        else if(getItemViewType(position) == TYPE_CONTENT) {
            try {
                holder1.content.setText(getDetailsObject().getString("content"));
                holder1.likecount.setText(String.valueOf(getDetailsObject().getString("like")));
                holder1.dislikecount.setText(String.valueOf(getDetailsObject().getString("dislike")));
                if(!getDetailsObject().getString("img").equals("")) {
                    holder1.setImg_str(getDetailsObject().getString("img"));
                    new MyAsyncTaskGetBlogImg().execute(holder1);
                }
                else {
                    holder1.setForumImgNull(0);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        else if(getItemViewType(position) == TYPE_FOOTER) {
        }
        else {
            forum_reply forumReplyItem = forumReplyItems.get(position-3);
            holder1.comment_username.setText(forumReplyItem.getAuthor());
            holder1.comment_content.setText(forumReplyItem.getContent());
            holder1.comment_time.setText(forumReplyItem.getTime());
        }
//        holder1.tv.setText(items.get(position));
    }

    private class forumItemDetailsHolder extends RecyclerView.ViewHolder {

        Integer u_id;
        String avatar_str;
        String img_str;
        CircleImageView avatar;
        TextView author;
        TextView time;
        TextView title;
        TextView content;
        TextView comment_username;
        TextView comment_content;
        TextView comment_time;
        LinearLayout forum_like,forum_dislike;
        ImageView likeimg,dislikeimg;
        TextView likecount,dislikecount;
        ImageView forum_img1;
        ImageView forum_img2;
        ImageView forum_img3;

        void setForumImgNull(int which) {
            ViewGroup.LayoutParams params = forum_img1.getLayoutParams();
            params.height = 0;
            forum_img1.setLayoutParams(params);
            forum_img2.setLayoutParams(params);
            forum_img3.setLayoutParams(params);
        }
        void setImg_str(String img_str) {
            this.img_str = img_str;
        }
        String getImg_str() {
            return this.img_str;
        }
        void setU_id(Integer u_id) {
            this.u_id = u_id;
        }
        Integer getU_id() {
            return this.u_id;
        }
        void setAvatar_str(String avatar_str) {
            this.avatar_str = avatar_str;
        }
        String getAvatar_str() {
            return this.avatar_str;
        }
        private forumItemDetailsHolder(View itemView, int viewType) {
            super(itemView);
            if (viewType == TYPE_FOOTER) {
                return;
            }
            if(viewType == TYPE_TITLE) {
                title = (TextView) itemView.findViewById(R.id.details_title);
            }
            if(viewType == TYPE_AUTHOR) {
                avatar = (CircleImageView) itemView.findViewById(R.id.details_author_avatar);
                author = (TextView) itemView.findViewById(R.id.details_author_name);
                time = (TextView) itemView.findViewById(R.id.details_time);
            }
            else if (viewType == TYPE_CONTENT) {
                content = (TextView) itemView.findViewById(R.id.details_content);
                forum_like = (LinearLayout) itemView.findViewById(R.id.details_like);
                forum_dislike = (LinearLayout) itemView.findViewById(R.id.details_dislike);
                likeimg = (ImageView) itemView.findViewById(R.id.details_like_icon);
                likecount = (TextView) itemView.findViewById(R.id.details_like_count);
                dislikeimg = (ImageView) itemView.findViewById(R.id.details_dislike_icon);
                dislikecount = (TextView) itemView.findViewById(R.id.details_dislike_count);
                forum_img1 = (ImageView) itemView.findViewById(R.id.forum_img1);
                forum_img2 = (ImageView) itemView.findViewById(R.id.forum_img2);
                forum_img3 = (ImageView) itemView.findViewById(R.id.forum_img3);

                forum_like.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        forum_like.setBackgroundResource(R.drawable.ic_forum_like_after);
                        likeimg.setBackgroundResource(R.drawable.ic_thumb_up_after);
                        likecount.setTextColor(0xFFFF6347);
                    }
                });

            }
            else {
                comment_username = (TextView) itemView.findViewById(R.id.details_reply_author);
                comment_content = (TextView) itemView.findViewById(R.id.details_reply_content);
                comment_time = (TextView) itemView.findViewById(R.id.details_reply_time);
            }
        }

    }

    private class MyAsyncTaskGetUserAvatar extends AsyncTask<forumItemDetailsHolder, String, CircleImageView> {
        Bitmap avatar;
        @Override
        protected CircleImageView doInBackground(forumItemDetailsHolder... biHolders) {
            avatar = LoadImageUtil.sendGets("http://"+getIP()+"/HelloWeb/Upload/Avatar/"+biHolders[0].getAvatar_str());
            return biHolders[0].avatar;
        }

        @Override
        protected void onPostExecute(CircleImageView avatar_btm) {
            avatar_btm.setImageBitmap(avatar);
        }
    }
    private class MyAsyncTaskGetBlogImg extends AsyncTask<forumItemDetailsHolder, String, forumItemDetailsHolder> {
        List<Bitmap> img  = new ArrayList<>();
        @Override
        protected forumItemDetailsHolder doInBackground(forumItemDetailsHolder... biHolders) {
            img.clear();
            if(biHolders[0].getImg_str().contains(";")) {
                for(int i = 0,j;;) {
                    if((j = biHolders[0].getImg_str().indexOf(";", i)) != -1) {
                        img.add(LoadImageUtil.sendGets("http://"+getIP()+"/HelloWeb/Upload/Forum/"+biHolders[0].getImg_str().substring(i, j)));
                        i = j+1;
                    }
                    else break;
                }
            }
            return biHolders[0];
        }

        @Override
        protected void onPostExecute(forumItemDetailsHolder bih) {
            int count_img;
            for(count_img = 0;count_img<img.size();count_img++) {
                ViewGroup.LayoutParams para = bih.forum_img1.getLayoutParams();
                para.height = 500;
                if(count_img == 0) {
                    bih.forum_img1.setImageBitmap(img.get(count_img));
                    bih.forum_img2.setImageBitmap(null);
                    bih.forum_img3.setImageBitmap(null);
                    bih.forum_img1.setLayoutParams(para);
                }
                else if(count_img == 1) {
                    bih.forum_img2.setImageBitmap(img.get(count_img));
                    bih.forum_img3.setImageBitmap(null);
                    bih.forum_img2.setLayoutParams(para);
                }
                else if(count_img == 2) {
                    bih.forum_img3.setImageBitmap(img.get(count_img));
                    bih.forum_img3.setLayoutParams(para);
                }
            }
        }
    }
}