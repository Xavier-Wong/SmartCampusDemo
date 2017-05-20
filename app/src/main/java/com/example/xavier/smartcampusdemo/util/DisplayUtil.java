package com.example.xavier.smartcampusdemo.util;

import android.app.Activity;

/**
 * Created by Xavier on 5/4/2017.
 */

public class DisplayUtil {

    public static int getStatusHeightPx(Activity act)
    {
        int height = 0;
        int resourceId = act.getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            height = act.getResources().getDimensionPixelSize(resourceId);
        }
        return height;
    }
}
