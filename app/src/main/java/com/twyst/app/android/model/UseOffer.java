package com.twyst.app.android.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by rahuls on 19/8/15.
 */
public class UseOffer implements Serializable{

    @SerializedName("event_outlet")
    private String eventOutlet;

    @SerializedName("event_meta")
    private UseOfferMeta useOfferMeta;

    public String getEventOutlet() {
        return eventOutlet;
    }

    public void setEventOutlet(String eventOutlet) {
        this.eventOutlet = eventOutlet;
    }

    public UseOfferMeta getUseOfferMeta() {
        return useOfferMeta;
    }

    public void setUseOfferMeta(UseOfferMeta useOfferMeta) {
        this.useOfferMeta = useOfferMeta;
    }
}
