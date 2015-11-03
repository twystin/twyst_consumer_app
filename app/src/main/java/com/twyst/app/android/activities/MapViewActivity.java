package com.twyst.app.android.activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;

import com.twyst.app.android.R;
import com.twyst.app.android.util.AppConstants;

/**
 * Created by anilkg on 29/6/15.
 */
public class MapViewActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap map;
    LocationManager locationManager;
    private MyLocationListener listener;
    private boolean gps_enabled = false;
    private boolean network_enabled = false;
    private double latitude, longitude;
    private String outletName;
    LatLng latlng;
    private static final long MIN_TIME = 40;
    private static final float MIN_DISTANCE = 1000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mapview);

        Intent intent = getIntent();
        Bundle extras = intent.getExtras();
        if (extras != null) {

            String lat = extras.getString(AppConstants.INTENT_PARAM_OUTLET_LOCATION_LAT);
            String lng = extras.getString(AppConstants.INTENT_PARAM_OUTLET_LOCATION_LONG);
            outletName = extras.getString(AppConstants.INTENT_PARAM_OUTLET_NAME);

            latitude = Double.parseDouble(lat);
            longitude = Double.parseDouble(lng);


        }
        setUpMapIfNeeded();

        if (locationManager == null) {
            locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
            listener = new MyLocationListener();

            try {
                gps_enabled = locationManager
                        .isProviderEnabled(LocationManager.GPS_PROVIDER);
            } catch (Exception ex) {
            }

            try {
                network_enabled = locationManager
                        .isProviderEnabled(LocationManager.NETWORK_PROVIDER);
            } catch (Exception ex) {
            }
            // don't start listeners if no provider is enabled
            // if (!gps_enabled && !network_enabled)
            // return false;
            if (gps_enabled)
                locationManager
                        .requestLocationUpdates(LocationManager.GPS_PROVIDER, MIN_TIME, MIN_DISTANCE, listener);
            if (network_enabled)
                locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, MIN_TIME, MIN_DISTANCE, listener);

        }



    }


    private void setUpMapIfNeeded() {
        // Do a null check to confirm that we have not already instantiated the map.
        if (map == null) {
            // Try to obtain the map from the SupportMapFragment.
            MapFragment mapFragment = (MapFragment) getFragmentManager()
                    .findFragmentById(R.id.map);
            mapFragment.getMapAsync(this);
            map = mapFragment.getMap();

//            showOutletOnMap(lati,lngi);
        }
    }


    private void showOutletOnMap(double lati, double lngi) {

        if (map != null) {
            map.addMarker(new MarkerOptions()
                    .position(new LatLng(lati, lngi))
                    .title(outletName));
        }

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        googleMap.addMarker(new MarkerOptions()
                .position(new LatLng(latitude, longitude))
                .title(outletName));
        map.setMyLocationEnabled(true);

        googleMap.moveCamera(CameraUpdateFactory.newLatLng(new LatLng(latitude, longitude)));
        googleMap.animateCamera(CameraUpdateFactory.zoomTo(15));

        listener.onLocationChanged(null);

    }


    public class MyLocationListener implements LocationListener {

        @Override
        public void onLocationChanged(Location location) {
            if (location != null) {
                latlng = new LatLng(location.getLatitude(),
                        location.getLongitude());

                if (map != null) {
                    map.addMarker(new MarkerOptions()
                            .position(latlng)
                            .title("Current Location"));
                    zoomMapInitial(new LatLng(latitude, longitude), latlng);
                }
            } else {

                SharedPreferences preferences = getSharedPreferences(AppConstants.PREFERENCE_SHARED_PREF_NAME, Context.MODE_PRIVATE);
                String lastLatitude = preferences.getString(AppConstants.PREFERENCE_CURRENT_USED_LAT, "");
                String lastLongitude = preferences.getString(AppConstants.PREFERENCE_CURRENT_USED_LNG, "");

                latlng = new LatLng(Double.parseDouble(lastLatitude), Double.parseDouble(lastLongitude));

                String locationName = preferences.getString(AppConstants.PREFERENCE_CURRENT_USED_LOCATION_NAME, "");
                if (map != null) {
                    map.addMarker(new MarkerOptions()
                            .position(latlng)
                            .title(locationName));
                    zoomMapInitial(new LatLng(latitude, longitude), latlng);
                }

            }

        }

        @Override
        public void onStatusChanged(String s, int i, Bundle bundle) {

        }

        @Override
        public void onProviderEnabled(String s) {

        }

        @Override
        public void onProviderDisabled(String s) {

        }
    }
    protected void zoomMapInitial(LatLng finalPlace, LatLng currenLoc) {
        try {
            LatLngBounds.Builder bc = new LatLngBounds.Builder();

            bc.include(finalPlace);
            bc.include(currenLoc);
            map.moveCamera(CameraUpdateFactory.newLatLngBounds(bc.build(), 200));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
