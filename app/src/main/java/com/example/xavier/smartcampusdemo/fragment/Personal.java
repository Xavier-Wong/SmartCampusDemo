package com.example.xavier.smartcampusdemo.fragment;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.xavier.smartcampusdemo.R;
import com.example.xavier.smartcampusdemo.entity.user;
import com.example.xavier.smartcampusdemo.service.UserInfoService;
import com.facebook.drawee.view.SimpleDraweeView;

import static com.example.xavier.smartcampusdemo.service.NetService.getAvatarPath;
import static com.example.xavier.smartcampusdemo.service.NetService.getIP;

/**
 * Created by Xavier on 5/7/2017.
 */

public class Personal extends Fragment {

    SimpleDraweeView avatar, avatarBg;
    TextView username;

    SharedPreferences sharedPreferences;
    private String uid;
    private Activity activity;
    public int page = 0;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.fragment_personal, container, false);
        this.activity = getActivity();
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        sharedPreferences = activity.getSharedPreferences("USER_INFO", Context.MODE_PRIVATE);
        uid = sharedPreferences.getString("userID","");

        avatar = (SimpleDraweeView) activity.findViewById(R.id.personal_avatar);
        avatarBg = (SimpleDraweeView) activity.findViewById(R.id.personal_avatar_bg);
        username =(TextView) activity.findViewById(R.id.personal_username);
        new MyAsyncTaskGetUserInfoItem().execute(uid);
        super.onActivityCreated(savedInstanceState);
    }

    private class MyAsyncTaskGetUserInfoItem extends AsyncTask<String, String, user> {

        @Override
        protected user doInBackground(String... params) {
            return UserInfoService.getUserById(params[0]);
        }

        @Override
        protected void onPostExecute(user user) {
            super.onPostExecute(user);
            if(user!=null) {
                avatar.setImageURI(getAvatarPath() + user.getAvatar());
                avatarBg.setImageURI(getAvatarPath() + user.getAvatar());
                username.setText(user.getUsername());
            }
        }
    }
}
