package com.twyst.app.android.activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.appsflyer.AppsFlyerLib;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import com.twyst.app.android.R;
import com.twyst.app.android.model.CustomisedAddress;
import com.twyst.app.android.model.LocationData;
import com.twyst.app.android.util.AppConstants;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

/**
 * Created by rahuls on 30/7/15.
 */
public class ChangeMapActivity extends FragmentActivity implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener, LocationListener {

    // LogCat tag
    private static final String TAG = ChangeMapActivity.class.getSimpleName();

    GoogleMap googleMap;
    LocationData locationData;

    ProgressBar circularProgressBar;
    AutoCompleteTextView et_location;

    MarkerOptions markerOptions;
    ArrayAdapter<CustomisedAddress> arrayAdapter;

    private final static int PLAY_SERVICES_RESOLUTION_REQUEST = 1000;

    private Location mLastLocation;

    // Google client to interact with Google API
    private GoogleApiClient mGoogleApiClient;

    // boolean flag to toggle periodic location updates
    private boolean mRequestingLocationUpdates = true;

    private LocationRequest mLocationRequest;

    // Location updates intervals in sec
    private static int UPDATE_INTERVAL = 10000; // 10 sec
    private static int FATEST_INTERVAL = 5000; // 5 sec
    private static int DISPLACEMENT = 10; // 10 meters

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_map);
        // First we need to check availability of play services
        if (checkPlayServices()) {

            // Building the GoogleApi client
            buildGoogleApiClient();

            createLocationRequest();
        }


        SupportMapFragment supportMapFragment = (SupportMapFragment)
                getSupportFragmentManager().findFragmentById(R.id.googleMap);

        locationData = new LocationData();

        et_location = (AutoCompleteTextView) findViewById(R.id.et_location);
        circularProgressBar = (ProgressBar) findViewById(R.id.circularProgressBar);

        // Getting a reference to the map
        googleMap = supportMapFragment.getMap();
        googleMap.setMyLocationEnabled(true);

        et_location.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                togglePeriodicLocationUpdates(false);
                String locationToSearch = charSequence.toString();
                if (TextUtils.isEmpty(locationToSearch)) return;
                new GeocoderTask().execute(locationToSearch);
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        et_location.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                CustomisedAddress selectedCustomisedAddress = (CustomisedAddress) adapterView.getAdapter().getItem(i);
                View view1 = ChangeMapActivity.this.getCurrentFocus();
                if (view1 != null) {
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(view1.getWindowToken(), 0);
                }
                // Creating an instance of GeoPoint, to display in Google Map
                LatLng latLng = new LatLng(selectedCustomisedAddress.getLatitude(), selectedCustomisedAddress.getLongitude());
                setMarkerOnMap(latLng, selectedCustomisedAddress.getAddress(), true);

            }
        });

        findViewById(R.id.sendBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (locationData != null) {
                    Bundle info = new Bundle();
                    info.putSerializable("locationData", locationData);
                    Intent intent = new Intent();
                    intent.putExtras(info);
                    setResult(RESULT_OK, intent);
                    finish();
                }
            }
        });

        findViewById(R.id.myMapLocationButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getCurrentLocation();
            }
        });

        // Setting a click event handler for the map
        googleMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {

            @Override
            public void onMapClick(LatLng latLng) {
                setMarkerOnMap(latLng, "", false);
            }
        });
    }

    private void setMarkerOnMap(LatLng latLng, String addressTitle, boolean isAnimate) {
        MarkerOptions markerOptions = new MarkerOptions();
        // Setting the position for the marker
        markerOptions.position(latLng);

        locationData.setLat(String.valueOf(latLng.latitude));
        locationData.setLng(String.valueOf(latLng.longitude));
        markerOptions.title(addressTitle);

        googleMap.clear();
        googleMap.addMarker(markerOptions);
        if (isAnimate) {
            googleMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
            googleMap.animateCamera(CameraUpdateFactory.zoomTo(15));
        }
    }

    // An AsyncTask class for accessing the GeoCoding Web Service
    private class GeocoderTask extends AsyncTask<String, Void, List<Address>> {

        @Override
        protected void onPreExecute() {
            Log.d(TAG, "onPreExecute");
            circularProgressBar.setVisibility(View.VISIBLE);
        }

        @Override
        protected List<Address> doInBackground(String... locationName) {
            Log.d(TAG, "doInBackground");
            // Creating an instance of Geocoder class
            Geocoder geocoder = new Geocoder(getBaseContext());
            List<Address> addresses = null;

            try {
                // Getting a maximum of 10 Address that matches the input text
                addresses = geocoder.getFromLocationName(locationName[0], 10);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return addresses;
        }

        @Override
        protected void onPostExecute(List<Address> addressList) {
            Log.d(TAG, "onPostExecute");
            circularProgressBar.setVisibility(View.INVISIBLE);
            if (addressList == null || addressList.size() == 0) {
                Toast.makeText(getBaseContext(), "No Location found", Toast.LENGTH_SHORT).show();
            } else {

                ArrayList<CustomisedAddress> addressArrayList = new ArrayList<CustomisedAddress>(addressList.size());
                for (Address address : addressList) {
                    CustomisedAddress customisedAddress = new CustomisedAddress();
//                    String addressTitle = String.format("%s, %s",
//                            address.getMaxAddressLineIndex() > 0 ? address.getAddressLine(0) : "",
//                            address.getCountryName());
                    String addressTitle = "";

                    // Fetch the address lines using getAddressLine,
                    // join them, and send them to the thread.
                    for(int i = 0; i < address.getMaxAddressLineIndex(); i++) {
                        addressTitle = addressTitle + address.getAddressLine(i)+ ", ";
                    }
                    addressTitle = addressTitle + address.getAddressLine(address.getMaxAddressLineIndex());

                    customisedAddress.setAddress(addressTitle);
                    customisedAddress.setLatitude(address.getLatitude());
                    customisedAddress.setLongitude(address.getLongitude());
                    addressArrayList.add(customisedAddress);
                }

                Log.v("ADAPTER LIST", addressArrayList.toString());
                if (et_location.getAdapter() == null) {
                    arrayAdapter = new ArrayAdapter<CustomisedAddress>(ChangeMapActivity.this, R.layout.location_dropdown_item, addressArrayList);
                    et_location.setAdapter(arrayAdapter);
                } else {
                    arrayAdapter.setNotifyOnChange(false); // Prevents 'clear()' from clearing/resetting the listview
                    arrayAdapter.clear();
                    arrayAdapter.addAll(addressArrayList);
                    // note that a call to notifyDataSetChanged() implicitly sets the setNotifyOnChange back to 'true'!
                    // That's why the call 'setNotifyOnChange(false) should be called first every time (see call before 'clear()').
                    arrayAdapter.notifyDataSetChanged();
                }

            }
        }
    }


    /**
     * Method to display the location on UI
     */
    private void getCurrentLocation() {
        mRequestingLocationUpdates = true;
        mLastLocation = LocationServices.FusedLocationApi
                .getLastLocation(mGoogleApiClient);
        double latitude, longitude;
        LatLng latLng;
        String addressTitle = "";

        if (mLastLocation != null) {
            latitude = mLastLocation.getLatitude();
            longitude = mLastLocation.getLongitude();
            latLng = new LatLng(latitude, longitude);
        } else {
            SharedPreferences preferences = getSharedPreferences(AppConstants.PREFERENCE_SHARED_PREF_NAME, Context.MODE_PRIVATE);
            String lastLatitude = preferences.getString(AppConstants.PREFERENCE_CURRENT_USED_LAT, "");
            String lastLongitude = preferences.getString(AppConstants.PREFERENCE_CURRENT_USED_LNG, "");
            addressTitle = preferences.getString(AppConstants.PREFERENCE_CURRENT_USED_LOCATION_NAME, "");

            latitude = Double.parseDouble(lastLatitude);
            longitude = Double.parseDouble(lastLongitude);
            latLng = new LatLng(latitude, longitude);
            Toast.makeText(ChangeMapActivity.this, "Couldn't get the location. Showing last saved location", Toast.LENGTH_SHORT).show();
        }
        setMarkerOnMap(latLng, addressTitle, true);
    }

    @Override
    public void onLocationChanged(Location location) {
        Log.d(TAG, "onLocationChanged");
        // Assign the new location
        mLastLocation = location;

        if (mRequestingLocationUpdates) {
            getCurrentLocation();
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

    /**
     * Creating location request object
     */
    protected void createLocationRequest() {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(UPDATE_INTERVAL);
        mLocationRequest.setFastestInterval(FATEST_INTERVAL);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setSmallestDisplacement(DISPLACEMENT);
    }


    /**
     * Method to toggle periodic location updates
     */
    private void togglePeriodicLocationUpdates(boolean requestingLocationUpdates) {
        if (requestingLocationUpdates) {
            mRequestingLocationUpdates = true;
            // Starting the location updates
            startLocationUpdates();
            Log.d(TAG, "Periodic location updates started!");

        } else {
            mRequestingLocationUpdates = false;
            // Stopping the location updates
            stopLocationUpdates();
            Log.d(TAG, "Periodic location updates stopped!");
        }
    }

    /**
     * Starting the location updates
     */
    protected void startLocationUpdates() {
        LocationServices.FusedLocationApi.requestLocationUpdates(
                mGoogleApiClient, mLocationRequest, this);

    }

    /**
     * Stopping location updates
     */
    protected void stopLocationUpdates() {
        LocationServices.FusedLocationApi.removeLocationUpdates(
                mGoogleApiClient, this);
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (mGoogleApiClient != null) {
            mGoogleApiClient.connect();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        checkPlayServices();

        // Resuming the periodic location updates
        if (mGoogleApiClient.isConnected() && mRequestingLocationUpdates) {
            startLocationUpdates();
        }
        AppsFlyerLib.onActivityResume(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mGoogleApiClient != null && mGoogleApiClient.isConnected()) {
            mGoogleApiClient.disconnect();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        stopLocationUpdates();
        AppsFlyerLib.onActivityPause(this);
    }

    /**
     * Google api callback methods
     */
    @Override
    public void onConnectionFailed(ConnectionResult result) {
        Log.i(TAG, "Connection failed: ConnectionResult.getErrorCode() = "
                + result.getErrorCode());
    }

    @Override
    public void onConnected(Bundle arg0) {
        Log.d(TAG, "onConnected");
        if (mRequestingLocationUpdates) {
            startLocationUpdates();
            // Once connected with google api, get the location
            getCurrentLocation();
        }
    }

    @Override
    public void onConnectionSuspended(int arg0) {
        mGoogleApiClient.connect();
    }

    /**
     * Method to verify google play services on the device
     */
    private boolean checkPlayServices() {
        int resultCode = GooglePlayServicesUtil
                .isGooglePlayServicesAvailable(this);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (GooglePlayServicesUtil.isUserRecoverableError(resultCode)) {
                GooglePlayServicesUtil.getErrorDialog(resultCode, this,
                        PLAY_SERVICES_RESOLUTION_REQUEST).show();
            } else {
                Toast.makeText(getApplicationContext(),
                        "This device is not supported.", Toast.LENGTH_LONG)
                        .show();
                finish();
            }
            return false;
        }
        return true;
    }

}