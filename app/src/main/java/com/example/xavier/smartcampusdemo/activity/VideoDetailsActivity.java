package com.example.xavier.smartcampusdemo.activity;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.MediaController;
import android.widget.TextView;

import com.example.xavier.smartcampusdemo.R;
import com.example.xavier.smartcampusdemo.adapter.FragmentAdapter;
import com.example.xavier.smartcampusdemo.entity.video;
import com.example.xavier.smartcampusdemo.fragment.videoShare.videoComment;
import com.example.xavier.smartcampusdemo.fragment.videoShare.videoInfo;
import com.example.xavier.smartcampusdemo.service.VideoItemService;
import com.example.xavier.smartcampusdemo.util.ColorUtils;
import com.example.xavier.smartcampusdemo.util.CosVideoView;
import com.facebook.drawee.view.SimpleDraweeView;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import me.imid.swipebacklayout.lib.app.SwipeBackActivity;

import static com.example.xavier.smartcampusdemo.service.NetService.getVideoPath;


/**
 * Created by Xavier on 3/10/2017.
 *
 */

public class VideoDetailsActivity extends SwipeBackActivity implements View.OnClickListener {

    Toolbar toolbar;
    View video_view;
    View toobar_view;
    TextView toobar_play;
    CircleImageView toobar_play_icon;
    SimpleDraweeView col_avatar;
    NestedScrollView nestedScrollView;

    FloatingActionButton fab;
    AppBarLayout app_bar;
    Activity activity;
    video video;
    CosVideoView videoView;
    //    IjkVideoView videoView;
    FrameLayout videoHolder;
    MediaController mediaController;
    CollapsingToolbarLayout collapsingToolbarLayout;
    private String vid, uid;
    private ViewPager mViewPager;
    private TabLayout tabLayout;
    private List<String> mTitle = new ArrayList<>();
    private List<Fragment> mFragments = new ArrayList<>();
    private CollapsingToolbarLayoutState state;
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == 1) {
                col_avatar.setImageURI(getVideoPath() + video.getVideo_str().split("\\.")[0] + ".jpg");

                startCollapsing();

            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_videos_details);
        toolbar = (Toolbar) findViewById(R.id.video_details_toolbar);
        vid = getIntent().getStringExtra("url");
        SharedPreferences sharedPreferences = getSharedPreferences("USER_INFO", Context.MODE_PRIVATE);
        uid = sharedPreferences.getString("userID","");

        new Thread(new InitialThread()).start();

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


        videoView = (CosVideoView) findViewById(R.id.video_collapsing_video);
        assert videoView != null;
        videoView.setZOrderOnTop(true);
        mediaController = new MediaController(this);
        new getVideoAsyncTask().execute(vid);
        app_bar = (AppBarLayout) findViewById(R.id.video_app_bar);
        toobar_play_icon = (CircleImageView) findViewById(R.id.video_toolbar_icon);
        toobar_play = (TextView) findViewById(R.id.video_toolbar_play);
        toobar_view = findViewById(R.id.video_toolbar_info);
        //video_view = findViewById(R.id.video_collapsing_video_holder);
        col_avatar = (SimpleDraweeView) findViewById(R.id.video_collapsing_avatar);
        nestedScrollView = (NestedScrollView) findViewById(R.id.video_nested_scroll_view);

        final ViewGroup.LayoutParams layoutParams = nestedScrollView.getLayoutParams();
        ViewTreeObserver vto2 = toolbar.getViewTreeObserver();
        vto2.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                toolbar.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                layoutParams.width = getResources().getDisplayMetrics().widthPixels;
                layoutParams.height = getResources().getDisplayMetrics().heightPixels - toolbar.getHeight() - getStatusBarHeight();

                Log.i("height,width", layoutParams.height +""+ layoutParams.width);
            }
        });



        nestedScrollView.setLayoutParams(layoutParams);

        fab = (FloatingActionButton) findViewById(R.id.fab2);
        toobar_view.setOnClickListener(this);
        fab.setOnClickListener(this);
        initLayout();

        FragmentAdapter mAdapter = new FragmentAdapter(getSupportFragmentManager(), mTitle, mFragments);
        mViewPager.setAdapter(mAdapter);

        tabLayout.setupWithViewPager(mViewPager);
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);
        tabLayout.setTabMode(TabLayout.MODE_FIXED);
        videoHolder = (FrameLayout) findViewById(R.id.video_collapsing_holder);
        //initPlayer();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.video_toolbar_info:
                app_bar.setExpanded(true);
            case R.id.fab2:
                AppBarLayout.LayoutParams appbarParams = (AppBarLayout.LayoutParams) collapsingToolbarLayout.getLayoutParams();
                appbarParams.setScrollFlags(0);
                collapsingToolbarLayout.setLayoutParams(appbarParams);
                videoHolder.setVisibility(View.VISIBLE);
                videoView.start();
                //player.start();
