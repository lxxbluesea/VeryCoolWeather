package com.example.verycoolweather.util;

import android.text.TextUtils;

import com.example.verycoolweather.db.VeryCoolWeatherDB;
import com.example.verycoolweather.model.City;
import com.example.verycoolweather.model.County;
import com.example.verycoolweather.model.Province;

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
}
