package com.yilong.charts;

import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.FrameLayout;
import android.widget.TextView;
import com.yilong.charts.fragment.KChartsFragment;
import com.yilong.charts.fragment.MinuteChartsFragment;
import com.yilong.charts.fragment.TestChartsFragment;
import com.yilong.charts.touchlayout.MinuteTouchLinearLayout;
import com.yilong.charts.view.ChartTabView;
import com.yilong.charts.view.KChartView;

import java.sql.RowId;
import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends FragmentActivity implements View.OnClickListener, MinuteTouchLinearLayout.MinuteLineListener, KChartView.KLineListener, ChartTabView.OnmClickListener {


    MinuteChartsFragment minuteChartsFragment;
    KChartsFragment kChartsFragment;
    TestChartsFragment testChartsFragment;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);//隐藏标题
        setContentView(R.layout.main);
        initView();
        initFragment();
        ChartTabView chartTabView = (ChartTabView) findViewById(R.id.chart_tabview);
        chartTabView.mSetOnclickListener(this);

        changeFragments(0);

        new Thread(new Runnable() {
            @Override
            public void run() {
                new Timer().schedule(new TimerTask() {
                    @Override
                    public void run() {

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                minuteChartsFragment.setOnListener(MainActivity.this);

                            }
                        });
                    }
                }, 1000);
            }
        }).start();

    }

    TextView today_open_price; //今开
    TextView lasest_price;  //最新价格
    TextView height_price;//最高价格
    TextView text_volume; //交易量
    TextView close_landscape;//关闭横屏

    private void initView() {
        today_open_price = (TextView) findViewById(R.id.today_open_price);
        lasest_price = (TextView) findViewById(R.id.lasest_price);
        height_price = (TextView) findViewById(R.id.height_price);
        text_volume = (TextView) findViewById(R.id.text_volume);
        close_landscape = (TextView) findViewById(R.id.close_landscape);
        close_landscape.setOnClickListener(this);

    }

    void initFragment() {
        minuteChartsFragment = new MinuteChartsFragment();
        kChartsFragment = new KChartsFragment();
        testChartsFragment = new TestChartsFragment();
    }

    private void changeFragments(int index) {

        String tag = getTagName(index);
        FragmentTransaction mTransaction = getSupportFragmentManager()
                .beginTransaction();
        if (getSupportFragmentManager().getFragments() != null)
            for (Fragment mFragment : getSupportFragmentManager()
                    .getFragments()) {
                if (mFragment.getTag() != null) {
                    if (mFragment.getTag().equals(tag)) {
                        if (mFragment.isAdded()) {
                            mTransaction.show(mFragment);
                        }
                    } else {
                        mTransaction.hide(mFragment);
                    }
                }
            }
        if (getSupportFragmentManager().findFragmentByTag(tag) == null) {
            if (tag != null && kChartsFragment != null && tag.equals(KChartsFragment.class.getSimpleName())
                    && !kChartsFragment.isAdded()) {
                mTransaction.add(R.id.frame_layout, kChartsFragment,
                        KChartsFragment.class.getSimpleName());
            } else if (tag != null && minuteChartsFragment != null && tag.equals(MinuteChartsFragment.class.getSimpleName())
                    && !minuteChartsFragment.isAdded()) {
                mTransaction.add(R.id.frame_layout, minuteChartsFragment,
                        MinuteChartsFragment.class.getSimpleName());
            } else if (tag != null && testChartsFragment != null && tag.equals(TestChartsFragment.class.getSimpleName())
                    && !testChartsFragment.isAdded()) {
                mTransaction.add(R.id.frame_layout, testChartsFragment,
                        TestChartsFragment.class.getSimpleName());
            }
        }
        mTransaction.commitAllowingStateLoss();
    }


    private String getTagName(int index) {
        String tag = MinuteChartsFragment.class.getSimpleName();
        switch (index) {
            case 0:
                tag = MinuteChartsFragment.class.getSimpleName();
                break;
            case 1:
                tag = KChartsFragment.class.getSimpleName();
                addKListener();
                break;
            case 2:
                tag = KChartsFragment.class.getSimpleName();
                addKListener();
                break;
            case 3:
                tag = KChartsFragment.class.getSimpleName();
                addKListener();
                break;
            default:
                break;
        }
        return tag;
    }


    public void addKListener() {

        new Thread(new Runnable() {
            @Override
            public void run() {
                new Timer().schedule(new TimerTask() {
                    @Override
                    public void run() {

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                kChartsFragment.setOnKLineListener(MainActivity.this);
                            }
                        });
                    }
                }, 1000);
            }
        }).start();
    }

    @Override
    public void minuteCallBack(float latest_price, long volume) {
        lasest_price.setText(latest_price + "");
        text_volume.setText("" + volume);

    }

    @Override
    public void KCallBack(float latest_price, long volume) {
        lasest_price.setText(latest_price + "");
        text_volume.setText("" + volume);
    }

    @Override
    public void onClick(View view) {

        switch (view.getId()) {
            case R.id.close_landscape:
                if (this.getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) { // 竖屏
                    this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                    close_landscape.setVisibility(View.GONE);
                }
                break;
        }
    }

    @Override
    public void OnClickedPosition(int position) {
        changeFragments(position);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {

        if (this.getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) { // 竖屏
            close_landscape.setVisibility(View.GONE);
        } else {
            close_landscape.setVisibility(View.VISIBLE);
        }
        super.onConfigurationChanged(newConfig);
    }
}