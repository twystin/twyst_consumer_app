package com.twyst.app.android.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by rahuls on 17/8/15.
 */
public class WriteToUs implements Serializable{

    @SerializedName("event_meta")
    private WriteMeta writeMeta;

    public WriteMeta getWriteMeta() {
        return writeMeta;
    }

    public void setWriteMeta(WriteMeta writeMeta) {
        this.writeMeta = writeMeta;
    }
}
