package com.example.verycoolweather.util;

/**
 * Created by ZJGJK03 on 2014/11/10.
 */
public interface HttpCallbackListener {
    void onFinish(String response);
    void onError(Exception e);
}
