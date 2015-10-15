package com.twyst.app.android.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

/**
 * Created by satishk on 8/6/15.
 */
public class Outlet extends Data implements Serializable {

    private String name;

    private String city;

    private String address;

    private String locality_1;

    private String locality_2;

    private String distance;

    private String lat;

    @SerializedName("long")
    private String lng;

    private boolean open;

    private String phone;

    private List<Offer> offers;

    public String getLng() {
        return lng;
    }

    public void setLng(String lng) {
        this.lng = lng;
    }

    public String getLat() {
        return lat;
    }

    public void setLat(String lat) {
        this.lat = lat;
    }

    private boolean following;

    private String background;

    @SerializedName("is_paying")
    private boolean isPaying;

    public boolean isPaying() {
        return isPaying;
    }

    public void setIsPaying(boolean isPaying) {
        this.isPaying = isPaying;
    }

    public String getName() {
        return name;
    }

    public String getLocality_1() {
        return locality_1;
    }

    public void setLocality_1(String locality_1) {
        this.locality_1 = locality_1;
    }

    public String getLocality_2() {
        return locality_2;
    }

    public void setLocality_2(String locality_2) {
        this.locality_2 = locality_2;
    }

    public String getDistance() {
        return distance;
    }

    public void setDistance(String distance) {
        this.distance = distance;
    }

    public boolean isFollowing() {
        return following;
    }

    public void setFollowing(boolean following) {
        this.following = following;
    }

    public String getBackground() {
        return background;
    }

    public void setBackground(String background) {
        this.background = background;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }


    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public boolean isOpen() {
        return open;
    }

    public void setOpen(boolean open) {
        this.open = open;
    }


    public List<Offer> getOffers() {
        return offers;
    }

    public void setOffers(List<Offer> offers) {
        this.offers = offers;
    }


}
