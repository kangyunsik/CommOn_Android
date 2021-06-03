package com.example.test;

import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class CustomTask_place extends AsyncTask<String, Void, ArrayList<Place>> {
    String server_ip = MapsActivity.server_ip;
    int server_port = 8080;

    String sendMsg, receiveMsg, check;

    @Override
    // doInBackground의 매개변수 값이 여러개일 경우를 위해 배열로
    protected ArrayList<Place> doInBackground(String... strings) {
        return getPlaces(Double.parseDouble(strings[0]), Double.parseDouble(strings[1]), strings[2], strings[3]);
    }

    public ArrayList<Place> getPlaces(double latitude, double longitude, String keywords, String radius) {
        MyUtil myUtil = new MyUtil();
        String http = "https://maps.googleapis.com/maps/api/place/textsearch/json?query=" + keywords + "&location=" + latitude + "," + longitude + "&language=ko&key=" + "AIzaSyCZcuJZlFBCjWt5tbg4IeAFBSn3qom8mEo" +
                "&radius=" + radius;
        String receivedMsg = "";
        String str;
        URL url;

        ArrayList<Place> places = new ArrayList<>();

        try {
            url = new URL(http);

            HttpURLConnection conn = (HttpURLConnection) url.openConnection();

            if (conn.getResponseCode() == HttpURLConnection.HTTP_OK) {
                InputStreamReader tmp;
                tmp = new InputStreamReader(conn.getInputStream(), StandardCharsets.UTF_8);
                BufferedReader reader = new BufferedReader(tmp);
                StringBuilder buffer = new StringBuilder();
                while ((str = reader.readLine()) != null) {
                    buffer.append(str);
                }
                receivedMsg = buffer.toString();

                reader.close();
            } else {
                Log.i("통신 결과",conn.getResponseCode()+"에러");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            JSONObject jsonObject = new JSONObject(receivedMsg);

            JSONArray resultsArray = (JSONArray) jsonObject.get("results");

            for (int i = 0; i < resultsArray.length(); i++) {
                Place place = new Place();
                JSONObject resultsObject = (JSONObject) resultsArray.get(i);

                place.setLocation((String) resultsObject.get("formatted_address"));

                JSONObject geometryObject = (JSONObject) resultsObject.get("geometry");
                JSONObject locationObject = (JSONObject) geometryObject.get("location");

                place.setLatitude((double) locationObject.get("lat"));
                place.setLongitude((double) locationObject.get("lng"));
                place.setName((String) resultsObject.get("name"));

                try {
                    place.setRating((double) resultsObject.get("rating"));
                } catch (java.lang.ClassCastException e) {
                    place.setRating(0);
                }


                if (myUtil.getDist(place, latitude, longitude) < Double.parseDouble(radius))
                    places.add(place);

            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        places.sort((f1, f2) -> Double.compare(f2.getRating(), f1.getRating()));

        return places;
    }
}
