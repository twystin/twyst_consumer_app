package com.twyst.app.android.service;

import android.app.Service;
import android.content.Context;
import android.content.Intent;

import android.content.SharedPreferences;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

import com.google.gson.Gson;
import com.twyst.app.android.model.BaseResponse;
import com.twyst.app.android.model.LocationOffline;
import com.twyst.app.android.model.LocationOfflineList;
import com.twyst.app.android.model.UserLocation;
import com.twyst.app.android.util.AppConstants;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by rahuls on 27/08/15.
 */
public class LocationService extends Service implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener, LocationListener {

    private GoogleApiClient mGoogleApiClient;
    private LocationRequest mLocationRequest;
    protected static final String TAG = "location-updates";

    private final static int USER_ONE_LOCATION_CHECK_TIME = 30000;
    private final static int DISTANCE_LIMIT = 2000;
    private final static int LOCATION_REQUEST_REFRESH_INTERVAL = 1000;
    private final static int LOCATION_REQUEST_SMALLEST_DISPLACEMENT = 50;
    private final static int LOCATION_OFFLINE_LIST_MAX_SIZE = 10;

    private static Handler handler  = new Handler();

    @Override
    public void onCreate() {
        super.onCreate();

        System.out.println("LocationService.. onCreate()");

        if (AppConstants.IS_DEVELOPMENT) {
            return;
        }

        mGoogleApiClient = new GoogleApiClient.Builder(getApplicationContext())
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();

        mLocationRequest = new LocationRequest();
        final SharedPreferences prefs = getSharedPreferences(AppConstants.PREFERENCE_SHARED_PREF_NAME, Context.MODE_PRIVATE);
        mLocationRequest.setInterval(prefs.getInt(AppConstants.PREFERENCE_LOCATION_REQUEST_REFRESH_INTERVAL,LOCATION_REQUEST_REFRESH_INTERVAL));
        mLocationRequest.setFastestInterval(1000);
        mLocationRequest.setSmallestDisplacement(prefs.getInt(AppConstants.PREFERENCE_LOCATION_REQUEST_SMALLEST_DISPLACEMENT,LOCATION_REQUEST_SMALLEST_DISPLACEMENT));
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        if (mGoogleApiClient != null) {
            mGoogleApiClient.connect();
        }
    }

    @Override
    public void onDestroy() {
        System.out.println("LocationService.. onDestroy()");
        super.onDestroy();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
//        super.onStartCommand(intent, flags, startId);
//        System.out.println("LocationService.. onStartCommand()");
        return START_STICKY;
    }

    @Override
    public IBinder onBind(Intent arg0) {
        return null;
    }

    @Override
    public void onConnected(Bundle bundle) {
        System.out.println("LocationService.onConnected bundle = " + bundle);
        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
    }

    @Override
    public void onConnectionSuspended(int i) {
        System.out.println("LocationService.onConnectionSuspended i = " + i);
        mGoogleApiClient.connect();
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        System.out.println("Connection failed: ConnectionResult.getErrorCode() = " + connectionResult.getErrorCode());
    }

