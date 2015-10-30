package com.twyst.app.android.activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.appsflyer.AppsFlyerLib;
import com.twyst.app.android.R;
import com.twyst.app.android.adapters.TutorialPagerAdapter;
import com.twyst.app.android.util.AppConstants;


public class TutorialActivity extends FragmentActivity {
    private int dotsCount;
    private ImageView[] dots;
    private static boolean valueOfCountModified = false;
    private int launchCount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        setContentView(R.layout.activity_tutorial);

        TutorialPagerAdapter tutorialPagerAdapter = new TutorialPagerAdapter(getSupportFragmentManager());

        ViewPager viewPager = (ViewPager) findViewById(R.id.tutorialViewPager);
        viewPager.setAdapter(tutorialPagerAdapter);


        final View skipBtn = findViewById(R.id.skipBtn);
        viewPager.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                for (int i = 0; i < dotsCount; i++) {
                    dots[i].setImageResource(R.drawable.dot_inactive);
                }
                dots[position].setImageResource(R.drawable.dot_active);

                if (position == 3) {
                    skipBtn.setVisibility(View.GONE);
                } else {
                    skipBtn.setVisibility(View.VISIBLE);
                }
            }
        });

        LinearLayout dotsLayout = (LinearLayout) findViewById(R.id.viewPagerCountDots);
        dotsCount = tutorialPagerAdapter.getCount();
        dots = new ImageView[dotsCount];
        for (int i = 0; i < dotsCount; i++) {
            dots[i] = new ImageView(getBaseContext());
            dots[i].setImageResource(R.drawable.dot_inactive);
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            if (i > 0) {
                layoutParams.setMargins(25, 0, 0, 0);
            }
            dots[i].setLayoutParams(layoutParams);
            dotsLayout.addView(dots[i]);
        }
        dots[0].setImageResource(R.drawable.dot_active);


        skipBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                letsTwyst();
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

    public void letsTwyst() {
        SharedPreferences prefs = getSharedPreferences(AppConstants.PREFERENCE_SHARED_PREF_NAME, Context.MODE_PRIVATE);
        boolean phoneVerified = prefs.getBoolean(AppConstants.PREFERENCE_PHONE_VERIFIED, false);
        boolean emailVerified = prefs.getBoolean(AppConstants.PREFERENCE_EMAIL_VERIFIED, false);
        int tutorialCount = prefs.getInt(AppConstants.PREFERENCE_TUTORIAL_COUNT, 0);

        if(tutorialCount == 2) {
            saveSkipCount();
        }

        if (!phoneVerified) {
            startActivity(new Intent(getBaseContext(), PhoneVerificationActivity.class));
            finish();
        } else if (!emailVerified) {
            startActivity(new Intent(getBaseContext(), LoginActivity.class));
            finish();
        } else {
            Intent intent = new Intent(getBaseContext(), DiscoverActivity.class);
            intent.setAction("setChildNo");
            intent.putExtra("Search", false);
            startActivity(intent);
            finish();
        }


    }
    private void saveSkipCount() {
        SharedPreferences.Editor sharedPreferences = getSharedPreferences(AppConstants.PREFERENCE_SHARED_PREF_NAME, Context.MODE_PRIVATE).edit();
        sharedPreferences.putBoolean(AppConstants.PREFERENCE_TUTORIAL_SKIPPED, true);
        sharedPreferences.commit();
    }

}
