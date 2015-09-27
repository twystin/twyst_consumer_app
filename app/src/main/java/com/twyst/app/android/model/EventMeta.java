package com.twyst.app.android.model;

import java.io.Serializable;

/**
 * Created by anilkg on 27/8/15.
 */
public class EventMeta implements Serializable {

    private String issue;
    private String offer;
    private String comment;

    public String getIssue() {
        return issue;
    }

    public void setIssue(String issue) {
        this.issue = issue;
    }

    public String getOffer() {
        return offer;
    }

    public void setOffer(String offer) {
        this.offer = offer;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }
}
