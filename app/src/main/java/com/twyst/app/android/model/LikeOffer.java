package com.twyst.app.android.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by rahuls on 3/8/15.
 */
public class LikeOffer implements Serializable{

    @SerializedName("event_meta")
    private LikeOfferMeta offerMeta;

    public LikeOfferMeta getOfferMeta() {
        return offerMeta;
    }

    public void setOfferMeta(LikeOfferMeta offerMeta) {
        this.offerMeta = offerMeta;
    }
}
