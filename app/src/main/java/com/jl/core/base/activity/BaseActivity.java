package com.jl.core.base.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.databinding.ViewDataBinding;

import com.jl.core.NetworkProgressDialog;
import com.jl.core.base.IBaseView;
import com.jl.core.log.LogUtils;
import com.jl.myapplication.App;
import com.jl.myapplication.R;

public abstract class BaseActivity extends AppCompatActivity implements IBaseView,View.OnClickListener{
    private TextView mJmui_title_tv;
    private ImageButton mReturn_btn;
    private TextView mJmui_title_left;
    public Button mJmui_commit_btn;
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

        mContext = this;
        mActivity = this;
        initView();
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
    protected abstract void initView();

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

    @Override
    public void jumpToActivity(Class<? extends BaseActivity> aClass) {
        Intent intent = new Intent(this, aClass);
        startActivity(intent);
    }

    @Override
    public void jumpToActivity(Class<? extends BaseActivity> aClass, Bundle bundle) {
        Intent intent = new Intent(this, aClass);
        intent.putExtras(bundle);
        startActivity(intent);
    }

    @Override
    public void jumpToActivityAndFinish(Class<? extends BaseActivity> aClass) {
        jumpToActivity(aClass);
        finish();
    }

    @Override
    public void jumpToActivityAndFinish(Class<? extends BaseActivity> aClass, Bundle bundle) {
        jumpToActivity(aClass, bundle);
        finish();
    }

    @Override
    public void jumpToActivityForResult(Class<? extends BaseActivity> aClass, int resultCode) {
        Intent intent = new Intent(this, aClass);
        startActivityForResult(intent, resultCode);
    }

    //初始化各个activity的title
    public void initTitle(boolean returnBtn, boolean titleLeftDesc, String titleLeft, String title, boolean save, String desc) {
        mReturn_btn = (ImageButton) findViewById(R.id.return_btn);
        mJmui_title_left = (TextView) findViewById(R.id.jmui_title_left);
        mJmui_title_tv = (TextView) findViewById(R.id.jmui_title_tv);
        mJmui_commit_btn = (Button) findViewById(R.id.jmui_commit_btn);

        if (returnBtn) {
            mReturn_btn.setVisibility(View.VISIBLE);
            mReturn_btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    if (imm.isActive() && getCurrentFocus() != null) {
                        if (getCurrentFocus().getWindowToken() != null) {
                            imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
                        }
                    }
                    finish();
                }
            });
        }
        if (titleLeftDesc) {
            mJmui_title_left.setVisibility(View.VISIBLE);
            mJmui_title_left.setText(titleLeft);
        }
        mJmui_title_tv.setText(title);
        if (save) {
            mJmui_commit_btn.setVisibility(View.VISIBLE);
            mJmui_commit_btn.setText(desc);
        }

    }

    public void setTitle(String title) {
        ((TextView) findViewById(R.id.tvTitle)).setText(title);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.ib_back) {
            finish();
        }
    }
}
