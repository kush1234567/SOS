package com.bawp.womensafety.model;

import android.app.Application;

public class womenSafetyApi extends Application {
    private String userId;
    private String username;
    private static womenSafetyApi instance;
    public static womenSafetyApi getInstance() {
        if (instance == null)
            instance =new womenSafetyApi();
        return instance;

    }
    public womenSafetyApi(){};

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
