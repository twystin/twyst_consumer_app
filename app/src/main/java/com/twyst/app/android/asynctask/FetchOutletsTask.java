package com.twyst.app.android.asynctask;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.text.TextUtils;
import android.text.format.DateFormat;
import com.twyst.app.android.model.BaseResponse;
import com.twyst.app.android.model.DiscoverData;
import com.twyst.app.android.model.Outlet;
import com.twyst.app.android.service.HttpService;
import com.twyst.app.android.util.AppConstants;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by Vipul Sharma on 11/6/2015.
 */
public class FetchOutletsTask {

    private Context mContext;
    private boolean isDiscoverReady=false;
    private boolean isFileReadyToSend =false;
    List<Outlet> mOutletList = new ArrayList<Outlet>();

    public FetchOutletsTask(Context context){
        this.mContext = context;
    }

    public void fetch(){
        registerReceiver();
        final SharedPreferences prefs = mContext.getSharedPreferences(AppConstants.PREFERENCE_SHARED_PREF_NAME, Context.MODE_PRIVATE);
        String latitude = prefs.getString(AppConstants.PREFERENCE_CURRENT_USED_LAT, null);
        String longitude = prefs.getString(AppConstants.PREFERENCE_CURRENT_USED_LNG, null);
        String userToken = prefs.getString(AppConstants.PREFERENCE_USER_TOKEN, "");

        Calendar calendar = Calendar.getInstance();
        Date currentDateTime = calendar.getTime();
        String mDate = (String) DateFormat.format("MM-dd-yyyy", currentDateTime);
        int hoursIn = calendar.get(Calendar.HOUR_OF_DAY);
        int minutes = calendar.get(Calendar.MINUTE);
        String mTime = convertHoursToString(hoursIn) + ":" + convertMinutesToString(minutes);

        int start= 1;
        int end = start + AppConstants.DISCOVER_LIST_PAGESIZE - 1;

        HttpService.getInstance().getRecommendedOutlets(userToken, start, end, latitude, longitude, mDate, mTime, new Callback<BaseResponse<DiscoverData>>() {
            @Override
            public void success(BaseResponse<DiscoverData> arrayListBaseResponse, Response response) {
                ArrayList<Outlet> outlets = arrayListBaseResponse.getData().getOutlets();
                String twystBucks = arrayListBaseResponse.getData().getTwystBucks();
                if (!TextUtils.isEmpty(twystBucks)) {
                    prefs.edit().putInt(AppConstants.PREFERENCE_LAST_TWYST_BUCK, Integer.parseInt(twystBucks)).apply();
                }
                prefs.edit().putString(AppConstants.PREFERENCE_CHECK_FIRST_LAUNCH, "Yes").apply();
                onSuccess(outlets);
            }

            @Override
            public void failure(RetrofitError error) {
                onSuccess(null);
            }
        });
    }

    private void onSuccess(List<Outlet> outletList) {
        mOutletList = outletList;
        isFileReadyToSend = true;
        if (isDiscoverReady)
        {
            checkSendBroadCast();
        }
    }

    private void checkSendBroadCast() {
            Intent intent = new Intent(AppConstants.INTENT_SPLASH_RECO_DOWNLOADED);
            Bundle bundle = new Bundle();
            bundle.putSerializable(AppConstants.INTENT_SPLASH_OUTLET_LIST, (ArrayList<Outlet>) mOutletList);
            intent.putExtras(bundle);
            LocalBroadcastManager.getInstance(mContext.getApplicationContext()).sendBroadcast(intent);

    }

    private String convertHoursToString(int hours) {
        String hoursStr = "";
        if (hours == 0 || hours == 24) {
            hoursStr = "00";
        } else if (hours < 10) {
            hoursStr = "0" + hours;
        } else {
            hoursStr = "" + hours;
        }

        return hoursStr;
    }

    private String convertMinutesToString(int minutes) {
        String timeStr = "";
        if (minutes == 0) {
            timeStr = "00";
        } else {
            timeStr = "" + minutes;
        }
        return timeStr;
    }

    private BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            isDiscoverReady = true;
            if (isFileReadyToSend){
                checkSendBroadCast();
            }
            unRegisterReceiver();

        }
    };

    private void unRegisterReceiver() {
        LocalBroadcastManager.getInstance(mContext).unregisterReceiver(mReceiver);
    }

    private void registerReceiver() {
        LocalBroadcastManager.getInstance(mContext).registerReceiver(mReceiver, mIntentFilter);
    }

    private IntentFilter mIntentFilter = new IntentFilter(AppConstants.INTENT_DISCOVER_STARTED);

}
