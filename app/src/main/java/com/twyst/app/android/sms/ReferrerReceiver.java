package com.twyst.app.android.sms;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;

import com.twyst.app.android.util.AppConstants;

/**
 * Created by rahuls on 13/8/15.
 */
public class ReferrerReceiver extends BroadcastReceiver{
    @Override
    public void onReceive(Context context, Intent intent) {
        String referrer = intent.getStringExtra("referrer");
        String nString = "twyst://?"+Uri.decode(referrer);
        Uri code = Uri.parse(nString);
        String phoneNumber = code.getQueryParameter("code");
        String promotionCode = code.getQueryParameter("promotion");
        Log.i("referrer code", "" + phoneNumber);
        Log.i("referrer promotion", "" + promotionCode);
        if("appinvite".equals(promotionCode) && !TextUtils.isEmpty(phoneNumber)) {
            SharedPreferences.Editor sharedPreferences = context.getSharedPreferences(AppConstants.PREFERENCE_SHARED_PREF_NAME, Context.MODE_PRIVATE).edit();
            sharedPreferences.putString(AppConstants.PREFERENCE_USER_REFERRAL, phoneNumber);
            sharedPreferences.apply();
        }
    }

}
