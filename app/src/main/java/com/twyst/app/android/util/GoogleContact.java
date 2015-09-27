package com.twyst.app.android.util;

/**
 * Created by vivek on 06/08/15.
 */
public class GoogleContact {
    private final String name;
    private final String email;
    private boolean selected;

    public GoogleContact(String name, String email) {
        this.name = name;
        this.email = email;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    @Override
    public String toString() {
        return name;
    }
}
