package in.twyst.activities;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.SystemClock;
import android.provider.CalendarContract;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.balysv.materialripple.MaterialRippleLayout;
import com.google.gson.Gson;
import com.google.gson.internal.LinkedTreeMap;
import com.squareup.picasso.Picasso;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import in.twyst.R;
import in.twyst.alarm.NotificationPublisherReceiver;
import in.twyst.model.AvailableNext;
import in.twyst.model.BaseResponse;
import in.twyst.model.EventMeta;
import in.twyst.model.LikeOffer;
import in.twyst.model.LikeOfferMeta;
import in.twyst.model.Offer;
import in.twyst.model.Outlet;
import in.twyst.model.OutletDetailData;
import in.twyst.model.ReportProblem;
import in.twyst.model.ShareOffer;
import in.twyst.model.ShareOfferData;
import in.twyst.model.UseOffer;
import in.twyst.model.UseOfferData;
import in.twyst.model.UseOfferMeta;
import in.twyst.model.Voucher;
import in.twyst.service.HttpService;
import in.twyst.util.AppConstants;
import in.twyst.util.TwystProgressHUD;
import in.twyst.util.Utils;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by rahuls on 16/7/15.
 */
public class OfferDetailActivity extends BaseActivity {

    private Offer offer;
    private String day;
    private String time;
    private Outlet outlet;
    private Calendar calendar;
    private String term1 = null;
    private String term2 = null;
    private String term3 = null;
    private String term4 = null;
    boolean image1clicked = false;
    boolean image2clicked = false;
    boolean image3clicked = false;
    int count;
    private String extendDate, extendDate2,lapsedDate;
    private View reportProblemLayout;
    private boolean isPanelShown;
    private View obstructor, obstructor2;
    private Gson gson = new Gson();
    private String issueReport;
    private String itemValue, idValue;
    List<String> values = new ArrayList<>();
    List<String> outletID = new ArrayList<>();
    int itemPosition = -2;
    TextView locationText;
    boolean flag;
    String expiryTextCoupon = "";
    TextView date;

    @Override
    protected String getTagName() {
        return null;
    }

    @Override
    protected int getLayoutResource() {
        return R.layout.activity_offer_detail;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setupAsChild = true;
        super.onCreate(savedInstanceState);
        Bundle data = getIntent().getExtras();

        if(getIntent().getStringExtra(AppConstants.INTENT_PARAM_OUTLET_ID)!=null && getIntent().getStringExtra(AppConstants.INTENT_PARAM_OFFER_ID)!=null){
            String outletId = getIntent().getStringExtra(AppConstants.INTENT_PARAM_OUTLET_ID);
            String offerId = getIntent().getStringExtra(AppConstants.INTENT_PARAM_OFFER_ID);
            outletDetail(outletId,offerId);
        }else {

            offer = (Offer) data.getSerializable(AppConstants.INTENT_PARAM_OFFER_OBJECT);
            outlet = (Outlet) data.getSerializable(AppConstants.INTENT_PARAM_OUTLET_OBJECT);
            setup();
        }
    }

