package com.example.verycoolweather.util;

import android.app.Activity;
import android.os.Bundle;
import android.view.Window;

/**
 * Created by ZJGJK03 on 2014/11/10.
 */
public class BaseActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
    }
}
