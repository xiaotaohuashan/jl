package com.jl.core.network;

import com.google.gson.JsonObject;
import com.jl.core.network.httpInterface.DefaultObserver;

import java.io.IOException;
import java.net.CookieManager;
import java.net.CookiePolicy;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

public class BaseNetWork<T> {

    //接口地址
    private String mUrl;

    //接口对象
    private T mApi;

    private Class<T> mApiClass;

    public BaseNetWork(String url, Class<T> c) {
        mUrl = url;
        mApi = getRetrofit().create(c);
        mApiClass = c;
    }

    /**
     * 返回接口对象
     *
     * @return
     */
    public T getApi() {
        return mApi;
    }

    /**
     * 返回接口对象
     *
     * @return
     */
    public T getApi(String url) {
        if (!mUrl.equals(url)) {
            return getRetrofit().create(mApiClass);
        }
        return mApi;
    }

    /**
     * 获取retrofit
     * @return
     */
    private Retrofit getRetrofit() {
        CookieManager manager = new CookieManager();
        manager.setCookiePolicy(CookiePolicy.ACCEPT_ALL);
        OkHttpClient client = getOkHttpClient();
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(ApiConstants.BASE_URL)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .build();
        return retrofit;
    }

    /**
     * 获取okHttpClient
     *
     * @return
     */
    private OkHttpClient getOkHttpClient() {
        OkHttpClient client = new OkHttpClient().newBuilder()
                .addInterceptor(new Interceptor() {
            @Override
            public Response intercept(Chain chain) throws IOException {
                Request oldRequest = chain.request();
                Request.Builder builder = oldRequest.newBuilder();

                builder.method(oldRequest.method(), oldRequest.body());

//                //添加经纬度
//                addLocation(oldRequest, builder);
//                //获取token
//                addTokenInfo(chain, oldRequest, builder);
//                //添加app信息
//                addAppInfo(oldRequest, builder);

                Request newRequest = builder.build();
//                LogUtils.i( "intecept", newRequest);

                return chain.proceed(newRequest);
            }
        }).readTimeout(60, TimeUnit.SECONDS)
                .writeTimeout(60, TimeUnit.SECONDS)
                .connectTimeout(60, TimeUnit.SECONDS)
                .build();
        return client;
    }

    public void setSubscribe(Observable<JsonObject> observable, DefaultObserver observer) {
        observable.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(observer);
    }
}
