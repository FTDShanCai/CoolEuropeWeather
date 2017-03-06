package slingge.cooleuropeweather.httpRequest;

import android.content.Context;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import slingge.cooleuropeweather.bean.WeatherDataBean.AQIBean;
import slingge.cooleuropeweather.bean.WeatherDataBean.Daily_forecastBean;
import slingge.cooleuropeweather.bean.WeatherDataBean.Hourly_forecastBean;
import slingge.cooleuropeweather.bean.WeatherDataBean.NowBean;
import slingge.cooleuropeweather.bean.WeatherDataBean.SuggestionBean;
import slingge.cooleuropeweather.bean.WeatherDataBean.WeatherBean;
import slingge.cooleuropeweather.util.abLog;

import static android.R.id.list;

/**
 * 获取天气信息
 * Created by Slingge on 2017/3/2 0002.
 */

public class WeatherHttp {

    private Context context;

    public WeatherHttp(Context context) {
        this.context = context;
    }

    
    public interface WeatherDataBackCall {
        void weathData(AQIBean aqiBean, List<Daily_forecastBean> dailyList, SuggestionBean suggeBean, NowBean nowBean,Hourly_forecastBean hourlyBean);
    }

    public WeatherDataBackCall weatherData;

    public void setWeatherDataBackCall(WeatherDataBackCall weatherData) {
        this.weatherData = weatherData;
    }


    public void weatherHttp(String city) {
        OkHttpUtils.get().url("https://api.heweather.com/x3/weather?").addParams("city", city).addParams("key", "a26e3b8650914bc6a429a6e035253cf5").
                build().execute(new StringCallback() {
            @Override
            public void onError(Call call, Exception e, int id) {

            }

            @Override
            public void onResponse(String response, int id) {
                try {
                    JSONObject obj = new JSONObject(response);
                    String res = obj.getString("HeWeather data service 3.0");
                    JSONArray array = new JSONArray(res);
                    for (int i = 0; i < array.length(); i++) {
                        obj = array.getJSONObject(i);
                        if (obj.getString("status").equals("ok")) {
                            Gson gson = new Gson();
                            AQIBean aqiBean = gson.fromJson(obj.getString("aqi"), AQIBean.class);
                            array = new JSONArray(obj.getString("daily_forecast"));
                            NowBean nowBean = gson.fromJson(obj.getString("now"), NowBean.class);
                            List<Daily_forecastBean> dailyList = new ArrayList<>();
                            for (i = 0; i < array.length(); i++) {
                                Daily_forecastBean dailyBean = gson.fromJson(array.getJSONObject(i).toString(), Daily_forecastBean.class);
                                dailyList.add(dailyBean);
                            }
                            JSONArray array1 = new JSONArray(obj.getString("hourly_forecast"));
                            Hourly_forecastBean hourlyBean = null;
                            for (i = 0; i < array1.length(); i++) {
                                hourlyBean = gson.fromJson(array1.getJSONObject(i).toString(), Hourly_forecastBean.class);
                            }

                            SuggestionBean suggeBean = gson.fromJson(obj.getString("suggestion"), SuggestionBean.class);
                            weatherData.weathData(aqiBean, dailyList, suggeBean, nowBean,hourlyBean);
                        }
                        abLog.e("天气信息", obj.toString());
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });

    }


}
