package com.jl.core.network.httpInterface;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.jl.core.log.LogUtils;
import com.jl.core.utils.JsonUtils;
import com.jl.core.utils.ToastUtils;
import com.jl.myapplication.R;


import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;

public abstract class DefaultObserver implements Observer<JsonObject> {
    private final String DATAS = "data";
    private final String STATUS = "status";
    private final String MSG = "msg";
    private final String PATH = "path";
    private final String SECONDDATA = "secondData";
    private final String THREADDATA = "threadData";
    private final String FOURDARA = "fourData";

    @Override
    public void onSubscribe(Disposable d) {
    }

    @Override
    public void onNext(JsonObject jsonObject) {
        if (jsonObject != null) {
            LogUtils.i(JsonUtils.toJson(jsonObject));
            try {
                int status = jsonObject.get(STATUS).getAsInt();
                String message = jsonObject.get(MSG).getAsString();
                JsonElement data = jsonObject.get(DATAS);

                onSuccess(status,message, data, jsonObject);
//                if (status >= 200 && status <= 300) {
//                    // status == 216 || status == 431 || status == 210
//                    // 216网络访问成功，有数据  431网络访问成功，但没有数据
//                    onSuccess(status, data, jsonObject);
//                } else {
//                    onOther(status, data, jsonObject);
//                    ToastUtils.show(message);
//                    onResponseMsg(message);
//                }

            } catch (Exception e) {
                LogUtils.e("DefaultObsetver------->OnNext:Exception:" + e);
                ToastUtils.show(R.string.network_error_hint);
            }

        } else {
            onUnSuccessFinish();
            ToastUtils.show(R.string.network_error_hint);
        }
    }

    @Override
    public void onError(Throwable e) {
        LogUtils.e("DefaultObserver------>onError:" + e);
        onFailure(e);
        onFinish();
        ToastUtils.show(R.string.network_error_hint);
    }

    @Override
    public void onComplete() {
        onFinish();
    }

    public abstract void onSuccess(int code,String message, JsonElement data, JsonObject all_data);

    public boolean onAllData(JsonObject jsonObject) {
        return false;
    }

    public void onUnSuccessFinish() {

    }

    public void onEmptyData() {

    }

    public void onOther(int code, JsonElement data, JsonObject all_data) {

    }

    public void onResponseMsg(String message) {

    }

    public void onFailure(Throwable e) {

    }

    public void onFinish() {

    }

}
