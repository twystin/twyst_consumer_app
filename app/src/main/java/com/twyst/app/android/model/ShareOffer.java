package com.twyst.app.android.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by rahuls on 3/8/15.
 */
public class ShareOffer implements Serializable{
    @SerializedName("event_outlet")
    private String outletId;

    @SerializedName("event_meta")
    private ShareOfferData shareOfferData;

    public String getOutletId() {
        return outletId;
    }

    public void setOutletId(String outletId) {
        this.outletId = outletId;
    }

    public ShareOfferData getShareOfferData() {
        return shareOfferData;
    }

    public void setShareOfferData(ShareOfferData shareOfferData) {
        this.shareOfferData = shareOfferData;
    }
}
