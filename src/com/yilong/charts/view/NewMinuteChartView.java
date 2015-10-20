package com.yilong.charts.view;

import android.content.Context;
import android.content.res.Configuration;
import android.graphics.*;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import com.yilong.charts.AppConstants;
import com.yilong.charts.CompareResult;
import com.yilong.charts.Model.MinuteModel;
import com.yilong.charts.utils.Utils;

import java.util.ArrayList;
import java.util.List;

/**
 * 自定义分时线draw
 */
public class NewMinuteChartView extends View {

    protected Context mContext;
    //   x轴刻度数组
    private List<String> axisXTitles;
    private boolean dashLongitude = true;//经线是否使用虚线
    private int borderColor = Color.GRAY;//边框颜色
    private int longitudeColor = Color.GRAY;//经线颜色
    private int backgroudColor = Color.WHITE;//背景色
    private boolean dashLatitude = true;//纬线是否为虚线
    /**
     * 默认虚线效果
     */
    public static final PathEffect DEFAULT_DASH_EFFECT = new DashPathEffect(
            new float[]{3, 3, 3, 3}, 1);
    private PathEffect dashEffect = DEFAULT_DASH_EFFECT;//虚线效果

    /**
     * 经线字体大小及颜色
     */
    public int longtitudeFontSize = 0;
    public int longtitudeFontColor = Color.GRAY;
    public int discrptionSize = 0;
    public int riseAndDecline = 0;

    /**
     * 纬线字体大小及颜色
     */
    private int latitudeColor = Color.GRAY;
    private static int latitudeFontColor = Color.GRAY;
    private static int latitudeFontSize = 20;
    private float axisYTitleWidth = 0;  //价格title宽度
    protected float axisXGapWidht = 0;  //X轴虚线每个间距
    protected int axisYLineCount = 0;  //y轴线条数量


    /**
     * 分时线数据
     */
    private MinuteModel minuteModels;

    /**
     * 边框矩形
     */
    public Rect borderRect = new Rect();

    private float maxValue = -1;
    private float minValue = -1;


    /***
     * 最大值最小值颜色
     */
    private int maxValueColor = Color.parseColor("#60000000");
    private int minValueColor = Color.parseColor("#60000000");


    /**
     * 涨幅度
     */
    float riseAndDeclineCount = 0;

    protected PointF touchPoint = new PointF();

    float minuteCurrentPrive = 0;   //分时价格

    public String listTime = "";//时间

    public NewMinuteChartView(Context context) {
        super(context);
        initView(context);
    }

