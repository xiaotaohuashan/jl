package com.jl.myapplication.jl_me.activity;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.DatePicker;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;

import com.bigkoo.pickerview.TimePickerView;
import com.jl.core.base.activity.BaseActivity;
import com.jl.core.utils.Const;
import com.jl.core.utils.StringUtil;
import com.jl.myapplication.R;
import com.jl.myapplication.databinding.ActivitySettingBinding;

import com.luck.picture.lib.PictureSelector;
import com.luck.picture.lib.config.PictureConfig;

import com.luck.picture.lib.config.PictureMimeType;
import com.luck.picture.lib.entity.LocalMedia;

import com.yanzhenjie.permission.AndPermission;
import com.yanzhenjie.permission.runtime.Permission;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class SettingActivity extends BaseActivity {
    private TimePickerView pvTime; //时间选择器对象
    private DatePickerDialog datePickerDialog;
    ActivitySettingBinding mBinding;
    private PopupWindow _PopBottom;
    // 性别
    private int _gender;
    // 生日
    private String _birthday;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_setting;
    }

    @Override
    protected void initData() {
        mBinding = getBindView();
    }

    @Override
    protected void setListener() {
        super.setListener();
        mBinding.rlImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // 上传头像
                requestPermission();
            }
        });

        mBinding.rlSex.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // 用户性别
                PopupwindowShow();
            }
        });
        mBinding.birthday.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // 用户生日
                initTimePicker(); //初始化时间选择器
                pvTime.show();//显示时间选择器
            }
        });
    }
    // 请求权限
    public void requestPermission() {
        AndPermission.with(this)
                .runtime()
                .permission(Permission.CAMERA,Permission.READ_EXTERNAL_STORAGE,Permission.WRITE_EXTERNAL_STORAGE)
                .onGranted(permissions -> {
                    showSelectDialog(1);
                })
                .onDenied(permissions -> {
                    // Storage permission are not allowed.
                })
                .start();
    }

    public void showSelectDialog(int maxNum) {
        PictureSelector.create(this)
                .openGallery(PictureConfig.TYPE_IMAGE)
                .imageSpanCount(4)// 每行显示个数 int
                .maxSelectNum(1)
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
                .forResult(PictureConfig.CHOOSE_REQUEST);//结果回调onActivityResult code

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == -1 && data != null) {
            switch (requestCode) {
                case PictureConfig.CHOOSE_REQUEST:
                    List<LocalMedia> selectList = PictureSelector.obtainMultipleResult(data);
                    mBinding.ivHeadImage.setImageURI(Uri.parse(selectList.get(0).getCompressPath()));
                    break;
            }
        }
    }

    private void PopupwindowShow() {

        View view = LayoutInflater.from(this).inflate(R.layout.bm_pop_sex,
                null);
        RelativeLayout layout_choose;
        RelativeLayout layout_photo;
        RelativeLayout layout_cancel;
        layout_choose = (RelativeLayout) view.findViewById(R.id.layout_choose);
        layout_photo = (RelativeLayout) view.findViewById(R.id.layout_photo);
        layout_cancel = (RelativeLayout) view.findViewById(R.id.layout_cancel);

        layout_photo.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                backgroundAlpha(1f);
                _gender = 1;
                mBinding.tvSex.setText("男");
                _PopBottom.dismiss();
            }
        });
        layout_choose.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                backgroundAlpha(1f);
                _gender = 2;
                mBinding.tvSex.setText("女");
                _PopBottom.dismiss();
            }
        });
        layout_cancel.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                backgroundAlpha(1f);
                _PopBottom.dismiss();
            }
        });

        _PopBottom = new PopupWindow(view);
        _PopBottom.setWidth(WindowManager.LayoutParams.MATCH_PARENT);
        _PopBottom.setHeight(WindowManager.LayoutParams.MATCH_PARENT);
        _PopBottom.setTouchable(true);
        _PopBottom.setFocusable(true);
        _PopBottom.setOutsideTouchable(true);
        ColorDrawable dw = new ColorDrawable(0000000000);
        _PopBottom.setBackgroundDrawable(dw);
        backgroundAlpha(0.8f);
        // 动画效果 从底部弹起
        _PopBottom.setAnimationStyle(R.style.Animations_GrowFromBottom);

        _PopBottom.showAtLocation(mBinding.rlImage, Gravity.BOTTOM, 0, 0);//parent view随意

    }

    public void backgroundAlpha(float bgAlpha)
    {
        WindowManager.LayoutParams lp = getWindow().getAttributes();
        lp.alpha = bgAlpha; //0.0-1.0
        getWindow().setAttributes(lp);
    }

    //初始化时间选择器
    private void initTimePicker() {
        Calendar selectedDate = Calendar.getInstance();
        Calendar startDate = Calendar.getInstance();
        startDate.set(1900, 1, 1);//起始时间
        Calendar endDate = Calendar.getInstance();
        endDate.set(2099, 12, 31);//结束时间
        pvTime = new TimePickerView.Builder(this,
                new TimePickerView.OnTimeSelectListener() {
                    @Override
                    public void onTimeSelect(Date date, View v) {
                        //选中事件回调
                        //mTvMyBirthday 这个组件就是个TextView用来显示日期 如2020-09-08
                        mBinding.tvBirthdayTime.setText(getTimes(date));
                    }
                })
                //年月日时分秒 的显示与否，不设置则默认全部显示
                .setType(new boolean[]{true, true, true, false, false, false})
                .setLabel("年", "月", "日", "时", "", "")
                .isCenterLabel(true)
                .setDividerColor(Color.DKGRAY)
                .setContentSize(21)
                .setDate(selectedDate)
                .setRangDate(startDate, endDate)
                .setDecorView(null)
                .build();
    }

    //格式化时间
    private String getTimes(Date date) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        return format.format(date);
    }
}
