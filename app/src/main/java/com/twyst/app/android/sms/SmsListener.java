package com.twyst.app.android.sms;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.provider.Telephony;
import android.telephony.SmsMessage;
import android.text.TextUtils;
import android.util.Log;

import com.twyst.app.android.util.AppConstants;

/**
 * Created by satish on 20/06/15.
 */
public class SmsListener extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d(getClass().getSimpleName(), "Intent action: " + intent.getAction());
        if (Telephony.Sms.Intents.SMS_RECEIVED_ACTION.equals(intent.getAction())) {
            Log.d(SmsListener.class.getSimpleName(), "SMS Received...");
            Bundle bundle = intent.getExtras();
            SmsMessage[] msgs = null;
            String msg_from;
            if (bundle != null) {
                try {
                    Object[] pdus = (Object[]) bundle.get("pdus");
                    msgs = new SmsMessage[pdus.length];
                    for (int i = 0; i < msgs.length; i++) {
                        msgs[i] = SmsMessage.createFromPdu((byte[]) pdus[i]);
                        msg_from = msgs[i].getOriginatingAddress();
                        String msgBody = msgs[i].getMessageBody();
                        Log.d(SmsListener.class.getSimpleName(), "SMS from...: " + msg_from);
                        Log.d(SmsListener.class.getSimpleName(), "SMS body...: " + msgBody);
                        if (!TextUtils.isEmpty(msgBody) && msgBody.toLowerCase().contains("twyst verification code")) {
                            SharedPreferences.Editor editor = context.getSharedPreferences(AppConstants.PREFERENCE_SHARED_PREF_NAME, Context.MODE_PRIVATE).edit();
                            editor.putString(AppConstants.PREFERENCE_SMS_BODY, msgBody).commit();
                            Log.d(SmsListener.class.getSimpleName(), "SMS BODY SAVED");
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
