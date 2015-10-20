package com.yilong.charts.Model;

import com.meilishuo.gson.annotations.SerializedName;

import java.util.ArrayList;


/**
 * Created by MLS on 15/9/17.
 */
public class KLineModel {

    @SerializedName("result")
    public String result;

    @SerializedName("descrption")
    public Descrption descrption;


    @SerializedName("data")
    public ArrayList<KData> data;


    public class KData {

        @SerializedName("volume")
        public long volume;
        @SerializedName("rise_and_decline")
        public String rise_and_decline;
        @SerializedName("list_time")
        public String list_time;

        @SerializedName("most_expensive")  //最高价
        public float better_height_price;

        @SerializedName("lowest_price")   //最低价
        public float better_low_price;

        @SerializedName("latest_price")   //收盘价
        public float close_price;

        @SerializedName("today_ope")  //开盘价
        public float open_price;


        public KData(float better_height_price, float better_low_price, float close_price, float open_price) {
            this.better_height_price = better_height_price;
            this.better_low_price = better_low_price;
            this.close_price = close_price;
            this.open_price = open_price;

        }

        /**
         * k线屏幕x轴位置
         */
        float screenXLocation = 0;

        /**
         * 比例缩放后的高度（最大值）
         */
        float scaleClosePrice;

        public float getScreenXLocation() {
            return screenXLocation;
        }

        public void setKScreenXLocation(float screenXLocation) {
            this.screenXLocation = screenXLocation;
        }

        public float getScaleClosePrice() {
            return scaleClosePrice;
        }

        public void setScaleClosePriceValue(float scaleClosePrice) {
            this.scaleClosePrice = scaleClosePrice;
        }
    }

    public class Descrption {

        @SerializedName("Highestprice")
        public float Highestprice;
        @SerializedName("Lowestprice")
        public float Lowestprice;
        @SerializedName("closed_yesterday")
        public float closed_yesterday;
        @SerializedName("today_ope")
        public float today_ope;

    }

}
