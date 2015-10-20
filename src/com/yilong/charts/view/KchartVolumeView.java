package com.yilong.charts.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;
import android.util.AttributeSet;
import android.util.Log;
import com.yilong.charts.AppConstants;
import com.yilong.charts.EventBus.MlsEventBus;
import com.yilong.charts.Model.KLineModel;
import com.yilong.charts.PriceF;

import java.util.ArrayList;

/**
 * 交易量柱形图
 */
public class KchartVolumeView extends GridChartView {


    Context mContext;
    float volumeLength = 0;
    PriceF pricef = new PriceF();
    ArrayList<KLineModel.KData> kDatas;

    public KchartVolumeView(Context context) {
        super(context);
        init(context);
    }

    public KchartVolumeView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context);
    }

    public KchartVolumeView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    private void init(Context mContext) {
        this.mContext = mContext;
        MlsEventBus.register(this);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (kDatas != null && kDatas.size() > 0) {
            drawSticks(canvas);
        }
        drawAxisYTitle(canvas);
        drawCrossCoordinate(canvas);
    }

    private void drawSticks(Canvas canvas) {

        // 点线距离
        volumeLength = ((borderRect.right - borderRect.left) / AppConstants.K_DISPLAY_SHOW_COUNT);
        float startX = borderRect.left + volumeLength / 2;

        Paint mPaint = new Paint();
        mPaint.setColor(Color.parseColor("#999999"));
        mPaint.setAntiAlias(true);
        mPaint.setStrokeWidth(2);

        for (int x = 0; x < kDatas.size(); x++) {
            KLineModel.KData data = kDatas.get(x);
            float cha = pricef.maxValue - data.volume;
            float pricequjian = pricef.maxValue - pricef.minValue;
            float valueY = (cha * (borderRect.bottom - 2) / pricequjian);

            if(data.open_price<data.close_price){
                mPaint.setColor(Color.RED);
            }else{
                mPaint.setColor(Color.GREEN);
            }
            //绘制线条
                canvas.drawRect(startX - (volumeLength / 2 - 1), valueY, startX + (volumeLength / 2 - 1), borderRect.bottom - 1, mPaint);
            //X位移
            startX = (startX + volumeLength);
        }
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

            canvas.drawLine(touchPoint.x, borderRect.top, touchPoint.x, borderRect.bottom, mPaint);
        }
    }

    private int yWidth = 0;

    public void setKData(ArrayList<KLineModel.KData> kDatas) {
        this.kDatas = kDatas;

        long tempHeihtVolume = 0;
        long tempLowVolume = kDatas.get(0).volume;
        if (kDatas.size() <= AppConstants.K_DISPLAY_SHOW_COUNT) {
            for (int x = 0; x < kDatas.size(); x++) {

                if (tempHeihtVolume < kDatas.get(x).volume) {
                    tempHeihtVolume = kDatas.get(x).volume;
                }
                if (tempLowVolume > kDatas.get(x).volume) {
                    tempLowVolume = kDatas.get(x).volume;
                }
            }
        } else {
            for (int x = kDatas.size() - 1; x >= kDatas.size() - AppConstants.K_DISPLAY_SHOW_COUNT; x--) {
                if (tempHeihtVolume < kDatas.get(x).volume) {
                    tempHeihtVolume = kDatas.get(x).volume;
                }
                if (tempLowVolume > kDatas.get(x).volume) {
                    tempLowVolume = kDatas.get(x).volume;
                }
            }
        }

       Paint mPaint =  getAxisYtitlePaintFont();
        yWidth = (int) mPaint.measureText(tempHeihtVolume+"");

        setMaxAndMinValue(tempHeihtVolume, tempLowVolume);
    }

    public void setMaxAndMinValue(long maxValue, long minValue) {
        pricef.maxValue = maxValue;
        pricef.minValue = minValue;
        super.maxValue = maxValue;
        super.minValue = minValue;
        invalidate();
    }


    public void setTouchPoint(PointF touchPoint) {
        this.touchPoint = touchPoint;
        invalidate();
    }

    @Override
    protected void onDetachedFromWindow() {
        MlsEventBus.unregister(this);
        super.onDetachedFromWindow();
    }

    int sycnScrollTo;   //k线传递过来的滑动

    public void onEventMainThread(MlsEventBus.MainEvent event) {

        if (event != null && MlsEventBus.K_CHART_VOLUME_VOLUEM_SCROLL_TO.equals(event.eventDes)) {
            if (event.eventObject instanceof Integer) {

                sycnScrollTo = (int) event.eventObject;

                scrollTo(sycnScrollTo, 0);
            }
        }
        if (event != null && MlsEventBus.K_CHART_VOLUME_CROSS_X.equals(event.eventDes)) {
            if (event.eventObject instanceof Float) {

                setTouchPoint(new PointF((float) event.eventObject, 0));
            }
        }
        if (event != null && MlsEventBus.K_CHART_VOLUME_LEFT_AXISY_TITLE_WIDTH.equals(event.eventDes)) {
            if (event.eventObject instanceof Integer) {
               int tempWidth =  (int) event.eventObject;
                if(tempWidth>=yWidth){
                    setAxisYTitleWidth(tempWidth);
                }else{
                    setAxisYTitleWidth(yWidth);
                    MlsEventBus.postMainThread(MlsEventBus.K_CHART_VOLUME_VOLUEM_CALLBACK_WIDTH,yWidth);
                }
            }
        }
    }
}
