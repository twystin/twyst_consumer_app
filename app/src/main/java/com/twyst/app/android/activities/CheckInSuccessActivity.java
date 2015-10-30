package com.twyst.app.android.activities;

import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.appsflyer.AppsFlyerLib;
import com.twyst.app.android.R;
import com.twyst.app.android.model.BaseResponse;
import com.twyst.app.android.model.ShareOutlet;
import com.twyst.app.android.service.HttpService;
import com.twyst.app.android.util.AppConstants;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by anilkg on 24/8/15.
 */
public class CheckInSuccessActivity extends BaseActivity {
    @Override
    protected String getTagName() {
        return this.getClass().getSimpleName();
    }

    @Override
    protected int getLayoutResource() {
        return R.layout.checkin_succes_layout;
    }

    private LinearLayout checkInShareBtn;
    private LinearLayout checkInFeedBackBtn;
    private TextView checkInBucks;
    private TextView checkInOutletName;
    private TextView checkInTextHeader;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setupAsChild = true;
        super.onCreate(savedInstanceState);


        final String outletname=getIntent().getStringExtra(AppConstants.INTENT_PARAM_CHECKIN_OUTLET_NAME);
        final String outletId=getIntent().getStringExtra(AppConstants.INTENT_PARAM_CHECKIN_OUTLET_ID);
        String checkInHeader=getIntent().getStringExtra(AppConstants.INTENT_PARAM_CHECKIN_HEADER);
        String checkInLine1=getIntent().getStringExtra(AppConstants.INTENT_PARAM_CHECKIN_LINE1);
        String checkInLine2=getIntent().getStringExtra(AppConstants.INTENT_PARAM_CHECKIN_LINE2);
        String  checkInCode=getIntent().getStringExtra(AppConstants.INTENT_PARAM_CHECKIN_CODE);
        int checkInCount=getIntent().getIntExtra(AppConstants.INTENT_PARAM_CHECKIN_COUNT, 0);

        if (checkInLine2==null){
            checkInLine2=" ";
        }

        checkInShareBtn = (LinearLayout) findViewById(R.id.checkInShareButton);
        checkInFeedBackBtn = (LinearLayout) findViewById(R.id.checkInFeedbackButton);
        checkInOutletName = (TextView) findViewById(R.id.checkInOutletName);
        checkInTextHeader = (TextView) findViewById(R.id.checkInTextHeader);
        checkInBucks = (TextView) findViewById(R.id.checkInBucks);
        checkInBucks.setText("You have earned 50 Twyst bucks for this!");

        if (checkInCount>0){ //Checkins to go
            String outletNameText = "You have checked in at "
                    + " <font color='"
                    + getResources().getColor(R.color.colorPrimaryDark) + "'><b>" + outletname
                    + "</b></font>"
                    + " and you are "
                    + " <font color='"
                    + getResources().getColor(R.color.colorPrimaryDark) + "'><b>" + String.valueOf(checkInCount)
                    + "</b></font>"
                    + " check-ins away to unlock your reward!"
                    ;
            checkInOutletName.setText(Html.fromHtml(outletNameText),
                    TextView.BufferType.SPANNABLE);

            checkInTextHeader.setVisibility(View.GONE);

        }else{ //Unlocked a reward
            String outletNameText = "You have checked in at "
                    + " <font color='"
                    + getResources().getColor(R.color.colorPrimaryDark) + "'><b>" + outletname
                    + "</b></font>"
                    + " and unlocked a reward!";
            checkInOutletName.setText(Html.fromHtml(outletNameText),
                    TextView.BufferType.SPANNABLE);

            String walletText = "Your voucher will be available in your"
                    + " <font color='"
                    + getResources().getColor(R.color.colorPrimaryDark) + "'><b>" + "Wallet"
                    + "</b></font>"
                    + " after 3 hours.";

            String headerText = "You get"
                    + " <font color='"
                    + getResources().getColor(R.color.colorPrimaryDark) + "'><b>" + checkInHeader+" "+checkInLine1+" "+checkInLine2
                    + "</b></font>"
                    + " on your next visit/order. "
                    +walletText;
            checkInTextHeader.setText(Html.fromHtml(headerText),
                    TextView.BufferType.SPANNABLE);

        }


        findViewById(R.id.share).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String text2 = "https://play.google.com/store/apps/details?id=" + getApplication().getPackageName() + "&ah=-smMDxRK7pmXEK32N7mSNcbZ2ZM";
                String text = "Hey Checkout the offers being offered by " + outletname + " outlet on \"Twyst\" app.\n" + "Download Now:" + text2;
                showShareIntents("Share using", text);

                ShareOutlet shareOutlet = new ShareOutlet();
                shareOutlet.setOutletId(outletId);
                HttpService.getInstance().shareOutlet(getUserToken(), shareOutlet, new Callback<BaseResponse>() {
                    @Override
                    public void success(BaseResponse baseResponse, Response response) {

                    }

                    @Override
                    public void failure(RetrofitError error) {
                        handleRetrofitError(error);
                    }
                });
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
}
