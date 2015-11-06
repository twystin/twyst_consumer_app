package com.twyst.app.android.activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Point;
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

    private GoogleMap mMap;
    private double mLat, mLng;
    private String mOutletName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mapview);

        Intent intent = getIntent();
        Bundle extras = intent.getExtras();
        if (extras != null) {

            String lat = extras.getString(AppConstants.INTENT_PARAM_OUTLET_LOCATION_LAT);
            String lng = extras.getString(AppConstants.INTENT_PARAM_OUTLET_LOCATION_LONG);
            mOutletName = extras.getString(AppConstants.INTENT_PARAM_OUTLET_NAME);
            if (lat != null && lng != null) {
                mLat = Double.parseDouble(lat);
                mLng = Double.parseDouble(lng);
            }

        }
        setUpMapIfNeeded();
    }


    private void setUpMapIfNeeded() {
        // Do a null check to confirm that we have not already instantiated the map.
        if (mMap == null) {
            // Try to obtain the map from the SupportMapFragment.
            MapFragment mapFragment = (MapFragment) getFragmentManager()
                    .findFragmentById(R.id.map);
            mapFragment.getMapAsync(this);
            mMap = mapFragment.getMap();
        }
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setMyLocationEnabled(true);

        SharedPreferences preferences = getSharedPreferences(AppConstants.PREFERENCE_SHARED_PREF_NAME, Context.MODE_PRIVATE);
        String lastLatitude = preferences.getString(AppConstants.PREFERENCE_CURRENT_USED_LAT, "");
        String lastLongitude = preferences.getString(AppConstants.PREFERENCE_CURRENT_USED_LNG, "");
        LatLng myLatlng = new LatLng(Double.parseDouble(lastLatitude), Double.parseDouble(lastLongitude));
        String locationName = preferences.getString(AppConstants.PREFERENCE_CURRENT_USED_LOCATION_NAME, "");

        mMap.addMarker(new MarkerOptions()
                .position(myLatlng)
                .title(locationName));

        if (mLat != 0 && mLng != 0) {
            mMap.addMarker(new MarkerOptions()
                    .position(new LatLng(mLat, mLng))
                    .title(mOutletName));
            zoomMapInitial(new LatLng(mLat, mLng), myLatlng);
        } else {
            mMap.moveCamera(CameraUpdateFactory.newLatLng(myLatlng));
            mMap.animateCamera(CameraUpdateFactory.zoomTo(15));
        }

    }

    protected void zoomMapInitial(LatLng finalPlace, LatLng currentPlace) {
        try {
            LatLngBounds bounds = new LatLngBounds.Builder()
                    .include(finalPlace)
                    .include(currentPlace)
                    .build();
            Point displaySize = new Point();
            getWindowManager().getDefaultDisplay().getSize(displaySize);
            mMap.moveCamera(CameraUpdateFactory.newLatLngBounds(bounds, displaySize.x, 250, 30));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
