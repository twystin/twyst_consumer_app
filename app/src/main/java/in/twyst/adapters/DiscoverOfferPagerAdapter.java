package in.twyst.adapters;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

import in.twyst.R;
import in.twyst.activities.OfferDetailActivity;
import in.twyst.activities.OutletDetailsActivity;
import in.twyst.activities.SubmitOfferActivity;
import in.twyst.model.Offer;
import in.twyst.model.Outlet;
import in.twyst.util.AppConstants;

/**
 * Created by vivek on 20/08/15.
 */
public class DiscoverOfferPagerAdapter extends PagerAdapter {
    private List<Offer> items = new ArrayList<>();
    private Outlet outlet;
    private boolean hasMoreOffers;

    public DiscoverOfferPagerAdapter(List<Offer> items, Outlet outlet, boolean hasMoreOffers) {
        this.items = items;
        this.outlet = outlet;
        this.hasMoreOffers = hasMoreOffers;
    }

    @Override
    public int getCount() {
        return items.size() + 1;
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view.equals(object);
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        //System.out.println("container = [" + container + "], position = [" + position + "]");
        LayoutInflater inflater = (LayoutInflater) container.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        if (position < items.size()) {


            final View itemView = inflater.inflate(R.layout.layout_offer, container, false);


            TextView text1 = (TextView) itemView.findViewById(R.id.text1);
            TextView text2 = (TextView) itemView.findViewById(R.id.text2);
            TextView text3 = (TextView) itemView.findViewById(R.id.text3);
            TextView time = (TextView) itemView.findViewById(R.id.availabilityText);
            TextView footerText = (TextView) itemView.findViewById(R.id.footerText);
            ImageView footerImageView = (ImageView) itemView.findViewById(R.id.footerImageView);
            TextView buckTextImage = (TextView) itemView.findViewById(R.id.buckTextImage);
            LinearLayout detailLayout = (LinearLayout) itemView.findViewById(R.id.detailLayout);
            ImageView detailIcon = (ImageView) itemView.findViewById(R.id.detailIcon);
            TextView detailText = (TextView) itemView.findViewById(R.id.detailText);
            RelativeLayout footer = (RelativeLayout) itemView.findViewById(R.id.footerLayout);
            LinearLayout availabilityLayout = (LinearLayout) itemView.findViewById(R.id.availabilityLayout);
            LinearLayout parentView = (LinearLayout) itemView.findViewById(R.id.parentView);

            text1.setText("");
            text2.setText("");
            text3.setText("");
            footerText.setText("");
            time.setText("");
            detailText.setText("");

            final Offer offer = items.get(position);
            text1.setText(offer.getHeader());

            if (TextUtils.isEmpty(offer.getLine1())) {
                text2.setVisibility(View.GONE);
            } else {
                text2.setVisibility(View.VISIBLE);
                text2.setText(offer.getLine1());
            }

            if (TextUtils.isEmpty(offer.getLine2())) {
                text3.setVisibility(View.GONE);
            } else {
                text3.setVisibility(View.VISIBLE);
                text3.setText(offer.getLine2());
            }

            String expiryText = "";
            String lapseText = "";
            if ("coupon".equalsIgnoreCase(offer.getType())) {
                if (!TextUtils.isEmpty(offer.getLapseDate())) {
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
                    try {
                        Date lapseDate = sdf.parse(offer.getLapseDate());
                        long days = TimeUnit.DAYS.convert(lapseDate.getTime() - System.currentTimeMillis(), TimeUnit.MILLISECONDS);

                        long hours = TimeUnit.HOURS.convert(lapseDate.getTime() - System.currentTimeMillis(), TimeUnit.MILLISECONDS);
                        Log.i("days", "" + days);
                        if (offer.isAvailableNow()) {

                            if (days == 0) {
                                lapseText = hours + ((hours == 1) ? " hours " : " hours ") + "left";
                            } else if (days > 7) {
                                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("d-MMM");
                                lapseText = "valid till " + simpleDateFormat.format(lapseDate).toLowerCase();
                            } else {
                                lapseText = days + ((days == 1) ? " day " : " days ") + "left";
                            }
                        } else {

                            if (!offer.isAvailableNow() && !TextUtils.isEmpty(offer.getAvailableNext().getDay()) && !TextUtils.isEmpty(offer.getAvailableNext().getTime())) {

                                lapseText = offer.getAvailableNext().getDay() + ", " + offer.getAvailableNext().getTime();


                            } else {

                                lapseText = null;
                                detailIcon.setVisibility(View.INVISIBLE);
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
            }else {
                if (!TextUtils.isEmpty(offer.getExpiry())) {
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
                    try {
                        Date expiryDate = sdf.parse(offer.getExpiry());
                        long days = TimeUnit.DAYS.convert(expiryDate.getTime() - System.currentTimeMillis(), TimeUnit.MILLISECONDS);

                        long hours = TimeUnit.HOURS.convert(expiryDate.getTime() - System.currentTimeMillis(), TimeUnit.MILLISECONDS);
                       // long minutes = TimeUnit.MINUTES.convert(expiryDate.getTime() - System.currentTimeMillis(), TimeUnit.MILLISECONDS);
                        if (offer.isAvailableNow()) {

                            if (days == 0) {
                                expiryText = hours + ((hours == 1) ? " hours " : " hours ") + "left";
                                /*if(hours == 0){
                                    expiryText = minutes + ((minutes == 1) ? " minutes " : " minutes ") + "left";
                                }else {
                                    expiryText = hours + ((hours == 1) ? " hours " : " hours ") + "left";
                                }*/
                            } else if (days > 7) {
                                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("d-MMM");
                                expiryText = "valid till " + simpleDateFormat.format(expiryDate).toLowerCase();
                            } else {
                                expiryText = days + ((days == 1) ? " day " : " days ") + "left";
                            }
                        } else {

                            if (!offer.isAvailableNow() && !TextUtils.isEmpty(offer.getAvailableNext().getDay()) && !TextUtils.isEmpty(offer.getAvailableNext().getTime())) {

                                expiryText
                                        = offer.getAvailableNext().getDay() + ", " + offer.getAvailableNext().getTime();

                            } else {
                                expiryText = null;
                                detailIcon.setVisibility(View.INVISIBLE);
                            }
                        }

                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                }
            }

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(itemView.getContext(), OfferDetailActivity.class);
                    intent.putExtra(AppConstants.INTENT_PARAM_OUTLET_OBJECT, outlet);
                    intent.putExtra(AppConstants.INTENT_PARAM_OFFER_OBJECT, offer);
                    itemView.getContext().startActivity(intent);
                }
            });

            footerImageView.setVisibility(View.VISIBLE);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT);

            Resources resources = itemView.getContext().getResources();
            if ("checkin".equalsIgnoreCase(offer.getType())) {
                itemView.setBackgroundResource(R.drawable.card_checkin);

                text1.setTextColor(resources.getColor(R.color.outlet_txt_color_grey));
                text2.setTextColor(resources.getColor(R.color.outlet_txt_color_grey));
                text3.setTextColor(resources.getColor(R.color.outlet_txt_color_grey));
                time.setText(expiryText);
                params.weight = 1.0f;
                availabilityLayout.setLayoutParams(params);
                parentView.removeAllViews();
                parentView.addView(availabilityLayout);
                detailLayout.setVisibility(View.GONE);

                footerImageView.setImageDrawable(resources.getDrawable(R.drawable.icon_discover_offer_checkin_locked));

                if (offer.getNext() == 1) {
                    footerText.setText(offer.getNext() + " check-in to go");
                } else {
                    footerText.setText(offer.getNext() + " check-ins to go");
                }



            } else if ("coupon".equalsIgnoreCase(offer.getType())) {
                text1.setTextColor(resources.getColor(R.color.offer_color_red));
                text2.setTextColor(resources.getColor(R.color.offer_color_red));
                text3.setTextColor(resources.getColor(R.color.offer_color_red));
                time.setText(lapseText);
                params.weight = 1.0f;
                parentView.setWeightSum(1f);
                availabilityLayout.setLayoutParams(params);
                parentView.removeAllViews();
                parentView.addView(availabilityLayout);
                detailLayout.setVisibility(View.GONE);

                if (offer.isAvailableNow()) {
                    itemView.setBackgroundResource(R.drawable.card_red);


                    footerText.setText("use offer");
                    footerImageView.setImageDrawable(resources.getDrawable(R.drawable.icon_discover_offer_coupon_wallet));
                } else {
                    itemView.setBackgroundResource(R.drawable.card_gray);
                    footerText.setText("remind me");
                    footerImageView.setImageDrawable(resources.getDrawable(R.drawable.icon_discover_offer_clock_white));
                }


            } else if ("pool".equalsIgnoreCase(offer.getType())) {
                time.setText(expiryText);
                text1.setTextColor(resources.getColor(R.color.offer_color_yellow));
                text2.setTextColor(resources.getColor(R.color.offer_color_yellow));
                text3.setTextColor(resources.getColor(R.color.offer_color_yellow));

                if (offer.isAvailableNow()) {
                    itemView.setBackgroundResource(R.drawable.card_yellow);
                    footerText.setText("grab offer");
//                    footerImageView.setVisibility(View.GONE);
                    buckTextImage.setVisibility(View.VISIBLE);
                    buckTextImage.setText(String.valueOf(100));

                    footerImageView.setVisibility(View.GONE);

                    if(offer.getSourceName()!=null && !TextUtils.isEmpty(offer.getSourceName())){
                        detailIcon.setBackground(resources.getDrawable(R.drawable.icon_discover_offer_socialpool_grey));
                        detailText.setText("from " + offer.getSourceName());
                        params.weight = 0.5f;
                        parentView.setWeightSum(1f);
                        availabilityLayout.setLayoutParams(params);
                        detailLayout.setLayoutParams(params);
                        parentView.removeAllViews();
                        parentView.addView(availabilityLayout);
                        parentView.addView(detailLayout);
                        detailLayout.setVisibility(View.VISIBLE);
                    }else {
                        detailLayout.setVisibility(View.GONE);
                        params.weight = 1.0f;
                        parentView.setWeightSum(1f);
                        availabilityLayout.setLayoutParams(params);
                        parentView.removeAllViews();
                        parentView.addView(availabilityLayout);
                    }

                } else {
                    itemView.setBackgroundResource(R.drawable.card_gray);
                    footerText.setText("remind me");
                    footerImageView.setImageDrawable(resources.getDrawable(R.drawable.icon_discover_offer_clock_white));
                }

            } else if ("offer".equalsIgnoreCase(offer.getType())) {
                text1.setTextColor(resources.getColor(R.color.offer_color_green));
                text2.setTextColor(resources.getColor(R.color.offer_color_green));
                text3.setTextColor(resources.getColor(R.color.offer_color_green));
                params.weight = 1.0f;
                parentView.setWeightSum(1f);
                availabilityLayout.setLayoutParams(params);
                parentView.removeAllViews();
                parentView.addView(availabilityLayout);
                detailLayout.setVisibility(View.GONE);
                time.setText(expiryText);
                if (offer.isAvailableNow()) {
                    itemView.setBackgroundResource(R.drawable.card_green);

                    footerText.setText("use offer");
                    if (offer.getOfferCost() > 0) {
                        buckTextImage.setVisibility(View.VISIBLE);
                        buckTextImage.setText(String.valueOf(offer.getOfferCost()));
                    } else {
                        buckTextImage.setVisibility(View.GONE);
                    }
                    footerImageView.setVisibility(View.GONE);
                } else {
                    itemView.setBackgroundResource(R.drawable.card_gray);
                    footerText.setText("remind me");
                    footerImageView.setImageDrawable(resources.getDrawable(R.drawable.icon_discover_offer_clock_white));
                }

            } else if ("deal".equalsIgnoreCase(offer.getType())) {

                text1.setTextColor(resources.getColor(R.color.offer_color_green));
                text2.setTextColor(resources.getColor(R.color.offer_color_green));
                text3.setTextColor(resources.getColor(R.color.offer_color_green));
                params.weight = 1.0f;
                parentView.setWeightSum(1f);
                availabilityLayout.setLayoutParams(params);
                parentView.removeAllViews();
                parentView.addView(availabilityLayout);
                detailLayout.setVisibility(View.GONE);
                time.setText(expiryText);
                if (offer.isAvailableNow()) {
                    itemView.setBackgroundResource(R.drawable.card_green);
                    footerImageView.setVisibility(View.GONE);
                    footerText.setText("use offer");
                    buckTextImage.setVisibility(View.GONE);

                } else {
                    itemView.setBackgroundResource(R.drawable.card_gray);
                    footerText.setText("remind me");
                    footerImageView.setImageDrawable(resources.getDrawable(R.drawable.icon_discover_offer_clock_white));
                }

            } else if ("bank_deal".equalsIgnoreCase(offer.getType())) {

                text1.setTextColor(resources.getColor(R.color.offer_color_blue));
                text2.setTextColor(resources.getColor(R.color.offer_color_blue));
                text3.setTextColor(resources.getColor(R.color.offer_color_blue));
                time.setText(expiryText);
                if(!TextUtils.isEmpty(offer.getSource())){
                    detailIcon.setBackground(resources.getDrawable(R.drawable.icon_discover_offer_bank_creditcard_grey));
                    detailText.setText(offer.getSource());
                    params.weight = 0.5f;
                    parentView.setWeightSum(1f);
                    availabilityLayout.setLayoutParams(params);
                    detailLayout.setLayoutParams(params);
                    parentView.removeAllViews();
                    parentView.addView(availabilityLayout);
                    parentView.addView(detailLayout);
                    detailLayout.setVisibility(View.VISIBLE);
                }else {
                    detailLayout.setVisibility(View.GONE);
                    params.weight = 1.0f;
                    parentView.setWeightSum(1f);
                    availabilityLayout.setLayoutParams(params);
                    parentView.removeAllViews();
                    parentView.addView(availabilityLayout);
                }

                if(offer.isAvailableNow()){
                    itemView.setBackgroundResource(R.drawable.card_blue);
                    footerImageView.setVisibility(View.GONE);
                    buckTextImage.setVisibility(View.GONE);
                    footerText.setText("use offer");

                }else {
                    itemView.setBackgroundResource(R.drawable.card_gray);
                    footerText.setText("remind me");
                    footerImageView.setImageDrawable(resources.getDrawable(R.drawable.icon_discover_offer_clock_white));
                }
            }


            if ("flatoff".equalsIgnoreCase(offer.getMeta().getRewardType())) {

                text1.setTextSize(TypedValue.COMPLEX_UNIT_PX, resources.getDimensionPixelSize(R.dimen.offer_flatoff_size));

            } else if ("reducedprice".equalsIgnoreCase(offer.getMeta().getRewardType())) {

                text1.setTextSize(TypedValue.COMPLEX_UNIT_PX, resources.getDimensionPixelSize(R.dimen.offer_reduced_size));

            } else if ("buffet".equalsIgnoreCase(offer.getMeta().getRewardType())) {

                text1.setTextSize(TypedValue.COMPLEX_UNIT_PX, resources.getDimensionPixelSize(R.dimen.offer_buffet_size));

            } else if ("custom".equalsIgnoreCase(offer.getMeta().getRewardType())) {

                text1.setTextSize(TypedValue.COMPLEX_UNIT_PX, resources.getDimensionPixelSize(R.dimen.offer_custom_size));

            } else if ("onlyhappyhours".equalsIgnoreCase(offer.getMeta().getRewardType())) {

                text1.setTextSize(TypedValue.COMPLEX_UNIT_PX, resources.getDimensionPixelSize(R.dimen.offer_flatoff_size));

            } else {   //buy_one_get_one,free,discount,happyhours,unlimited,combo

                text1.setTextSize(TypedValue.COMPLEX_UNIT_PX, resources.getDimensionPixelSize(R.dimen.offer_all_size));
            }

            ((ViewPager) container).addView(itemView);
            return itemView;

        } else {
            final View itemView = inflater.inflate(R.layout.layout_footer_offer, container, false);

            TextView footerText = (TextView) itemView.findViewById(R.id.footerText);
            RelativeLayout emptyLayout = (RelativeLayout) itemView.findViewById(R.id.emptyLayout);

            if (hasMoreOffers) {
                footerText.setText("view more");
                emptyLayout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(v.getContext(), OutletDetailsActivity.class);
                        intent.putExtra(AppConstants.INTENT_PARAM_OUTLET_ID, outlet.get_id());
                        v.getContext().startActivity(intent);
                    }
                });

            } else {
//                footerText.setText("submit offer");
                footerText.setText("");
                emptyLayout.setBackgroundResource(R.drawable.submit_offer_card_front);
                emptyLayout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(v.getContext(), SubmitOfferActivity.class);
                        intent.putExtra(AppConstants.INTENT_PARAM_SUMIT_OFFER_OUTLET_NAME, outlet.getName());
                        intent.putExtra(AppConstants.INTENT_PARAM_SUMIT_OFFER_OUTLET_ADDRESS,outlet.getAddress());
                        v.getContext().startActivity(intent);
                    }
                });

            }

            ((ViewPager) container).addView(itemView);
            return itemView;
        }



    }

    @Override
    public float getPageWidth(int position) {
        return 0.85f;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
    }

}
