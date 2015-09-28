package com.twyst.app.android.model;

import java.io.Serializable;

/**
 * Created by satishk on 6/6/15.
 */
public class Data implements Serializable {

    private String _id;

    private int __v;

    private int twyst_bucks;

    public int getTwyst_bucks() {
        return twyst_bucks;
    }

    public void setTwyst_bucks(int twyst_bucks) {
        this.twyst_bucks = twyst_bucks;
    }

    public String get_id() {
        return _id;
    }

    public void set_id(String _id) {
        this._id = _id;
    }

    public int get__v() {
        return __v;
    }

    public void set__v(int __v) {
        this.__v = __v;
    }
}
