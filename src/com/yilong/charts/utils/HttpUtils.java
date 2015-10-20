package com.yilong.charts.utils;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import org.apache.http.Header;

/**
 * Created by zhkqy on 15/9/16.
 */
public class HttpUtils {

    /**
     * 采用AsyncHttpClient的Post方式进行实现
     */
    public static void AsyncHttpClientPost(final Context context, String url,final  RequestListener listener) {
        AsyncHttpClient client = new AsyncHttpClient();
        client.setTimeout(15000);
        RequestParams params = new RequestParams();
//        params.put("code", 100001); // 设置请求的参数名和参数值
//        params.put("type", 1);// 设置请求的参数名和参数
//        params.put("type", 1);// 设置请求的参数名和参数

        client.get(url, params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(final int statusCode, Header[] headers,
                                 final byte[] responseBody) {
                ((Activity) context).runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (listener != null) {
                            listener.onCompleted(statusCode, new String(responseBody));
                        }
                    }
                });
            }

            @Override
            public void onFailure(int statusCode, Header[] headers,
                                  byte[] responseBody,final Throwable error) {

                ((Activity) context).runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (listener != null) {
                            listener.onException(error);
                        }
                    }
                });

            }
        });
    }



    /**
     * 采用AsyncHttpClient的Post方式进行实现
     */
    public static void kAsyncHttpClientPost(final Context context, String url,final RequestListener listener) {
        AsyncHttpClient client = new AsyncHttpClient();
        client.setTimeout(15000);
        RequestParams params = new RequestParams();
//        params.put("code", 501001); // 设置请求的参数名和参数值
//        params.put("type", 1);// 设置请求的参数名和参数
        // 执行post方法

        client.get(url, params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(final int statusCode, Header[] headers,
                                 final byte[] responseBody) {
                ((Activity) context).runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (listener != null) {
                            listener.onCompleted(statusCode, new String(responseBody));
                        }
                    }
                });
            }

            @Override
            public void onFailure(int statusCode, Header[] headers,
                                  byte[] responseBody,final Throwable error) {

                ((Activity) context).runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (listener != null) {
                            listener.onException(error);
                        }
                    }
                });

            }
        });
    }

    public interface RequestListener {
        void onException(Throwable error);

        void onCompleted(int statusCode, String response);
    }
}
