package in.twyst.activities;

import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import in.twyst.R;
import in.twyst.model.BaseResponse;
import in.twyst.model.Outlet;
import in.twyst.model.ShareOutlet;
import in.twyst.service.HttpService;
import in.twyst.util.AppConstants;
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

        if (checkInLine2==null){
            checkInLine2=" ";
        }

        checkInShareBtn = (LinearLayout) findViewById(R.id.checkInShareButton);
        checkInFeedBackBtn = (LinearLayout) findViewById(R.id.checkInFeedbackButton);
        checkInOutletName = (TextView) findViewById(R.id.checkInOutletName);
        checkInTextHeader = (TextView) findViewById(R.id.checkInTextHeader);
        checkInBucks = (TextView) findViewById(R.id.checkInBucks);
        checkInBucks.setText("You have earned 50 Twyst bucks for this!");

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

        final String outletName = "You have checked in at "
                + " <font color='"
                + getResources().getColor(R.color.colorPrimaryDark) + "'><b>" + outletname
                + "</b></font>"
                + " and unlocked a reward!";
        checkInOutletName.setText(Html.fromHtml(outletName),
                TextView.BufferType.SPANNABLE);

        findViewById(R.id.share).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String text2 = "https://play.google.com/store/apps/details?id="+getApplication().getPackageName()+"&ah=-smMDxRK7pmXEK32N7mSNcbZ2ZM";
                String text = "Hey Checkout the offers being offered by " + outletname + " outlet on \"Twyst\" app.\n" + "Download Now:"+text2;
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
}
