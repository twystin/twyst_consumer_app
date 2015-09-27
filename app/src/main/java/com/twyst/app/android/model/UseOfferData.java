package com.twyst.app.android.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by rahuls on 21/8/15.
 */
public class UseOfferData implements Serializable{

    @SerializedName("twyst_bucks")
    private String bucks;

    @SerializedName("code")
    private String code;

    public String getBucks() {
        return bucks;
    }

    public void setBucks(String bucks) {
        this.bucks = bucks;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }
}
