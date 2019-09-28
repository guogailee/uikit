package com.guogai.kit.ui.segment;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;

import com.guogai.kit.R;

import java.util.ArrayList;
import java.util.List;

/**
 * SegmentControl:
 * 一、默认水平方向，垂直方向不符合设计前提
 * 二、几组相等关系：
 * mNormalTextColor=mItemSelectedBackgroundColor
 * mSelectedTextColor=mNormalBackgroundColor
 * 线的大小=边框线的大小==分割线的大小
 * 线的颜色=边框线的颜色=分割线的颜色=mItemSelectedBackgroundColor
 * <p/>
 * Created by guogai on 2015/12/21.
 */

public class SegmentControl extends View {
    private final static int DEFAULT_ITEM_TEXT_SIZE = 30;
    private final static int DEFAULT_CORNER_RADIUS = 0;
    private final static int DEFAULT_NORMAL_BACKGROUND_COLOR = Color.WHITE;
    private final static int DEFAULT_ITEM_SELECTED_BACKGROUND_COLOR = Color.BLUE;
    private final static int DEFAULT_LINE_SIZE = 3;
    private final static int DEFAULT_CURRENT_INDEX = 0;
    private final static int DEFAULT_MIN_HEIGHT = 60;
    private List<String> mListTexts;
    private int mItemNormalTextColor;
    private int mItemSelectedTextColor;
    private int mItemTextSize;
    private int mCornerRadius;
    private int mNormalBackgroundColor;
    private int mItemSelectedBackgroundColor;
    private int mLineSize;
    private int mLineColor;
    private int mCurrentIndex;
    private int mItemWidth;
    private int mItemHeight;
    private int mPointSize;
    private int mPointColor;
    private ArrayList<Integer> mListPoints;
    private Paint mPaint;
    private Rect[] mItemTextBounds;      //item文字实际的边界
    private Rect[] mItemTextDrawBounds;  //item文字绘制的边界
    private boolean mInTapRegion = false;   //是否当前手指仅在小范围内移动，当手指仅在小范围内移动时，视为手指未曾移动过
    private OnSegmentControlClickListener mOnSegmentControlClickListener;

    public SegmentControl(Context context) {
        super(context);
        init(context, null);
    }

