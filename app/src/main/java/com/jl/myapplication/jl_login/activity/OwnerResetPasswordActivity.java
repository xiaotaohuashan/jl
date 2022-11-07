package com.jl.myapplication.jl_login.activity;

import android.content.Context;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import com.jl.core.base.activity.BaseActivity;
import com.jl.core.utils.StringUtil;
import com.jl.myapplication.R;
import com.jl.myapplication.config.Configs;
import com.jl.myapplication.databinding.ActivityBindWechatLayoutBinding;


/**
 * Created by chenhe_han on 2018/8/13.
 * 公司：
 * 描述：忘记密码 V_1.2.0
 */

public class OwnerResetPasswordActivity extends BaseActivity {
    private ActivityBindWechatLayoutBinding mBinding;

    //开始状态
    private static final int STATE_START = 1;
    //手机号码正确 可以获取验证码
    private static final int STATE_READY = 2;
    //倒计时状态
    private static final int STATE_COUNT_DOWN = 3;
    //倒计时结束
    private static final int STATE_RETRY = 4;

    private int mVerifyState = STATE_START;

    private static final long COUNT_DOWN_PERIOD = 60 * 1000L;
    private CountDownTimer mCountDownTimer;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_bind_wechat_layout;
    }

    @Override
    protected void initView() {
        mBinding = getBindView();
        mBinding.titleText.setText("找回密码");
        mBinding.bindButton.setText("下一步");
        mCountDownTimer = new CountDownTimer(COUNT_DOWN_PERIOD + 600, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                mBinding.fetchVerifyText.setText(String.format(getString(R.string.verify_count_down_text), millisUntilFinished / 1000));
            }

            @Override
            public void onFinish() {
                changeVerifyState(STATE_RETRY);
            }
        };
    }

    @Override
    protected void setListener() {
        super.setListener();
        mBinding.backImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        mBinding.fetchVerifyText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isFastClick()) {
                    fetchVerify();
                }
            }
        });
        mBinding.bindButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isFastClick()) {
                    clickNext();
                }
            }
        });
        mBinding.inputPhoneEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (StringUtil.checkPhoneNumber(s.toString())) {
                    if (mVerifyState == STATE_START) {
                        changeVerifyState(STATE_READY);
                    }
                } else {
                    if (mVerifyState == STATE_READY) {
                        changeVerifyState(STATE_START);
                    }
                }
                onInputChange();
            }
        });
        mBinding.inputVerifyEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                onInputChange();
            }
        });
    }

    private void onInputChange() {
        String phoneNumber = mBinding.inputPhoneEditText.getText().toString();
        String verifyCode = mBinding.inputVerifyEditText.getText().toString();
        mBinding.bindButton.setEnabled(StringUtil.checkPhoneNumber(phoneNumber) && StringUtil.checkVerifyCode(verifyCode));
    }

    private void changeVerifyState(int state) {
        if (state == mVerifyState) {
            return;
        }
        switch (state) {
            case STATE_START:
                mBinding.fetchVerifyText.setEnabled(false);
                mBinding.fetchVerifyText.setText("发送验证码");
                break;
            case STATE_READY:
                mBinding.fetchVerifyText.setEnabled(true);
                mBinding.fetchVerifyText.setText("发送验证码");
                break;
            case STATE_COUNT_DOWN:
                mBinding.fetchVerifyText.setEnabled(false);
                mBinding.fetchVerifyText.setText("发送验证码");
                break;
            case STATE_RETRY:
                mBinding.fetchVerifyText.setEnabled(true);
                mBinding.fetchVerifyText.setText("获取验证码");
                break;
            default:
                break;
        }
        mVerifyState = state;
    }

    private void clickNext() {
        final String phoneNumber = mBinding.inputPhoneEditText.getText().toString();
        String verifyCode = mBinding.inputVerifyEditText.getText().toString();
        Bundle bundle = new Bundle();
        bundle.putString(Configs.PHONE_NUMBER, phoneNumber);
        jumpToActivity(ChangePasswordActivity.class, bundle);
        finish();
    }

    private void fetchVerify() {
        final String phone = mBinding.inputPhoneEditText.getText().toString();
        changeVerifyState(STATE_COUNT_DOWN);
        mCountDownTimer.start();
    }

    @Override
    public void onBackPressed() {
        cancelTimer();
        super.onBackPressed();
    }

    private void cancelTimer() {
        if (mCountDownTimer != null) {
            mCountDownTimer.cancel();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        cancelTimer();
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        View view = getCurrentFocus();
        if (view != null && view instanceof EditText) {
            Rect rect = new Rect();
            view.getGlobalVisibleRect(rect);
            if (!rect.contains(((int) ev.getRawX()), ((int) ev.getRawY()))) {
                view.clearFocus();
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
            }
        }
        return super.dispatchTouchEvent(ev);
    }
}
