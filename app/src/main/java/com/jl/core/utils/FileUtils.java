package com.jl.core.utils;

import android.os.Environment;
import android.webkit.MimeTypeMap;

import com.jl.myapplication.App;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

public class FileUtils {

    public static String getExtension(String url){
        return MimeTypeMap.getFileExtensionFromUrl(url);
    }

    public static boolean isFile(String path){
        File file = new File(path);
        return file.isFile();
    }

    public static boolean deleteFile(String path){
        File file = new File(path);
        return file.delete();
    }

    public static boolean isDirectory(String path){
        File file = new File(path);
        return file.isDirectory();
    }

    public static byte[] readFile(File file){
        if(file.isFile()){
            FileInputStream fileInputStream = null;
            try{
                fileInputStream = new FileInputStream(file);
                byte[] buffer = new byte[1024];
                ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                int len = 0;
                while ((len = fileInputStream.read(buffer)) != -1){
                    outputStream.write(buffer, 0, len);
                }
                return outputStream.toByteArray();
            }catch (IOException e){
                e.printStackTrace();
            }
        }else{
            System.out.println("it's not file");
        }
        return null;
    }

    public static void writeFile(File file, byte[] data){
        try {
            FileOutputStream fos = new FileOutputStream(file);
            fos.write(data);
            fos.close();
        }catch (IOException e){
            e.printStackTrace();
        }
    }

    /**
     * data目录 /data
     * @return
     */
    public static File getDataDirectory() {
        return Environment.getDataDirectory();
    }

    /**
     * 缓存目录 /cache
     * @return
     */
    public static File getDownloadCacheDirectory(){
        return Environment.getDownloadCacheDirectory();
    }

    /**
     * 外部或共享的存储目录
     * @return
     */
    public static File getExternalStorageDirectory(){
        return Environment.getExternalStorageDirectory();
    }

    /**
     * 系统目录 /system
     */
    public static File getRootDirectory(){
        return Environment.getRootDirectory();
    }

    /**
     * Alarm目录
     */
    public static File getExternalStoragePublicDirectoryAlarm(){
        return Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_ALARMS);
    }

    /**
     * DCIM目录
     */
    public static File getExternalStoragePublicDirectoryDCIM(){
        return Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM);
    }

    /**
     * Download目录
     */
    public static File getExternalStoragePublicDirectoryDownload(){
        return Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
    }

    /**
     * Movies目录
     */
    public static File getExternalStoragePublicDirectoryMovies(){
        return Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MOVIES);
    }

    /**
     * Music目录
     */
    public static File getExternalStoragePublicDirectoryMusic(){
        return Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MUSIC);
    }

    /**
     * Notification目录
     */
    public static File getExternalStoragePublicDirectoryNotification(){
        return Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_NOTIFICATIONS);
    }

    /**
     * Pictures目录
     */
    public static File getExternalStoragePublicDirectoryPictures(){
        return Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_NOTIFICATIONS);
    }

    /**
     * Podcasts目录
     */
    public static File getExternalStoragePublicDirectoryPodcasts(){
        return Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PODCASTS);
    }

    /**
     * Ringtones目录
     */
    public static File getExternalStoragePublicDirectoryRingtones(){
        return Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_RINGTONES);
    }

    /**
     * 获取外部私有目录
     */
    public static File getExternalFilesDir(){
        return App.getContext().getExternalFilesDir("");
    }

    /**
     * 获取外部私有目录下应用的目录
     */
    public static File getExternalAppDir(){
        return App.getContext().getExternalFilesDir(DeviceUtils.getPackName());
    }

    /**
     * 获取应用缓存目录
     */
    public static File getExternalCacheDir(){
        return App.getContext().getExternalCacheDir();
    }
}
