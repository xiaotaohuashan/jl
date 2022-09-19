package com.jl.core.utils;
import android.content.Context;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.StringRes;

import com.jl.core.log.LogUtils;
import com.jl.myapplication.App;
import com.jl.myapplication.R;

/**
 * toast工具
 * Created by PC on 2019/2/26.
 */

public class ToastUtils {
    private static final String TAG = "ToastUtils";
    private static Context mContext;
    private static Toast mToast;
    private static CharSequence oldMsg;
    private static long oldTime;
    private static long curTime;
    private static long REPEAT_DISPLAY_INTERVAL = 2000L;

    public ToastUtils() {
    }

    public static void init(Context context) {
        if(context != null) {
            mContext = context.getApplicationContext();
        }

    }

    public static void show(@StringRes int resId) {
        show(App.getInstance(), resId);
    }

    public static void show(Context context, @StringRes int resId) {
        if(context != null) {
            String msg = context.getString(resId);
            show((Context)context, msg);
        }
    }

    public static void show(CharSequence msg) {
        show(mContext, msg);
    }

    public static void show(Context context, CharSequence msg) {
        if(context == null) {
            LogUtils.i("ToastUtils" + "show" + "context is null");
        } else {
            curTime = System.currentTimeMillis();
            if(mToast == null) {
                Class var2 = ToastUtils.class;
                synchronized(ToastUtils.class) {
                    if(mToast == null) {
                        context.getApplicationContext();
                        mToast = Toast.makeText(context, "", Toast.LENGTH_SHORT);
                    }
                    show(mToast, msg);
                }
            } else {
                show(mToast, msg);
            }

        }
    }

    private static void show(Toast toast, CharSequence msg) {
        if(toast == null) {
            Log.e("ToastUtils", "mToast is null");
        } else if(msg == null) {
            Log.e("ToastUtils", "msg is null");
        } else {
            if(msg.equals(oldMsg)) {
                if(curTime > oldTime + REPEAT_DISPLAY_INTERVAL) {
                    toast.show();
                    oldTime = curTime;
                }
            } else {
                oldMsg = msg;
                toast.setText(msg);
                toast.show();
                oldTime = curTime;
            }

        }
    }

    /**
     * 自定义toast
     * @param view toast样式
     * @param giavity 起点位置
     * @param xOffset 水平向右位移
     * @param yOffset 垂直向下位移
     */
    public static void customToast(Context context, View view, int giavity, int xOffset, int yOffset) {
        Toast toast = Toast.makeText(context, "", Toast.LENGTH_LONG);
        toast.setView(view);
        toast.setGravity(giavity,xOffset,yOffset);
//        toast_pass_word_success.setMargin(float horizontalMargin, float verticalMargin)
        toast.show();
    }

    /**
     * 居中展示的toast
     * @param view
     */
    public static void customToastCenter(Context context, View view) {
        Toast toast = Toast.makeText(context, "", Toast.LENGTH_LONG);
        toast.setView(view);
        toast.setGravity(Gravity.CENTER,0,0);
//        toast_pass_word_success.setMargin(float horizontalMargin, float verticalMargin)
        toast.show();
    }

    /**
     * 居中展示的toast
     * @param text 提示内容
     */
    public static void customToastCenter(Context context, String text) {
        View view = LayoutInflater.from(context).inflate(R.layout.toast_common_view,null);
        TextView textTv = view.findViewById(R.id.toast_text);
        textTv.setText(text);
        Toast toast = Toast.makeText(context, "", Toast.LENGTH_LONG);
        toast.setView(view);
        toast.setGravity(Gravity.CENTER,0,0);
//        toast_pass_word_success.setMargin(float horizontalMargin, float verticalMargin)
        toast.show();
    }
}
