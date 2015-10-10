package com.twyst.app.android.activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.graphics.drawable.AnimationDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.gcm.GoogleCloudMessaging;

import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import com.twyst.app.android.R;
import com.twyst.app.android.service.HttpService;
import com.twyst.app.android.util.AppConstants;
import com.twyst.app.android.util.PhoneBookContacts;

/**
 * Created by satish on 23/12/14.
 */
public class SplashActivity extends Activity {

    private static int SPLASH_TIME_OUT = 2200;
    private GoogleCloudMessaging googleCloudMessaging;
    private Context context;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_splash);

        if (AppConstants.IS_DEVELOPMENT) {
            getKey();
            generateHashKey();
        }
        SharedPreferences prefs = getSharedPreferences(AppConstants.PREFERENCE_SHARED_PREF_NAME, MODE_PRIVATE);
        if (prefs.getBoolean(AppConstants.PREFERENCE_IS_FIRST_RUN,true)){
            // Do first run stuff here then set 'firstrun' as false
            // using the following line to edit/commit prefs
            prefs.edit().putBoolean(AppConstants.PREFERENCE_IS_PUSH_ENABLED, true).apply();

            prefs.edit().putBoolean(AppConstants.PREFERENCE_IS_FIRST_RUN, false).apply();
        }

        context = getApplicationContext();
        showAnimation();
        new FetchContact().execute();
        if (checkPlayServices()) {
            googleCloudMessaging = GoogleCloudMessaging.getInstance(this);
            registerInBackground();
        } else {
            Log.i(getClass().getSimpleName(), "No valid Google Play Services APK found.");
        }

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                SharedPreferences.Editor sharedPreferences = getSharedPreferences(AppConstants.PREFERENCE_SHARED_PREF_NAME, Context.MODE_PRIVATE).edit();
                sharedPreferences.remove(AppConstants.PREFERENCE_LAST_DRAWERITEM_CLICKED).commit();
                sharedPreferences.remove(AppConstants.PREFERENCE_PARAM_SEARCH_QUERY).commit();

                SharedPreferences prefs = getSharedPreferences(AppConstants.PREFERENCE_SHARED_PREF_NAME, Context.MODE_PRIVATE);
                int tutorialCount = prefs.getInt(AppConstants.PREFERENCE_TUTORIAL_COUNT, 0);
                boolean phoneVerified = prefs.getBoolean(AppConstants.PREFERENCE_PHONE_VERIFIED, false);
                boolean emailVerified = prefs.getBoolean(AppConstants.PREFERENCE_EMAIL_VERIFIED, false);
                boolean tutorialSkipped = prefs.getBoolean(AppConstants.PREFERENCE_TUTORIAL_SKIPPED, false);

                if (phoneVerified && emailVerified && (tutorialCount >= 3 || tutorialSkipped)) {
                    Intent intent = new Intent(SplashActivity.this, DiscoverActivity.class);
                    intent.setAction("setChildNo");
                    intent.putExtra("Search", false);
                    startActivity(intent);
                } else {
                    SharedPreferences.Editor editor = prefs.edit();
                    editor.putInt(AppConstants.PREFERENCE_TUTORIAL_COUNT, ++tutorialCount).commit();
                    startActivity(new Intent(SplashActivity.this, TutorialActivity.class));
                }
                finish();
                Log.i(getClass().getSimpleName(), "Splash killed.");
            }
        }, SPLASH_TIME_OUT);
    }

    private void showAnimation() {
        final ImageView ivT = (ImageView) findViewById(R.id.ivT);
        final ImageView ivW = (ImageView) findViewById(R.id.ivW);
        final ImageView ivY = (ImageView) findViewById(R.id.ivY);
        final ImageView ivS = (ImageView) findViewById(R.id.ivS);
        final ImageView ivT2 = (ImageView) findViewById(R.id.ivT2);
        final ImageView ivIN = (ImageView) findViewById(R.id.ivIN);

        final Animation splashSlideDownT = AnimationUtils.loadAnimation(this, R.anim.splash_slide_down);
        final Animation splashSlideDownW = AnimationUtils.loadAnimation(this, R.anim.splash_slide_down);
        final Animation splashSlideDownY = AnimationUtils.loadAnimation(this, R.anim.splash_slide_down);
        final Animation splashSlideDownS = AnimationUtils.loadAnimation(this, R.anim.splash_slide_down);
        final Animation splashSlideDownT2 = AnimationUtils.loadAnimation(this, R.anim.splash_slide_down);
        final Animation splashSlideDownIN = AnimationUtils.loadAnimation(this, R.anim.splash_slide_down);

        final AnimationDrawable frameAnimation = (AnimationDrawable) ivY.getDrawable();

        splashSlideDownIN.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                ivY.post(new Runnable() {
                    public void run() {
                        if (frameAnimation != null) {
                            ivY.setBackground(null);
                            frameAnimation.start();
                        }
                    }
                });
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });

        splashSlideDownT2.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                ivIN.startAnimation(splashSlideDownIN);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        splashSlideDownS.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                ivT2.startAnimation(splashSlideDownT2);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        splashSlideDownY.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                ivS.startAnimation(splashSlideDownS);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        splashSlideDownW.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                ivY.startAnimation(splashSlideDownY);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        splashSlideDownT.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                ivW.startAnimation(splashSlideDownW);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });

        ivT.startAnimation(splashSlideDownT);


    }

    protected boolean checkPlayServices() {
        if (AppConstants.IS_DEVELOPMENT) {
            return true;
        }

        int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
        if (resultCode != ConnectionResult.SUCCESS) {
            return false;
        }
        return true;
    }

    private void registerInBackground() {
        if (AppConstants.IS_DEVELOPMENT) {
            return;
        }

        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... params) {
                try {
                    if (googleCloudMessaging == null) {
                        googleCloudMessaging = GoogleCloudMessaging.getInstance(context);
                    }
                    final String registrationId = googleCloudMessaging.register(AppConstants.GCM_PROJECT_ID);
                    Log.i(getClass().getSimpleName(), "Device registered, registration ID=" + registrationId);
                    final SharedPreferences prefs = HttpService.getInstance().getSharedPreferences();

                    SharedPreferences.Editor editor = prefs.edit();
                    editor.putString(AppConstants.PREFERENCE_REGISTRATION_ID, registrationId);
                    editor.commit();

                } catch (IOException e) {
                    e.printStackTrace();
                }
                return null;
            }
        }.execute(null, null, null);
    }


    private void getKey() {
        PackageInfo info;
        try {
            info = getPackageManager().getPackageInfo("com.twyst.app.android", PackageManager.GET_SIGNATURES);
            for (Signature signature : info.signatures) {
                MessageDigest md;
                md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                String something = new String(Base64.encode(md.digest(), 0));
            }
        } catch (PackageManager.NameNotFoundException e1) {
        } catch (NoSuchAlgorithmException e) {
        } catch (Exception e) {
        }
    }

    private void generateHashKey() {

        try {
            PackageInfo info = getPackageManager().getPackageInfo("com.twyst.app.android", PackageManager.GET_SIGNATURES);
            for (Signature signature : info.signatures) {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                String hashCode  = Base64.encodeToString(md.digest(), Base64.DEFAULT);
                System.out.println("Print the hashKey for Facebook :" + hashCode);
                Log.d("KeyHash:", Base64.encodeToString(md.digest(),Base64.DEFAULT));
            }
        } catch (PackageManager.NameNotFoundException e) {

            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {

            e.printStackTrace();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    public class FetchContact extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... params) {
            PhoneBookContacts.getInstance().loadContacts(getApplicationContext());
            return null;
        }

        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
        }
    }

}