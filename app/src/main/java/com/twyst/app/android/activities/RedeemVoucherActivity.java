package com.twyst.app.android.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.GravityCompat;
import android.text.TextUtils;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.appsflyer.AppsFlyerLib;
import com.squareup.picasso.Picasso;

import com.twyst.app.android.R;
import com.twyst.app.android.model.BaseResponse;
import com.twyst.app.android.model.EventMeta;
import com.twyst.app.android.model.Offer;
import com.twyst.app.android.model.Outlet;
import com.twyst.app.android.model.ReportProblem;
import com.twyst.app.android.model.ShareOffer;
import com.twyst.app.android.model.ShareOfferData;
import com.twyst.app.android.model.UseOfferData;
import com.twyst.app.android.service.HttpService;
import com.twyst.app.android.util.AppConstants;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by rahuls on 5/8/15.
 */
public class RedeemVoucherActivity extends BaseActivity{

    private Offer offer;
    private Outlet outlet;
    private UseOfferData useOfferData;
    boolean image1clicked = false;
    boolean image2clicked = false;
    boolean image3clicked = false;
    private View reportProblemLayout;
    private boolean isPanelShown;
    private View obstructor,obstructor2;
    private String issueReport;
    private TextView tvRedeemGetBucks;

    @Override
    protected String getTagName() {
        return null;
    }

