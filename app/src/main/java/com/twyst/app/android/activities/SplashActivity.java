package com.twyst.app.android.activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.graphics.drawable.AnimationDrawable;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.appsflyer.*;

import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.LinkedList;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.tagmanager.Container;
import com.google.android.gms.tagmanager.ContainerHolder;
import com.google.android.gms.tagmanager.TagManager;
import com.google.gson.Gson;
import com.twyst.app.android.R;
import com.twyst.app.android.asynctask.FetchOutletsTask;
import com.twyst.app.android.model.BaseResponse;
import com.twyst.app.android.model.ContainerHolderSingleton;
import com.twyst.app.android.service.HttpService;
import com.twyst.app.android.util.AppConstants;
import com.twyst.app.android.util.PhoneBookContacts;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by satish on 23/12/14.
 */
public class SplashActivity extends Activity implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener{

    private static String TAG = "SplashActivity";
    private static final long TIMEOUT_FOR_CONTAINER_OPEN_MILLISECONDS = 2000;

    private boolean fromSplashScreenDownloading = false;
    // Google client to interact with Google API
    private GoogleApiClient mGoogleApiClient;

    private static final String CONTAINER_ID = "GTM-PNRJQ9";
    private static Handler handler  = new Handler();

    private static int SPLASH_TIME_OUT = 2200;
    private GoogleCloudMessaging googleCloudMessaging;
    private Context context;
    private ContainerHolder mContainerHolder = null;