    public SegmentControl(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    /**
     * 初始化
     *
     * @param context
     * @param attrs
     */
    private void init(Context context, AttributeSet attrs) {
        //设置默认值
        mNormalBackgroundColor = DEFAULT_NORMAL_BACKGROUND_COLOR;
        mItemSelectedBackgroundColor = DEFAULT_ITEM_SELECTED_BACKGROUND_COLOR;
        mItemNormalTextColor = DEFAULT_ITEM_SELECTED_BACKGROUND_COLOR;
        mItemSelectedTextColor = DEFAULT_NORMAL_BACKGROUND_COLOR;
        mItemTextSize = DEFAULT_ITEM_TEXT_SIZE;
        mCornerRadius = DEFAULT_CORNER_RADIUS;
        mLineSize = DEFAULT_LINE_SIZE;
        mLineColor = DEFAULT_ITEM_SELECTED_BACKGROUND_COLOR;
        mCurrentIndex = DEFAULT_CURRENT_INDEX;
        mListTexts = new ArrayList<>();
        mListPoints = new ArrayList<>();
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        initViews(context, attrs);
    }

    /**
     * 初始化Views
     *
     * @param context
     * @param attrs
     */
    private void initViews(Context context, AttributeSet attrs) {
        parseAttrs(context, attrs);
    }

    /**
     * 解析
     *
     * @param context
     * @param attrs
     */
    private void parseAttrs(Context context, AttributeSet attrs) {
        if (attrs != null) {
            TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.SegmentControl);
            //解析文字
            String itemText = a.getString(R.styleable.SegmentControl_segmentItemText);
            if (!TextUtils.isEmpty(itemText)) {
                String[] texts = itemText.split("\\|");
                int size = texts.length > 5 ? 5 : texts.length;
                mListTexts.clear();
                for (int i = 0; i < size; i++) {
                    mListTexts.add(texts[i]);
                }
            }
            //解析文字颜色
            int itemNormalTextColorRes = a.getResourceId(R.styleable.SegmentControl_segmentItemNormalTextColor, 0);
            if (itemNormalTextColorRes == 0) {
                int itemNormalTextColor = a.getColor(R.styleable.SegmentControl_segmentItemNormalTextColor, 0);
                if (itemNormalTextColor != 0) {
                    setItemNormalTextColor(itemNormalTextColor);
                }
            } else {
                setItemNormalTextColor(getResources().getColor(itemNormalTextColorRes));
            }
            int itemSelectedTextColorRes = a.getResourceId(R.styleable.SegmentControl_segmentItemSelectedTextColor, 0);
            if (itemSelectedTextColorRes == 0) {
                int itemSelectedTextColor = a.getColor(R.styleable.SegmentControl_segmentItemSelectedTextColor, 0);
                if (itemSelectedTextColor != 0) {
                    setItemSelectedTextColor(itemSelectedTextColor);
                }
            } else {
                setItemSelectedTextColor(getResources().getColor(itemSelectedTextColorRes));
            }
            //解析文字大小
            int itemTextSize = a.getDimensionPixelSize(R.styleable.SegmentControl_segmentItemTextSize, DEFAULT_ITEM_TEXT_SIZE);
            setItemTextSize(itemTextSize);
            //解析正常背景颜色
            int normalBackgroundColorRes = a.getResourceId(R.styleable.SegmentControl_segmentNormalBackgroundColor, 0);
            if (normalBackgroundColorRes == 0) {
                int normalBackgroundColor = a.getColor(R.styleable.SegmentControl_segmentNormalBackgroundColor, 0);
                if (normalBackgroundColor != 0) {
                    setNormalBackgroundColor(normalBackgroundColor);
                }
            } else {
                setNormalBackgroundColor(getResources().getColor(normalBackgroundColorRes));
            }
            //解析item选中背景颜色
            int itemSelectedBackgroundColorRes = a.getResourceId(R.styleable.SegmentControl_segmentItemSelectedBackgroundColor, 0);
            if (itemSelectedBackgroundColorRes == 0) {
                int itemSelectedBackgroundColor = a.getColor(R.styleable.SegmentControl_segmentItemSelectedBackgroundColor, 0);
                if (itemSelectedBackgroundColor != 0) {
                    setItemSelectedBackgroundColor(itemSelectedBackgroundColor);
                }
            } else {
                setItemSelectedBackgroundColor(getResources().getColor(itemSelectedBackgroundColorRes));
            }
            //解析边框圆角半径
            int cornerRadius = a.getDimensionPixelSize(R.styleable.SegmentControl_segmentCornerRadius, DEFAULT_CORNER_RADIUS);
            setCornerRadius(cornerRadius);
            //解析线（边框线||分割线）大小
            int lineSize = a.getDimensionPixelSize(R.styleable.SegmentControl_segmentLineSize, DEFAULT_LINE_SIZE);
            setLineSize(lineSize);
            int lineColorRes = a.getResourceId(R.styleable.SegmentControl_segmentLineColor, 0);
            if (lineColorRes == 0) {
                int lineColor = a.getColor(R.styleable.SegmentControl_segmentLineColor, 0);
                if (lineColor != 0) {
                    setLineColor(lineColor);
                }
            } else {
                setLineColor(lineColorRes);
            }
            //解析当前选中项
            int currentIndex = a.getInt(R.styleable.SegmentControl_segmentCurrentIndex, DEFAULT_CURRENT_INDEX);
            setCurrentIndex(currentIndex);
            a.recycle();
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);

        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);

        int width = 0;
        int height = 0;

