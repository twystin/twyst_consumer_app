package com.twyst.app.android.model;

import java.io.Serializable;

/**
 * Created by Vipul Sharma on 10/16/2015.
 */
public class OutletList implements Serializable {

    private String _id;

    private String name;

    private String address;

    public String get_id() {
        return _id;
    }

    public void set_id(String _id) {
        this._id = _id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress ()
    {
        return address;
    }

    public void setAddress (String address)
    {
        this.address = address;
    }
}
