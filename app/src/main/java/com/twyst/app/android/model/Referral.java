package com.twyst.app.android.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by rahuls on 19/8/15.
 */
public class Referral implements Serializable {
    @SerializedName("event_meta")
    private ReferralMeta referralMeta;

    public ReferralMeta getReferralMeta() {
        return referralMeta;
    }

    public void setReferralMeta(ReferralMeta referralMeta) {
        this.referralMeta = referralMeta;
    }

}