        if (mListTexts != null && mListTexts.size() > 0) {
            if (mItemTextBounds == null || mItemTextBounds.length != mListTexts.size()) {
                mItemTextBounds = new Rect[mListTexts.size()];
            }
            if (mItemTextDrawBounds == null || mItemTextDrawBounds.length != mListTexts.size()) {
                mItemTextDrawBounds = new Rect[mListTexts.size()];
            }
            //计算item文字的宽和高
            for (int i = 0; i < mListTexts.size(); i++) {
                String text = mListTexts.get(i);
                if (text != null) {

                    if (mItemTextBounds[i] == null)
                        mItemTextBounds[i] = new Rect();
                    mPaint.getTextBounds(text, 0, text.length(), mItemTextBounds[i]);

                    if (mItemWidth < mItemTextBounds[i].width())
                        mItemWidth = mItemTextBounds[i].width();
                    if (mItemHeight < mItemTextBounds[i].height())
                        mItemHeight = mItemTextBounds[i].height();
                }
            }
            //计算绘制文字的范围
            for (int i = 0; i < mListTexts.size(); i++) {
                if (mItemTextDrawBounds[i] == null)
                    mItemTextDrawBounds[i] = new Rect();
                mItemTextDrawBounds[i].left = i * mItemWidth;
                mItemTextDrawBounds[i].top = 0;
                mItemTextDrawBounds[i].right = mItemTextDrawBounds[i].left + mItemWidth;
                mItemTextDrawBounds[i].bottom = mItemTextDrawBounds[i].top + mItemHeight;
            }
            //计算实际的width
            switch (widthMode) {
                case MeasureSpec.AT_MOST:
                    if (widthSize <= (mItemWidth * mListTexts.size() + mLineSize * (mListTexts.size() - 1))) {
                        width = widthSize;
                        mItemWidth = widthSize / mListTexts.size();
                    } else {
                        width = (mItemWidth * mListTexts.size() + mLineSize * (mListTexts.size() - 1));
                    }
                    break;
                case MeasureSpec.EXACTLY:
                    width = widthSize;
                    mItemWidth = widthSize / mListTexts.size();
                    break;
                case MeasureSpec.UNSPECIFIED:
                    width = mItemWidth * mListTexts.size() + mLineSize * (mListTexts.size() - 1);
                    break;
            }
            //计算实际的height
            switch (heightMode) {
                case MeasureSpec.AT_MOST:
                    mItemHeight = mItemHeight <= DEFAULT_MIN_HEIGHT ? DEFAULT_MIN_HEIGHT : mItemHeight;
                    height = heightSize <= mItemHeight ? heightSize : mItemHeight;
                    break;
                case MeasureSpec.EXACTLY:
                    height = heightSize;
                    mItemHeight = heightSize;
                    break;
                case MeasureSpec.UNSPECIFIED:
                    height = heightSize <= mItemHeight ? heightSize : mItemHeight;
                    break;
            }
        } else {
            width = widthMode == MeasureSpec.UNSPECIFIED ? 0 : widthSize;
            height = heightMode == MeasureSpec.UNSPECIFIED ? 0 : heightSize;
        }

