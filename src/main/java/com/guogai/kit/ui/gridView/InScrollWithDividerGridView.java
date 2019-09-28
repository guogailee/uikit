package com.guogai.kit.ui.gridView;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.GridView;
import android.widget.ListAdapter;

/**
 * 1.带分割线的GirdView(只适合Child大小一样的情况)
 * 2.解决嵌套ScrollView以后遇到的一些问题
 * Created by guogai on 2016/2/19.
 */

public class InScrollWithDividerGridView extends GridView {
    private boolean mEnableScroll;
    private int mDividerSize;
    private int mDividerColor;
    private Paint mLinePaint;
    private boolean checkFixedHeight = true;
    private int mRow, mColumn;
    private int mItemWidth, mItemHeight;
    private int mFirstItemX, mFirstItemY;

    public InScrollWithDividerGridView(Context context) {
        super(context);
        init();
    }

    public InScrollWithDividerGridView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public InScrollWithDividerGridView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        //解决GridView嵌套在ScrollView里面,ScrollView展示不正确问题
        setFocusable(false);
        mLinePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mLinePaint.setStyle(Paint.Style.STROKE);
    }


    @Override
    protected void dispatchDraw(Canvas canvas) {
        super.dispatchDraw(canvas);
        if (getCount() == 0) {
            return;
        }
        //解决ScrollView嵌套GridView后显示不完整问题
        if (checkFixedHeight) {
            View firstItem = getChildAt(0);
            mColumn = getWidth() / firstItem.getWidth();
            mItemWidth = firstItem.getMeasuredWidth();
            mItemHeight = firstItem.getMeasuredHeight();
            mFirstItemX = getPaddingLeft();
            mFirstItemY = getPaddingTop();
            checkFixedHeight = false;
        }
        //计算行数--跟Adapter有关
        mRow = getAdapter().getCount() / mColumn + (getAdapter().getCount() % mColumn == 0 ? 0 : 1);
        getLayoutParams().height = mItemHeight * mRow + mDividerSize;
        requestLayout();

        if (mDividerSize > 0) {
            //画水平线
            for (int i = 0; i <= mRow; i++) {
                canvas.drawLine(mFirstItemX, mFirstItemY + mItemHeight * i + mDividerSize / 2, getWidth() - getPaddingRight(), getPaddingTop() + mItemHeight * i + mDividerSize / 2, mLinePaint);
            }
            //画垂直线
            for (int i = 0; i < mColumn - 1; i++) {
                canvas.drawLine(mFirstItemX + mItemWidth * (i + 1), getPaddingTop(), getPaddingLeft() + mItemWidth * (i + 1), getPaddingTop() + mItemHeight * mRow, mLinePaint);
            }
        }
    }

    @Override
    public void setAdapter(ListAdapter adapter) {
        super.setAdapter(adapter);
        checkFixedHeight = true;
    }

    //禁止GridView滚动解决方案
    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (ev.getAction() == MotionEvent.ACTION_MOVE) {
            if (mEnableScroll) {
                return true;
            }
        }
        return super.dispatchTouchEvent(ev);
    }

    /**
     * 设置GridView滚动与否
     *
     * @param isScroll
     */
    public void setEnableScroll(boolean isScroll) {
        mEnableScroll = !isScroll;
    }

    /**
     * 设置Divider
     *
     * @param dividerSize
     * @param dividerColor
     */
    public void setDivider(int dividerSize, int dividerColor) {
        if (dividerSize >= 0) {
            mDividerSize = dividerSize;
        }
        if (dividerColor != 0) {
            mDividerColor = dividerColor;
        }
        //init时候不生效，invalidate不生效(坑)
        mLinePaint.setColor(mDividerColor);
        mLinePaint.setStrokeWidth(mDividerSize);
    }
}
