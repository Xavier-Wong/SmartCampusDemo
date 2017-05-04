package com.example.xavier.smartcampusdemo.Fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.xavier.smartcampusdemo.Adapter.FragmentAdapter;
import com.example.xavier.smartcampusdemo.Fragment.typeforum.agrForum;
import com.example.xavier.smartcampusdemo.Fragment.typeforum.ecoForum;
import com.example.xavier.smartcampusdemo.Fragment.typeforum.engForum;
import com.example.xavier.smartcampusdemo.Fragment.typeforum.hisForum;
import com.example.xavier.smartcampusdemo.Fragment.typeforum.litForum;
import com.example.xavier.smartcampusdemo.Fragment.typeforum.mansciForum;
import com.example.xavier.smartcampusdemo.Fragment.typeforum.medForum;
import com.example.xavier.smartcampusdemo.Fragment.typeforum.milsciForum;
import com.example.xavier.smartcampusdemo.Fragment.typeforum.pedForum;
import com.example.xavier.smartcampusdemo.Fragment.typeforum.jurForum;
import com.example.xavier.smartcampusdemo.Fragment.typeforum.phiForum;
import com.example.xavier.smartcampusdemo.Fragment.typeforum.sciForum;
import com.example.xavier.smartcampusdemo.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Xavier on 11/7/2016.
 * techForumFragment
 */

public class techForum extends Fragment {

    public static int PHI = 1;
    public static int ECO = 2;
    public static int JUR = 3;
    public static int PED = 4;
    public static int LIT = 5;
    public static int HIS = 6;
    public static int SCI = 7;
    public static int ENG = 8;
    public static int AGR = 9;
    public static int MED = 10;
    public static int MILSCI = 11;
    public static int MANSCI = 12;
    private int cachePagers = 1;
    View view;
    private TabLayout tabLayout;
    public static ViewPager mViewPager;
    private List<String> mTypeForumTitle = new ArrayList<>();
    private List<Fragment> mTypeForumFragments = new ArrayList<>();
    private int currentIndex;
    private boolean[] fragmentsUpdateFlag = { false, false, false, false ,false, false, false, false ,false, false, false, false };


    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        view = inflater.inflate(R.layout.fragment_forum, container, false);

        boolean isPublished = getActivity().getIntent().getBooleanExtra("isPublished", false);
        int currentPage = getActivity().getIntent().getIntExtra("currentPage", 0);

        initLayout();
        FragmentAdapter mAdapter = new FragmentAdapter(getChildFragmentManager(), mTypeForumTitle, mTypeForumFragments);
        mViewPager.setAdapter(mAdapter);
        mViewPager.setOffscreenPageLimit(cachePagers);
        if(isPublished) {
//            refreshAll();
            mViewPager.setCurrentItem(currentPage);
        }

        tabLayout.setupWithViewPager(mViewPager);
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);
        tabLayout.setTabMode(TabLayout.MODE_SCROLLABLE);
        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                fragmentsUpdateFlag[position] = true;
                currentIndex = position;
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        return view;
    }

    public void initLayout() {
        tabLayout = (TabLayout) view.findViewById(R.id.forum_type_selector);
        mViewPager = (ViewPager) view.findViewById(R.id.viewPager1);
        mTypeForumTitle.add("哲学");
        mTypeForumTitle.add("经济学");
        mTypeForumTitle.add("法学");
        mTypeForumTitle.add("教育学");
        mTypeForumTitle.add("文学");
        mTypeForumTitle.add("历史学");
        mTypeForumTitle.add("理学");
        mTypeForumTitle.add("工学");
        mTypeForumTitle.add("农学");
        mTypeForumTitle.add("医学");
        mTypeForumTitle.add("军事学");
        mTypeForumTitle.add("管理学");

        mTypeForumFragments.add(new phiForum());
        mTypeForumFragments.add(new ecoForum());
        mTypeForumFragments.add(new jurForum());
        mTypeForumFragments.add(new pedForum());
        mTypeForumFragments.add(new litForum());
        mTypeForumFragments.add(new hisForum());
        mTypeForumFragments.add(new sciForum());
        mTypeForumFragments.add(new engForum());
        mTypeForumFragments.add(new agrForum());
        mTypeForumFragments.add(new medForum());
        mTypeForumFragments.add(new milsciForum());
        mTypeForumFragments.add(new mansciForum());

    }


}

