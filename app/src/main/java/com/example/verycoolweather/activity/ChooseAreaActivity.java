package com.example.verycoolweather.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.verycoolweather.R;
import com.example.verycoolweather.db.VeryCoolWeatherDB;
import com.example.verycoolweather.model.City;
import com.example.verycoolweather.model.County;
import com.example.verycoolweather.model.Province;
import com.example.verycoolweather.util.BaseActivity;
import com.example.verycoolweather.util.HttpCallbackListener;
import com.example.verycoolweather.util.HttpUtil;
import com.example.verycoolweather.util.Utility;

import java.util.ArrayList;
import java.util.List;


public class ChooseAreaActivity extends BaseActivity {

    public static final int LEVEL_PROVINCE=0;
    public static final int LEVEL_CITY=1;
    public static final int LEVEL_COUNTY=2;

    ProgressDialog progressDialog;
    TextView title_tv;
    ListView areaList_lv;
    ArrayAdapter<String> adapter;
    VeryCoolWeatherDB veryCoolWeatherDB;
    List<String> dataList=new ArrayList<String>();

    /**
     *
     * 省列表
     */
    List<Province> provinceList;
    /**
     * 城市列表
     */
    List<City> cityList;
    /**
     * 县列表
     */
    List<County> countyList;
    /**
     * 选中的省
     */
    Province selectedProvince;
    /**
     * 选中的城市
     */
    City selectedCity;
//    /**
//     * 选中的县
//     */
//    County selectedCounty;
    /**
     * 当前选中的级别
     */
    int currentLevel;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        SharedPreferences preferences= PreferenceManager.getDefaultSharedPreferences(this);
        if(preferences.getBoolean("city_selected",false))
        {
            Intent intent=new Intent(this,WeatherActivity.class);
            startActivity(intent);
            finish();
            return;
        }

        setContentView(R.layout.activity_choose_area);

        areaList_lv=(ListView)findViewById(R.id.arealist_lv);
        title_tv=(TextView)findViewById(R.id.title_tv);
        adapter=new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,dataList);
        areaList_lv.setAdapter(adapter);
        veryCoolWeatherDB=VeryCoolWeatherDB.getInstance();
        areaList_lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if(currentLevel==LEVEL_PROVINCE)
                {
                    selectedProvince=provinceList.get(position);
                    queryCities();
                }
                else if(currentLevel==LEVEL_CITY)
                {
                    selectedCity=cityList.get(position);
                    queryCounties();
                }
                else if(currentLevel==LEVEL_COUNTY)
                {
                    String countyCode=countyList.get(position).getCountyCode();
                    Intent intent=new Intent(ChooseAreaActivity.this,WeatherActivity.class);
                    intent.putExtra("county_code",countyCode);
                    startActivity(intent);
                    finish();
                }
            }
        });
        queryProvinces();
    }

    /**
     * 查询全国所有省，优先从数据库获得，如果没有再从服务器上查询
     */
    void queryProvinces()
    {
        provinceList=veryCoolWeatherDB.loadProvinces();
        if(provinceList.size()>0)
        {
            dataList.clear();
            for(Province province:provinceList)
            {
                dataList.add(province.getProvinceName());
            }
            adapter.notifyDataSetChanged();
            areaList_lv.setSelection(0);
            title_tv.setText("中国");
            currentLevel=LEVEL_PROVINCE;
        }
        else
            queryFromServer(null,"province");
    }
    /**
     * 查询全国所有城市，优先从数据库获得，如果没有再从服务器上查询
     */
    void queryCities()
    {
        cityList=veryCoolWeatherDB.loadCities(selectedProvince.getId());
        if(cityList.size()>0)
        {
            dataList.clear();
            for(City city:cityList)
            {
                dataList.add(city.getCityName());
            }
            adapter.notifyDataSetChanged();
            areaList_lv.setSelection(0);
            title_tv.setText(selectedProvince.getProvinceName());
            currentLevel=LEVEL_CITY;
        }
        else
            queryFromServer(selectedProvince.getProvinceCode(),"city");
    }
    /**
     * 查询全国所有县，优先从数据库获得，如果没有再从服务器上查询
     */
    void queryCounties()
    {
        countyList=veryCoolWeatherDB.loadCounties(selectedCity.getId());
        if(countyList.size()>0)
        {
            dataList.clear();
            for(County county:countyList)
            {
                dataList.add(county.getCountyName());
            }
            adapter.notifyDataSetChanged();
            areaList_lv.setSelection(0);
            title_tv.setText(selectedCity.getCityName());
            currentLevel=LEVEL_COUNTY;
        }
        else
            queryFromServer(selectedCity.getCityCode(),"county");
    }

    /**
     *传入代号和类型，从服务器上下载数据
     * @param code
     * @param type
     */
    void queryFromServer(final String code,final String type)
    {
        String address;
        if(!TextUtils.isEmpty(code))
        {
            address="http://www.weather.com.cn/data/list3/city"+code+".xml";
        }
        else
        {
            address="http://www.weather.com.cn/data/list3/city.xml";
        }
        showProgressDialog();
        HttpUtil.sendHttpRequest(address,new HttpCallbackListener() {
            @Override
            public void onFinish(String response) {
                boolean result=false;
                if("province".equals(type))
                {
                    result= Utility.handleProvincesResponse(veryCoolWeatherDB,response);
                }
                else if("city".equals(type))
                {
                    result=Utility.handleCitiesResponse(veryCoolWeatherDB,response,selectedProvince.getId());
                }
                else if("county".equals(type))
                {
                    result=Utility.handleCountiesResponse(veryCoolWeatherDB,response,selectedCity.getId());
                }
                if(result)
                {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            closeProgressDialog();
                            if("province".equals(type))
                            {
                                queryProvinces();
                            }
                            else if("city".equals(type))
                            {
                                queryCities();
                            }
                            else if("county".equals(type))
                            {
                                queryCounties();
                            }
                        }
                    });
                }
            }

            @Override
            public void onError(Exception e) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        closeProgressDialog();
                        Toast.makeText(ChooseAreaActivity.this,"加载失败",Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }

    /**
     * 显示进度条
     */
    void showProgressDialog()
    {
        if(progressDialog==null)
        {
            progressDialog=new ProgressDialog(this);
            progressDialog.setMessage("正在加载中...");
            progressDialog.setCanceledOnTouchOutside(false);
        }
        progressDialog.show();
    }
    /**
     * 关闭进度条
     */
    void closeProgressDialog()
    {
        if(progressDialog!=null)
        {
            progressDialog.dismiss();
        }
    }

    /**
     * 捕获BACK按键，根据级别来判断是返回到省或市，或者退出
     */
    @Override
    public void onBackPressed() {
        //super.onBackPressed();
        if(currentLevel==LEVEL_COUNTY)
        {
            queryCities();
        }
        else if(currentLevel==LEVEL_CITY)
        {
            queryProvinces();
        }
        else {
            finish();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_choose_area, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
