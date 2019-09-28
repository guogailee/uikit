package com.guogai.kit.ui.spinner;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.os.Build;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.guogai.kit.R;

import java.util.ArrayList;

/**
 * 自定义Spinner(有bug,嵌套ListView后)
 * Created by guogai on 2015/12/22.
 */
public class SpinnerView extends LinearLayout {
    private static final int DEFAULT_ARROW_WIDTH = 30;
    private static final int DEFAULT_ARROW_HEIGHT = 30;
    private ImageView mIvArrow;
    private TextView mTvTitle;
    private PopupWindow mPopupWindow;
    private LinearLayout mLayoutContent;
    private ListView mListView;
    private BaseSpinnerListAdapter mListAdapter;
    private Animation mAnimFromDownToUp;
    private Animation mAnimFromUpToDown;
    private boolean mArrowAnimFlag;
    private ArrayList<String> mListOriginalData;
    private ArrayList<String> mListCacheData;
    private OnSpinnerItemClickListener mOnSpinnerItemClickListener;
    private Context mContext;

    public SpinnerView(Context context) {
        super(context);
        init(context, null);
    }

    public SpinnerView(Context context, AttributeSet attrs) {
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
        mContext = context;
        mListOriginalData = new ArrayList<>();
        mListCacheData = new ArrayList<>();
        initAnim();
        initViews(attrs);
    }