    private void setup(){
        TextView outletName = (TextView) findViewById(R.id.outletName);
        TextView distance = (TextView) findViewById(R.id.outletDistance);
        TextView type_offer = (TextView) findViewById(R.id.type_offer);
        TextView coupon_text1 = (TextView) findViewById(R.id.coupon_text1);
        TextView coupon_text2 = (TextView) findViewById(R.id.coupon_text2);
        ImageView outletImage = (ImageView) findViewById(R.id.outletImage);
        date = (TextView) findViewById(R.id.expiryDate);
        ImageView buttonImage = (ImageView) findViewById(R.id.btnImage);
        TextView btntext = (TextView) findViewById(R.id.btntext);
        LinearLayout loginBtn = (LinearLayout) findViewById(R.id.loginBtn);
        final TextView likeText = (TextView) findViewById(R.id.likeText);
        ImageView buttonAct = (ImageView) findViewById(R.id.buttonAct);
        locationText = (TextView)findViewById(R.id.locationText);
        reportProblemLayout = findViewById(R.id.reportProblemLayout);
        obstructor = findViewById(R.id.obstructor);
        obstructor2 = findViewById(R.id.obstructor2);
        final ImageView imageReport1 = (ImageView) findViewById(R.id.imageReport1);
        final ImageView imageReport2 = (ImageView) findViewById(R.id.imageReport2);
        final ImageView imageReport3 = (ImageView) findViewById(R.id.imageReport3);
        final TextView feedbackET = (TextView) findViewById(R.id.feedbackET);
        RelativeLayout report1 = (RelativeLayout) findViewById(R.id.report1);
        RelativeLayout report2 = (RelativeLayout) findViewById(R.id.report2);
        RelativeLayout report3 = (RelativeLayout) findViewById(R.id.report3);
        TextView buckText = (TextView) findViewById(R.id.buckText);
        isPanelShown = false;

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

        TextView reportText = (TextView) findViewById(R.id.reportText);

        reportText.setOnClickListener(new View.OnClickListener() {
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
                            Toast.makeText(OfferDetailActivity.this, "Thanks for letting us know.", Toast.LENGTH_SHORT).show();

                        }

                        @Override
                        public void failure(RetrofitError error) {

                        }
                    });

                } else {
                    Toast.makeText(OfferDetailActivity.this, "Please fill all the required fields.", Toast.LENGTH_SHORT).show();
                }

            }
        });


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

        Picasso picasso = Picasso.with(getApplicationContext());
        picasso.setIndicatorsEnabled(AppConstants.DEGUG_PICASSO);
        picasso.setLoggingEnabled(AppConstants.DEGUG_PICASSO);
        picasso.load(outlet.getBackground())
                .noFade()
                .fit()
                .into(outletImage);


        findViewById(R.id.viewAllOffer).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(OfferDetailActivity.this, OutletDetailsActivity.class);
                intent.putExtra(AppConstants.INTENT_PARAM_OUTLET_ID, outlet.get_id());
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);

            }
        });

        if (offer.isLike()) {
            likeText.setCompoundDrawablesWithIntrinsicBounds(getResources().getDrawable(R.drawable.offer_detail_thumb_icon_yellow), null, null, null);
        } else {
            likeText.setCompoundDrawablesWithIntrinsicBounds(getResources().getDrawable(R.drawable.offer_detail_thumb_icon), null, null, null);
        }

        if (offer.getLikeCount() > 0) {
            likeText.setText("" + offer.getLikeCount());

        } else {
            likeText.setText("");
        }

        likeText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (!offer.isLike()) {
                    final TwystProgressHUD twystProgressHUD = TwystProgressHUD.show(OfferDetailActivity.this, false, null);
                    likeText.setCompoundDrawablesWithIntrinsicBounds(getResources().getDrawable(R.drawable.offer_detail_thumb_icon_yellow), null, null, null);
                    final LikeOffer likeOffer = new LikeOffer();
                    LikeOfferMeta likeOfferMeta = new LikeOfferMeta();
                    likeOfferMeta.setOutlet(outlet.get_id());
                    likeOfferMeta.setOffer(offer.get_id());
                    likeOffer.setOfferMeta(likeOfferMeta);

                    HttpService.getInstance().postLikeOffer(getUserToken(), likeOffer, new Callback<BaseResponse>() {
                        @Override
                        public void success(BaseResponse baseResponse, Response response) {
                            twystProgressHUD.dismiss();
                            if (baseResponse.isResponse()) {

                                offer.setLike(true);
                                likeText.setText(String.valueOf(offer.getLikeCount() + 1));
                                offer.setLikeCount(offer.getLikeCount() + 1);
                                likeText.setCompoundDrawablesWithIntrinsicBounds(getResources().getDrawable(R.drawable.offer_detail_thumb_icon_yellow), null, null, null);
                            } else {
                                likeText.setCompoundDrawablesWithIntrinsicBounds(getResources().getDrawable(R.drawable.offer_detail_thumb_icon), null, null, null);
                            }
                        }

                        @Override
                        public void failure(RetrofitError error) {
                            twystProgressHUD.dismiss();
                            handleRetrofitError(error);
                        }
                    });
                } else {

                    final TwystProgressHUD twystProgressHUD = TwystProgressHUD.show(OfferDetailActivity.this, false, null);
                    likeText.setCompoundDrawablesWithIntrinsicBounds(getResources().getDrawable(R.drawable.offer_detail_thumb_icon), null, null, null);
                    final LikeOffer likeOffer = new LikeOffer();
                    LikeOfferMeta likeOfferMeta = new LikeOfferMeta();
                    likeOfferMeta.setOutlet(outlet.get_id());
                    likeOfferMeta.setOffer(offer.get_id());
                    likeOffer.setOfferMeta(likeOfferMeta);

                    HttpService.getInstance().postUnLikeOffer(getUserToken(), likeOffer, new Callback<BaseResponse>() {
                        @Override
                        public void success(BaseResponse baseResponse, Response response) {
                            twystProgressHUD.dismiss();
                            if (baseResponse.isResponse()) {

                                offer.setLike(false);

                                if(offer.getLikeCount()==1){
                                    likeText.setText(String.valueOf(0));
                                }else {
                                    likeText.setText(String.valueOf(offer.getLikeCount() - 1));

                                }
                                offer.setLikeCount(offer.getLikeCount() - 1);
                                likeText.setCompoundDrawablesWithIntrinsicBounds(getResources().getDrawable(R.drawable.offer_detail_thumb_icon), null, null, null);
                            } else {
                                likeText.setCompoundDrawablesWithIntrinsicBounds(getResources().getDrawable(R.drawable.offer_detail_thumb_icon_yellow), null, null, null);
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

        String expiryText = "";
        String lapseText = "";
        if ("coupon".equalsIgnoreCase(offer.getType())) {
            if (!TextUtils.isEmpty(offer.getLapseDate())) {
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
                try {
                    Date lapseDate = sdf.parse(offer.getLapseDate());
                    Date expiryDate = sdf.parse(offer.getExpiry());
                    long days = TimeUnit.DAYS.convert(lapseDate.getTime() - System.currentTimeMillis(), TimeUnit.MILLISECONDS);

                    long hours = TimeUnit.HOURS.convert(lapseDate.getTime() - System.currentTimeMillis(), TimeUnit.MILLISECONDS);
                    SimpleDateFormat extendDateFormat = new SimpleDateFormat("dd-MMM yyyy");
                    extendDate = extendDateFormat.format(expiryDate);
                    lapsedDate = extendDateFormat.format(lapseDate);

                    SimpleDateFormat extendDateFormat2 = new SimpleDateFormat("MM/dd/yyyy");
                    extendDate2 = extendDateFormat2.format(expiryDate);
                    Log.i("days", "" + days);
                    if (offer.isAvailableNow()) {

                        if (days == 0) {
                            lapseText = hours + ((hours == 1) ? " hours " : " hours ") + "left";
                            expiryTextCoupon = hours + ((hours == 1) ? " hours " : " hours ") + "left";
                        } else if (days > 7) {
                            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("d-MMM");
                            lapseText = "valid till " + simpleDateFormat.format(lapseDate).toLowerCase();
                            expiryTextCoupon = "valid till " + simpleDateFormat.format(expiryDate).toLowerCase();
                        } else {
                            lapseText = days + ((days == 1) ? " day " : " days ") + "left";
                            expiryTextCoupon = days + ((days == 1) ? " day " : " days ") + "left";
                        }
                    } else {

                        if(!offer.isAvailableNow() && !TextUtils.isEmpty(offer.getAvailableNext().getDay()) && !TextUtils.isEmpty(offer.getAvailableNext().getTime())){

                            lapseText = offer.getAvailableNext().getDay()+", "+offer.getAvailableNext().getTime();
                            expiryTextCoupon = offer.getAvailableNext().getDay()+", "+offer.getAvailableNext().getTime();
                        }else {
                            lapseText = null;
                            expiryTextCoupon = null;
                            date.setVisibility(View.INVISIBLE);
                        }

                    }


                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
        }else if("checkin".equalsIgnoreCase(offer.getType())){
            if (!TextUtils.isEmpty(offer.getExpiry())) {
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
                try {
                    Date expiryDate = sdf.parse(offer.getExpiry());
                    long days = TimeUnit.DAYS.convert(expiryDate.getTime() - System.currentTimeMillis(), TimeUnit.MILLISECONDS);

                    long hours = TimeUnit.HOURS.convert(expiryDate.getTime() - System.currentTimeMillis(), TimeUnit.MILLISECONDS);
                    Log.i("days", "" + days);

                    if (days == 0) {
                        expiryText = hours + ((hours == 1) ? " hours " : " hours ") + "left";
                    } else if (days > 7) {
                        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("d-MMM");
                        expiryText = "valid till " + simpleDateFormat.format(expiryDate).toLowerCase();
                    } else {
                        expiryText = days + ((days == 1) ? " day " : " days ") + "left";
                    }

                }catch (ParseException e) {
                    e.printStackTrace();
                }
            }
        }
        else {
            if (!TextUtils.isEmpty(offer.getExpiry())) {
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
                try {
                    Date expiryDate = sdf.parse(offer.getExpiry());
                    long days = TimeUnit.DAYS.convert(expiryDate.getTime() - System.currentTimeMillis(), TimeUnit.MILLISECONDS);

                    long hours = TimeUnit.HOURS.convert(expiryDate.getTime() - System.currentTimeMillis(), TimeUnit.MILLISECONDS);
                    Log.i("days", "" + days);
                    if (offer.isAvailableNow()) {

                        if (days == 0) {
                            expiryText = hours + ((hours == 1) ? " hours " : " hours ") + "left";
                        } else if (days > 7) {
                            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("d-MMM");
                            expiryText = "valid till " + simpleDateFormat.format(expiryDate).toLowerCase();
                        } else {
                            expiryText = days + ((days == 1) ? " day " : " days ") + "left";
                        }
                    } else {

                        if (!offer.isAvailableNow() && !TextUtils.isEmpty(offer.getAvailableNext().getDay()) && !TextUtils.isEmpty(offer.getAvailableNext().getTime())) {

                            expiryText = offer.getAvailableNext().getDay()+", "+offer.getAvailableNext().getTime();

                        } else {
                            expiryText = null;
                            date.setVisibility(View.INVISIBLE);
                        }

                    }

                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
        }

     /*   if (!TextUtils.isEmpty(offer.getExpiry())) {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
            try {
                Date expiryDate = sdf.parse(offer.getExpiry());
                long days = TimeUnit.DAYS.convert(expiryDate.getTime() - System.currentTimeMillis(), TimeUnit.MILLISECONDS);
                SimpleDateFormat extendDateFormat = new SimpleDateFormat("dd-MMM yyyy");
                extendDate = extendDateFormat.format(expiryDate);

                SimpleDateFormat extendDateFormat2 = new SimpleDateFormat("dd/MM/yyyy");
                extendDate2 = extendDateFormat2.format(expiryDate);

                if (days > 7) {
                    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("d-MMM");
                    expiryText = "valid till " + simpleDateFormat.format(expiryDate).toLowerCase();
                } else {
                    expiryText = days + ((days == 1) ? " day " : " days ") + "left";
                }

            } catch (ParseException e) {
                e.printStackTrace();
            }
        }*/

        calendar = Calendar.getInstance();
        if (offer.getAvailableNext() != null) {
            AvailableNext availableNext = offer.getAvailableNext();
            if (!TextUtils.isEmpty(availableNext.getDay()) && !TextUtils.isEmpty(availableNext.getTime())) {
                day = availableNext.getDay();
                time = availableNext.getTime();


                int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);


                if (day.equalsIgnoreCase("SUN")) {
                    calendar = Utils.nextDayOfWeek(dayOfWeek);
                } else if (day.equalsIgnoreCase("MON")) {
                    calendar = Utils.nextDayOfWeek(dayOfWeek);
                } else if (day.equalsIgnoreCase("TUE")) {
                    calendar = Utils.nextDayOfWeek(dayOfWeek);
                } else if (day.equalsIgnoreCase("WED")) {
                    calendar = Utils.nextDayOfWeek(dayOfWeek);
                } else if (day.equalsIgnoreCase("THU")) {
                    calendar = Utils.nextDayOfWeek(dayOfWeek);
                } else if (day.equalsIgnoreCase("FRI")) {
                    calendar = Utils.nextDayOfWeek(dayOfWeek);
                } else if (day.equalsIgnoreCase("SAT")) {
                    calendar = Utils.nextDayOfWeek(dayOfWeek);
                }

            }

        }

        if ("checkin".equalsIgnoreCase(offer.getType())) {

            type_offer.setTextColor(getResources().getColor(R.color.outlet_txt_color_grey));
            coupon_text1.setTextColor(getResources().getColor(R.color.outlet_txt_color_grey));
            coupon_text2.setTextColor(getResources().getColor(R.color.outlet_txt_color_grey));
            buttonImage.setVisibility(View.VISIBLE);
            buttonImage.setImageDrawable(getResources().getDrawable(R.drawable.offer_detail_locked_coupon_icon));

            if (offer.getNext() == 1) {
                btntext.setText(offer.getNext() + " check-in to go");
            } else {
                btntext.setText(offer.getNext() + " check-ins to go");
            }
            loginBtn.setEnabled(false);
            date.setText(expiryText);

        } else if ("coupon".equalsIgnoreCase(offer.getType())) {
            type_offer.setTextColor(getResources().getColor(R.color.offer_color_red));
            coupon_text1.setTextColor(getResources().getColor(R.color.offer_color_red));
            coupon_text2.setTextColor(getResources().getColor(R.color.offer_color_red));

            buttonImage.setVisibility(View.VISIBLE);
            date.setText(lapseText);
            if (offer.isAvailableNow()) {

                int sdk = android.os.Build.VERSION.SDK_INT;
                if (sdk < android.os.Build.VERSION_CODES.JELLY_BEAN) {
                    loginBtn.setBackgroundDrawable(getResources().getDrawable(R.drawable.button_red));
                } else {
                    loginBtn.setBackground(getResources().getDrawable(R.drawable.button_red));
                }

                btntext.setText("use offer");
                buttonImage.setImageDrawable(getResources().getDrawable(R.drawable.offer_detail_coupon_icon));
                loginBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        if(offer.getOutletList() == null){
                            if(offer.getIssuedBy()!=null){
                                String id = offer.getIssuedBy();
                                redeemConfirmationCoupon(id);
                            }
                        }else {
                            if(locationText.length()!=0) {
                                redeemConfirmationCoupon(idValue);
                            }else {
                                Toast.makeText(OfferDetailActivity.this,"Please pick a location before proceeding!",Toast.LENGTH_SHORT).show();
                            }
                        }

                     /*   if(locationText.length()!=0) {
                            if (offer.getOutletList() == null) {
                                String id = offer.getIssuedBy();
                                redeemConfirmationCoupon(id);
                            } else {
                                redeemConfirmationCoupon(idValue);
                            }
                        }else {
                            Toast.makeText(OfferDetailActivity.this,"Please pick a location before proceeding!",Toast.LENGTH_SHORT).show();
                        }*/

                    }
                });

                buttonAct.setImageDrawable(getResources().getDrawable(R.drawable.remind_me_button_offer_detail));
                buttonAct.setImageDrawable(getResources().getDrawable(R.drawable.button_extend));
                buttonAct.setVisibility(View.VISIBLE);
                buttonAct.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        int bucks = getTwystBucks();
                        if (bucks >= 150) {
                            if(extendDate.equals(lapsedDate)){
                                Toast.makeText(getBaseContext(),"You have already extend voucher to its expiry date",Toast.LENGTH_SHORT).show();
                            }else {
                                extendVoucher();
                            }
                        } else {
                            extendVoucherError("coupon");
                        }

                    }
                });

            } else {

                int sdk = android.os.Build.VERSION.SDK_INT;
                if (sdk < android.os.Build.VERSION_CODES.JELLY_BEAN) {
                    loginBtn.setBackgroundDrawable(getResources().getDrawable(R.drawable.button_grey));
                } else {
                    loginBtn.setBackground(getResources().getDrawable(R.drawable.button_grey));
                }
                btntext.setText("remind me");

                loginBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (!TextUtils.isEmpty(day) && !TextUtils.isEmpty(time)) {

                            setReminder(calendar, time);
                        } else {
                            Toast.makeText(OfferDetailActivity.this, "Available next day and time are null!", Toast.LENGTH_LONG).show();
                        }
                    }
                });

                buttonImage.setImageDrawable(getResources().getDrawable(R.drawable.offer_detail_remind_me));
                buttonAct.setImageDrawable(getResources().getDrawable(R.drawable.button_extend));
                buttonAct.setVisibility(View.VISIBLE);
                buttonAct.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        int bucks = getTwystBucks();
                        if (bucks >= 150) {
                            if(extendDate.equals(lapsedDate)){
                                Toast.makeText(getBaseContext(),"You have already extend voucher to its expiry date",Toast.LENGTH_SHORT).show();
                            }else {
                                extendVoucher();
                            }
                        } else {
                            extendVoucherError("coupon");
                        }

                    }
                });

            }

        } else if ("pool".equalsIgnoreCase(offer.getType())) {
            date.setText(expiryText);
            type_offer.setTextColor(getResources().getColor(R.color.offer_color_yellow));
            coupon_text1.setTextColor(getResources().getColor(R.color.offer_color_yellow));
            coupon_text2.setTextColor(getResources().getColor(R.color.offer_color_yellow));
            buckText.setVisibility(View.VISIBLE);
            buckText.setText("");
            btntext.setText("grab offer");
            buttonImage.setVisibility(View.GONE);
            int sdk = android.os.Build.VERSION.SDK_INT;
            if (sdk < android.os.Build.VERSION_CODES.JELLY_BEAN) {
                loginBtn.setBackgroundDrawable(getResources().getDrawable(R.drawable.button_yellow));
            } else {
                loginBtn.setBackground(getResources().getDrawable(R.drawable.button_yellow));
            }
            loginBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    int bucks = getTwystBucks();
                    if (bucks >= offer.getOfferCost()) {
                        grabOffer();
                    } else {
                        grabOfferError();
                    }
                }
            });
            buttonAct.setVisibility(View.INVISIBLE);
                /*buttonAct.setImageDrawable(getResources().getDrawable(R.drawable.remind_me_button_offer_detail));
                buttonAct.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        setReminderWithoutProirInfo();
                    }
                });*/


        } else if ("offer".equalsIgnoreCase(offer.getType())) {
            type_offer.setTextColor(getResources().getColor(R.color.offer_color_green));
            coupon_text1.setTextColor(getResources().getColor(R.color.offer_color_green));
            coupon_text2.setTextColor(getResources().getColor(R.color.offer_color_green));
            date.setText(expiryText);
            if (offer.isAvailableNow()) {

                btntext.setText("use offer");
                buttonImage.setVisibility(View.GONE);
                int sdk = android.os.Build.VERSION.SDK_INT;
                if (sdk < android.os.Build.VERSION_CODES.JELLY_BEAN) {
                    loginBtn.setBackgroundDrawable(getResources().getDrawable(R.drawable.button_offer_green));
                } else {
                    loginBtn.setBackground(getResources().getDrawable(R.drawable.button_offer_green));
                }
                loginBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (offer.getOfferCost() != 0) {
                            if (getTwystBucks() >= offer.getOfferCost()) {
                                redeemConfirmationOffer("offer");
                            } else {
                                extendVoucherError("offer");
                            }
                        }
                    }
                });
                if (offer.getOfferCost() > 0) {
                    buckText.setVisibility(View.VISIBLE);
                    buckText.setText(String.valueOf(offer.getOfferCost()));
                } else {
                    buckText.setVisibility(View.GONE);
                }

                buttonAct.setVisibility(View.INVISIBLE);
                /*buttonAct.setImageDrawable(getResources().getDrawable(R.drawable.remind_me_button_offer_detail));
                buttonAct.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        setReminderWithoutProirInfo();
                    }
                });*/

            } else {
                buttonImage.setVisibility(View.VISIBLE);
                btntext.setText("remind me");
                int sdk = android.os.Build.VERSION.SDK_INT;
                if (sdk < android.os.Build.VERSION_CODES.JELLY_BEAN) {
                    loginBtn.setBackgroundDrawable(getResources().getDrawable(R.drawable.button_grey));
                } else {
                    loginBtn.setBackground(getResources().getDrawable(R.drawable.button_grey));
                }
                loginBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (!TextUtils.isEmpty(day) && !TextUtils.isEmpty(time)) {
                            setReminder(calendar, time);
                        } else {
                            Toast.makeText(OfferDetailActivity.this, "Available next day and time are null!", Toast.LENGTH_LONG).show();
                        }
                    }
                });
                buttonImage.setImageDrawable(getResources().getDrawable(R.drawable.offer_detail_remind_me));
                buttonAct.setImageDrawable(getResources().getDrawable(R.drawable.button_extend));
                buttonAct.setVisibility(View.INVISIBLE);

            }

        } else if ("deal".equalsIgnoreCase(offer.getType())) {

            date.setText(expiryText);
            type_offer.setTextColor(getResources().getColor(R.color.offer_color_green));
            coupon_text1.setTextColor(getResources().getColor(R.color.offer_color_green));
            coupon_text2.setTextColor(getResources().getColor(R.color.offer_color_green));

            if (offer.isAvailableNow()) {

                int sdk = android.os.Build.VERSION.SDK_INT;
                if (sdk < android.os.Build.VERSION_CODES.JELLY_BEAN) {
                    loginBtn.setBackgroundDrawable(getResources().getDrawable(R.drawable.button_offer_green));
                } else {
                    loginBtn.setBackground(getResources().getDrawable(R.drawable.button_offer_green));
                }
                buttonImage.setVisibility(View.GONE);
                btntext.setText("use offer");
                loginBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        redeemConfirmationDeal("deal");
                    }
                });
                buttonAct.setVisibility(View.INVISIBLE);
                /*buttonAct.setImageDrawable(getResources().getDrawable(R.drawable.remind_me_button_offer_detail));
                buttonAct.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        setReminderWithoutProirInfo();
                    }
                });*/
                buckText.setVisibility(View.GONE);


            } else {
                buttonImage.setVisibility(View.VISIBLE);
                int sdk = android.os.Build.VERSION.SDK_INT;
                if (sdk < android.os.Build.VERSION_CODES.JELLY_BEAN) {
                    loginBtn.setBackgroundDrawable(getResources().getDrawable(R.drawable.button_grey));
                } else {
                    loginBtn.setBackground(getResources().getDrawable(R.drawable.button_grey));
                }
                btntext.setText("remind me");
                loginBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (!TextUtils.isEmpty(day) && !TextUtils.isEmpty(time)) {
                            setReminder(calendar, time);
                        } else {
                            Toast.makeText(OfferDetailActivity.this, "Available next day and time are null!", Toast.LENGTH_LONG).show();
                        }
                    }
                });
                buttonImage.setImageDrawable(getResources().getDrawable(R.drawable.offer_detail_remind_me));
                buttonAct.setVisibility(View.INVISIBLE);

            }

        } else if ("bank_deal".equalsIgnoreCase(offer.getType())) {
            TextView bankcard = (TextView)findViewById(R.id.bankcard);
            date.setText(expiryText);
            type_offer.setTextColor(getResources().getColor(R.color.offer_color_blue));
            coupon_text1.setTextColor(getResources().getColor(R.color.offer_color_blue));
            coupon_text2.setTextColor(getResources().getColor(R.color.offer_color_blue));
            if(!TextUtils.isEmpty(offer.getSource())){
                bankcard.setVisibility(View.VISIBLE);
                bankcard.setText(offer.getSource());
            }else {
                bankcard.setVisibility(View.GONE);
            }

            if (offer.isAvailableNow()) {
                buttonImage.setVisibility(View.GONE);
                int sdk = android.os.Build.VERSION.SDK_INT;
                if (sdk < android.os.Build.VERSION_CODES.JELLY_BEAN) {
                    loginBtn.setBackgroundDrawable(getResources().getDrawable(R.drawable.button_blue));
                } else {
                    loginBtn.setBackground(getResources().getDrawable(R.drawable.button_blue));
                }
                btntext.setText("use offer");
                loginBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        redeemConfirmationDeal("bank_deal");

                    }
                });
                buckText.setVisibility(View.GONE);
                buttonAct.setVisibility(View.INVISIBLE);
                /*buttonAct.setImageDrawable(getResources().getDrawable(R.drawable.remind_me_button_offer_detail));
                buttonAct.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        setReminderWithoutProirInfo();
                    }
                });*/
            } else {
                buttonImage.setVisibility(View.VISIBLE);
                int sdk = android.os.Build.VERSION.SDK_INT;
                if (sdk < android.os.Build.VERSION_CODES.JELLY_BEAN) {
                    loginBtn.setBackgroundDrawable(getResources().getDrawable(R.drawable.button_grey));
                } else {
                    loginBtn.setBackground(getResources().getDrawable(R.drawable.button_grey));
                }
                btntext.setText("remind me");
                loginBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (!TextUtils.isEmpty(day) && !TextUtils.isEmpty(time)) {
                            setReminder(calendar, time);
                        } else {
                            Toast.makeText(OfferDetailActivity.this, "Available next day and time are null!", Toast.LENGTH_LONG).show();
                        }
                    }
                });
                buttonImage.setImageDrawable(getResources().getDrawable(R.drawable.offer_detail_remind_me));
                buttonAct.setVisibility(View.INVISIBLE);

            }
        }


        type_offer.setText(offer.getHeader());

        if (!TextUtils.isEmpty(offer.getDescription())) {
            coupon_text1.setText(offer.getDescription());
            coupon_text2.setVisibility(View.GONE);
        } else {
            coupon_text1.setText(offer.getLine1());
            coupon_text2.setText(offer.getLine2());
        }

        TextView viewMore = (TextView) findViewById(R.id.viewMore1);
        final TextView terms1 = (TextView) findViewById(R.id.terms1);
        final TextView terms2 = (TextView) findViewById(R.id.terms2);


        if (!TextUtils.isEmpty(offer.getTerms())) {
            String[] splitStr = new String[0];

            if (offer.getTerms().contains(";") || !offer.getTerms().isEmpty()) {
                splitStr = offer.getTerms().trim().split("\\s*;\\s*");
                for (int i = 0; i < splitStr.length; i++) {
                    term1 = splitStr[0];
                    if (offer.getTerms().contains(";") &&!TextUtils.isEmpty(splitStr[1])) {
                        term2 = splitStr[1];
                        terms2.setVisibility(View.VISIBLE);
                    } else {
                        terms2.setVisibility(View.GONE);
                    }
                }

            } else {
                term1 = offer.getTerms();
            }
        }

        if (!TextUtils.isEmpty(term1)) {
            terms1.setText(term1.toString().trim());
            terms1.setVisibility(View.VISIBLE);
        }
        if (!TextUtils.isEmpty(term2)) {
            terms2.setText(term2.toString().trim());
            terms2.setVisibility(View.VISIBLE);
        }

        viewMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                viewMoreDialog();
            }
        });

        RelativeLayout relativeLayoutPicLoc = (RelativeLayout)findViewById(R.id.relativeLayoutPicLoc);
        relativeLayoutPicLoc.setVisibility(View.GONE);

        if("coupon".equalsIgnoreCase(offer.getType())){
            if(offer.getOutletList()!=null){
                relativeLayoutPicLoc.setVisibility(View.VISIBLE);

                findViewById(R.id.locationView).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        List<Offer.OutletList> outletList = offer.getOutletList();

                        values.clear();
                        outletID.clear();

                        pickLocDialog(outletList);

                    }
                });
            }
        }


        if (!TextUtils.isEmpty(outlet.getDistance())) {
            distance.setText(outlet.getDistance() + " Km");
        } else {
            distance.setVisibility(View.INVISIBLE);
        }

        outletName.setText(outlet.getName());

        hideProgressHUDInLayout();
        findViewById(R.id.contentView).setVisibility(View.VISIBLE);
    }

    private void pickLocDialog(List<Offer.OutletList> outletList) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater li = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View dialogView = li.inflate(R.layout.dialog_view_outlets, null);
        final ListView listView = (ListView) dialogView.findViewById(R.id.locList);

        MaterialRippleLayout locChoose = (MaterialRippleLayout)dialogView.findViewById(R.id.locChoose);
        for(int i=0;i<outletList.size();i++){
            String address = "";
            String id = "";
            boolean added = false;
            if (outletList.get(i).getLocation_1().get(0)!=null) {
                address += outletList.get(i).getLocation_1().get(0);
                id += outletList.get(i).get_id();
                added = true;
            }

            values.add(address);
            outletID.add(id);
        }

        ListAdapter adapter = new ListAdapter(this,values,outletID);
        listView.setAdapter(adapter);

        /*listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {

                // ListView Clicked item index
                itemPosition = position;
                itemValue = (String) listView.getItemAtPosition(position);

            }

        });*/


        builder.setView(dialogView);

        final AlertDialog dialog = builder.create();
        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(true);
        dialog.show();

        locChoose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (flag) {
                    findViewById(R.id.textView20).setVisibility(View.GONE);
                    locationText.setVisibility(View.VISIBLE);
                    locationText.setText(itemValue);
                    dialog.dismiss();
                } else {
                    Toast.makeText(OfferDetailActivity.this, "Please pick a location!", Toast.LENGTH_SHORT).show();
                }

            }
        });

        dialogView.findViewById(R.id.locCancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (locationText.length() > 0) {
                    findViewById(R.id.textView20).setVisibility(View.GONE);
                } else {
                    flag = false;
                    findViewById(R.id.textView20).setVisibility(View.VISIBLE);
                }
                dialog.dismiss();
            }
        });

    }

    /*private void pickLocDialog(List<Offer.OutletList> outletList) {


        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater li = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View dialogView = li.inflate(R.layout.dialog_view_outlets, null);
        final ListView listView = (ListView) dialogView.findViewById(R.id.locList);
        List<String> values = new ArrayList<>();
        List<String> ids = new ArrayList<>();

        for(int i=0;i<outletList.size();i++){
            String outletIds = outletList.get(i).get_id();
            String outlets = outletList.get(i).getName();
            values.add(outlets);
            ids.add(outletIds);
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                R.layout.loc_picker_layout, android.R.id.text1, values);


        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {

                // ListView Clicked item index
                int itemPosition     = position;
                itemValue = (String) listView.getItemAtPosition(position);

            }

        });


        builder.setView(dialogView);

        final AlertDialog dialog = builder.create();
        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(true);
        dialog.show();

        dialogView.findViewById(R.id.locClose).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();


            }
        });

        dialogView.findViewById(R.id.locCancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

    }*/

    private void viewMoreDialog() {


        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater li = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View dialogView = li.inflate(R.layout.dialog_viewmore_tnc_offer_details, null);
        TextView termsD1 = (TextView) dialogView.findViewById(R.id.termsd1);
        TextView termsD2 = (TextView) dialogView.findViewById(R.id.termsd2);
        TextView termsD3 = (TextView) dialogView.findViewById(R.id.termsd3);
        TextView termsD4 = (TextView) dialogView.findViewById(R.id.termsd4);
        TextView termsD5 = (TextView) dialogView.findViewById(R.id.termsd5);
        TextView termsD6 = (TextView) dialogView.findViewById(R.id.termsd6);
        TextView termsD7 = (TextView) dialogView.findViewById(R.id.termsd7);
        TextView termsD8 = (TextView) dialogView.findViewById(R.id.termsd8);

        termsD1.setText("1. Offers (2 or more) cannot be clubbed.");
        termsD2.setText("2. Offers are not valid on specially priced combinations e.g. any other buffet brunch deal corporate dining rate happy hours etc.");
        termsD3.setText("3. Only 1 voucher offer can be used per bill generated.");
        termsD4.setText("4. In certain cases, specific items may be part of the offer.");
        termsD5.setText("5. The availability of the specific items is not guaranteed and must be checked with the outlet before using the offer.");
        termsD6.setText("6. The offers are provided at the sole discretion of the merchant,and the merchant reserves the right to alter or withdraw the offer at any time.");
        termsD7.setText("7. Offers vouchers consisting of alcohol alcohol-based products will be available only to individuals above legal drinking age.");
        termsD8.setText("8. The establishment reserves the right to verify the customer's age before providing such reward.");


        builder.setView(dialogView);

        final AlertDialog dialog = builder.create();
        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(true);
        dialog.show();

        dialogView.findViewById(R.id.extendBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();


            }
        });

        dialogView.findViewById(R.id.cancelExtendBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

    }

    public void setReminder(Calendar day, String time) {
        try {
            long timeDelay;
            Calendar calendarToday = Calendar.getInstance();
            Calendar calendarDest = Calendar.getInstance();

            AvailableNext availableNext = offer.getAvailableNext();
            String sDay = availableNext.getDay();
            String sTime = availableNext.getTime();
            int sHour = Integer.parseInt(sTime.substring(0, 2));
            int sMin = Integer.parseInt(sTime.substring(3, 5));
            String ampm = sTime.substring(6, 8);

            if (sDay.equalsIgnoreCase("Tomorrow")) {
                calendarDest.add(Calendar.DATE, 1);

            } else if (sDay.equalsIgnoreCase("Today")) {
//                calendarDest.add(Calendar.DATE, 0);
            } else {
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
                Date dateEnd = sdf.parse(sDay);
                calendarDest.setTime(dateEnd);
            }
            calendarDest.set(Calendar.HOUR_OF_DAY, sHour);
            calendarDest.set(Calendar.MINUTE, sMin);
            calendarDest.set(Calendar.SECOND, 0);
            if (ampm.equalsIgnoreCase("AM")) {
                calendarDest.set(Calendar.AM_PM, Calendar.AM);
            } else {
                calendarDest.set(Calendar.AM_PM, Calendar.PM);
            }


            timeDelay = calendarDest.getTime().getTime() - calendarToday.getTime().getTime();
            scheduleNotification(getNotification(outlet.getName()), timeDelay);
            Log.d("OfferDetailActivity", "reminder set for " + timeDelay / 1000 + " seconds");
            Toast.makeText(OfferDetailActivity.this, "Reminder set", Toast.LENGTH_SHORT).show();
        } catch (ParseException e) {
            e.printStackTrace();
        }

//        Calendar calendar = Calendar.getInstance();
//        calendar.add(Calendar.DATE, day.get(Calendar.DAY_OF_WEEK));
//        long begin = calendar.getTimeInMillis();
//        calendar.set(Calendar.MINUTE, calendar.get(Calendar.MINUTE) + 10);
//        long end = calendar.getTimeInMillis();
//        Intent intent = new Intent(Intent.ACTION_INSERT)
//                .setData(CalendarContract.Events.CONTENT_URI)
//                .putExtra(CalendarContract.EXTRA_EVENT_BEGIN_TIME, begin)
//                .putExtra(CalendarContract.EXTRA_EVENT_END_TIME, end)
//                .putExtra(CalendarContract.Events.TITLE, " Reminder: Twyst offer at " + outlet.getName())
//                .putExtra(CalendarContract.Events.AVAILABILITY, CalendarContract.Events.AVAILABILITY_BUSY);
//        startActivity(intent);
    }

    private void scheduleNotification(Notification notification, long delay) {

        String offer_id = offer.get_id();
        String outlet_id = outlet.get_id();

        int notiCount = getNotificationCount();
        Intent notificationIntent = new Intent(this, NotificationPublisherReceiver.class);
        notificationIntent.putExtra(NotificationPublisherReceiver.NOTIFICATION_ID, notiCount);
        notificationIntent.putExtra(NotificationPublisherReceiver.NOTIFICATION, notification);
        notificationIntent.putExtra(NotificationPublisherReceiver.OUTLET_ID, outlet_id);
        notificationIntent.putExtra(NotificationPublisherReceiver.OFFER_ID, offer_id);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, notiCount, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        long futureInMillis = SystemClock.elapsedRealtime() + delay;
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        alarmManager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP, futureInMillis, pendingIntent);
    }

    public int getNotificationCount() {
        SharedPreferences prefs = this.getSharedPreferences(AppConstants.PREFERENCE_SHARED_PREF_NAME, Context.MODE_PRIVATE);
        int lastCount = prefs.getInt(AppConstants.PREFERENCE_NOTIFICATION_COUNT, 0);
        prefs.edit().putInt(AppConstants.PREFERENCE_NOTIFICATION_COUNT, lastCount + 1).commit();
        return lastCount;
    }

    private Notification getNotification(String content) {
        Notification.Builder builder = new Notification.Builder(this);
        builder.setContentTitle("Offer available");
        builder.setContentText(content);
        builder.setSmallIcon(R.drawable.ic_launcher);
        return builder.build();
    }

    public void setReminderWithoutProirInfo() {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DATE, 1);
        Intent intent = new Intent(Intent.ACTION_INSERT)
                .setData(CalendarContract.Events.CONTENT_URI)
                .putExtra(CalendarContract.Events.TITLE, " Reminder: Twyst offer at " + outlet.getName());
        startActivity(intent);
    }

    private void redeemConfirmationCoupon(final String id) {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater li = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View dialogView = li.inflate(R.layout.dialog_redeem_confirmation, null);


        builder.setView(dialogView);

        final AlertDialog dialog = builder.create();
        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(true);
        dialog.show();

        dialogView.findViewById(R.id.redeemConfirmBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                UseOffer useOffer = new UseOffer();
                UseOfferMeta useOfferMeta = new UseOfferMeta();
                useOfferMeta.setOffer(null);
                useOfferMeta.setType(null);
                useOfferMeta.setCoupon(offer.getCode());
                useOffer.setEventOutlet(id);
                useOffer.setUseOfferMeta(useOfferMeta);

                HttpService.getInstance().postRedeemCoupon(getUserToken(), useOffer, new Callback<BaseResponse>() {
                    @Override
                    public void success(BaseResponse baseResponse, Response response) {

                        if (baseResponse.isResponse()) {
                            dialog.dismiss();
                            Toast.makeText(OfferDetailActivity.this, "Coupon redeemed successfully!", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(OfferDetailActivity.this, RedeemVoucherActivity.class);
                            intent.putExtra(AppConstants.INTENT_PARAM_OUTLET_OBJECT, outlet);
                            intent.putExtra(AppConstants.INTENT_PARAM_OFFER_OBJECT, offer);
                            startActivity(intent);
                        } else {
                            dialog.dismiss();
                            Toast.makeText(OfferDetailActivity.this, "Unable to redeem coupon!", Toast.LENGTH_SHORT).show();
                        }

                    }

                    @Override
                    public void failure(RetrofitError error) {
                        dialog.dismiss();
                        handleRetrofitError(error);
                    }
                });

            }
        });

        dialogView.findViewById(R.id.redeemCancelBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
    }

    private void redeemConfirmationOffer(final String type) {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater li = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View dialogView = li.inflate(R.layout.dialog_redeem_confirmation2, null);

        builder.setView(dialogView);

        final AlertDialog dialog = builder.create();
        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(true);
        dialog.show();

        dialogView.findViewById(R.id.redeemConfirmBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final UseOffer useOffer = new UseOffer();
                UseOfferMeta useOfferMeta = new UseOfferMeta();
                useOfferMeta.setOffer(offer.get_id());
                useOfferMeta.setType(type);
                useOfferMeta.setCoupon(null);
                useOffer.setEventOutlet(outlet.get_id());
                useOffer.setUseOfferMeta(useOfferMeta);

                HttpService.getInstance().postGenerateCoupon(getUserToken(), useOffer, new Callback<BaseResponse>() {
                    @Override
                    public void success(BaseResponse baseResponse, Response response) {

                        if (baseResponse.isResponse()) {
                            dialog.dismiss();
                            Map dataMap = (LinkedTreeMap) baseResponse.getData();
                            String json = gson.toJson(dataMap);
                            UseOfferData useOfferData = gson.fromJson(json, UseOfferData.class);

                            Toast.makeText(OfferDetailActivity.this, "Offer used successfully!", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(OfferDetailActivity.this, RedeemVoucherActivity.class);
                            intent.putExtra(AppConstants.INTENT_PARAM_OUTLET_OBJECT, outlet);
                            intent.putExtra(AppConstants.INTENT_PARAM_OFFER_OBJECT, offer);
                            intent.putExtra(AppConstants.INTENT_PARAM_USE_OFFER_DATA_OBJECT, useOfferData);
                            startActivity(intent);
                        } else {
                            dialog.dismiss();
                            String[] split = baseResponse.getData().toString().split("\\s*-\\s*");
                            String s = split[1];
                            Toast.makeText(OfferDetailActivity.this, s, Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void failure(RetrofitError error) {
                        dialog.dismiss();
                        handleRetrofitError(error);
                    }
                });


            }
        });

        dialogView.findViewById(R.id.redeemCancelBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
    }

    private void redeemConfirmationDeal(final String type) {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater li = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View dialogView = li.inflate(R.layout.dialog_redeem_confirmation3, null);


        builder.setView(dialogView);

        final AlertDialog dialog = builder.create();
        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(true);
        dialog.show();

        dialogView.findViewById(R.id.redeemConfirmBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                UseOffer useOffer = new UseOffer();
                UseOfferMeta useOfferMeta = new UseOfferMeta();
                useOfferMeta.setOffer(offer.get_id());
                useOfferMeta.setType(type);
                useOfferMeta.setCoupon(null);
                useOffer.setEventOutlet(outlet.get_id());
                useOffer.setUseOfferMeta(useOfferMeta);

                HttpService.getInstance().postDealLog(getUserToken(), useOffer, new Callback<BaseResponse>() {
                    @Override
                    public void success(BaseResponse baseResponse, Response response) {

                        if (baseResponse.isResponse()) {
                            dialog.dismiss();
                            Toast.makeText(OfferDetailActivity.this, "Offer redeemed successfully!", Toast.LENGTH_SHORT).show();
                        } else {
                            dialog.dismiss();
                            String[] split = baseResponse.getData().toString().split("\\s*-\\s*");
                            String s = split[1];
                            Toast.makeText(OfferDetailActivity.this, s, Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void failure(RetrofitError error) {
                        dialog.dismiss();
                        handleRetrofitError(error);
                    }
                });

            }
        });

        dialogView.findViewById(R.id.redeemCancelBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
    }

    private void grabOffer() {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater li = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View dialogView = li.inflate(R.layout.dialog_grabcoupon_confirm, null);

        TextView grabText = (TextView)dialogView.findViewById(R.id.grabText);

        builder.setView(dialogView);

        final AlertDialog dialog = builder.create();
        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(true);
        dialog.show();

        dialogView.findViewById(R.id.grabCouponBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


            }
        });

        dialogView.findViewById(R.id.cancelGrabCoupon).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
    }


    private void grabOfferError() {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater li = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View dialogView = li.inflate(R.layout.dialog_graboffer_error, null);

        builder.setView(dialogView);

        final AlertDialog dialog = builder.create();
        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(true);
        dialog.show();

        dialogView.findViewById(R.id.grabErrorBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


            }
        });

        dialogView.findViewById(R.id.cancelGrabError).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
    }

    private void extendVoucher() {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater li = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View dialogView = li.inflate(R.layout.dialog_extend_voucher, null);

        TextView extendtext = (TextView) dialogView.findViewById(R.id.extendtext);

        extendtext.setText("Extend this voucher's validity till " + extendDate + ".This will cost you 150 Twyst Bucks.");

        builder.setView(dialogView);

        final AlertDialog dialog = builder.create();
        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(true);
        dialog.show();

        dialogView.findViewById(R.id.extendBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Voucher voucher = new Voucher();
                Voucher.VoucherMeta voucherMeta = new Voucher.VoucherMeta();

                voucherMeta.setOffer(offer.get_id());
                voucherMeta.setDate(extendDate2);
                voucher.setVoucherMeta(voucherMeta);
                HttpService.getInstance().extendVoucher(getUserToken(), voucher, new Callback<BaseResponse>() {
                    @Override
                    public void success(BaseResponse baseResponse, Response response) {

                        dialog.dismiss();
                        if (baseResponse.isResponse()) {
                            date.setText(expiryTextCoupon);
                            Toast.makeText(OfferDetailActivity.this, "Offer extended to its maximum date!", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(OfferDetailActivity.this, baseResponse.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void failure(RetrofitError error) {

                        handleRetrofitError(error);
                        dialog.dismiss();
                    }
                });
            }
        });

        dialogView.findViewById(R.id.cancelExtendBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
    }

    private void extendVoucherError(String type) {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater li = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View dialogView = li.inflate(R.layout.dialog_extend_voucher_error, null);
        TextView costTv = (TextView) dialogView.findViewById(R.id.costTv);
        if(type.equalsIgnoreCase("offer")){
            costTv.setText("You don't have enough Twyst Bucks to extend this voucher's validity. You need 100 twyst bucks.");
        }else {
            costTv.setText("You don't have enough Twyst Bucks to extend this voucher's validity. You need 150 twyst bucks.");
        }
        builder.setView(dialogView);

        final AlertDialog dialog = builder.create();
        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(true);
        dialog.show();

        dialogView.findViewById(R.id.extendBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


            }
        });

        dialogView.findViewById(R.id.cancelExtendBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
    }

    public int getTwystBucks() {
        SharedPreferences prefs = this.getSharedPreferences(AppConstants.PREFERENCE_SHARED_PREF_NAME, Context.MODE_PRIVATE);
        return prefs.getInt(AppConstants.PREFERENCE_LAST_TWYST_BUCK, 0);
    }

    public void slideUp(final View view) {
        if (!isPanelShown) {
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

    public void slideDown(final View view) {
        if (isPanelShown) {
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

    public class ListAdapter extends BaseAdapter{

        List <String> outletID;
        List <String> values;
        Activity activity;
        boolean[] itemChecked;

        public ListAdapter(Activity activity, List<String> values,List<String> id){
            super();
            this.activity = activity;
            this.values = values;
            this.outletID = id;
            itemChecked = new boolean[values.size()];
        }

        @Override
        public int getCount() {
            return values.size();
        }

        @Override
        public Object getItem(int position) {
            return values.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            final ViewHolder holder;

            LayoutInflater inflater = activity.getLayoutInflater();
            if (convertView == null) {
                holder = new ViewHolder();
                convertView = inflater.inflate(R.layout.loc_picker_layout, null);
                holder.textView = (TextView) convertView.findViewById(R.id.text);
                holder.checkBox = (CheckBox) convertView.findViewById(R.id.check);

                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            holder.textView.setText(values.get(position));
            holder.checkBox.setChecked(itemChecked[position]);

            holder.checkBox.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    for (int i=0;i<itemChecked.length;i++){
                        if(i==position){
                            flag = true;
                            itemChecked[i]=true;
                            itemPosition = position;
                            itemValue = String.valueOf(holder.textView.getText());
                            idValue = outletID.get(position);
                        }else{
                            itemChecked[i]=false;
                        }
                    }
                    notifyDataSetChanged();
                }
            });
            return convertView;
        }

        private class ViewHolder {

            TextView textView;
            CheckBox checkBox;
        }
    }

    private void outletDetail(String outletId, final String offerId){
        SharedPreferences preferences = getSharedPreferences(AppConstants.PREFERENCE_SHARED_PREF_NAME, Context.MODE_PRIVATE);
        String appLocationLatiude = preferences.getString(AppConstants.PREFERENCE_CURRENT_LAT, "");
        String appLocationLongitude = preferences.getString(AppConstants.PREFERENCE_CURRENT_LNG, "");
        HttpService.getInstance().getOutletDetails(outletId, getUserToken(), appLocationLatiude, appLocationLongitude, new Callback<BaseResponse<OutletDetailData>>() {

            @Override
            public void success(BaseResponse<OutletDetailData> outletBaseResponse, Response response) {

                outlet = outletBaseResponse.getData().getOutlet();
                String twystBucks = outletBaseResponse.getData().getTwystBucks();

                sharedPreferences.putInt(AppConstants.PREFERENCE_LAST_TWYST_BUCK, Integer.parseInt(twystBucks));
                sharedPreferences.commit();

                for(int i=0;i<outlet.getOffers().size();i++){
                    if(outlet.getOffers().get(i).get_id().equals(offerId)){
                        offer = outlet.getOffers().get(i);
                        break;
                    }
                }
                setup();

            }

            @Override
            public void failure(RetrofitError error) {
                hideProgressHUDInLayout();
                handleRetrofitError(error);
            }
        });
    }

}