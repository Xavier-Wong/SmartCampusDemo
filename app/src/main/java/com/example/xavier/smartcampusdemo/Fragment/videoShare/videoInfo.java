package com.example.xavier.smartcampusdemo.Fragment.videoShare;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.xavier.smartcampusdemo.Entity.video;
import com.example.xavier.smartcampusdemo.R;
import com.example.xavier.smartcampusdemo.Service.VideoItemService;
import com.example.xavier.smartcampusdemo.Util.NetUtil.LoadImageUtil;

import java.io.File;

import de.hdodenhof.circleimageview.CircleImageView;

import static com.example.xavier.smartcampusdemo.Service.NetService.getIP;

/**
 * Created by Xavier on 4/30/2017.
 *
 */

public class videoInfo extends Fragment {

    String id = "";

    TextView tv_title;
    CircleImageView tv_avatar;
    TextView tv_author;
    TextView tv_like;
    TextView tv_dislike;
    TextView tv_time;
    TextView tv_content;

    Activity activity;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.fragment_video_info, container, false);
        this.activity = getActivity();
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        initial_static();
        super.onActivityCreated(savedInstanceState);
    }

    void initial_static() {
        tv_title = (TextView) activity.findViewById(R.id.video_info_title);
        tv_avatar = (CircleImageView) activity.findViewById(R.id.video_info_avatar);
        tv_author = (TextView) activity.findViewById(R.id.video_info_author);
        tv_like = (TextView) activity.findViewById(R.id.video_info_like_count);
        tv_dislike = (TextView) activity.findViewById(R.id.video_info_dislike_count);
        tv_time = (TextView) activity.findViewById(R.id.video_info_time);
        tv_content = (TextView) activity.findViewById(R.id.video_info_content);
        id = activity.getIntent().getStringExtra("url");
        Log.d("videoid",id);
        new MyGetVideoInfoAsyncTask().execute(id);
    }

    private class MyGetVideoInfoAsyncTask extends AsyncTask<String, String, video> {

        Bitmap avatar;
        @Override
        protected video doInBackground(String... ids) {
            video vd = VideoItemService.getVideoItem(ids[0]);
            avatar = LoadImageUtil.sendGets("http://"+getIP()+"/HelloWeb/Upload/Avatar/"+vd.getAvatar());
            return vd;
        }

        @Override
        protected void onPostExecute(video video) {
            Log.d("videotitle",video.getTitle());
            tv_title.setText(video.getTitle());
            tv_like.setText(String.valueOf(video.getLike()));
            tv_dislike.setText(String.valueOf(video.getDislike()));
            tv_avatar.setImageBitmap(avatar);
            tv_author.setText(video.getAuthor());
            tv_time.setText(video.getTime());
            tv_content.setText(video.getContent());
        }
    }
}
