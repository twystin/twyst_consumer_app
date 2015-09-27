package com.twyst.app.android.model;

import android.text.TextUtils;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by rahuls on 20/7/15.
 */
public class LocationData implements Serializable{

    @SerializedName("location_1")
    private String location1;

    @SerializedName("location_2")
    private String location2;

    @SerializedName("City")
    private String city;

    @SerializedName("lat")
    private String lat;

    @SerializedName("long")
    private String lng;

    private double distance;

    public double getDistance() {
        return distance;
    }

    public void setDistance(double distance) {
        this.distance = distance;
    }

    public String getLocation1() {
        return location1;
    }

    public void setLocation1(String location1) {
        this.location1 = location1;
    }

    public String getLocation2() {
        return location2;
    }

    public void setLocation2(String location2) {
        this.location2 = location2;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getLat() {
        return lat;
    }

    public void setLat(String lat) {
        this.lat = lat;
    }

    public String getLng() {
        return lng;
    }

    public void setLng(String lng) {
        this.lng = lng;
    }

    @Override
    public String toString() {
        String s = "";
        boolean added = false;
        if (!TextUtils.isEmpty(getLocation1())) {
            s = getLocation1();
            added = true;
        }

        if (!TextUtils.isEmpty(getLocation2())) {
            if (added) {
                s = s + ", ";
            }
            s = s + getLocation2();
            added = true;
        }

        if (!TextUtils.isEmpty(getCity())) {
            if (added) {
                s = s + ", ";
            }
            s = s + getCity();
        }

        return s;
    }
}
