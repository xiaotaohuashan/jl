package com.jl.core.view.dialog;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;

import androidx.databinding.DataBindingUtil;

import com.jl.core.utils.ScreenUtil;
import com.jl.core.utils.StringUtil;
import com.jl.myapplication.R;
import com.jl.myapplication.databinding.DialogTwobuttonMessageBinding;


/**
 * Created by zkl on 2018/5/3.
 */

public class TwoButtonMsgDialogUtil {

    public static Dialog dialog;

    public static void showDialog(Context context, String title, String content , View.OnClickListener listener) {
        showDialog(context,title,content,"","",listener);
    }

    public static void showDialog(Context context, String title, String content , String left, String right, View.OnClickListener listener) {
        dialog = new AlertDialog.Builder(context).create();
        dialog.show();

        DialogTwobuttonMessageBinding bind = DataBindingUtil.inflate(LayoutInflater.from(context), R.layout.dialog_twobutton_message, null, false);
        bind.tvCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
        bind.tvTitle.setText(title);
        bind.tvContent.setText(content);
        if(StringUtil.isEmpty(content)){
            bind.tvContent.setVisibility(View.GONE);
        }
        if(!StringUtil.isEmpty(left)){
            bind.tvCancel.setText(left);
        }
        if(!StringUtil.isEmpty(right)){
            bind.tvConfirm.setText(right);
        }

        bind.tvConfirm.setOnClickListener(listener);
        Window window = dialog.getWindow();
        window.setContentView(bind.getRoot());
        window.setLayout(ScreenUtil.screenWidth()*4/5, ViewGroup.MarginLayoutParams.WRAP_CONTENT);
        dialog.setCanceledOnTouchOutside(true);
    }

    public static void dismiss() {
        if (dialog != null)
            dialog.dismiss();
    }

    public static void show() {
        if (dialog != null)
            dialog.show();
    }
}
