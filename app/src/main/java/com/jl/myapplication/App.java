package com.jl.myapplication;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.os.StrictMode;

import com.jl.core.gateway.Gateway;
import com.jl.core.network.AppService;
import com.jl.core.social.SocialCenter;
import com.tencent.bugly.crashreport.CrashReport;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.jpush.android.api.JPushInterface;
import cn.jpush.im.android.api.JMessageClient;
import cn.jpush.im.android.api.model.Conversation;
import cn.jpush.im.android.api.model.Message;

public class App extends Application {
    public static final String CONV_TITLE = "conv_title";
    public static final String CONV_TYPE = "conversationType"; //value使用 ConversationType
    public static final int IMAGE_MESSAGE = 1;
    public static final int TAKE_PHOTO_MESSAGE = 2;
    public static final int TAKE_LOCATION = 3;
    public static final int FILE_MESSAGE = 4;
    public static final int TACK_VIDEO = 5;
    public static final int TACK_VOICE = 6;
    public static final int BUSINESS_CARD = 7;
    public static List<Message> forwardMsg = new ArrayList<>();
    public static final String TARGET_ID = "targetId";
    public static final String TARGET_APP_KEY = "targetAppKey";
    public static final String GROUP_ID = "groupId";
    public static final int REQUEST_CODE_FRIEND_INFO = 16;
    public static String FILE_DIR = "sdcard/JChatDemo/recvFiles/";
    public static String PICTURE_DIR = "sdcard/JChatDemo/pictures/";
    public static String THUMP_PICTURE_DIR;
    private static App mApp;
    private static Context mContext;//全局上下文
    private static AppService appService;
    public static Conversation delConversation;
    public static final int REQUEST_CODE_CHAT_DETAIL = 14;
    public static Map<Long, Boolean> isAtMe = new HashMap<>();
    public static Map<Long, Boolean> isAtall = new HashMap<>();
    public static List<Message> ids = new ArrayList<>();
    public static final int REQUEST_CODE_SEND_LOCATION = 24;
    public static final int REQUEST_CODE_SEND_FILE = 26;
    public static final int REQUEST_CODE_FRIEND_LIST = 17;
    public static final int RESULT_CODE_SEND_FILE = 27;
    public static final String MSG_LIST_JSON = "msg_list_json";
    public static final int RESULT_CODE_CHAT_DETAIL = 15;
    public static final int RESULT_CODE_SEND_LOCATION = 25;
    public static final String NAME = "name";
    public static final String ATALL = "atall";
    public static final int RESULT_CODE_AT_ALL = 32;
    public static final int RESULT_CODE_AT_MEMBER = 31;
    public static final String MSG_JSON = "msg_json";
    @Override
    public void onCreate() {
        super.onCreate();
        mApp = this;
        mContext = getApplicationContext();
        THUMP_PICTURE_DIR = mContext.getFilesDir().getAbsolutePath() + "/JChatDemo";
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
        //极光聊天初始化
        JMessageClient.init(this);
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
