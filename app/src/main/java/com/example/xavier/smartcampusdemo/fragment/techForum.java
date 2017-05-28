package com.example.xavier.smartcampusdemo.fragment;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.example.xavier.smartcampusdemo.R;
import com.example.xavier.smartcampusdemo.adapter.techForumViewAdapter;
import com.example.xavier.smartcampusdemo.entity.forum;
import com.example.xavier.smartcampusdemo.service.ForumItemService;
import com.example.xavier.smartcampusdemo.util.ColorUtils;
import com.zhy.view.flowlayout.FlowLayout;
import com.zhy.view.flowlayout.TagAdapter;
import com.zhy.view.flowlayout.TagFlowLayout;

import java.util.List;

import static android.view.ViewGroup.LayoutParams.WRAP_CONTENT;
import static com.example.xavier.smartcampusdemo.activity.MainActivity.fab;
import static com.example.xavier.smartcampusdemo.util.NetUtil.NetUtil.isNetworkAvailable;

/**
 * Created by Xavier on 11/7/2016.
 * techForumFragment
 */

public class techForum extends Fragment implements SwipeRefreshLayout.OnRefreshListener {

    public static TabLayout forumTypeSelector;
    public static int ALL = 1;
    public static int page = 0;
    public static int type = ALL;
    private static techForumViewAdapter adapter;
    private static boolean isLoading = true;
    private static boolean noMore = false;
    View popView;
    PopupWindow popWindow;
    RecyclerView mRecyclerView;
    SwipeRefreshLayout mSwipeRefreshLayout;
    String TAG = "FragmentCheck" + getClass().getSimpleName();
    boolean isDestroyView = false;
    private Activity activity;

    public static void refresh() {
        adapter.removeAll();
        noMore = false;
        page = 0;
        new MyAsyncTaskGetForumItem().execute(page, type);
    }

    public static void relrefresh(int ntype) {
        type = ntype;
        refresh();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.fragment_forum, container, false);
        this.activity = getActivity();
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {

        mRecyclerView = (RecyclerView) activity.findViewById(R.id.forum_rv);
        final LinearLayoutManager mLayoutManager = new LinearLayoutManager(activity);
        mSwipeRefreshLayout = (SwipeRefreshLayout) activity.findViewById(R.id.swipe_refresh_forumlayout);
        mSwipeRefreshLayout.setColorSchemeColors(ColorUtils.getColor(this,R.color.aliceblue), ColorUtils.getColor(this,R.color.antiquewhite), ColorUtils.getColor(this,R.color.aqua),ColorUtils.getColor(this,R.color.aquamarine));
        mSwipeRefreshLayout.setOnRefreshListener(this);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setHasFixedSize(true);
        adapter = new techForumViewAdapter(activity, null);
        mRecyclerView.setAdapter(adapter);
        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }

