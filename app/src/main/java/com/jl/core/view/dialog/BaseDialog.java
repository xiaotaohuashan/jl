package com.jl.core.view.dialog;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.jl.core.base.IBaseView;
import com.jl.core.base.activity.BaseActivity;
import com.jl.myapplication.R;


/**
 * Created by chenhe_han on 2018/5/11.
 * 公司：
 * 描述：弹窗的基础类  和BaseFragment类似
 */

public abstract class BaseDialog extends DialogFragment implements IBaseView {
    protected Activity mActivity;
    protected BaseActivity mBaseActivity;
    protected View mRootView;
    private long lastClickTime;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mActivity = ((Activity) context);
        if (context instanceof BaseActivity){
            mBaseActivity = ((BaseActivity) context);
        }
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NO_TITLE, R.style.CustomDialog);
        initArguments();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        getDialog().setCancelable(isCancelDialog());
        getDialog().setCanceledOnTouchOutside(isCancelDialog());
        mRootView = inflater.inflate(getLayoutId(), container, false);
        initData();
        return mRootView;
    }

    protected abstract void initArguments();

    protected abstract int getLayoutId();
    protected abstract void initData();

    protected boolean isCancelDialog(){
        return true;
    }

    @Override
    public void showLoadingDialog() {

    }

    @Override
    public void hideLoadingDialog() {

    }

    @Override
    public boolean isFastClick() {
        return isFastClick(DEFAULT_TIME_MILLS);
    }

    @Override
    public boolean isFastClick(long mills) {
        long timeStamp = System.currentTimeMillis();
        if (timeStamp - lastClickTime <= mills) {
            return true;
        }
        lastClickTime = timeStamp;
        return false;
    }

    @Override
    public void jumpToActivity(Class<? extends BaseActivity> aClass) {
        Intent intent = new Intent();
        intent.setClass(mActivity, aClass);
        startActivity(intent);
    }

    @Override
    public void jumpToActivity(Class<? extends BaseActivity> aClass, Bundle bundle) {
        Intent intent = new Intent(mActivity, aClass);
        intent.putExtras(bundle);
        startActivity(intent);
    }

    @Override
    public void jumpToActivityAndFinish(Class<? extends BaseActivity> aClass) {
        jumpToActivity(aClass);
        mActivity.finish();
    }

    @Override
    public void jumpToActivityAndFinish(Class<? extends BaseActivity> aClass, Bundle bundle) {
        jumpToActivity(aClass, bundle);
        mActivity.finish();
    }

    @Override
    public void jumpToActivityForResult(Class<? extends BaseActivity> aClass, int resultCode) {

    }
}
