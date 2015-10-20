package com.yilong.charts.view;

import android.content.Context;
import android.graphics.*;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import com.yilong.charts.CompareResult;
import com.yilong.charts.utils.Utils;

import java.util.List;

/**
 * Created by MLS on 15/9/11.
 */
public class GridChartView extends View {

    protected Context mContext;
    private boolean displayBorder = true;//显示边框
    private int borderColor = Color.GRAY;//边框颜色
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

    /**
     * 纬线字体大小及颜色
     */
    private int latitudeColor = Color.GRAY;
    private int latitudeFontColor = Color.GRAY;
    private int latitudeFontSize = 20;
    private float axisYTitleWidth = 0;  //价格title宽度
    protected int axisYLineCount = 0;  //y轴线条数量
    /**
     * 边框矩形
     */
    public Rect borderRect = new Rect();

    public long maxValue = -1;
    public long minValue = -1;


    /***
     * 最大值最小值颜色
     */
    private int maxValueColor = Color.parseColor("#60000000");
    private int minValueColor = Color.parseColor("#60000000");

    public GridChartView(Context context) {
        super(context);
        initView(context);
    }

    public GridChartView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initView(context);
    }

    public GridChartView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(context);
    }


    protected void initView(Context context) {
        this.mContext = context;
        longtitudeFontSize = Utils.dip2px(mContext, 13);
        latitudeFontSize = Utils.dip2px(mContext, 13);
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
        if (this.displayBorder) {
            drawBorder(canvas);
        }
        /**绘制y轴网格线*/
        drawAxisGridY(canvas);
        /**绘制y轴title*/
        drawAxisYTitle(canvas);
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
        canvas.drawLine(axisYTitleWidth + 2 + getScrollX(), 2, 2 + borderWidth + getScrollX(), 2, mPaint);
        canvas.drawLine(axisYTitleWidth + 2 + getScrollX(), borderHeight + 2, borderWidth + 2 + getScrollX(), borderHeight + 2, mPaint);
        canvas.drawLine(axisYTitleWidth + 2 + getScrollX(), 2, axisYTitleWidth + 2 + getScrollX(), borderHeight + 2, mPaint);
        canvas.drawLine(borderWidth + 2 + getScrollX(), 2, borderWidth + 2 + getScrollX(), borderHeight + 2, mPaint);
        borderRect.set((int) (axisYTitleWidth + 2), 2, borderWidth + 2, borderHeight + 2);

    }

    /**
     * 绘制y轴title
     */
    public void drawAxisYTitle(Canvas canvas) {
        if (maxValue >= 0 && minValue >= 0) {
            Paint mPaint = getAxisYtitlePaintFont();
            mPaint.setColor(Color.parseColor("#ffffff"));
            canvas.drawRect(getScrollX(), 0, getScrollX() + borderRect.left - 2, getHeight(), mPaint);
            mPaint.setColor(maxValueColor);
            float tempValue = mPaint.measureText(maxValue + "");
            canvas.drawText(maxValue + "", getScrollX() + axisYTitleWidth - tempValue, latitudeFontSize, mPaint);
            mPaint.setColor(minValueColor);
            tempValue = mPaint.measureText(minValue + "");
            canvas.drawText(minValue + "", getScrollX() + axisYTitleWidth - tempValue, borderRect.bottom, mPaint);
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
            float yOffset = (borderRect.bottom) / (axisYLineCount - 1);
            for (int i = 0; i < axisYLineCount; i++) {
                if (i > 0 && i < axisYLineCount - 1) {
                    // 绘制线条
                    canvas.drawLine(borderRect.left + getScrollX(), yOffset * i, borderRect.right + getScrollX(), yOffset * i,
                            mPaintLine);
                }
            }
        }
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

    /**
     * 十字坐标触摸点
     */
    protected PointF touchPoint = new PointF();


    public void setMaxAndMinValue(long maxValue, long minValue) {
        this.maxValue = maxValue;
        this.minValue = minValue;
        postInvalidate();
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

    public void setAxisYTitleWidth(float axisYTitleWidth) {
        this.axisYTitleWidth = axisYTitleWidth;
        invalidate();
    }
}
