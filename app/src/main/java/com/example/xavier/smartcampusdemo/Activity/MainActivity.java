package com.example.xavier.smartcampusdemo.Activity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
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
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.example.xavier.smartcampusdemo.Adapter.FragmentAdapter;
import com.example.xavier.smartcampusdemo.Fragment.Notice;
import com.example.xavier.smartcampusdemo.Fragment.microBlog;
import com.example.xavier.smartcampusdemo.Fragment.techForum;
import com.example.xavier.smartcampusdemo.Fragment.videoShare;
import com.example.xavier.smartcampusdemo.R;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import static com.example.xavier.smartcampusdemo.Service.NetService.getIP;

public class MainActivity extends BaseActivity implements
        NavigationView.OnNavigationItemSelectedListener {

    private ViewPager mViewPager;
    private TabLayout tabLayout;
    private PopupWindow window;
    private NavigationView navigationView;
    private String p_author, p_content, p_title, p_type;
    SharedPreferences sharedPreferences;
    TextView usrName, usrRole;
    private DrawerLayout drawer;
    Handler handler = new Handler();
    private List<String> mTitle = new ArrayList<>();
    private List<Fragment> mFragments = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        final FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        assert fab != null;
        initLayout();

        /**
         * 初始化Adapter
         */
        FragmentAdapter mAdapter = new FragmentAdapter(getSupportFragmentManager(), mTitle, mFragments);

        mViewPager.setOffscreenPageLimit(3);
        mViewPager.setAdapter(mAdapter);
        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                if(position == 0)
                    fab.setVisibility(View.VISIBLE);
                else
                    fab.setVisibility(View.INVISIBLE);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        tabLayout.setupWithViewPager(mViewPager);
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);
        tabLayout.setTabMode(TabLayout.MODE_FIXED);

        final View popupView = MainActivity.this.getLayoutInflater().inflate(R.layout.techforum_publish, null);
        final EditText publish_title = (EditText) popupView.findViewById(R.id.forum_publish_title);
        final EditText publish_content = (EditText) popupView.findViewById(R.id.forum_publish_content);
        Button publish_commit = (Button) popupView.findViewById(R.id.forum_publish_commit);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!isLogged()) {
                    new AlertDialog.Builder(MainActivity.this).setTitle("发布论坛请先登录！")
                            .setIcon(android.R.drawable.ic_dialog_info)
                            .setPositiveButton("点此登陆", new DialogInterface.OnClickListener() {

                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    // 点击“确认”后的操作
                                    SignInActivity.actionStart(MainActivity.this);
                                }
                            })
                            .setNegativeButton("忽略", new DialogInterface.OnClickListener() {

                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    // 点击“返回”后的操作,这里不设置没有任何操作
                                }
                            }).show();
                }
                else {
                    window = new PopupWindow(popupView, ViewPager.LayoutParams.MATCH_PARENT, getWindowManager().getDefaultDisplay().getHeight() * 3 / 4);

                    publish_content.setHeight(window.getHeight()*3/4);
                    window.setAnimationStyle(R.style.popup_window_anim);
                    window.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#F8F8F8")));
                    window.setFocusable(true);
                    window.setOutsideTouchable(true);
                    window.setInputMethodMode(PopupWindow.INPUT_METHOD_NEEDED);
                    window.update();
                    window.showAtLocation(fab, Gravity.BOTTOM, 0, 0);
                }
            }
        });

        publish_commit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sharedPreferences = getSharedPreferences("USER_INFO", Context.MODE_PRIVATE);
                p_author = sharedPreferences.getString("userName","");
                p_content = publish_content.getText().toString();
                p_title = publish_title.getText().toString();
                p_type = "1";
                new Thread(new MyThread()).start();
                window.dismiss();
                techForum.refresh();
            }
        });

        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
            this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        navigationView = (NavigationView) findViewById(R.id.nav_view);
        assert navigationView != null;

        navigationView.setCheckedItem(R.id.nav_home);
        if(isLogged())
            navigationView.getMenu().removeItem(R.id.nav_login);
        else
            navigationView.getMenu().removeItem(R.id.nav_logout);
        navigationView.setNavigationItemSelectedListener(this);

        initUser_header();
    }

    public void initLayout() {
        tabLayout = (TabLayout) findViewById(R.id.tabLayout);
        mViewPager = (ViewPager) findViewById(R.id.viewPager);
        mTitle.add("技术论坛");
        mTitle.add("视频分享");
        mTitle.add("微博");
        mTitle.add("公告");

        mFragments.add(new techForum());
        mFragments.add(new videoShare());
        mFragments.add(new microBlog());
        mFragments.add(new Notice());
    }

    public void initUser_header() {
        String userName;
        String userRole;

        View headerView = navigationView.getHeaderView(0);
        usrName = (TextView) headerView.findViewById(R.id.usrName);
        usrRole = (TextView) headerView.findViewById(R.id.usrRole);
        if(isLogged()) {
            userName = sharedPreferences.getString("userName", "null");
            userRole = sharedPreferences.getString("userRole", "null");
            usrName.setText(userName);
            usrRole.setText(userRole);
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
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
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
                            // 点击“确认”后的操作
                            sharedPreferences = getSharedPreferences("USER_INFO", Context.MODE_PRIVATE);
                            SharedPreferences.Editor editor = sharedPreferences.edit();
                            editor.clear();
                            editor.apply();
                            refresh();
                        }
                    })
                    .setNegativeButton("返回", new DialogInterface.OnClickListener() {

                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            // 点击“返回”后的操作,这里不设置没有任何操作
                        }
                    }).show();
        } else if (id == R.id.nav_about) {

        }

        navigationView.setCheckedItem(R.id.nav_home);
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        assert drawer != null;
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void refresh() {
        Intent intent = new Intent(MainActivity.this, MainActivity.class);
        startActivity(intent);
        techForum.refresh();
        finish();
    }

    private class MyThread implements Runnable {

        @Override
        public void run() {
            Properties properties = System.getProperties();
            properties.list(System.out);
            String url = "http://" + getIP() + "/HelloWeb/ForumPublishLet";
            try {
                HttpURLConnection conn = (HttpURLConnection) new URL(url).openConnection();
                conn.setReadTimeout(3000);
                conn.setRequestMethod("POST");

                conn.setDoOutput(true);
                conn.setDoInput(true);
                PrintWriter printWriter = new PrintWriter(conn.getOutputStream());
                String post = "author="+p_author+"&content="+p_content+"&title="+p_title+"&type="+p_type;
                printWriter.write(post);
                printWriter.flush();
                BufferedInputStream bis = new BufferedInputStream(conn.getInputStream());
                ByteArrayOutputStream bos = new ByteArrayOutputStream();
                int len;
                byte[] arr = new byte[1024];
                while ((len=bis.read(arr))!= -1) {
                    bos.write(arr,0,len);
                    bos.flush();
                }
                bos.close();

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}