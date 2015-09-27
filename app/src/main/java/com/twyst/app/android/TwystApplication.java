package com.twyst.app.android;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.support.multidex.MultiDex;

import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.Tracker;

import com.twyst.app.android.service.HttpService;
import com.twyst.app.android.service.LocationService;
import com.twyst.app.android.util.AppConstants;

/**
 * Created by satish on 31/05/15.
 */
public class TwystApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        HttpService.getInstance().setup(getApplicationContext(), getGATracker());

        Intent locationService = new Intent();
        locationService.setClass(getApplicationContext(), LocationService.class);
        startService(locationService);
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
