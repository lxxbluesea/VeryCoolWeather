package com.example.verycoolweather.util;

import android.app.Application;
import android.content.Context;

/**
 * Created by ZJGJK03 on 2014/11/10.
 */
public class MyApplication extends Application {
    static Context context;//定义静态对象

    @Override
    public void onCreate() {
        super.onCreate();
        context=getApplicationContext();//初始化对象
    }
    //获取Context对象
    public static Context getContext()
    {
        return context;
    }
}
