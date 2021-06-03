package com.example.test;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

public class GeoUtil {
    static HashMap<String,String> saved = new HashMap<>();

    public String getCurrentAddress(Context context,String... strings) {
        String address;
        if((address = saved.get(strings[0]+"/"+strings[1]))!=null){
            Log.d("GEOCODER","SAVED."+address);
            return address;
        }


        LatLng latlng = new LatLng(Double.parseDouble(strings[0]),Double.parseDouble(strings[1]));
        //지오코더... GPS를 주소로 변환
        Geocoder geocoder = new Geocoder(context, Locale.getDefault());

        List<Address> addresses;

        Log.d("GEOCODER"," "+latlng.latitude+"/"+latlng.longitude);
        try {

            addresses = geocoder.getFromLocation(
                    latlng.latitude,
                    latlng.longitude,
                    1);
        } catch (IOException ioException) {
            //네트워크 문제
            Log.d("GEOCODER",latlng.latitude+"/"+latlng.longitude);
            Toast.makeText(context, "지오코더 서비스 사용불가", Toast.LENGTH_LONG).show();
            return "지오코더 서비스 사용불가";
        } catch (IllegalArgumentException illegalArgumentException) {
            Toast.makeText(context, "잘못된 GPS 좌표", Toast.LENGTH_LONG).show();
            return "잘못된 GPS 좌표";
        }

        if (addresses == null || addresses.size() == 0) {
            //Toast.makeText(context, "주소 미발견", Toast.LENGTH_LONG).show();
            return "주소 미발견";

        } else {
            saved.put(strings[0]+"/"+strings[1],addresses.get(0).getAddressLine(0));
            return addresses.get(0).getAddressLine(0);
        }
    }
}
