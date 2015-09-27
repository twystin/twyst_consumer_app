package com.twyst.app.android.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by satishk on 8/6/15.
 */
public class Offer implements Serializable {

    private String _id;

    private String type;

    private String header;

    private String line1;

    private String line2;

    private int next;

    @SerializedName("meta")
    private Meta meta;

    @SerializedName("available_now")
    private boolean availableNow;

    @SerializedName("available_next")
    private AvailableNext availableNext;

    @SerializedName("expiry")
    private String expiry;

    private String code;

    @SerializedName("offer_source")
    private String source;

    @SerializedName("issued_at")
    private String issuedDate;

    private String description;

    private String terms;

    @SerializedName("is_like")
    private boolean like;

    @SerializedName("offer_likes")
    private int likeCount;

    @SerializedName("lapse_date")
    private String lapseDate;

    @SerializedName("offer_cost")
    private int offerCost;

    @SerializedName("outlets")
    private List<OutletList> outletList;

    @SerializedName("issued_by")
    private String issuedBy;

    @SerializedName("lapsed_coupon_source")
    private LapsedCouponSource lapsedCouponSource;

    public LapsedCouponSource getLapsedCouponSource() {
        return lapsedCouponSource;
    }

    public void setLapsedCouponSource(LapsedCouponSource lapsedCouponSource) {
        this.lapsedCouponSource = lapsedCouponSource;
    }

    public String getIssuedBy() {
        return issuedBy;
    }

    public void setIssuedBy(String issuedBy) {
        this.issuedBy = issuedBy;
    }

    public List<OutletList> getOutletList() {
        return outletList;
    }

    public void setOutletList(List<OutletList> outletList) {
        this.outletList = outletList;
    }

    public int getOfferCost() {
        return offerCost;
    }

    public void setOfferCost(int offerCost) {
        this.offerCost = offerCost;
    }

    public String getLapseDate() {
        return lapseDate;
    }

    public void setLapseDate(String lapseDate) {
        this.lapseDate = lapseDate;
    }

    public boolean isLike() {
        return like;
    }

    public void setLike(boolean like) {
        this.like = like;
    }

    public int getLikeCount() {
        return likeCount;
    }

    public void setLikeCount(int likeCount) {
        this.likeCount = likeCount;
    }

    public String getTerms() {
        return terms;
    }

    public void setTerms(String terms) {
        this.terms = terms;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getIssuedDate() {
        return issuedDate;
    }

    public void setIssuedDate(String issuedDate) {
        this.issuedDate = issuedDate;
    }

    public String get_id() {
        return _id;
    }

    public void set_id(String _id) {
        this._id = _id;
    }

    public AvailableNext getAvailableNext() {
        return availableNext;
    }

    public void setAvailableNext(AvailableNext availableNext) {
        this.availableNext = availableNext;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
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

    public String getLine2() {
        return line2;
    }

    public void setLine2(String line2) {
        this.line2 = line2;
    }

    public int getNext() {
        return next;
    }

    public void setNext(int next) {
        this.next = next;
    }

    public Meta getMeta() {
        return meta;
    }

    public void setMeta(Meta meta) {
        this.meta = meta;
    }

    public boolean isAvailableNow() {
        return availableNow;
    }

    public void setAvailableNow(boolean availableNow) {
        this.availableNow = availableNow;
    }

    public String getExpiry() {
        return expiry;
    }

    public void setExpiry(String expiry) {
        this.expiry = expiry;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public static class OutletList implements Serializable {

        private String _id;

        private String name;

        private String city;

        private ArrayList<String> location_1;

        private ArrayList<String> location_2;

        public ArrayList<String> getLocation_1() {
            return location_1;
        }

        public void setLocation_1(ArrayList<String> location_1) {
            this.location_1 = location_1;
        }

        public ArrayList<String> getLocation_2() {
            return location_2;
        }

        public void setLocation_2(ArrayList<String> location_2) {
            this.location_2 = location_2;
        }

        public String getCity() {
            return city;
        }

        public void setCity(String city) {
            this.city = city;
        }

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
    }
}

