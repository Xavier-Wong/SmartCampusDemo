package com.example.xavier.smartcampusdemo.Util;

import android.app.Activity;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;

/**
 * Created by Xavier on 4/8/2017.
 *
 */

public class ColorUtils {
    public static Integer getColor(Fragment fragment, int id) {
        return ContextCompat.getColor(fragment.getContext(),id);
    }
    public static Integer getColor(Activity activity, int id) {
        return ContextCompat.getColor(activity,id);
    }
}
