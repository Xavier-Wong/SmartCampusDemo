package com.example.xavier.smartcampusdemo.activity;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.os.Build;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;

import com.example.xavier.smartcampusdemo.R;
import com.example.xavier.smartcampusdemo.adapter.Fragment1Adapter;
import com.example.xavier.smartcampusdemo.fragment.Notice;
import com.example.xavier.smartcampusdemo.fragment.Personal;
import com.example.xavier.smartcampusdemo.fragment.microBlog;
import com.example.xavier.smartcampusdemo.fragment.techForum;
import com.example.xavier.smartcampusdemo.fragment.videoShare.videoShare;
import com.example.xavier.smartcampusdemo.util.ColorUtils;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends BaseActivity implements ViewPager.OnPageChangeListener ,View.OnClickListener{

    //布局控件声明
    public static FloatingActionButton fab;
    SharedPreferences sharedPreferences;
    Toolbar toolbar;
    Activity activity;
    int cachedPages = 4;
    String TAG = "ActivityCheck" + getClass().getSimpleName();
    private ViewPager mViewPager;
    private TabLayout tabLayout;
    private List<Integer> tabIconRsc = new ArrayList<>();
    private List<Integer> tabIconRsce = new ArrayList<>();
    private List<Integer> tabColor = new ArrayList<>();
    private List<Fragment> mFragments = new ArrayList<>();
    private List<String> mTitles = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG,"onCreate");
        setContentView(R.layout.activity_main);
        activity = this;

        //初始化工具栏
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("论坛");
        setSupportActionBar(toolbar);

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

        /*
          初始化Adapter
         */

        fab.setOnClickListener(this);

        Fragment1Adapter mAdapter = new Fragment1Adapter(getSupportFragmentManager(), mFragments);
        mViewPager.setAdapter(mAdapter);
        mViewPager.setOffscreenPageLimit(cachedPages);
        mViewPager.addOnPageChangeListener(this);
        tabLayout.setupWithViewPager(mViewPager);
        for (int i = 0; i < mAdapter.getCount(); i++) {
            TabLayout.Tab tab = tabLayout.getTabAt(i);//获得每一个tab
            assert tab != null;
            tab.setCustomView(R.layout.item_tab_normal);//给每一个tab设置view
            View tabRoot = tab.getCustomView();
            assert tabRoot != null;
            if (i == 0) {
                // 设置第一个tab的TextView是被选择的样式
                ((ImageView)tabRoot.findViewById(R.id.tab_icon)).setImageResource(tabIconRsce.get(0));//第一个tab被选中
                toolbar.setTitle(mTitles.get(0));
                toolbar.setBackgroundColor(tabColor.get(0));
                tabLayout.setBackgroundColor(tabColor.get(0));
                fab.setBackgroundTintList(ColorStateList.valueOf(tabColor.get(0)));
            }
            else {
                ((ImageView)tabRoot.findViewById(R.id.tab_icon)).setImageResource(tabIconRsc.get(i));
            }
        }

        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);
        tabLayout.setTabMode(TabLayout.MODE_FIXED);

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                View tabRoot = tab.getCustomView();
                assert tabRoot != null;
                ((ImageView)tabRoot.findViewById(R.id.tab_icon)).setImageResource(tabIconRsce.get(tab.getPosition()));
                mViewPager.setCurrentItem(tab.getPosition(),false);
                toolbar.setTitle(mTitles.get(tab.getPosition()));
                toolbar.setBackgroundColor(tabColor.get(tab.getPosition()));
                tabLayout.setBackgroundColor(tabColor.get(tab.getPosition()));
                fab.setBackgroundTintList(ColorStateList.valueOf(tabColor.get(tab.getPosition())));
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                View tabRoot = tab.getCustomView();
                assert tabRoot != null;
                ((ImageView)tabRoot.findViewById(R.id.tab_icon)).setImageResource(tabIconRsc.get(tab.getPosition()));
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

    }

    public void initLayout() {
        fab = (FloatingActionButton) findViewById(R.id.fab);
        tabLayout = (TabLayout) findViewById(R.id.tabLayout);
        mViewPager = (ViewPager) findViewById(R.id.viewPager);
        tabIconRsc.add(R.drawable.forum);
        tabIconRsc.add(R.drawable.video);
        tabIconRsc.add(R.drawable.vblog);
        tabIconRsc.add(R.drawable.announce);
        tabIconRsc.add(R.drawable.personal);
        tabIconRsce.add(R.drawable.forum_selected);
        tabIconRsce.add(R.drawable.video_selected);
        tabIconRsce.add(R.drawable.vblog_selected);
        tabIconRsce.add(R.drawable.announce_selected);
        tabIconRsce.add(R.drawable.personal_selected);
        tabColor.add(ColorUtils.getColor(activity, R.color.customblue));
        tabColor.add(ColorUtils.getColor(activity, R.color.darkgoldenrod));
        tabColor.add(ColorUtils.getColor(activity, R.color.darkkhaki));
        tabColor.add(ColorUtils.getColor(activity, R.color.darkorange));
        tabColor.add(ColorUtils.getColor(activity, R.color.darkmagenta));

        mTitles.add("论坛");
        mTitles.add("视频");
        mTitles.add("微博");
        mTitles.add("公告");
        mTitles.add("个人");
        mFragments.add(new techForum());
        mFragments.add(new videoShare());
        mFragments.add(new microBlog());
        mFragments.add(new Notice());
        mFragments.add(new Personal());
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        // Inflate the menu; this adds items to the action bar if it is present.
//        getMenuInflater().inflate(R.menu.main, menu);
//        return true;
//    }
//
//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        switch (item.getItemId()) {
//        }
//        return super.onOptionsItemSelected(item);
//    }

    boolean isLogged() {
        sharedPreferences = getSharedPreferences("USER_INFO", Context.MODE_PRIVATE);
        return !sharedPreferences.getAll().isEmpty();
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {
        fab.show();
        switch (position) {
            case 0:
                fab.setImageResource(R.drawable.ask);
                break;
            case 1:
                fab.setImageResource(R.drawable.shoot);
                break;
            case 2:
                fab.setImageResource(R.drawable.blog);
                break;
            case 3:
                fab.setImageResource(R.drawable.broadcast);
                break;
            case 4:
                if(!isLogged()) {
                    fab.setImageResource(R.drawable.login);
                }
                else {
                    fab.setImageResource(R.drawable.pushblog_new);
                }
                break;
        }
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }

    @Override
    public void onClick(View v) {
        if(mViewPager.getCurrentItem() == 4) {
            if(!isLogged()) {
                SignInActivity.actionStart(MainActivity.this);
            }
            else {
                new AlertDialog.Builder(this).setTitle("退出登陆？")
                        .setIcon(android.R.drawable.ic_dialog_info)
                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                sharedPreferences = getSharedPreferences("USER_INFO", Context.MODE_PRIVATE);
                                SharedPreferences.Editor editor = sharedPreferences.edit();
                                editor.clear();
                                editor.apply();
                                Intent intent = getIntent();
                                overridePendingTransition(0, 0);
                                intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                                finish();
                                overridePendingTransition(0, 0);
                                startActivity(intent);
                            }
                        })
                        .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                            }
                        }).show();
            }
        }
        else {
            if (!isLogged()) {
                new AlertDialog.Builder(MainActivity.this).setTitle("该功能需登录方可使用！")
                        .setIcon(android.R.drawable.ic_dialog_info)
                        .setPositiveButton("立即登录", new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                SignInActivity.actionStart(MainActivity.this);
                            }
                        })
                        .setNegativeButton("以后再说", new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                            }
                        }).show();
            } else {
                Intent intent;
                switch (mViewPager.getCurrentItem()) {
                    case 0:
                        intent = new Intent(MainActivity.this, ForumPublishActivity.class);
                        startActivity(intent);
                        break;
                    case 1:
                        intent = new Intent(MainActivity.this, VideoPublishActivity.class);
                        startActivity(intent);
                        break;
                    case 2:
                        intent = new Intent(MainActivity.this, BlogPublishActivity.class);
                        startActivity(intent);
                        break;
                }
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "onResume");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy");
    }

    @Override
    public void onSaveInstanceState(Bundle outState, PersistableBundle outPersistentState) {
        super.onSaveInstanceState(outState, outPersistentState);
        Log.d(TAG, "onSaveInstanceState");
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d(TAG, "onPause");
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.d(TAG, "onStop");
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.d(TAG, "onStart");
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        Log.d(TAG, "onRestoreInstanceState");
    }
}