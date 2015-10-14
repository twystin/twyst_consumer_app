package com.twyst.app.android.model;

import java.io.Serializable;

/**
 * Created by Vipul Sharma on 10/14/2015.
 */
public class EventDate implements Serializable {
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