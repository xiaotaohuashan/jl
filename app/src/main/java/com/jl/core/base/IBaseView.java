package com.jl.core.base;

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

}
