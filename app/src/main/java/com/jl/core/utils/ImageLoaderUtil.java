package com.jl.core.utils;

import android.app.Activity;
import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Environment;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.jl.core.log.LogUtils;
import com.jl.myapplication.R;
import com.jl.myapplication.config.Configs;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;


public class ImageLoaderUtil {
    private static RequestOptions defaultOptions;

    static {
        defaultOptions = new RequestOptions()
                .placeholder(R.mipmap.icon_image_loading)
                .error(R.mipmap.icon_image_loading)
                .fallback(R.mipmap.icon_image_loading)
                .diskCacheStrategy(DiskCacheStrategy.RESOURCE);
    }

    /**
     * 加载本地资源图片
     *
     * @param resourceId 本地图片RecoloadImageHDurceId
     * @param view       要显示的ImageView
     */
    public static void loadImage(int resourceId, ImageView view) {
        Glide.with(view).load(resourceId).into(view);
    }

    /**
     * 加载本地图片
     *
     * @param file 本地图片file
     * @param view 要显示的ImageView
     */
    public static void loadImage(File file, ImageView view) {
        Glide.with(view).load(file).into(view);
    }

    /**
     * 正常加载图片
     *
     * @param url  图片url
     * @param view 要显示的ImageView
     */
    public static void loadImage(String url, ImageView view) {
        loadImageBase(url, view, null, defaultOptions);
    }

    /**
     * 正常加载图片（带监听器）
     *
     * @param url             图片url
     * @param view            要显示的ImageView
     * @param loadingListener 监听器
     */
    public static void loadImage(String url, ImageView view, RequestListener<Drawable> loadingListener) {
        loadImageBase(url, view, loadingListener, defaultOptions);
    }

    /**
     * 正常加载图片（带监听器）
     *
     * @param url        图片url
     * @param view       要显示的ImageView
     * @param resourseId 默认占位图ID
     */
    public static void loadImage(String url, ImageView view, int resourseId) {
        RequestOptions defaultOptions = new RequestOptions()
                .placeholder(resourseId)
                .error(resourseId)
                .fallback(resourseId);
        loadImageBase(url, view, null, defaultOptions);
    }

    /**
     * 不使用缓存加载图片
     *
     * @param url  图片url
     * @param view 要显示的ImageView
     */
    public static void loadImageNoCache(String url, ImageView view) {
        RequestOptions defaultOptions = new RequestOptions();
//                .placeholder(R.mipmap.img_default)
//                .error(R.mipmap.img_default)
//                .fallback(R.mipmap.img_default);
        RequestOptions optionsNoCache = defaultOptions.diskCacheStrategy(DiskCacheStrategy.NONE)
                .skipMemoryCache(true);
        loadImageBase(url, view, null, optionsNoCache);
    }

    /**
     * 加载圆角图片
     *
     * @param url    图片url
     * @param view   要显示的ImageView
     * @param conner 圆角大小（dp）
     */
    public static void loadImageConner(String url, ImageView view, float conner) {
        RequestOptions defaultOptions = new RequestOptions()
                .placeholder(R.mipmap.icon_image_loading)
                .error(R.mipmap.icon_image_loading)
                .fallback(R.mipmap.icon_image_loading);
        RequestOptions optionsConner = defaultOptions.transform(new RoundedCorners(DpUtil.dp2px(view.getContext(), conner)));
        loadImageBase(url, view, null, optionsConner);
    }

    /**
     * 加载原图
     *
     * @param url  图片url
     * @param view 要显示的ImageView
     */
    public static void loadImageHD(String url, ImageView view) {
        Glide.with(view).load(url).apply(defaultOptions).into(view);
    }

    public static void loadImageBase(String url, ImageView view, RequestListener<Drawable> loadingListener, RequestOptions options) {
        if (view != null)
           Glide.with(view).load(url).apply(options).listener(loadingListener).into(view);
    }

    public final static Bitmap returnBitMap(String url) {
        URL myFileUrl = null;
        Bitmap bitmap = null;
        try {
            myFileUrl = new URL(url);
            HttpURLConnection conn;
            conn = (HttpURLConnection) myFileUrl.openConnection();
            conn.setDoInput(true);
            int length = conn.getContentLength();
            conn.connect();
            InputStream is = conn.getInputStream();
            BufferedInputStream bis = new BufferedInputStream(is, length);
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inSampleSize =2;    // 设置缩放比例
            Rect rect = new Rect(0, 0,0,0);
            bitmap = BitmapFactory.decodeStream(bis,rect,options);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return bitmap;
    }

    /**
     * 将Bitmap转换成文件
     * 保存文件
     * @param bm
     * @param fileName
     * @throws IOException
     */
    public static File saveFile(Bitmap bm, String fileName) throws IOException {
        String path = Configs.VIDEO_DOWNLOAD_FILE_PATH;
        LogUtils.i(path);
        File dirFile = new File(path);
        if(!dirFile.exists()){
            dirFile.mkdir();
        }
        File myCaptureFile = new File(path + fileName);
        BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(myCaptureFile));
        bm.compress(Bitmap.CompressFormat.JPEG, 80, bos);
        bos.flush();
        bos.close();
        return myCaptureFile;
    }

    //获取sd卡路径
    public static String getSDPath(){
        File sdDir = null;
        boolean sdCardExist = Environment.getExternalStorageState()
                .equals(Environment.MEDIA_MOUNTED);//判断sd卡是否存在
        if(sdCardExist)
        {
            sdDir = Environment.getExternalStorageDirectory();//获取跟目录
        }
        return sdDir.toString();
    }

    public static void loadImage(final OnLoadImageEndCallback callback, final Activity context, final List<String> listdata) {
        final ProgressDialog dialog = new ProgressDialog(context);
        dialog.setMessage("正在加载图片...");
        dialog.show();
        new Thread(new Runnable() {
            @Override
            public void run() {
                List<Bitmap> bitmapList = new ArrayList<>();
                for (String url : listdata) {
                    bitmapList.add(returnImageBitMap(url));
                }
                context.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        dialog.cancel();
                    }
                });
                callback.onEnd(bitmapList);
            }
        }).start();
    }

    public interface OnLoadImageEndCallback {
        void onEnd(List<Bitmap> bitmapList);
    }

    public static Bitmap returnImageBitMap(final String url){
        Bitmap bitmap=null;

        URL imageurl = null;

        try {
            imageurl = new URL(url);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        try {
            HttpURLConnection conn = (HttpURLConnection)imageurl.openConnection();
            conn.setDoInput(true);
            conn.connect();
            InputStream is = conn.getInputStream();
            bitmap = BitmapFactory.decodeStream(is);
            is.close();

        } catch (IOException e) {
            e.printStackTrace();
        }

        return bitmap;
    }
}