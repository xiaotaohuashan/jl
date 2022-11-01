package com.jl.myapplication.jl_home.activity;


import android.content.Intent;

import com.jl.core.base.activity.BaseActivity;
import com.jl.myapplication.R;
import com.jl.myapplication.databinding.ActivityGoodsBinding;
import com.jl.myapplication.jl_home.adapter.ImageAdapter;
import com.jl.myapplication.model.DataBean;
import com.youth.banner.indicator.CircleIndicator;


/**
 * 商品页面
 */
public class GoodsActivity extends BaseActivity {
    private ActivityGoodsBinding mBinding;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_goods;
    }

    @Override
    protected void initView() {
        mBinding = getBindView();
        setTitle("商品");
        showMessage();
    }

    private void showMessage(){
        //自定义的图片适配器，也可以使用默认的BannerImageAdapter
        ImageAdapter adapter = new ImageAdapter(DataBean.getTestData3(),this);
        mBinding.viewBanner.setAdapter(adapter)
                .addBannerLifecycleObserver(this)//添加生命周期观察者
                .setIndicator(new CircleIndicator(this))//设置指示器
                .setOnBannerListener((data, position) -> {
                    String[] images = new String[DataBean.getTestData3().size()];
                    for (int i = 0; i < DataBean.getTestData3().size(); i++) {
                        images[i] = DataBean.getTestData3().get(i).imageUrl;
                    }
                    Intent intent = new Intent(mActivity, ImageBrowseActivity.class);
                    intent.putExtra("position",position);
                    intent.putExtra("img", images);
                    mActivity.startActivity(intent);
                });
    }
}
