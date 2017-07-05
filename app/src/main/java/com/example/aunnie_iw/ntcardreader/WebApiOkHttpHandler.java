package com.example.aunnie_iw.ntcardreader;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Created by Aunnie-IW on 26/6/2560.
 */

public class WebApiOkHttpHandler {
    public WebApiOkHttpHandler() {
    }
    public JSONArray GetHTTPData(String... strings)
    {
        try {
            JSONObject a = new JSONObject();
            //URL url = new URL("http://api.ttrs.caas.in.th/api.php");
            if(strings[0].equals("dataprovince")) {
                a.put("controller", "data");
                a.put("action", "dataprovince");
            }
            else if (strings[0].equals("dataamphur")){
                a.put("controller", "data");
                a.put("action", "dataamphur");
                a.put("province_id",strings[1]);
            }
            else if (strings[0].equals("datadistrict")){
                a.put("controller", "data");
                a.put("action", "datadistrict");
                a.put("province_id", strings[1]);
                a.put("amphur_id", strings[2]);
            }
            // 1.Creaste okHttp Client object
            OkHttpClient client = new OkHttpClient();
            Log.d("1", "doInBackground: ");

            // 2.Define request being sent to the server
            RequestBody postData = new FormBody.Builder()
                    //.add("type","json")
                    .add("appkey","7VDhAhLpqLQtzp3T" +
                            "")
                    .add("params",a.toString())
                    .build();
            Log.d("2", "doInBackground: ");
            //
            Request request = new Request.Builder()
                    .url("http://api.ttrs.caas.in.th/api.php")
                    .post(postData)
                    //.addHeader("content-type", "application/json; charset=utf-8")
                    .build();
            Log.d("3", "doInBackground: ");
            //3. Transpory the requset and wait for response to process next

            Response response = client.newCall(request).execute();
            if (response.isSuccessful()){
                Log.d("Success", "doInBackground: ");
                JSONObject b = new JSONObject(response.body().string());
                //String result = response.body().string();

                //Log.d(result, "doInBackground: ");
                //Log.d("4", "doInBackground: ");
                //Log.d(response.body().string(), "doInBackground: ");
                System.out.println(b.toString());
                Log.d("sssssssssss", "doInBackground: ");
               // JSONArray arr = new JSONArray();
                //b.getJSONArray("province_name");
                JSONArray arr = b.getJSONArray("data");
               // arr.put(b);
                List<String> list = new ArrayList<String>();
                System.out.println(arr.toString());
                if(strings[0].equals("dataprovince")) {
                    for(int i = 0; i < arr.length(); i++){
                        list.add(arr.getJSONObject(i).getString("province_name"));
                    }
                }

                else if (strings[0].equals("dataamphur")){
                    for(int i = 0; i < arr.length(); i++){
                        list.add(arr.getJSONObject(i).getString("amphur_name"));
                    }
                }
                else if (strings[0].equals("datadistrict")){
                    for(int i = 0; i < arr.length(); i++){
                        list.add(arr.getJSONObject(i).getString("district_name"));
                    }
                }
                String[] stockArr = new String[list.size()];
                stockArr = list.toArray(stockArr);
                for(String s : stockArr)
                    System.out.println(s);
                return arr;

            }else{
                Log.d("Not Success", "doInBackground: ");
                return null;
               // return "Not Success - code :"+ response.code();
            }
        }catch (Exception e) {
            Log.d("eeee", "doInBackground: ");
            Log.e(e.getMessage(), "doInBackground: ");
            e.printStackTrace();
            return null;
        }


    }
}
