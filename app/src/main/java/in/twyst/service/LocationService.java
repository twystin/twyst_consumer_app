package in.twyst.service;

import android.app.Service;
import android.content.Context;
import android.content.Intent;

import android.content.SharedPreferences;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.os.IBinder;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import in.twyst.model.BaseResponse;
import in.twyst.model.LocationData;
import in.twyst.model.UserLocation;
import in.twyst.util.AppConstants;
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
    protected Location mCurrentLocation;
    protected static final String TAG = "location-updates";
    private UserLocation locationData;

    @Override
    public void onCreate() {
        super.onCreate();

        System.out.println("LocationService.. onCreate()");

        if (!AppConstants.IS_DEVELOPMENT) {
            return;
        }

        mGoogleApiClient = new GoogleApiClient.Builder(getApplicationContext())
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();

        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(10000);
        mLocationRequest.setFastestInterval(1000);
        mLocationRequest.setSmallestDisplacement(500);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);

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
        super.onStartCommand(intent, flags, startId);
        System.out.println("LocationService.. onStartCommand()");
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

    //Location Listener
    @Override
    public void onLocationChanged(Location location) {
        System.out.println("LocationService.onLocationChanged i = " + location);
        //Toast.makeText(this, "LocationService.onConnectionSuspended", Toast.LENGTH_SHORT).show();

        mCurrentLocation = location;
        locationData = new UserLocation();
        locationData.setLat(String.valueOf(mCurrentLocation.getLatitude()));
        locationData.setLng(String.valueOf(mCurrentLocation.getLongitude()));
        HttpService.getInstance().postLocation(getUserToken(), locationData, new Callback<BaseResponse>() {
            @Override
            public void success(BaseResponse baseResponse, Response response) {}

            @Override
            public void failure(RetrofitError error) {}
        });

        getAddress(mCurrentLocation);

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