package com.yilong.charts.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.yilong.charts.R;

/**
 * Created by zhkqy on 15/9/16.
 */
public class TestChartsFragment extends Fragment {


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return initView(inflater);
    }

    private View initView(LayoutInflater inflater) {
        View v = inflater.inflate(R.layout.fragment_test_charts, null);

        return v;
    }


}
