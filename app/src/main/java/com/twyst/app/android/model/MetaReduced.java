package com.twyst.app.android.model;

import java.io.Serializable;

/**
 * Created by rahuls on 15/6/15.
 */
public class MetaReduced implements Serializable {
    private String what;
    private String for_what;

    public String getWhat() {
        return what;
    }

    public void setWhat(String what) {
        this.what = what;
    }

    public String getFor_what() {
        return for_what;
    }

    public void setFor_what(String for_what) {
        this.for_what = for_what;
    }
}
