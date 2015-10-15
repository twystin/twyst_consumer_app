package com.twyst.app.android.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

import retrofit.http.Field;

/**
 * Created by Vipul Sharma on 10/15/2015.
 */
public class UpdateProfile implements Serializable {
    private String email;
    private String image;
    @SerializedName("first_name")
    private String fname;
    @SerializedName("middle_name")
    private String mname;
    @SerializedName("last_name")
    private String lname;
    private String city;
    private String id;
    private String source;
    private String facebookUri;
    private String googleplusUri;
    @SerializedName("gcmId")
    private String deviceId;
    @SerializedName("os_version")
    private String version;
    private String device;
    private String model;
    private String product;

    @SerializedName("life_events")
    private LifeEvents[] lifeEvents;

    public LifeEvents[] getLifeEvents() {
        return lifeEvents;
    }

    public void setLifeEvents(LifeEvents[] lifeEvents) {
        this.lifeEvents = lifeEvents;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getFname() {
        return fname;
    }

    public void setFname(String fname) {
        this.fname = fname;
    }

    public String getMname() {
        return mname;
    }

    public void setMname(String mname) {
        this.mname = mname;
    }

    public String getLname() {
        return lname;
    }

    public void setLname(String lname) {
        this.lname = lname;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getFacebookUri() {
        return facebookUri;
    }

    public void setFacebookUri(String facebookUri) {
        this.facebookUri = facebookUri;
    }

    public String getGoogleplusUri() {
        return googleplusUri;
    }

    public void setGoogleplusUri(String googleplusUri) {
        this.googleplusUri = googleplusUri;
    }

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getDevice() {
        return device;
    }

    public void setDevice(String device) {
        this.device = device;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public String getProduct() {
        return product;
    }

    public void setProduct(String product) {
        this.product = product;
    }

}
