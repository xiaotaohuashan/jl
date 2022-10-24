package com.jl.myapplication.jl_login;

import android.content.Intent;

import android.util.TypedValue;
import android.widget.ImageView;
import android.widget.LinearLayout;
import androidx.viewpager.widget.ViewPager;

import com.jl.core.base.activity.BaseActivity;
import com.jl.myapplication.R;
import com.jl.myapplication.jl_login.adapter.GuideViewpagerAdapter;

import java.util.ArrayList;
import java.util.List;

public class GuideActivity extends BaseActivity {

    private ViewPager mViewPager;
    private List<String> mNewImages = new ArrayList<>();
    // dots容器
    private LinearLayout mLayoutDots;
    // 指示器布局参数
    private LinearLayout.LayoutParams mDotParams;
    // 指示器容器列表
    private List<ImageView> mDots = new ArrayList<ImageView>();
    //当前索引
    private int mCurrPageIndex;

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

        mNewImages.add("https://tc.16pic.com/00/93/62/16pic_9362798_b.png");
        mNewImages.add("https://img-qn.51miz.com/preview/element/00/01/22/13/E-1221354-3186DE29.jpg");
        mNewImages.add("https://pics4.baidu.com/feed/060828381f30e92421eb77eee4b292001f95f7e0.jpeg?token=0e013d11c770267d49400993b919c110");
        if (mNewImages.size() > 0){
            init();
        }else {
            startActivity(new Intent(GuideActivity.this,LoginActivity.class));
        }
    }
}
