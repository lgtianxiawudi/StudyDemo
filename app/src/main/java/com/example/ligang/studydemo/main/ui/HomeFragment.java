package com.example.ligang.studydemo.main.ui;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.ligang.commonlibrary.base.ui.BaseFragment;
import com.example.ligang.commonlibrary.util.ActivityUtil;
import com.example.ligang.commonlibrary.util.DividerItemDecoration;
import com.example.ligang.studydemo.R;
import com.example.ligang.studydemo.dashboard.ui.DashboardDemoActivity;
import com.example.ligang.studydemo.main.adapter.MainListAdapter;
import com.example.ligang.studydemo.nestedsroll.ui.NestedScrollingCustomActivity;
import com.example.ligang.studydemo.progress.ui.ProgressCircleActivity;
import com.example.ligang.studydemo.scratch.ui.ScratchActivity;

import org.w3c.dom.Text;

import java.util.Arrays;


/**
 * Created by ligang967 on 16/9/6.
 */
public class HomeFragment extends BaseFragment implements AdapterView.OnItemClickListener {

    private String[] datas = null;

    private RecyclerView recyclerView;

    private MainListAdapter mainListAdapter;

    @Override
    protected String currentTitle() {
        return getString(R.string.main_tab_index);
    }

    @Override
    protected void requestData() {
        datas = getResources().getStringArray(R.array.main_list);
        mainListAdapter = new MainListAdapter(Arrays.asList(datas));
        mainListAdapter.setOnItemClickListener(this);
        recyclerView.setAdapter(mainListAdapter);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        recyclerView = (RecyclerView) inflater.inflate(R.layout.home_content,null);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext(),LinearLayoutManager.VERTICAL,false);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.addItemDecoration(new DividerItemDecoration(getContext(),DividerItemDecoration.VERTICAL_LIST,R.drawable.bottom_bg));

        return recyclerView;
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        switch (i){
            case 0:{//刮奖功能
                ActivityUtil.startActivity(getActivity(), ScratchActivity.class);
            }
            break;
            case 1:{//仪表盘
                ActivityUtil.startActivity(getActivity(), DashboardDemoActivity.class);
            }
            break;
            case 2:{//进度条
                ActivityUtil.startActivity(getActivity(), ProgressCircleActivity.class);
            }
            break;
            case 3:{
                ActivityUtil.startActivity(getActivity(), NestedScrollingCustomActivity.class);
            }
            break;
        }
    }
}
