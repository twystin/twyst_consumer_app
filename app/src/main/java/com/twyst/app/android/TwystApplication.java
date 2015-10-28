package com.twyst.app.android;

import android.app.ActivityManager;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.support.multidex.MultiDex;
import android.support.v7.widget.AppCompatTextView;
import android.widget.Toast;

import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.Tracker;

import com.twyst.app.android.service.HttpService;
import com.twyst.app.android.service.LocationService;
import com.twyst.app.android.util.AppConstants;

import uk.co.chrisjenx.calligraphy.CalligraphyConfig;

/**
 * Created by satish on 31/05/15.
 */
public class TwystApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        CalligraphyConfig.initDefault(new CalligraphyConfig.Builder()
                        .setDefaultFontPath("fonts/Roboto-Regular.ttf")
                        .addCustomStyle(AppCompatTextView.class, android.R.attr.textViewStyle)
                        .setFontAttrId(R.attr.fontPath)
                        .build()
        );

        HttpService.getInstance().setup(getApplicationContext(), getGATracker());

        if (!isMyServiceRunning(LocationService.class)){
            Intent locationService = new Intent();
            locationService.setClass(getApplicationContext(), LocationService.class);
            startService(locationService);
        }
    }

    private boolean isMyServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

    public synchronized Tracker getGATracker() {
        GoogleAnalytics analytics = GoogleAnalytics.getInstance(this);
        //analytics.getLogger().setLogLevel(Logger.LogLevel.VERBOSE);
        analytics.setDryRun(AppConstants.IS_DEVELOPMENT);

        Tracker tracker = analytics.newTracker(AppConstants.GOOGLE_ANALYTICS_ID);
        tracker.enableExceptionReporting(true);
        //tracker.setSampleRate(20);
        tracker.enableAutoActivityTracking(true);

        return tracker;
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }
}
