package com.jl.myapplication.wxapi;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import com.jl.core.log.LogUtils;
import com.tencent.mm.opensdk.constants.ConstantsAPI;
import com.tencent.mm.opensdk.modelbase.BaseReq;
import com.tencent.mm.opensdk.modelbase.BaseResp;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.IWXAPIEventHandler;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;

import io.reactivex.annotations.Nullable;

public class WXPayEntryActivity extends Activity implements IWXAPIEventHandler {
    IWXAPI api;
    int resCode;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        api = WXAPIFactory.createWXAPI(this, "wx1e024b5be06af83f",false);
        api.registerApp("wx1e024b5be06af83f");//注册appid
        api.handleIntent(getIntent(), this);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        api.handleIntent(intent, this);
    }

    @Override
    public void onReq(BaseReq baseReq) {
        LogUtils.i("success");
    }

    @Override
    public void onResp(BaseResp baseResp) {
        if (baseResp.getType() == ConstantsAPI.COMMAND_PAY_BY_WX) {
            resCode = baseResp.errCode;
            switch (resCode) {
                case 0:
                    if (mWXEntryCallback != null){
                        mWXEntryCallback.OrderPay();
                    }
                    finish();
                    break;
                case -1:
                    Toast.makeText(this,"支付失败",Toast.LENGTH_SHORT).show();
                    finish();
                    break;
                case -2:
                    Toast.makeText(this,"用户取消支付",Toast.LENGTH_SHORT).show();
                    finish();
                    break;
            }
        }else{

        }
    }

    private static WXEntryCallback mWXEntryCallback;
    public static void setWXEntryCallback(WXEntryCallback wXEntryCallback){
        mWXEntryCallback = wXEntryCallback;
    }
}
