package com.jl.myapplication.jl_me.activity;


import android.Manifest;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.bumptech.glide.Glide;
import com.jl.core.base.activity.BaseActivity;
import com.jl.core.log.LogUtils;
import com.jl.core.utils.DpUtil;
import com.jl.core.utils.ListUtil;
import com.jl.core.utils.PicSelectUtil;
import com.jl.core.utils.StringUtil;
import com.jl.myapplication.R;
import com.jl.myapplication.databinding.ActivityMyCardBinding;
import com.jl.myapplication.databinding.ActivityPublishBinding;
import com.jl.myapplication.databinding.ImageviewPhotoBinding;
import com.jl.myapplication.jl_home.adapter.ImageAdapter;
import com.jl.myapplication.jl_me.adapter.PublishImageAdapter;
import com.luck.picture.lib.PictureSelector;
import com.luck.picture.lib.entity.LocalMedia;
import com.yanzhenjie.permission.AndPermission;
import com.yanzhenjie.permission.runtime.Permission;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;


/**
 * 发布管理
 */
public class PublishActivity extends BaseActivity {
    private List<LocalMedia> imagePathList = new ArrayList<>();
    private ActivityPublishBinding mBinding;
    @Override
    protected int getLayoutId() {
        return R.layout.activity_publish;
    }

    @Override
    protected void initView() {
        mBinding = getBindView();
        setTitle("发布管理");
    }

    @Override
    protected void setListener() {
        super.setListener();
        mBinding.ivAddMorePic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                requestPermissions();
            }
        });
    }

    private void requestPermissions() {
        LogUtils.i("0000");
        AndPermission.with(this)
                .runtime()
                .permission(Permission.CAMERA,Permission.READ_EXTERNAL_STORAGE,Permission.WRITE_EXTERNAL_STORAGE)
                .onGranted(permissions -> {
                    LogUtils.i("1111");
                    PicSelectUtil.chooseMultiplePic(this, 1001, 9, imagePathList);
                })
                .onDenied(permissions -> {
                    // Storage permission are not allowed.
                })
                .start();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            List<LocalMedia> pic = PictureSelector.obtainMultipleResult(data);
            imagePathList.clear();
            imagePathList.addAll(pic);
            initPhotoView();
        }
    }

    /**
     * 展示选择的图片
     */
    private void initPhotoView() {
        mBinding.llImageContainer.removeAllViews();
        if (!ListUtil.isEmpty(imagePathList)) {
            for (int i = 0; i < imagePathList.size(); i++) {
                ImageviewPhotoBinding bindView = DataBindingUtil.inflate(LayoutInflater.from(mContext), R.layout.imageview_photo, null, false);
                mBinding.llImageContainer.addView(bindView.getRoot());
                ImageView ivImage=bindView.getRoot().findViewById(R.id.ivImage);
                RelativeLayout.LayoutParams lpImage= (RelativeLayout.LayoutParams) ivImage.getLayoutParams();
                lpImage.bottomMargin= DpUtil.dp2px(mContext,6);

               /* ImageView ivDelete=bindView.getRoot().findViewById(R.id.ivDelete);
                RelativeLayout.LayoutParams lpDelete= (RelativeLayout.LayoutParams) ivDelete.getLayoutParams();
                lpDelete.topMargin= DpUtil.dp2px(mContext,-3);*/

                Glide.with(bindView.ivImage).load(imagePathList.get(i).getCompressPath()).into(bindView.ivImage);
                int finalI = i;
                bindView.ivDelete.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        imagePathList.remove(finalI);
                        mBinding.llImageContainer.removeViewAt(finalI);
                        initPhotoView();
                    }
                });
            }
        }
    }
}
