package com.twyst.app.android.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by rahuls on 12/8/15.
 */
public class OutletDetailData implements Serializable{

    @SerializedName("outlet")
    private Outlet outlet;

    @SerializedName("twyst_bucks")
    private String twystBucks;

    public String getTwystBucks() {
        return twystBucks;
    }

    public void setTwystBucks(String twystBucks) {
        this.twystBucks = twystBucks;
    }

    public Outlet getOutlet() {
        return outlet;
    }

    public void setOutlet(Outlet outlet) {
        this.outlet = outlet;
    }
}
