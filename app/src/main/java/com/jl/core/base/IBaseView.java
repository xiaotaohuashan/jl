package com.jl.core.base;

import android.os.Bundle;

import com.jl.core.base.activity.BaseActivity;

/**
 * Created by chenhe_han on 2018/5/11.
 * 公司：
 * 描述：view的基础接口 activity fragment dialog 都需要继承
 */

public interface IBaseView {
    /**
     * 默认的时间间隔
     */
    long DEFAULT_TIME_MILLS = 500L;
    /**
     * 显示loading框
     */
    void showLoadingDialog();

    /**
     * 隐藏loading框
     */
    void hideLoadingDialog();

    /**
     * 是否连续点击 默认值
     * 防止用户重复操作
     * @return
     */
    boolean isFastClick();

    /**
     * 指定时间间隔
     * @param mills  毫秒数
     * @return
     */
    boolean isFastClick(long mills);

    /**
     * 跳转
     * @param aClass 要跳转的activity
     */
    void jumpToActivity(Class<? extends BaseActivity> aClass);

    /**
     * 跳转传值
     * @param aClass 要跳转的activity
     * @param bundle 数据
     */
    void jumpToActivity(Class<? extends BaseActivity> aClass, Bundle bundle);

    /**
     * 跳转后销毁原来界面
     * @param aClass activity
     */
    void jumpToActivityAndFinish(Class<? extends BaseActivity> aClass);

    /**
     * 跳转后销毁原来界面
     * @param aClass activity
     * @param bundle 数据
     */
    void jumpToActivityAndFinish(Class<? extends BaseActivity> aClass, Bundle bundle);
    void jumpToActivityForResult(Class<? extends BaseActivity> aClass, int resultCode);
}
