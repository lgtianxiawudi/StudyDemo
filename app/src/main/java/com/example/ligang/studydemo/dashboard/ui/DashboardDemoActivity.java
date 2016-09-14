package com.example.ligang.studydemo.dashboard.ui;

import android.os.Bundle;
import android.widget.ImageView;

import com.example.ligang.commonlibrary.base.ui.BaseActivity;
import com.example.ligang.commonlibrary.svgparse.SvgView;
import com.example.ligang.commonlibrary.util.LogUtil;
import com.example.ligang.studydemo.BuildConfig;
import com.example.ligang.studydemo.R;
import com.example.ligang.studydemo.dashboard.view.DashboardView;

/**
 * Created by ligang967 on 16/9/9.
 */

public class DashboardDemoActivity extends BaseActivity {

    @Override
    protected String currActivityName() {
        return getString(R.string.dashboard_title);
    }

    @Override
    protected void initView() {

    }

    @Override
    protected void requestData() {

    }

    private DashboardView dashboardView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sample_dashboard_view);
        dashboardView = (DashboardView) findViewById(R.id.dashbaosrd_demo);
        dashboardView.setMinAndMaxValue(250, 350);
        dashboardView.setCurrentValue(300);
        LogUtil.setEnvIsdebug(BuildConfig.IS_DEBUG);
        ImageView imageView = (ImageView) findViewById(R.id.image_view);
//        LoadingDrawable loadingDrawable = null;
        try {
//            loadingDrawable = new LoadingDrawable(new SvgAnimView(this));
            SvgView svgView = new SvgView(this, R.drawable.vector_drawable_set);
            imageView.setImageDrawable(svgView);
            svgView.start();
//            loadingDrawable.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }




}
