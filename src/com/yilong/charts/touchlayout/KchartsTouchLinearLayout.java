package com.yilong.charts.touchlayout;

import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.LinearLayout;
import com.yilong.charts.Model.KLineModel;
import com.yilong.charts.view.KChartView;
import com.yilong.charts.view.KchartVolumeView;

import java.util.ArrayList;


/***
 * k线touch类以及传送数据
 */

public class KchartsTouchLinearLayout extends LinearLayout {

    Context mContext;
    KChartView kChartView;
    KchartVolumeView volumeChartView;
    ArrayList<KLineModel.KData> kDatas;
    KChartView.kChartToBottomListener toBottomListener;


    private void initView(Context mContext) {
        this.mContext = mContext;
    }

    public KchartsTouchLinearLayout(Context context) {
        super(context);
        initView(context);
    }

    public KchartsTouchLinearLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initView(context);
    }

    public KchartsTouchLinearLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(context);
    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

        if (this.getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
            heightMeasureSpec = MeasureSpec.makeMeasureSpec(400, MeasureSpec.EXACTLY);
        }
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        int childCount = getChildCount();

        if (childCount == 2) {
            kChartView = (KChartView) getChildAt(0);
            kChartView.setAxisYLineCount(5);
            kChartView.setMaxValueColor(Color.parseColor("#FF0000"));
            kChartView.setMinValueColor(Color.parseColor("#7ED591"));
            kChartView.setkChartBottomListener(toBottomListener);


            volumeChartView = (KchartVolumeView) getChildAt(1);
            volumeChartView.setAxisYLineCount(5);
            volumeChartView.setMaxValueColor(Color.parseColor("#FF0000"));
            volumeChartView.setMinValueColor(Color.parseColor("#7ED591"));

        }
    }

    public void setKData(ArrayList<KLineModel.KData> kDatas) {
        this.kDatas = kDatas;
        kChartView.setKData(kDatas);
        volumeChartView.setKData(kDatas);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {

        return true;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        kChartView.setOnTouchEvent(event);
        return true;
    }

    public void setkChartBottomListener(KChartView.kChartToBottomListener toBottomListener) {
        this.toBottomListener = toBottomListener;
    }


    public void setCurrentArrayLeftLocation(int size) {
        kChartView.setCurrentArrayLeftLocation(kChartView.getCurrentArrayLeftLocation() + size - 1);
    }

    public void setOnKLineListener(KChartView.KLineListener lineListener) {
        kChartView.setOnKLineListener(lineListener);
    }

}
