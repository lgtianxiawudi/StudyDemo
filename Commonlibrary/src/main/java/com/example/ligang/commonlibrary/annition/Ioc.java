package com.example.ligang.commonlibrary.annition;

import android.app.Activity;

/**
 * Created by ligang967 on 16/9/26.
 */

public class Ioc {
    public static void inject(Activity activity){
        inject(activity , activity);
    }
    public static void inject(Object host , Object root) {
        Class<?> clazz = host.getClass();
        String proxyClassFullName = clazz.getName()+"$$ViewInjector";
        //省略try,catch相关代码
        Class<?> proxyClazz = null;
        try {
            proxyClazz = Class.forName(proxyClassFullName);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        if (proxyClazz==null){
            return;
        }
        ViewInjector viewInjector = null;
        try {
            viewInjector = (ViewInjector) proxyClazz.newInstance();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        if (viewInjector==null){
            return;
        }
        viewInjector.inject(host,root);
    }
}