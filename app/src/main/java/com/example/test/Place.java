package com.example.test;

public class Place {
    private double latitude;
    private double longitude;
    private double rating;
    private String name;
    private String location;

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public double getRating() {
        return rating;
    }

    public String getName() {
        return name;
    }

    public String getLocation() {
        return location;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public void setRating(double rating) {
        this.rating = rating;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setLocation(String location) {
        this.location = location;
    }
}
