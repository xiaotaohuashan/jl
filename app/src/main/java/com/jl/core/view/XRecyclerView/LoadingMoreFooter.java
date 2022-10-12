package com.jl.core.view.XRecyclerView;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.jl.core.utils.DpUtil;
import com.jl.core.view.XRecyclerView.ProgressBarIndicator.AVLoadingIndicatorView;
import com.jl.myapplication.R;


public class LoadingMoreFooter extends LinearLayout {

    private SimpleViewSwitcher progressCon;
    public final static int STATE_LOADING = 0;
    public final static int STATE_COMPLETE = 1;
    public final static int STATE_NOMORE = 2;
    public final static int STATE_ERROR = 3;
    private TextView mText;


    public LoadingMoreFooter(Context context) {
        super(context);
        initView();
    }

    /**
     * @param context
     * @param attrs
     */
    public LoadingMoreFooter(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView();
    }

    public void initView() {
        setOrientation(VERTICAL);

        View line = new View(getContext());
        line.setBackgroundColor(Color.parseColor("#e1e4e5"));
        addView(line, ViewGroup.LayoutParams.MATCH_PARENT, 1);

        LinearLayout linearchild = new LinearLayout(getContext());
        linearchild.setGravity(Gravity.CENTER);
        linearchild.setBackgroundColor(0xffffffff);
        linearchild.setLayoutParams(new LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        progressCon = new SimpleViewSwitcher(getContext());
        progressCon.setLayoutParams(new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        AVLoadingIndicatorView progressView = new AVLoadingIndicatorView(this.getContext());
        progressView.setIndicatorColor(0xffB5B5B5);
        progressView.setIndicatorId(ProgressStyle.BallRotate);
        progressCon.setView(progressView);
        linearchild.addView(progressCon);

        mText = new TextView(getContext());
        mText.setText("正在加载...");
        LayoutParams layoutParams = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        layoutParams.setMargins((int) getResources().getDimension(R.dimen.ten_dp), 0, 0, 0);
        mText.setLayoutParams(layoutParams);
        linearchild.addView(mText);

        addView(linearchild, ViewGroup.LayoutParams.MATCH_PARENT, DpUtil.dp2px(getContext(),45));
    }

    public void setProgressStyle(int style) {
        if (style == ProgressStyle.SysProgress) {
            progressCon.setView(new ProgressBar(getContext(), null, android.R.attr.progressBarStyle));
        } else {
            AVLoadingIndicatorView progressView = new AVLoadingIndicatorView(this.getContext());
            progressView.setIndicatorColor(0xffB5B5B5);
            progressView.setIndicatorId(style);
            progressCon.setView(progressView);
        }
    }

    public void setState(int state) {
        switch (state) {
            case STATE_LOADING:
                mText.setText(getContext().getText(R.string.listview_loading));
                progressCon.setVisibility(View.VISIBLE);
                this.setVisibility(View.VISIBLE);
                break;
            case STATE_COMPLETE:
                this.setVisibility(View.GONE);
                break;
            case STATE_NOMORE:
                mText.setText(getContext().getText(R.string.nomore_loading));
                progressCon.setVisibility(View.GONE);
                this.setVisibility(View.VISIBLE);
                break;
            case STATE_ERROR:
                mText.setText(getContext().getText(R.string.load_error));
                progressCon.setVisibility(View.GONE);
                this.setVisibility(View.VISIBLE);
                break;
        }
    }
}
