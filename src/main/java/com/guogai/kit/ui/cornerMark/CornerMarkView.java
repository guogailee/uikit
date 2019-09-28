package com.guogai.kit.ui.cornerMark;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.os.Build;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;
import android.widget.TextView;

import com.guogai.kit.R;


/**
 * Created by guogai on 2016/2/22.
 */


public class CornerMarkView extends TextView {

    public static final byte LEFT_TOP = 0x00;

    public static final byte RIGHT_TOP = 0x01;

    public static final byte LEFT_BOTTOM = 0x02;

    public static final byte RIGHT_BOTTOM = 0x03;

    private static final float THE_SQUARE_ROOT_OF_2 = (float) Math.sqrt(2);

    private static final int DEFAULT_MARK_WIDTH = 18;

    private static final int DEFAULT_CORNER_DISTANCE = 25;

    private static final int DEFAULT_MARK_BACKGROUND_COLOR = 0x9F27CDC0;

    private static final int DEFAULT_MARK_TEXT_SIZE = 15;

    private static final int DEFAULT_MARK_TEXT_COLOR = 0xFFFFFFFF;

    private int mMarkBackgroundColor;

    private Path mPath;

    private Paint mPaint;

    private String mMarkText;

    private int mMarkTextSize;

    private Paint mTextPaint;

    private Rect mMarkTextBound;

    private int mMarkTextColor;

    private float mDensity;

    private int mMarkLocation;

    private MyPoint startPoint;

    private MyPoint endPoint;

    private boolean mMarkVisibility;

    public CornerMarkView(Context context) {
        this(context, null);
    }

    public CornerMarkView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CornerMarkView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs, defStyleAttr);
        mDensity = context.getResources().getDisplayMetrics().density;
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.CornerMarkView, defStyleAttr, 0);
        mMarkLocation = a.getInteger(R.styleable.CornerMarkView_mark_location, 0);
        mMarkBackgroundColor = a.getColor(R.styleable.CornerMarkView_mark_background_color, DEFAULT_MARK_BACKGROUND_COLOR);
        mMarkText = a.getString(R.styleable.CornerMarkView_mark_text);
        mMarkTextSize = a.getDimensionPixelSize(R.styleable.CornerMarkView_mark_textSize, dip2px(DEFAULT_MARK_TEXT_SIZE));
        mMarkTextColor = a.getColor(R.styleable.CornerMarkView_mark_textColor, DEFAULT_MARK_TEXT_COLOR);
        mMarkVisibility = a.getBoolean(R.styleable.CornerMarkView_mark_visibility, true);
        a.recycle();
        if (TextUtils.isEmpty(mMarkText)) mMarkText = "";
        mPaint = new Paint();
        mPath = new Path();
        mTextPaint = new Paint();
        mMarkTextBound = new Rect();
        startPoint = new MyPoint();
        endPoint = new MyPoint();
    }

    private void init(Context context, AttributeSet attrs, int defStyleAttr) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB && Build.VERSION.SDK_INT <= Build.VERSION_CODES.KITKAT) {
            setLayerType(View.LAYER_TYPE_SOFTWARE, null);
        }
    }

    public void setMarkTextSize(int textSize) {
        this.mMarkTextSize = dip2px(textSize);
        invalidate();
    }

    public void setMarkTextColor(int markTextColor) {
        if (this.mMarkTextColor == markTextColor) return;
        this.mMarkTextColor = markTextColor;
        invalidate();
    }

    public void setMarkText(String markText) {
        if (markText.equals(this.mMarkText)) return;
        this.mMarkText = markText;
        invalidate();
    }

    public void setMarkBackgroundColor(int markBackgroundColor) {
        if (this.mMarkBackgroundColor == markBackgroundColor) return;
        this.mMarkBackgroundColor = markBackgroundColor;
        invalidate();
    }


    public void setMarkLocation(int markLocation) {
        if (markLocation == this.mMarkLocation) return;
        this.mMarkLocation = markLocation;
        invalidate();
    }

    public void setMarkVisibility(boolean markVisibility) {
        if (this.mMarkVisibility == markVisibility) return;
        this.mMarkVisibility = markVisibility;
        invalidate();
    }

    @Override
    protected void onDraw(@SuppressWarnings("NullableProblems") Canvas mCanvas) {
        if (dip2px(DEFAULT_MARK_WIDTH) > 0 && mMarkVisibility) {
            float rDistance = dip2px(DEFAULT_CORNER_DISTANCE) + dip2px(DEFAULT_MARK_WIDTH) / 2;
            chooseMarkLocation(rDistance);
            //对文字长度的处理
            if (mMarkText.length() > 4) {
                mMarkText = mMarkText.substring(0, 4) + "..";
            }
            mTextPaint.setTextSize(mMarkTextSize);
            mTextPaint.getTextBounds(mMarkText, 0, mMarkText.length(), mMarkTextBound);
            mPaint.setDither(true);
            mPaint.setAntiAlias(true);
            mPaint.setColor(mMarkBackgroundColor);
            mPaint.setStyle(Paint.Style.STROKE);
            mPaint.setStrokeJoin(Paint.Join.ROUND);
            mPaint.setStrokeCap(Paint.Cap.SQUARE);
            mPaint.setStrokeWidth(dip2px(DEFAULT_MARK_WIDTH));
            mPath.reset();
            mPath.moveTo(startPoint.x, startPoint.y);
            mPath.lineTo(endPoint.x, endPoint.y);
            mCanvas.drawPath(mPath, mPaint);
            mTextPaint.setColor(mMarkTextColor);
            mTextPaint.setTextSize(mMarkTextSize);
            mTextPaint.setAntiAlias(true);
            //斜边长度
            float hypotenuse = THE_SQUARE_ROOT_OF_2 * rDistance;
            mCanvas.drawTextOnPath(mMarkText, mPath, hypotenuse / 2 - mMarkTextBound.width() / 2,
                    mMarkTextBound.height() / 2 - 2, mTextPaint);
        }
    }

    private void chooseMarkLocation(float rDistance) {
        int mWidth = getMeasuredWidth();
        int mHeight = getMeasuredHeight();
        switch (mMarkLocation) {
            case 0:
                startPoint.x = 0;
                startPoint.y = rDistance;
                endPoint.x = rDistance;
                endPoint.y = 0;
                break;
            case 1:
                startPoint.x = mWidth - rDistance;
                startPoint.y = 0;
                endPoint.x = mWidth;
                endPoint.y = rDistance;
                break;
            case 2:
                startPoint.x = 0;
                startPoint.y = mHeight - rDistance;
                endPoint.x = rDistance;
                endPoint.y = mHeight;
                break;
            case 3:
                startPoint.x = mWidth - rDistance;
                startPoint.y = mHeight;
                endPoint.x = mWidth;
                endPoint.y = mHeight - rDistance;
                break;
        }
    }

    private int dip2px(int dip) {
        return (int) (mDensity * dip + 0.5f);
    }

    private int px2dip(float px) {
        return (int) (px / mDensity + 0.5f);
    }

    static class MyPoint {
        float x;
        float y;
    }
}
