package com.yilong.charts.view;

import android.content.Context;
import android.graphics.*;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import com.yilong.charts.CompareResult;
import com.yilong.charts.Model.KLineModel;
import com.yilong.charts.Model.MinuteModel;
import com.yilong.charts.PriceF;
import com.yilong.charts.listener.KChartCrossListener;
import com.yilong.charts.utils.Utils;

import java.util.ArrayList;

/**
 * 主要负责画k线
 */
public class KChartCanvasView {


    Context mContext;
    KChartView kchartView;
    /***
     * 如果k线一屏展示不下第一次需要移动到最右端
     */
    boolean oneflag = true;

    public KChartCanvasView(Context context, KChartView kchartView) {
        this.mContext = context;
        this.kchartView = kchartView;
    }

    /**
     * 绘制k线
     *
     * @param chartView
     * @param canvas
     * @param kDatas        k线数据
     * @param borderRect    边框布局
     * @param dispalyCount
     * @param crossListener 十字光标监听
     * @param pricef        最高价格  最低价格
     */
    public void drawKLines(KChartView chartView, Canvas canvas, ArrayList<KLineModel.KData> kDatas, Rect borderRect, float dispalyCount, KChartCrossListener crossListener, PriceF pricef) {

        if (kDatas != null && kDatas.size() > 0) {
            // 点线距离
            chartView.lineGapWidth = ((borderRect.right - borderRect.left) / dispalyCount);

            Paint mPaint = new Paint();
            mPaint.setAntiAlias(true);
            mPaint.setStrokeWidth(2);

            float startX = borderRect.left + chartView.lineGapWidth / 2;
            for (int x = 0; x < kDatas.size(); x++) {

                KLineModel.KData data = kDatas.get(x);
                /**按比例算出y轴位置*/
                float betterHeightValue = ((pricef.maxValue - data.better_height_price) * (borderRect.bottom - borderRect.top) / (pricef.maxValue - pricef.minValue)) + chartView.discrptionSize;
                float betterLowPrice = ((pricef.maxValue - data.better_low_price) * (borderRect.bottom - borderRect.top) / (pricef.maxValue - pricef.minValue)) + chartView.discrptionSize;

                float closePrice = ((pricef.maxValue - data.close_price) * (borderRect.bottom - borderRect.top) / (pricef.maxValue - pricef.minValue)) + chartView.discrptionSize;
                float openPrice = ((pricef.maxValue - data.open_price) * (borderRect.bottom - borderRect.top) / (pricef.maxValue - pricef.minValue)) + chartView.discrptionSize;

                if (openPrice < closePrice) {
                    mPaint.setColor(Color.GREEN);
                } else {
                    mPaint.setColor(Color.RED);
                }

                String time = data.list_time;

                String showTime = caculateTimeShow(time.trim());
                //绘制线条

                if (!TextUtils.isEmpty(showTime)) {
                    Paint mPaintLine = new Paint();
                    mPaintLine.setColor(chartView.latitudeColor);
                    mPaintLine.setPathEffect(chartView.dashEffect);
                    canvas.drawLine(startX, borderRect.top, startX, borderRect.bottom, mPaintLine);
                    int tempTimeWidth = (int) chartView.getAxisYtitlePaintFont().measureText(showTime);

                    canvas.drawText(showTime, (startX - tempTimeWidth / 2), chartView.getHeight() - 2, chartView.getAxisYtitlePaintFont());
                }
                canvas.drawLine(startX, betterHeightValue, startX, betterLowPrice, mPaint);
                canvas.drawRect(startX - (chartView.lineGapWidth / 2 - 1), openPrice, startX + (chartView.lineGapWidth / 2 - 1), closePrice, mPaint);

                data.setScaleClosePriceValue(closePrice);
                data.setKScreenXLocation(startX);
                //X位移
                startX = (startX + chartView.lineGapWidth);
            }
            /**绘制十字交叉线用*/
            crossListener.setData(kDatas);
            /**边界检测用*/
            kchartView.checkBorderRight = startX - chartView.lineGapWidth / 2 - 1;
            if (oneflag) {
                if (kDatas.size() > dispalyCount) {
                    kchartView.scrollTo((int) (kchartView.checkBorderRight), 0);
                }
                oneflag = false;
            }
        }
    }

    private String caculateTimeShow(String time) {

//        2015-05-22 17:00:09
        String[] strArray = time.split("\\s+");
        if (strArray.length == 2) {

            String tempa = strArray[0];
            String[] arrays = tempa.split("-");
            if (arrays.length == 3) {
                try {
                    int data = Integer.valueOf(arrays[2]);
                    if (data == 1) {
                        return tempa;
                    }
                } catch (Exception e) {
                    return "";
                }
            }
        }
        return "";
    }

    /**
     * 绘制边框
     */
    protected void drawBorder(Canvas canvas, Rect borderRect) {
        Paint mPaint = new Paint();
        mPaint.setColor(kchartView.borderColor);
        mPaint.setStrokeWidth(2);
        mPaint.setStyle(Paint.Style.STROKE);

        /**
         * 如果左边y轴title有数据 则空出相应的位置
         */
        canvas.drawRect(borderRect.left + kchartView.getScrollX(), borderRect.top - 2, borderRect.right + kchartView.getScrollX(), borderRect.bottom, mPaint);
    }

}
