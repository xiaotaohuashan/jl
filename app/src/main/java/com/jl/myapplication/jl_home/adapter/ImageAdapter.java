package com.jl.myapplication.jl_home.adapter;

import android.content.Context;
import android.view.ViewGroup;
import android.widget.ImageView;


import com.bumptech.glide.Glide;
import com.jl.myapplication.jl_home.ImageHolder;
import com.jl.myapplication.model.DataBean;
import com.youth.banner.adapter.BannerAdapter;

import java.util.List;

/**
 * 自定义布局，图片
 */
public class ImageAdapter extends BannerAdapter<DataBean, ImageHolder> {
    private Context mContext;

    public ImageAdapter(List<DataBean> mDatas,Context context) {
        super(mDatas);
        //设置数据，也可以调用banner提供的方法,或者自己在adapter中实现
        mContext = context;
    }

    //更新数据
    public void updateData(List<DataBean> data) {
        //这里的代码自己发挥，比如如下的写法等等
        mDatas.clear();
        mDatas.addAll(data);
        notifyDataSetChanged();
    }


    //创建ViewHolder，可以用viewType这个字段来区分不同的ViewHolder
    @Override
    public ImageHolder onCreateHolder(ViewGroup parent, int viewType) {
        ImageView imageView = new ImageView(parent.getContext());
        //注意，必须设置为match_parent，这个是viewpager2强制要求的
        ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT);
        imageView.setLayoutParams(params);
        imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
        return new ImageHolder(imageView);
    }

    @Override
    public void onBindView(ImageHolder holder, DataBean data, int position, int size) {
        Glide.with(mContext).load(data.imageUrl).into(holder.imageView);
    }

}
