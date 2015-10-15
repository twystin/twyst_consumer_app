package com.twyst.app.android.adapters;

import android.content.Intent;
import android.content.res.Resources;
import android.support.v7.widget.RecyclerView;
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

import com.twyst.app.android.R;
import com.twyst.app.android.activities.OfferDetailActivity;
import com.twyst.app.android.activities.OutletDetailsActivity;
import com.twyst.app.android.activities.SubmitOfferActivity;
import com.twyst.app.android.model.Meta;
import com.twyst.app.android.model.Offer;
import com.twyst.app.android.util.AppConstants;

/**
 * Created by anilkg on 22/6/15.
 */
public class OutletDetailsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int VIEW_NORMAL = 0;
    private static final int VIEW_FOOTER = 1;
    private static final int VIEW_INVISIBLE = 2;
    private String outletname;
    private String address;
    private String outlet_id;
    private List<Offer> items = new ArrayList<>();


    public List<Offer> getItems() {
        return items;
    }

    public void setItems(List<Offer> items, String name, String address, String outlet_id) {
        this.items = items;
        this.outletname=name;
        this.address=address;
        this.outlet_id= outlet_id;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        if (viewType == VIEW_NORMAL) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_outlet_detail_card, parent, false);

            OfferViewHolder vh = new OfferViewHolder(v);
            return vh;
        } else if (viewType == VIEW_FOOTER) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_outlet_detail_submit_card, parent, false);

            SubmitViewHolder vh = new SubmitViewHolder(v);
            return vh;
        } else if (viewType == VIEW_INVISIBLE) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_invisible, parent, false);

        InvisibleViewHolder vh = new InvisibleViewHolder(v);
        return vh;
    }


    throw new RuntimeException("there is no type that matches the type " + viewType + " + make sure your using types correctly");
    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0) {
            return VIEW_INVISIBLE;
        }else if (position == items.size()+1) {
            return VIEW_FOOTER;
        } else {
            return VIEW_NORMAL;
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder1, int position) {
        if(holder1 instanceof OfferViewHolder){
            final OfferViewHolder holder = (OfferViewHolder) holder1;
            final Offer offer = items.get(position-1);
            final View view = holder.itemView;
            Resources resources = view.getContext().getResources();

            String expiryText = "";
            String lapseText = "";
            if("coupon".equalsIgnoreCase(offer.getType())){
                if (!TextUtils.isEmpty(offer.getLapseDate())) {
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
                    try {
                        Date lapseDate = sdf.parse(offer.getLapseDate());
                        long days = TimeUnit.DAYS.convert(lapseDate.getTime() - System.currentTimeMillis(), TimeUnit.MILLISECONDS);

                        long hours = TimeUnit.HOURS.convert(lapseDate.getTime() - System.currentTimeMillis(),TimeUnit.MILLISECONDS);
                        Log.i("days",""+days);
                        if(offer.isAvailableNow()){

                            if(days == 0){
                                lapseText = hours + ((hours == 1) ? " hours " : " hours ") + "left";
                            }else if(days > 7){
                                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("d-MMM");
                                lapseText = "valid till " + simpleDateFormat.format(lapseDate).toLowerCase();
                            }else {
                                lapseText = days + ((days == 1) ? " day " : " days ") + "left";
                            }
                        }else {

                            if(!offer.isAvailableNow() && !TextUtils.isEmpty(offer.getAvailableNext().getDay()) && !TextUtils.isEmpty(offer.getAvailableNext().getTime())){

                                lapseText = offer.getAvailableNext().getDay()+", "+offer.getAvailableNext().getTime();

                            }else {
                                lapseText = null;
                                holder.clock.setVisibility(View.INVISIBLE);
                            }

                        }


                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                }else {
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
                                holder.clock.setVisibility(View.INVISIBLE);
                            }

                        }

                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                }else { }
            }

            if ("coupon".equalsIgnoreCase(offer.getType())) {
                holder.couponBg.setBackgroundResource(R.color.outlet_coupon_bg_color_white);
                holder.typeOffer.setTextColor(resources.getColor(R.color.offer_color_red));
                holder.text2.setTextColor(resources.getColor(R.color.offer_color_red));
                holder.text3.setTextColor(resources.getColor(R.color.offer_color_red));
                holder.eclipseDot.setTextColor(resources.getColor(R.color.offer_color_red));
                //holder.couponLine.setBackgroundResource(R.drawable.outlet_detail_coupon_line_white);
                holder.outlet500.setVisibility(View.GONE);
                holder.footerImageView.setVisibility(View.VISIBLE);
                holder.time.setText(lapseText);
                if (offer.isAvailableNow()) {
                    holder.footerBckGrd.setBackgroundResource(R.drawable.button_red);

                    holder.footerImageView.setImageDrawable(resources.getDrawable(R.drawable.icon_outlet_detail_coupon_wallet_white));
                    holder.footerText.setText(resources.getString(R.string.use_offer));

                } else {
                    holder.footerBckGrd.setBackgroundResource(R.drawable.button_grey);
                    holder.footerImageView.setImageDrawable(resources.getDrawable(R.drawable.icon_outlet_detail_coupon_forma));
                    holder.footerText.setText(resources.getString(R.string.remind_me));

                }


            } else if ("pool".equalsIgnoreCase(offer.getType())) {
                holder.card_user_img.setBackgroundResource(R.drawable.icon_discover_offer_socialpool_grey);

                if(offer.getLapsedCouponSource()!=null){
                    String toDisplay;
                    if (offer.getLapsedCouponSource().getName()!=null && !TextUtils.isEmpty(offer.getLapsedCouponSource().getName())){
                        toDisplay = offer.getLapsedCouponSource().getName();
                    }else{
                        toDisplay = offer.getLapsedCouponSource().getPhone();
                    }
                    holder.cardDetails.setText("from " + toDisplay);
                    holder.cardDetails.setVisibility(View.VISIBLE);
                    holder.card_user_img.setVisibility(View.VISIBLE);
                }else {
                    holder.cardDetails.setVisibility(View.INVISIBLE);
                    holder.card_user_img.setVisibility(View.INVISIBLE);
                }

                holder.time.setText(expiryText);
                holder.typeOffer.setTextColor(resources.getColor(R.color.offer_color_yellow));
                holder.text2.setTextColor(resources.getColor(R.color.offer_color_yellow));
                holder.text3.setTextColor(resources.getColor(R.color.offer_color_yellow));
                holder.eclipseDot.setTextColor(resources.getColor(R.color.offer_color_yellow));

                if(offer.isAvailableNow()){
                    holder.footerBckGrd.setBackgroundResource(R.drawable.button_yellow);
                    holder.outlet500.setVisibility(View.VISIBLE);
                    holder.outlet500.setText(String.valueOf(100));
                    holder.footerImageView.setVisibility(View.GONE);
                    holder.footerText.setText(resources.getString(R.string.grab_offer));

                }else {
                    holder.footerBckGrd.setBackgroundResource(R.drawable.button_grey);
                    holder.outlet500.setVisibility(View.GONE);
                    holder.couponBg.setBackgroundResource(R.color.outlet_coupon_bg_color_white);
                    holder.footerImageView.setVisibility(View.VISIBLE);
                    holder.footerImageView.setImageDrawable(resources.getDrawable(R.drawable.icon_outlet_detail_coupon_forma));
                    holder.footerText.setText(resources.getString(R.string.remind_me));
                }

            } else if ("offer".equalsIgnoreCase(offer.getType())) {
                holder.typeOffer.setTextColor(resources.getColor(R.color.offer_color_green));
                holder.text2.setTextColor(resources.getColor(R.color.offer_color_green));
                holder.text3.setTextColor(resources.getColor(R.color.offer_color_green));
                holder.eclipseDot.setTextColor(resources.getColor(R.color.offer_color_green));
                holder.couponBg.setBackgroundResource(R.color.outlet_coupon_bg_color_white);
                //holder.couponLine.setBackgroundResource(R.drawable.outlet_detail_coupon_line_white);
                holder.time.setText(expiryText);
                if (offer.isAvailableNow()) {
                    holder.footerBckGrd.setBackgroundResource(R.drawable.button_green);
                    holder.footerImageView.setVisibility(View.GONE);
                    holder.footerText.setText(resources.getString(R.string.use_offer));
                    if(offer.getOfferCost()>0) {
                        holder.outlet500.setVisibility(View.VISIBLE);
                        holder.outlet500.setText(String.valueOf(offer.getOfferCost()));
                    }else {
                        holder.outlet500.setVisibility(View.GONE);
                    }
                } else {
                    holder.footerBckGrd.setBackgroundResource(R.drawable.button_grey);
                    holder.footerImageView.setVisibility(View.VISIBLE);
                    holder.outlet500.setVisibility(View.GONE);
                    holder.footerImageView.setImageDrawable(resources.getDrawable(R.drawable.icon_outlet_detail_coupon_forma));
                    holder.footerText.setText(resources.getString(R.string.remind_me));
                }


            } else if ("deal".equalsIgnoreCase(offer.getType())) {
                holder.couponBg.setBackgroundResource(R.color.outlet_coupon_bg_color_white);
               // holder.couponLine.setBackgroundResource(R.drawable.outlet_detail_coupon_line_white);
                holder.time.setText(expiryText);

                holder.typeOffer.setTextColor(resources.getColor(R.color.offer_color_green));
                holder.text2.setTextColor(resources.getColor(R.color.offer_color_green));
                holder.text3.setTextColor(resources.getColor(R.color.offer_color_green));
                holder.eclipseDot.setTextColor(resources.getColor(R.color.offer_color_green));
                if (offer.isAvailableNow()) {
                    holder.footerBckGrd.setBackgroundResource(R.drawable.button_green);
                    holder.outlet500.setVisibility(View.GONE);
                    holder.footerImageView.setVisibility(View.GONE);
                    holder.footerText.setText(resources.getString(R.string.use_offer));

                } else {
                    holder.footerBckGrd.setBackgroundResource(R.drawable.button_grey);
                    holder.outlet500.setVisibility(View.GONE);
                    holder.couponBg.setBackgroundResource(R.color.outlet_coupon_bg_color_white);
                    holder.footerImageView.setVisibility(View.VISIBLE);
                    holder.footerImageView.setImageDrawable(resources.getDrawable(R.drawable.icon_outlet_detail_coupon_forma));
                    holder.footerText.setText(resources.getString(R.string.remind_me));

                }

            } else if("bank_deal".equalsIgnoreCase(offer.getType())){
                holder.card_user_img.setBackgroundResource(R.drawable.icon_outlet_detail_creditcard);

                if(!TextUtils.isEmpty(offer.getSource())){
                    holder.cardDetails.setText(offer.getSource());

                    holder.cardDetails.setVisibility(View.VISIBLE);
                    holder.card_user_img.setVisibility(View.VISIBLE);
                }else {
                    holder.cardDetails.setVisibility(View.INVISIBLE);
                    holder.card_user_img.setVisibility(View.INVISIBLE);
                }

                holder.time.setText(expiryText);
                holder.typeOffer.setTextColor(resources.getColor(R.color.offer_color_blue));
                holder.text2.setTextColor(resources.getColor(R.color.offer_color_blue));
                holder.text3.setTextColor(resources.getColor(R.color.offer_color_blue));
                holder.eclipseDot.setTextColor(resources.getColor(R.color.offer_color_blue));

                if(offer.isAvailableNow()){
                    holder.footerBckGrd.setBackgroundResource(R.drawable.button_blue);
                    holder.outlet500.setVisibility(View.GONE);
                    holder.footerImageView.setVisibility(View.GONE);
                    holder.footerText.setText(resources.getString(R.string.use_offer));

                }else {
                    holder.footerBckGrd.setBackgroundResource(R.drawable.button_grey);
                    holder.outlet500.setVisibility(View.GONE);
                    holder.couponBg.setBackgroundResource(R.color.outlet_coupon_bg_color_white);
                    holder.footerImageView.setVisibility(View.VISIBLE);
                    holder.footerImageView.setImageDrawable(resources.getDrawable(R.drawable.icon_outlet_detail_coupon_forma));
                    holder.footerText.setText(resources.getString(R.string.remind_me));
                }
            } else if("checkin".equalsIgnoreCase(offer.getType())){

             // view.setBackgroundResource(R.drawable.card_checkin_bogo);
                holder.typeOffer.setTextColor(resources.getColor(R.color.outlet_txt_color_grey));
                holder.text2.setTextColor(resources.getColor(R.color.outlet_txt_color_grey));
                holder.text3.setTextColor(resources.getColor(R.color.outlet_txt_color_grey));
                holder.eclipseDot.setTextColor(resources.getColor(R.color.outlet_txt_color_grey_dark));
                holder.footerBckGrd.setBackgroundResource(R.drawable.button_light_grey);
                holder.footerImageView.setImageDrawable(resources.getDrawable(R.drawable.icon_outlet_detail_coupon_lock_white));
                holder.couponBg.setBackgroundResource(R.color.outlet_coupon_bg_color_grey);
                //holder.couponLine.setBackgroundResource(R.drawable.outlet_detail_coupon_line_grey);
                holder.footerText.setText(offer.getNext() + " check-ins to go");
                holder.outlet500.setVisibility(View.GONE);
                holder.footerImageView.setVisibility(View.VISIBLE);

                holder.time.setText(expiryText);
            }

            holder.couponBg.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(view.getContext(), OfferDetailActivity.class);
                    intent.putExtra(AppConstants.INTENT_PARAM_OUTLET_OBJECT,((OutletDetailsActivity)view.getContext()).outlet);
                    intent.putExtra(AppConstants.INTENT_PARAM_OFFER_OBJECT,offer);
                    view.getContext().startActivity(intent);
                }
            });
            holder.footerBckGrd.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(view.getContext(), OfferDetailActivity.class);
                    intent.putExtra(AppConstants.INTENT_PARAM_OUTLET_OBJECT,((OutletDetailsActivity)view.getContext()).outlet);
                    intent.putExtra(AppConstants.INTENT_PARAM_OFFER_OBJECT,offer);
                    view.getContext().startActivity(intent);
                }
            });

            if (offer.getMeta() != null) {
                Meta meta = offer.getMeta();
                if (meta!= null) {
                    if (meta.getRewardType() != null) {

                        if (TextUtils.isEmpty(offer.getLine1()) || TextUtils.isEmpty(offer.getLine2())) {
                            holder.eclipseDot.setVisibility(View.INVISIBLE);
                        }else {
                            holder.eclipseDot.setVisibility(View.VISIBLE);
                        }
                        holder.typeOffer.setText(offer.getHeader());
                        holder.text2.setText(offer.getLine1());
                        holder.text3.setText(offer.getLine2());


                        if (meta.getRewardType().equals("buy_one_get_one") || meta.getRewardType().equals("onlyhappyhours") || meta.getRewardType().equals("flatoff")
                                || meta.getRewardType().equals("reduced") || meta.getRewardType().equals("happyhours") || meta.getRewardType().equals("custom")) {
                            holder.typeOffer.setTextSize(TypedValue.COMPLEX_UNIT_PX, resources.getDimensionPixelSize(R.dimen.outlet_detail_header_type2_text));

                        } else {
                            //unlimited,combo,buffet,discount,free
                            holder.typeOffer.setTextSize(TypedValue.COMPLEX_UNIT_PX, resources.getDimensionPixelSize(R.dimen.outlet_detail_header_type1_text));
                        }


                    }else {

                    }
                }
                else {

                }
            }else {}

        }else if(holder1 instanceof SubmitViewHolder){

            SubmitViewHolder holder = (SubmitViewHolder) holder1;
            final View view = holder.itemView;
            Resources resources = view.getContext().getResources();
//            holder.submitOffer.setText("submit offer!");
            holder.submitOffer.setText("");

//            holder.submitOffer.setTextSize(TypedValue.COMPLEX_UNIT_PX, resources.getDimensionPixelSize(R.dimen.outlet_detail_header_type1_text));
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(final View v) {

                    Intent intent = new Intent(v.getContext(), SubmitOfferActivity.class);
                    intent.putExtra(AppConstants.INTENT_PARAM_SUBMIT_OFFER_OUTLET_NAME, outletname);
                    intent.putExtra(AppConstants.INTENT_PARAM_SUBMIT_OFFER_OUTLET_ADDRESS, address);
                    intent.putExtra(AppConstants.INTENT_PARAM_SUBMIT_OFFER_OUTLET_ID, outlet_id);
                    v.getContext().startActivity(intent);

                }
            });
        }

    }

    @Override
    public int getItemCount() {
        return items.size()+2;
    }

    public static class OfferViewHolder extends RecyclerView.ViewHolder {


        RelativeLayout couponBg;
        ImageView footerImageView;
        TextView footerText;
        TextView typeOffer;
        TextView text2;
        TextView text3;
        TextView time;
        ImageView card_user_img;
        TextView cardDetails;
        TextView eclipseDot;
        LinearLayout footerBckGrd;
        ImageView couponLine;
        TextView outlet500;
        RelativeLayout couponDetail;
        ImageView clock;

        public OfferViewHolder(View itemView) {
            super(itemView);


            couponBg = (RelativeLayout) itemView.findViewById(R.id.coupon_bg);
            typeOffer = (TextView) itemView.findViewById(R.id.type_offer);
            text2 = (TextView) itemView.findViewById(R.id.coupon_text1);
            text3 = (TextView) itemView.findViewById(R.id.coupon_text2);
            time = (TextView) itemView.findViewById(R.id.valid_txt);
            clock = (ImageView) itemView.findViewById(R.id.clock);

            card_user_img = (ImageView) itemView.findViewById(R.id.coupon_user_card);
            cardDetails = (TextView) itemView.findViewById(R.id.card_user_details);
            eclipseDot = (TextView) itemView.findViewById(R.id.eclipse);

            footerText = (TextView) itemView.findViewById(R.id.coupon_btn_txt);
            footerImageView = (ImageView) itemView.findViewById(R.id.coupon_img_icon);
            footerBckGrd = (LinearLayout) itemView.findViewById(R.id.footer_bckgrd);
            couponLine = (ImageView) itemView.findViewById(R.id.line);
            outlet500 = (TextView) itemView.findViewById(R.id.outlet_500);
            couponDetail = (RelativeLayout) itemView.findViewById(R.id.couponDetail);

            typeOffer.setText("");
            text2.setText("");
            text3.setText("");
            footerText.setText("");
            time.setText("");


        }
    }

    public static class SubmitViewHolder extends RecyclerView.ViewHolder {

        TextView submitOffer;

        public SubmitViewHolder(View view) {
            super(view);
            submitOffer = (TextView) view.findViewById(R.id.submitOffer);
            submitOffer.setText("");
            view.findViewById(R.id.coupon_bg).setBackgroundResource(R.drawable.submit_offer_card);
        }
    }

    public static class InvisibleViewHolder extends RecyclerView.ViewHolder {


        public InvisibleViewHolder(View view) {
            super(view);
        }
    }


}

