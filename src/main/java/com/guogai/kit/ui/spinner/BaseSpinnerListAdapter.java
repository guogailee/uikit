package com.guogai.kit.ui.spinner;

import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import java.util.ArrayList;

/**
 * SpinnerListAdapter基类
 */
public abstract class BaseSpinnerListAdapter extends BaseAdapter {

    public abstract ArrayList<String> getListData();

    @Override
    public int getCount() {
        return 0;
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        return null;
    }
}
