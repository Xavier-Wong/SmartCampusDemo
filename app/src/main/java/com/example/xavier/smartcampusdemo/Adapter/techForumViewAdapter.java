package com.example.xavier.smartcampusdemo.Adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.xavier.smartcampusdemo.Activity.ForumsDetailsActivity;
import com.example.xavier.smartcampusdemo.Activity.MainActivity;
import com.example.xavier.smartcampusdemo.Entity.forum;
import com.example.xavier.smartcampusdemo.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Xavier on 11/12/2016.
 * techForumAdapter
 */

public class techForumViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int TYPE_HEADER = 0;  //说明是带有Header的
    private static final int TYPE_FOOTER = 1;  //说明是带有Footer的
    private static final int TYPE_NORMAL = 2;  //说明是不带有header和footer的

    private int HEADER_INSERT = 0, FOOTER_INSERT = 0;
    private View mHeaderView,mFooterView;
    private List<forum> forumItems;
    private RecyclerView mRecyclerView;
    private Context context;

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

    public List<forum> getForumItems() {
        return forumItems;
    }

    public void removeAll() {
        forumItems.clear();
        notifyDataSetChanged();
    }
    @Override
    public int getItemCount() {
        if(mHeaderView == null && mFooterView == null){
            return forumItems.size();
        }else if(mHeaderView == null){
            return forumItems.size() + 1;
        }else if (mFooterView == null){
            return forumItems.size() + 1;
        }else {
            return forumItems.size() + 2;
        }
    }

    //HeaderView和FooterView的get和set函数
    public View getHeaderView() {
        return mHeaderView;
    }
    public void setHeaderView(View headerView) {
        mHeaderView = headerView;
        HEADER_INSERT = 1;
        notifyItemInserted(0);
    }
    public View getFooterView() {
        return mFooterView;
    }
    public void setFooterView(View footerView) {
        mFooterView = footerView;
        FOOTER_INSERT = 1;
        notifyItemInserted(getItemCount()-1);
    }


    @Override
    public int getItemViewType(int position) {
        if (HEADER_INSERT == 0 && FOOTER_INSERT == 0){
            return TYPE_NORMAL;
        }
        if (position == getItemCount()-1){
            //最后一个,应该加载Footer
            return TYPE_FOOTER;
        }
        return TYPE_NORMAL;
    }

    @Override
    public forumItemHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        if(mFooterView != null && viewType == TYPE_FOOTER){
            return new forumItemHolder(mFooterView);
        }
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.techforum_item_normal, viewGroup, false);
        return new forumItemHolder(v);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        forumItemHolder fiHolder = (forumItemHolder) holder;
        if(getItemViewType(position) == TYPE_NORMAL){
            if(fiHolder != null) {
                //这里加载数据的时候要注意，是从position-1开始，因为position==0已经被header占用了
                bindForumItem(position, fiHolder.forumItemTitle, fiHolder.forumItemAuthor, fiHolder.forumItemTime);
                return;
            }
            return;
        }else if(getItemViewType(position) == TYPE_HEADER){
            return;
        }else{
            return;
        }
    }

    private void bindForumItem(int position, TextView forumTitle, TextView forumAuthor, TextView forumTime) {
        forum forumItem = forumItems.get(position);
        forumTitle.setText(forumItem.getTitle());
        forumAuthor.setText(forumItem.getAuthor());
        forumTime.setText(forumItem.getTime());
    }

    private class forumItemHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        float x;
        float y;
        private TextView forumItemTitle;
        private TextView forumItemAuthor;
        private TextView forumItemTime;

        private forumItemHolder(View itemView) {
            super(itemView);
            if (itemView == mHeaderView){
                return;
            }
            if (itemView == mFooterView){
                return;
            }
            forumItemTitle = (TextView) itemView.findViewById(R.id.fragment_forumitems_title);
            forumItemAuthor = (TextView) itemView.findViewById(R.id.fragment_forumitems_author);
            forumItemTime = (TextView) itemView.findViewById(R.id.fragment_forumitems_time);
            itemView.findViewById(R.id.forum_item_container).setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.forum_item_container:
                        Intent intent = new Intent(context, ForumsDetailsActivity.class);
                        intent.putExtra("url", String.valueOf(getForumItems().get(getAdapterPosition()).getF_id()));
                        context.startActivity(intent);
            }

        }

    }

}
