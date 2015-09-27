package com.twyst.app.android.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

/**
 * Created by rahuls on 26/8/15.
 */

public class Friend implements Serializable{

    @SerializedName("source")
    private String source;

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    @SerializedName("list")
    private List<Friends> list;

    public List<Friends> getList() {
        return list;
    }

    public void setList(List<Friends> list) {
        this.list = list;
    }

    public static class Friends implements Serializable{

        private String phone;

        private String name;

        private String id;

        public String getPhone() {
            return phone;
        }

        public void setPhone(String phone) {
            this.phone = phone;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }
    }
}

