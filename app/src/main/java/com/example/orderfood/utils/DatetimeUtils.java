package com.example.orderfood.utils;

import android.annotation.SuppressLint;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class DatetimeUtils {
    public static final String DEFAULT_FORMAT_DATE = "dd-MM-yyyy,hh:mm a";

    public static String convertTimeStampToDate(long timeStamp){
        String result ="";
        try {

            @SuppressLint("SimpleDateFormat")
            SimpleDateFormat sdf = new SimpleDateFormat(DEFAULT_FORMAT_DATE);
            Date date = new Date(timeStamp);
            result = sdf.format(date);
        }catch (Exception e){
            e.printStackTrace();
        }
        return result;

    }
}
