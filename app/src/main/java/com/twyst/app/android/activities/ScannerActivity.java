package com.twyst.app.android.activities;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.text.TextUtils;
import android.util.Log;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.appsflyer.AppsFlyerLib;
import com.google.gson.Gson;
import com.google.gson.internal.LinkedTreeMap;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.Result;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.twyst.app.android.R;
import com.twyst.app.android.model.BaseResponse;
import com.twyst.app.android.model.CheckInSuccessData;
import com.twyst.app.android.model.CheckinCode;
import com.twyst.app.android.model.CheckinData;
import com.twyst.app.android.service.HttpService;
import com.twyst.app.android.util.AppConstants;

import org.w3c.dom.Text;

import me.dm7.barcodescanner.zxing.ZXingScannerView;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by vivek on 04/08/15.
 */
public class ScannerActivity extends ActionBarActivity implements ZXingScannerView.ResultHandler {
    private ZXingScannerView mScannerView;
    private Gson GSON = new Gson();

    @Override
    public void onCreate(Bundle state) {
        super.onCreate(state);
        mScannerView = new ZXingScannerView(this);   // Programmatically initialize the scanner view
        mScannerView.setAutoFocus(true);
        List<BarcodeFormat> formats = new ArrayList<>();
        formats.add(BarcodeFormat.QR_CODE);
        mScannerView.setFormats(formats);
        setContentView(mScannerView);                // Set the scanner view as the content view
    }

    @Override
    public void onResume() {
        super.onResume();
        mScannerView.setResultHandler(this); // Register ourselves as a handler for scan results.
        mScannerView.startCamera();          // Start camera on resume
        AppsFlyerLib.onActivityResume(this);
    }

    @Override
    public void onPause() {
        super.onPause();
        mScannerView.stopCamera();           // Stop camera on pause
        AppsFlyerLib.onActivityPause(this);
    }

    @Override
    public void handleResult(Result rawResult) {
        // Do something with the result here
        Log.v("ScannerActivity: ", rawResult.getText()); // Prints scan results
        Log.v("ScannerActivity: ", rawResult.getBarcodeFormat().toString()); // Prints the scan format (qrcode, pdf417 etc.)

        if (!TextUtils.isEmpty(rawResult.getText())) {
            CheckinData checkinData = new CheckinData();
            CheckinCode code = new CheckinCode();
            String result = rawResult.getText();
            String codeRetrieved = "";
            if (result!=null &!TextUtils.isEmpty(result)){
                codeRetrieved = result.substring(result.length() - 6);
            }
            code.setCode(codeRetrieved);
            checkinData.setCheckinCode(code);

            HttpService.getInstance().postCheckin(getUserToken(), checkinData, new Callback<BaseResponse>() {
                @Override
                public void success(BaseResponse baseResponse, Response response) {
                    if (baseResponse.isResponse()) {
                        Map dataMap = (LinkedTreeMap) baseResponse.getData();
                        String json = GSON.toJson(dataMap);
                        CheckInSuccessData checkInSuccessData = GSON.fromJson(json, CheckInSuccessData.class);
                        Intent intent = new Intent(getApplicationContext(), CheckInSuccessActivity.class);
                        intent.putExtra(AppConstants.INTENT_PARAM_CHECKIN_HEADER,checkInSuccessData.getHeader());
                        intent.putExtra(AppConstants.INTENT_PARAM_CHECKIN_LINE1,checkInSuccessData.getLine1());
                        intent.putExtra(AppConstants.INTENT_PARAM_CHECKIN_LINE2,checkInSuccessData.getLine2());
                        intent.putExtra(AppConstants.INTENT_PARAM_CHECKIN_OUTLET_NAME,checkInSuccessData.getOutlet_name());
                        intent.putExtra(AppConstants.INTENT_PARAM_CHECKIN_OUTLET_ID, checkInSuccessData.getOutlet_id());
                        intent.putExtra(AppConstants.INTENT_PARAM_CHECKIN_CODE,checkInSuccessData.getCode());
                        intent.putExtra(AppConstants.INTENT_PARAM_CHECKIN_COUNT,checkInSuccessData.getCheckins_to_go());
                        startActivity(intent);
                    } else {

                        String errorString = baseResponse.getData().toString();
                        String[] strings = errorString.trim().split("\\s*-\\s*");
                        String errorMsg = strings[1];
                        checkInErrorDialog(errorMsg);

                    }
                }

                @Override
                public void failure(RetrofitError error) {

                }
            });

        } else {
            Toast.makeText(this, "Not able to scan", Toast.LENGTH_LONG).show();

        }


    }

    private void checkInErrorDialog(String errorMsg) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater li = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View dialogView = li.inflate(R.layout.dialog_check_in_error, null);

        TextView extendText = (TextView) dialogView.findViewById(R.id.message);
        extendText.setText(errorMsg);

        builder.setView(dialogView);
        final AlertDialog dialog = builder.create();
        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();

        dialogView.findViewById(R.id.extendBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
                finish();

            }
        });

        dialogView.findViewById(R.id.cancelExtendBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
                finish();
            }
        });

    }

    private String getUserToken() {
        SharedPreferences prefs = this.getSharedPreferences(AppConstants.PREFERENCE_SHARED_PREF_NAME, Context.MODE_PRIVATE);
        return prefs.getString(AppConstants.PREFERENCE_USER_TOKEN, "");

    }
}
