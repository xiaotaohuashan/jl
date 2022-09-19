package com.jl.myapplication.login;

import android.content.Intent;

import android.util.TypedValue;
import android.widget.ImageView;
import android.widget.LinearLayout;
import androidx.viewpager.widget.ViewPager;

import com.jl.core.base.activity.BaseActivity;
import com.jl.myapplication.R;
import com.jl.myapplication.login.adapter.GuideViewpagerAdapter;
import com.jl.myapplication.model.Guide;

import java.util.ArrayList;
import java.util.List;

public class GuideActivity extends BaseActivity {

    private ViewPager mViewPager;
    private List<Guide> mImages;
    private List<String> mNewImages = new ArrayList<>();
    // dots容器
    private LinearLayout mLayoutDots;
    // 指示器布局参数
    private LinearLayout.LayoutParams mDotParams;
    // 指示器容器列表
    private List<ImageView> mDots = new ArrayList<ImageView>();
    //当前索引
    private int mCurrPageIndex;
    // 区分app来源：1.E房东2.E工长3.E租客
    private int mAppType = 1;

    private void init(){
        mViewPager = findViewById(R.id.view_pager);

        mLayoutDots = findViewById(R.id.llLayoutDots);

        mViewPager.setAdapter(new GuideViewpagerAdapter(mNewImages, getSupportFragmentManager()));

        initDots();
        initViewPagerListener();
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_guide;
    }

    @Override
    protected void initData() {
        getData();
    }

    private void initDots() {
        mDotParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);

        mDotParams.rightMargin = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 30, getResources().getDisplayMetrics());

        // 根据viewpager 的数据去添加指示器
        for(int i = 0; i < mNewImages.size(); i++) {

            ImageView imageView = new ImageView(getApplicationContext());

            // 添加背景选择器
            //imageView.setBackgroundResource(R.drawable.dot_selector);
            // 添加图片选择器
            imageView.setImageResource(R.drawable.dot_selector);

            // 默认为不选择灰色
            imageView.setSelected(false);

            // 添加到dot容器
            mLayoutDots.addView(imageView, mDotParams);
            mDots.add(imageView);
        }
        // 设置第一个为选择状态
        if (mDots.size() > 0){
            mDots.get(0).setSelected(true);
        }
    }

    private void initViewPagerListener() {

        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {

                //旧点不亮
                mDots.get(mCurrPageIndex % mNewImages.size()).setSelected(false);
                //设置新的点
                mCurrPageIndex = position;
                //新点亮起来
                mDots.get(mCurrPageIndex % mNewImages.size()).setSelected(true);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    private void getData() {

        mNewImages.add("http://picm.bbzhi.com/mingxingbizhi/gaoqingtaiwankuanpingmeinvbizhi/gaoqingtaiwankuanpingmeinvbizhi_351522_m.jpg");
        mNewImages.add("http://picm.bbzhi.com/mingxingbizhi/gaoqingtaiwankuanpingmeinvbizhi/gaoqingtaiwankuanpingmeinvbizhi_351522_m.jpg");
        mNewImages.add("http://picm.bbzhi.com/mingxingbizhi/gaoqingtaiwankuanpingmeinvbizhi/gaoqingtaiwankuanpingmeinvbizhi_351522_m.jpg");
        if (mNewImages.size() > 0){
            init();
        }else {
            startActivity(new Intent(GuideActivity.this,LoginActivity.class));
        }
    }
}
