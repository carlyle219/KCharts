package com.yilong.charts;

/**
 * 比较类
 */
public class CompareResult {

    float maxValue = 0;
    int maxWidthCount = 0;

    public CompareResult(float maxValue, int maxWidthCount) {
        this.maxValue = maxValue;
        this.maxWidthCount = maxWidthCount;
    }

    public float getMaxValue() {
        return maxValue;
    }

    public void setMaxValue(float maxValue) {
        this.maxValue = maxValue;
    }

    public int getMaxWidthCount() {
        return maxWidthCount;
    }

    public void setMaxWidthCount(int maxWidthCount) {
        this.maxWidthCount = maxWidthCount;
    }
}