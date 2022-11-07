package com.jl.myapplication.config;

import static com.jl.core.utils.ImageLoaderUtil.getSDPath;

//配置
public class Configs {


    public static final String WECHAT_APPID = "wx1e024b5be06af83f";

    // 下载到相册的图片和视频的路径
    public static final String VIDEO_DOWNLOAD_FILE_PATH =  getSDPath() +"/junlang/";

    public static final String PHONE_NUMBER_REGEX = "^((13[0-9])|(14[5,7])|(15[0-3,5-9])|(16[6])|(17[0,1,3,5-8])|(18[0-9])|(19[8,9]))\\d{8}$";

    //手机号
    public static final String PHONE_NUMBER = "phoneNumber";

    public static final String PASSWORD_REGEX = "[a-zA-Z0-9]+";

    public static final String CHARACTOR_REGEX = "[a-zA-Z]+";

    //支持数字字母和中文
    public static final String INPUT_REGEX = "[^a-zA-Z0-9\\u4E00-\\u9FA5]";
}
