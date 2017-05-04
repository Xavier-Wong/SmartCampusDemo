package com.example.xavier.smartcampusdemo.Activity;

import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;
import android.widget.MediaController;
import android.widget.RelativeLayout;
import android.widget.VideoView;

import com.example.xavier.smartcampusdemo.Adapter.FragmentAdapter;
import com.example.xavier.smartcampusdemo.Entity.video;
import com.example.xavier.smartcampusdemo.Fragment.videoShare.videoComment;
import com.example.xavier.smartcampusdemo.Fragment.videoShare.videoInfo;
import com.example.xavier.smartcampusdemo.R;
import com.example.xavier.smartcampusdemo.Service.VideoItemService;

import java.util.ArrayList;
import java.util.List;

import me.imid.swipebacklayout.lib.app.SwipeBackActivity;


/**
 * Created by Xavier on 3/10/2017.
 *
 */

public class VideoDetailsActivity extends SwipeBackActivity {

    private String id = "";
    VideoView videoView;
    MediaController mediaController;
    private ViewPager mViewPager;
    private TabLayout tabLayout;

    private List<String> mTitle = new ArrayList<>();
    private List<Fragment> mFragments = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_videos_details);

        id = getIntent().getStringExtra("url");

        final int sdk = Build.VERSION.SDK_INT;
        Window window = getWindow();
        WindowManager.LayoutParams params = window.getAttributes();
        if (sdk >= Build.VERSION_CODES.KITKAT) {
            int bits = 0;    // 设置透明状态栏
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.KITKAT) {
                bits = WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS;
            }
            if ((params.flags & bits) == 0) {
                params.flags |= bits;
                window.setAttributes(params);
            }
        }

        //初始化界面控件
        initLayout();

        FragmentAdapter mAdapter = new FragmentAdapter(getSupportFragmentManager(), mTitle, mFragments);
        mViewPager.setAdapter(mAdapter);
        //mViewPager.addOnPageChangeListener(this);

        tabLayout.setupWithViewPager(mViewPager);
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);
        tabLayout.setTabMode(TabLayout.MODE_FIXED);

        videoView = (VideoView) findViewById(R.id.videoView);
        RelativeLayout rl = (RelativeLayout) findViewById(R.id.video_container);
        assert videoView != null;
        videoView.setZOrderOnTop(true);
        mediaController = new MediaController(this);
        new getVideoAsyncTask().execute(id);
    }

    public void initLayout() {
        tabLayout = (TabLayout) findViewById(R.id.video_tablayout);
        mViewPager = (ViewPager) findViewById(R.id.video_viewpager);
        mTitle.add("详情");
        mTitle.add("评论");

        mFragments.add(new videoInfo());
        mFragments.add(new videoComment());
    }

    private class getVideoAsyncTask extends AsyncTask<String, String, video> {

        @Override
        protected video doInBackground(String... ids) {
            return VideoItemService.getVideoById(ids[0]);
        }

        @Override
        protected void onPostExecute(video video) {
            Log.d("videourl",video.getVideo_str());
            videoView.setVideoURI(Uri.parse("http://192.168.1.214:8080/HelloWeb/Upload/Video/"+video.getVideo_str()));
            videoView.setMediaController(mediaController);
            videoView.start();
        }
    }
    class getVideoThread extends Thread {

        private String id;

        getVideoThread(String id) {
            this.id = id;
        }

        @Override
        public void run() {
            video video = null;
            video = VideoItemService.getVideoById(id);
            video.getVideo_str();
        }
    }

}
