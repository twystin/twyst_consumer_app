package com.twyst.app.android.model;

import java.io.Serializable;

/**
 * Created by satishk on 5/6/15.
 */
public class OTPCode extends Data implements Serializable {

    private String code;

    private String phone;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

}
