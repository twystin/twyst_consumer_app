package com.twyst.app.android.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by satishk on 8/6/15.
 */
public class Meta implements Serializable {

    @SerializedName("reward_type")
    private String rewardType;

    public String getRewardType() {
        return rewardType;
    }

    public void setRewardType(String rewardType) {
        this.rewardType = rewardType;
    }
}
