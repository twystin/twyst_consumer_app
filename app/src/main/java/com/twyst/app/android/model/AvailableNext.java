package com.twyst.app.android.model;

import java.io.Serializable;

/**
 * Created by rahuls on 4/8/15.
 */
public class AvailableNext implements Serializable{

    private String day;

    private String time;

    public String getDay() {
        return day;
    }

    public void setDay(String day) {
        this.day = day;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }
}