    private Runnable timedTask = new Runnable(){

        @Override
        public void run() {
            final SharedPreferences prefs = getSharedPreferences(AppConstants.PREFERENCE_SHARED_PREF_NAME, Context.MODE_PRIVATE);
            Location currentLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
            if (currentLocation!=null){
                String locationOfflineListString = prefs.getString(AppConstants.PREFERENCE_LAST_SAVED_LOCATIONS_LIST, "");
                Gson gson = new Gson();
                LocationOfflineList locationOfflineList = gson.fromJson(locationOfflineListString, LocationOfflineList.class);
                LinkedList<LocationOffline> fifo = locationOfflineList.getFifo();

                for (LocationOffline locationOffline : fifo) {
                    Location newLocation = new Location("");
                    newLocation.setLatitude(locationOffline.getLatitude());
                    newLocation.setLongitude(locationOffline.getLongitude());
                    double distance = currentLocation.distanceTo(newLocation);
                    if (distance <= prefs.getInt(AppConstants.PREFERENCE_DISTANCE_LIMIT,DISTANCE_LIMIT)) {
                        Toast.makeText(LocationService.this, "User at one location. Send to server", Toast.LENGTH_LONG).show();

                        UserLocation locationData;
                        locationData = new UserLocation();
                        locationData.setLat(String.valueOf(currentLocation.getLatitude()));
                        locationData.setLng(String.valueOf(currentLocation.getLongitude()));

                        HttpService.getInstance().postLocation(getUserToken(), locationData, new Callback<BaseResponse>() {
                            @Override
                            public void success(BaseResponse baseResponse, Response response) {
                                Toast.makeText(LocationService.this,"Location posted successfully to server",Toast.LENGTH_LONG).show();
                            }

                            @Override
                            public void failure(RetrofitError error) {
                            }
                        });

                        getAddress(currentLocation);



                        if(prefs.edit().putString(AppConstants.PREFERENCE_LAST_SAVED_LOCATIONS_LIST, "").commit())
                            break;
                    }
                }

            }else{
                prefs.edit().putString(AppConstants.PREFERENCE_LAST_SAVED_LOCATIONS_LIST, "").apply();
            }

        }};

    //Location Listener
    @Override
    public void onLocationChanged(Location location) {
        System.out.println("LocationService.onLocationChanged i = " + location);
        Toast.makeText(LocationService.this, "Location changed", Toast.LENGTH_LONG).show();

        final SharedPreferences prefs = getSharedPreferences(AppConstants.PREFERENCE_SHARED_PREF_NAME, Context.MODE_PRIVATE);

        String locationOfflineListString = prefs.getString(AppConstants.PREFERENCE_LAST_SAVED_LOCATIONS_LIST,"");
        Gson gson = new Gson();
        LocationOfflineList locationOfflineList = gson.fromJson(locationOfflineListString, LocationOfflineList.class);

        if (locationOfflineList==null)
            locationOfflineList = new LocationOfflineList();

        LocationOffline locationOffline = new LocationOffline();
        locationOffline.setLatitude(location.getLatitude());
        locationOffline.setLongitude(location.getLongitude());
        locationOffline.setTimeStamp(location.getTime());
        locationOfflineList.addToLast(locationOffline, prefs.getInt(AppConstants.PREFERENCE_LOCATION_OFFLINE_LIST_MAX_SIZE,LOCATION_OFFLINE_LIST_MAX_SIZE));

        Gson gson2 = new Gson();
        String json = gson2.toJson(locationOfflineList);

        prefs.edit().putString(AppConstants.PREFERENCE_LAST_SAVED_LOCATIONS_LIST,json).apply();

        handler.removeCallbacks(timedTask);
        handler.postDelayed(timedTask, prefs.getInt(AppConstants.PREFERENCE_USER_ONE_LOCATION_CHECK_TIME,USER_ONE_LOCATION_CHECK_TIME));

    }

