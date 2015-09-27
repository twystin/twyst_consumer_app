package com.twyst.app.android.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

/**
 * Created by rahuls on 31/8/15.
 */
public class FriendData implements Serializable{

    private String _id;

    @SerializedName("friends")
    private List<FriendLists> list;

    public String get_id() {
        return _id;
    }

    public void set_id(String _id) {
        this._id = _id;
    }

    public List<FriendLists> getList() {
        return list;
    }

    public void setList(List<FriendLists> list) {
        this.list = list;
    }

    public static class FriendLists implements Serializable{

        private String source;

        private String add_date;

        private String name;

        private String _id;

        private String phone;

        private String email;

        private String user;

        public String getPhone() {
            return phone;
        }

        public void setPhone(String phone) {
            this.phone = phone;
        }

        public String getEmail() {
            return email;
        }

        public void setEmail(String email) {
            this.email = email;
        }

        public String getUser() {
            return user;
        }

        public void setUser(String user) {
            this.user = user;
        }

        public String getSource() {
            return source;
        }

        public void setSource(String source) {
            this.source = source;
        }

        public String getAdd_date() {
            return add_date;
        }

        public void setAdd_date(String add_date) {
            this.add_date = add_date;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String get_id() {
            return _id;
        }

        public void set_id(String _id) {
            this._id = _id;
        }

        @Override
        public String toString() {

            return  (name!=null)?name:email;
        }
    }
}
