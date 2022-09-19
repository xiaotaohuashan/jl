package com.jl.core.log;

import android.text.TextUtils;



public class LogUtils {
    public static void i(String... args){
        print("Info", args);
    }

    public static void v(String... args){
        print("Verbose", args);
    }

    public static void w(String... args) {
        print("Warn", args);
    }

    public static void e(String... args){
        print("Error", args);
    }

    private static void print(String type, String... args){
//        DateTime dt = new DateTime();

        String str = " ";
        str += "\n********************************************************************\n";
//        str += "\tTime: " + dt.toString("yyyy-MM-dd HH:mm:ss.SSSSSS") + "\n";
        str += "\tThread ID: " + Thread.currentThread().getId() + "\n";
        str += "\tThread Group Name: " + Thread.currentThread().getThreadGroup().getName() + "\n";
        str += "\tFile: " + Thread.currentThread().getStackTrace()[4].getFileName() + "\n";
        str += "\tClass: " + Thread.currentThread().getStackTrace()[4].getClassName() + "\n";
        str += "\tFunction: " + Thread.currentThread().getStackTrace()[4].getMethodName() + "\n";
        str += "\tLine: " + Thread.currentThread().getStackTrace()[4].getLineNumber() + "\n";

        String tag = "[JunLang]";
        String msg = "";
        if(args.length == 1){
            msg = args[0];
        }else{
            msg = TextUtils.join(" ", args);
        }

        str += "\t" + type + ": " + msg + "\n";
        str += "********************************************************************\n";
        android.util.Log.i(tag, str);
    }
}
