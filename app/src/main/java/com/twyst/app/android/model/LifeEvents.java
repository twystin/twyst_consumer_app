package com.twyst.app.android.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by Vipul Sharma on 9/28/2015.
 */
public class LifeEvents implements Serializable {

    @SerializedName("birthday")
    private EventDate birthdayDate;

    @SerializedName("anniversary")
    private EventDate anniversaryDate;

    public EventDate getBirthdayDate() {
        return birthdayDate;
    }

    public void setBirthdayDate(EventDate birthdayDate) {
        this.birthdayDate = birthdayDate;
    }

    public EventDate getAnniversaryDate() {
        return anniversaryDate;
    }

    public void setAnniversaryDate(EventDate anniversaryDate) {
        this.anniversaryDate = anniversaryDate;
    }
}
