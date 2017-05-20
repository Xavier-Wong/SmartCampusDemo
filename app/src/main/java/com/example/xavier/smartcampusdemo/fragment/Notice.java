package com.example.xavier.smartcampusdemo.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.xavier.smartcampusdemo.R;

/**
 * Created by Xavier on 11/7/2016.
 * NoticeFragment
 */

public class Notice extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        return inflater.inflate(R.layout.fragment_notic, container, false);
    }


}