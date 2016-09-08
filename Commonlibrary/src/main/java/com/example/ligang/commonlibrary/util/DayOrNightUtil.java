package com.example.ligang.commonlibrary.util;

import android.support.v7.app.AppCompatDelegate;

/**
 * Created by ligang967 on 16/9/8.
 */
public class DayOrNightUtil {
    public static void setDayOrNight(@AppCompatDelegate.NightMode int mode){
        AppCompatDelegate.setDefaultNightMode(mode);
    }
}
