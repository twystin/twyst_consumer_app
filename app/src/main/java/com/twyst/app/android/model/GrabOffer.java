package com.twyst.app.android.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by Vipul Sharma on 9/22/2015.
 */
public class GrabOffer implements Serializable {

    @SerializedName("event_meta")
    private GrabOfferMeta grabOfferMeta;

    @SerializedName("event_outlet")
    private String outletID;

    public GrabOfferMeta getGrabOfferMeta() {
        return grabOfferMeta;
    }

    public void setGrabOfferMeta(GrabOfferMeta grabOfferMeta) {
        this.grabOfferMeta = grabOfferMeta;
    }

    public String getOutletID() {
        return outletID;
    }

    public void setOutletID(String outletID) {
        this.outletID = outletID;
    }

    public static class GrabOfferMeta implements Serializable {

        @SerializedName("code")
        private String code;

        public String getCode() {
            return code;
        }

        public void setCode(String code) {
            this.code = code;
        }
    }
}
