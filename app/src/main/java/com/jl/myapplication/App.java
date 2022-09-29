package com.jl.myapplication;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.os.StrictMode;

import com.jl.core.gateway.Gateway;
import com.jl.core.network.AppService;
import com.jl.core.social.SocialCenter;
import com.tencent.bugly.crashreport.CrashReport;

import cn.jpush.android.api.JPushInterface;

public class App extends Application {
    private static App mApp;
    private static Context mContext;//全局上下文
    private static AppService appService;
    @Override
    public void onCreate() {
        super.onCreate();
        mApp = this;
        mContext = getApplicationContext();
        //bugly
        CrashReport.initCrashReport(getApplicationContext(), "a240c7e802", false);

        //严苛模式，检测内存泄露
        StrictMode.ThreadPolicy.Builder builder = new StrictMode.ThreadPolicy.Builder();
        builder.detectDiskReads()//检测执行磁盘读取的代码
                .detectDiskWrites()//检测执行磁盘写入的代码
                .detectNetwork()//检测执行请求网络写入的代码
                .penaltyLog();//打印违规日志
        StrictMode.setThreadPolicy(builder.build());

        //微信分享登录
        SocialCenter.getInstance().init();
        //极光推送
        JPushInterface.setDebugMode(true);
        JPushInterface.init(this);
    }

    //获取全局的上下文
    public static Context getContext(){
        return mContext;
    }

    public static App getInstance() {
        return mApp;
    }

    public static AppService getService() {
        if(appService == null) {
            appService = new AppService();
        }
        return appService;
    }

}