            @Override
            public void onScrolled(RecyclerView recyclerView,int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (dy >0) {
                    // Scroll Down
                    if (fab.isShown()) {
                        fab.hide();
                    }
                }
                else if (dy <0) {
                    // Scroll Up
                    if (!fab.isShown()) {
                        fab.show();
                    }
                }
                int firstVisibleItems, visibleItemCount, totalItemCount;

                visibleItemCount = mLayoutManager.getChildCount();
                totalItemCount = mLayoutManager.getItemCount();
                firstVisibleItems = mLayoutManager.findFirstVisibleItemPosition();

                if(!isNetworkAvailable(activity)) {
                    Toast toast = Toast.makeText(activity,"网络连接错误",Toast.LENGTH_SHORT);
                    toast.setGravity(Gravity.BOTTOM, 0, 100);
                    toast.show();
                    adapter.notifyItemRemoved(adapter.getItemCount());
                }
                else if ((visibleItemCount + firstVisibleItems) >= totalItemCount && !isLoading && adapter.getItemCount()>=10) {
                    // 判断点
                    if(!noMore) {
                        isLoading = true;
                        new MyAsyncTaskGetForumItem().execute(page, type);
                    }
                }
            }
        });

        forumTypeSelector = (TabLayout) activity.findViewById(R.id.forum_type_selector);
        popView = View.inflate(activity, R.layout.forum_type_more_popup, null);
        initPopupWindow();
        initSelector();
        if(activity.getIntent().getBooleanExtra("isPublished",false) && activity.getIntent().getIntExtra("currentPage",0)+1 != type)
            forumTypeSelector.getTabAt(activity.getIntent().getIntExtra("currentPage",0)).select();
        else
            new MyAsyncTaskGetForumItem().execute(page, type);
        super.onActivityCreated(savedInstanceState);
    }

    void initSelector() {
        ImageView forumTypeMore = (ImageView) activity.findViewById(R.id.forum_type_more);
        String[] titleArr = getResources().getStringArray(R.array.forumTypes);
        for (String aTitleArr : titleArr) {
            forumTypeSelector.addTab(forumTypeSelector.newTab().setText(aTitleArr));
        }
        forumTypeSelector.setTabMode(TabLayout.MODE_SCROLLABLE);
        forumTypeSelector.setTabTextColors(R.color.black, R.color.green);
        forumTypeSelector.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                techForum.relrefresh(tab.getPosition()+1);
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
                techForum.refresh();

            }
        });
        forumTypeMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showPopupWindow();
            }
        });

    }

    void initPopupWindow() {
        int width = getResources().getDisplayMetrics().widthPixels;
        int height = getResources().getDisplayMetrics().heightPixels;

        TagFlowLayout pop = (TagFlowLayout) popView.findViewById(R.id.forum_type_more_popup);

        pop.setAdapter(new TagAdapter<String>(getResources().getStringArray(R.array.forumTypes)) {
            @Override
            public View getView(FlowLayout parent, final int position, String s) {
                TextView tagview = new TextView(activity);
                tagview.setPadding(10, 5, 10, 5);
                tagview.setText(s);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN)
                    tagview.setBackground(getResources().getDrawable(R.drawable.forum_reply_style));
                else tagview.setBackgroundDrawable(getResources().getDrawable(R.drawable.ic_arrow_drop_down_black_24dp));
                tagview.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        forumTypeSelector.getTabAt(position).select();
                        popWindow.dismiss();
                    }
                });
                return tagview;
            }
        });

        popWindow = new PopupWindow(popView,width,WRAP_CONTENT);
        popWindow.setAnimationStyle(R.style.Widget_AppCompat_Spinner_DropDown);
        popWindow.setFocusable(true);
        popWindow.setOutsideTouchable(true);// 设置允许在外点击消失
        ColorDrawable dw = new ColorDrawable(0xffffffff);
        popWindow.setBackgroundDrawable(dw);

    }

    private void showPopupWindow() {
        View parent = activity.findViewById(R.id.forum_type_selector);
        popWindow.showAsDropDown(parent);
    }

    @Override
    public void onRefresh() {
        if(!isNetworkAvailable(activity)) {
            Toast toast = Toast.makeText(activity,"网络连接错误",Toast.LENGTH_SHORT);
            toast.setGravity(Gravity.BOTTOM, 0, 100);
            toast.show();
        }
        else{
            refresh();
        }
        mSwipeRefreshLayout.setRefreshing(false);

    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        page = 0;
        Log.d(TAG,"onCreate");
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        Log.d(TAG,"onAttach");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG,"onDestroy");
    }

    @Override
    public void onDetach() {
        super.onDetach();
        Log.d(TAG,"onDetach");
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Log.d(TAG,"onViewCreated");
    }

    @Override
    public void onStart() {
        super.onStart();
        Log.d(TAG,"onStart");
    }

    @Override
    public void onStop() {
        super.onStop();
        Log.d(TAG,"onStop");
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        Log.d(TAG,"onDestroyView");
        isDestroyView = true;
        adapter.removeAll();
        page = 0;
    }

    private static class MyAsyncTaskGetForumItem extends AsyncTask<Integer, String, List<forum>> {
        @Override
        protected List<forum> doInBackground(Integer... params) {
            return ForumItemService.getForumItems(params[0], params[1]);
        }

        @Override
        protected void onPostExecute(List<forum> forumItems) {
            if (forumItems.size() > 0) {
                adapter.addForumItem(forumItems);
                adapter.notifyDataSetChanged();
                page++;
            }
            if (forumItems.size() < 10 && !noMore) {
                adapter.notifyEnd();
                if (page == 0) {
                    adapter.notifyNone();
                    adapter.notifyDataSetChanged();
                }
                noMore = true;
            }
            isLoading = false;
        }

    }

}

