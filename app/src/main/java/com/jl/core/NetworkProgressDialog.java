package com.jl.core;

import android.app.Dialog;
import android.content.Context;
import android.view.Window;
import android.view.WindowManager;

import com.jl.myapplication.R;


/**
 * 网络加载动画
 */

public class NetworkProgressDialog extends Dialog {
    public static NetworkProgressDialog ProgressDialogBarView = null;

    public NetworkProgressDialog(Context context) {
        super(context);
    }
    public NetworkProgressDialog(Context context, int theme) {
        super(context, theme);
        setCanceledOnTouchOutside(true);
    }

    public static NetworkProgressDialog createDialog(Context context) {
        ProgressDialogBarView = new NetworkProgressDialog(context, R.style.dialog_network);
        ProgressDialogBarView.setContentView(R.layout.progress_network);
        Window wd = ProgressDialogBarView.getWindow();
        WindowManager.LayoutParams lp = wd.getAttributes();
        lp.alpha = 0.96f;
        wd.setAttributes(lp);
        ProgressDialogBarView.setCanceledOnTouchOutside(false);
        return ProgressDialogBarView;
    }
    public static void clear() {
        ProgressDialogBarView = null;
    }
}