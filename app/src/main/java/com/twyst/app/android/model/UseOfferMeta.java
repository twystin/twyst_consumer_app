package com.twyst.app.android.model;

import java.io.Serializable;

/**
 * Created by rahuls on 19/8/15.
 */
public class UseOfferMeta implements Serializable{

    private String offer;

    private String type;

    private String coupon;

    public String getCoupon() {
        return coupon;
    }

    public void setCoupon(String coupon) {
        this.coupon = coupon;
    }

    public String getOffer() {
        return offer;
    }

    public void setOffer(String offer) {
        this.offer = offer;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
