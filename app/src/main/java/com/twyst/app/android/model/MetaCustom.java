package com.twyst.app.android.model;

import java.io.Serializable;

/**
 * Created by satish on 15/06/15.
 */
public class MetaCustom implements Serializable {
    private String text;

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
}
