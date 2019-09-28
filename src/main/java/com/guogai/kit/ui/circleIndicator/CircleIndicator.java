package com.guogai.kit.ui.circleIndicator;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.View;

/**
 * 圆形指示器
 * Created by guogai on 2016/3/18.
 */
public class CircleIndicator extends View {
    private static final int DEFAULT_CIRCLE_SIZE = 4;
    private static final int DEFAULT_CIRCLE_INTERVAL = 4;
    private static final int DEFAULT_COLOR_STROKE = Color.WHITE;
    private static final int DEFAULT_COLOR_FILL = Color.WHITE;
    private static final int DEFAULT_CIRCLE_BORDER_SIZE = 1;
    private Paint mPaintStroke;
    private Paint mPaintFill;
    private int mCircleSize;
    private int mCircleInterval;
    private int mCurrentPage;
    private ViewPager mViewPager;

    public CircleIndicator(Context context) {
        super(context);
        init();
    }

    public CircleIndicator(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        mCircleSize = DEFAULT_CIRCLE_SIZE;
        mCircleInterval = DEFAULT_CIRCLE_INTERVAL;
        mPaintStroke = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaintStroke.setStyle(Paint.Style.STROKE);
        mPaintStroke.setStrokeWidth(DEFAULT_CIRCLE_BORDER_SIZE);
        mPaintStroke.setColor(DEFAULT_COLOR_STROKE);
        mPaintFill = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaintFill.setStyle(Paint.Style.FILL);
        mPaintFill.setColor(DEFAULT_COLOR_FILL);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int width = 0;
        int height = 0;
        int count = mViewPager.getAdapter().getCount();
        int widthSpecMode = MeasureSpec.getMode(widthMeasureSpec);
        int heightSpecMode = MeasureSpec.getMode(heightMeasureSpec);
        int widthSpecSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightSpecSize = MeasureSpec.getSize(heightMeasureSpec);
        switch (widthSpecMode) {
            case MeasureSpec.EXACTLY:
                width = widthSpecSize;
                break;
            case MeasureSpec.AT_MOST:
                width = getPaddingLeft() + getPaddingRight() + +(count * 2 * mCircleSize) + (count - 1) * mCircleInterval;
                width = Math.min(width, widthSpecSize);
                break;
        }
        switch (heightSpecMode) {
            case MeasureSpec.EXACTLY:
                height = heightSpecSize;
                break;
            case MeasureSpec.AT_MOST:
                height = 2 * mCircleSize + getPaddingTop() + getPaddingBottom();
                height = Math.min(height, heightSpecSize);
                break;
        }
        setMeasuredDimension(width, height);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (mViewPager == null) {
            return;
        }
        for (int i = 0; i < mViewPager.getAdapter().getCount(); i++) {
            canvas.drawCircle(getPaddingLeft() + mCircleSize + (i * (2 * mCircleSize + mCircleInterval)),
                    getPaddingTop() + mCircleSize,
                    mCircleSize,
                    mPaintStroke);
        }
        canvas.drawCircle(getPaddingLeft() + mCircleSize + (2 * mCircleSize + mCircleInterval) * mCurrentPage,
                getPaddingTop() + mCircleSize,
                mCircleSize,
                mPaintFill);
    }

    /**
     * 设置Target
     *
     * @param view
     */
    public void setTargetView(ViewPager view) {
        if (view != null) {
            mViewPager = view;
            mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
                @Override
                public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                }

                @Override
                public void onPageSelected(int position) {
                    setCurrentPage(position);
                }

                @Override
                public void onPageScrollStateChanged(int state) {
                }
            });
        }
    }

    /**
     * 设置圆点的颜色
     *
     * @param colorStroke
     * @param colorFill
     */
    public void setCircleColors(int colorStroke, int colorFill) {
        if (colorStroke != 0 && colorStroke != DEFAULT_COLOR_STROKE) {
            mPaintStroke.setColor(colorStroke);
        }
        if (colorFill != 0 && colorFill != DEFAULT_COLOR_FILL) {
            mPaintFill.setColor(colorFill);
        }
        invalidate();
    }

    /**
     * 设置圆点的大小
     *
     * @param circleSize
     */
    public void setCircleSize(int circleSize) {
        if (circleSize >= 0 && circleSize != DEFAULT_CIRCLE_SIZE) {
            mCircleSize = circleSize;
            invalidate();
        }
    }

    /**
     * 设置圆点的间距
     *
     * @param circleInterval
     */
    public void setCircleInterval(int circleInterval) {
        if (circleInterval >= 0 && circleInterval != DEFAULT_CIRCLE_INTERVAL) {
            mCircleInterval = circleInterval;
            invalidate();
        }
    }

    /**
     * 设置圆点的边框大小
     *
     * @param circleBorderSize
     */
    public void setCircleBorderSize(int circleBorderSize) {
        if (circleBorderSize > 0 && circleBorderSize != DEFAULT_CIRCLE_BORDER_SIZE) {
            mPaintStroke.setStrokeWidth(circleBorderSize);
            invalidate();
        }
    }

    /**
     * 设置当前的页面
     *
     * @param currentPage
     */
    private void setCurrentPage(int currentPage) {
        if (currentPage >= 0) {
            mCurrentPage = currentPage;
            invalidate();
        }
    }
}
