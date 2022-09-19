package com.jl.core.social;

public interface SocialCenterCallback {
    default void onWechatLoginSuccess(String code){};
    default void onWechatLoginFailed(){};
    default void onWechatShareSuccess(){};
    default void onWechatShareFailed(){};

    default void onWeiboShareSuccess(){};
    default void onWeiboShareCancel(){};
    default void onWeiboShareFailed(){};
}
