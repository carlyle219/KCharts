package com.yilong.charts;

import android.graphics.Point;

public class PriceF {
    public float maxValue;
    public float minValue;

    public PriceF() {
    }

    public PriceF(float maxValue, float minValue) {
        this.maxValue = maxValue;
        this.minValue = minValue;
    }

    public PriceF(Point pricePoint) {
        this.maxValue = pricePoint.x;
        this.maxValue = pricePoint.y;
    }

    public float getMinValue() {
        return minValue;
    }

    public void setMinValue(float minValue) {
        this.minValue = minValue;
    }

    public float getMaxValue() {
        return maxValue;
    }

    public void setMaxValue(float maxValue) {
        this.maxValue = maxValue;
    }
}
