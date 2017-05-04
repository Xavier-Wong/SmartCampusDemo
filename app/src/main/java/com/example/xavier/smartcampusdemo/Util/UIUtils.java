package com.example.xavier.smartcampusdemo.Util;

import android.app.Activity;
import android.content.Context;
import android.os.Build;
import android.view.Gravity;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

/**
 * Created by Xavier on 4/7/2017.
 *
 */

public class UIUtils {
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
}
