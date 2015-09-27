package com.twyst.app.android.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by rahuls on 20/7/15.
 */
public class UsedDetail implements Serializable{

    @SerializedName("used_phone")
    private String usedPhone;

    @SerializedName("used_at")
    private String usedAt;

    @SerializedName("used_by")
    private String usedBy;

    @SerializedName("usedTime")
    private String used_time;

    public String getUsedPhone() {
        return usedPhone;
    }

    public void setUsedPhone(String usedPhone) {
        this.usedPhone = usedPhone;
    }

    public String getUsedAt() {
        return usedAt;
    }

    public void setUsedAt(String usedAt) {
        this.usedAt = usedAt;
    }

    public String getUsedBy() {
        return usedBy;
    }

    public void setUsedBy(String usedBy) {
        this.usedBy = usedBy;
    }

    public String getUsed_time() {
        return used_time;
    }

    public void setUsed_time(String used_time) {
        this.used_time = used_time;
    }
}
