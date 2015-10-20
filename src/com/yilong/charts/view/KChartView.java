package com.yilong.charts.view;

import android.content.Context;
import android.content.res.Configuration;
import android.graphics.*;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.OverScroller;
import com.yilong.charts.AppConstants;
import com.yilong.charts.CompareResult;
import com.yilong.charts.EventBus.MlsEventBus;
import com.yilong.charts.Model.KLineModel;
import com.yilong.charts.PriceF;
import com.yilong.charts.listener.KChartCrossListener;
import com.yilong.charts.listener.KChartScrollListener;
import com.yilong.charts.utils.Utils;

import java.util.ArrayList;

/**
 * Created by MLS on 15/9/11.
 * k线走势图
 */
public class KChartView extends View {

    protected Context mContext;
    public int borderColor = Color.GRAY;//边框颜色
    private int backgroudColor = Color.WHITE;//背景色
    private boolean dashLatitude = true;//纬线是否为虚线
    /**
     * 默认虚线效果
     */
    public static final PathEffect DEFAULT_DASH_EFFECT = new DashPathEffect(
            new float[]{3, 3, 3, 3}, 1);
    public PathEffect dashEffect = DEFAULT_DASH_EFFECT;//虚线效果

    /***
     * 最大值最小值颜色
     */
    private int maxValueColor = Color.parseColor("#60000000");
    private int minValueColor = Color.parseColor("#60000000");
    /**
     * 经线字体大小及颜色
     */
    public int longtitudeFontSize = 0;
    /**
     * 纬线字体大小及颜色
     */
    public int latitudeColor = Color.GRAY;
    private int latitudeFontColor = Color.GRAY;
    private int latitudeFontSize = 20;
    private int axisYTitleWidth = 0;  //价格title宽度
    protected int axisYLineCount = 0;  //y轴线条数量
    /**
     * 边框矩形
     */
    public Rect borderRect = new Rect();


    /***
     * 横竖屏幕
     */
    public boolean oneflag = true;

    /**
     * k线是否可以左右滑动
     */
    boolean isAbleScroll = true;

    /**
     * 加载进度词
     */
    String loadingText = "";

    /**
     * 滑动之后数组左边在数组中的位置
     */
    public int currentArrayLeftLocation = 0;

    /**
     * k线每一个距离
     */
    public float lineGapWidth = 0;

    /**
     * 十字坐标触摸点
     */
    protected PointF touchPoint = new PointF();

    /**
     * 时间
     */
    private String listTime = "";
    public int discrptionSize = 0;

    public KChartScrollListener scrollListener;
    public KChartCrossListener crossListener;
    public OverScroller mScroller;

    public KChartView(Context context) {
        super(context);
        initView(context);
    }

