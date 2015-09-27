package com.twyst.app.android.activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.text.TextUtils;
import android.view.Menu;
import android.view.View;
import android.widget.Button;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import com.twyst.app.android.R;
import com.twyst.app.android.model.LocationData;
import com.twyst.app.android.util.AppConstants;

/**
 * Created by rahuls on 30/7/15.
 */
public class ChangeMapActivity extends FragmentActivity implements LocationListener {

    GoogleMap googleMap;
    String locationName;
    LocationData locationData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_map);
        if (!isGooglePlayServicesAvailable()) {
            finish();
        }
        SupportMapFragment supportMapFragment = (SupportMapFragment)
                getSupportFragmentManager().findFragmentById(R.id.googleMap);

        locationData = new LocationData();

        // Getting a reference to the map
        googleMap = supportMapFragment.getMap();
        googleMap.setMyLocationEnabled(true);

        LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        Criteria criteria = new Criteria();
        String bestProvider = locationManager.getBestProvider(criteria, true);
        final Location location = locationManager.getLastKnownLocation(bestProvider);
//        if (location != null) {
            onLocationChanged(location);
//        }
        locationManager.requestLocationUpdates(bestProvider, 20000, 0, this);


        final Button sendBtn = (Button)findViewById(R.id.sendBtn);
        sendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!TextUtils.isEmpty(locationName)) {
                    Bundle info = new Bundle();
                    info.putSerializable("locationData", locationData);
                    Intent intent = new Intent();
                    intent.putExtras(info);

                    setResult(RESULT_OK, intent);
                    finish();
                }
            }
        });


        // Setting a click event handler for the map
        googleMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {

            @Override
            public void onMapClick(LatLng latLng) {


                // Creating a marker
                MarkerOptions markerOptions = new MarkerOptions();

                // Setting the position for the marker
                markerOptions.position(latLng);


                locationData.setLat(String.valueOf(latLng.latitude));
                locationData.setLng(String.valueOf(latLng.longitude));
                locationName = latLng.toString();

                // Setting the title for the marker.
                // This will be displayed on taping the marker
                //markerOptions.title(locationName);

                // Clears the previously touched position
                googleMap.clear();

                // Animating to the touched position
                googleMap.animateCamera(CameraUpdateFactory.newLatLng(latLng));

                // Placing a marker on the touched position
                googleMap.addMarker(markerOptions);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }


    @Override
    public void onLocationChanged(Location location) {
        double latitude,longitude;
        LatLng latLng;
        if (location != null){
            latitude = location.getLatitude();
            longitude = location.getLongitude();
            latLng = new LatLng(latitude, longitude);

            locationName = latLng.toString();
            locationData.setLat(String.valueOf(latLng.latitude));
            locationData.setLng(String.valueOf(latLng.longitude));
        } else{

            SharedPreferences preferences = getSharedPreferences(AppConstants.PREFERENCE_SHARED_PREF_NAME, Context.MODE_PRIVATE);
            String lastLatitude = preferences.getString(AppConstants.PREFERENCE_LAST_LOCATION_LATITUDE, "");
            String lastLongitude = preferences.getString(AppConstants.PREFERENCE_LAST_LOCATION_LONGITUDE, "");

            latitude = Double.parseDouble(lastLatitude);
            longitude = Double.parseDouble(lastLongitude);
            latLng = new LatLng(latitude, longitude);

            locationName = preferences.getString(AppConstants.PREFERENCE_LAST_LOCATION_NAME, "");
            locationData.setLat(String.valueOf(latLng.latitude));
            locationData.setLng(String.valueOf(latLng.longitude));

        }
        googleMap.addMarker(new MarkerOptions().position(latLng));
        googleMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
        googleMap.animateCamera(CameraUpdateFactory.zoomTo(15));
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }

    private boolean isGooglePlayServicesAvailable() {
        int status = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
        if (ConnectionResult.SUCCESS == status) {
            return true;
        } else {
            GooglePlayServicesUtil.getErrorDialog(status, this, 0).show();
            return false;
        }
    }
}