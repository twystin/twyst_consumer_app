package in.twyst.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.GravityCompat;
import android.view.Gravity;

import in.twyst.R;
import in.twyst.util.AppConstants;

/**
 * Created by rahuls on 20/8/15.
 */
public class FaqActivity extends BaseActivity {
    private boolean fromDrawer;

    @Override
    protected String getTagName() {
        return null;
    }

    @Override
    protected int getLayoutResource() {
        return R.layout.activity_faq;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setupAsChild=true;
        super.onCreate(savedInstanceState);
        fromDrawer = getIntent().getBooleanExtra(AppConstants.INTENT_PARAM_FROM_DRAWER, false);

    }

    @Override
    public void onBackPressed() {
        if (drawerOpened) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            if (fromDrawer) {
                //clear history and go to discover
                Intent intent = new Intent(getBaseContext(), DiscoverActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

                startActivity(intent);

            } else {
                super.onBackPressed();
            }

        }
    }
}
