package com.yilong.charts.listener;

import android.content.Context;
import android.graphics.Matrix;
import android.graphics.PointF;
import android.util.FloatMath;
import android.util.Log;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.ViewConfiguration;
import android.widget.OverScroller;
import com.yilong.charts.AppConstants;
import com.yilong.charts.Model.KLineModel;
import com.yilong.charts.view.KChartView;

import java.util.ArrayList;

/**
 * Created by MLS on 15/9/11.
 * k线滚动类
 */
public class KChartScrollListener {


    private Context mContext;
    private float mLastX;
    private KChartView chartView;
    private OverScroller mScroller;
    private VelocityTracker mVelocityTracker;
    private int mMaximumVelocity, mMinimumVelocity;

    public KChartScrollListener(Context context) {
        this.mContext = context;

        mMaximumVelocity = ViewConfiguration.get(context)
                .getScaledMaximumFlingVelocity();
        mMinimumVelocity = ViewConfiguration.get(context)
                .getScaledMinimumFlingVelocity();

    }

    // 第一个按下的手指的点
    private PointF startPoint = new PointF();
    // 两个按下的手指的触摸点的中点
    private PointF midPoint = new PointF();
    // 初始的两个手指按下的触摸点的距离
    private float oriDis = 1f;
    private Matrix matrix = new Matrix();
    private Matrix savedMatrix = new Matrix();
    private static final int NONE = 0;
    private static final int DRAG = 1;
    private static final int ZOOM = 2;
    private int mode = NONE;

    public void SetOnTouch(KChartView chartView, MotionEvent event, OverScroller mScroller, ArrayList<KLineModel.KData> kDatas, KChartView.kChartToBottomListener toBottomListener) {
        this.mScroller = mScroller;
        this.chartView = chartView;
        float x = event.getX();
        if (mVelocityTracker == null) {
            mVelocityTracker = VelocityTracker.obtain();
        }
        mVelocityTracker.addMovement(event);
        switch (event.getAction() & MotionEvent.ACTION_MASK) {  //多点触控
            case MotionEvent.ACTION_DOWN:
                if (!mScroller.isFinished())
                    mScroller.abortAnimation();
                initVelocityTrackerIfNotExists();

                mLastX = x;

                // 第一个手指按下事件
                matrix.set(chartView.getMatrix());
                savedMatrix.set(matrix);
                startPoint.set(event.getX(), event.getY());
                mode = DRAG;

                break;
            case MotionEvent.ACTION_POINTER_DOWN:

                // 第二个手指按下事件
                oriDis = distance(event);
                if (oriDis > 10f) {
                    savedMatrix.set(matrix);
                    midPoint = middle(event);
                    mode = ZOOM;
                }
                break;
            case MotionEvent.ACTION_MOVE:

                if (mode == DRAG) {
                    // 是一个手指拖动
                    float dx = x - mLastX;
                    chartView.scrollBy((int) -dx, 0);
                    mLastX = x;
                    if (chartView.getScrollX()>=-250 &&chartView.getScrollX() < 0) {
                        chartView.setLoadingText("向右拉加载");
                    }
                    if (chartView.getScrollX()< -250 ) {
                        chartView.setLoadingText("加载中   ");
                    }

                } else if (mode == ZOOM) {
                    // 两个手指滑动
                    float newDist = distance(event);
                    if (newDist > 10f) {

                        int cha = (int) (newDist - oriDis);
                        if (cha > 50 && AppConstants.K_DISPLAY_SHOW_COUNT > 40f) {
                            AppConstants.K_DISPLAY_SHOW_COUNT -= 1;
                        } else if (cha < -50 && AppConstants.K_DISPLAY_SHOW_COUNT < 80f) {
                            AppConstants.K_DISPLAY_SHOW_COUNT += 1;
                        }
                        chartView.invalidate();

                        matrix.set(savedMatrix);
                        float scale = newDist / oriDis;
                        matrix.postScale(scale, scale, midPoint.x, midPoint.y);
                    }
                }
                break;

            case MotionEvent.ACTION_CANCEL:
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_POINTER_UP:

                if (!mScroller.isFinished()) {
                    mScroller.abortAnimation();
                }
                mode = NONE;
                mVelocityTracker.computeCurrentVelocity(1000, mMaximumVelocity);
                int velocityX = (int) mVelocityTracker.getXVelocity();

                if (Math.abs(velocityX) > mMinimumVelocity) {
                    chartView.fling(-velocityX);
                }

                if (kDatas != null && kDatas.size() > 0) {
                    for (int i = 0; i < kDatas.size(); i++) {
                        float tempValue = kDatas.get(i).getScreenXLocation();
                        if (tempValue >chartView.getScrollX()+chartView.borderRect.left) {
                            chartView.currentArrayLeftLocation = i;
                            break;
                        }
                    }
                }
                if (chartView.getScrollX() < 0) {

                    if(chartView.getScrollX()< -250){
                        if(toBottomListener!=null){
                            toBottomListener.theBottom();
                        }
                    }
                    chartView.scrollTo(0, 0);
                    chartView.invalidate();
                }
                recycleVelocityTracker();
                break;
        }
    }


    // 计算两个触摸点之间的距离
    private float distance(MotionEvent event) {
        float x = event.getX(0) - event.getX(1);
        float y = event.getY(0) - event.getY(1);
        return FloatMath.sqrt(x * x + y * y);
    }

    // 计算两个触摸点的中点
    private PointF middle(MotionEvent event) {
        float x = event.getX(0) + event.getX(1);
        float y = event.getY(0) + event.getY(1);
        return new PointF(x / 2, y / 2);
    }

    private void initVelocityTrackerIfNotExists() {
        if (mVelocityTracker == null) {
            mVelocityTracker = VelocityTracker.obtain();
        }
    }

    private void recycleVelocityTracker() {
        if (mVelocityTracker != null) {
            mVelocityTracker.recycle();
            mVelocityTracker = null;
        }
    }

}
