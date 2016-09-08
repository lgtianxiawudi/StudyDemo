package com.example.ligang.studydemo.main.ui;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.ligang.commonlibrary.base.ui.BaseFragment;
import com.example.ligang.studydemo.R;

/**
 * Created by ligang967 on 16/9/6.
 */
public class ShopFragment extends BaseFragment{
    @Override
    protected String currentTitle() {
        return getString(R.string.main_tab_shop);
    }

    @Override
    protected void requestData() {

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        TextView content = new TextView(getActivity());
        content.setText(currentTitle());
        return content;
    }
}
