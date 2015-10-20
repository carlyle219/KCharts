package com.yilong.charts.view;

import android.app.Activity;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.yilong.charts.R;

/**
 * 竖屏的话  点击横屏
 */
public class MyFrameLayout extends FrameLayout {

    Context mContext;

    public MyFrameLayout(Context context) {
        super(context);
        initView(context);
    }

    public MyFrameLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initView(context);
    }

    public MyFrameLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(context);
    }

    protected void initView(Context context) {
        this.mContext = context;

    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        if (mContext.getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) { // 竖屏
            if (mContext instanceof Activity) {
                ((Activity) mContext).setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
            }
            return true;
        } else {
            return false;
        }
    }
}
