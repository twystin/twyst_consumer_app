package com.twyst.app.android.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by Vipul Sharma on 9/28/2015.
 */
public class LifeEvents implements Serializable {
    public static final String BIRTHDAY = "birthday";
    public static final String ANNIVERSARY = "anniversary";
    public static final int TYPES_COUNT = 2;

    @SerializedName("event_date")
    private EventDate eventDate;

    @SerializedName("event_type")
    private String eventType;

    @SerializedName("event_meta")
    private String eventMeta;

    public EventDate getEventDate() {
        return eventDate;
    }

    public void setEventDate(EventDate eventDate) {
        this.eventDate = eventDate;
    }

    public String getEventType() {
        return eventType;
    }

    public void setEventType(String eventType) {
        this.eventType = eventType;
    }

    public String getEventMeta() {
        return eventMeta;
    }

    public void setEventMeta(String eventMeta) {
        this.eventMeta = eventMeta;
    }
}
