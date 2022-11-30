package com.jl.core.utils;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.provider.MediaStore;

import com.jl.myapplication.jl_message.utils.AttachmentStore;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.Calendar;

public class BitmapUtil {

    public static void compressPicture(String srcPath, String desPath) {

        FileOutputStream fos = null;
        BitmapFactory.Options op = new BitmapFactory.Options();

        // 开始读入图片，此时把options.inJustDecodeBounds 设回true了
        op.inJustDecodeBounds = true;
        Bitmap bitmap = BitmapFactory.decodeFile(srcPath, op);
        op.inJustDecodeBounds = false;

        // 缩放图片的尺寸
        float w = op.outWidth;
        float h = op.outHeight;
        float hh = 1024f;//
        float ww = 1024f;//
        // 最长宽度或高度1024
        float be = 1.0f;
        if (w > h && w > ww) {
            be = (float) (w / ww);
        } else if (w < h && h > hh) {
            be = (float) (h / hh);
        }
        if (be <= 0) {
            be = 1.0f;
        }
        op.inSampleSize = (int) be;// 设置缩放比例,这个数字越大,图片大小越小.
        // 重新读入图片，注意此时已经把options.inJustDecodeBounds 设回false了
        bitmap = BitmapFactory.decodeFile(srcPath, op);
        int desWidth = (int) (w / be);
        int desHeight = (int) (h / be);
        bitmap = Bitmap.createScaledBitmap(bitmap, desWidth, desHeight, true);
        try {
            fos = new FileOutputStream(desPath);
            if (bitmap != null) {
                bitmap.compress(Bitmap.CompressFormat.JPEG, 72, fos);
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    // 删除文件
    public static void delImage(String path){
        File file = new File(path);
        if (file.exists() && file.isFile()){
            file.delete();
        }
    }

    // 得到路径
    public static String getVideoFile() {

        File mediaFile = new File(FileUtils.getExternalStorageDirectory() + "/Video");
        if (!mediaFile.exists()) {
            mediaFile.mkdir();
        }
        String videoPath = mediaFile.getAbsolutePath() + File.separator + getDate() + ".mp4";
        return videoPath;
    }

    // 得到路径
    public static String getImageFile() {

        File mediaFile = new File(FileUtils.getExternalStorageDirectory() + "/Image");
        if (!mediaFile.exists()) {
            mediaFile.mkdir();
        }
        String imagePath = mediaFile.getAbsolutePath() + File.separator + getDate() + ".jpg";
        return imagePath;
    }

    // 得到路径
    public static String getAudioFile() {

        File mediaFile = new File(FileUtils.getExternalStorageDirectory() + "/Audio");
        if (!mediaFile.exists()) {
            mediaFile.mkdir();
        }
        String audioPath = mediaFile.getAbsolutePath() + File.separator + getDate() + ".mp3";
        return audioPath;
    }
    /*
     * 获取录制视频的日期 作为存储文件路径一部分
     * */
    private static String getDate() {
        // 获取当前时间
        Calendar ca = Calendar.getInstance();
        // 对应 年 月 日 时 分 秒
        int year = ca.get(Calendar.YEAR);
        int month = ca.get(Calendar.MONTH) + 1;
        int day = ca.get(Calendar.DATE);
        int hour = ca.get(Calendar.HOUR_OF_DAY);
        int minute = ca.get(Calendar.MINUTE);
        int second = ca.get(Calendar.SECOND);
        String months;
        String days;
        String hours;
        String minutes;
        String seconds;
        if (month < 10){
            months = "0" + minute;
        }else {
            months = "" + minute;
        }
        if (day < 10){
            days = "0" + day;
        }else {
            days = "" + day;
        }
        if (hour < 10){
            hours = "0" + hour;
        }else {
            hours = "" + hour;
        }
        if (minute < 10){
            minutes = "0" + minute;
        }else {
            minutes = "" + minute;
        }
        if (second < 10){
            seconds = "0" + second;
        }else {
            seconds = "" + second;
        }

        String date = year +  months + days + "_" + hours +  minutes + seconds;
        return date;
    }

    public static String getRealFilePath(final Context context, final Uri uri ) {
        if ( null == uri ) return null;
        final String scheme = uri.getScheme();
        String data = null;
        if ( scheme == null )
            data = uri.getPath();
        else if ( ContentResolver.SCHEME_FILE.equals( scheme ) ) {
            data = uri.getPath();
        } else if ( ContentResolver.SCHEME_CONTENT.equals( scheme ) ) {
            Cursor cursor = context.getContentResolver().query( uri, new String[] { MediaStore.Images.ImageColumns.DATA }, null, null, null );
            if ( null != cursor ) {
                if ( cursor.moveToFirst() ) {
                    int index = cursor.getColumnIndex( MediaStore.Images.ImageColumns.DATA );
                    if ( index > -1 ) {
                        data = cursor.getString( index );
                    }
                }
                cursor.close();
            }
        }
        return data;
    }

    public static Bitmap zoomBitmap(Bitmap bitmap, float vw, float vh) {
        // 获得图片宽高
        float width = bitmap.getWidth();
        float height = bitmap.getHeight();

        // 图片缩放倍数以及x，y轴平移位置
        float scaleWidht, scaleHeight, x, y;
        // 新的图片
        Bitmap newbmp = null;
        // 变换矩阵
        Matrix matrix = new Matrix();
        // 当宽高比大于所需要尺寸的宽高比时以宽的倍数为缩放倍数
        if ((width/height)<=vw/vh){

            scaleWidht = vw / width;
            scaleHeight = scaleWidht;
            // 获取bitmap源文件中y做表需要偏移的像数大小
            y = ((height*scaleHeight - vh) / 2)/scaleHeight;
            x = 0;
        }else {
            scaleWidht = vh / height;
            scaleHeight = scaleWidht;
            // 获取bitmap源文件中x做表需要偏移的像数大小
            x = ((width*scaleWidht -vw ) / 2)/scaleWidht;
            y = 0;
        }
        matrix.postScale(scaleWidht / 1f, scaleHeight / 1f);
        try {
            // 获得新的图片 （原图，x轴起始位置，y轴起始位置，x轴结束位置，Y轴结束位置，缩放矩阵，是否过滤原图）为防止报错取绝对值
            if (width - x > 0 && height - y > 0&&bitmap!=null)
                // createBitmap()方法中定义的参数x+width要小于或等于bitmap.getWidth()，y+height要小于或等于bitmap.getHeigh()
                newbmp = Bitmap.createBitmap(bitmap, (int) Math.abs(x), (int) Math.abs(y), (int) Math.abs(width - x),
                        (int) Math.abs(height - y), matrix, false);

        } catch (Exception e) {
            // 如果报错则返回原图，不至于为空白
            e.printStackTrace();
            return bitmap;
        }
        return newbmp;
    }

    public static String extractThumbnail(String videoPath, String thumbPath) {
        if (!AttachmentStore.isFileExist(thumbPath)) {
            Bitmap bitmap = ThumbnailUtils.createVideoThumbnail(videoPath, MediaStore.Images.Thumbnails.MICRO_KIND);
            if (bitmap != null) {
                AttachmentStore.saveBitmap(bitmap, thumbPath, true);
                return thumbPath;
            }
        }
        return thumbPath;
    }
}
