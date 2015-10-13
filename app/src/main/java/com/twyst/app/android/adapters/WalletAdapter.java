package com.twyst.app.android.adapters;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

import com.twyst.app.android.R;
import com.twyst.app.android.activities.DiscoverActivity;
import com.twyst.app.android.activities.OfferDetailActivity;
import com.twyst.app.android.activities.OutletDetailsActivity;
import com.twyst.app.android.activities.WalletActivity;
import com.twyst.app.android.model.BaseResponse;
import com.twyst.app.android.model.Data;
import com.twyst.app.android.model.Meta;
import com.twyst.app.android.model.Offer;
import com.twyst.app.android.model.Outlet;
import com.twyst.app.android.service.HttpService;
import com.twyst.app.android.util.AppConstants;
import com.twyst.app.android.util.RoundedTransformation;
import com.twyst.app.android.util.TwystProgressHUD;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by rahuls on 10/7/15.
 */
public class WalletAdapter extends RecyclerView.Adapter<WalletAdapter.WalletViewHolder>{

    private List<Outlet> items = new ArrayList<>();

    public List<Outlet> getItems() {
        return items;
    }

    public void setItems(List<Outlet> items) {
        this.items = items;
    }


    @Override
    public WalletViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_wallet_card, parent, false);

        WalletViewHolder viewHolder = new WalletViewHolder(v);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final WalletViewHolder holder, int position) {

        final View view = holder.itemView;
        final Outlet outlet = items.get(position);
        final Resources resources = view.getContext().getResources();

        List<Offer> offers = outlet.getOffers();
        String lapseText = "";
        if (!TextUtils.isEmpty(offers.get(0).getLapseDate())) {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
            try {
                Date lapseDate = sdf.parse(offers.get(0).getLapseDate());
                long days = TimeUnit.DAYS.convert(lapseDate.getTime() - System.currentTimeMillis(), TimeUnit.MILLISECONDS);

                long hours = TimeUnit.HOURS.convert(lapseDate.getTime() - System.currentTimeMillis(),TimeUnit.MILLISECONDS);
                Log.i("days",""+days);
                if(offers.get(0).isAvailableNow()){

                    if(days == 0){
                        lapseText = hours + ((hours == 1) ? " hours " : " hours ") + "left";
                    }else if(days > 7){
                        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("d-MMM");
                        lapseText = "valid till " + simpleDateFormat.format(lapseDate).toLowerCase();
                    }else {
                        lapseText = days + ((days == 1) ? " day " : " days ") + "left";
                    }
                }else {

                    if(offers.get(0).getAvailableNext()!=null) {
                        if (!offers.get(0).isAvailableNow() && !TextUtils.isEmpty(offers.get(0).getAvailableNext().getDay()) && !TextUtils.isEmpty(offers.get(0).getAvailableNext().getTime())) {
                                lapseText = offers.get(0).getAvailableNext().getDay() + ", " + offers.get(0).getAvailableNext().getTime();
                        } else {
                            lapseText = null;
                        }
                    }

                }

            } catch (ParseException e) {
                e.printStackTrace();
            }
        }


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
        holder.outletName.setText(outlet.getName());
        holder.outletAddress.setText(address);
        if(!TextUtils.isEmpty(outlet.getDistance())) {
            holder.distance.setText(outlet.getDistance() + " Km");
        }else {
            holder.distance.setVisibility(View.GONE);
        }

        Picasso picasso = Picasso.with(view.getContext());
        picasso.setIndicatorsEnabled(AppConstants.DEGUG_PICASSO);
        picasso.setLoggingEnabled(AppConstants.DEGUG_PICASSO);
        picasso.load(outlet.getBackground())
                .noFade()
                .centerCrop()
                .resize(250, 372)
                .transform(new RoundedTransformation(24, 0))
                .into(holder.outletImage);

        if(!offers.get(0).isAvailableNow()){
            holder.footer_bckgrd.setBackgroundColor(resources.getColor(R.color.offer_color_gray));
            holder.line.setImageDrawable(resources.getDrawable(R.drawable.wallet_vertical_grey_divider));
            holder.coupon_img_icon.setImageDrawable(resources.getDrawable(R.drawable.icon_outlet_detail_coupon_forma));
            holder.coupon__btn_txt.setText(resources.getString(R.string.remind_me));
        }else {
            holder.footer_bckgrd.setBackgroundColor(resources.getColor(R.color.offer_color_red));
            holder.line.setImageDrawable(resources.getDrawable(R.drawable.wallet_vertical_red_divider));
            holder.coupon_img_icon.setImageDrawable(resources.getDrawable(R.drawable.icon_outlet_detail_coupon_wallet_white));
            holder.coupon__btn_txt.setText(resources.getString(R.string.use_offer));
        }

        if(outlet.isFollowing()){
            holder.followOutletBtn.setImageResource(R.drawable.icon_discover_follow_outlet);
        }else {
            holder.followOutletBtn.setImageResource(R.drawable.icon_discover_follow_outlet_no);
        }

        holder.valid_txt.setText(lapseText);
        holder.type_offer.setText(offers.get(0).getHeader());
        holder.coupon_text1.setText(offers.get(0).getLine1());
        holder.coupon_text2.setText(offers.get(0).getLine2());

        if (offers.get(0).getMeta() != null) {
            Meta meta = offers.get(0).getMeta();
            if (meta != null) {
                if (meta.getRewardType() != null) {

                    if (meta.getRewardType().equals("buy_one_get_one") || meta.getRewardType().equals("onlyhappyhours") || meta.getRewardType().equals("flatoff")
                            || meta.getRewardType().equals("reduced") || meta.getRewardType().equals("happyhours") || meta.getRewardType().equals("custom")) {
                        holder.type_offer.setTextSize(TypedValue.COMPLEX_UNIT_PX, resources.getDimensionPixelSize(R.dimen.outlet_detail_header_type2_text));

                    } else {
                        //unlimited,combo,buffet,discount,free
                        holder.type_offer.setTextSize(TypedValue.COMPLEX_UNIT_PX, resources.getDimensionPixelSize(R.dimen.outlet_detail_header_type1_text));
                    }

                }
            }
        }

        holder.outletImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(view.getContext(), OutletDetailsActivity.class);
                intent.putExtra(AppConstants.INTENT_PARAM_OUTLET_ID, outlet.get_id());
                view.getContext().startActivity(intent);
            }
        });

        holder.coupon_bg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Offer offer = outlet.getOffers().get(0);
                Intent intent = new Intent(view.getContext(), OfferDetailActivity.class);
                intent.putExtra(AppConstants.INTENT_PARAM_OUTLET_OBJECT,outlet);
                intent.putExtra(AppConstants.INTENT_PARAM_OFFER_OBJECT, offer);
                ((WalletActivity) view.getContext()).startActivityForResult(intent, WalletActivity.REQ_CODE);

            }
        });

        holder.callOutletBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {

                AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());
                builder.setTitle("Do you want to call "+outlet.getName() +"?")
                        .setPositiveButton("YES", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                if(!TextUtils.isEmpty(outlet.getPhone())){
                                    String number = "tel:" + outlet.getPhone();
                                    view.getContext().startActivity(new Intent(Intent.ACTION_CALL, Uri.parse(number)));
                                }else{
                                    Toast.makeText(view.getContext(),"Phone number doesn't exist!",Toast.LENGTH_SHORT).show();
                                }

                            }
                        })
                        .setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                // User cancelled the dialog
                            }
                        });
                builder.create().show();

            }
        });
        SharedPreferences prefs = view.getContext().getSharedPreferences(AppConstants.PREFERENCE_SHARED_PREF_NAME, Context.MODE_PRIVATE);
        final String userToken = prefs.getString(AppConstants.PREFERENCE_USER_TOKEN, "");

        holder.followOutletBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
                final TwystProgressHUD twystProgressHUD = TwystProgressHUD.show(view.getContext(), false, null);
                if (outlet.isFollowing()) {
                    holder.followOutletBtn.setImageResource(R.drawable.icon_discover_follow_outlet_no);
                    HttpService.getInstance().unFollowEvent(userToken, outlet.get_id(), new Callback<BaseResponse<Data>>() {
                        @Override
                        public void success(BaseResponse<Data> dataBaseResponse, Response response) {
                            if (dataBaseResponse.isResponse()) {
                                outlet.setFollowing(false);
                                holder.followOutletBtn.setImageResource(R.drawable.icon_discover_follow_outlet_no);
                                twystProgressHUD.dismiss();
                                Log.i("unfollow event response" ,""+  response);
                            } else {
                                holder.followOutletBtn.setImageResource(R.drawable.icon_discover_follow_outlet);
                                Log.i("false response" ,""+  dataBaseResponse.getMessage());
                            }
                        }

                        @Override
                        public void failure(RetrofitError error) {
                            twystProgressHUD.dismiss();
                            WalletActivity walletActivity = (WalletActivity) view.getContext();
                            walletActivity.handleRetrofitError(error);
                        }
                    });


                } else {
                    holder.followOutletBtn.setImageResource(R.drawable.icon_discover_follow_outlet);
                    HttpService.getInstance().followEvent(userToken, outlet.get_id(), new Callback<BaseResponse<Data>>() {
                        @Override
                        public void success(BaseResponse<Data> dataBaseResponse, Response response) {
                            if (dataBaseResponse.isResponse()) {
                                int twystBucks = dataBaseResponse.getData().getTwyst_bucks();
                                WalletActivity walletActivity = (WalletActivity) view.getContext();
                                if (twystBucks > walletActivity.getTwystBucks()){
                                    walletActivity.setTwystBucks(twystBucks);
                                    ((TextView) walletActivity.findViewById(R.id.buckText)).setText(String.valueOf(twystBucks));
                                    Toast.makeText(walletActivity, walletActivity.getResources().getString(R.string.bucks_earned_follow_outlet), Toast.LENGTH_SHORT).show();
                                }
                                holder.followOutletBtn.setImageResource(R.drawable.icon_discover_follow_outlet);
                                outlet.setFollowing(true);
                                twystProgressHUD.dismiss();
                                Log.i("follow event response" ,""+ response);
                            } else {
                                holder.followOutletBtn.setImageResource(R.drawable.icon_discover_follow_outlet_no);
                                Log.i("false response",""+ dataBaseResponse.getMessage());
                            }
                        }

                        @Override
                        public void failure(RetrofitError error) {
                            twystProgressHUD.dismiss();
                            WalletActivity walletActivity = (WalletActivity) view.getContext();
                            walletActivity.handleRetrofitError(error);
                        }
                    });

                }
            }
        });



    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public static class WalletViewHolder extends RecyclerView.ViewHolder {

        ImageView outletImage;
        TextView outletName;
        TextView outletAddress;
        TextView distance;
        ImageView callOutletBtn;
        ImageView followOutletBtn;
        TextView valid_txt;
        TextView type_offer;
        TextView coupon_text1;
        TextView coupon_text2;
        ImageView footer_bckgrd;
        ImageView coupon_img_icon;
        TextView outlet_500;
        TextView coupon__btn_txt;
        ImageView line;
        RelativeLayout coupon_bg;

        public WalletViewHolder(View itemView) {
            super(itemView);
            outletImage = (ImageView)itemView.findViewById(R.id.outletImage);
            callOutletBtn = (ImageView)itemView.findViewById(R.id.callOutletBtn);
            followOutletBtn = (ImageView)itemView.findViewById(R.id.followOutletBtn);
            footer_bckgrd = (ImageView)itemView.findViewById(R.id.footer_bckgrd);
            coupon_img_icon = (ImageView)itemView.findViewById(R.id.coupon_img_icon);
            line = (ImageView)itemView.findViewById(R.id.line);
            outletName = (TextView)itemView.findViewById(R.id.outletName);
            outletAddress = (TextView)itemView.findViewById(R.id.outletAddress);
            distance = (TextView)itemView.findViewById(R.id.distance);
            valid_txt = (TextView)itemView.findViewById(R.id.valid_txt);
            type_offer = (TextView)itemView.findViewById(R.id.type_offer);
            coupon_text1 = (TextView)itemView.findViewById(R.id.coupon_text1);
            coupon_text2 = (TextView)itemView.findViewById(R.id.coupon_text2);
            outlet_500 = (TextView)itemView.findViewById(R.id.outlet_500);
            coupon__btn_txt = (TextView)itemView.findViewById(R.id.coupon__btn_txt);
            coupon_bg = (RelativeLayout)itemView.findViewById(R.id.coupon_bg);

        }
    }
}
