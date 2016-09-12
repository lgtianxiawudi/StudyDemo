package com.example.ligang.studydemo.scratch.ui;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.example.ligang.commonlibrary.base.ui.BaseActivity;
import com.example.ligang.studydemo.R;

public class ScratchActivity extends BaseActivity {

    @Override
    protected String currActivityName() {
        return getString(R.string.scratch_title);
    }

    @Override
    protected void requestData() {

    }

    @Override
    protected void initView() {

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sample_scratch_view);
    }
}
