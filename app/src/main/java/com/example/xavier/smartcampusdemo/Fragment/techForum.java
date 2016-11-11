package com.example.xavier.smartcampusdemo.Fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.example.xavier.smartcampusdemo.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Xavier on 11/7/2016.
 */

public class techForum extends Fragment {
    private ListView listView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.techforum_fragment, container, false);
        listView = (ListView)view.findViewById(R.id.lv1_techForum);
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(getActivity(),
                android.R.layout.simple_list_item_1 ,getData());
        listView.setAdapter(arrayAdapter);
        return view;
    }

    private List<String> getData(){
        List<String> data = new ArrayList<String>();
        data.add("计算机类");
        data.add("土木类");
        data.add("文学类");
        return data;
    }
}

