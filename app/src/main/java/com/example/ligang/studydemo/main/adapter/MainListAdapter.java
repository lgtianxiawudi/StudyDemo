package com.example.ligang.studydemo.main.adapter;

import android.support.annotation.NonNull;
import android.widget.TextView;

import com.example.ligang.commonlibrary.base.adapter.BaseRecyViewAdapter;
import com.example.ligang.commonlibrary.base.adapter.BaseRecyViewAdapterHolder;
import com.example.ligang.studydemo.R;

import java.util.List;

/**
 * Created by ligang967 on 16/9/5.
 */
public class MainListAdapter extends BaseRecyViewAdapter<String> {
    public MainListAdapter(List<String> datas) {
        super(datas);
    }

    @Override
    public void bindDataToItem(BaseRecyViewAdapterHolder baseRecyViewAdapterHolder, String s) {
        TextView content = baseRecyViewAdapterHolder.getViewById(R.id.main_list_item_title);
        content.setText(s);
    }

    @Override
    public int getLayoutId() {
        return R.layout.main_list_adapter;
    }
}
