package com.jl.core.utils;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;

import com.jl.core.log.LogUtils;
import com.jl.myapplication.R;
import com.luck.picture.lib.PictureSelector;
import com.luck.picture.lib.config.PictureConfig;
import com.luck.picture.lib.config.PictureMimeType;
import com.luck.picture.lib.entity.LocalMedia;

import java.util.List;


public class PicSelectUtil {

    /**
     * 选择头像（选择单个图片，1:1剪裁，压缩）
     *
     * @param activity    调用的activity
     * @param requestCode startActivityForResult的requestCode
     */
    public static void chooseHeadPic(final Activity activity, final int requestCode) {
        String[] arr = {"拍照", "从相册选择照片"};
        new AlertDialog.Builder(activity)
                .setItems(arr, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        if (i == 0) {
                            PictureSelector.create(activity)
                                    .openCamera(PictureMimeType.ofImage())// 拍照，也可录像或也可音频 看你传入的类型是图片or视频
                                    //.theme(R.style.picture_default_style)// 主题样式设置 具体参考 values/styles
                                    .maxSelectNum(1)// 最大图片选择数量
                                    .minSelectNum(1)// 最小选择数量
                                    .selectionMode(PictureConfig.SINGLE)// 多选or单选 PictureConfig.MULTIPLE : PictureConfig.SINGLE
                                    .previewImage(true)// 是否可预览图片
                                    .enableCrop(true)// 是否裁剪
                                    .compress(true)// 是否压缩
                                    .glideOverride(160, 160)// glide加载宽高，越小图片列表越流畅，但会影响列表图片浏览的清晰度
                                    .withAspectRatio(1, 1)// 裁剪比例 如16:9 3:2 3:4 1:1 可自定义
                                    .freeStyleCropEnabled(true)// 裁剪框是否可拖拽
                                    .circleDimmedLayer(true)// 是否圆形裁剪
                                    .showCropFrame(false)// 是否显示裁剪矩形边框 圆形裁剪时建议设为false
                                    .showCropGrid(false)// 是否显示裁剪矩形网格 圆形裁剪时建议设为false
                                    .forResult(requestCode);//结果回调onActivityResult code
                        } else if (i == 1) {
                            PictureSelector.create(activity)
                                    .openGallery(PictureMimeType.ofImage())// 照片，也可录像或也可音频 看你传入的类型是图片or视频
                                    //.theme(R.style.picture_default_style)// 主题样式设置 具体参考 values/styles
                                    .maxSelectNum(1)// 最大图片选择数量
                                    .minSelectNum(1)// 最小选择数量
                                    .selectionMode(PictureConfig.SINGLE)// 多选or单选 PictureConfig.MULTIPLE : PictureConfig.SINGLE
                                    .previewImage(true)// 是否可预览图片
                                    .enableCrop(true)// 是否裁剪
                                    .compress(true)// 是否压缩
                                    .glideOverride(160, 160)// glide加载宽高，越小图片列表越流畅，但会影响列表图片浏览的清晰度
                                    .withAspectRatio(1, 1)// 裁剪比例 如16:9 3:2 3:4 1:1 可自定义
                                    .freeStyleCropEnabled(true)// 裁剪框是否可拖拽
                                    .circleDimmedLayer(true)// 是否圆形裁剪
                                    .showCropFrame(false)// 是否显示裁剪矩形边框 圆形裁剪时建议设为false
                                    .showCropGrid(false)// 是否显示裁剪矩形网格 圆形裁剪时建议设为false
                                    .forResult(requestCode);//结果回调onActivityResult code
                        }
                    }
                }).show();
    }

    /**
     * 选择多图（选择多个图片，不剪裁，压缩）
     *
     * @param activity    调用的activity
     * @param requestCode startActivityForResult的requestCode
     * @param num         选择图片个数
     * @param list        选择的图片列表
     */
    public static void chooseMultiplePic(final Activity activity, final int requestCode, final int num, final List<LocalMedia> list) {
        String[] arr = {"拍照", "从相册选择照片"};
        LogUtils.i("2222");
        new AlertDialog.Builder(activity)
                .setItems(arr, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        LogUtils.i("3333");
                        if (i == 0) {
                            PictureSelector.create(activity)
                                    .openCamera(PictureMimeType.ofImage())// 单独拍照，也可录像或也可音频 看你传入的类型是图片or视频
                                    //.theme(R.style.picture_default_style)// 主题样式设置 具体参考 values/styles
                                    .maxSelectNum(num)// 最大图片选择数量
                                    .minSelectNum(1)// 最小选择数量
                                    .selectionMode(PictureConfig.MULTIPLE)// 多选or单选 PictureConfig.MULTIPLE : PictureConfig.SINGLE
                                    .previewImage(true)// 是否可预览图片
                                    .compress(true)// 是否压缩
                                    .glideOverride(160, 160)// glide加载宽高，越小图片列表越流畅，但会影响列表图片浏览的清晰度
                                    .selectionMedia(list)// 传入已选图片 List<LocalMedia> list
                                    .forResult(requestCode);//结果回调onActivityResult code
                        } else if (i == 1) {
                            PictureSelector.create(activity)
                                    .openGallery(PictureMimeType.ofImage())// 单独拍照，也可录像或也可音频 看你传入的类型是图片or视频
                                    //.theme(R.style.picture_default_style)// 主题样式设置 具体参考 values/styles
                                    .maxSelectNum(num)// 最大图片选择数量
                                    .minSelectNum(1)// 最小选择数量
                                    .selectionMode(PictureConfig.MULTIPLE)// 多选or单选 PictureConfig.MULTIPLE : PictureConfig.SINGLE
                                    .previewImage(true)// 是否可预览图片
                                    .compress(true)// 是否压缩
                                    .glideOverride(160, 160)// glide加载宽高，越小图片列表越流畅，但会影响列表图片浏览的清晰度
                                    .selectionMedia(list)// 传入已选图片 List<LocalMedia> list
                                    .forResult(requestCode);//结果回调onActivityResult code
                        }
                    }
                }).show();
    }

    /**
     * 选择单图（选择单个图片，不剪裁，压缩）
     *
     * @param activity    调用的activity
     * @param requestCode startActivityForResult的requestCode
     */
    public static void chooseSinglePic(final Activity activity, final int requestCode) {
        String[] arr = {"拍照", "从相册选择照片"};
        new AlertDialog.Builder(activity)
                .setItems(arr, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        if (i == 0) {
                            PictureSelector.create(activity)
                                    .openCamera(PictureMimeType.ofImage())// 单独拍照，也可录像或也可音频 看你传入的类型是图片or视频
                                    //.theme(R.style.picture_default_style)// 主题样式设置 具体参考 values/styles
                                    .maxSelectNum(1)// 最大图片选择数量
                                    .minSelectNum(1)// 最小选择数量
                                    .selectionMode(PictureConfig.SINGLE)// 多选or单选 PictureConfig.MULTIPLE : PictureConfig.SINGLE
                                    .previewImage(true)// 是否可预览图片
                                    .compress(true)// 是否压缩
                                    .glideOverride(160, 160)// glide加载宽高，越小图片列表越流畅，但会影响列表图片浏览的清晰度
                                    .forResult(requestCode);//结果回调onActivityResult code
                        } else if (i == 1) {
                            PictureSelector.create(activity)
                                    .openGallery(PictureMimeType.ofImage())// 单独拍照，也可录像或也可音频 看你传入的类型是图片or视频
                                    //.theme(R.style.picture_default_style)// 主题样式设置 具体参考 values/styles
                                    .maxSelectNum(1)// 最大图片选择数量
                                    .minSelectNum(1)// 最小选择数量
                                    .selectionMode(PictureConfig.SINGLE)// 多选or单选 PictureConfig.MULTIPLE : PictureConfig.SINGLE
                                    .previewImage(true)// 是否可预览图片
                                    .compress(true)// 是否压缩
                                    .glideOverride(160, 160)// glide加载宽高，越小图片列表越流畅，但会影响列表图片浏览的清晰度
                                    .forResult(requestCode);//结果回调onActivityResult code
                        }
                    }
                }).show();
    }

    /**
     * 选择头像
     *
     * @param activity    调用的activity
     * @param requestCode startActivityForResult的requestCode
     * @param num         选择图片个数
     */
    public static void chooseMultiplePic(final Activity activity, final int requestCode, final int num) {
        PictureSelector.create(activity)
                .openGallery(PictureConfig.TYPE_IMAGE)
                .imageSpanCount(4)// 每行显示个数 int
                .maxSelectNum(num)
                .selectionMode(PictureConfig.SINGLE)// 多选 or 单选 PictureConfig.MULTIPLE or PictureConfig.SINGLE
                .isSingleDirectReturn(true)//PictureConfig.SINGLE模式下是否直接返回
                .previewImage(true)// 是否可预览图片 true or false
                .isCamera(true)// 是否显示拍照按钮 true or false
                .imageFormat(PictureMimeType.JPEG)// 拍照保存图片格式后缀,默认jpeg
                .isZoomAnim(true)// 图片列表点击 缩放效果 默认true
                .setOutputCameraPath(Const.getImgPath())// 自定义拍照保存路径,可不填
                .enableCrop(true)// 是否裁剪 true or false
                .compress(true)// 是否压缩 true or false
                .compressSavePath(Const.getImgPath())//压缩图片保存地址
                .freeStyleCropEnabled(true)// 裁剪框是否可拖拽 true or false
                .showCropGrid(true)// 是否显示裁剪矩形网格 圆形裁剪时建议设为false    true or false
                .forResult(requestCode);//结果回调onActivityResult code
    }

    /**
     * 选择多图（选择多个图片，不剪裁，压缩）
     *
     * @param activity    调用的activity
     * @param requestCode startActivityForResult的requestCode
     * @param num         选择图片个数
     * @param type        类型  0拍照  1相册
     */
    public static void chooseMultiplePic(final Activity activity, final int requestCode, final int num, final int type) {
        if (type == 0) {
            PictureSelector.create(activity)
                    .openCamera(PictureMimeType.ofImage())// 单独拍照，也可录像或也可音频 看你传入的类型是图片or视频
                    //.theme(R.style.picture_default_style)// 主题样式设置 具体参考 values/styles
                    .maxSelectNum(num)// 最大图片选择数量
                    .minSelectNum(1)// 最小选择数量
                    .selectionMode(PictureConfig.MULTIPLE)// 多选or单选 PictureConfig.MULTIPLE : PictureConfig.SINGLE
                    .previewImage(true)// 是否可预览图片
                    .compress(true)// 是否压缩
                    .glideOverride(160, 160)// glide加载宽高，越小图片列表越流畅，但会影响列表图片浏览的清晰度
                    .forResult(requestCode);//结果回调onActivityResult code
        } else if (type == 1) {
            PictureSelector.create(activity)
                    .openGallery(PictureMimeType.ofImage())// 单独拍照，也可录像或也可音频 看你传入的类型是图片or视频
                    //.theme(R.style.picture_default_style)// 主题样式设置 具体参考 values/styles
                    .maxSelectNum(num)// 最大图片选择数量
                    .minSelectNum(1)// 最小选择数量
                    .selectionMode(PictureConfig.MULTIPLE)// 多选or单选 PictureConfig.MULTIPLE : PictureConfig.SINGLE
                    .previewImage(true)// 是否可预览图片
                    .compress(true)// 是否压缩
                    .glideOverride(160, 160)// glide加载宽高，越小图片列表越流畅，但会影响列表图片浏览的清晰度
                    .forResult(requestCode);//结果回调onActivityResult code
        }
    }
}
