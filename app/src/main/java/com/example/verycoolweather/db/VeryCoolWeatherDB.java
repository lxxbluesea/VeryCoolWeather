package com.example.verycoolweather.db;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.verycoolweather.model.City;
import com.example.verycoolweather.model.County;
import com.example.verycoolweather.model.Province;
import com.example.verycoolweather.util.MyApplication;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ZJGJK03 on 2014/11/10.
 */
public class VeryCoolWeatherDB {
    public static final int VERSION = 1;
    public static final String DB_NAME = "very_cool_weather";
    static VeryCoolWeatherDB veryCoolWeatherDB;
    SQLiteDatabase db;

    private VeryCoolWeatherDB() {
        VeryCoolWeatherOpenHelper dbhelper = new VeryCoolWeatherOpenHelper(MyApplication.getContext(), DB_NAME, null, VERSION);
        db = dbhelper.getWritableDatabase();
    }

    /**
     * 获取VeryCoolWeatherDb的实例
     */
    public synchronized static VeryCoolWeatherDB getInstance() {
        if (veryCoolWeatherDB == null) {
            veryCoolWeatherDB = new VeryCoolWeatherDB();
        }
        return veryCoolWeatherDB;
    }

    /**
     * 将Province实例存到数据库中
     */
    public void saveProvince(Province province) {
        if (province != null) {
            ContentValues values = new ContentValues();
            values.put("province_name", province.getProvinceName());
            values.put("province_code", province.getProvinceCode());
            db.insert("province", null, values);
        }
    }

    /**
     * 获取全国所有的省份
     */
    public List<Province> loadProvinces() {
        List<Province> list = new ArrayList<Province>();
        Cursor cursor = db.query("province", null, null, null, null, null, null);
        if (cursor.moveToFirst()) {
            do {
                Province province = new Province();
                province.setId(cursor.getInt(cursor.getColumnIndex("id")));
                province.setProvinceName(cursor.getString(cursor.getColumnIndex("province_name")));
                province.setProvinceCode(cursor.getString(cursor.getColumnIndex("province_code")));
                list.add(province);
            } while (cursor.moveToNext());
        }
        return list;
    }

    /**
     * 将City实例存到数据库
     */
    public void saveCity(City city) {
        if (city != null) {
            ContentValues values = new ContentValues();
            values.put("city_name", city.getCityName());
            values.put("city_code", city.getCityCode());
            values.put("province_id", city.getProvinceId());
            db.insert("city", null, values);
        }
    }

    /**
     * 读取某个省里所有的城市
     */
    public List<City> loadCities(int provinceId) {
        List<City> list = new ArrayList<City>();
        Cursor cursor = db.query("city", null, "province_id=?", new String[]{String.valueOf(provinceId)}, null, null, null);
        if (cursor.moveToFirst()) {
            do {
                City city = new City();
                city.setId(cursor.getInt(cursor.getColumnIndex("id")));
                city.setCityName(cursor.getString(cursor.getColumnIndex("city_name")));
                city.setCityCode(cursor.getString(cursor.getColumnIndex("city_code")));
                city.setProvinceId(provinceId);
                list.add(city);
            } while (cursor.moveToNext());
        }
        return list;
    }

    /**
     * 将County实例存到数据库
     */
    public void saveCounty(County county) {
        if (county != null) {
            ContentValues values = new ContentValues();
            values.put("county_name", county.getCountName());
            values.put("county_code", county.getCountCode());
            values.put("city_id", county.getCityId());
            db.insert("county", null, values);
        }
    }

    /**
     * 读取某个城市里所有的县
     */
    public List<County> loadCounties(int cityId) {
        List<County> list = new ArrayList<County>();
        Cursor cursor = db.query("county", null, "city_id=?", new String[]{String.valueOf(cityId)}, null, null, null);
        if (cursor.moveToFirst()) {
            do {
                County county = new County();
                county.setId(cursor.getInt(cursor.getColumnIndex("id")));
                county.setCountName(cursor.getString(cursor.getColumnIndex("county_name")));
                county.setCountCode(cursor.getString(cursor.getColumnIndex("county_code")));
                county.setCityId(cityId);
                list.add(county);
            } while (cursor.moveToNext());
        }
        return list;
    }

}
