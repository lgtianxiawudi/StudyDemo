package com.example.ligang.studydemo.progress.ui;

import android.os.Bundle;

import com.example.ligang.commonlibrary.base.ui.BaseActivity;
import com.example.ligang.commonlibrary.view.TopTitleView;
import com.example.ligang.studydemo.R;
import com.example.ligang.studydemo.progress.view.ProgressCircle;

/**
 * Created by ligang967 on 16/9/18.
 */

public class ProgressCircleActivity extends BaseActivity {

    private ProgressCircle demo_process;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sample_progress_circle);
        demo_process = (ProgressCircle)findViewById(R.id.demo_process);
        demo_process.start();
    }

    @Override
    protected String currActivityName() {
        return getString(R.string.progress_title);
    }

    @Override
    protected void requestData() {

    }

    @Override
    protected void initView() {

    }

    @Override
    public TopTitleView getTopTitleView() {
        return super.getTopTitleView();
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
