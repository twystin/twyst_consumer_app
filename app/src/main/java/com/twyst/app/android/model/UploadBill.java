package com.twyst.app.android.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by rahuls on 6/8/15.
 */
public class UploadBill implements Serializable{

    @SerializedName("event_outlet")
    private String outletId;

    @SerializedName("event_meta")
    private UploadBillMeta uploadBillMeta;

    public String getOutletId() {
        return outletId;
    }

    public void setOutletId(String outletId) {
        this.outletId = outletId;
    }

    public UploadBillMeta getUploadBillMeta() {
        return uploadBillMeta;
    }

    public void setUploadBillMeta(UploadBillMeta uploadBillMeta) {
        this.uploadBillMeta = uploadBillMeta;
    }
}
