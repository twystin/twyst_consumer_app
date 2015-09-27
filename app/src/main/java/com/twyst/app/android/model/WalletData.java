package com.twyst.app.android.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by rahuls on 10/8/15.
 */
public class WalletData implements Serializable{

    @SerializedName("coupons")
    private ArrayList<Outlet> outlets = new ArrayList<>();

    @SerializedName("twyst_bucks")
    private String twystBucks;

    public String getTwystBucks() {
        return twystBucks;
    }

    public void setTwystBucks(String twystBucks) {
        this.twystBucks = twystBucks;
    }

    public ArrayList<Outlet> getOutlets() {
        return outlets;
    }

    public void setOutlets(ArrayList<Outlet> outlets) {
        this.outlets = outlets;
    }
}
