package com.example.xavier.smartcampusdemo.util;

import android.content.Context;
import android.graphics.PointF;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;

/**
 * Created by Xavier on 4/1/2017.
 *
 */

public class NoScrollViewPager extends ViewPager {
    PointF downP = new PointF();
    /** 触摸时当前的点 **/
    PointF curP = new PointF();
    public NoScrollViewPager(Context context) {
        super(context);
    }

    public NoScrollViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public boolean onTouchEvent(MotionEvent arg0) {
        float x;
        float y;
        x = arg0.getX();
        y = arg0.getY();
        switch (arg0.getAction()) {
            case MotionEvent.ACTION_DOWN:
                curP.x = x;
                curP.y = y;
                break;
            case MotionEvent.ACTION_MOVE:
                if(Math.abs(curP.x - arg0.getX()) > 10){

                    if (curP.x < x && canScrollHorizontally(-1)) {
                        // Left to Right
                        return false;
                    } else if(curP.x > x && canScrollHorizontally(1)){
                        // Right to Left
                        return false;
                    }else{
                        // pass to viewpager?
                        return true;
                    }
                }
                break;
            case MotionEvent.ACTION_SCROLL:
                if(Math.abs(curP.x - arg0.getX()) > 10){

                    if (curP.x < x && canScrollHorizontally(-1)) {
                        // Left to Right
                        return false;
                    } else if(curP.x > x && canScrollHorizontally(1)){
                        // Right to Left
                        return false;
                    }else{
                        // pass to viewpager?
                        return true;
                    }
                }
                break;
        }
        Log.d("currenttouchactionhhh",arg0.getX()+","+arg0.getY());
        return true;
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent arg0) {
        float x;
        float y;
        x = arg0.getX();
        y = arg0.getY();
        switch (arg0.getAction()) {
            case MotionEvent.ACTION_DOWN:
                curP.x = x;
                curP.y = y;
                break;
            case MotionEvent.ACTION_MOVE:
                if(Math.abs(curP.x - arg0.getX()) > 10){

                    if (curP.x < x && canScrollHorizontally(-1)) {
                        // Left to Right
                        return true;
                    } else if(curP.x > x && canScrollHorizontally(1)){
                        // Right to Left
                        return true;
                    }else{
                        // pass to viewpager?
                        return false;
                    }
                }
                break;
            case MotionEvent.ACTION_SCROLL:
                if(Math.abs(curP.x - arg0.getX()) > 10){

                    if (curP.x < x && canScrollHorizontally(-1)) {
                        // Left to Right
                        return false;
                    } else if(curP.x > x && canScrollHorizontally(1)){
                        // Right to Left
                        return false;
                    }else{
                        // pass to viewpager?
                        return true;
                    }
                }
                break;
        }
        return false;
    }
}