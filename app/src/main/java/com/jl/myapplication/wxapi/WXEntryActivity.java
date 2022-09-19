package com.jl.myapplication.wxapi;


import android.app.Activity;
import android.os.Bundle;

import com.jl.core.log.LogUtils;
import com.jl.core.social.SocialAction;
import com.jl.core.social.SocialCenter;
import com.tencent.mm.opensdk.modelbase.BaseReq;
import com.tencent.mm.opensdk.modelbase.BaseResp;
import com.tencent.mm.opensdk.modelmsg.SendAuth;
import com.tencent.mm.opensdk.openapi.IWXAPIEventHandler;


import io.reactivex.annotations.Nullable;

/**
 * Created by chenhe_han on 2018/8/1.
 * 公司：
 * 描述：微信分享使用
 */

public class WXEntryActivity   extends Activity implements IWXAPIEventHandler {
    private static final int RETURN_MSG_TYPE_LOGIN = 1;
    private static final int RETURN_MSG_TYPE_SHARE = 2;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //如果没回调onResp，八成是这句没有写
        SocialCenter.getInstance().getWXApi().handleIntent(getIntent(),this);
    }

    // 微信发送请求到第三方应用时，会回调到该方法
    @Override
    public void onReq(BaseReq baseReq) {
        LogUtils.i("错误码： " + baseReq.openId);
    }

    // 第三方应用发送到微信的请求处理后的响应结果，会回调到该方法
    //app发送消息给微信，处理返回消息的回调
    @Override
    public void onResp(BaseResp baseResp) {
        LogUtils.i("错误码： " + baseResp.errCode);
        switch (baseResp.errCode){
            case BaseResp.ErrCode.ERR_AUTH_DENIED:
            case BaseResp.ErrCode.ERR_USER_CANCEL:
                if(baseResp.getType() == RETURN_MSG_TYPE_LOGIN){
                    SocialCenter.getInstance().actionFailed(SocialAction.WECHAT_LOGIN);
                }else{
                    SocialCenter.getInstance().actionFailed(SocialAction.WECHAT_SHARE);
                }
                break;
            case BaseResp.ErrCode.ERR_OK:
                switch(baseResp.getType()){
                    case RETURN_MSG_TYPE_LOGIN:
                        String code = ((SendAuth.Resp) baseResp).code;
                        SocialCenter.getInstance().actionSuccess(SocialAction.WECHAT_LOGIN, code);
                        break;
                    case RETURN_MSG_TYPE_SHARE:
                        SocialCenter.getInstance().actionSuccess(SocialAction.WECHAT_SHARE);
                        break;
                }
                break;
            default:

                break;
        }
        finish();
    }


}
