package com.example.xavier.smartcampusdemo.ViewHolder;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.example.xavier.smartcampusdemo.R;

/**
 * Created by Xavier on 3/1/2017.
 */

public class forumItemHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

    public TextView forumItemTitle;
    public TextView forumItemAuthor;
    public TextView forumItemTime;

    public forumItemHolder(View itemView) {
        super(itemView);

        forumItemTitle = (TextView) itemView.findViewById(R.id.fragment_forumitems_listview_title);
        forumItemAuthor = (TextView) itemView.findViewById(R.id.fragment_forumitems_listview_source);
        forumItemTime = (TextView) itemView.findViewById(R.id.fragment_forumitems_listview_time);
        itemView.findViewById(R.id.forum_item_container).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.forum_item_container:
                break;
        }
    }
}
