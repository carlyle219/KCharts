package com.yilong.charts.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.meilishuo.gson.Gson;
import com.yilong.charts.Model.MinuteModel;
import com.yilong.charts.R;
import com.yilong.charts.touchlayout.MinuteTouchLinearLayout;
import com.yilong.charts.utils.HttpUtils;
import com.yilong.charts.view.NewMinuteChartView;
import com.yilong.charts.view.VolumeChartView;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by zhkqy on 15/9/16.
 */
public class MinuteChartsFragment extends Fragment {

    private String minuteUrl = "http://www.cpt001.com/appjson/First/time_Fenshi.php?code=601004&type=1";  //分时数据
    MinuteTouchLinearLayout touchLinearLayout;
    Gson gson;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return initView(inflater);
    }

    private View initView(LayoutInflater inflater) {

        View v = inflater.inflate(R.layout.fragment_minute_charts, null);
        touchLinearLayout = (MinuteTouchLinearLayout) v.findViewById(R.id.minute_toutch);


        gson = new Gson();

        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {

                if (getActivity() != null) {
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            getMinuteData();
                        }
                    });

                }
            }
        }, 500, 40000);

        return v;
    }


    public void setOnListener(MinuteTouchLinearLayout.MinuteLineListener lineListener) {
        if (touchLinearLayout != null) {
            touchLinearLayout.setOnMinuteListener(lineListener);
        }
    }

    /***
     * 获取分时线数据
     */
    private void getMinuteData() {
        HttpUtils.AsyncHttpClientPost(getActivity(), minuteUrl, new HttpUtils.RequestListener() {
                    @Override
                    public void onException(Throwable error) {

                    }

                    @Override
                    public void onCompleted(int statusCode, String response) {

                        MinuteModel minuteModel = gson.fromJsonWithNoException(response, MinuteModel.class);
                        if (minuteModel != null && minuteModel.data != null && minuteModel.data.size() > 0) {
                            touchLinearLayout.setMinuteData(minuteModel);

                        }
                    }
                }
        );
    }

}
