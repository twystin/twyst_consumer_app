package com.twyst.app.android.model;

import java.io.Serializable;

/**
 * Created by anilkg on 25/8/15.
 */
public class CheckInSuccessData implements Serializable {

    private Double twyst_bucks;
    private String outlet_name;
    private String outlet_id;
    private String code;
    private String header;
    private String line1;
    private String line2;
    private int checkins_to_go;

    public int getCheckins_to_go() {
        return checkins_to_go;
    }

    public void setCheckins_to_go(int checkins_to_go) {
        this.checkins_to_go = checkins_to_go;
    }

    public String getLine2() {
        return line2;
    }

    public void setLine2(String line2) {
        this.line2 = line2;
    }

    public String getOutlet_name() {
        return outlet_name;
    }

    public void setOutlet_name(String outlet_name) {
        this.outlet_name = outlet_name;
    }

    public String getOutlet_id() {
        return outlet_id;
    }

    public void setOutlet_id(String outlet_id) {
        this.outlet_id = outlet_id;
    }

    public Double getTwyst_bucks() {
        return twyst_bucks;
    }

    public void setTwyst_bucks(Double twyst_bucks) {
        this.twyst_bucks = twyst_bucks;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getHeader() {
        return header;
    }

    public void setHeader(String header) {
        this.header = header;
    }

    public String getLine1() {
        return line1;
    }

    public void setLine1(String line1) {
        this.line1 = line1;
    }

}
