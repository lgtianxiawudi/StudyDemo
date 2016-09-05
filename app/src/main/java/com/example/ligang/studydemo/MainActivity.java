package com.example.ligang.studydemo;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.example.ligang.commonlibrary.base.ui.BaseActivity;

public class MainActivity extends BaseActivity {

    @Override
    protected String currActivityName() {
        return getString(R.string.main_title);
    }

    @Override
    protected void requestData() {

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }
}
