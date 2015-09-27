package com.twyst.app.android.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by rahuls on 29/7/15.
 */
public class Feedback implements Serializable{

    @SerializedName("event_outlet")
    private String outletId;

    @SerializedName("event_meta")
    private FeedbackMeta feedbackMeta;

    public String getOutletId() {
        return outletId;
    }

    public void setOutletId(String outletId) {
        this.outletId = outletId;
    }

    public FeedbackMeta getFeedbackMeta() {
        return feedbackMeta;
    }

    public void setFeedbackMeta(FeedbackMeta feedbackMeta) {
        this.feedbackMeta = feedbackMeta;
    }

}
