package com.twyst.app.android.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by rahuls on 3/8/15.
 */
public class CheckinData implements Serializable{

    @SerializedName("event_meta")
    private CheckinCode checkinCode;

    public CheckinCode getCheckinCode() {
        return checkinCode;
    }

    public void setCheckinCode(CheckinCode checkinCode) {
        this.checkinCode = checkinCode;
    }
}
