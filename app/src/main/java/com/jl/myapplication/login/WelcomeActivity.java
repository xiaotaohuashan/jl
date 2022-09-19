package com.jl.myapplication.login;

import android.content.Intent;
import android.view.View;

import com.jl.core.base.activity.BaseActivity;
import com.jl.myapplication.R;
import com.jl.myapplication.databinding.ActivityWelcomeBinding;

public class WelcomeActivity extends BaseActivity {
    private ActivityWelcomeBinding mBinding;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_welcome;
    }

    @Override
    protected void initData() {

        mBinding = getBindView();

//        mBinding.getPresenter() = new WelcomeActivity();
//        mBinding.btn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//
//            }
//        });
    }

    public void onClickOne(View view){
        Intent it = new Intent(WelcomeActivity.this, GuideActivity.class);
        startActivity(it);
        finish();
    }

}
