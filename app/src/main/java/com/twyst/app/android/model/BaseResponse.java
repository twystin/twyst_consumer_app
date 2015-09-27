package com.twyst.app.android.model;

import java.io.Serializable;

/**
 * Created by satishk on 6/6/15.
 */
public class BaseResponse<T> implements Serializable {

    private boolean response;

    private String message;

    private T data;

    public boolean isResponse() {
        return response;
    }

    public void setResponse(boolean response) {
        this.response = response;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

}

