package com.yilong.charts.utils;

import android.content.Context;
import android.graphics.Paint;
import android.text.TextPaint;
import com.yilong.charts.CompareResult;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.TreeMap;

/**
 * Created by zhkqy on 15/9/16.
 */
public class Utils {

    public static int dip2px(Context context, float dipValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dipValue * scale + 0.5f);
    }

    public static int px2dip(Context context, float pxValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (pxValue / scale + 0.5f);
    }


    public static int getStringHeight(String s, int textSize) {
        TextPaint textPaint = new TextPaint(Paint.ANTI_ALIAS_FLAG);
        textPaint.setTextSize(textSize);
        Paint.FontMetrics fontMetrics = textPaint.getFontMetrics();
        float fTop = fontMetrics.top;
        float fBottom = fontMetrics.bottom;
        return (int) (fBottom - fTop);
//        float textWidth = (int)textPaint.measureText(commentText.getText());
    }

    /**
     * 比较float类型的字符数
     *
     * @param maxValue
     * @param minValue
     * @return
     */
    public static CompareResult compareMaxStringWidthCount(float maxValue, float minValue) {
        String maxString = String.valueOf(maxValue);
        String minString = String.valueOf(minValue);

        if (maxString.length() > minString.length()) {
            return new CompareResult(maxValue, maxString.length());
        } else {
            return new CompareResult(minValue, minString.length());
        }

    }


    public static double formatMoney2(double money) {
        DecimalFormat df = new DecimalFormat("0.00");
        if (money < 0) {
            return 0;
        } else {
            return Double.parseDouble(df.format(roundMoney(Math.abs(money), 2)));
        }
    }

    /**
     * 对数字money小数点后保留newScale位 并且四舍五入
     *
     * @param money
     * @param newScale
     * @return
     */
    public static double roundMoney(double money, int newScale) {
        BigDecimal bg = new BigDecimal(money);
        return bg.setScale(newScale, BigDecimal.ROUND_HALF_UP).doubleValue();
    }


}