    @Override
    protected int getLayoutResource() {
        return R.layout.redeem_voucher_activity;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setupAsChild = true;
        super.onCreate(savedInstanceState);

        offer = (Offer) getIntent().getExtras().getSerializable(AppConstants.INTENT_PARAM_OFFER_OBJECT);
        outlet = (Outlet) getIntent().getExtras().getSerializable(AppConstants.INTENT_PARAM_OUTLET_OBJECT);
        useOfferData = (UseOfferData) getIntent().getExtras().getSerializable(AppConstants.INTENT_PARAM_USE_OFFER_DATA_OBJECT);

        TextView outletName = (TextView)findViewById(R.id.outletName);
        TextView distance = (TextView)findViewById(R.id.outletDistance);
        TextView voucherCodetext = (TextView)findViewById(R.id.voucherCodetext);
        TextView free = (TextView)findViewById(R.id.free);
        TextView offerDetail1 = (TextView)findViewById(R.id.offerDetail1);
        TextView offerDetail2 = (TextView)findViewById(R.id.offerDetail2);
        TextView tvRedeemGetBucks = (TextView)findViewById(R.id.tvRedeemGetBucks);
        ImageView outletImage =(ImageView)findViewById(R.id.outletImage);
        reportProblemLayout = findViewById(R.id.reportProblemLayout);
        obstructor = findViewById(R.id.obstructor);
        obstructor2 = findViewById(R.id.obstructor2);
        final ImageView imageReport1 =(ImageView)findViewById(R.id.imageReport1);
        final ImageView imageReport2 =(ImageView)findViewById(R.id.imageReport2);
        final ImageView imageReport3 =(ImageView)findViewById(R.id.imageReport3);
        final TextView feedbackET = (TextView)findViewById(R.id.feedbackET);
        RelativeLayout report1 = (RelativeLayout)findViewById(R.id.report1);
        RelativeLayout report2 = (RelativeLayout)findViewById(R.id.report2);
        RelativeLayout report3 = (RelativeLayout)findViewById(R.id.report3);

        findViewById(R.id.obstructor2).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                slideDown(reportProblemLayout);
                image1clicked = false;
                image2clicked = false;
                image3clicked = false;
                imageReport1.setImageDrawable(getResources().getDrawable(R.drawable.icon_check_unchecked));
                imageReport2.setImageDrawable(getResources().getDrawable(R.drawable.icon_check_unchecked));
                imageReport3.setImageDrawable(getResources().getDrawable(R.drawable.icon_check_unchecked));
            }
        });

        findViewById(R.id.reportText).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                slideUp(reportProblemLayout);
                image1clicked = false;
                image2clicked = false;
                image3clicked = false;
                feedbackET.setText("");
                imageReport1.setImageDrawable(getResources().getDrawable(R.drawable.icon_check_unchecked));
                imageReport2.setImageDrawable(getResources().getDrawable(R.drawable.icon_check_unchecked));
                imageReport3.setImageDrawable(getResources().getDrawable(R.drawable.icon_check_unchecked));
            }
        });

        report1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!image1clicked) {
                    image1clicked = true;
                    image2clicked = false;
                    image3clicked = false;
                    issueReport = "Offer rejected by outlet";
                    imageReport1.setImageDrawable(getResources().getDrawable(R.drawable.icon_check_checked));
                    imageReport2.setImageDrawable(getResources().getDrawable(R.drawable.icon_check_unchecked));
                    imageReport3.setImageDrawable(getResources().getDrawable(R.drawable.icon_check_unchecked));

                } else {
                    image1clicked = false;
                    image2clicked = false;
                    image3clicked = false;
                    issueReport = "";
                    imageReport1.setImageDrawable(getResources().getDrawable(R.drawable.icon_check_unchecked));
                    imageReport2.setImageDrawable(getResources().getDrawable(R.drawable.icon_check_unchecked));
                    imageReport3.setImageDrawable(getResources().getDrawable(R.drawable.icon_check_unchecked));
                }

            }
        });

        report2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!image2clicked) {
                    image1clicked = false;
                    image2clicked = true;
                    image3clicked = false;
                    issueReport = "Invalid or expired offer";
                    imageReport1.setImageDrawable(getResources().getDrawable(R.drawable.icon_check_unchecked));
                    imageReport3.setImageDrawable(getResources().getDrawable(R.drawable.icon_check_unchecked));
                    imageReport2.setImageDrawable(getResources().getDrawable(R.drawable.icon_check_checked));
                } else {
                    image1clicked = false;
                    image2clicked = false;
                    image3clicked = false;
                    issueReport = "";
                    imageReport1.setImageDrawable(getResources().getDrawable(R.drawable.icon_check_unchecked));
                    imageReport3.setImageDrawable(getResources().getDrawable(R.drawable.icon_check_unchecked));
                    imageReport2.setImageDrawable(getResources().getDrawable(R.drawable.icon_check_unchecked));
                }

            }
        });

        report3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (!image3clicked) {
                    image1clicked = false;
                    image2clicked = false;
                    image3clicked = true;
                    issueReport = "Offer details incorrect";
                    imageReport1.setImageDrawable(getResources().getDrawable(R.drawable.icon_check_unchecked));
                    imageReport2.setImageDrawable(getResources().getDrawable(R.drawable.icon_check_unchecked));
                    imageReport3.setImageDrawable(getResources().getDrawable(R.drawable.icon_check_checked));
                } else {
                    image1clicked = false;
                    image2clicked = false;
                    image3clicked = false;
                    issueReport = "";
                    imageReport1.setImageDrawable(getResources().getDrawable(R.drawable.icon_check_unchecked));
                    imageReport2.setImageDrawable(getResources().getDrawable(R.drawable.icon_check_unchecked));
                    imageReport3.setImageDrawable(getResources().getDrawable(R.drawable.icon_check_unchecked));
                }


            }
        });

        findViewById(R.id.cancelBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                slideDown(reportProblemLayout);
                feedbackET.setText("");
            }
        });

        findViewById(R.id.sendReportBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (image1clicked || image2clicked || image3clicked || !TextUtils.isEmpty(feedbackET.getText())) {
                    slideDown(reportProblemLayout);


                    ReportProblem reportProblem = new ReportProblem();
                    reportProblem.setEventOutlet(outlet.get_id());
                    EventMeta eventMeta = new EventMeta();
                    eventMeta.setIssue(issueReport);
                    eventMeta.setComment(feedbackET.getText().toString());
                    eventMeta.setOffer(offer.get_id());
                    reportProblem.setEventMeta(eventMeta);


                    HttpService.getInstance().reportProblem(getUserToken(), reportProblem, new Callback<BaseResponse>() {
                        @Override
                        public void success(BaseResponse baseResponse, Response response) {
                            feedbackET.setText("");
                            Toast.makeText(RedeemVoucherActivity.this, "Thanks for letting us know.", Toast.LENGTH_SHORT).show();

                        }

                        @Override
                        public void failure(RetrofitError error) {

                        }
                    });

                } else {
                    Toast.makeText(RedeemVoucherActivity.this, "Please fill all the required fields.", Toast.LENGTH_SHORT).show();
                }

            }
        });

        findViewById(R.id.btnNow).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(RedeemVoucherActivity.this, UploadBillActivity.class);
                intent.putExtra(AppConstants.INTENT_PARAM_UPLOAD_BILL_OUTLET_ID, outlet.get_id());
                intent.putExtra(AppConstants.INTENT_PARAM_UPLOAD_BILL_OUTLET_ADDRESS, outlet.getAddress());
                intent.putExtra(AppConstants.INTENT_PARAM_UPLOAD_BILL_OUTLET_NAME, outlet.getName());
                startActivity(intent);
            }
        });

        findViewById(R.id.btnLater).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(RedeemVoucherActivity.this,DiscoverActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            }
        });

        Picasso picasso = Picasso.with(getApplicationContext());
        picasso.setIndicatorsEnabled(AppConstants.DEGUG_PICASSO);
        picasso.setLoggingEnabled(AppConstants.DEGUG_PICASSO);
        picasso.load(outlet.getBackground())
                .noFade()
                .fit()
                .into(outletImage);

        String address = "";
        boolean added = false;
        if (!TextUtils.isEmpty(outlet.getLocality_1())) {
            address += outlet.getLocality_1();
            added = true;
        }

        if (!TextUtils.isEmpty(outlet.getLocality_2())) {
            if (added) {
                address += ", ";
            }
            address += outlet.getLocality_2();
            added = true;
        }

        if (!TextUtils.isEmpty(outlet.getCity())) {
            if (added) {
                address += ", ";
            }
            address += outlet.getCity();
        }

        distance.setText(address);

        outletName.setText(outlet.getName());

        if(useOfferData!=null){
            if(useOfferData.getCode()!=null){
                voucherCodetext.setText(useOfferData.getCode());
            }
        }
        else {
            voucherCodetext.setText(offer.getCode());
        }

        int amountToDisplay;
        if(useOfferData!=null){
            amountToDisplay = offer.getOfferCost();
        }else{
            amountToDisplay = 50;
        }

        free.setText(offer.getHeader());
        String redeemCodeGetTwystBucks = getResources().getString(R.string.redeem_code_get_twyst_bucks);
        String redeemCodeGetTwystBucksMsg = String.format(redeemCodeGetTwystBucks,amountToDisplay);
        tvRedeemGetBucks.setText(redeemCodeGetTwystBucksMsg);

        if(!TextUtils.isEmpty(offer.getDescription())){
            offerDetail1.setText(offer.getDescription());
            offerDetail2.setVisibility(View.GONE);
        }else{
            offerDetail1.setText(offer.getLine1());
            offerDetail2.setText(offer.getLine2());
        }

        ((TextView) findViewById(R.id.tvTermsConditions)).setText(getFormattedTermsConditions(offer.getTerms()));

        findViewById(R.id.layoutTermsConditions).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                viewMoreDialog(getFormattedTermsConditions(offer.getTerms()));
            }
        });


        hideProgressHUDInLayout();

        findViewById(R.id.outletShare).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String text2 = "https://play.google.com/store/apps/details?id="+getApplication().getPackageName()+"&ah=-smMDxRK7pmXEK32N7mSNcbZ2ZM";
                String text = "Hey Checkout the offers being offered by " + outlet.getName() + " outlet on \"Twyst\" app.\n" + "Download Now:"+text2;
                showShareIntents("Share using", text);


                ShareOffer shareOffer = new ShareOffer();
                ShareOfferData shareOfferData = new ShareOfferData();

                shareOfferData.setOfferId(offer.get_id());
                shareOffer.setOutletId(outlet.get_id());

                shareOffer.setShareOfferData(shareOfferData);
                HttpService.getInstance().shareOffer(getUserToken(), shareOffer, new Callback<BaseResponse>() {
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

    @Override
    public void onBackPressed() {
        if (drawerOpened) {
            drawerLayout.closeDrawer(GravityCompat.START);
        }else {
            Intent intent = new Intent(RedeemVoucherActivity.this,DiscoverActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            finish();
        }

    }

    public void slideUp(final View view) {
        if(!isPanelShown) {
            // Show the panel
            Animation bottomUp = AnimationUtils.loadAnimation(this,
                    R.anim.slide_up);
            final AlphaAnimation animation1 = new AlphaAnimation(0.0f, 0.7f);
            animation1.setDuration(150);
            obstructor.setVisibility(View.VISIBLE);
            obstructor2.setVisibility(View.VISIBLE);

            reportProblemLayout.startAnimation(bottomUp);
            obstructor.startAnimation(animation1);
            reportProblemLayout.setVisibility(View.VISIBLE);
            isPanelShown = true;
        }
    }

    public void slideDown(final View view){
        if(isPanelShown){
            // Hide the Panel
            final AlphaAnimation animation1 = new AlphaAnimation(0.7f, 0.0f);
            animation1.setDuration(100);
            obstructor.setVisibility(View.GONE);
            obstructor2.setVisibility(View.GONE);
            Animation bottomDown = AnimationUtils.loadAnimation(this,
                    R.anim.slide_out_down);

            reportProblemLayout.startAnimation(bottomDown);
            obstructor.startAnimation(animation1);
            reportProblemLayout.setVisibility(View.GONE);
            isPanelShown = false;
        }
    }

}
