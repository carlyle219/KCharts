package com.yilong.charts.listener;

import android.content.Context;
import android.graphics.PointF;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import com.yilong.charts.EventBus.MlsEventBus;
import com.yilong.charts.Model.KLineModel;
import com.yilong.charts.view.KChartView;

import java.util.ArrayList;

/**
 * Created by MLS on 15/9/11.
 * k线十字交叉线
 */
public class KChartCrossListener implements GestureDetector.OnGestureListener {

    Context context;
    GestureDetector mGesture;
    ArrayList<KLineModel.KData> kDatas;
    PointLocation location;
    private float scaleClosePrice = 0;
    KChartView.KLineListener kLineListener;

    public KChartCrossListener(Context context) {
        this.context = context;
        initView();
    }


    private void initView() {
        mGesture = new GestureDetector(context, this);
        location = new PointLocation();
    }

    /**
     * pointf.x = 价格的x轴坐标   y＝位于kdata
     */
    boolean flag = false;
    /**
     * 最终的值 和点
     */
    float endVaule = 0;
    int postion = 0;


    KChartView chartView;

    /**
     * 比例收盘价  也就是十字横线
     */


    public void SetOnTouch(KChartView chartView, MotionEvent event, KChartView.KLineListener kLineListener) {
        this.chartView = chartView;
        this.kLineListener = kLineListener;
        switch (event.getAction()) {
            case MotionEvent.ACTION_MOVE:

                float moveX = event.getX() + chartView.getScrollX();
                Log.i("ceshit", " moveX   " + moveX);
                if (flag) {
                    /**十字光标显示的时候 后面不可以滚动*/
                    chartView.setIsAbleScroll(false);

                    if (kDatas != null && kDatas.size() > 0) {
                        for (int i = 0; i < kDatas.size(); i++) {
                            float tempValue = kDatas.get(i).getScreenXLocation();
                            if (tempValue > moveX) {
                                location.setAxisXtempEnd(tempValue);
                                location.setAxisYEndLoaction(i);
                                if (i > 0) {
                                    location.setAxisXtempStart(kDatas.get(i - 1).getScreenXLocation());
                                    location.setAxisXStartLocation(i - 1);
                                }
                                break;
                            }
                        }
                    }
                    if (location.getAxisXtempStart() > 0 && location.getAxisXtempEnd() > 0) {
                        /**如果x值位于强后位置近的一方就赋值給它*/

                        if (location.getAxisXtempEnd() - moveX > moveX - location.getAxisXtempStart()) {
                            endVaule = location.getAxisXtempStart();
                            postion = location.getAxisXStartLocation();
                        } else {
                            endVaule = location.getAxisXtempEnd();
                            postion = location.getAxisYEndLoaction();
                        }
                        if (postion > 0 && postion < kDatas.size()) {
                            scaleClosePrice = kDatas.get(postion).getScaleClosePrice();
                        }

                        if (kLineListener != null) {
                            kLineListener.KCallBack(kDatas.get(postion).close_price, kDatas.get(postion).volume);
                        }
                        chartView.setListTime(kDatas.get(postion).list_time);
                        chartView.setTouchPoint(new PointF(endVaule, scaleClosePrice));
                    }
                }
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                /**
                 * 与成交量联动
                 */
                MlsEventBus.postMainThread(
                        MlsEventBus.K_CHART_VOLUME_CROSS_X,
                        0f);
                chartView.setIsAbleScroll(true);
                //抬起去掉光标
                flag = false;
                chartView.setTouchPoint(new PointF(0, 0));
                break;
        }
        this.chartView = chartView;
        mGesture.onTouchEvent(event);
    }

    /***
     * 记录前后x轴绝对点坐标  和 数组中的位置
     */
    class PointLocation {

        float axisXtempStart;
        float axisXtempEnd;

        /**
         * 数组前后位置
         */
        int axisXStartLocation;
        int axisYEndLoaction;

        public float getAxisXtempStart() {
            return axisXtempStart;
        }

        public void setAxisXtempStart(float axisXtempStart) {
            this.axisXtempStart = axisXtempStart;
        }

        public float getAxisXtempEnd() {
            return axisXtempEnd;
        }

        public void setAxisXtempEnd(float axisXtempEnd) {
            this.axisXtempEnd = axisXtempEnd;
        }

        public int getAxisXStartLocation() {
            return axisXStartLocation;
        }

        public void setAxisXStartLocation(int axisXStartLocation) {
            this.axisXStartLocation = axisXStartLocation;
        }

        public int getAxisYEndLoaction() {
            return axisYEndLoaction;
        }

        public void setAxisYEndLoaction(int axisYEndLoaction) {
            this.axisYEndLoaction = axisYEndLoaction;
        }

    }


    @Override
    public boolean onDown(MotionEvent motionEvent) {
        return false;
    }

    @Override
    public void onShowPress(MotionEvent motionEvent) {

    }

    @Override
    public boolean onSingleTapUp(MotionEvent motionEvent) {
        return false;
    }

    @Override
    public boolean onScroll(MotionEvent motionEvent, MotionEvent motionEvent1, float distanceX, float v1) {

        return false;
    }

    @Override
    public void onLongPress(MotionEvent motionEvent) {
        //如果长按 显示十字光标
        if (!flag) {
            Log.i("ceshibb","onLongPress");
            flag = true;
            chartView.setIsAbleScroll(false);
            motionEvent.setAction(MotionEvent.ACTION_MOVE);
            SetOnTouch(chartView, motionEvent, kLineListener);
        }


    }

    @Override
    public boolean onFling(MotionEvent motionEvent, MotionEvent motionEvent1, float v, float v1) {
        return false;
    }

    public void setData(ArrayList<KLineModel.KData> kDatas) {
        this.kDatas = kDatas;
    }


}
