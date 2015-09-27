package com.twyst.app.android.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by rahuls on 12/8/15.
 */
public class DiscoverData implements Serializable{

    @SerializedName("outlets")
    private ArrayList<Outlet> outlets;

    @SerializedName("twyst_bucks")
    private String twystBucks;

    public ArrayList<Outlet> getOutlets() {
        return outlets;
    }

    public void setOutlets(ArrayList<Outlet> outlets) {
        this.outlets = outlets;
    }

    public String getTwystBucks() {
        return twystBucks;
    }

    public void setTwystBucks(String twystBucks) {
        this.twystBucks = twystBucks;
    }
}
