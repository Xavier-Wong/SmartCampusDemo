package com.example.xavier.smartcampusdemo.ViewHolder;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.xavier.smartcampusdemo.R;

/**
 * Created by Xavier on 11/27/2016.
 */

public class videoItemHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

    public ImageView videoItemIcon;
    public TextView videoItemTitle;
    public TextView videoItemTime;

    public videoItemHolder(View itemView) {
        super(itemView);
        videoItemIcon = (ImageView) itemView.findViewById(R.id.base_video_item_icon);
        videoItemTitle = (TextView) itemView.findViewById(R.id.base_video_item_title);
        videoItemTime = (TextView) itemView.findViewById(R.id.base_video_item_time);
        itemView.findViewById(R.id.base_video_item_container).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.base_video_item_container:
                break;
        }
    }
}
