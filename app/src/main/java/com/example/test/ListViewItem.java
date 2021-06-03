package com.example.test;

import android.graphics.drawable.Drawable;

public class ListViewItem {
    private Drawable icon;
    private String id;
    private String location;

    public void setIcon(Drawable icon) {
        this.icon = icon;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public String getLocation() {
        return location;
    }

    public Drawable getIcon() {
        return this.icon;
    }


}