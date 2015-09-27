package com.twyst.app.android.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by rahuls on 5/8/15.
 */
public class Voucher implements Serializable{

    @SerializedName("event_meta")
    private VoucherMeta voucherMeta;

    public VoucherMeta getVoucherMeta() {
        return voucherMeta;
    }

    public void setVoucherMeta(VoucherMeta voucherMeta) {
        this.voucherMeta = voucherMeta;
    }

    public static class VoucherMeta implements Serializable{
        @SerializedName("offer")
        private String offer;

        public String getOffer() {
            return offer;
        }

        public void setOffer(String offer) {
            this.offer = offer;
        }

        @SerializedName("date")
        private String date;


        public String getDate() {
            return date;
        }

        public void setDate(String date) {
            this.date = date;
        }
    }
}
