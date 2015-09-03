package in.twyst.activities;

import android.os.Bundle;

import in.twyst.R;

/**
 * Created by rahuls on 20/8/15.
 */
public class FaqActivity extends BaseActivity{
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
    }
}
