package com.twyst.app.android.model;

import com.google.gson.annotations.SerializedName;

/**
 * Created by rahuls on 3/8/15.
 */
public class SubmitOffer {

    @SerializedName("event_outlet")
    private String outletId;

    @SerializedName("event_meta")
    private SubmitOfferMeta submitOfferMeta;

    public String getOutletId() {
        return outletId;
    }

    public void setOutletId(String outletId) {
        this.outletId = outletId;
    }

    public SubmitOfferMeta getSubmitOfferMeta() {
        return submitOfferMeta;
    }

    public void setSubmitOfferMeta(SubmitOfferMeta submitOfferMeta) {
        this.submitOfferMeta = submitOfferMeta;
    }
}
