package com.example.verycoolweather.model;

/**
 * Created by ZJGJK03 on 2014/11/10.
 */
public class County {
    int id,cityId;
    String countName,countCode;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getCityId() {
        return cityId;
    }

    public void setCityId(int cityId) {
        this.cityId = cityId;
    }

    public String getCountName() {
        return countName;
    }

    public void setCountName(String countName) {
        this.countName = countName;
    }

    public String getCountCode() {
        return countCode;
    }

    public void setCountCode(String countCode) {
        this.countCode = countCode;
    }
}
