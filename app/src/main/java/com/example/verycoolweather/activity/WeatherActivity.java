package com.example.verycoolweather.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.verycoolweather.R;
import com.example.verycoolweather.util.BaseActivity;
import com.example.verycoolweather.util.HttpCallbackListener;
import com.example.verycoolweather.util.HttpUtil;
import com.example.verycoolweather.util.LogUtil;
import com.example.verycoolweather.util.Utility;

/**
 * Created by ZJGJK03 on 2014/11/11.
 */
public class WeatherActivity extends BaseActivity {
    LinearLayout weatherInfo_Layout;
    TextView cityName_tv,publishText_tv,weatherDesp_tv,temp1_tv,temp2_tv,currentDate_tv;
    Button switchCity_bt,refreshWeather_bt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weather);
        /**
         * 初始化控件
         */
        weatherInfo_Layout=(LinearLayout)findViewById(R.id.weather_info_layout);
        cityName_tv=(TextView)findViewById(R.id.city_name_tv);
        publishText_tv=(TextView)findViewById(R.id.publish_text_tv);
        weatherDesp_tv=(TextView)findViewById(R.id.weather_desp_tv);
        temp1_tv=(TextView)findViewById(R.id.temp1_tv);
        temp2_tv=(TextView)findViewById(R.id.temp2_tv);
        currentDate_tv=(TextView)findViewById(R.id.current_date_tv);
        switchCity_bt=(Button)findViewById(R.id.switch_city_bt);
        refreshWeather_bt=(Button)findViewById(R.id.refresh_weather_bt);

        String countyCode=getIntent().getStringExtra("county_code");
        if(!TextUtils.isEmpty(countyCode))
        {
            publishText_tv.setText("同步中...");
            weatherInfo_Layout.setVisibility(View.INVISIBLE);
            cityName_tv.setVisibility(View.INVISIBLE);
            queryWeatherCode(countyCode);
        }
        else
        {
            showWeather();
        }

        switchCity_bt.setOnClickListener(listener);
        refreshWeather_bt.setOnClickListener(listener);

    }
    View.OnClickListener listener=new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId())
            {
                case R.id.switch_city_bt:
                    Intent intent=new Intent(WeatherActivity.this,ChooseAreaActivity.class);
                    intent.putExtra("from_weather_activity",true);
                    startActivity(intent);
                    finish();
                    break;
                case R.id.refresh_weather_bt:
                    publishText_tv.setText("同步中...");
                    SharedPreferences preferences= PreferenceManager.getDefaultSharedPreferences(WeatherActivity.this);
                    String weatherCode=preferences.getString("weather_code","");
                    if(!TextUtils.isEmpty(weatherCode))
                    {
                        queryWeatherInfo(weatherCode);
                    }
                    break;
                default:
                    break;
            }
        }
    };

    /**
     *
     * @param countyCode
     */
    void queryWeatherCode(String countyCode)
    {
        String address="http://www.weather.com.cn/data/list3/city"+countyCode+".xml";
        LogUtil.d("WeatherActivity",address);
        queryFromServer(address,"countyCode");
    }

    /**
     *
     * @param weatherCode
     */
    void queryWeatherInfo(String weatherCode)
    {
        String address="http://www.weather.com.cn/data/cityinfo/"+weatherCode+".html";
        LogUtil.d("WeatherActivity",address);
        queryFromServer(address,"weatherCode");
    }

    /**
     *
     * @param address
     * @param type
     */
    void queryFromServer(final String address,final String type)
    {
        HttpUtil.sendHttpRequest(address,new HttpCallbackListener() {
            @Override
            public void onFinish(String response) {
                if("countyCode".equals(type))
                {
                    if(!TextUtils.isEmpty(response))
                    {
                        String[] array=response.split("\\|");
                        if(array!=null && array.length==2)
                        {
                            String weatherCode=array[1];
                            queryWeatherInfo(weatherCode);
                        }
                    }
                }
                else if("weatherCode".equals(type))
                {
                    Utility.handleWeatherResponse(WeatherActivity.this,response);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            showWeather();
                        }
                    });
                }
            }

            @Override
            public void onError(Exception e) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        publishText_tv.setText("同步失败");
                    }
                });
            }
        });
    }

    /**
     *
     */
    void showWeather()
    {
        SharedPreferences preferences=PreferenceManager.getDefaultSharedPreferences(this);
        cityName_tv.setText(preferences.getString("city_name",""));
        LogUtil.d("WeatherActivity",cityName_tv.getText().toString());
        temp1_tv.setText(preferences.getString("temp1",""));
        temp2_tv.setText(preferences.getString("temp2",""));
        weatherDesp_tv.setText(preferences.getString("weather_desp",""));
        publishText_tv.setText("今天"+preferences.getString("publish_time","")+"发布");
        currentDate_tv.setText(preferences.getString("current_date",""));
        weatherInfo_Layout.setVisibility(View.VISIBLE);
        cityName_tv.setVisibility(View.VISIBLE);
    }
}