        setMeasuredDimension(width, height);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        //初始化SegmentControl正常背景
        RadiusDrawable normalBackgroundDrawable = new RadiusDrawable(mCornerRadius, true, mNormalBackgroundColor);
        normalBackgroundDrawable.setStrokeWidth(mLineSize);
        normalBackgroundDrawable.setStrokeColor(mLineColor);
        //1.画正常背景
        Rect rect = new Rect();
        rect.left = 0;
        rect.top = 0;
        rect.right = getMeasuredWidth();
        rect.bottom = getMeasuredHeight();
        normalBackgroundDrawable.setBounds(rect);
        normalBackgroundDrawable.draw(canvas);
        //初始化item选中背景
        RadiusDrawable itemSelectedBackgroundDrawable = new RadiusDrawable(false, mItemSelectedBackgroundColor);
        if (mListTexts != null && mListTexts.size() > 0) {
            for (int i = 0; i < mListTexts.size(); i++) {
                //2.画分割线
                if (i < mListTexts.size() - 1) {
                    mPaint.setColor(mLineColor);
                    mPaint.setStrokeWidth(mLineSize);
                    canvas.drawLine(mItemTextDrawBounds[i].right, 0, mItemTextDrawBounds[i].right, getHeight(), mPaint);
                }

                //3.画item选中背景
                if (i == mCurrentIndex && itemSelectedBackgroundDrawable != null) {
                    int topLeftRadius = 0;
                    int topRightRadius = 0;
                    int bottomLeftRadius = 0;
                    int bottomRightRadius = 0;

                    if (i == 0) {
                        topLeftRadius = mCornerRadius;
                        bottomLeftRadius = mCornerRadius;
                    } else if (i == mListTexts.size() - 1) {
                        topRightRadius = mCornerRadius;
                        bottomRightRadius = mCornerRadius;
                    }
                    itemSelectedBackgroundDrawable.setRadiuses(topLeftRadius, topRightRadius, bottomLeftRadius, bottomRightRadius);
                    itemSelectedBackgroundDrawable.setBounds(mItemTextDrawBounds[i]);
                    itemSelectedBackgroundDrawable.draw(canvas);

                    //选中，设置画笔颜色为选中文字颜色
                    mPaint.setColor(mItemSelectedTextColor);
                } else {
                    //未选中，设置画笔颜色为正常文字颜色
                    mPaint.setColor(mItemNormalTextColor);
                }

                //4.画文字
                mPaint.setTextSize(mItemTextSize);
                mPaint.setTextAlign(Paint.Align.CENTER);
                canvas.drawText(mListTexts.get(i), mItemTextDrawBounds[i].left + mItemWidth / 2, (mItemHeight - mPaint.ascent() - mPaint.descent()) / 2, mPaint);

                //5.画点
                for (int j = 0; j < mListPoints.size(); j++) {
                    if (i == mListPoints.get(j)) {
                        float x = mItemTextDrawBounds[i].left + mItemWidth / 2 + mItemTextBounds[i].width() / 2 + 2 * mPointSize;
                        float y = (mItemHeight + mPaint.ascent()) / 2;
                        mPaint.setColor(mPointColor);
                        canvas.drawCircle(x, y, mPointSize, mPaint);
                    }
                }
            }
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float mStartX = event.getX();
        float mStartY = event.getY();
        float mCurrentX;
        float mCurrentY;
        //为touch的两个不同手指设定的一段距离限制，当距离超过这段才认为事件为move--For Better experience
        int mTouchSlop;
        int touchSlop;
        if (getContext() != null) {
            touchSlop = ViewConfiguration.getTouchSlop();
        } else {
            final ViewConfiguration configuration = ViewConfiguration.get(getContext());
            touchSlop = configuration.getScaledTouchSlop();
        }
        mTouchSlop = touchSlop * touchSlop;
        switch (event.getAction() & MotionEvent.ACTION_MASK) {
            case MotionEvent.ACTION_DOWN:
                mInTapRegion = true;
                break;
            case MotionEvent.ACTION_MOVE:
                mCurrentX = event.getX();
                mCurrentY = event.getY();

                int dx = (int) (mCurrentX - mStartX);
                int dy = (int) (mCurrentY - mStartY);
                int distance = dx * dx + dy * dy;

                if (distance > mTouchSlop) {
                    mInTapRegion = false;
                }
                break;
            case MotionEvent.ACTION_UP:
                if (mInTapRegion) {
                    int index;//默认0
                    index = (int) (mStartX / mItemWidth);

                    if (mCurrentIndex != index) {
                        if (mOnSegmentControlClickListener != null) {
                            mOnSegmentControlClickListener.onSegmentControlClick(index);
                        }
                    }
                    mCurrentIndex = index;
                    invalidate();
                }
                break;
        }
        return true;//返回true代表onTouch执行成功
    }

    /**
     * 设置item文字
     *
     * @param s1
     */
    public void setItemTexts(String s1) {
        setItemText(s1, null);
    }

    /**
     * 设置item文字
     *
     * @param s1
     * @param s2
     */
    public void setItemText(String s1, String s2) {
        setItemText(s1, s2, null);
    }

    /**
     * 设置item文字
     *
     * @param s1
     * @param s2
     * @param s3
     */
    public void setItemText(String s1, String s2, String s3) {
        setItemText(s1, s2, s3, null);
    }

