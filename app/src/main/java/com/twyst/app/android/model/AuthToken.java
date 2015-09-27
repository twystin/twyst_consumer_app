package com.twyst.app.android.model;

import java.io.Serializable;

/**
 * Created by satishk on 5/6/15.
 */
public class AuthToken extends Data implements Serializable {

    private String token;

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}
