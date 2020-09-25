package com.jiyehoo.easydmkj;

import com.google.gson.Gson;
import com.jiyehoo.easydmkj.json.ActInfo;

import org.json.JSONException;

public class Utility {
    //获取JSON主的实体类，加载到ActInfo
    public static ActInfo handleActInfo(String jsonStr) {
        try {
            org.json.JSONObject jsonObject = new org.json.JSONObject(jsonStr);
            String stringObject = jsonObject.toString();
            return new Gson().fromJson(stringObject, ActInfo.class);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }
}
