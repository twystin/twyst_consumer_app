package com.twyst.app.android.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by rahuls on 31/8/15.
 */
public class GetFriend implements Serializable{

    private String _id;

    @SerializedName("friends")
    private FriendData friendData;

    public String get_id() {
        return _id;
    }

    public void set_id(String _id) {
        this._id = _id;
    }

    public FriendData getFriendData() {
        return friendData;
    }

    public void setFriendData(FriendData friendData) {
        this.friendData = friendData;
    }
}
