package com.example.test;

import android.util.Log;

import com.google.android.gms.maps.model.Marker;

import java.util.ArrayList;

public class MyUtil {
    final double standard = 0.00000001;

    public double getBestDist(Marker marker, ArrayList<Marker> landmarks) {
        double dist = Double.MAX_VALUE, temp;

        for(Marker m : landmarks){
            if(getAbsDist(marker,m) > standard ){
                temp = getAbsDist(marker,m);
                Log.d("랜드마크temp",temp+"");
                dist = dist > temp ? temp : dist;
            }
        }
        return dist;
    }

    public double getDist(Place p, double latitude, double longitude){
        return getAbsDist(p.getLatitude(),p.getLongitude(),latitude,longitude);
    }

    public ArrayList<FriendsViewItem> getDiffViewItem(ArrayList<FriendsViewItem> before, ArrayList<FriendsViewItem> after){
        ArrayList<FriendsViewItem> changed = new ArrayList<>();
        Log.d("디프"," before size : "+before.size() +" after size : " + after.size());
        for(int i=0;i<before.size();i++){
            Log.d("디프", "i : "+i + " bc : " + before.get(i).isChecked() + " ac : " + after.get(i).isChecked());
            if(before.get(i).isChecked() != after.get(i).isChecked()){
                changed.add(after.get(i));
            }
        }
        Log.d("디프",changed.size()+"");
        return changed;
    }

    public double getAbsDist(double lat1, double long1, double lat2, double long2){
        return Math.abs(lat1-lat2) + Math.abs(long1-long2);
    }

    private double getAbsDist(Marker m1, Marker m2){
        return Math.abs(m1.getPosition().latitude - m2.getPosition().latitude) + Math.abs(m1.getPosition().longitude - m2.getPosition().longitude);
    }

}
