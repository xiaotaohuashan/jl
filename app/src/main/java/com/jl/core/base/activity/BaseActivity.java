package com.jl.core.base.activity;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.databinding.ViewDataBinding;

import com.jl.core.NetworkProgressDialog;
import com.jl.core.base.IBaseView;
import com.jl.core.log.LogUtils;
import com.jl.myapplication.App;

public abstract class BaseActivity extends AppCompatActivity implements IBaseView {
    protected Context mContext;
    protected Activity mActivity;
    private ViewDataBinding viewDataBinding;
    private NetworkProgressDialog pd;
    private long lastClickTime;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LogUtils.i(this.getClass().getSimpleName() + " onCreate");
        if (getLayoutId() != 0) {
            setContentView(getLayoutId());
            viewDataBinding = DataBindingUtil.setContentView(this, getLayoutId());
        }
        if (getLayoutId() != 0) {
            setContentView(getLayoutId());
        }
        mContext = this;
        mActivity = this;
        initData();
        setListener();
    }

    /**
     * 返回布局信息
     *
     */
    protected abstract int getLayoutId();

    /**
     * 初始化数据
     */
    protected abstract void initData();

    /**
     * 初始化监听
     */
    protected void setListener() {}

    /**
     * 获取Application
     *
     * @return
     */
    public App getApp() {
        return  App.getInstance();
    }


    public <T extends ViewDataBinding> T getBindView() {
        return (T)viewDataBinding;
    }

    public void showLoadingDialog() {
        if (pd == null) {
            pd = NetworkProgressDialog.createDialog(this);
            if (!pd.isShowing()) {
                pd.show();
            }
        }
    }

    public void hideLoadingDialog() {
        if (pd != null) {
            pd.dismiss();
            pd = null;
            NetworkProgressDialog.clear();
        }
    }

    @Override
    public boolean isFastClick() {
        return isFastClick(DEFAULT_TIME_MILLS);
    }

    @Override
    public boolean isFastClick(long mills) {
        long currentTime = System.currentTimeMillis();
        if (currentTime - lastClickTime <= mills) {
            return true;
        }
        lastClickTime = currentTime;
        return false;
    }
}
