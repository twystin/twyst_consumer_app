package com.twyst.app.android.activities;


import android.os.Bundle;

/**
 * Created by rahuls on 7/9/15.
 */
public class SearchActivity extends DiscoverActivity{

    @Override
    public void onBackPressed() {
        finish();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        search = getIntent().getBooleanExtra("Search", false);
        super.onCreate(savedInstanceState);
    }

}
