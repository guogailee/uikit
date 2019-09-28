package com.guogai.kit.ui.segment;

import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PixelFormat;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;

/**
 * 圆角Drawable
 * (使用这个的目的是解决android 2.3系统中drawable下的bug:
 * 一、bottomRightRadius和bottomLeftRadius是掉转的;
 * http://www.2cto.com/kf/201408/322479.html
 * 二、Android2.3上的一个BUG，有时候加载第一个资源的时候会返回ColorDrawable，而不是BitmapDrawable
 * http://blog.csdn.net/leehong2005/article/details/9127463)
 * Created by guogai on 2015/12/21.
 */

public class RadiusDrawable extends Drawable {
    private int mLeft;
    private int mTop;
    private int mRight;
    private int mBottom;
    private int mTopLeftRadius;
    private int mTopRightRadius;
    private int mBottomLeftRadius;
    private int mBottomRightRadius;
    private int mStrokeWidth;
    private int mStrokeColor;
    private int mRadiusDrawableColor;
    private Path mPath;
    private Paint mPaint;
    private boolean mIsStroke;

    public RadiusDrawable(boolean isStroke, int radiusDrawableColor) {
        mIsStroke = isStroke;
        mRadiusDrawableColor = radiusDrawableColor;
        init();
    }

    public RadiusDrawable(int radius, boolean isStroke, int radiusDrawableColor) {
        mTopLeftRadius = radius;
        mTopRightRadius = radius;
        mBottomLeftRadius = radius;
        mBottomRightRadius = radius;
        mIsStroke = isStroke;
        mRadiusDrawableColor = radiusDrawableColor;
        init();
    }

    public RadiusDrawable(int topLeftRadius, int topRightRadius, int bottomLeftRadius, int bottomRightRadius, boolean isStroke, int radiusDrawableColor) {
        mTopLeftRadius = topLeftRadius;
        mTopRightRadius = topRightRadius;
        mBottomLeftRadius = bottomLeftRadius;
        mBottomRightRadius = bottomRightRadius;
        mIsStroke = isStroke;
        mRadiusDrawableColor = radiusDrawableColor;
        init();
    }

    private void init() {
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    }

    @Override
    public void setBounds(int left, int top, int right, int bottom) {
        super.setBounds(left, top, right, bottom);
        mLeft = left;
        mTop = top;
        mRight = right;
        mBottom = bottom;
        if (mIsStroke) {
            int halfStrokeWidth = mStrokeWidth / 2;
            mLeft += halfStrokeWidth;
            mTop += halfStrokeWidth;
            mRight -= halfStrokeWidth;
            mBottom -= halfStrokeWidth;
        }
        mPath = new Path();
        /**画路径*/
        //路径开始点左上角
        mPath.moveTo(mLeft + mTopLeftRadius, mTop);
        //路径结束点右上角
        mPath.lineTo(mRight - mTopRightRadius, mTop);
        mPath.arcTo(new RectF(mRight - mTopRightRadius * 2, mTop, mRight, mTop + mTopRightRadius * 2), -90, 90);
        //下一个路径结束点右下角
        mPath.lineTo(mRight, mBottom - mBottomRightRadius);
        mPath.arcTo(new RectF(mRight - mBottomRightRadius * 2, mBottom - mBottomRightRadius * 2, mRight, mBottom), 0, 90);
        //下一个路径结束点左下角
        mPath.lineTo(mLeft + mBottomLeftRadius, mBottom);
        mPath.arcTo(new RectF(mLeft, mBottom - mBottomLeftRadius * 2, mLeft + mBottomLeftRadius * 2, mBottom), 90, 90);
        //下一个路径结束点左上角
        mPath.lineTo(mLeft, mTop + mTopLeftRadius);
        mPath.arcTo(new RectF(mLeft, mTop, mLeft + mTopLeftRadius * 2, mTop + mTopLeftRadius * 2), 180, 90);
        mPath.close();

    }

    @Override
    public void draw(Canvas canvas) {
        //画radiusDrawable
        if (mRadiusDrawableColor != 0) {
            mPaint.setColor(mRadiusDrawableColor);
            mPaint.setStyle(Paint.Style.FILL);
            canvas.drawPath(mPath, mPaint);
        }
        //画边框
        if (mStrokeWidth > 0) {
            mPaint.setColor(mStrokeColor);
            mPaint.setStyle(Paint.Style.STROKE);
            mPaint.setStrokeJoin(Paint.Join.MITER);
            mPaint.setStrokeWidth(mStrokeWidth);
            canvas.drawPath(mPath, mPaint);
        }
    }

    @Override
    public void setAlpha(int alpha) {

    }

    @Override
    public void setColorFilter(ColorFilter colorFilter) {

    }

    @Override
    public int getOpacity() {
        return PixelFormat.TRANSLUCENT;
    }

    /**
     * 设置画笔的宽度
     *
     * @param width
     */
    public void setStrokeWidth(int width) {
        mStrokeWidth = width;
        setBounds(mLeft, mTop, mRight, mBottom);
    }

    /**
     * 设置画笔的颜色
     *
     * @param color
     */
    public void setStrokeColor(int color) {
        mStrokeColor = color;
    }

    /**
     * 设置radiusDrawable的颜色
     *
     * @param color
     */
    public void setRadiusDrawableColor(int color) {
        mRadiusDrawableColor = color;
    }

    /**
     * 设置圆角半径的大小
     *
     * @param radius
     */
    public void setRadius(int radius) {
        mTopLeftRadius = radius;
        mTopRightRadius = radius;
        mBottomLeftRadius = radius;
        mBottomRightRadius = radius;
    }

    /**
     * 设置四个角的圆角半径
     *
     * @param topLeftRadius
     * @param topRightRadius
     * @param bottomLeftRadius
     * @param bottomRightRadius
     */
    public void setRadiuses(int topLeftRadius, int topRightRadius, int bottomLeftRadius, int bottomRightRadius) {
        mTopLeftRadius = topLeftRadius;
        mTopRightRadius = topRightRadius;
        mBottomLeftRadius = bottomLeftRadius;
        mBottomRightRadius = bottomRightRadius;
    }
}