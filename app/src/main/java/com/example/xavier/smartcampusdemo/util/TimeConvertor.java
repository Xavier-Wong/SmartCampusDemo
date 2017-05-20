package com.example.xavier.smartcampusdemo.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created by Xavier on 3/8/2017.
 *
 */

public class TimeConvertor {
    public static String stampToDate(String s){
        String res;
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.CHINA);
        long lt = Long.valueOf(s);
        Date date = new Date(lt);
        res = simpleDateFormat.format(date);
        return res;
    }

    public static String timeToNow(String date){

        Date d1 = new Date();
        Date d2 = null;

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.CHINA);

        try {
            d2 = sdf.parse(date);
        } catch (ParseException pe) {
            System.out.println(pe.getMessage());
        }

        long dd1 = d1.getTime();
        assert d2 != null;
        long dd2 = d2.getTime();
        if((dd1-dd2) < 60000) {
            return "刚刚";
        }
        else if((dd1-dd2) < 3600000L) {
            int minutes = (int) ((dd1-dd2)/60/1000);
            return String.valueOf(minutes) + "分钟前";
        }
        else if((dd1-dd2) < 86400000L){
            int hours = (int) ((dd1-dd2)/60/60/1000);
            return String.valueOf(hours) + "小时前";
        }
        else if((dd1-dd2) < 604800000L){
            int days = (int) ((dd1-dd2)/24/60/60/1000);
            return String.valueOf(days) + "天前";
        }
        else if((dd1-dd2) < 2592000000L){
            int weeks = (int) ((dd1-dd2)/7/24/60/60/1000);
            return String.valueOf(weeks) + "周前";
        }
        else if((dd1-dd2) < 31536000000L){
            int months = (int) ((dd1-dd2)/30/24/60/60/1000);
            return String.valueOf(months) + "月前";
        }
        else {
            int years = (int) ((dd1-dd2)/30/24/60/60/1000);
            return String.valueOf(years) + "年前";
        }
    }
}
