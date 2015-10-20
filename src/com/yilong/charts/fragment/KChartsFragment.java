package com.yilong.charts.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.meilishuo.gson.Gson;
import com.yilong.charts.Model.KLineModel;
import com.yilong.charts.R;
import com.yilong.charts.touchlayout.KchartsTouchLinearLayout;
import com.yilong.charts.utils.HttpUtils;
import com.yilong.charts.view.KChartView;

import java.util.ArrayList;
import java.util.Collections;

/**
 * Created by zhkqy on 15/9/16.
 */
public class KChartsFragment extends Fragment implements KChartView.kChartToBottomListener {

    private String dayKUrl = "http://www.cpt001.com/Mobile.php?s=/TimeData/Day/type/1/code/100001/pagenow/";   //日k数据
    private Gson gson;
    ArrayList<KLineModel.KData> arrayList = new ArrayList<KLineModel.KData>();
    ArrayList<KLineModel.KData> reverseArrays = new ArrayList<KLineModel.KData>();
    KchartsTouchLinearLayout kchartsTouch;
    KChartView.KLineListener lineListener;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return initView(inflater);
    }

    private View initView(LayoutInflater inflater) {

        View v = inflater.inflate(R.layout.fragment_k_charts, null);
        gson = new Gson();
        arrayList.clear();
        reverseArrays.clear();
        kchartsTouch = (KchartsTouchLinearLayout) v.findViewById(R.id.k_chart_touch);
        kchartsTouch.setkChartBottomListener(this);

        getDayKData(page);

        return v;
    }

    int page = 1;

    private void getDayKData(int page) {

        HttpUtils.kAsyncHttpClientPost(getActivity(), dayKUrl + (page + ""), new HttpUtils.RequestListener() {
                    @Override
                    public void onException(Throwable error) {

                    }

                    @Override
                    public void onCompleted(int statusCode, String response) {
//
                        KLineModel kLineModel = gson.fromJsonWithNoException(response, KLineModel.class);

                        if (kLineModel != null && kLineModel.data != null && kLineModel.data.size() > 0) {

                            Collections.reverse(kLineModel.data);
                            arrayList = new ArrayList<KLineModel.KData>();
                            arrayList.addAll(kLineModel.data);
                            arrayList.addAll(reverseArrays);
                            if (reverseArrays.size() > 0) {
                                //不是第一次加载  需要设置滚动位置
                                kchartsTouch.setCurrentArrayLeftLocation(kLineModel.data.size());
                            }
                            kchartsTouch.setKData(arrayList);
                            reverseArrays = arrayList;

                        }

                    }
                }
        );
    }

    @Override
    public void theBottom() {

        getDayKData(++page);

    }

    public void setOnKLineListener(KChartView.KLineListener lineListener) {
        this.lineListener = lineListener;
        kchartsTouch.setOnKLineListener(lineListener);
    }

}
