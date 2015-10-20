package com.yilong.charts.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;
import android.util.AttributeSet;
import android.view.MotionEvent;
import com.yilong.charts.AppConstants;
import com.yilong.charts.Model.MinuteModel;

import java.util.ArrayList;

/**
 * 交易量柱形图
 */
public class VolumeChartView extends GridChartView {

    /**
     * 设置交易量 最大值 最小值
     */
    long volumeMinValue = 0;
    long volumeMaxValue = 0;


    Context mContext;
    ArrayList<MinuteModel.MinuteData> volumeDatas;
    float volumeLength = 0;

    public VolumeChartView(Context context) {
        super(context);
        init(context);
    }

    public VolumeChartView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context);
    }

    public VolumeChartView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    private void init(Context mContext) {
        this.mContext = mContext;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (volumeDatas != null && volumeDatas.size() > 0) {
            drawSticks(canvas);
        }

        drawCrossCoordinate(canvas);
    }


    private void drawSticks(Canvas canvas) {

        // 点线距离
        volumeLength = ((borderRect.right - borderRect.left) / AppConstants.MINUTE_DISPLAY_SHOW_COUNT);
        float startX = borderRect.left + 1;

        Paint mPaint = new Paint();
        mPaint.setColor(Color.parseColor("#999999"));
        mPaint.setAntiAlias(true);
        mPaint.setStrokeWidth(2);

        for (int x = 0; x < volumeDatas.size(); x++) {
            MinuteModel.MinuteData volumeData = volumeDatas.get(x);
            float cha = volumeMaxValue - volumeData.volume + volumeData.volume * 0.02f;
            float pricequjian = volumeMaxValue - volumeMinValue;
            float valueY = (cha * (borderRect.bottom - 2) / pricequjian);

            //绘制线条
            if (x > 0) {
                canvas.drawRect(startX - (volumeLength / 2 - 1), valueY, startX + (volumeLength / 2 - 1), borderRect.bottom - 1, mPaint);
            }
            //X位移
            startX = (startX + volumeLength);

        }
    }

    public void setVolumeData(ArrayList<MinuteModel.MinuteData> volumeDatas) {
        this.volumeDatas = volumeDatas;

        invalidate();
    }

    /**
     * 绘制十字坐标
     */
    protected void drawCrossCoordinate(Canvas canvas) {
        Paint mPaint = new Paint();
        mPaint.setColor(Color.BLACK);
        mPaint.setStrokeWidth(2);

        /**2.绘制竖线*/
        if (touchPoint.x > 0) {
            /**竖轴边界检查*/
            if (touchPoint.x < borderRect.left) {
                touchPoint.x = borderRect.left;
            }
            if (touchPoint.x > borderRect.right) {
                touchPoint.x = borderRect.right;
            }
            canvas.drawLine(touchPoint.x, borderRect.top, touchPoint.x, borderRect.bottom, mPaint);
        }
    }


    @Override
    public void setMaxAndMinValue(long maxValue, long minValue) {

        this.volumeMaxValue = maxValue;
        this.volumeMinValue = minValue;

        super.setMaxAndMinValue(maxValue, minValue);
    }


    public void setVolumeMaxValue(int volumeMaxValue) {
        this.volumeMaxValue = volumeMaxValue;
    }

    public void setVolumeMinValue(int volumeMinValue) {
        this.volumeMinValue = volumeMinValue;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        return super.onTouchEvent(event);
    }

    /**
     * 由外层布局设置联动
     */
    public void setTouchPoint(PointF pointF) {
        touchPoint = pointF;
        invalidate();
    }

}
