package com.yilong.charts.touchlayout;

import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.LinearLayout;
import com.yilong.charts.CompareResult;
import com.yilong.charts.Model.MinuteModel;
import com.yilong.charts.R;
import com.yilong.charts.utils.Utils;
import com.yilong.charts.view.NewMinuteChartView;
import com.yilong.charts.view.VolumeChartView;

import java.util.ArrayList;
import java.util.List;


/**
 * 处理分时touch事件与传递数据
 */
public class MinuteTouchLinearLayout extends LinearLayout {


    Context mContext;
    public ArrayList<MinuteModel.MinuteData> minuteDatas;  //分时线数据
    PointLocation location;
    NewMinuteChartView minuteChart;
    VolumeChartView volumeChartView;
    Paint mPaint = NewMinuteChartView.getAxisYtitlePaintFont();

    /**
     * 十字交叉线回调
     */
    MinuteLineListener lineListener;

    /**
     * pointf.x = 价格的x轴坐标   y＝位于kdata
     */

    int postion = 0;

    private void initView(Context mContext) {
        this.mContext = mContext;
        location = new PointLocation();
    }

    public MinuteTouchLinearLayout(Context context) {
        super(context);
        initView(context);
    }

    public MinuteTouchLinearLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initView(context);
    }

    public MinuteTouchLinearLayout(Context context, AttributeSet attrs) {
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
            minuteChart = (NewMinuteChartView) getChildAt(0);
            volumeChartView = (VolumeChartView) getChildAt(1);
            initMinuteChart();
            volumeChartView.setAxisYLineCount(5);
        }
    }

    private void initMinuteChart() {
        List<String> xtitle = new ArrayList<String>();
        xtitle.add("9:30");
        xtitle.add("10:30");
        xtitle.add("13:00");
        xtitle.add("14:00");
        xtitle.add("15:00");
        minuteChart.setAxisXTitles(xtitle);
        minuteChart.setAxisYLineCount(5);
        minuteChart.setMaxValueColor(Color.parseColor("#FF0000"));
        minuteChart.setMinValueColor(Color.parseColor("#7ED591"));
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {

        return true;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        switch (event.getAction()) {
            case MotionEvent.ACTION_MOVE:
                float moveX = event.getX() + minuteChart.getScrollX();

                if (minuteDatas != null && minuteDatas.size() > 0) {
                    for (int i = 0; i < minuteDatas.size(); i++) {
                        float tempValue = minuteDatas.get(i).getScreenXLocation();
                        if (tempValue > moveX) {
                            location.setAxisXtempEnd(tempValue);
                            location.setAxisYEndLoaction(i);
                            if (i > 0) {
                                location.setAxisXtempStart(minuteDatas.get(i - 1).getScreenXLocation());
                                location.setAxisXStartLocation(i - 1);
                            }
                            break;
                        }
                    }
                }
                if (location.getAxisXtempStart() > 0 && location.getAxisXtempEnd() > 0) {
                    /**如果x值位于强后位置近的一方就赋值給它*/

                    /**
                     * 最终的值 和点
                     */
                    float endVaule = 0;

                    if (location.getAxisXtempEnd() - moveX > moveX - location.getAxisXtempStart()) {
                        endVaule = location.getAxisXtempStart();
                        postion = location.getAxisXStartLocation();
                    } else {
                        endVaule = location.getAxisXtempEnd();
                        postion = location.getAxisYEndLoaction();
                    }
                    /**
                     * 比例收盘价  也就是十字横线
                     */
                    float scaleLatestPrice = 0;

                    if (postion >= 0 && postion < minuteDatas.size()) {
                        scaleLatestPrice = minuteDatas.get(postion).getScaleLatestPrice();
                        /**这里回调*/

                        if (lineListener != null) {

                            minuteChart.setMinuteCurrentPrive(minuteDatas.get(postion).latest_price);
                            if (!TextUtils.isEmpty(minuteDatas.get(postion).list_time)) {
                                minuteChart.setListTime(minuteDatas.get(postion).list_time);
                            }
                            lineListener.minuteCallBack(minuteDatas.get(postion).latest_price, minuteDatas.get(postion).volume);
                        }

                    }
                    minuteChart.setTouchPoint(new PointF(endVaule, scaleLatestPrice));
                    volumeChartView.setTouchPoint(new PointF(endVaule, scaleLatestPrice));

                }
                break;
            case MotionEvent.ACTION_UP:
                //抬起去掉光标
                minuteChart.setTouchPoint(new PointF(0, 0));
                volumeChartView.setTouchPoint(new PointF(0, 0));
                break;
        }

        return true;
    }


    /***
     * 设置分时线数据
     *
     * @param minuteModel
     */
    public void setMinuteData(MinuteModel minuteModel) {

        if (minuteModel != null && minuteModel.data != null && minuteModel.data.size() > 0) {
            ArrayList<MinuteModel.MinuteData> minuteDatas = minuteModel.data;

            this.minuteDatas = minuteModel.data;

            caculate(minuteModel, minuteDatas, minuteModel.descrption.Highestprice, minuteModel.descrption.Lowestprice);
        }
    }


    /***
     * 计算 分时的价格与交易量最大价格
     *
     * @param minuteModel
     * @param minuteDatas
     * @param highestprice
     * @param lowestprice
     */
    public void caculate(MinuteModel minuteModel, ArrayList<MinuteModel.MinuteData> minuteDatas, float highestprice, float lowestprice) {

        float axisYTitleWidth = 0;

        CompareResult compareResult = Utils.compareMaxStringWidthCount(highestprice, lowestprice);
        float tempMinutepWidth = mPaint.measureText(compareResult.getMaxValue() + "");
        if (tempMinutepWidth > axisYTitleWidth) {
            axisYTitleWidth = tempMinutepWidth;
        }

        /**
         * 计算分时线成交量最大值最小值
         */
        long volumeMinValue = minuteDatas.get(0).volume;
        long volumeMaxValue = 0;
        for (int i = 0; i < minuteDatas.size(); i++) {

            long currentVolume = minuteDatas.get(i).volume;

            if (currentVolume > volumeMaxValue) {
                volumeMaxValue = currentVolume;
            }
            if (currentVolume < volumeMinValue) {
                volumeMinValue = currentVolume;
            }
        }

        Paint mPaint = NewMinuteChartView.getAxisYtitlePaintFont();
        int tempMinuteStickpWidth = (int) mPaint.measureText(String.valueOf(volumeMaxValue > volumeMinValue ? volumeMaxValue : volumeMinValue));

        if (tempMinuteStickpWidth > axisYTitleWidth) {
            axisYTitleWidth = tempMinuteStickpWidth;
        }

        minuteChart.setAxisYTitleWidth(axisYTitleWidth);
        minuteChart.setMaxAndMinValue(highestprice, lowestprice);
        minuteChart.setMinuteData(minuteModel);

        volumeChartView.setAxisYTitleWidth(axisYTitleWidth);//设置交易量title宽度
        volumeChartView.setMaxAndMinValue(volumeMaxValue, volumeMinValue);//设置金额
        volumeChartView.setVolumeData(minuteDatas);  //展示数据


        float rise = 0;
        for (int i = 0; i < minuteDatas.size(); i++) {

            float TempTise = minuteDatas.get(i).rise_and_decline;

            if (TempTise > rise) {
                rise = TempTise;
            }
        }
        minuteChart.setRiseAndDecline(rise);
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

    public void setOnMinuteListener(MinuteLineListener lineListener) {
        this.lineListener = lineListener;
    }

    public interface MinuteLineListener {
        public void minuteCallBack(float latest_price, long volume);
    }

}
