package com.jl.core.utils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

/**
 * json工具类
 * Created by lenovo on 2017/9/4.
 */

public class JsonUtils {
    /**
     * 获取时间
     *
     * @param all
     * @return
     */
//    public static long getNowTime(JsonElement all) {
//        long time = 0;
//        try {
//            time = all.getAsJsonObject().get(ServiceHelper.PARAM_NOWTIME).getAsLong();
//        } catch (Exception e) {
//        }
//        return time;
//    }

    /**
     * 获取列表
     *
     * @param je
     * @param <T>
     * @return
     */
    public static <T> List<T> getList(JsonElement je, Class<T> cls) {
        Gson gson = new Gson();
        List<T> list = new ArrayList<T>();
        JsonArray array = je.getAsJsonArray();
        for (final JsonElement elem : array) {
            list.add(gson.fromJson(elem, cls));
        }
        return list;
    }

    /**
     * 转换为Json
     *
     * @param obj
     * @return
     */
    public static String toJson(Object obj) {
        Gson gson = new GsonBuilder().create();
        return gson.toJson(obj);
    }

    /**
     * 将json转换为model
     *
     * @param json
     * @param c
     * @param <T>
     * @return
     */
    public static <T> T fromJson(String json, Class<T> c) {
        Gson gson = new GsonBuilder().create();
        return gson.fromJson(new JsonParser().parse(json), c);
    }

    /**
     * 将json转换为model
     *
     * @param je
     * @param c
     * @param <T>
     * @return
     */
    public static <T> T fromJson(JsonElement je, Class<T> c) {
        Gson gson = new GsonBuilder().create();
        return gson.fromJson(je, c);
    }

    /**
     * 将json转换为model
     *
     * @param je
     * @param <T>
     * @return
     */
    public <T> T fromJson(JsonElement je, Type type) {
        Gson gson = new GsonBuilder().create();
        return gson.fromJson(je, type);
    }

    /**
     * 获取JsonElement
     *
     * @param json
     * @return
     */
    public static JsonElement getJsonElement(String json) {
        JsonParser parser = new JsonParser();
        return parser.parse(json);
    }
}
