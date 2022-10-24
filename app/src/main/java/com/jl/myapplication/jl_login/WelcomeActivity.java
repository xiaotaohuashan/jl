package com.jl.myapplication.jl_login;

import android.content.Intent;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Message;
import com.jl.core.base.activity.BaseActivity;
import com.jl.core.utils.SettingsUtil;

import com.jl.myapplication.R;

public class WelcomeActivity extends BaseActivity {
    private CountDownTimer mCountDownTimer;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_welcome;
    }

    @Override
    protected void initView() {
        mCountDownTimer = new CountDownTimer(1000, 1000) {
            @Override
            public void onTick(long l) {
            }

            @Override
            public void onFinish() {
                handler.sendEmptyMessage(0);
            }
        };
        mCountDownTimer.start();

    }

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 0:
                    intoApp();
                    break;
            }
        }
    };

    private void intoApp() {
        //用户是否点击过立即体验
        if (!SettingsUtil.isGuidePage()) {
            // 去引导页面
            gotoGuide();
        } else {
            gotoLogin();
        }
    }

    //首次进入去引导页
    private void gotoGuide() {
        Intent it = new Intent(WelcomeActivity.this, GuideActivity.class);
        startActivity(it);
        finish();
    }

    //未登录去登录
    private void gotoLogin() {
        Intent it = new Intent(WelcomeActivity.this, LoginActivity.class);
        startActivity(it);
        finish();
    }
}
