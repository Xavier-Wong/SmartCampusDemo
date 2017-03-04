package com.example.xavier.smartcampusdemo.Util;

import org.json.JSONArray;
import org.json.JSONObject;

/**
 * Created by Xavier on 3/3/2017.
 *
 */

public class JSONUtil {

    public static JSONObject getJsonObject(String resp){
        try {
            return new JSONObject(resp);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static JSONObject[] getJsonObjects(String response){
        try {
            JSONArray jsonArray = new JSONArray(response);
            JSONObject[] jsonObjects = new JSONObject[jsonArray.length()];
            for(int i = 0;i<jsonArray.length();i++){
                jsonObjects[i] = jsonArray.getJSONObject(i);
            }
            return jsonObjects;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
