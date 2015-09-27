package com.twyst.app.android.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by anilkg on 27/8/15.
 */
public class ReportProblem  implements Serializable{


    @SerializedName("event_outlet")
    private String eventOutlet;

    @SerializedName("event_meta")
    private EventMeta eventMeta;

    public String getEventOutlet() {
        return eventOutlet;
    }

    public void setEventOutlet(String eventOutlet) {
        this.eventOutlet = eventOutlet;
    }

    public EventMeta getEventMeta() {
        return eventMeta;
    }

    public void setEventMeta(EventMeta eventMeta) {
        this.eventMeta = eventMeta;
    }
}