//                player.play(getVideoPath()+video.getVideo_str());
                fab.setVisibility(View.GONE);
                nestedScrollView.setNestedScrollingEnabled(false);
                ViewGroup.LayoutParams layoutParams = nestedScrollView.getLayoutParams();
                layoutParams.height = getResources().getDisplayMetrics().heightPixels - app_bar.getHeight();
                nestedScrollView.setLayoutParams(layoutParams);
                break;


        }
    }

    private void startCollapsing() {
        collapsingToolbarLayout = (CollapsingToolbarLayout) findViewById(R.id.video_collapsing_layout);
        collapsingToolbarLayout.setBackgroundColor(ColorUtils.getColor(VideoDetailsActivity.this, R.color.darkgoldenrod));
        app_bar.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                if (verticalOffset == 0) {
                    if (state != CollapsingToolbarLayoutState.EXPANDED) {
                        state = CollapsingToolbarLayoutState.EXPANDED;//修改状态标记为展开
                    }
                    toobar_view.setVisibility(View.GONE);
                } else if (Math.abs(verticalOffset) >= appBarLayout.getTotalScrollRange()) {
                    if (state != CollapsingToolbarLayoutState.COLLAPSED) {
                        state = CollapsingToolbarLayoutState.COLLAPSED;//修改状态标记为折叠
                    }
                    collapsingToolbarLayout.setTitle("");//设置title不显示
                    toobar_view.setVisibility(View.VISIBLE);
                } else {
                    if (state != CollapsingToolbarLayoutState.INTERNEDIATE) {
                        if(state == CollapsingToolbarLayoutState.COLLAPSED){
                            toobar_view.setVisibility(View.GONE);
                        }
                        state = CollapsingToolbarLayoutState.INTERNEDIATE;//修改状态标记为中间
                    }
                }
            }
        });
    }

    public void initLayout() {
        tabLayout = (TabLayout) findViewById(R.id.video_tablayout);
        mViewPager = (ViewPager) findViewById(R.id.video_viewpager);
        mTitle.add("详情");
        mTitle.add("评论");

        mFragments.add(new videoInfo());
        mFragments.add(new videoComment());
    }

    public int getStatusBarHeight() {
        int result = 0;
        int resourceId = getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = getResources().getDimensionPixelSize(resourceId);
        }
        return result;
    }

    private enum CollapsingToolbarLayoutState {
        EXPANDED,
        COLLAPSED,
        INTERNEDIATE
    }

    private class InitialThread extends Thread {

        @Override
        public void run() {
            video = VideoItemService.getVideoItem(vid);
            if (video.getU_id() != 0) {
                Message msg = new Message();
                msg.what = 1;
                handler.sendMessage(msg);
            }
        }
    }

    private class getVideoAsyncTask extends AsyncTask<String, String, video> {

        @Override
        protected video doInBackground(String... ids) {
            return VideoItemService.getVideoById(ids[0]);
        }

        @Override
        protected void onPostExecute(video video) {
            videoView.setVideoURI(Uri.parse(getVideoPath()+video.getVideo_str()));
//            videoView.setVideoURI(Uri.parse(getVideoPath()+video.getVideo_str()));
            videoView.setMediaController(mediaController);
            videoView.start();
        }
    }
}
