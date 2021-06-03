package com.example.test;

public class FriendsInfo {
    private String id;
    private double latitude;
    private double longitude;
    private int updated;

    public String getId() {
        return id;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public int getUpdated() {
        return updated;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public void setUpdated(int updated) {
        this.updated = updated;
    }
}
