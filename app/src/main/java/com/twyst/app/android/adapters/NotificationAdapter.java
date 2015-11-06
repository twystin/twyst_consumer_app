package com.twyst.app.android.adapters;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.drawable.GradientDrawable;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import com.twyst.app.android.R;
import com.twyst.app.android.activities.DiscoverActivity;
import com.twyst.app.android.activities.OfferDetailActivity;
import com.twyst.app.android.activities.SearchActivity;
import com.twyst.app.android.activities.WalletActivity;
import com.twyst.app.android.model.NotificationData;
import com.twyst.app.android.util.AppConstants;

/**
 * Created by rahuls on 23/7/15.
 */
public class NotificationAdapter extends RecyclerView.Adapter<NotificationAdapter.NotificationViewHolder>{

	private List<NotificationData> items = new ArrayList<>();

	public List<NotificationData> getItems() {
		return items;
	}

	public void setItems(List<NotificationData> items) {
		this.items = items;
	}


    @Override
    public NotificationViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_notification_card, parent, false);

        NotificationViewHolder viewHolder = new NotificationViewHolder(v);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(NotificationViewHolder holder, int position) {
        final View view = holder.itemView;
        final Resources resources = view.getContext().getResources();
		final NotificationData notificationData = items.get(position);

		GradientDrawable background = (GradientDrawable) holder.notifyCircle.getBackground();

        holder.notifyHeader.setText(notificationData.getMessage());
        holder.notifyDetail.setText(notificationData.getDetail());


        if(notificationData.getIcon().equalsIgnoreCase(resources.getString(R.string.icon_voucher_available))){
            background.setColor(resources.getColor(R.color.notification_red));
            holder.notifyCircle.setBackground(background);
            holder.notificationBtn.setImageDrawable(resources.getDrawable(R.drawable.notification_wallet_icon));
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (!TextUtils.isEmpty(notificationData.getOutlet()) && !TextUtils.isEmpty(notificationData.getOffer())) {
                        Intent intent = new Intent(view.getContext(), OfferDetailActivity.class);
                        intent.putExtra(AppConstants.INTENT_PARAM_OUTLET_ID, notificationData.getOutlet());
                        intent.putExtra(AppConstants.INTENT_PARAM_OFFER_ID, notificationData.getOffer());
                        view.getContext().startActivity(intent);
                    }
                }
            });

        } else if(notificationData.getIcon().equalsIgnoreCase(resources.getString(R.string.icon_voucher_redeemed))){
            background.setColor(resources.getColor(R.color.notification_light_gray));
            holder.notifyCircle.setBackground(background);
            holder.notificationBtn.setImageDrawable(resources.getDrawable(R.drawable.notification_wallet_icon));


        } else if(notificationData.getIcon().equalsIgnoreCase(resources.getString(R.string.icon_voucher_pending))){
            background.setColor(resources.getColor(R.color.notification_green));
            holder.notifyCircle.setBackground(background);
            holder.notificationBtn.setImageDrawable(resources.getDrawable(R.drawable.notification_voucher_pending));
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (!TextUtils.isEmpty(notificationData.getOutlet()) && !TextUtils.isEmpty(notificationData.getOffer())) {
                        Intent intent = new Intent(view.getContext(), OfferDetailActivity.class);
                        intent.putExtra(AppConstants.INTENT_PARAM_OUTLET_ID, notificationData.getOutlet());
                        intent.putExtra(AppConstants.INTENT_PARAM_OFFER_ID, notificationData.getOffer());
                        view.getContext().startActivity(intent);
                    } else {
                        Intent intent = new Intent(view.getContext(), WalletActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                        view.getContext().startActivity(intent);
                    }
                }
            });

        } else if (notificationData.getIcon().equalsIgnoreCase(resources.getString(R.string.icon_active_voucher_nearby))){
            background.setColor(resources.getColor(R.color.notification_blue));
            holder.notifyCircle.setBackground(background);
            holder.notificationBtn.setImageDrawable(resources.getDrawable(R.drawable.notification_active_voucher));
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (!TextUtils.isEmpty(notificationData.getOutlet()) && !TextUtils.isEmpty(notificationData.getOffer())) {
                        Intent intent = new Intent(view.getContext(), OfferDetailActivity.class);
                        intent.putExtra(AppConstants.INTENT_PARAM_OUTLET_ID, notificationData.getOutlet());
                        intent.putExtra(AppConstants.INTENT_PARAM_OFFER_ID, notificationData.getOffer());
                        view.getContext().startActivity(intent);
                    } else {
                        Intent intent = new Intent(view.getContext(), WalletActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                        view.getContext().startActivity(intent);
                    }
                }
            });

        } else if(notificationData.getIcon().equalsIgnoreCase(resources.getString(R.string.icon_new_offers))){
            background.setColor(resources.getColor(R.color.notification_purple));
            holder.notifyCircle.setBackground(background);
            holder.notificationBtn.setImageDrawable(resources.getDrawable(R.drawable.notification_offer_new));
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (!TextUtils.isEmpty(notificationData.getOutlet()) && !TextUtils.isEmpty(notificationData.getOffer())) {
                        Intent intent = new Intent(view.getContext(), OfferDetailActivity.class);
                        intent.putExtra(AppConstants.INTENT_PARAM_OUTLET_ID, notificationData.getOutlet());
                        intent.putExtra(AppConstants.INTENT_PARAM_OFFER_ID, notificationData.getOffer());
                        view.getContext().startActivity(intent);
                    } else {
                        Intent intent = new Intent(view.getContext(), WalletActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                        view.getContext().startActivity(intent);
                    }
                }
            });

        } else if(notificationData.getIcon().equalsIgnoreCase(resources.getString(R.string.icon_offers_nearby))){
            background.setColor(resources.getColor(R.color.notification_blue));
            holder.notifyCircle.setBackground(background);
            holder.notificationBtn.setImageDrawable(resources.getDrawable(R.drawable.notification_offer_nearby));
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (!TextUtils.isEmpty(notificationData.getOutlet())) {
                        SharedPreferences.Editor sharedPreferences = view.getContext().getSharedPreferences(AppConstants.PREFERENCE_SHARED_PREF_NAME, Context.MODE_PRIVATE).edit();
                        sharedPreferences.putString(AppConstants.PREFERENCE_PARAM_SEARCH_QUERY, "");
                        sharedPreferences.commit();
                        Intent intent = new Intent(view.getContext(), SearchActivity.class);
                        intent.putExtra("Search", true);
                        intent.setAction("setChildYes");
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        view.getContext().startActivity(intent);
                    }
                }
            });

        } else if(notificationData.getIcon().equalsIgnoreCase(resources.getString(R.string.icon_reactivate_user))){
            background.setColor(resources.getColor(R.color.notification_orange));
            holder.notifyCircle.setBackground(background);
            holder.notificationBtn.setImageDrawable(resources.getDrawable(R.drawable.notification_outlet_icon));
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (!TextUtils.isEmpty(notificationData.getOutlet()) && !TextUtils.isEmpty(notificationData.getOffer())) {
                        Intent intent = new Intent(view.getContext(), OfferDetailActivity.class);
                        intent.putExtra(AppConstants.INTENT_PARAM_OUTLET_ID, notificationData.getOutlet());
                        intent.putExtra(AppConstants.INTENT_PARAM_OFFER_ID, notificationData.getOffer());
                        view.getContext().startActivity(intent);
                    } else {
                        Intent intent = new Intent(view.getContext(), DiscoverActivity.class);
                        intent.setAction("setChildNo");
                        view.getContext().startActivity(intent);
                    }
                }
            });

        } else if(notificationData.getIcon().equalsIgnoreCase(resources.getString(R.string.icon_grab_offer))){
            background.setColor(resources.getColor(R.color.notification_yellow));
            holder.notifyCircle.setBackground(background);
            holder.notificationBtn.setImageDrawable(resources.getDrawable(R.drawable.notification_buck_icon));
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (!TextUtils.isEmpty(notificationData.getOutlet()) && !TextUtils.isEmpty(notificationData.getOffer())) {
                        Intent intent = new Intent(view.getContext(), OfferDetailActivity.class);
                        intent.putExtra(AppConstants.INTENT_PARAM_OUTLET_ID, notificationData.getOutlet());
                        intent.putExtra(AppConstants.INTENT_PARAM_OFFER_ID, notificationData.getOffer());
                        view.getContext().startActivity(intent);
                    }
                }
            });

        } else if(notificationData.getIcon().equalsIgnoreCase(resources.getString(R.string.icon_new_features))){
            background.setColor(resources.getColor(R.color.notification_purple));
            holder.notifyCircle.setBackground(background);
            holder.notificationBtn.setImageDrawable(resources.getDrawable(R.drawable.notification_twyst_icon));

        } else if(notificationData.getIcon().equalsIgnoreCase(resources.getString(R.string.icon_coupon_grabbed))){
            background.setColor(resources.getColor(R.color.notification_light_gray));
            holder.notifyCircle.setBackground(background);
            holder.notificationBtn.setImageDrawable(resources.getDrawable(R.drawable.notification_buck_icon));

        }  else if(notificationData.getIcon().equalsIgnoreCase(resources.getString(R.string.icon_birthday))) {
            background.setColor(resources.getColor(R.color.notification_pink));
            holder.notifyCircle.setBackground(background);
            holder.notificationBtn.setImageDrawable(resources.getDrawable(R.drawable.notification_brthday_icon));
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (!TextUtils.isEmpty(notificationData.getOutlet()) && !TextUtils.isEmpty(notificationData.getOffer())) {
                        Intent intent = new Intent(view.getContext(), OfferDetailActivity.class);
                        intent.putExtra(AppConstants.INTENT_PARAM_OUTLET_ID, notificationData.getOutlet());
                        intent.putExtra(AppConstants.INTENT_PARAM_OFFER_ID, notificationData.getOffer());
                        view.getContext().startActivity(intent);
                    }
                }
            });


        } else if(notificationData.getIcon().equalsIgnoreCase(resources.getString(R.string.icon_offer_approved_and_live))){
            background.setColor(resources.getColor(R.color.notification_red));
            holder.notifyCircle.setBackground(background);
            holder.notificationBtn.setImageDrawable(resources.getDrawable(R.drawable.notification_offer_approve));
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (!TextUtils.isEmpty(notificationData.getOutlet()) && !TextUtils.isEmpty(notificationData.getOffer())) {
                        Intent intent = new Intent(view.getContext(), OfferDetailActivity.class);
                        intent.putExtra(AppConstants.INTENT_PARAM_OUTLET_ID, notificationData.getOutlet());
                        intent.putExtra(AppConstants.INTENT_PARAM_OFFER_ID, notificationData.getOffer());
                        view.getContext().startActivity(intent);
                    }
                }
            });

        }  else if(notificationData.getIcon().equalsIgnoreCase(resources.getString(R.string.icon_outlet_specific_offer))){
            background.setColor(resources.getColor(R.color.notification_light_red));
            holder.notifyCircle.setBackground(background);
            holder.notificationBtn.setImageDrawable(resources.getDrawable(R.drawable.notification_specific_offer));

        } else if(notificationData.getIcon().equalsIgnoreCase("submit_offer/report_problem on success")){
            background.setColor(resources.getColor(R.color.notification_light_red));
            holder.notifyCircle.setBackground(background);
            holder.notificationBtn.setImageDrawable(resources.getDrawable(R.drawable.notification_specific_offer));
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (!TextUtils.isEmpty(notificationData.getOutlet()) && !TextUtils.isEmpty(notificationData.getOffer())) {
                        Intent intent = new Intent(view.getContext(), OfferDetailActivity.class);
                        intent.putExtra(AppConstants.INTENT_PARAM_OUTLET_ID, notificationData.getOutlet());
                        intent.putExtra(AppConstants.INTENT_PARAM_OFFER_ID, notificationData.getOffer());
                        view.getContext().startActivity(intent);
                    }
                }
            });

        } else if (notificationData.getIcon().equalsIgnoreCase(resources.getString(R.string.icon_winback))){
            background.setColor(resources.getColor(R.color.notification_orange));
            holder.notifyCircle.setBackground(background);
            holder.notificationBtn.setImageDrawable(resources.getDrawable(R.drawable.notification_voucher_winback));
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (!TextUtils.isEmpty(notificationData.getOutlet()) && !TextUtils.isEmpty(notificationData.getOffer())) {
                        Intent intent = new Intent(view.getContext(), OfferDetailActivity.class);
                        intent.putExtra(AppConstants.INTENT_PARAM_OUTLET_ID, notificationData.getOutlet());
                        intent.putExtra(AppConstants.INTENT_PARAM_OFFER_ID, notificationData.getOffer());
                        view.getContext().startActivity(intent);
                    }
                }
            });

        }else if(notificationData.getIcon().equalsIgnoreCase(resources.getString(R.string.icon_bill_rejected))){
            background.setColor(resources.getColor(R.color.notification_light_gray));
            holder.notifyCircle.setBackground(background);
            holder.notificationBtn.setImageDrawable(resources.getDrawable(R.drawable.notification_bill_reject));
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (!TextUtils.isEmpty(notificationData.getOutlet()) && !TextUtils.isEmpty(notificationData.getOffer())) {
                        Intent intent = new Intent(view.getContext(), OfferDetailActivity.class);
                        intent.putExtra(AppConstants.INTENT_PARAM_OUTLET_ID, notificationData.getOutlet());
                        intent.putExtra(AppConstants.INTENT_PARAM_OFFER_ID, notificationData.getOffer());
                        view.getContext().startActivity(intent);
                    }
                }
            });

        }else if(notificationData.getIcon().equalsIgnoreCase(resources.getString(R.string.icon_bill_approved))){
            background.setColor(resources.getColor(R.color.notification_orange));
            holder.notifyCircle.setBackground(background);
            holder.notificationBtn.setImageDrawable(resources.getDrawable(R.drawable.notification_bill_approved));
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (!TextUtils.isEmpty(notificationData.getOutlet()) && !TextUtils.isEmpty(notificationData.getOffer())) {
                        Intent intent = new Intent(view.getContext(), OfferDetailActivity.class);
                        intent.putExtra(AppConstants.INTENT_PARAM_OUTLET_ID, notificationData.getOutlet());
                        intent.putExtra(AppConstants.INTENT_PARAM_OFFER_ID, notificationData.getOffer());
                        view.getContext().startActivity(intent);
                    }
                }
            });

        } else if(notificationData.getIcon().equalsIgnoreCase(resources.getString(R.string.icon_wallet))){
            background.setColor(resources.getColor(R.color.notification_red));
            holder.notifyCircle.setBackground(background);
            holder.notificationBtn.setImageDrawable(resources.getDrawable(R.drawable.notification_wallet_icon));
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (!TextUtils.isEmpty(notificationData.getOutlet()) && !TextUtils.isEmpty(notificationData.getOffer())) {
                        Intent intent = new Intent(view.getContext(), OfferDetailActivity.class);
                        intent.putExtra(AppConstants.INTENT_PARAM_OUTLET_ID, notificationData.getOutlet());
                        intent.putExtra(AppConstants.INTENT_PARAM_OFFER_ID, notificationData.getOffer());
                        view.getContext().startActivity(intent);
                    }
                }
            });

        }else{
            background.setColor(resources.getColor(R.color.notification_light_red));
            holder.notifyCircle.setBackground(background);
            holder.notificationBtn.setImageDrawable(resources.getDrawable(R.drawable.notification_unknown));
        }



    }


    @Override
    public int getItemCount() {
        return items.size();
    }

    public static class NotificationViewHolder extends RecyclerView.ViewHolder{

        ImageView notificationBtn;
        RelativeLayout notifyCircle;
        TextView notifyHeader;
        TextView notifyDetail;

        public NotificationViewHolder(View itemView) {
            super(itemView);
            notifyCircle =(RelativeLayout)itemView.findViewById(R.id.notifyCircle);
            notificationBtn = (ImageView) itemView.findViewById(R.id.notificationBtn);
            notifyHeader = (TextView) itemView.findViewById(R.id.notifyHeader);
            notifyDetail = (TextView) itemView.findViewById(R.id.notifyDetail);

        }
    }
}
