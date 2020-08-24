package com.bawp.womensafety.ui;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class JsonParser {
    private HashMap<String,String>parseJsonobject(JSONObject object) throws JSONException {
        //initialize hash map
        HashMap<String,String>dataList=new HashMap<>();
       //get name,lonitute,latitute
        try {
            String name = object.getString("name");

            String latitute = object.getJSONObject("geometry")
                    .getJSONObject("location").getString("lat");

            String longitute = object.getJSONObject("geometry")
                    .getJSONObject("location").getString("lng");

            //Put all value in hash map
            dataList.put("name", name);
            dataList.put("lat", latitute);
            dataList.put("lng",longitute);
        } catch (JSONException e) {
            e.printStackTrace();
        }

           return dataList;
    }
    private List<HashMap<String,String>>parseJsonArray(JSONArray jsonArray)
    {
        //initialize hash map list
        List<HashMap<String,String>> dataList=new ArrayList<>();
        for(int i=0;i<jsonArray.length();i++)
        {
            try {
                //initialize hash map
                HashMap<String,String>data=parseJsonobject((JSONObject)jsonArray.get(i));
                //add data to hash list
                dataList.add(data);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return  dataList;
    }

    public  List<HashMap<String,String>>parserResult(JSONObject object)
    {
        //initialize json array
        JSONArray jsonArray=null;
        //Get Result array
        try {
            jsonArray= object.getJSONArray("results");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        assert jsonArray != null;
        return parseJsonArray(jsonArray);
    }
}
