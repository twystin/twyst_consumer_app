package com.twyst.app.android.model;

import java.io.Serializable;

/**
 * Created by satish on 15/06/15.
 */
public class MetaDiscount implements Serializable {
    private String max;
    private String percentage;

    public String getMax() {
        return max;
    }

    public void setMax(String max) {
        this.max = max;
    }

    public String getPercentage() {
        return percentage;
    }

    public void setPercentage(String percentage) {
        this.percentage = percentage;
    }
}
