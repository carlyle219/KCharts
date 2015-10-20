package com.yilong.charts.Model;

import com.meilishuo.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;


/**
 * Created by MLS on 15/9/17.
 */
public class MinuteModel implements Serializable {

    @SerializedName("result")
    public String result;

    @SerializedName("descrption")
    public Descrption descrption;

    @SerializedName("data")
    public ArrayList<MinuteData> data;

    public class MinuteData implements Serializable {
        @SerializedName("latest_price")
        public float latest_price;

        @SerializedName("volume")
        public long volume;

        @SerializedName("Averageline")
        public float Averageline;

        @SerializedName("list_time")
        public String list_time;

        @SerializedName("lowest_price")
        public float lowest_price;

        @SerializedName("rise_and_decline")
        public float rise_and_decline;

        /**
         * 比例缩放后的高度（最大值）
         */

        float scaleLatestPrice;

        public float getScaleLatestPrice() {
            return scaleLatestPrice;
        }

        public void setScaleLatestPrice(float scaleLatestPrice) {
            this.scaleLatestPrice = scaleLatestPrice;
        }

        /**
         * k线屏幕x轴位置
         */
        float screenXLocation = 0;


        public float getScreenXLocation() {
            return screenXLocation;
        }

        public void setKScreenXLocation(float screenXLocation) {
            this.screenXLocation = screenXLocation;
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
