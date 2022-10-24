package com.jl.myapplication.jl_login.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.bumptech.glide.Glide;
import com.jl.core.base.fragment.BaseFragment;
import com.jl.core.utils.SettingsUtil;
import com.jl.myapplication.R;
import com.jl.myapplication.databinding.FragmentGuideBinding;
import com.jl.myapplication.jl_login.LoginActivity;

public class GuideFragment extends BaseFragment {
    private FragmentGuideBinding mBinding;
    protected Context mContext;

    @Override
    public int getLayoutId() {
        return R.layout.fragment_guide;
    }

    @Override
    public void setListener() {
        mBinding.tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent it = new Intent(mContext, LoginActivity.class);
                startActivity(it);
                getActivity().finish();
                // 用户点击过立即体验，以后引导页面不显示
                SettingsUtil.setGuidePage(true);
            }
        });
    }

    @Override
    public void initView() {
        mBinding = getBindView();
        mContext = getActivity();
        Bundle bundle = getArguments();
        String image = bundle.getString("image");
        Boolean goToLogin = bundle.getBoolean("gotologin", true);
        if (goToLogin){
            mBinding.goToLogin.setVisibility(View.VISIBLE);
        }else {
            mBinding.goToLogin.setVisibility(View.GONE);
        }
        Glide.with(this).load(image).into(mBinding.iv);
        mBinding.tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent it = new Intent(mContext, LoginActivity.class);
                startActivity(it);
                getActivity().finish();
                // 用户点击过立即体验，以后引导页面不显示
                SettingsUtil.setGuidePage(true);
            }
        });
    }
}
