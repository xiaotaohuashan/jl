package com.jl.core.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;

import com.google.gson.Gson;
import com.jl.myapplication.App;

import java.lang.reflect.Type;

// 硬盘缓存
public class SettingsUtil {
    private static final String HOME_DATA = "home_data";//首页数据
    private static final String APP_SETTINGS = "app_settings";
    private static final String AVATTAR = "avatar";//头像
    private static final String USER_NAME = "userName";
    private static final String USER_PASSWORD = "userPassword";
    private static final String USER_TYPE = "userType";
    private static final String IS_GUIDE_PAGE = "is_guide_page";//用户是否第一次打开app
    // 通过类名字去获取一个对象
    public static <T> T getObject(Context context, Class<T> clazz) {
        String key = getKey(clazz);
        String json = getString(context, key, null);
        if (TextUtils.isEmpty(json)) {
            return null;
        }
        try {
            Gson gson = new Gson();
            return gson.fromJson(json, clazz);
        } catch (Exception e) {
            return null;
        }
    }

    // 通过Type去获取一个泛型对象
    public static <T> T getObject(Context context, Type type) {
        String key = getKey(type);
        String json = getString(context, key, null);
        if (TextUtils.isEmpty(json)) {
            return null;
        }
        try {
            Gson gson = new Gson();
            return gson.fromJson(json, type);
        } catch (Exception e) {
            return null;
        }
    }

    public static void putObject(Context context, Object object) {
        String key = getKey(object.getClass());
        Gson gson = new Gson();
        String json = gson.toJson(object);
        putString(context, key, json);
    }

    /**
     * 保存一个泛型对象
     *
     * @param context
     * @param object
     * @param type    如果你要保存 List<Person> 这个类, type应该 传入 new TypeToken<List<Person>>() {}.getType()
     */
    public static void putObject(Context context, Object object, Type type) {
        String key = getKey(type);
        Gson gson = new Gson();
        String json = gson.toJson(object);
        putString(context, key, json);
    }

    public static void removeObject(Context context, Class<?> clazz) {
        remove(context, getKey(clazz));
    }

    public static void removeObject(Context context, Type type) {
        remove(context, getKey(type));
    }

    public static String getKey(Class<?> clazz) {
        return clazz.getName();
    }

    public static String getKey(Type type) {
        return type.toString();
    }

    public static void remove(Context context, String key) {
        SharedPreferences sp = context.getSharedPreferences(HOME_DATA, Context.MODE_PRIVATE);
        SharedPreferences.Editor edit = sp.edit();
        edit.remove(key);
        edit.commit();
    }

    public static void putString(Context context, String key, String value) {
        SharedPreferences sp = context.getSharedPreferences(HOME_DATA, Context.MODE_PRIVATE);
        SharedPreferences.Editor edit = sp.edit();
        edit.putString(key, value);
        edit.commit();
    }

    public static String getString(Context context, String key, String defValue) {
        SharedPreferences sp = context.getSharedPreferences(HOME_DATA, Context.MODE_PRIVATE);
        return sp.getString(key, defValue);
    }

    public static String getTrueName() {
        return App.getInstance().getSharedPreferences(APP_SETTINGS, 0).getString(USER_NAME, "");
    }

    public static void setTrueName(String param) {
        SharedPreferences.Editor localEditor = App.getInstance().getSharedPreferences(APP_SETTINGS, 0).edit();
        localEditor.putString(USER_NAME, param);
        localEditor.apply();
    }

    public static String getPassword() {
        return App.getInstance().getSharedPreferences(APP_SETTINGS, 0).getString(USER_PASSWORD, "");
    }

    public static void setPassword(String param) {
        SharedPreferences.Editor localEditor = App.getInstance().getSharedPreferences(APP_SETTINGS, 0).edit();
        localEditor.putString(USER_PASSWORD, param);
        localEditor.apply();
    }

    public static String getUserType() {
        return App.getInstance().getSharedPreferences(APP_SETTINGS, 0).getString(USER_TYPE, "");
    }

    public static void setUserType(String param) {
        SharedPreferences.Editor localEditor = App.getInstance().getSharedPreferences(APP_SETTINGS, 0).edit();
        localEditor.putString(USER_TYPE, param);
        localEditor.apply();
    }

    public static boolean isGuidePage() {
        return App.getInstance().getSharedPreferences(APP_SETTINGS, 0).getBoolean(IS_GUIDE_PAGE, false);
    }

    public static void setGuidePage(boolean param) {
        SharedPreferences.Editor localEditor = App.getInstance().getSharedPreferences(APP_SETTINGS, 0).edit();
        localEditor.putBoolean(IS_GUIDE_PAGE, param);
        localEditor.apply();
    }
}


