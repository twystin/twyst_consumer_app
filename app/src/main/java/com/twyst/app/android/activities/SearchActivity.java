package com.twyst.app.android.activities;


import android.os.Bundle;
import android.view.View;

import com.appsflyer.AppsFlyerLib;
import com.twyst.app.android.R;

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

        findViewById(R.id.closeBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

    }

    @Override
    public void onResume() {
        super.onResume();
        AppsFlyerLib.onActivityResume(this);
    }
    @Override
    public void onPause() {
        super.onPause();
        AppsFlyerLib.onActivityPause(this);
    }

}
