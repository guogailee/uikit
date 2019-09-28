package com.guogai.kit.ui.scrollView;

import android.content.Context;
import android.graphics.Rect;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.ViewTreeObserver;
import android.widget.ScrollView;

/**
 * 监听键盘，自动上移ScrollView
 * Created by guogai on 2016/1/18.
 */
public class WithOnSoftKeyBoardListenerScrollView extends ScrollView {
    public WithOnSoftKeyBoardListenerScrollView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public WithOnSoftKeyBoardListenerScrollView(Context context) {
        super(context);
        init();
    }

    private void init() {
        monitorKeyBoard();
    }

    /**
     * 监听键盘
     */
    private void monitorKeyBoard() {
        new Handler().post(new Runnable() {
            @Override
            public void run() {
                getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                    @Override
                    public void onGlobalLayout() {
                        Rect rect = new Rect();
                        getWindowVisibleDisplayFrame(rect);
                        int screenHeight = getRootView().getHeight();
                        int keyBoardHeight = screenHeight - rect.bottom;
//                        int offset = screenHeight - getHeight();
//                        if (offset > keyBoardHeight) {
//                            fullScroll(ScrollView.FOCUS_DOWN);
//                        }
                        if (keyBoardHeight > 0) {
                            fullScroll(ScrollView.FOCUS_DOWN);
                        }
                    }
                });
            }
        });
    }
}
