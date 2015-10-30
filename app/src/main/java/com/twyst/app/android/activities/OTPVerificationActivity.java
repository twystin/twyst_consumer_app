package com.twyst.app.android.activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.Toast;

import com.appsflyer.AppsFlyerLib;
import com.nispok.snackbar.Snackbar;
import com.nispok.snackbar.SnackbarManager;
import com.nispok.snackbar.enums.SnackbarType;
import com.nispok.snackbar.listeners.ActionClickListener;

import com.twyst.app.android.R;
import com.twyst.app.android.model.AuthToken;
import com.twyst.app.android.model.BaseResponse;
import com.twyst.app.android.model.OTPCode;
import com.twyst.app.android.service.HttpService;
import com.twyst.app.android.util.AppConstants;
import com.twyst.app.android.util.TwystProgressHUD;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by satish on 06/06/15.
 */
public class OTPVerificationActivity extends Activity {

    private String getTagName() {
        return this.getClass().getSimpleName();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_otp_verification);
        Intent intent = getIntent();

        final String phone = intent.getStringExtra(AppConstants.INTENT_PARAM_OTP_PHONE);

        final EditText eTCode = (EditText) findViewById(R.id.otpCode);

        findViewById(R.id.submitBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (TextUtils.isEmpty(eTCode.getText()) || eTCode.getText().toString().trim().length() < 4) {
                    eTCode.setError("Invalid code !!");
                } else {
                    eTCode.setError(null);
                    final TwystProgressHUD twystProgressHUD = TwystProgressHUD.show(OTPVerificationActivity.this, false, null);
                    HttpService.getInstance().userAuthToken(eTCode.getText().toString(),phone, new Callback<BaseResponse<AuthToken>>() {
                        @Override
                        public void success(BaseResponse<AuthToken> baseResponse, Response response) {
                            twystProgressHUD.dismiss();
                            if (baseResponse.isResponse()) {

                                // AuthToken authToken = new Gson().fromJson(new Gson().toJson(baseResponse.getData()), AuthToken.class);

                                AuthToken authToken = baseResponse.getData();
                                saveUserToken(authToken.getToken());
                                Intent intent = new Intent(getBaseContext(), LoginActivity.class);
                                startActivity(intent);
                                finish();
                            } else {
                                Toast.makeText(OTPVerificationActivity.this, baseResponse.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void failure(RetrofitError error) {
                            twystProgressHUD.dismiss();
                            handleRetrofitError(error);
                        }
                    });

                }
            }
        });

        findViewById(R.id.resend).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final TwystProgressHUD twystProgressHUD = TwystProgressHUD.show(OTPVerificationActivity.this, false, null);
                HttpService.getInstance().getMobileAuthCode(phone, new Callback<BaseResponse<OTPCode>>() {
                    @Override
                    public void success(BaseResponse<OTPCode> baseResponse, Response response) {
                        twystProgressHUD.dismiss();
                        if (baseResponse.isResponse()) {

                            Toast.makeText(OTPVerificationActivity.this, "Code resent successfully", Toast.LENGTH_SHORT).show();

                        } else {
                            Log.d("error message", "" + baseResponse.getMessage());
                        }
                    }

                    @Override
                    public void failure(RetrofitError error) {
                        twystProgressHUD.dismiss();
                        handleRetrofitError(error);
                    }
                });
            }
        });

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);

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

    private void saveUserToken(String userToken) {
        SharedPreferences.Editor sharedPreferences = getSharedPreferences(AppConstants.PREFERENCE_SHARED_PREF_NAME, Context.MODE_PRIVATE).edit();
        sharedPreferences.putString(AppConstants.PREFERENCE_USER_TOKEN, userToken);
        sharedPreferences.putBoolean(AppConstants.PREFERENCE_PHONE_VERIFIED, true);
        sharedPreferences.commit();
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
//                .color(getResources().getColor(R.color.snackbar_bg))
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
                showSnackbar(snackbar); // activity where it is displayed
            }
        }, 500);

    }

    protected void showSnackbar(Snackbar snackbar) {
        SnackbarManager.show(snackbar, this);
    }
}