    private void getAddress(Location mCurrentLocation) {

        String errorMessage = "";

        if (mCurrentLocation == null) {
            Log.wtf(TAG, "errorMessage");
            return;
        }

        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        List<Address> addresses = null;

        try {
            addresses = geocoder.getFromLocation(
                    mCurrentLocation.getLatitude(),
                    mCurrentLocation.getLongitude(),
                    1);
        } catch (IOException ioException) {
            // Catch network or other I/O problems.
            Log.e(TAG, "service_not_available", ioException);
        } catch (IllegalArgumentException illegalArgumentException) {
            // Catch invalid latitude or longitude values.
            Log.e(TAG, "invalid_lat_long_used" + ". " +
                    "Latitude = " + mCurrentLocation.getLatitude() +
                    ", Longitude = " + mCurrentLocation.getLongitude(), illegalArgumentException);
        }

        // Handle case where no address was found.
        if (addresses == null || addresses.size() == 0) {
            if (errorMessage.isEmpty()) {
                errorMessage = "no_address_found";
                Log.e(TAG, errorMessage);
            }
        } else {
            Address address = addresses.get(0);
            ArrayList<String> addressFragments = new ArrayList<String>();

            for (int i = 0; i < address.getMaxAddressLineIndex(); i++) {
                addressFragments.add(address.getAddressLine(i));
            }
            SharedPreferences.Editor sharedPreferences = getSharedPreferences(AppConstants.PREFERENCE_SHARED_PREF_NAME, Context.MODE_PRIVATE).edit();
            sharedPreferences.putString(AppConstants.PREFERENCE_LOCALITY_SHOWN_DRAWER, address.getLocality());
            sharedPreferences.putString(AppConstants.PREFERENCE_PREVIOUS_LOCALITY_SHOWN_DRAWER, String.valueOf(addressFragments));
            sharedPreferences.commit();

            SharedPreferences prefs = getSharedPreferences(AppConstants.PREFERENCE_SHARED_PREF_NAME, Context.MODE_PRIVATE);

            String launched = prefs.getString(AppConstants.PREFERENCE_CHECK_FIRST_LAUNCH,"");


            if(launched.equalsIgnoreCase("Yes")){
                String loc = prefs.getString(AppConstants.PREFERENCE_PREVIOUS_LOCALITY_SHOWN_DRAWER, "");
                if(loc.equalsIgnoreCase(String.valueOf(addressFragments))){
                    sharedPreferences = getSharedPreferences(AppConstants.PREFERENCE_SHARED_PREF_NAME, Context.MODE_PRIVATE).edit();
                    sharedPreferences.putString(AppConstants.PREFERENCE_LOCALITY_SHOWN_DRAWER, address.getLocality());
                    sharedPreferences.putString(AppConstants.PREFERENCE_CURRENT_LAT, String.valueOf(mCurrentLocation.getLatitude()));
                    sharedPreferences.putString(AppConstants.PREFERENCE_CURRENT_LNG, String.valueOf(mCurrentLocation.getLongitude()));
                    sharedPreferences.commit();
                }else {
                    sharedPreferences = getSharedPreferences(AppConstants.PREFERENCE_SHARED_PREF_NAME, Context.MODE_PRIVATE).edit();
                    sharedPreferences.putString(AppConstants.PREFERENCE_PREVIOUS_LOCALITY_SHOWN_DRAWER, String.valueOf(addressFragments));
                    sharedPreferences.putString(AppConstants.PREFERENCE_CURRENT_LAT, String.valueOf(mCurrentLocation.getLatitude()));
                    sharedPreferences.putString(AppConstants.PREFERENCE_CURRENT_LNG, String.valueOf(mCurrentLocation.getLongitude()));
                    sharedPreferences.commit();
                }
            }else {
                sharedPreferences.putString(AppConstants.PREFERENCE_LOCALITY_SHOWN_DRAWER, address.getLocality());
                sharedPreferences.putString(AppConstants.PREFERENCE_PREVIOUS_LOCALITY_SHOWN_DRAWER, String.valueOf(addressFragments));
                sharedPreferences.putString(AppConstants.PREFERENCE_CURRENT_LAT, String.valueOf(mCurrentLocation.getLatitude()));
                sharedPreferences.putString(AppConstants.PREFERENCE_CURRENT_LNG, String.valueOf(mCurrentLocation.getLongitude()));
                sharedPreferences.commit();
            }


            Log.i(TAG, "" + addressFragments + address.getLocality());


        }
    }

    public String getUserToken() {
        SharedPreferences prefs = this.getSharedPreferences(AppConstants.PREFERENCE_SHARED_PREF_NAME, Context.MODE_PRIVATE);
        return prefs.getString(AppConstants.PREFERENCE_USER_TOKEN, "");

    }
}