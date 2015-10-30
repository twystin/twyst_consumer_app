package com.twyst.app.android.activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.appsflyer.AppsFlyerLib;
import com.nispok.snackbar.Snackbar;
import com.nispok.snackbar.SnackbarManager;
import com.nispok.snackbar.enums.SnackbarType;
import com.nispok.snackbar.listeners.ActionClickListener;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.twyst.app.android.R;
import com.twyst.app.android.model.AuthToken;
import com.twyst.app.android.model.BaseResponse;
import com.twyst.app.android.model.OTPCode;
import com.twyst.app.android.service.HttpService;
import com.twyst.app.android.util.AppConstants;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by satish on 06/06/15.
 */
public class PhoneVerificationActivity extends Activity {

    private String otpCodeReaded;
    private SharedPreferences.Editor sharedPreferences;
    private String getTagName() {
        return this.getClass().getSimpleName();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_phone_verification);
        final EditText mobileNo = (EditText) findViewById(R.id.phone);

        final Button continueBtn = (Button) findViewById(R.id.continueBtn);
        sharedPreferences = getSharedPreferences(AppConstants.PREFERENCE_SHARED_PREF_NAME, Context.MODE_PRIVATE).edit();
        continueBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (TextUtils.isEmpty(mobileNo.getText()) || mobileNo.getText().toString().trim().length() < 10) {
                    mobileNo.setError("Please enter valid mobile number");

                } else {
                    mobileNo.setError(null);
                    continueBtn.setEnabled(false);
                    InputMethodManager imm = (InputMethodManager)getSystemService(
                            Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(view.getWindowToken(), 0);

                    findViewById(R.id.circularProgressBar).setVisibility(View.VISIBLE);
                    HttpService.getInstance().getMobileAuthCode(mobileNo.getText().toString(), new Callback<BaseResponse<OTPCode>>() {
                        @Override
                        public void success(final BaseResponse<OTPCode> twystResponse, Response response) {
                            if (twystResponse.isResponse()) {
                                final OTPCode otpCode = twystResponse.getData();
                                Toast.makeText(PhoneVerificationActivity.this, "Verifying Number", Toast.LENGTH_SHORT).show();
                                final Handler handler = new Handler();

                                Runnable runnable = new Runnable() {
                                    int retry = 0;
                                    @Override
                                    public void run() {
                                        if (checkSmsCode()) {
                                            validateOTP(otpCode);

                                        } else {
                                            if (retry < AppConstants.MAX_WAIT_FOR_SMS_IN_SECONDS) {
                                                handler.postDelayed(this, 1000);
                                                Log.d("retry", "" + retry);
                                                retry++;

                                            } else {
                                                sharedPreferences.putString(AppConstants.PREFERENCE_USER_PHONE,otpCode.getPhone());
                                                sharedPreferences.apply();
                                                Intent intent = new Intent(getBaseContext(), OTPVerificationActivity.class);
                                                intent.putExtra(AppConstants.INTENT_PARAM_OTP_PHONE, otpCode.getPhone());
                                                startActivity(intent);
                                                finish();
                                            }

                                        }
                                    }

                                };
                                handler.postDelayed(runnable, 1000);

                            } else {
                                continueBtn.setEnabled(true);
                                findViewById(R.id.circularProgressBar).setVisibility(View.INVISIBLE);
                                Log.d("error message", "" + twystResponse.getMessage());
                                Toast.makeText(PhoneVerificationActivity.this, twystResponse.getMessage(), Toast.LENGTH_SHORT).show();
                                if(twystResponse.getMessage().equalsIgnoreCase("We have already sent you an authentication code.")){
                                    sharedPreferences.putString(AppConstants.PREFERENCE_USER_PHONE,mobileNo.getText().toString());
                                    sharedPreferences.apply();
                                    Intent intent = new Intent(getBaseContext(), OTPVerificationActivity.class);
                                    intent.putExtra(AppConstants.INTENT_PARAM_OTP_PHONE, mobileNo.getText().toString());
                                    startActivity(intent);
                                    finish();
                                }
                            }

                        }

                        @Override
                        public void failure(RetrofitError error) {
                            findViewById(R.id.circularProgressBar).setVisibility(View.INVISIBLE);
                            handleRetrofitError(error);
                        }
                    });

                }
            }
        });

        findViewById(R.id.terms).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String googleDocsUrl = "http://docs.google.com/viewer?url=";
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setDataAndType(Uri.parse(googleDocsUrl + "http://twyst.in/legal/terms_of_use.pdf"), "text/html");
                startActivity(intent);
            }
        });


    }

    @Override
    public void onResume() {
        super.onResume();
        AppsFlyerLib.onActivityResume(this);
    }
    @Override
    public void onPause() {
        super.onPause();
        AppsFlyerLib.onActivityPause(this);
    }

    private void validateOTP(final OTPCode otpCode) {
        HttpService.getInstance().userAuthToken(otpCodeReaded, otpCode.getPhone(), new Callback<BaseResponse<AuthToken>>() {
            @Override
            public void success(BaseResponse<AuthToken> baseResponse, Response response) {
                if (baseResponse.isResponse()) {

                    AuthToken authToken = baseResponse.getData();
                    sharedPreferences.putString(AppConstants.PREFERENCE_USER_TOKEN, authToken.getToken());
                    sharedPreferences.putBoolean(AppConstants.PREFERENCE_PHONE_VERIFIED, true);
                    sharedPreferences.putString(AppConstants.PREFERENCE_USER_PHONE,otpCode.getPhone());
                    sharedPreferences.commit();

                    Toast.makeText(PhoneVerificationActivity.this, "Number Verified!", Toast.LENGTH_SHORT).show();

                    startActivity(new Intent(getBaseContext(), LoginActivity.class));
                    finish();

                } else {
                    sharedPreferences.putString(AppConstants.PREFERENCE_USER_PHONE,otpCode.getPhone());
                    sharedPreferences.apply();
                    Intent intent = new Intent(getBaseContext(), OTPVerificationActivity.class);
                    intent.putExtra(AppConstants.INTENT_PARAM_OTP_PHONE, otpCode.getPhone());
                    startActivity(intent);
                    finish();

                }
            }

            @Override
            public void failure(RetrofitError error) {
                handleRetrofitError(error);
            }
        });
    }

    private boolean checkSmsCode() {
        Cursor cursor = getContentResolver().query(Uri.parse("content://sms/inbox"), null, null, null, null);

        if (cursor.moveToFirst()) {
            do {
                String msgData = "";
                for (int idx = 0; idx < cursor.getColumnCount(); idx++) {
                    msgData += " " + cursor.getColumnName(idx) + ":" + cursor.getString(idx);
                }

            } while (cursor.moveToNext());
        } else {
            // empty box, no SMS
        }


        SharedPreferences pref = getSharedPreferences(AppConstants.PREFERENCE_SHARED_PREF_NAME, Context.MODE_PRIVATE);
        String smsBody = pref.getString(AppConstants.PREFERENCE_SMS_BODY, "");
        if (TextUtils.isEmpty(smsBody)) {
            return false;
        } else {
            Toast.makeText(PhoneVerificationActivity.this, "Reading Verification SMS", Toast.LENGTH_SHORT).show();
            Pattern p = Pattern.compile("[0-9]{4}");
            Matcher m = p.matcher(smsBody);
            if(m.find()){
                otpCodeReaded = m.group(0).trim();
            }else{
                otpCodeReaded = null;
            }

            sharedPreferences.remove(AppConstants.PREFERENCE_SMS_BODY);
            sharedPreferences.commit();
            return true;
        }

    }

    public void handleRetrofitError(RetrofitError error) {
        if (error.getKind() == RetrofitError.Kind.NETWORK) {
            buildAndShowSnackbarWithMessage("No internet connection.");
        } else {
            buildAndShowSnackbarWithMessage("An unexpected error has occurred. Please try after some time.");
        }
        Log.e(getTagName(), "failure", error);
    }

    public void buildAndShowSnackbarWithMessage(String msg) {
        final Snackbar snackbar = Snackbar.with(getApplicationContext())
                .type(SnackbarType.MULTI_LINE)
                .color(getResources().getColor(R.color.snackbar_bg))
                .text(msg)
                .actionLabel("RETRY") // action button label
                .duration(Snackbar.SnackbarDuration.LENGTH_INDEFINITE)
                .swipeToDismiss(false)
                .actionListener(new ActionClickListener() {
                    @Override
                    public void onActionClicked(Snackbar snackbar) {
                        Intent intent = getIntent();
                        overridePendingTransition(0, 0);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                        finish();
                        overridePendingTransition(0, 0);
                        startActivity(intent);
                    }
                });
        snackbar.postDelayed(new Runnable() {
            @Override
            public void run() {
                showSnackbar(snackbar);
            }
        }, 500);

    }

    protected void showSnackbar(Snackbar snackbar) {
        SnackbarManager.show(snackbar, this);
    }
}
