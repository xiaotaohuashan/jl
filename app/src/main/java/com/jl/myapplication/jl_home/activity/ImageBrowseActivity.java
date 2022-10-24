package com.jl.myapplication.jl_home.activity;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.jl.core.utils.ImageLoaderUtil;
import com.jl.myapplication.R;
import com.luck.picture.lib.photoview.OnViewTapListener;
import com.luck.picture.lib.photoview.PhotoView;


public class ImageBrowseActivity extends Activity {
    private ViewPager view_pager;
    private TextView tvIndicator;

    private String[] images;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_browse);
        images = getIntent().getStringArrayExtra("img");

        tvIndicator = findViewById(R.id.tvIndicator);
        tvIndicator.setText("1/" + images.length);

        view_pager = findViewById(R.id.view_pager);
        view_pager.setPageMargin((int) (getResources().getDisplayMetrics().density * 15));



        view_pager.setAdapter(new PagerAdapter() {
            @Override
            public int getCount() {
                return images.length;
            }

            @Override
            public boolean isViewFromObject(View view, Object object) {
                return view == object;
            }

            @Override
            public Object instantiateItem(ViewGroup container, int position) {
                final PhotoView view = new PhotoView(ImageBrowseActivity.this);
                view.setOnViewTapListener(new OnViewTapListener() {
                    @Override
                    public void onViewTap(View view, float x, float y) {
                        finish();
                    }
                });

                view.setScaleType(ImageView.ScaleType.FIT_CENTER);
                ImageLoaderUtil.loadImageHD(images[position], view);
                container.addView(view);
                return view;
            }

            @Override
            public void destroyItem(ViewGroup container, int position, Object object) {
                container.removeView((View) object);
            }
        });
        view_pager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                tvIndicator.setText(position + 1 + "/" + images.length);
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });
        view_pager.setCurrentItem(getIntent().getIntExtra("position", 0));

    }

    @Override
    public void finish() {
        super.finish();
//        overridePendingTransition(com.luck.picture.lib.R.anim.modal_in, com.luck.picture.lib.R.anim.modal_out);
    }
}