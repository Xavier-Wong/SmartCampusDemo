package com.example.xavier.smartcampusdemo.Adapter;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.view.ViewGroup;

import com.example.xavier.smartcampusdemo.Fragment.typeforum.agrForum;
import com.example.xavier.smartcampusdemo.Fragment.typeforum.ecoForum;
import com.example.xavier.smartcampusdemo.Fragment.typeforum.engForum;
import com.example.xavier.smartcampusdemo.Fragment.typeforum.hisForum;
import com.example.xavier.smartcampusdemo.Fragment.typeforum.jurForum;
import com.example.xavier.smartcampusdemo.Fragment.typeforum.litForum;
import com.example.xavier.smartcampusdemo.Fragment.typeforum.mansciForum;
import com.example.xavier.smartcampusdemo.Fragment.typeforum.medForum;
import com.example.xavier.smartcampusdemo.Fragment.typeforum.milsciForum;
import com.example.xavier.smartcampusdemo.Fragment.typeforum.pedForum;
import com.example.xavier.smartcampusdemo.Fragment.typeforum.phiForum;
import com.example.xavier.smartcampusdemo.Fragment.typeforum.sciForum;

import java.util.ArrayList;
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
