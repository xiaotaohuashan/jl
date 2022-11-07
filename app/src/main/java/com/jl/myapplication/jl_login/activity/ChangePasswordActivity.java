package com.jl.myapplication.jl_login.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import com.jl.core.base.activity.BaseActivity;
import com.jl.core.utils.StringUtil;
import com.jl.core.utils.ToastUtils;
import com.jl.myapplication.R;
import com.jl.myapplication.config.Configs;
import com.jl.myapplication.databinding.ActivityChangePasswordLayoutBinding;


/**
 * Created by chenhe_han on 2018/8/9.
 * 公司：
 * 描述：终重置密码 V_1.2.0
 */

public class ChangePasswordActivity extends BaseActivity {
    private ActivityChangePasswordLayoutBinding mBinding;

    private String mPhoneNumber;
    private boolean isConfirmReady = false;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_change_password_layout;
    }

    @Override
    protected void initView() {
        mBinding = getBindView();
        Intent intent = getIntent();
        Bundle extras = intent.getExtras();
        mPhoneNumber = extras.getString(Configs.PHONE_NUMBER);
    }

    @Override
    protected void setListener() {
        super.setListener();
        TextChangerListener listener = new TextChangerListener();
        mBinding.editInputPassword.addTextChangedListener(listener);
        mBinding.editConfirmPassword.addTextChangedListener(listener);
        mBinding.backImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        mBinding.nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isFastClick()){
                    doChangePassword();
                }
            }
        });
    }

    private void doChangePassword() {
        // 验证密码是否一致  请求接口修改密码
        String origin = mBinding.editInputPassword.getText().toString();
        String confirm = mBinding.editConfirmPassword.getText().toString();
        if (!origin.equals(confirm)){
            ToastUtils.show(R.string.password_not_equal);
            return;
        }
        ToastUtils.show("重置成功");
        finish();
    }


    private void changeConfirmState(boolean isReady) {
        if (isConfirmReady == isReady) {
            return;
        }
        mBinding.nextButton.setEnabled(isReady);
        isConfirmReady = isReady;
    }

    private class TextChangerListener implements TextWatcher {

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }

        @Override
        public void afterTextChanged(Editable s) {
            if (StringUtil.checkPassword(mBinding.editInputPassword.getText().toString()) &&
                    StringUtil.checkPassword(mBinding.editConfirmPassword.getText().toString())) {
                changeConfirmState(true);
            } else {
                changeConfirmState(false);
            }
        }
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