    private void setContainerHolder(ContainerHolder containerHolder) {
        this.mContainerHolder = containerHolder;
    }
    private Runnable timedTask = new Runnable(){

        @Override
        public void run() {
            setContainerHolder(ContainerHolderSingleton.getContainerHolder());
            if (mContainerHolder != null) {
                mContainerHolder.refresh();
            }
            updateConstantsfromContainer();
        }};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_splash);

        if (AppConstants.IS_DEVELOPMENT) {
            getKey();
            generateHashKey();
        }

        setupAppsFlyer();

        SharedPreferences prefs = getSharedPreferences(AppConstants.PREFERENCE_SHARED_PREF_NAME, MODE_PRIVATE);
        if (prefs.getBoolean(AppConstants.PREFERENCE_IS_FIRST_RUN,true)){
            // Do first run stuff here then set 'firstrun' as false
            // using the following line to edit/commit prefs
            prefs.edit().putBoolean(AppConstants.PREFERENCE_IS_PUSH_ENABLED, true).apply();

            prefs.edit().putBoolean(AppConstants.PREFERENCE_IS_FIRST_RUN, false).apply();
        }

        context = getApplicationContext();

        showAnimation();
        saveOutletsList();
        new FetchContact().execute();

        if (checkPlayServices()) {
            buildGoogleApiClient();
            googleCloudMessaging = GoogleCloudMessaging.getInstance(this);
            registerInBackground();
            downloadContainer();
            handler.postDelayed(timedTask, TIMEOUT_FOR_CONTAINER_OPEN_MILLISECONDS + 1000);
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
                    intent.putExtra(AppConstants.INTENT_FROM_SPLASH_DOWNLOADING, fromSplashScreenDownloading);
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

    private void setupAppsFlyer() {
        // Set the Currency
        AppsFlyerLib.setCurrencyCode("USD");

        AppsFlyerLib.setAppsFlyerKey("yezoub3j6KZJt3VPyKoJ2Z");
        AppsFlyerLib.sendTracking(this);

        final String LOG_TAG ="AppsFlyer";
        AppsFlyerLib.registerConversionListener(this, new AppsFlyerConversionListener() {
            public void onInstallConversionDataLoaded(Map<String, String> conversionData) {
                DebugLogQueue.getInstance().push("\nGot conversion data from server");
                for (String attrName : conversionData.keySet()) {
                    Log.d(LOG_TAG, "attribute: " + attrName + " = " + conversionData.get(attrName));
                }
            }

            public void onInstallConversionFailure(String errorMessage) {
                Log.d(LOG_TAG, "error getting conversion data: " + errorMessage);
            }

            public void onAppOpenAttribution(Map<String, String> attributionData) {
                printMap(attributionData);
            }

            public void onAttributionFailure(String errorMessage) {
                Log.d(LOG_TAG, "error onAttributionFailure : " + errorMessage);

            }

            private void printMap(Map<String, String> map) {
                for (String key : map.keySet()) {
                    Log.d(LOG_TAG, key + "="+map.get(key));
                }

            }
        });
    }

    private void saveOutletsList() {
        final SharedPreferences preferences = getSharedPreferences(AppConstants.PREFERENCE_SHARED_PREF_NAME, Context.MODE_PRIVATE);
        HttpService.getInstance().getOutletsList(new Callback<BaseResponse>() {
            @Override
            public void success(BaseResponse baseResponse, Response response) {

                Object object = baseResponse.getData();
                Gson gson = new Gson();
                String outletList = gson.toJson(object);

                preferences.edit().putString(AppConstants.PREFERENCE_OUTLETS_LIST, outletList).apply();
            }

            @Override
            public void failure(RetrofitError error) {
            }
        });
    }

    private void downloadContainer() {
        TagManager tagManager = TagManager.getInstance(this);

        // Modify the log level of the logger to print out not only
        // warning and error messages, but also verbose, debug, info messages.
//        tagManager.setVerboseLoggingEnabled(true);

        PendingResult<ContainerHolder> pending =
                tagManager.loadContainerPreferNonDefault(CONTAINER_ID,
                        R.raw.gtm);

        // The onResult method will be called as soon as one of the following happens:
        //     1. a saved container is loaded
        //     2. if there is no saved container, a network container is loaded
        //     3. the 2-second timeout occurs
        pending.setResultCallback(new ResultCallback<ContainerHolder>() {
            @Override
            public void onResult(ContainerHolder containerHolder) {
                ContainerHolderSingleton.setContainerHolder(containerHolder);
                Container container = containerHolder.getContainer();
                if (!containerHolder.getStatus().isSuccess()) {
                    Log.e("SplashActivity", "failure loading container");
//                    displayErrorToUser(R.string.load_error);
                    return;
                }
                ContainerHolderSingleton.setContainerHolder(containerHolder);
                ContainerLoadedCallback.registerCallbacksForContainer(container);
                containerHolder.setContainerAvailableListener(new ContainerLoadedCallback());
//                startMainActivity();
            }
        }, TIMEOUT_FOR_CONTAINER_OPEN_MILLISECONDS, TimeUnit.MILLISECONDS);
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
        AppsFlyerLib.onActivityResume(this);
    }

    @Override
    public void onPause() {
        super.onPause();
        AppsFlyerLib.onActivityPause(this);
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

    private void updateConstantsfromContainer() {
        if (mContainerHolder != null) {
            Double USER_ONE_LOCATION_CHECK_TIME = (mContainerHolder.getContainer().getDouble(AppConstants.PREFERENCE_USER_ONE_LOCATION_CHECK_TIME));
            Double DISTANCE_LIMIT = (mContainerHolder.getContainer().getDouble(AppConstants.PREFERENCE_DISTANCE_LIMIT));
            Double LOCATION_REQUEST_REFRESH_INTERVAL = (mContainerHolder.getContainer().getDouble(AppConstants.PREFERENCE_LOCATION_REQUEST_REFRESH_INTERVAL));
            Double LOCATION_REQUEST_SMALLEST_DISPLACEMENT = (mContainerHolder.getContainer().getDouble(AppConstants.PREFERENCE_LOCATION_REQUEST_SMALLEST_DISPLACEMENT));
            Double LOCATION_REQUEST_PRIORITY = (mContainerHolder.getContainer().getDouble(AppConstants.PREFERENCE_LOCATION_REQUEST_PRIORITY));
            Double LOCATION_OFFLINE_LIST_MAX_SIZE = (mContainerHolder.getContainer().getDouble(AppConstants.PREFERENCE_LOCATION_OFFLINE_LIST_MAX_SIZE));

            //Earn-burn values update
            Double TWYST_BUCKS_INVITE_FRIENDS = (mContainerHolder.getContainer().getDouble("invite_friends"));
            Double TWYST_BUCKS_FOLLOW = (mContainerHolder.getContainer().getDouble("follow"));
            Double TWYST_BUCKS_LIKE_OFFER = (mContainerHolder.getContainer().getDouble("like_offer"));
            Double TWYST_BUCKS_CHECKIN_OUTLET_NON_PAYING = (mContainerHolder.getContainer().getDouble("checkin"));
            Double TWYST_BUCKS_CHECKIN_OUTLET_PAYING = (mContainerHolder.getContainer().getDouble("checkin_is_paying"));
            Double TWYST_BUCKS_SUBMIT_OFFER = (mContainerHolder.getContainer().getDouble("submit_offer"));
            Double TWYST_BUCKS_SHARE_OFFER = (mContainerHolder.getContainer().getDouble("share_offer"));
            Double TWYST_BUCKS_SHARE_OUTLET = (mContainerHolder.getContainer().getDouble("share_outlet"));
            Double TWYST_BUCKS_SUGGESTION = (mContainerHolder.getContainer().getDouble("suggestion"));
            Double TWYST_BUCKS_SHARE_CHECKIN = (mContainerHolder.getContainer().getDouble("share_checkin"));
            Double TWYST_BUCKS_SHARE_REDEMPTION = (mContainerHolder.getContainer().getDouble("share_redemption"));
            Double TWYST_BUCKS_GRAB = (mContainerHolder.getContainer().getDouble("grab"));
            Double TWYST_BUCKS_EXTEND = (mContainerHolder.getContainer().getDouble("extend"));
            Double TWYST_BUCKS_REDEEM = (mContainerHolder.getContainer().getDouble("redeem"));
            Double TWYST_BUCKS_BUY_CHECKIN = (mContainerHolder.getContainer().getDouble("buy_checkin"));

            final SharedPreferences.Editor prefsEdit = getSharedPreferences(AppConstants.PREFERENCE_SHARED_PREF_NAME, Context.MODE_PRIVATE).edit();
            prefsEdit.putInt((AppConstants.PREFERENCE_USER_ONE_LOCATION_CHECK_TIME),USER_ONE_LOCATION_CHECK_TIME.intValue());
            prefsEdit.putInt((AppConstants.PREFERENCE_DISTANCE_LIMIT),DISTANCE_LIMIT.intValue());
            prefsEdit.putInt((AppConstants.PREFERENCE_LOCATION_REQUEST_REFRESH_INTERVAL),LOCATION_REQUEST_REFRESH_INTERVAL.intValue());
            prefsEdit.putInt((AppConstants.PREFERENCE_LOCATION_REQUEST_SMALLEST_DISPLACEMENT),LOCATION_REQUEST_SMALLEST_DISPLACEMENT.intValue());
            prefsEdit.putInt((AppConstants.PREFERENCE_LOCATION_REQUEST_PRIORITY),LOCATION_REQUEST_PRIORITY.intValue());
            prefsEdit.putInt((AppConstants.PREFERENCE_LOCATION_OFFLINE_LIST_MAX_SIZE),LOCATION_OFFLINE_LIST_MAX_SIZE.intValue());

            prefsEdit.putInt((AppConstants.PREFERENCE_TWYST_BUCKS_INVITE_FRIENDS),TWYST_BUCKS_INVITE_FRIENDS.intValue());
            prefsEdit.putInt((AppConstants.PREFERENCE_TWYST_BUCKS_FOLLOW),TWYST_BUCKS_FOLLOW.intValue());
            prefsEdit.putInt((AppConstants.PREFERENCE_TWYST_BUCKS_LIKE_OFFER),TWYST_BUCKS_LIKE_OFFER.intValue());
            prefsEdit.putInt((AppConstants.PREFERENCE_TWYST_BUCKS_CHECKIN_OUTLET_NON_PAYING),TWYST_BUCKS_CHECKIN_OUTLET_NON_PAYING.intValue());
            prefsEdit.putInt((AppConstants.PREFERENCE_TWYST_BUCKS_CHECKIN_OUTLET_PAYING),TWYST_BUCKS_CHECKIN_OUTLET_PAYING.intValue());
            prefsEdit.putInt((AppConstants.PREFERENCE_TWYST_BUCKS_SUBMIT_OFFER),TWYST_BUCKS_SUBMIT_OFFER.intValue());
            prefsEdit.putInt((AppConstants.PREFERENCE_TWYST_BUCKS_SHARE_OFFER),TWYST_BUCKS_SHARE_OFFER.intValue());
            prefsEdit.putInt((AppConstants.PREFERENCE_TWYST_BUCKS_SHARE_OUTLET),TWYST_BUCKS_SHARE_OUTLET.intValue());
            prefsEdit.putInt((AppConstants.PREFERENCE_TWYST_BUCKS_SUGGESTION),TWYST_BUCKS_SUGGESTION.intValue());
            prefsEdit.putInt((AppConstants.PREFERENCE_TWYST_BUCKS_SHARE_CHECKIN),TWYST_BUCKS_SHARE_CHECKIN.intValue());
            prefsEdit.putInt((AppConstants.PREFERENCE_TWYST_BUCKS_SHARE_REDEMPTION),TWYST_BUCKS_SHARE_REDEMPTION.intValue());
            prefsEdit.putInt((AppConstants.PREFERENCE_TWYST_BUCKS_GRAB),TWYST_BUCKS_GRAB.intValue());
            prefsEdit.putInt((AppConstants.PREFERENCE_TWYST_BUCKS_EXTEND),TWYST_BUCKS_EXTEND.intValue());
            prefsEdit.putInt((AppConstants.PREFERENCE_TWYST_BUCKS_REDEEM),TWYST_BUCKS_REDEEM.intValue());
            prefsEdit.putInt((AppConstants.PREFERENCE_TWYST_BUCKS_BUY_CHECKIN),TWYST_BUCKS_BUY_CHECKIN.intValue());

            prefsEdit.apply();
        }
    }


private static class ContainerLoadedCallback implements ContainerHolder.ContainerAvailableListener {
    @Override
    public void onContainerAvailable(ContainerHolder containerHolder, String containerVersion) {
        // We load each container when it becomes available.
        Container container = containerHolder.getContainer();
        registerCallbacksForContainer(container);
    }

    public static void registerCallbacksForContainer(Container container) {
        // Register two custom function call macros to the container.
        container.registerFunctionCallMacroCallback("increment", new CustomMacroCallback());
        container.registerFunctionCallMacroCallback("mod", new CustomMacroCallback());
        // Register a custom function call tag to the container.
        container.registerFunctionCallTagCallback("custom_tag", new CustomTagCallback());
    }
}

private static class CustomMacroCallback implements Container.FunctionCallMacroCallback {
    private int numCalls;

    @Override
    public Object getValue(String name, Map<String, Object> parameters) {
        if ("increment".equals(name)) {
            return ++numCalls;
        } else if ("mod".equals(name)) {
            return (Long) parameters.get("key1") % Integer.valueOf((String) parameters.get("key2"));
        } else {
            throw new IllegalArgumentException("Custom macro name: " + name + " is not supported.");
        }
    }
}

private static class CustomTagCallback implements Container.FunctionCallTagCallback {
    @Override
    public void execute(String tagName, Map<String, Object> parameters) {
        // The code for firing this custom tag.
        Log.i("SplashActivity", "Custom function call tag :" + tagName + " is fired.");
    }
}

    private void tryFetchCurrentLocationStartDownload() {
        Location mLastLocation = null;
        if (mGoogleApiClient != null) {
            mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        }
        if (mLastLocation != null) {
            // Got current location
            String lat = String.valueOf(mLastLocation.getLatitude());
            String lng = String.valueOf(mLastLocation.getLongitude());
            SharedPreferences.Editor sharedPreferences = getSharedPreferences(AppConstants.PREFERENCE_SHARED_PREF_NAME, Context.MODE_PRIVATE).edit();
            sharedPreferences.putString(AppConstants.PREFERENCE_CURRENT_USED_LAT, lat);
            sharedPreferences.putString(AppConstants.PREFERENCE_CURRENT_USED_LNG, lng);
            if (sharedPreferences.commit()){
                new FetchOutletsTask(getApplicationContext()).fetch();
                fromSplashScreenDownloading = true;
            }
        }
        }

    @Override
    public void onConnected(Bundle bundle) {
        final SharedPreferences prefs = this.getSharedPreferences(AppConstants.PREFERENCE_SHARED_PREF_NAME, Context.MODE_PRIVATE);
        String userToken = prefs.getString(AppConstants.PREFERENCE_USER_TOKEN, "");
        if (!TextUtils.isEmpty(userToken)){
            tryFetchCurrentLocationStartDownload();
        }
    }

    @Override
    public void onConnectionSuspended(int i) {
        mGoogleApiClient.connect();
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Log.i(TAG, "Connection failed: ConnectionResult.getErrorCode() = "
                + connectionResult.getErrorCode());
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (mGoogleApiClient != null) {
            mGoogleApiClient.connect();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mGoogleApiClient != null && mGoogleApiClient.isConnected()) {
            mGoogleApiClient.disconnect();
        }
    }

    /**
     * Creating google api client object
     */
    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API).build();
    }

//    @Override
//    public void onResult(LocationSettingsResult locationSettingsResult) {
//        final Status status = locationSettingsResult.getStatus();
//        switch (status.getStatusCode()) {
//            case LocationSettingsStatusCodes.SUCCESS:
//                Log.i(TAG, "All location settings are satisfied.");
////                getLocationFetch(true, true);
//                break;
//        }
//    }

}

