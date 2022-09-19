package com.jl.core.utils;

import android.app.Activity;

import java.util.Iterator;
import java.util.Stack;

/**
 * Created by chenhe_han on 2018/5/11.
 * 公司：
 * 描述：activity管理工具
 */

public class ActivityManager {

    private Stack<Activity> mActivities;

    private ActivityManager() {
        mActivities = new Stack<>();
    }

    public static ActivityManager getInstance() {
        return SingletonHolder.ACTIVITY_MANAGER;
    }

    public void push(Activity activity) {
        mActivities.push(activity);
    }

    public Activity currentActivity() {
        if (mActivities.isEmpty()) {
            return null;
        }
        return mActivities.lastElement();
    }

    public void removeLast() {
        if (mActivities.isEmpty()) {
            return;
        }
        Activity activity = mActivities.pop();
        if (!activity.isFinishing()) {
            activity.finish();
        }
    }

    public void remove(Class<?> aClass) {
        if (mActivities.isEmpty()) {
            return;
        }
        Iterator<Activity> iterator = mActivities.iterator();
        while (iterator.hasNext()) {
            Activity activity = iterator.next();
            if (activity.getClass().getCanonicalName().equals(aClass.getCanonicalName())) {
                if (!activity.isFinishing()) {
                    activity.finish();
                }
                iterator.remove();
                break;
            }
        }
    }

    public void remove(Class<?>... activities) {
        if (activities == null || activities.length == 0) {
            return;
        }
        for (Class<?> activity : activities) {
            remove(activity);
        }
    }

    public void finishAll() {
        if (mActivities.isEmpty()) {
            return;
        }
        for (Activity activity : mActivities) {
            if (!activity.isFinishing()) {
                activity.finish();
            }
        }
        mActivities.clear();
    }

    private static class SingletonHolder {
        private static final ActivityManager ACTIVITY_MANAGER = new ActivityManager();
    }
}
