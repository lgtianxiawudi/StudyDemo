package com.example.ligang.studydemo.nestedsroll.ui;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.example.ligang.commonlibrary.base.ui.BaseActivity;
import com.example.ligang.commonlibrary.util.DividerItemDecoration;
import com.example.ligang.studydemo.R;
import com.example.ligang.studydemo.main.adapter.MainListAdapter;

import java.util.Arrays;

/**
 * Created by ligang967 on 16/9/23.
 */

public class NestedScrollingCustomActivity extends BaseActivity {

    private RecyclerView recyclerView ;

    @Override
    protected String currActivityName() {
        return getString(R.string.nested_custom);
    }

    @Override
    protected void requestData() {
        String[] datas = getResources().getStringArray(R.array.nested_custom_list);
        MainListAdapter mainListAdapter = new MainListAdapter(Arrays.asList(datas));
        recyclerView.setAdapter(mainListAdapter);
    }

    @Override
    protected void initView() {
        recyclerView = (RecyclerView) findViewById(R.id.list);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.addItemDecoration(new DividerItemDecoration(this,DividerItemDecoration.VERTICAL_LIST,R.drawable.bottom_bg));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sample_nested_view);
    }
}