    public NewMinuteChartView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initView(context);
    }

    public NewMinuteChartView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(context);
    }


    protected void initView(Context context) {
        this.mContext = context;

        longtitudeFontSize = Utils.dip2px(mContext, 13);
        latitudeFontSize = Utils.dip2px(mContext, 13);
        discrptionSize = Utils.dip2px(mContext, 13);
        riseAndDecline = Utils.dip2px(mContext, 13);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        setMeasuredDimension(measureWidth(widthMeasureSpec),
                measureHeight(heightMeasureSpec));
    }

    private int measureWidth(int measureSpec) {
        int result = 0;
        int specMode = MeasureSpec.getMode(measureSpec);
        int specSize = MeasureSpec.getSize(measureSpec);

        if (specMode == MeasureSpec.EXACTLY) {
            result = specSize;
        } else if (specMode == MeasureSpec.AT_MOST) {
            result = Math.min(result, specSize);
        }
        return result;
    }

    private int measureHeight(int measureSpec) {
        int result = 0;
        int specMode = MeasureSpec.getMode(measureSpec);
        int specSize = MeasureSpec.getSize(measureSpec);

        if (specMode == MeasureSpec.EXACTLY) {
            result = specSize;
        } else if (specMode == MeasureSpec.AT_MOST) {
            result = Math.min(result, specSize);
        }
        return result;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        /**设置背景色*/
        setBackgroundColor(backgroudColor);
        /**绘制边界*/
        drawBorder(canvas);
        /**绘制x轴网格线*/
        drawAxisGridX(canvas);
        /**绘制y轴网格线*/
        drawAxisGridY(canvas);
        /**绘制y轴title*/
        drawAxisYTitle(canvas);
        /**绘制分时线*/
        if (null != this.minuteModels) {
            drawLines(canvas);
        }
        /**绘制描述信息*/
        drawDiscrption(canvas);
        /**绘制涨幅度*/
        drawRiseAndDecline(canvas);

        drawCrossCoordinate(canvas);
    }

    private void drawRiseAndDecline(Canvas canvas) {

        Paint mPaint = getAxisYtitlePaintFont();
        mPaint.setTextSize(riseAndDecline);
        mPaint.setColor(Color.RED);

        int riseWidth = (int) mPaint.measureText(riseAndDeclineCount + "%");
        canvas.drawText(riseAndDeclineCount + "%", getScrollX() + borderRect.right - riseWidth - 2, borderRect.top + riseAndDecline - 4, mPaint);
        mPaint.setColor(Color.GREEN);
        int declineWdith = (int) mPaint.measureText("-" + riseAndDeclineCount + "%");
        canvas.drawText("-" + riseAndDeclineCount + "%", getScrollX() + borderRect.right - declineWdith - 2, borderRect.bottom - 4, mPaint);

    }

    private void drawDiscrption(Canvas canvas) {

        Paint mPaint = getAxisYtitlePaintFont();
        mPaint.setTextSize(discrptionSize);
        mPaint.setColor(Color.RED);

        canvas.drawText("分时:" + minuteCurrentPrive, getScrollX() + axisYTitleWidth, discrptionSize - 4, mPaint);

        mPaint.setColor(Color.BLACK);

        int listTimeWidth = (int) mPaint.measureText(listTime);

        canvas.drawText(listTime, borderRect.right - listTimeWidth, discrptionSize - 4, mPaint);

    }

    /**
     * 绘制边框
     */
    protected void drawBorder(Canvas canvas) {
        int width = super.getWidth() - 4;
        int height = super.getHeight() - 4;

        Paint mPaint = new Paint();
        mPaint.setColor(borderColor);
        mPaint.setStrokeWidth(2);
        mPaint.setStyle(Paint.Style.STROKE);
        int borderWidth = width;
        int borderHeight = height - longtitudeFontSize;
        /**
         * 如果左边y轴title有数据 则空出相应的位置
         */
        if (maxValue >= 0 && minValue >= 0) {
            Rect rect = new Rect();

            canvas.drawLine(axisYTitleWidth + 2 + getScrollX(), discrptionSize, 2 + borderWidth + getScrollX(), discrptionSize, mPaint);
            canvas.drawLine(axisYTitleWidth + 2 + getScrollX(), borderHeight + 2, borderWidth + 2 + getScrollX(), borderHeight + 2, mPaint);
            canvas.drawLine(axisYTitleWidth + 2 + getScrollX(), discrptionSize, axisYTitleWidth + 2 + getScrollX(), borderHeight + 2, mPaint);
            canvas.drawLine(borderWidth + 2 + getScrollX(), discrptionSize, borderWidth + 2 + getScrollX(), borderHeight + 2, mPaint);
            borderRect.set((int) (axisYTitleWidth + 2), discrptionSize + 2, borderWidth + 2, borderHeight + 2);
        } else {
            borderRect.set(2, discrptionSize + 2, borderWidth + 2, borderHeight + 2);
            canvas.drawRect(borderRect, mPaint);
        }
    }

    /**
     * 绘制经线
     *
     * @param canvas
     */
    protected void drawAxisGridX(Canvas canvas) {

        if (null != axisXTitles) {
            int counts = axisXTitles.size();
            int height = getHeight() - 2;
            Paint mPaintLine = new Paint();
            mPaintLine.setColor(longitudeColor);
            mPaintLine.setStyle(Paint.Style.STROKE);
            if (dashLongitude) {
                mPaintLine.setPathEffect(dashEffect);
            }
            Paint mPaintFont = new Paint();
            mPaintFont.setColor(longtitudeFontColor);
            mPaintFont.setTextSize(longtitudeFontSize);
            mPaintFont.setStrokeWidth(2);
            mPaintFont.setAntiAlias(true);

            if (counts > 1) {
                float xOffset = (borderRect.right - borderRect.left) / (float) (counts - 1);
                // 绘制刻度
                for (int x = 0; x < counts; x++) {
                    /**获取字符串宽度*/
                    int textWidth = axisXTitles.get(x).length() * longtitudeFontSize / 2;
                    if (0 == x) {
                        canvas.drawText(axisXTitles.get(x), borderRect.left, height, mPaintFont);
                    } else if (x > 0 && x < counts - 1) {
                        /**绘制线条*/
                        canvas.drawLine(xOffset * x + borderRect.left, discrptionSize + 2, xOffset * x + borderRect.left, height - longtitudeFontSize, mPaintLine);
                        /**绘制x轴title*/
                        canvas.drawText(axisXTitles.get(x), borderRect.left + xOffset * x - textWidth / 2, height, mPaintFont);
                    } else if (counts - 1 == x) {
                        canvas.drawText(axisXTitles.get(x), getWidth() - textWidth, height, mPaintFont);
                    }
                }
                axisXGapWidht = xOffset;
            }
        }
    }

    /**
     * 绘制纬线
     */
    protected void drawAxisGridY(Canvas canvas) {

        // 线条Paint
        Paint mPaintLine = new Paint();
        mPaintLine.setColor(latitudeColor);
        if (dashLatitude) {
            mPaintLine.setPathEffect(dashEffect);
        }

        if (axisYLineCount > 0) {
            float yOffset = (borderRect.bottom - borderRect.top) / (axisYLineCount - 1);
            for (int i = 0; i < axisYLineCount; i++) {
                if (i > 0 && i < axisYLineCount - 1) {
                    // 绘制线条
                    canvas.drawLine(borderRect.left, yOffset * i + discrptionSize, borderRect.right + getScrollX(), yOffset * i + discrptionSize,
                            mPaintLine);
                }
            }
        }
    }

    /**
     * 绘制y轴title
     */
    private void drawAxisYTitle(Canvas canvas) {
        if (maxValue >= 0 && minValue >= 0) {
            Paint mPaint = getAxisYtitlePaintFont();
            mPaint.setColor(Color.parseColor("#ffffff"));
            canvas.drawRect(getScrollX(), 0, getScrollX() + borderRect.left - 2, borderRect.bottom, mPaint);
            mPaint.setColor(maxValueColor);

            float tempValue = mPaint.measureText(maxValue + "");
            canvas.drawText(maxValue + "", getScrollX() + axisYTitleWidth - tempValue, latitudeFontSize + discrptionSize, mPaint);
            mPaint.setColor(minValueColor);
            tempValue = mPaint.measureText(minValue + "");
            canvas.drawText(minValue + "", getScrollX() + axisYTitleWidth - tempValue, borderRect.bottom, mPaint);
        }

    }

    //返回y轴title价格画笔
    public static Paint getAxisYtitlePaintFont() {
        Paint axisYtitlePaintFont = new Paint();
        axisYtitlePaintFont.setColor(latitudeFontColor);
        axisYtitlePaintFont.setTextSize(latitudeFontSize);
        axisYtitlePaintFont.setAntiAlias(true);
        axisYtitlePaintFont.setStrokeWidth(2);
        return axisYtitlePaintFont;
    }

    /**
     * 计算均线
     */
    ArrayList<Float> AveragelineArray = new ArrayList<>();

    private void drawLines(Canvas canvas) {

        float lineLength = 0;
        // 点线距离
        lineLength = ((borderRect.right - borderRect.left) / AppConstants.MINUTE_DISPLAY_SHOW_COUNT);
        Paint mPaint = new Paint();
        mPaint.setColor(Color.RED);
        mPaint.setAntiAlias(true);
        mPaint.setStrokeWidth(2);
        //定义起始点
        PointF ptFirst = null;
        PointF AveragelineFirst = null;
        float startX = borderRect.left + 1;

        // 1.最大价格
        float maxVaule = minuteModels.descrption.Highestprice;
        // 2.最小值
        float minValue = minuteModels.descrption.Lowestprice;
        AveragelineArray.clear();
        for (int x = 0; x < minuteModels.data.size(); x++) {
            MinuteModel.MinuteData data = minuteModels.data.get(x);
            /**按比例算出分时线y轴位置*/
            float valueY = ((maxVaule - data.latest_price) * (borderRect.bottom - borderRect.top) / (maxVaule - minValue)) + discrptionSize;

            float rise_and_decline = 0;

            if(AveragelineArray.size()>0){
                AveragelineArray.add(data.latest_price);
            }else{
                AveragelineArray.add((maxVaule+minValue)/2);
            }
            float totalPrive = 0;

            if (AveragelineArray.size() > 0) {
                for (int y = 0; y < AveragelineArray.size(); y++) {
                    totalPrive += AveragelineArray.get(y);
                }
                rise_and_decline = totalPrive / AveragelineArray.size();
            }

            /**按比例算出分时平均线y轴位置*/
            float AveragelineY = ((maxVaule - rise_and_decline) * (borderRect.bottom - borderRect.top) / (maxVaule - minValue)) + discrptionSize;
            //绘制线条
            if (x > 0) {
                mPaint.setColor(Color.RED);
                canvas.drawLine(ptFirst.x, ptFirst.y, startX, valueY, mPaint);
                mPaint.setColor(Color.BLUE);
                if(AveragelineArray.size()>2){
                    canvas.drawLine(AveragelineFirst.x, AveragelineFirst.y, startX, AveragelineY, mPaint);
                }
            }
            data.setScaleLatestPrice(valueY);
            data.setKScreenXLocation(startX);

            //重置起始点
            ptFirst = new PointF(startX, valueY);
            AveragelineFirst = new PointF(startX, AveragelineY);
            //X位移
            startX = (startX + lineLength);

            /**最后一个数值给分时线 与 时间设置初始值*/
            if (x == minuteModels.data.size() - 1) {
                if (flag) {
                    minuteCurrentPrive = data.latest_price;
                    listTime = data.list_time;
                    flag = false;
                }
            }
        }
    }

    boolean flag = true;


    public void setMinuteData(MinuteModel minuteModels) {
        this.minuteModels = minuteModels;
        invalidate();
    }


    public void setMaxAndMinValue(float maxValue, float minValue) {
        this.maxValue = maxValue;
        this.minValue = minValue;
        postInvalidate();
    }

    /**
     * 绘制十字坐标
     */
    protected void drawCrossCoordinate(Canvas canvas) {
        Paint mPaint = new Paint();
        mPaint.setColor(Color.BLACK);
        mPaint.setStrokeWidth(2);
        if (touchPoint.x > 0 && touchPoint.y > 0) {

            canvas.drawLine(touchPoint.x, borderRect.top, touchPoint.x, borderRect.bottom, mPaint);
            canvas.drawLine(borderRect.left + getScrollX(), touchPoint.y, borderRect.right + getScrollX(), touchPoint.y, mPaint);

        }
    }

    public void setTouchPoint(PointF pointF) {
        touchPoint = pointF;
        invalidate();
    }

    public void setAxisXTitles(List<String> axisXTitles) {
        this.axisXTitles = axisXTitles;
    }


    public void setListTime(String listTime) {
        this.listTime = listTime;
        postInvalidate();
    }

    public float getRiseAndDecline() {
        return riseAndDeclineCount;
    }

    public void setRiseAndDecline(float riseAndDeclineCount) {
        this.riseAndDeclineCount = riseAndDeclineCount;
        invalidate();
    }

    public void setAxisYTitleWidth(float axisYTitleWidth) {
        this.axisYTitleWidth = axisYTitleWidth;
    }

    public void setAxisYLineCount(int axisYLineCount) {
        this.axisYLineCount = axisYLineCount;
    }

    public void setMinValueColor(int minValueColor) {
        this.minValueColor = minValueColor;
    }

    public void setMaxValueColor(int maxValueColor) {
        this.maxValueColor = maxValueColor;
    }

    public void setMinuteCurrentPrive(float minuteCurrentPrive) {
        this.minuteCurrentPrive = minuteCurrentPrive;
        invalidate();
    }

    @Override
    protected void onConfigurationChanged(Configuration newConfig) {
        invalidate();
        super.onConfigurationChanged(newConfig);
    }
}
