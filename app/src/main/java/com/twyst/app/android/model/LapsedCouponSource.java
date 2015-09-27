package com.twyst.app.android.model;

import java.io.Serializable;

/**
 * Created by Vipul Sharma on 9/24/2015.
 */
public class LapsedCouponSource implements Serializable {

    private String id;

    private String name;

    private String phone;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }
}

