package com.jl.core.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.jl.myapplication.R;

public class LoadLayout extends LinearLayout {

    public LoadLayout(Context context) {
        this(context, null);
    }

    public LoadLayout(Context context, AttributeSet attrs) {
        this(context, attrs, -1);
    }

    public LoadLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        LayoutInflater inflater = LayoutInflater.from(getContext());
        inflater.inflate(R.layout.loadings, this, true);
        inflater.inflate(R.layout.loading_failed, this, true);
        inflater.inflate(R.layout.loading_empty, this, true);
    }

    /**
     * 布局加载完成后隐藏所有View
     */
    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        for (int i = 0; i < 3; i++) {
            getChildAt(i).setVisibility(GONE);
        }
    }

    /**
     * 设置failed点击事件
     *
     * @param listener 监听器
     */
    public void setFailedClickListener(OnClickListener listener) {
        if (listener != null)
            findViewById(R.id.layout_load_failed).setOnClickListener(listener);
    }

    public void showLoading() {
        for (int i = 0; i < 3; i++) {
            View child = this.getChildAt(i);
            if (i == 0) {
                child.setVisibility(VISIBLE);
            } else {
                child.setVisibility(GONE);
            }
        }
    }

    public void showFailed() {
        for (int i = 0; i < 3; i++) {
            View child = this.getChildAt(i);
            if (i == 1) {
                child.setVisibility(VISIBLE);
            } else {
                child.setVisibility(GONE);
            }
        }
    }

    public void showEmpty() {
        for (int i = 0; i < 3; i++) {
            View child = this.getChildAt(i);
            if (i == 2) {
                child.setVisibility(VISIBLE);
            } else {
                child.setVisibility(GONE);
            }
        }
    }

    public void showContent() {
        for (int i = 0; i < 3; i++) {
            View child = this.getChildAt(i);
            child.setVisibility(GONE);
        }
    }

    public void AddEmptyButtonCLickListener(OnClickListener listener){
        findViewById(R.id.btn_load_empty).setVisibility(VISIBLE);
        findViewById(R.id.btn_load_empty).setOnClickListener(listener);
    }

    public void hideEmptyButton(){
        findViewById(R.id.btn_load_empty).setVisibility(GONE);
    }

    public void setEmptyIcon(int resourceId) {
        ((ImageView) findViewById(R.id.img_load_empty)).setImageResource(resourceId);
    }

    public void setEmptyText(String text) {
        ((TextView) findViewById(R.id.tv_load_empty)).setText(text);
    }
}