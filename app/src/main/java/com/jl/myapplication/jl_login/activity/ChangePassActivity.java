package com.jl.myapplication.jl_login.activity;

import android.content.Context;
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
import com.jl.myapplication.databinding.ActivityChangePassBinding;

public class ChangePassActivity extends BaseActivity {
    private ActivityChangePassBinding mBinding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_change_pass;
    }

    @Override
    protected void initView() {
        mBinding = getBindView();
        setTitle("修改密码");
    }

    @Override
    protected void setListener() {
        super.setListener();
        mBinding.changePassCurrentPassEdit.addTextChangedListener(textWatcher);
        mBinding.changePassNewPassEdit.addTextChangedListener(textWatcher);
        mBinding.changePassSurePassEdit.addTextChangedListener(textWatcher);

        mBinding.changePassSureTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isFastClick()) {
                    return;
                }
                String pass = mBinding.changePassCurrentPassEdit.getText().toString().trim();
                String newPass = mBinding.changePassNewPassEdit.getText().toString().trim();
                String surePass = mBinding.changePassSurePassEdit.getText().toString().trim();
                if (!StringUtil.checkPassword(newPass)) {
                    ToastUtils.show(ChangePassActivity.this, "新密码格式格式不正确");
                    return;
                }
                if (!newPass.equals(surePass)) {
                    mBinding.changePassDifferentTip.setVisibility(View.VISIBLE);
                    return;
                } else {
                    mBinding.changePassDifferentTip.setVisibility(View.GONE);
                }
                changePass(pass, newPass);
            }
        });
    }

    private void verifyInput(String currentPass, String newPass, String surePass) {
        if (StringUtil.checkPassLenth(currentPass, 8) && StringUtil.checkPassLenth(newPass, 8) &&
                StringUtil.checkPassLenth(surePass, 8)) {
            mBinding.changePassSureTv.setEnabled(true);
            mBinding.changePassSureTv.setBackgroundResource(R.mipmap.btnnext);
        } else {
            mBinding.changePassSureTv.setEnabled(false);
            mBinding.changePassSureTv.setBackgroundResource(R.mipmap.btnunnext);
        }
    }

    TextWatcher textWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }

        @Override
        public void afterTextChanged(Editable s) {
            verifyInput(mBinding.changePassCurrentPassEdit.getText().toString().trim(), mBinding.changePassNewPassEdit.getText().toString().trim(), mBinding.changePassSurePassEdit.getText().toString().trim());
        }
    };

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

    public void changePass(String password, final String newPassword) {
        ToastUtils.show(this, "修改成功");
        finish();
    }
}
