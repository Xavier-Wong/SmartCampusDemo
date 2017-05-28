package com.example.xavier.smartcampusdemo.util;

import android.app.Activity;
import android.content.Context;
import android.view.Gravity;
import android.widget.Toast;

/**
 * Created by Xavier on 4/7/2017.
 *
 */

public class DisplayUtils {
    public static void customBottomShortToast(Context context, String content, int OffsetX, int OffsetY) {
        Toast toast = Toast.makeText(context, content, Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.BOTTOM, OffsetX, OffsetY);
        toast.show();
    }

    public static void customCenterShortToast(Context context, String content, int OffsetX, int OffsetY) {
        Toast toast = Toast.makeText(context, content, Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.CENTER, OffsetX, OffsetY);
        toast.show();
    }

    public static void customTopShortToast(Context context, String content, int OffsetX, int OffsetY) {
        Toast toast = Toast.makeText(context, content, Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.TOP, OffsetX, OffsetY);
        toast.show();
    }

    public static int getStatusHeightPx(Activity act) {
        int height = 0;
        int resourceId = act.getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            height = act.getResources().getDimensionPixelSize(resourceId);
        }
        return height;
    }
}
