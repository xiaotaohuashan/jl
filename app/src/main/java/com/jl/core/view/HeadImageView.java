package com.jl.core.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.NinePatchDrawable;
import android.util.AttributeSet;
import android.widget.ImageView;

import com.jl.myapplication.R;

public class HeadImageView extends ImageView {
    private int _mBorderThickness = 0;
    private Context _mContext;
    private int _defaultColor = 0xFFFFFFFF;
    // 如果只有其中一个有值，则只画一个圆形边框
    private int _mBorderOutsideColor = 0;
    private int _mBorderInsideColor = 0;
    // 控件默认长、宽
    private int _defaultWidth = 0;
    private int _defaultHeight = 0;

    public HeadImageView(Context context) {
        super(context);
        _mContext = context;
    }

    public HeadImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        _mContext = context;
        setCustomAttributes(attrs);
    }

    public HeadImageView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        _mContext = context;
        setCustomAttributes(attrs);
    }

    private void setCustomAttributes(AttributeSet attrs) {
        TypedArray a = _mContext.obtainStyledAttributes(attrs, R.styleable.roundedimageview);
        _mBorderThickness = a.getDimensionPixelSize(R.styleable.roundedimageview_border_thickness, 0);
        _mBorderOutsideColor = a.getColor(R.styleable.roundedimageview_border_outside_color,_defaultColor);
        _mBorderInsideColor = a.getColor(R.styleable.roundedimageview_border_inside_color, _defaultColor);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        Drawable drawable = getDrawable() ;
        if (drawable == null) {
            return;
        }
        if (getWidth() == 0 || getHeight() == 0) {
            return;
        }
        this.measure(0, 0);
        if (drawable.getClass() == NinePatchDrawable.class)
            return;
        try {
            Bitmap b = ((BitmapDrawable) drawable).getBitmap();
            Bitmap bitmap = b.copy(Bitmap.Config.ARGB_8888, true);
            if (_defaultWidth == 0) {
                _defaultWidth = getWidth();
            }
            if (_defaultHeight == 0) {
                _defaultHeight = getHeight();
            }
            int radius = 0;
            if (_mBorderInsideColor != _defaultColor && _mBorderOutsideColor != _defaultColor) {// 定义画两个边框，分别为外圆边框和内圆边框
                radius = (_defaultWidth < _defaultHeight ? _defaultWidth : _defaultHeight) / 2 - 2 * _mBorderThickness;
                // 画内圆
                drawCircleBorder(canvas, radius + _mBorderThickness / 2,_mBorderInsideColor);
                // 画外圆
                drawCircleBorder(canvas, radius + _mBorderThickness + _mBorderThickness / 2, _mBorderOutsideColor);
            } else if (_mBorderInsideColor != _defaultColor && _mBorderOutsideColor == _defaultColor) {// 定义画一个边框
                radius = (_defaultWidth < _defaultHeight ? _defaultWidth : _defaultHeight) / 2 - _mBorderThickness;
                drawCircleBorder(canvas, radius + _mBorderThickness / 2, _mBorderInsideColor);
            } else if (_mBorderInsideColor == _defaultColor && _mBorderOutsideColor != _defaultColor) {// 定义画一个边框
                radius = (_defaultWidth < _defaultHeight ? _defaultWidth : _defaultHeight) / 2 - _mBorderThickness;
                drawCircleBorder(canvas, radius + _mBorderThickness / 2, _mBorderOutsideColor);
            } else {// 没有边框
                radius = (_defaultWidth < _defaultHeight ? _defaultWidth : _defaultHeight) / 2;
            }
            Bitmap roundBitmap = getCroppedRoundBitmap(bitmap, radius);
            canvas.drawBitmap(roundBitmap, _defaultWidth / 2 - radius, _defaultHeight / 2 - radius, null);
        } catch (Exception e) {

        }
    }

    /**
     * 获取裁剪后的圆形图片
     * @param
     */
    public Bitmap getCroppedRoundBitmap(Bitmap bmp, int radius) {
        Bitmap scaledSrcBmp;
        int diameter = radius * 2;
        // 为了防止宽高不相等，造成圆形图片变形，因此截取长方形中处于中间位置最大的正方形图片
        int bmpWidth = bmp.getWidth();
        int bmpHeight = bmp.getHeight();
        int squareWidth = 0, squareHeight = 0;
        int x = 0, y = 0;
        Bitmap squareBitmap;
        if (bmpHeight > bmpWidth) {// 高大于宽
            squareWidth = squareHeight = bmpWidth;
            x = 0;
            y = (bmpHeight - bmpWidth) / 2;
            // 截取正方形图片
            squareBitmap = Bitmap.createBitmap(bmp, x, y, squareWidth, squareHeight);
        } else if (bmpHeight < bmpWidth) {// 宽大于高
            squareWidth = squareHeight = bmpHeight;
            x = (bmpWidth - bmpHeight) / 2;
            y = 0;
            squareBitmap = Bitmap.createBitmap(bmp, x, y, squareWidth,squareHeight);
        } else {
            squareBitmap = bmp;
        }
        if (squareBitmap.getWidth() != diameter || squareBitmap.getHeight() != diameter) {
            scaledSrcBmp = Bitmap.createScaledBitmap(squareBitmap, diameter,diameter, true);
        } else {
            scaledSrcBmp = squareBitmap;
        }
        Bitmap output = Bitmap.createBitmap(scaledSrcBmp.getWidth(),
                scaledSrcBmp.getHeight(),
                Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(output);

        Paint paint = new Paint();
        Rect rect = new Rect(0, 0, scaledSrcBmp.getWidth(),scaledSrcBmp.getHeight());

        paint.setAntiAlias(true);
        paint.setFilterBitmap(true);
        paint.setDither(true);
        canvas.drawARGB(0, 0, 0, 0);
        canvas.drawCircle(scaledSrcBmp.getWidth() / 2,
                scaledSrcBmp.getHeight() / 2,
                scaledSrcBmp.getWidth() / 2,
                paint);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(scaledSrcBmp, rect, rect, paint);
        bmp = null;
        squareBitmap = null;
        scaledSrcBmp = null;
        return output;
    }

    /**
     * 边缘画圆
     */
    private void drawCircleBorder(Canvas canvas, int radius, int color) {
        Paint paint = new Paint();
            /* 去锯齿 */
        paint.setAntiAlias(true);
        paint.setFilterBitmap(true);
        paint.setDither(true);
        paint.setColor(color);
            /* 设置paint的　style　为STROKE：空心 */
        paint.setStyle(Paint.Style.STROKE);
            /* 设置paint的外框宽度 */
        paint.setStrokeWidth(_mBorderThickness);
        canvas.drawCircle(_defaultWidth / 2, _defaultHeight / 2, radius, paint);
    }
}
