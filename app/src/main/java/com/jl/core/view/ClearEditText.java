package com.jl.core.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.text.Editable;
import android.text.InputFilter;
import android.text.Spanned;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.CycleInterpolator;
import android.view.animation.TranslateAnimation;


import androidx.appcompat.widget.AppCompatEditText;

import com.jl.myapplication.R;
import com.jl.myapplication.config.Configs;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by chenhe_han on 2018/5/14.
 * 公司：
 * 描述：带删除按钮的editText
 */

public class ClearEditText extends AppCompatEditText implements
        View.OnFocusChangeListener, TextWatcher {
    /**
     * 删除按钮的引用
     */
    private Drawable mClearDrawable;
    /**
     * 控件是否有焦点
     */
    private boolean hasFocus;

    private static final int DEFAULT_DECIMAL_NUMBER = 2;
    private int mDecimalNumber;

    private IDeleteClickListener mDeleteClickListener;

    public ClearEditText(Context context) {
        this(context, null);
    }

    public ClearEditText(Context context, AttributeSet attrs) {
        //这里构造方法也很重要，不加这个很多属性不能再XML里面定义
        this(context, attrs, android.R.attr.editTextStyle);
    }

    public ClearEditText(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.ClearEditText);
        mDecimalNumber = typedArray.getInt(R.styleable.ClearEditText_decimalNumbers, DEFAULT_DECIMAL_NUMBER);
        boolean userFilter = typedArray.getBoolean(R.styleable.ClearEditText_userFilter, false);
        typedArray.recycle();
        init();
        if (userFilter){
            applyRule();
        }
        filterEmoji();
    }

    private void filterEmoji() {
        setFilters(new InputFilter[]{new InputFilter() {
            Pattern mPattern = Pattern.compile(Configs.INPUT_REGEX);
            @Override
            public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
                Matcher matcher = mPattern.matcher(source);
                if (matcher.find()){
                    return "";
                }
                return null;
            }
        }});
    }

    private void applyRule() {
        setFilters(new InputFilter[]{new InputFilter() {
            @Override
            public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
                String origin = dest.toString();
                if (origin.length() == 0){
                    if (source.equals(".")){
                        return "0.";
                    }
                }else if (origin.equals("0")){
                    if (!source.equals(".")){
                        return "";
                    }
                }else {
                    if (origin.contains(".")){
                        int index = origin.indexOf(".");
                        if (dend - index > mDecimalNumber){
                            return "";
                        }
                    }
                }

                return null;
            }
        }});
    }


    private void init() {
        //获取EditText的DrawableRight,假如没有设置我们就使用默认的图片
        mClearDrawable = getCompoundDrawables()[2];
        if (mClearDrawable == null) {
//          throw new NullPointerException("You can add drawableRight attribute in XML");
            mClearDrawable = getResources().getDrawable(R.mipmap.login_delete_icon);
        }

        mClearDrawable.setBounds(0, 0, mClearDrawable.getIntrinsicWidth(), mClearDrawable.getIntrinsicHeight());
        //默认设置隐藏图标
        setClearIconVisible(false);
        //设置焦点改变的监听
        setOnFocusChangeListener(this);
        //设置输入框里面内容发生改变的监听
        addTextChangedListener(this);

    }

    public void setOnDeleteClickListener(IDeleteClickListener listener){
        mDeleteClickListener = listener;
    }

    /**
     * 因为我们不能直接给EditText设置点击事件，所以我们用记住我们按下的位置来模拟点击事件
     * 当我们按下的位置 在  EditText的宽度 - 图标到控件右边的间距 - 图标的宽度  和
     * EditText的宽度 - 图标到控件右边的间距之间我们就算点击了图标，竖直方向就没有考虑
     */
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_UP) {
            if (getCompoundDrawables()[2] != null) {

                boolean touchable = event.getX() > (getWidth() - getTotalPaddingRight())
                        && (event.getX() < ((getWidth() - getPaddingRight())));

                if (touchable) {
                    this.setText("");
                    //删除按钮的点击事件
                    if (mDeleteClickListener != null){
                        mDeleteClickListener.onDelete();
                    }
                }
            }
        }

        return super.onTouchEvent(event);
    }

    /**
     * 当ClearEditText焦点发生变化的时候，判断里面字符串长度设置清除图标的显示与隐藏
     */
    @Override
    public void onFocusChange(View v, boolean hasFocus) {
        this.hasFocus = hasFocus;
        if (hasFocus) {
            setClearIconVisible(getText().length() > 0);
        } else {
            setClearIconVisible(false);
        }
    }


    /**
     * 设置清除图标的显示与隐藏，调用setCompoundDrawables为EditText绘制上去
     * @param visible
     */
    protected void setClearIconVisible(boolean visible) {
        Drawable right = visible ? mClearDrawable : null;
        setCompoundDrawables(getCompoundDrawables()[0],
                getCompoundDrawables()[1], right, getCompoundDrawables()[3]);
    }


    /**
     * 当输入框里面内容发生变化的时候回调的方法
     */
    @Override
    public void onTextChanged(CharSequence s, int start, int count,
                              int after) {
        if(hasFocus){
            setClearIconVisible(s.length() > 0);
        }
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count,
                                  int after) {

    }

    @Override
    public void afterTextChanged(Editable s) {

    }


    /**
     * 设置晃动动画
     */
    public void setShakeAnimation(){
        this.setAnimation(shakeAnimation(5));
    }


    /**
     * 晃动动画
     * @param counts 1秒钟晃动多少下
     * @return
     */
    public static Animation shakeAnimation(int counts){
        Animation translateAnimation = new TranslateAnimation(0, 10, 0, 0);
        translateAnimation.setInterpolator(new CycleInterpolator(counts));
        translateAnimation.setDuration(1000);
        return translateAnimation;
    }

    public interface IDeleteClickListener{
        void onDelete();
    }

}

