package com.example.test;

public class Landmark {
    private int num;
    private double latitude;
    private double longitude;
    private String location;
    private String name;
    private String timeList;

    public int getNum() {
        return num;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public String getTimeList() {
        return timeList;
    }

    public void setNum(int num) {
        this.num = num;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public void setTimeList(String timeList) { this.timeList = timeList; }
}
