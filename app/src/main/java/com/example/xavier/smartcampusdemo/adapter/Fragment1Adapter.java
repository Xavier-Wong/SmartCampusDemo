package com.example.xavier.smartcampusdemo.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.List;

/**
 * Created by Xavier on 11/7/2016.
 * MainAdapter
 */

public class Fragment1Adapter extends FragmentPagerAdapter{

    private List<String> titles;
    private List<Fragment> fragments;

    public Fragment1Adapter(FragmentManager fm, List<Fragment> fragments) {
        super(fm);
        this.titles = titles;
        this.fragments = fragments;
    }

    @Override
    public Fragment getItem(int position) {
        return fragments.get(position);
    }

    @Override
    public int getCount() {
        return fragments.size();
    }


}
