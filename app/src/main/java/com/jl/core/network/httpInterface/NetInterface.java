package com.jl.core.network.httpInterface;


import androidx.lifecycle.Observer;

import com.google.gson.JsonObject;
import com.jl.core.network.BaseNetWork;
import com.jl.myapplication.model.BaseResult;
import com.jl.myapplication.model.GuidePage;
import com.jl.myapplication.model.HelpStoreParamForPage;
import com.jl.myapplication.model.PasswordBean;


import io.reactivex.Observable;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;


public class NetInterface extends BaseNetWork<NetApi> {
    public NetInterface(String url) {
        super(url, NetApi.class);
    }

    /**
     * 密码登录
     * @param cipherText
     * @param observer
     */
    public void loginPW(PasswordBean cipherText, DefaultObserver observer) {
        Observable<JsonObject> observable = getApi().loginPW(cipherText);
        setSubscribe(observable,observer);
    }

    /**
     * 获取注册短信
     */
    public void getPhoneCode(HelpStoreParamForPage bean, DefaultObserver observer) {
        Observable<JsonObject> observable = getApi().getPhoneCode(bean);
        setSubscribe(observable,observer);
    }

    /**
     * 获取注册短信
     */
    public void guidePage(HelpStoreParamForPage bean, DefaultObserver observer) {
        Observable<JsonObject> observable = getApi().guidePage(bean);
        setSubscribe(observable,observer);
    }
}

interface NetApi {
    //密码登录
    @POST("/userLogin/loginByUserNameAndPwd")
    Observable<JsonObject> loginPW(@Body PasswordBean data);

    // 获取注册短信
    @POST("/user/getPhoneCode")
    Observable<JsonObject> getPhoneCode(@Body HelpStoreParamForPage data);

    // 引导页接口
    @POST("/elandlordsvr/contactUs/queryGuidePage")
    Observable<JsonObject> guidePage(@Body HelpStoreParamForPage data);

}
