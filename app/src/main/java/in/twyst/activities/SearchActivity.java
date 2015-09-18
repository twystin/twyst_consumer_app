package in.twyst.activities;


import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

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
