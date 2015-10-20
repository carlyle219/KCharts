package com.yilong.charts;

/**
 * Created by MLS on 15/9/22.
 */
public class AppConstants {

    /***
     * 分时线价格更新
     */
    public final static int MINUTE_UPDATE_PRICE = 10000;

    /***
     * 分时线成交量更新
     */
    public final static int MINUTE_STICK_UPDATE_PRICE = 10001;


    /**
     * 分时线的十指交叉线和交易量联动
     */
    public final static String MINUTEUPDATECROSSXVALUE = "com.yilong.charts.minute_update_cross_x_value";


    /**
     * K线交易量左边距离
     */
    public final static String K_CHART_VOLUME_LEFT_AXISY_TITLE_WIDTH = "com.yilong.charts.k_chart_volume_left_axisy_title_width";

    /**
     * k线交易量y轴十字交叉线x轴联动
     */
    public final static String K_CHART_VOLUME_CROSS_X = "com.yilong.charts.k_chart_volume_cross_x";

    /**
     * K线成交量联动
     */
    public final static String K_CHART_VOLUME_VOLUEM_SCROLL_TO = "com.yilong.charts.k_chart_volume_scroll_to";
    /***
     *  k线 一屏幕展示多少个k线
     */
    public static float K_DISPLAY_SHOW_COUNT = 60f;


    /***
     *  分时线 一屏幕展示多少个k线
     */
    public static float MINUTE_DISPLAY_SHOW_COUNT = 119f;

}