    /**
     * 设置item文字
     *
     * @param s1
     * @param s2
     * @param s3
     * @param s4
     */
    public void setItemText(String s1, String s2, String s3, String s4) {
        setItemText(s1, s2, s3, s4, null);
    }

    /**
     * 设置item文字
     *
     * @param s1
     * @param s2
     * @param s3
     * @param s4
     * @param s5
     */
    public void setItemText(String s1, String s2, String s3, String s4, String s5) {
        mListTexts.clear();
        if (s1 != null) {
            mListTexts.add(s1);
        }
        if (s2 != null) {
            mListTexts.add(s2);
        }
        if (s3 != null) {
            mListTexts.add(s3);
        }
        if (s4 != null) {
            mListTexts.add(s4);
        }
        if (s5 != null) {
            mListTexts.add(s5);
        }
    }

    /**
     * 设置item正常文字颜色
     *
     * @param color
     */
    public void setItemNormalTextColor(int color) {
        mItemNormalTextColor = color;
    }

    /**
     * 设置item选中文字颜色
     *
     * @param color
     */
    public void setItemSelectedTextColor(int color) {
        mItemSelectedTextColor = color;
    }

    /**
     * 设置item文字大小
     *
     * @param size
     */
    public void setItemTextSize(int size) {
        if (size != -1) {
            mItemTextSize = size;
        }
    }

    /**
     * 设置正常背景颜色
     *
     * @param color
     */
    public void setNormalBackgroundColor(int color) {
        if (mItemSelectedTextColor == DEFAULT_NORMAL_BACKGROUND_COLOR) {
            mItemSelectedTextColor = color;
        }
        mNormalBackgroundColor = color;
    }

    /**
     * 设置item选中背景颜色
     *
     * @param color
     */
    public void setItemSelectedBackgroundColor(int color) {
        if (mLineColor == DEFAULT_ITEM_SELECTED_BACKGROUND_COLOR) {
            mLineColor = color;
        }
        if (mItemNormalTextColor == DEFAULT_ITEM_SELECTED_BACKGROUND_COLOR) {
            mItemNormalTextColor = color;
        }
        mItemSelectedBackgroundColor = color;
    }

    /**
     * 设置边框圆角半径
     *
     * @param radius
     */
    public void setCornerRadius(int radius) {
        if (radius != -1) {
            mCornerRadius = radius;
        }
    }

    /**
     * 设置当前选中项
     *
     * @param index
     */
    public void setCurrentIndex(int index) {
        if (index != -1) {
            mCurrentIndex = index;
        }
    }

    /**
     * 设置线（边框线||分割线）的大小
     *
     * @param size
     */
    public void setLineSize(int size) {
        if (size != -1) {
            mLineSize = size;
        }
    }

    /**
     * 设置线（边框线||分割线）的颜色
     *
     * @param color
     */
    public void setLineColor(int color) {
        mLineColor = color;
    }


    /**
     * 设置点的样式
     *
     * @param size
     * @param color
     */
    public void setPointStyle(int size, int color) {
        if (size != -1 && color != 0) {
            mPointSize = size;
            mPointColor = color;
        }
    }

    /**
     * 显示点
     *
     * @param point
     */
    public void showPoint(int point) {
        mListPoints.add(point);
        invalidate();
    }

    /**
     * 隐藏点
     *
     * @param point
     */
    public void hidePoint(int point) {
        for (int j = 0; j < mListPoints.size(); j++) {
            if (mListPoints.get(j) == point) {
                mListPoints.remove(j);
                break;
            }
        }
        invalidate();
    }

    /**
     * 注册点击listener
     *
     * @param listener
     */
    public void setOnSegmentControlClickListener(OnSegmentControlClickListener listener) {
        if (listener != null) {
            mOnSegmentControlClickListener = listener;
        }
    }

    /**
     * 定义点击listener
     */
    public interface OnSegmentControlClickListener {
        void onSegmentControlClick(int index);
    }
}