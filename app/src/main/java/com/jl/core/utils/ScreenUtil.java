package com.jl.core.utils;

import android.util.DisplayMetrics;

import com.jl.myapplication.App;


/**
 *
 * 公司：
 * 描述：屏幕相关的工具类
 */

public class ScreenUtil {
    private static final DisplayMetrics METRICS = App.getInstance().getResources().getDisplayMetrics();

    /**
     * dp 转 px
     *
     * @param dpValue
     * @return
     */
    public static int dip2Px(int dpValue) {
        return (int) (METRICS.density * dpValue + 0.5f);
    }

    /**
     * px 转 dp
     *
     * @param pxValue
     * @return
     */
    public static int px2Dip(int pxValue) {
        return (int) (pxValue / METRICS.density + 0.5f);
    }

    /**
     * 获取屏幕宽度
     *
     * @return
     */
    public static int screenWidth() {
        return METRICS.widthPixels;
    }

    /**
     * 获取屏幕高度
     *
     * @return
     */
    public static int screenHeight() {
        return METRICS.heightPixels;
    }

    /**
     * sp转px
     *
     * @param spValue
     * @return
     */
    public static int sp2Px(int spValue) {
        return (int) (spValue * METRICS.scaledDensity + 0.5f);
    }

    /**
     * px转sp
     *
     * @param pxValue
     * @return
     */
    public static int px2Sp(int pxValue) {
        return (int) (pxValue / METRICS.scaledDensity + 0.5f);
    }

    public static float getDensity() {
        return METRICS.density;
    }

}
