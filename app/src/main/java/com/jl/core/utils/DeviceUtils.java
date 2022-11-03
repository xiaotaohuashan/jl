package com.jl.core.utils;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Environment;
import android.os.StatFs;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.text.format.Formatter;


import com.jl.core.log.LogUtils;
import com.jl.myapplication.App;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileFilter;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.Locale;

public class DeviceUtils {
    public static boolean hasNotchInScreen(Context context) {
        boolean ret = false;
        try {
            ClassLoader cl = context.getClassLoader();
            Class HwNotchSizeUtil = cl.loadClass("com.huawei.android.util.HwNotchSizeUtil");
            Method get = HwNotchSizeUtil.getMethod("hasNotchInScreen");
            ret = (boolean) get.invoke(HwNotchSizeUtil);
        } catch (ClassNotFoundException e) {
            LogUtils.i("test", "hasNotchInScreen ClassNotFoundException");
        } catch (NoSuchMethodException e) {
            LogUtils.i("test", "hasNotchInScreen NoSuchMethodException");
        } catch (Exception e) {
            LogUtils.i("test", "hasNotchInScreen Exception");
        } finally {
            return ret;
        }
    }

    public static int[] getNotchSize(Context context) {
        int[] ret = new int[]{0, 0};
        try {
            ClassLoader cl = context.getClassLoader();
            Class HwNotchSizeUtil = cl.loadClass("com.huawei.android.util.HwNotchSizeUtil");
            Method get = HwNotchSizeUtil.getMethod("getNotchSize");
            ret = (int[]) get.invoke(HwNotchSizeUtil);
        } catch (ClassNotFoundException e) {
            LogUtils.i("test", "getNotchSize ClassNotFoundException");
        } catch (NoSuchMethodException e) {
            LogUtils.i("test", "getNotchSize NoSuchMethodException");
        } catch (Exception e) {
            LogUtils.i("test", "getNotchSize Exception");
        } finally {
            return ret;
        }
    }

    public static boolean hasNotchInScreenOfOppo(Context context){
        return context.getPackageManager().hasSystemFeature("com.oppo.feature.screen.heteromorphism");
    }

    public static final int NOTCH_IN_SCREEN_VIVO = 0x00000020;//是否有凹槽
    public static final int ROUNDED_IN_SCREEN_VIVO = 0x00000008;//是否有圆角
    public static boolean hasNotchInScreenOfViVo(Context context){
        boolean ret = false;
        try {
            ClassLoader cl = context.getClassLoader();
            Class FtFeature = cl.loadClass("com.util.FtFeature");
            Method get = FtFeature.getMethod("isFeatureSupport", int.class);
            ret = (boolean) get.invoke(FtFeature, NOTCH_IN_SCREEN_VIVO);
        } catch (ClassNotFoundException e)
        { LogUtils.i("test", "hasNotchInScreen ClassNotFoundException"); }
        catch (NoSuchMethodException e)
        { LogUtils.i("test", "hasNotchInScreen NoSuchMethodException"); }
        catch (Exception e)
        { LogUtils.i("test", "hasNotchInScreen Exception"); }
        finally
        { return ret; }
    }