    /**
     * 初始化Anim
     */
    private void initAnim() {
        mArrowAnimFlag = true;
        //从下旋转到上
        mAnimFromDownToUp = new RotateAnimation(0f, 180f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        mAnimFromDownToUp.setDuration(0);
        mAnimFromDownToUp.setFillAfter(true);
        //从上旋转到下
        mAnimFromUpToDown = new RotateAnimation(180f, 0f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        mAnimFromUpToDown.setDuration(0);
        mAnimFromUpToDown.setFillAfter(true);
    }

    /**
     * 初始化Views
     *
     * @param attrs
     */
    private void initViews(AttributeSet attrs) {
        //设置SpinnerView的默认方向
        setOrientation(HORIZONTAL);
        //设置SpinnerView垂直居中
        setGravity(Gravity.CENTER_VERTICAL);
        //实例化Title
        mTvTitle = new TextView(mContext);
        mTvTitle.setGravity(Gravity.CENTER);
        addView(mTvTitle);
        //实例化Arrow
        mIvArrow = new ImageView(mContext);
        LayoutParams layoutParams = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        layoutParams.setMargins(10, 0, 0, 0);
        mIvArrow.setLayoutParams(layoutParams);
        addView(mIvArrow);
        //实例化mLayoutContent--用于盛装ListView
        mLayoutContent = new LinearLayout(mContext);
        mLayoutContent.setGravity(Gravity.CENTER_HORIZONTAL);
        mLayoutContent.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
        mLayoutContent.setOnClickListener(onClickListener);
        //实例化ListView
        mListView = new ListView(mContext);
        mListView.setDivider(null);
        mListView.setOnItemClickListener(onItemClickListener);
        mListView.setBackgroundColor(Color.WHITE);
        mListView.setLayoutParams(new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        mLayoutContent.addView(mListView);
        //实例化PopupWindow
        mPopupWindow = new PopupWindow(mLayoutContent, LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
        //解决2.3以下ListView item点击不生效
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB) {
            mPopupWindow.setFocusable(true);
        }
        //设置点击监听器
        setOnClickListener(onClickListener);
        //解析
        parseAttrs(attrs);
    }

    /**
     * 解析
     *
     * @param attrs
     */
    private void parseAttrs(AttributeSet attrs) {
        if (attrs != null) {
            TypedArray typeArray = mContext.obtainStyledAttributes(attrs, R.styleable.SpinnerView);
            //解析ListView的宽高
            int spinnerListViewWidth = typeArray.getDimensionPixelOffset(R.styleable.SpinnerView_spinnerListViewWidth, 0);
            int spinnerListViewHeight = typeArray.getDimensionPixelOffset(R.styleable.SpinnerView_spinnerListViewHeight, 0);
            setListViewSize(spinnerListViewWidth, spinnerListViewHeight);
            //解析Title的文字大小
            int spinnerTitleTextSize = typeArray.getDimensionPixelOffset(R.styleable.SpinnerView_spinnerTitleTextSize, 0);
            setTitleTextSize(spinnerTitleTextSize);
            //解析Title的文字颜色
            int spinnerTitleTextColorRes = typeArray.getResourceId(R.styleable.SpinnerView_spinnerTitleTextColor, 0);
            if (spinnerTitleTextColorRes == 0) {
                int spinnerTitleTextColor = typeArray.getColor(R.styleable.SpinnerView_spinnerTitleTextColor, 0);
                setTitleTextColor(spinnerTitleTextColor);
            } else {
                setTitleTextColor(spinnerTitleTextColorRes);
            }
            //解析Arrow的宽高
            int spinnerArrowWidth = typeArray.getDimensionPixelOffset(R.styleable.SpinnerView_spinnerArrowWidth, DEFAULT_ARROW_WIDTH);
            int spinnerArrowHeight = typeArray.getDimensionPixelOffset(R.styleable.SpinnerView_spinnerArrowHeight, DEFAULT_ARROW_HEIGHT);
            setArrowSize(spinnerArrowWidth, spinnerArrowHeight);
            //解析Arrow的背景图片
            int spinnerIconImageRes = typeArray.getResourceId(R.styleable.SpinnerView_spinnerArrowImage, 0);
            setArrowImageResource(spinnerIconImageRes);
            typeArray.recycle();
        }
    }

    /**
     * 选择处理
     */
    private void spinnerHandle(int position) {
        String itemValue = mListCacheData.get(position);
        mTvTitle.setText(itemValue);
        mListCacheData.clear();
        mListCacheData.addAll(mListOriginalData);
        mListCacheData.remove(itemValue);
        mListAdapter.getListData().clear();
        mListAdapter.getListData().addAll(mListCacheData);
        mListAdapter.notifyDataSetChanged();
        if (mOnSpinnerItemClickListener != null) {
            mOnSpinnerItemClickListener.onSpinnerItemClick(itemValue);
        }
    }

    /**
     * 是否展开
     *
     * @param
     */
    private void displayWithArrowAnim() {
        if (mArrowAnimFlag) {
            mPopupWindow.showAsDropDown(SpinnerView.this);
            mIvArrow.startAnimation(mAnimFromDownToUp);
            mArrowAnimFlag = false;
        } else {
            mPopupWindow.dismiss();
            mIvArrow.startAnimation(mAnimFromUpToDown);
            mArrowAnimFlag = true;
        }
    }

    private OnClickListener onClickListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            displayWithArrowAnim();
        }
    };

    private AdapterView.OnItemClickListener onItemClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            spinnerHandle(position);
            displayWithArrowAnim();
        }
    };

    /**
     * 设置View的样式(Adapter的样式和是否蒙层)
     *
     * @param listAdapter
     * @param isShadow
     */
    public void setViewStyle(BaseSpinnerListAdapter listAdapter, boolean isShadow) {
        if (listAdapter == null) {
            return;
        }
        mListAdapter = listAdapter;
        ArrayList<String> listData = mListAdapter.getListData();
        if (listData == null || listData.isEmpty()) {
            return;
        }
        mListOriginalData.clear();
        mListOriginalData.addAll(listData);
        mListCacheData.clear();
        mListCacheData.addAll(listData);
        mListView.setAdapter(mListAdapter);

        if (isShadow) {
            mLayoutContent.setBackgroundColor(Color.parseColor("#7E000000"));
        } else {
            mLayoutContent.setBackgroundColor(Color.TRANSPARENT);
        }
        spinnerHandle(0);
    }

    /**
     * 设置ListView的宽高
     *
     * @param width
     * @param height
     */
    public void setListViewSize(int width, int height) {
        if (width != 0 && height != 0) {
            mListView.setLayoutParams(new LayoutParams(width, height));
        }
    }

    /**
     * 设置Title的文字大小
     *
     * @param size
     */
    public void setTitleTextSize(int size) {
        if (size > 0) {
            mTvTitle.setTextSize(TypedValue.COMPLEX_UNIT_PX, size);
        }
    }

    /**
     * 设置Title的文字颜色
     *
     * @param color
     */
    public void setTitleTextColor(int color) {
        if (color != 0) {
            mTvTitle.setTextColor(color);
        }
    }

    /**
     * 设置Arrow的宽高
     *
     * @param width
     * @param height
     */
    public void setArrowSize(int width, int height) {
        if (width >= 0 && height >= 0) {
            mIvArrow.setLayoutParams(new LayoutParams(width, height));
        }
    }

    /**
     * 设置Arrow图片
     *
     * @param res
     */
    public void setArrowImageResource(int res) {
        if (res != 0) {
            mIvArrow.setImageResource(res);
        }
    }

    /**
     * popupWindow显示和消失的动画
     *
     * @param animationStyle
     */
    public void setPopupAnimation(int animationStyle) {
        if (animationStyle != 0) {
            mPopupWindow.setAnimationStyle(animationStyle);
        }
    }

    /**
     * 设置Item点击监听器
     *
     * @param listener
     */
    public void setOnSpinnerItemClickListener(OnSpinnerItemClickListener listener) {
        mOnSpinnerItemClickListener = listener;
    }

    /**
     * Item点击监听器
     */
    public interface OnSpinnerItemClickListener {
        void onSpinnerItemClick(String itemValue);
    }
}