    public KChartView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initView(context);
    }

    public KChartView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(context);
    }


    KChartCanvasView kChartCanvasView; //专门绘制k线的类

    protected void initView(Context context) {
        this.mContext = context;
        MlsEventBus.register(this);
        longtitudeFontSize = Utils.dip2px(mContext, 13);
        latitudeFontSize = Utils.dip2px(mContext, 13);
        discrptionSize = Utils.dip2px(mContext, 13);
        scrollListener = new KChartScrollListener(context);
        crossListener = new KChartCrossListener(context);
        mScroller = new OverScroller(context);
        kChartCanvasView = new KChartCanvasView(context, this);

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
        /**测量矩形边框大小    下面绘制都要用*/
        MeasureBorderRect();
        /**绘制y轴网格线*/
        drawAxisGridY(canvas);
        /**绘制k线*/
        kChartCanvasView.drawKLines(this,canvas, kDatas, borderRect, AppConstants.K_DISPLAY_SHOW_COUNT, crossListener, pricef);
        /**绘制加载中*/
        drawLoading(canvas);
        /**绘制y轴title*/
        drawAxisYTitle(canvas);
        /**绘制描述信息*/
        drawDiscrption(canvas);
        /**绘制边界*/
        kChartCanvasView.drawBorder(canvas, borderRect);
        /**绘制十字交叉线*/
        drawCrossCoordinate(canvas);

        drawScreenChange();
    }

    private void drawScreenChange() {

        if (oneflag) {
            scrollTo((int)(currentArrayLeftLocation * lineGapWidth), 0);
            oneflag = false;
        }
    }

    /**
     * 绘制描述信息
     *
     * @param canvas
     */
    private void drawDiscrption(Canvas canvas) {

        Paint mPaint = getAxisYtitlePaintFont();
        mPaint.setTextSize(discrptionSize);
        mPaint.setColor(Color.WHITE);

        canvas.drawRect(0,0,getWidth(),discrptionSize-2, mPaint);
        mPaint.setColor(Color.BLACK);
        int listTimeWidth = (int) mPaint.measureText(listTime);

        canvas.drawText(listTime, getScrollX()+borderRect.right - listTimeWidth, discrptionSize - 4, mPaint);

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

            float yOffset = (borderRect.bottom-borderRect.top-2) / (axisYLineCount - 1);
            for (int i = 0; i < axisYLineCount; i++) {
                if (i > 0 && i < axisYLineCount - 1) {
                    // 绘制线条
                    canvas.drawLine(borderRect.left + getScrollX(), yOffset * i+discrptionSize, borderRect.right + getScrollX(), yOffset * i+discrptionSize,
                            mPaintLine);
                }
            }
        }
    }

    /**
     * 绘制y轴title
     */
    private void drawAxisYTitle(Canvas canvas) {
        if (pricef.maxValue >= 0 && pricef.minValue >= 0) {
            Paint mPaint = getAxisYtitlePaintFont();
            mPaint.setColor(Color.parseColor("#ffffff"));
            canvas.drawRect(getScrollX(), 0, getScrollX() + borderRect.left - 2, getHeight(), mPaint);
            mPaint.setColor(maxValueColor);
            float tempValue = mPaint.measureText(pricef.maxValue + "");
            canvas.drawText(pricef.maxValue + "", borderRect.left+getScrollX()-tempValue, latitudeFontSize+discrptionSize-4, mPaint);
            mPaint.setColor(minValueColor);
            tempValue = mPaint.measureText(pricef.minValue + "");
            canvas.drawText(pricef.minValue + "",borderRect.left+getScrollX()-tempValue, borderRect.bottom, mPaint);
        }

    }


    public void setOnTouchEvent(MotionEvent event) {

        crossListener.SetOnTouch(this, event,kLineListener);
        if (isAbleScroll) {
            scrollListener.SetOnTouch(this, event, mScroller, kDatas, toBottomListener);
        }
    }

    /***
     * 测量绘画的矩形边框大小
     */
    private void MeasureBorderRect() {

        int width = super.getWidth() - 4;
        int height = super.getHeight() - 4;

        Paint mPaint = new Paint();
        mPaint.setColor(borderColor);
        mPaint.setStrokeWidth(2);
        mPaint.setStyle(Paint.Style.STROKE);

        int borderWidth = width;
        int borderHeight = height - longtitudeFontSize;

        if (pricef.maxValue >= 0 && pricef.minValue >= 0) {
            CompareResult compareResult = Utils.compareMaxStringWidthCount(pricef.maxValue, pricef.minValue);
            Paint axisYtitlePaintFont = getAxisYtitlePaintFont();
            int tempWidth = (int) axisYtitlePaintFont.measureText(String.valueOf(compareResult.getMaxValue()));

            if(tempWidth>=kVolumeAxisYWidth){
                axisYTitleWidth = tempWidth;
            }else{
                axisYTitleWidth = kVolumeAxisYWidth;
            }

            borderRect.set(axisYTitleWidth + 2, 2+discrptionSize, borderWidth + 2, borderHeight + 2);

        } else {
            borderRect.set(2, 2, borderWidth + 2, borderHeight + 2);
        }
        /***
         * 与成交量联动
         */
        MlsEventBus.postMainThread(MlsEventBus.K_CHART_VOLUME_LEFT_AXISY_TITLE_WIDTH, axisYTitleWidth);
    }


    //返回y轴title价格画笔
    public Paint getAxisYtitlePaintFont() {
        Paint axisYtitlePaintFont = new Paint();
        axisYtitlePaintFont.setColor(latitudeFontColor);
        axisYtitlePaintFont.setTextSize(latitudeFontSize);
        axisYtitlePaintFont.setAntiAlias(true);
        axisYtitlePaintFont.setStrokeWidth(2);
        return axisYtitlePaintFont;
    }

    public float checkBorderRight = 0;

    private ArrayList<KLineModel.KData> kDatas;

    PriceF pricef = new PriceF();

    /**
     * 绘制十字坐标
     */
    protected void drawCrossCoordinate(Canvas canvas) {
        Paint mPaint = new Paint();
        mPaint.setColor(Color.BLACK);
        mPaint.setStrokeWidth(2);
        if (touchPoint.x > 0 && touchPoint.y > 0) {

            if (touchPoint.x < borderRect.left + getScrollX()) {
                touchPoint.x = borderRect.left + getScrollX();
            }
            /**
             * 与成交量联动
             */
            MlsEventBus.postMainThread(
                    MlsEventBus.K_CHART_VOLUME_CROSS_X,
                    touchPoint.x);

            canvas.drawLine(touchPoint.x, borderRect.top, touchPoint.x, borderRect.bottom, mPaint);
            canvas.drawLine(borderRect.left + getScrollX(), touchPoint.y, borderRect.right + getScrollX(), touchPoint.y, mPaint);

        }
    }

    boolean inputOneFlag = true;

    public void setKData(ArrayList<KLineModel.KData> kDatas) {
        this.kDatas = kDatas;

        /***
         * 如果k线一屏展示不下第一次需要移动到最右端
         */
        if (inputOneFlag) {
            if (kDatas.size() > AppConstants.K_DISPLAY_SHOW_COUNT) {
                currentArrayLeftLocation = kDatas.size()-(int)AppConstants.K_DISPLAY_SHOW_COUNT;
            }
            inputOneFlag = false;
        }else{

        }
        float tempHeihtPrice = 0;
        float tempLowPrice = kDatas.get(0).better_low_price;
        if (kDatas.size() <= AppConstants.K_DISPLAY_SHOW_COUNT) {
            for (int x = 0; x < kDatas.size(); x++) {

                if (tempHeihtPrice < kDatas.get(x).better_height_price) {
                    tempHeihtPrice = kDatas.get(x).better_height_price;
                }
                if (tempLowPrice > kDatas.get(x).better_low_price) {
                    tempLowPrice = kDatas.get(x).better_low_price;
                }
            }
        } else {
            for (int x = kDatas.size() - 1; x >= kDatas.size() - AppConstants.K_DISPLAY_SHOW_COUNT; x--) {
                if (tempHeihtPrice < kDatas.get(x).better_height_price) {
                    tempHeihtPrice = kDatas.get(x).better_height_price;
                }
                if (tempLowPrice > kDatas.get(x).better_low_price) {
                    tempLowPrice = kDatas.get(x).better_low_price;
                }

            }
        }

        if(kDatas.size()>0){
            listTime = kDatas.get(kDatas.size()-1).list_time;
        }

        setMaxAndMinValue(tempHeihtPrice, tempLowPrice);
    }

    public void setMaxAndMinValue(float maxValue, float minValue) {
        pricef.maxValue = maxValue;
        pricef.minValue = minValue;
        invalidate();
    }

    /**
     * 十字交叉点
     */
    public void setTouchPoint(PointF touchPoint) {
        this.touchPoint = touchPoint;
        invalidate();
    }

    /**
     * 飞滑效果
     */
    public void fling(int velocityX) {
        mScroller.fling(getScrollX(), 0, velocityX, 0, 0, (int) (checkBorderRight - borderRect.right), 0, 0);
        invalidate();
    }

    @Override
    public void computeScroll() {
        if (mScroller.computeScrollOffset()) {
            scrollTo(mScroller.getCurrX(), 0);
            invalidate();
        }
    }


    @Override
    public void scrollTo(int x, int y) {

        /***
         * 如果展示的小于一屏幕 则不需要滚动
         */
        if (checkBorderRight < borderRect.right) {
            return;
        }

        if (x > (checkBorderRight - borderRect.right)) {
            x = (int) (checkBorderRight - borderRect.right);
        }
        caculateValue(x);
        /**
         * 与成交量联动
         */
        MlsEventBus.postMainThread(
                MlsEventBus.K_CHART_VOLUME_VOLUEM_SCROLL_TO,
                x);
        super.scrollTo(x, y);
    }

    /**
     * 滑动 计算价格最大值和最小值
     */
    int locationStart = 0;
    int locationEnd = 0;

    /**
     * 计算区间最大价格与最低价格
     */
    private void caculateValue(int deviationX) {

        if (kDatas != null && kDatas.size() > 0) {

            for (int i = 0; i < kDatas.size(); i++) {
                float tempValue = kDatas.get(i).getScreenXLocation();
                if (tempValue > borderRect.left + deviationX) {
                    locationStart = i;
                    break;
                }
            }

            locationEnd = (int) (locationStart + AppConstants.K_DISPLAY_SHOW_COUNT);

            float tempHeightValue = 0;
            float tempLowValue = 0;
            int count = locationEnd <= kDatas.size() ? locationEnd : kDatas.size();
            int y = locationStart;
            tempLowValue = kDatas.get(y).better_low_price;

            for (; y < count; y++) {

                if (tempHeightValue < kDatas.get(y).better_height_price) {
                    tempHeightValue = kDatas.get(y).better_height_price;
                }
                if (tempLowValue > kDatas.get(y).better_low_price) {
                    tempLowValue = kDatas.get(y).better_low_price;
                }
            }

            setMaxAndMinValue(tempHeightValue, tempLowValue);
        }
    }

    /**
     * 绘制加载中
     *
     * @param canvas
     */

    public void drawLoading(Canvas canvas) {
        Paint mPaint = getAxisYtitlePaintFont();
        float loadingTextWidth = mPaint.measureText(loadingText);

        canvas.drawText(loadingText, borderRect.left - loadingTextWidth, (borderRect.bottom - borderRect.top) / 2 + discrptionSize / 2, mPaint);
    }

    public void setLoadingText(String loadingText) {
        this.loadingText = loadingText;
        invalidate();
    }

    public void setAxisYLineCount(int axisYLineCount) {
        this.axisYLineCount = axisYLineCount;
    }

    public void setMinValueColor(int minValueColor) {
        this.minValueColor = minValueColor;
    }

    public void setIsAbleScroll(boolean isAbleScroll) {
        this.isAbleScroll = isAbleScroll;
    }


    public String getListTime() {
        return listTime;
    }

    public void setListTime(String listTime) {
        this.listTime = listTime;
    }


    @Override
    public void onConfigurationChanged(Configuration newConfig) {

        oneflag = true;
        super.onConfigurationChanged(newConfig);
    }

    public void setMaxValueColor(int maxValueColor) {
        this.maxValueColor = maxValueColor;
    }

    public int getCurrentArrayLeftLocation() {
        return currentArrayLeftLocation;
    }

    public void setCurrentArrayLeftLocation(int currentArrayLeftLocation) {
        oneflag = true;
        this.currentArrayLeftLocation = currentArrayLeftLocation;
    }

    public void setkVolumeAxisYWidth(int kVolumeAxisYWidth) {
        this.kVolumeAxisYWidth = kVolumeAxisYWidth;
        invalidate();
    }

    public int kVolumeAxisYWidth = 0;

    public void onEventMainThread(MlsEventBus.MainEvent event) {
        if (event != null && MlsEventBus.K_CHART_VOLUME_VOLUEM_CALLBACK_WIDTH.equals(event.eventDes)) {
            if (event.eventObject instanceof Integer) {

                setkVolumeAxisYWidth((int) event.eventObject);
            }
        }
    }

    kChartToBottomListener toBottomListener;

    public void setkChartBottomListener(kChartToBottomListener toBottomListener) {
        this.toBottomListener = toBottomListener;
    }

    public interface kChartToBottomListener {
        void theBottom();
    }

    public KLineListener kLineListener;

    public void setOnKLineListener(KLineListener kLineListener) {
        this.kLineListener = kLineListener;
    }

    public interface KLineListener {
        /***
         *  最新价  成交量
         * @param latest_price
         * @param volume
         */
         void KCallBack(float latest_price, long volume);
    }


}
