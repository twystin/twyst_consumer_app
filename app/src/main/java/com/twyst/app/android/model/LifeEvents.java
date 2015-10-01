package com.twyst.app.android.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by Vipul Sharma on 9/28/2015.
 */
public class LifeEvents implements Serializable {
    public static String TYPE_BDAY = "BDAY";
    public static String TYPE_ANNIVERSARY = "ANNIVERSARY";

    @SerializedName("event_type")
    private String eventType;

    @SerializedName("event_meta")
    private EventMetaData eventMeta;

    @SerializedName("event_date")
    private EventDate eventDate;

    public EventDate getEventDate() {
        return eventDate;
    }

    public void setEventDate(EventDate eventDate) {
        this.eventDate = eventDate;
    }

    public EventMetaData getEventMeta() {
        return eventMeta;
    }

    public void setEventMeta(EventMetaData eventMeta) {
        this.eventMeta = eventMeta;
    }

    public String getEventType() {
        return eventType;
    }

    public void setEventType(String eventType) {
        this.eventType = eventType;
    }

    public static class EventMetaData implements Serializable {

    }

    public static class EventDate implements Serializable {
        private int d;
        private int m;
        private int y;

        public int getY() {
            return y;
        }

        public void setY(int y) {
            this.y = y;
        }

        public int getD() {
            return d;
        }

        public void setD(int d) {
            this.d = d;
        }

        public int getM() {
            return m;
        }

        public void setM(int m) {
            this.m = m;
        }

    }
}
