package com.example.xavier.smartcampusdemo.Activity;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.xavier.smartcampusdemo.Adapter.Fragment1Adapter;
import com.example.xavier.smartcampusdemo.Fragment.Notice;
import com.example.xavier.smartcampusdemo.Fragment.microBlog;
import com.example.xavier.smartcampusdemo.Fragment.techForum;
import com.example.xavier.smartcampusdemo.Fragment.videoShare.videoShare;
import com.example.xavier.smartcampusdemo.R;
import com.example.xavier.smartcampusdemo.Util.ColorUtils;
import com.example.xavier.smartcampusdemo.Util.DisplayUtil;
import com.example.xavier.smartcampusdemo.Util.NetUtil.LoadImageUtil;
import com.example.xavier.smartcampusdemo.Util.NoScrollViewPager;
import com.example.xavier.smartcampusdemo.Util.UIUtils;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

import static com.example.xavier.smartcampusdemo.Service.NetService.getIP;

public class MainActivity extends BaseActivity implements
        NavigationView.OnNavigationItemSelectedListener,ViewPager.OnPageChangeListener ,View.OnClickListener{

    SharedPreferences sharedPreferences;

    //布局控件声明
    private FloatingActionButton fab;
    TextView usrName, usrRole;
    CircleImageView usrAvatar;
    private NoScrollViewPager mViewPager;
    private TabLayout tabLayout;
    private NavigationView navigationView;
    private DrawerLayout drawer;

    Activity activity;
    Handler handler = new Handler();

    int cachedPages = 1;

    private List<Integer> tabLayoutIds = new ArrayList<>();
    private List<Integer> tabIconIds = new ArrayList<>();
    private List<Integer> tabIconRsc = new ArrayList<>();
    private List<Integer> tabIconRsce = new ArrayList<>();
    private List<Fragment> mFragments = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        activity = this;
        //设置KITKAT以上版本状态栏透明
//        final int sdk = Build.VERSION.SDK_INT;
//        Window window = getWindow();
//        WindowManager.LayoutParams params = window.getAttributes();
//        if (sdk >= Build.VERSION_CODES.KITKAT) {
//            int bits = 0;    // 设置透明状态栏
//            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.KITKAT) {
//                bits = WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS;
//            }
//            if ((params.flags & bits) == 0) {
//                params.flags |= bits;
//                window.setAttributes(params);
//            }
//        }

        //初始化工具栏
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
//        toolbar.setPadding(0,DisplayUtil.getStatusHeightPx(this),0,0);

//        View topview = findViewById(R.id.topView);
//        ViewGroup.LayoutParams layoutParams = topview.getLayoutParams();
//        layoutParams.height = DisplayUtil.getStatusHeightPx(this);
//        topview.setLayoutParams(layoutParams);
//        topview.setBackgroundResource(R.drawable.main_toolbar_background);
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
            tab.setCustomView(tabLayoutIds.get(i));//给每一个tab设置view
            if (i == 0) {
                // 设置第一个tab的TextView是被选择的样式
                ((ImageView)tab.getCustomView().findViewById(tabIconIds.get(0))).setImageResource(tabIconRsce.get(0));//第一个tab被选中
            }
        }

        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);
        tabLayout.setTabMode(TabLayout.MODE_FIXED);

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                ((ImageView)tab.getCustomView().findViewById(tabIconIds.get(tab.getPosition()))).setImageResource(tabIconRsce.get(tab.getPosition()));
                mViewPager.setCurrentItem(tab.getPosition(),false);
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                ((ImageView)tab.getCustomView().findViewById(tabIconIds.get(tab.getPosition()))).setImageResource(tabIconRsc.get(tab.getPosition()));
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        navigationView = (NavigationView) findViewById(R.id.nav_view);
        assert navigationView != null;

        navigationView.setCheckedItem(R.id.nav_home);
        if(isLogged()) navigationView.getMenu().removeItem(R.id.nav_login);
        else navigationView.getMenu().removeItem(R.id.nav_logout);
        navigationView.setNavigationItemSelectedListener(this);

        initUser_header();
    }

    public void initLayout() {
        fab = (FloatingActionButton) findViewById(R.id.fab);
        tabLayout = (TabLayout) findViewById(R.id.tabLayout);
        mViewPager = (NoScrollViewPager) findViewById(R.id.viewPager);
        tabLayoutIds.add(R.layout.item_tab_forum);
        tabLayoutIds.add(R.layout.item_tab_video);
        tabLayoutIds.add(R.layout.item_tab_vblog);
        tabLayoutIds.add(R.layout.item_tab_announce);
        tabIconIds.add(R.id.tab_forum);
        tabIconIds.add(R.id.tab_video);
        tabIconIds.add(R.id.tab_vblog);
        tabIconIds.add(R.id.tab_announce);
        tabIconRsc.add(R.drawable.forum);
        tabIconRsc.add(R.drawable.video);
        tabIconRsc.add(R.drawable.vblog);
        tabIconRsc.add(R.drawable.announce);
        tabIconRsce.add(R.drawable.forum_selected);
        tabIconRsce.add(R.drawable.video_selected);
        tabIconRsce.add(R.drawable.vblog_selected);
        tabIconRsce.add(R.drawable.announce_selected);

        mFragments.add(new techForum());
        mFragments.add(new videoShare());
        mFragments.add(new microBlog());
        mFragments.add(new Notice());
    }

    public void initUser_header() {
        String userName;
        String userRole;
        String userAvatar;

        View headerView = navigationView.getHeaderView(0);
        usrName = (TextView) headerView.findViewById(R.id.usrName);
        usrRole = (TextView) headerView.findViewById(R.id.usrRole);
        usrAvatar = (CircleImageView) headerView.findViewById(R.id.usrAvatar);
        if(isLogged()) {
            userName = sharedPreferences.getString("userName", "null");
            userRole = sharedPreferences.getString("userRole", "null");
            userAvatar = sharedPreferences.getString("userAvatar", "null");

            usrName.setText(userName);
            usrRole.setText(userRole);
            usrAvatar.setImageResource(R.drawable.default_avatar);

            new Thread(new LoadPicThread(userAvatar)).start();
        }
    }

    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    boolean isLogged() {
        sharedPreferences = getSharedPreferences("USER_INFO", Context.MODE_PRIVATE);
        return !sharedPreferences.getAll().isEmpty();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_home) {
            // Handle the camera action
        } else if (id == R.id.nav_gallery) {

        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_settings) {

        } else if (id == R.id.nav_login) {
            SignInActivity.actionStart(MainActivity.this);
        } else if (id == R.id.nav_logout) {
            new AlertDialog.Builder(this).setTitle("确认退出吗？")
                    .setIcon(android.R.drawable.ic_dialog_info)
                    .setPositiveButton("确定", new DialogInterface.OnClickListener() {

                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            sharedPreferences = getSharedPreferences("USER_INFO", Context.MODE_PRIVATE);
                            SharedPreferences.Editor editor = sharedPreferences.edit();
                            editor.clear();
                            editor.apply();
                            onlyActivity(MainActivity.class);
                        }
                    })
                    .setNegativeButton("返回", new DialogInterface.OnClickListener() {

                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                        }
                    }).show();
        } else if (id == R.id.nav_about) {

        }

        navigationView.setCheckedItem(R.id.nav_home);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {
        switch (position) {
            case 0:
                fab.setVisibility(View.VISIBLE);
                fab.setImageResource(R.drawable.edit_new);
                break;
            case 1:
                fab.setVisibility(View.VISIBLE);
                fab.setImageResource(R.drawable.pushblog_new);
                break;
            case 2:
                fab.setVisibility(View.VISIBLE);
                fab.setImageResource(R.drawable.pushblog_new);
                break;
            case 3:
                fab.setVisibility(View.INVISIBLE);
                fab.setImageResource(R.drawable.pushblog_new);
                break;
        }
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }

    @Override
    public void onClick(View v) {
        if(!isLogged()) {
            new AlertDialog.Builder(MainActivity.this).setTitle("该功能需登录方可使用！")
                    .setIcon(android.R.drawable.ic_dialog_info)
                    .setPositiveButton("点此登录", new DialogInterface.OnClickListener() {

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
        }
        else {
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

    //加载个人头像
    private class LoadPicThread implements Runnable {

        String fileName;
        LoadPicThread(String fileName) {
            this.fileName = fileName;
        }
        @Override
        public void run() {
            final Bitmap avatar = LoadImageUtil.sendGets("http://"+getIP()+"/HelloWeb/Upload/Avatar/"+fileName);
            handler.post(new Runnable() {
                @Override
                public void run() {
//                    do {
//                        if(avatar!=null) {
//                            break;
//                        }
//                    }while (true);
                    if(avatar!=null)
                        usrAvatar.setImageBitmap(avatar);
                    else
                        UIUtils.customBottomShortToast(MainActivity.this,"服务器",0,0);
                }
            });
        }
    }

}