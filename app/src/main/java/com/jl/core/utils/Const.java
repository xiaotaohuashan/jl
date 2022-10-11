package com.jl.core.utils;

import android.os.Environment;
import android.util.Log;

import java.io.File;

public class Const {
    public static String IMG_PATH = Environment.getExternalStorageDirectory().getAbsolutePath() + "/" + "onlineStoreImg";

    public static String getImgPath() {
        File file = new File(IMG_PATH);
        if (!file.exists()) {
            boolean mkdirs = file.mkdirs();
            if (!mkdirs) {
                Log.e("TAG", "文件夹创建失败");
                return Environment.DIRECTORY_PICTURES+"/"+"onlineStoreImg";
            } else {
                Log.e("TAG", "文件夹创建成功");
            }
        }else {

        }
        return IMG_PATH;
    }


}
