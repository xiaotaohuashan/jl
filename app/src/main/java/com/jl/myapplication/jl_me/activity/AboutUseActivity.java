package com.jl.myapplication.jl_me.activity;

import android.content.Intent;
import android.view.View;

import com.jl.core.base.activity.BaseActivity;
import com.jl.core.base.activity.WebviewActivity;
import com.jl.myapplication.R;
import com.jl.myapplication.databinding.ActivityAboutUseBinding;


public class AboutUseActivity extends BaseActivity {
    private ActivityAboutUseBinding mBinding;
    @Override
    protected int getLayoutId() {
        return R.layout.activity_about_use;
    }

    @Override
    protected void initView() {
        mBinding = getBindView();
    }

    @Override
    public void setListener() {
        super.setListener();
        mBinding.tvRegisterNeedKnow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AboutUseActivity.this, WebviewActivity.class);
                intent.putExtra("url","http://www.helpstore.com.cn/static/yiqu.html");
                intent.putExtra("title","注册须知");
                startActivity(intent);
            }
        });
    }
}
