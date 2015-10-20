package com.yilong.charts.view;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.yilong.charts.R;

public class ChartTabView extends LinearLayout implements View.OnClickListener {

    Context mContext;
    OnmClickListener listener;

    public ChartTabView(Context context) {
        super(context);
        initView(context);
    }

    public ChartTabView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initView(context);
    }

    public ChartTabView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(context);
    }

    TextView minute;
    TextView dayK;
    TextView weekK;
    TextView monthK;
    View minuteRed;
    View dayRed;
    View weekRed;
    View monthRed;

    protected void initView(Context context) {
        this.mContext = context;

        View v = View.inflate(mContext, R.layout.view_charts_tab, null);
        minute = (TextView) v.findViewById(R.id.text_minute);
        dayK = (TextView) v.findViewById(R.id.text_day_K);
        weekK = (TextView) v.findViewById(R.id.text_week_K);
        monthK = (TextView) v.findViewById(R.id.text_month_K);

        minuteRed = v.findViewById(R.id.minute_red);
        dayRed = v.findViewById(R.id.day_red);
        weekRed = v.findViewById(R.id.week_red);
        monthRed = v.findViewById(R.id.month_red);

        minute.setOnClickListener(this);
        dayK.setOnClickListener(this);
        weekK.setOnClickListener(this);
        monthK.setOnClickListener(this);

        addView(v);
        switchTab(0);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.text_minute:
                switchTab(0);
                callbackPosition(0);
                break;
            case R.id.text_day_K:
                switchTab(1);
                callbackPosition(1);
                break;

            case R.id.text_week_K:
                switchTab(2);
                callbackPosition(2);
                break;

            case R.id.text_month_K:
                switchTab(3);
                callbackPosition(3);
                break;
        }
    }

    private void callbackPosition(int position) {
        if (listener != null) {
            listener.OnClickedPosition(position);
        }
    }

    private void switchTab(int position) {

        minuteRed.setVisibility(View.GONE);
        dayRed.setVisibility(View.GONE);
        weekRed.setVisibility(View.GONE);
        monthRed.setVisibility(View.GONE);

        minute.setBackgroundColor(Color.parseColor("#4CCCC2C9"));
        dayK.setBackgroundColor(Color.parseColor("#4CCCC2C9"));
        weekK.setBackgroundColor(Color.parseColor("#4CCCC2C9"));
        monthK.setBackgroundColor(Color.parseColor("#4CCCC2C9"));

        switch (position) {
            case 0:
                minuteRed.setVisibility(View.VISIBLE);
                minute.setBackgroundColor(Color.parseColor("#ffffff"));
                break;
            case 1:
                dayRed.setVisibility(View.VISIBLE);
                dayK.setBackgroundColor(Color.parseColor("#ffffff"));
                break;
            case 2:
                weekRed.setVisibility(View.VISIBLE);
                weekK.setBackgroundColor(Color.parseColor("#ffffff"));
                break;
            case 3:
                monthRed.setVisibility(View.VISIBLE);
                monthK.setBackgroundColor(Color.parseColor("#ffffff"));
                break;
        }

    }

    public void mSetOnclickListener(OnmClickListener listener) {
        this.listener = listener;

    }

    public interface OnmClickListener {

        void OnClickedPosition(int position);
    }

}