    public static int getNumberOfCPUCores() {
        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.GINGERBREAD_MR1) {
            return 1;
        }
        int cores;
        try {
            cores = new File("/sys/devices/system/cpu/").listFiles(CPU_FILTER).length;
        } catch (SecurityException e) {
            cores = 1;
        } catch (NullPointerException e) {
            cores = 1;
        }
        return cores;
    }

    private static final FileFilter CPU_FILTER = new FileFilter() {
        @Override
        public boolean accept(File pathname) {
            String path = pathname.getName();
            //regex is slow, so checking char by char.
            if (path.startsWith("cpu")) {
                for (int i = 3; i < path.length(); i++) {
                    if (path.charAt(i) < '0' || path.charAt(i) > '9') {
                        return false;
                    }
                }
                return true;
            }
            return false;
        }
    };

    public static String getAppMetaData(String key) {
        String resultData = null;
        try {
            PackageManager packageManager = App.getContext().getPackageManager();
            if (packageManager != null) {
                ApplicationInfo applicationInfo = packageManager.getApplicationInfo(App.getContext().getPackageName(), PackageManager.GET_META_DATA);
                if (applicationInfo != null) {
                    if (applicationInfo.metaData != null) {
                        resultData = applicationInfo.metaData.getString(key);
                    }
                }

            }
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        return resultData;
    }

    public static boolean isTabletDevice() {
        return (App.getContext().getResources().getConfiguration().screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK) >= Configuration.SCREENLAYOUT_SIZE_LARGE;
    }

    /**
     * sd卡是否可用
     * @return
     */
    public static boolean isExternalStorageAvailable(){
        return Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED);
    }


    /**
     * 手机内部存储空间
     * @return
     */
    public static String getIntervalMemorySize(){
        File file = Environment.getDataDirectory();
        StatFs statFs = new StatFs(file.getPath());
        long blockSizeLong = statFs.getBlockSizeLong();
        long blockCountLong = statFs.getBlockCountLong();
        long size = blockCountLong * blockSizeLong;
        return Formatter.formatFileSize(App.getContext(), size);
    }

    /**
     * 手机内部可用存储空间
     * @return
     */
    public static String getAvailableInternalMemorySize(){
        File file = Environment.getDataDirectory();
        StatFs statFs = new StatFs(file.getPath());
        long availableBlocksLong = statFs.getAvailableBlocksLong();
        long blockSizeLong = statFs.getBlockSizeLong();
        return Formatter.formatFileSize(App.getContext(), availableBlocksLong
                * blockSizeLong);
    }

    /**
     * 手机外部存储空间
     * @return
     */
    public static String getExternalMemorySize(){
        File file = Environment.getExternalStorageDirectory();
        StatFs statFs = new StatFs(file.getPath());
        long blockSizeLong = statFs.getBlockSizeLong();
        long blockCountLong = statFs.getBlockCountLong();
        return Formatter
                .formatFileSize(App.getContext(), blockCountLong * blockSizeLong);
    }

    /**
     * 手机外部可用存储空间
     * @return
     */
    public static String getAvailableExternalMemorySize(){
        File file = Environment.getExternalStorageDirectory();
        StatFs statFs = new StatFs(file.getPath());
        long availableBlocksLong = statFs.getAvailableBlocksLong();
        long blockSizeLong = statFs.getBlockSizeLong();
        return Formatter.formatFileSize(App.getContext(), availableBlocksLong
                * blockSizeLong);
    }

    public static String getSystemLanguage(){
        return Locale.getDefault().getLanguage();
    }

    public static String getSystemVersion(){
        return Build.VERSION.RELEASE;
    }

    public static String getSystemModel(){
        return Build.MODEL;
    }

    public static String getDeviceBrand() {
        return Build.BRAND;
    }

    public static int getVersionCode(){
        PackageManager manager = App.getContext().getPackageManager();
        int code = 0;
        try{
            PackageInfo info = manager.getPackageInfo(App.getContext().getPackageName(), 0);
            code = info.versionCode;
        }catch(PackageManager.NameNotFoundException e){
            e.printStackTrace();
        }
        return code;
    }

    public static String getVersionName(){
        PackageManager manager = App.getContext().getPackageManager();
        String name = null;
        try{
            PackageInfo info = manager.getPackageInfo(App.getContext().getPackageName(), 0);
            name = info.versionName;
        }catch(PackageManager.NameNotFoundException e){
            e.printStackTrace();
        }
        return name;
    }

    public static String getPackName(){
        return App.getContext().getPackageName();
    }

    public static String getAppName(){
        String appName = "";
        try{
            PackageManager packageManager = App.getContext().getPackageManager();
            ApplicationInfo info = App.getContext().getApplicationInfo();
            appName = (String) packageManager.getApplicationLabel(info);
        }catch(Exception e){
            e.printStackTrace();
        }
        return appName;
    }

    /**
     * 获取进程号对应的进程名
     *
     * @param pid 进程号
     * @return 进程名
     */
    public static String getProcessName(int pid) {
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new FileReader("/proc/" + pid + "/cmdline"));
            String processName = reader.readLine();
            if (!TextUtils.isEmpty(processName)) {
                processName = processName.trim();
            }
            return processName;
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        } finally {
            try {
                if (reader != null) {
                    reader.close();
                }
            } catch (IOException exception) {
                exception.printStackTrace();
            }
        }
        return null;
    }

    /**
     * 获取移动用户标志 IMSI
     * @return
     *
     * 用户权限：
     * <uses-permission android:name="android.permission.READ_PHONE_STATE" />
     */
    public static String getSubscriberId(){
        String strResult = "";
        TelephonyManager telephonyManager = (TelephonyManager) App.getContext().getSystemService(Context.TELEPHONY_SERVICE);
        if(telephonyManager != null){
            try{
                strResult = telephonyManager.getSubscriberId();
            }catch (SecurityException e){
                e.printStackTrace();
            }
        }
        return strResult;
    }

    /**
     * 获取设备ID
     *
     * 用户权限:
     * <uses-permission android:name="android.permission.READ_PHONE_STATE" />
     */
    public static String getDeviceId(){
        String strResult = null;
        TelephonyManager telephonyManager = (TelephonyManager) App.getContext()
                .getSystemService(Context.TELEPHONY_SERVICE);
        if (telephonyManager != null) {
            try{
                strResult = telephonyManager.getDeviceId();
            }catch (SecurityException e){
                e.printStackTrace();
            }
        }
        if (strResult == null) {
            strResult = Settings.Secure.getString(App.getContext().getContentResolver(),
                    Settings.Secure.ANDROID_ID);
        }
        return strResult;
    }

    /**
     * 获取SIM卡号
     *
     * 用到的权限：
     *  <uses-permission android:name="android.permission.READ_PHONE_STATE" />
     */
    public static String getSimID() {
        String strResult = "";
        TelephonyManager telephonyManager = (TelephonyManager) App.getContext()
                .getSystemService(Context.TELEPHONY_SERVICE);
        if (telephonyManager != null) {
            try{
                strResult = telephonyManager.getSimSerialNumber();
            }catch (SecurityException e){
                e.printStackTrace();
            }
        }
        return strResult;
    }

    /**
     * 获取Wifi Mac地址
     *
     * 要想获取更多Wifi相关信息请查阅WifiInfo资料
     *
     * 用到的权限：
     *  <uses-permission android:name="android.permission.ACCESS_WIFI_STATE" />
     */
    public static String getWifiMac() {
        WifiManager wifiManager = (WifiManager) App.getContext().getApplicationContext()
                .getSystemService(Context.WIFI_SERVICE);
        if (wifiManager != null) {
            WifiInfo wi = wifiManager.getConnectionInfo();
            return wi.getMacAddress();
        }
        return null;
    }
}
