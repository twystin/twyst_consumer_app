package in.twyst.activities;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.text.TextUtils;
import android.util.Log;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.ActionBarActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.internal.LinkedTreeMap;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.Result;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import in.twyst.R;
import in.twyst.model.BaseResponse;
import in.twyst.model.CheckInSuccessData;
import in.twyst.model.CheckinCode;
import in.twyst.model.CheckinData;
import in.twyst.model.Data;
import in.twyst.model.Voucher;
import in.twyst.service.HttpService;
import in.twyst.util.AppConstants;
import in.twyst.util.TwystProgressHUD;
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
    }

    @Override
    public void onPause() {
        super.onPause();
        mScannerView.stopCamera();           // Stop camera on pause
    }

    @Override
    public void handleResult(Result rawResult) {
        // Do something with the result here
        Log.v("ScannerActivity: ", rawResult.getText()); // Prints scan results
        Log.v("ScannerActivity: ", rawResult.getBarcodeFormat().toString()); // Prints the scan format (qrcode, pdf417 etc.)

        if (!TextUtils.isEmpty(rawResult.getText())) {
            CheckinData checkinData = new CheckinData();
            CheckinCode code = new CheckinCode();
            code.setCode(rawResult.getText());
            checkinData.setCheckinCode(code);

            HttpService.getInstance().postCheckin(getUserToken(), checkinData, new Callback<BaseResponse>() {
                @Override
                public void success(BaseResponse baseResponse, Response response) {
                    if (baseResponse.isResponse()) {
                        Map dataMap = (LinkedTreeMap) baseResponse.getData();
                        String json = GSON.toJson(dataMap);
                        CheckInSuccessData checkInSuccessData = GSON.fromJson(json, CheckInSuccessData.class);
                        Intent intent = new Intent(getApplicationContext(), CheckInSuccessActivity.class);
                        intent.putExtra(AppConstants.INTENT_PARAM_CHECKIN_BUCKS, checkInSuccessData.getTwyst_bucks());
                        intent.putExtra(AppConstants.INTENT_PARAM_CHECKIN_HEADER,checkInSuccessData.getHeader());
                        intent.putExtra(AppConstants.INTENT_PARAM_CHECKIN_LINE1,checkInSuccessData.getLine1());
                        intent.putExtra(AppConstants.INTENT_PARAM_CHECKIN_LINE2,checkInSuccessData.getLine2());
                        intent.putExtra(AppConstants.INTENT_PARAM_CHECKIN_OUTLET_NAME,checkInSuccessData.getOutlet());
                        intent.putExtra(AppConstants.INTENT_PARAM_CHECKIN_CODE,checkInSuccessData.getCode());
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
