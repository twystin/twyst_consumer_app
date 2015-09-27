package com.twyst.app.android.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by rahuls on 21/7/15.
 */
public class Suggestion implements Serializable{

    @SerializedName("event_meta")
    private SuggestionMeta suggestionMeta;

    public SuggestionMeta getSuggestionMeta() {
        return suggestionMeta;
    }

    public void setSuggestionMeta(SuggestionMeta suggestionMeta) {
        this.suggestionMeta = suggestionMeta;
    }
}
