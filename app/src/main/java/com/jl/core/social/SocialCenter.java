package com.jl.core.social;

import android.widget.Toast;

import com.jl.myapplication.App;
import com.jl.myapplication.config.Configs;
import com.tencent.mm.opensdk.modelmsg.SendAuth;
import com.tencent.mm.opensdk.modelmsg.SendMessageToWX;
import com.tencent.mm.opensdk.modelmsg.WXMediaMessage;
import com.tencent.mm.opensdk.modelmsg.WXTextObject;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;

import java.util.UUID;

public class SocialCenter {
    private final static int THUMB_SIZE = 200;

    private static SocialCenter _instance = null;

    // 第三方app和微信通信的openApi接口
    private IWXAPI _api;
    private String _userOpenId;

    private SocialCenterCallback _socialCenterCallback;

    public static SocialCenter getInstance() {
        if (_instance == null) {
            _instance = new SocialCenter();
        }
        return _instance;
    }

    private SocialCenter() {
    }

    public void init() {
        if (_api == null) {
            registToWX();
        }
    }

    private void registToWX() {
        //AppConst.WEIXIN.APP_ID是指你应用在微信开放平台上的AppID，记得替换。
        _api = WXAPIFactory.createWXAPI(App.getContext(), Configs.WECHAT_APPID, false);
        if (_api.isWXAppInstalled()) {
            // 将该app注册到微信
            _api.registerApp(Configs.WECHAT_APPID);
        }
    }

    public void actionSuccess(int type) {
        if (_socialCenterCallback != null) {
            if (type == SocialAction.WECHAT_LOGIN) {
                _socialCenterCallback.onWechatLoginSuccess(null);
            } else if (type == SocialAction.WECHAT_SHARE) {
                _socialCenterCallback.onWechatShareSuccess();
            }
        }
    }

    public void actionSuccess(int type, String code) {
        if (_socialCenterCallback != null) {
            if (type == SocialAction.WECHAT_LOGIN) {
                _socialCenterCallback.onWechatLoginSuccess(code);
            } else if (type == SocialAction.WECHAT_SHARE) {
                _socialCenterCallback.onWechatShareSuccess();
            }
        }
    }

    public void actionFailed(int type) {
        if (_socialCenterCallback != null) {
            if (type == SocialAction.WECHAT_LOGIN) {
                _socialCenterCallback.onWechatLoginFailed();
            } else if (type == SocialAction.WECHAT_SHARE) {
                _socialCenterCallback.onWechatShareSuccess();
            }
        }
    }

    public void setSocialCenterCallback(SocialCenterCallback callback) {
        _socialCenterCallback = callback;
    }

    public void wechatLogin() {
        if (!_api.isWXAppInstalled()) {
            Toast.makeText(App.getContext(), "您还未安装微信客户端", Toast.LENGTH_LONG).show();
            return;
        }
        final SendAuth.Req req = new SendAuth.Req();
        req.scope = "snsapi_userinfo";
        req.state = "diandi_wx_login";
        _api.sendReq(req);
    }

    /**
     * 分享到好友会话
     *
     * @param text
     */
    public void wxShareTextToSession(String text) {
        wxShareText(text, SendMessageToWX.Req.WXSceneSession);
    }

    /**
     * 分享到朋友圈
     */
    public void wxShareTextToTimeline(String text) {
        wxShareText(text, SendMessageToWX.Req.WXSceneTimeline);
    }

    private void wxShareText(String text, int scene) {
        if (_api == null || !_api.isWXAppInstalled()) {
            if (_socialCenterCallback != null) {
                _socialCenterCallback.onWechatShareFailed();
            }
            return;
        }
        WXTextObject textObject = new WXTextObject();
        textObject.text = text;

        WXMediaMessage msg = new WXMediaMessage();
        msg.mediaObject = textObject;
        msg.description = text;

        String transactionId = UUID.randomUUID().toString();
        SendMessageToWX.Req req = new SendMessageToWX.Req();
        req.transaction = "text-" + transactionId;
        req.message = msg;
        req.scene = scene;
        _api.sendReq(req);
    }

    public IWXAPI getWXApi() {
        return _api;
    }

    public void setUserOpenId(String openId) {
        _userOpenId = openId;
    }

    public String getUserOpenId() {
        return _userOpenId;
    }

    public void onWeiboShareSuccess() {
        if (_socialCenterCallback != null) {
            _socialCenterCallback.onWeiboShareSuccess();
        }
    }

    public void onWeiboShareCancel() {
        if (_socialCenterCallback != null) {
            _socialCenterCallback.onWeiboShareCancel();
        }
    }

    public void onWeiboShareFailed() {
        if (_socialCenterCallback != null) {
            _socialCenterCallback.onWeiboShareFailed();
        }
    }
}
