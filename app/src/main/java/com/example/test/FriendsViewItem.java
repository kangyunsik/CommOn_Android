package com.example.test;

import android.graphics.drawable.Drawable;

public class FriendsViewItem {
    private Drawable icon;
    private String id;
    private boolean checked;

    public void setIcon(Drawable icon) {
        this.icon = icon;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setChecked(boolean checked) {
        this.checked = checked;
    }

    public String getId() {
        return id;
    }

    public Drawable getIcon() {
        return this.icon;
    }

    public boolean isChecked() {
        return checked;
    }
}