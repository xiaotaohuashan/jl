package com.jl.myapplication.jl_login.adapter;

import android.os.Bundle;


import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.jl.myapplication.jl_login.fragment.GuideFragment;

import java.util.List;


public class GuideViewpagerAdapter extends FragmentPagerAdapter {
private List<String> mDataList;

public GuideViewpagerAdapter(List<String> dataList, FragmentManager fm) {
    super(fm);
    this.mDataList = dataList;
}

@Override
public Fragment getItem(int position) {

    GuideFragment fragment = new GuideFragment();
    Bundle bundle = new Bundle();
    bundle.putString("image", mDataList.get(position));
    if (position == mDataList.size() - 1){
        bundle.putBoolean("gotologin", true);
    }else {
        bundle.putBoolean("gotologin", false);
    }
    fragment.setArguments(bundle);
    return fragment;
}

@Override
public int getCount() {
    return mDataList == null ? 0 : mDataList.size();
}

}
