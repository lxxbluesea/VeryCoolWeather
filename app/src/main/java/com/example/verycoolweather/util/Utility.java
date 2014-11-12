package com.example.verycoolweather.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.text.TextUtils;

import com.example.verycoolweather.db.VeryCoolWeatherDB;
import com.example.verycoolweather.model.City;
import com.example.verycoolweather.model.County;
import com.example.verycoolweather.model.Province;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.prefs.PreferenceChangeEvent;

/**
 * Created by ZJGJK03 on 2014/11/10.
 */
public class Utility {
    public synchronized static boolean handleProvincesResponse(VeryCoolWeatherDB veryCoolWeatherDB,String response)
    {
        if(!TextUtils.isEmpty(response))
        {
            String[] allProvinces=response.split(",");
            if(allProvinces!=null && allProvinces.length>0)
            {
                for (String p :allProvinces)
                {
                    String[] array=p.split("\\|");
                    Province province=new Province();
                    province.setProvinceCode(array[0]);
                    province.setProvinceName(array[1]);
                    //存到数据库的Province表中
                    veryCoolWeatherDB.saveProvince(province);
                }
                return true;
            }
        }
        return false;
    }

    public synchronized static boolean handleCitiesResponse(VeryCoolWeatherDB veryCoolWeatherDB,String response,int provinecId)
    {
        if(!TextUtils.isEmpty(response))
        {
            String[] allCities=response.split(",");
            if(allCities!=null && allCities.length>0)
            {
                for (String c :allCities)
                {
                    String[] array=c.split("\\|");
                    City city=new City();
                    city.setCityCode(array[0]);
                    city.setCityName(array[1]);
                    city.setProvinceId(provinecId);
                    //存到数据库的City表中
                    veryCoolWeatherDB.saveCity(city);
                }
                return true;
            }
        }
        return false;
    }

    public synchronized static boolean handleCountiesResponse(VeryCoolWeatherDB veryCoolWeatherDB,String response,int cityId)
    {
        if(!TextUtils.isEmpty(response))
        {
            String[] allCounties=response.split(",");
            if(allCounties!=null && allCounties.length>0)
            {
                for (String c :allCounties)
                {
                    String[] array=c.split("\\|");
                    County city=new County();
                    city.setCountyCode(array[0]);
                    city.setCountyName(array[1]);
                    city.setCityId(cityId);
                    //存到数据库的County表中
                    veryCoolWeatherDB.saveCounty(city);
                }
                return true;
            }
        }
        return false;
    }

    /**
     * 解析服务器返回的JSON，并将结果存储在本地
     * @param response
     */
    public static void handleWeatherResponse(Context context,String response)
    {
        try
        {
            JSONObject jsonObject=new JSONObject(response);
            JSONObject weatherInfo=jsonObject.getJSONObject("weatherinfo");
            String cityName=weatherInfo.getString("city");
            String weatherCode=weatherInfo.getString("cityid");
            String temp1=weatherInfo.getString("temp1");
            String temp2=weatherInfo.getString("temp2");
            String weatherDesp=weatherInfo.getString("weather");
            String publishTime=weatherInfo.getString("ptime");
            saveWeatherInfo(context,cityName,weatherCode,temp1,temp2,weatherDesp,publishTime);

        }
        catch (JSONException e)
        {
            e.printStackTrace();
        }
    }

    /**
     * 将所有天气信息存储在ShardPreferences文件中
     * @param cityName
     * @param weatherCode
     * @param temp1
     * @param temp2
     * @param weatherDesp
     * @param publishTime
     */
    public static void saveWeatherInfo(Context context,String cityName,String weatherCode,String temp1,String temp2,String weatherDesp,String publishTime)
    {
        SimpleDateFormat simpleDateFormat=new SimpleDateFormat("yyyy年M月d日");
        SharedPreferences.Editor editor= PreferenceManager.getDefaultSharedPreferences(context).edit();
        editor.putBoolean("city_selected",true);
        editor.putString("city_name", cityName);
        editor.putString("weather_code", weatherCode);
        editor.putString("temp1", temp1);
        editor.putString("temp2", temp2);
        editor.putString("weather_desp", weatherDesp);
        editor.putString("publish_time", publishTime);
        editor.putString("current_date", simpleDateFormat.format(new Date()));
        editor.commit();

    }
}
