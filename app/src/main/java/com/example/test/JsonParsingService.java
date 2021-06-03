package com.example.test;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class JsonParsingService {
    private ArrayList<Landmark> landmarkArrayList;
    private ArrayList<FriendsViewItem> friendsViewItems;
    private MyUtil myUtil;

    public JsonParsingService() {
        landmarkArrayList = new ArrayList<Landmark>();
        friendsViewItems = new ArrayList<FriendsViewItem>();
        myUtil = new MyUtil();
    }

    public ArrayList<Landmark> getLandmarkArrayList() {
        return landmarkArrayList;
    }

    public ArrayList<FriendsViewItem> getFriendsViewItems() {
        return friendsViewItems;
    }

    public Landmark getCenterLandMark(){
        if(landmarkArrayList == null || landmarkArrayList.size() == 0)
            return null;

        double mid_lati = 0.0;
        double mid_longi = 0.0;

        for(Landmark l : landmarkArrayList){
            mid_lati += l.getLatitude();
            mid_longi += l.getLongitude();
        }
        mid_lati /= landmarkArrayList.size();
        mid_longi /= landmarkArrayList.size();

        Landmark l = landmarkArrayList.get(0);

        for(Landmark temp : landmarkArrayList){
            l = myUtil.getAbsDist(l.getLatitude(),l.getLongitude(),mid_lati,mid_longi) >  myUtil.getAbsDist(temp.getLatitude(),temp.getLongitude(),mid_lati,mid_longi) ? temp : l;
        }
        return l;
    }

    public ArrayList<FriendsInfo> locationParsing(String json) {
        ArrayList<FriendsInfo> friendsInfoList = new ArrayList<FriendsInfo>();
        json = json.replaceFirst("null","");
        try {
            JSONObject jsonObject = new JSONObject(json);

            JSONArray movieArray = jsonObject.getJSONArray("data");

            for (int i = 0; i < movieArray.length(); i++) {
                JSONObject movieObject = movieArray.getJSONObject(i);

                FriendsInfo friendsInfo = new FriendsInfo();

                friendsInfo.setId(movieObject.getString("ident"));
                friendsInfo.setLatitude(movieObject.getDouble("latitude"));
                friendsInfo.setLongitude(movieObject.getDouble("longitude"));
                friendsInfo.setUpdated(movieObject.getInt("diff"));

                friendsInfoList.add(friendsInfo);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return friendsInfoList;
    }

    public void landmarkParsing(String json) {
        landmarkArrayList = new ArrayList<>();
        try {
            landmarkArrayList = new ArrayList<>();
            JSONObject jsonObject = new JSONObject(json);

            JSONArray movieArray = jsonObject.getJSONArray("data");

            for (int i = 0; i < movieArray.length(); i++) {
                JSONObject movieObject = movieArray.getJSONObject(i);

                Landmark landmark = new Landmark();

                landmark.setNum(movieObject.getInt("num"));
                landmark.setLatitude(movieObject.getDouble("latitude"));
                landmark.setLongitude(movieObject.getDouble("longitude"));
                landmark.setTimeList(movieObject.getString("timeList"));
                System.out.println(landmark.getTimeList());

                landmarkArrayList.add(landmark);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void friendsParsing(String json) {
        friendsViewItems = new ArrayList<FriendsViewItem>();
        try {
            JSONObject jsonObject = new JSONObject(json);

            JSONArray movieArray = jsonObject.getJSONArray("data");

            for (int i = 0; i < movieArray.length(); i++) {
                JSONObject movieObject = movieArray.getJSONObject(i);

                FriendsViewItem fvi = new FriendsViewItem();

                fvi.setId(movieObject.getString("tuser"));
                if(movieObject.getInt("reveal") == 1)
                    fvi.setChecked(true);
                else
                    fvi.setChecked(false);

                friendsViewItems.add(fvi);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
