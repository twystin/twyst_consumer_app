package com.twyst.app.android.model;

import java.io.Serializable;

/**
 * Created by Vipul Sharma on 10/13/2015.
 */
public class LocationOffline implements Serializable{
    private double latitude;
    private double longitude;
    private long timeStamp;

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public long getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(long timeStamp) {
        this.timeStamp = timeStamp;
    }
}
