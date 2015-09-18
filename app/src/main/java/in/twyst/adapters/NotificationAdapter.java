package in.twyst.adapters;

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
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import in.twyst.R;
import in.twyst.activities.DiscoverActivity;
import in.twyst.activities.OfferDetailActivity;
import in.twyst.activities.OutletDetailsActivity;
import in.twyst.activities.SearchActivity;
import in.twyst.activities.WalletActivity;
import in.twyst.model.NotificationData;
import in.twyst.model.Outlet;
import in.twyst.util.AppConstants;

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
        if(notificationData.getIcon().equalsIgnoreCase("voucher_available")){
            background.setColor(resources.getColor(R.color.notification_red));
            holder.notifyCircle.setBackground(background);
            holder.notificationBtn.setImageDrawable(resources.getDrawable(R.drawable.notification_wallet_icon));
            holder.notifyHeader.setText("Voucher available");
            holder.notifyDetail.setText(notificationData.getMessage());
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

        } else if(notificationData.getIcon().equalsIgnoreCase("voucher_redeemed")){
            background.setColor(resources.getColor(R.color.notification_light_gray));
            holder.notifyCircle.setBackground(background);
            holder.notificationBtn.setImageDrawable(resources.getDrawable(R.drawable.notification_wallet_icon));
            holder.notifyHeader.setText("Voucher redeemed");
            holder.notifyDetail.setText(notificationData.getMessage());


        } else if(notificationData.getIcon().equalsIgnoreCase("voucher_pending")){
            background.setColor(resources.getColor(R.color.notification_green));
            holder.notifyCircle.setBackground(background);
            holder.notificationBtn.setImageDrawable(resources.getDrawable(R.drawable.notification_voucher_pending));
            holder.notifyHeader.setText("Voucher pending");
            holder.notifyDetail.setText(notificationData.getMessage());
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (!TextUtils.isEmpty(notificationData.getOutlet()) && !TextUtils.isEmpty(notificationData.getOffer())) {
                        Intent intent = new Intent(view.getContext(), OfferDetailActivity.class);
                        intent.putExtra(AppConstants.INTENT_PARAM_OUTLET_ID, notificationData.getOutlet());
                        intent.putExtra(AppConstants.INTENT_PARAM_OFFER_ID, notificationData.getOffer());
                        view.getContext().startActivity(intent);
                    }else {
                        Intent intent = new Intent(view.getContext(), WalletActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                        view.getContext().startActivity(intent);
                    }
                }
            });

        } else if (notificationData.getIcon().equalsIgnoreCase("active_voucher_nearby")){
            background.setColor(resources.getColor(R.color.notification_blue));
            holder.notifyCircle.setBackground(background);
            holder.notificationBtn.setImageDrawable(resources.getDrawable(R.drawable.notification_active_voucher));
            holder.notifyHeader.setText("Active voucher(s) nearby");
            holder.notifyDetail.setText(notificationData.getMessage());
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (!TextUtils.isEmpty(notificationData.getOutlet()) && !TextUtils.isEmpty(notificationData.getOffer())) {
                        Intent intent = new Intent(view.getContext(), OfferDetailActivity.class);
                        intent.putExtra(AppConstants.INTENT_PARAM_OUTLET_ID, notificationData.getOutlet());
                        intent.putExtra(AppConstants.INTENT_PARAM_OFFER_ID, notificationData.getOffer());
                        view.getContext().startActivity(intent);
                    }else {
                        Intent intent = new Intent(view.getContext(), WalletActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                        view.getContext().startActivity(intent);
                    }
                }
            });

        } else if(notificationData.getIcon().equalsIgnoreCase("new_offers")){
            background.setColor(resources.getColor(R.color.notification_purple));
            holder.notifyCircle.setBackground(background);
            holder.notificationBtn.setImageDrawable(resources.getDrawable(R.drawable.notification_offer_new));
            holder.notifyHeader.setText("New offers");
            holder.notifyDetail.setText(notificationData.getMessage());
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (!TextUtils.isEmpty(notificationData.getOutlet()) && !TextUtils.isEmpty(notificationData.getOffer())) {
                        Intent intent = new Intent(view.getContext(), OfferDetailActivity.class);
                        intent.putExtra(AppConstants.INTENT_PARAM_OUTLET_ID, notificationData.getOutlet());
                        intent.putExtra(AppConstants.INTENT_PARAM_OFFER_ID, notificationData.getOffer());
                        view.getContext().startActivity(intent);
                    }else {
                        Intent intent = new Intent(view.getContext(), WalletActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                        view.getContext().startActivity(intent);
                    }
                   /* if (!TextUtils.isEmpty(notificationData.getOutlet())) {
                        Intent intent = new Intent(view.getContext(), DiscoverActivity.class);
                        intent.setAction("setChildNo");
                        view.getContext().startActivity(intent);
                    }*/
                }
            });

        } else if(notificationData.getIcon().equalsIgnoreCase("offers_nearby")){
            background.setColor(resources.getColor(R.color.notification_blue));
            holder.notifyCircle.setBackground(background);
            holder.notificationBtn.setImageDrawable(resources.getDrawable(R.drawable.notification_offer_nearby));
            holder.notifyHeader.setText("Offers nearby");
            holder.notifyDetail.setText(notificationData.getMessage());
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

        } else if(notificationData.getIcon().equalsIgnoreCase("reactivate_user")){
            background.setColor(resources.getColor(R.color.notification_orange));
            holder.notifyCircle.setBackground(background);
            holder.notificationBtn.setImageDrawable(resources.getDrawable(R.drawable.notification_outlet_icon));
            holder.notifyHeader.setText("Reactivate user");
            holder.notifyDetail.setText(notificationData.getMessage());
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (!TextUtils.isEmpty(notificationData.getOutlet()) && !TextUtils.isEmpty(notificationData.getOffer())) {
                        Intent intent = new Intent(view.getContext(), OfferDetailActivity.class);
                        intent.putExtra(AppConstants.INTENT_PARAM_OUTLET_ID, notificationData.getOutlet());
                        intent.putExtra(AppConstants.INTENT_PARAM_OFFER_ID, notificationData.getOffer());
                        view.getContext().startActivity(intent);
                    }else {
                        Intent intent = new Intent(view.getContext(), DiscoverActivity.class);
                        intent.setAction("setChildNo");
                        view.getContext().startActivity(intent);
                    }
                }
            });

        } else if(notificationData.getIcon().equalsIgnoreCase("grab_offer")){
            background.setColor(resources.getColor(R.color.notification_yellow));
            holder.notifyCircle.setBackground(background);
            holder.notificationBtn.setImageDrawable(resources.getDrawable(R.drawable.notification_buck_icon));
            holder.notifyHeader.setText("Grab offer");
            holder.notifyDetail.setText(notificationData.getMessage());
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

        } else if(notificationData.getIcon().equalsIgnoreCase("new_features")){
            background.setColor(resources.getColor(R.color.notification_purple));
            holder.notifyCircle.setBackground(background);
            holder.notificationBtn.setImageDrawable(resources.getDrawable(R.drawable.notification_twyst_icon));
            holder.notifyHeader.setText("New features");
            holder.notifyDetail.setText(notificationData.getMessage());

        } else if(notificationData.getIcon().equalsIgnoreCase("coupon_grabbed")){
            background.setColor(resources.getColor(R.color.notification_light_gray));
            holder.notifyCircle.setBackground(background);
            holder.notificationBtn.setImageDrawable(resources.getDrawable(R.drawable.notification_buck_icon));
            holder.notifyHeader.setText("Coupon grabbed");
            holder.notifyDetail.setText(notificationData.getMessage());

        }  else if(notificationData.getIcon().equalsIgnoreCase("birthday")) {
            background.setColor(resources.getColor(R.color.notification_pink));
            holder.notifyCircle.setBackground(background);
            holder.notificationBtn.setImageDrawable(resources.getDrawable(R.drawable.notification_brthday_icon));
            holder.notifyHeader.setText("Birthday");
            holder.notifyDetail.setText(notificationData.getMessage());
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


        } else if(notificationData.getIcon().equalsIgnoreCase("offer_approved_and_live")){
            background.setColor(resources.getColor(R.color.notification_red));
            holder.notifyCircle.setBackground(background);
            holder.notificationBtn.setImageDrawable(resources.getDrawable(R.drawable.notification_offer_approve));
            holder.notifyHeader.setText("Offer approved and live");
            holder.notifyDetail.setText(notificationData.getMessage());
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

        }  else if(notificationData.getIcon().equalsIgnoreCase("outlet_specific_offer")){
            background.setColor(resources.getColor(R.color.notification_light_red));
            holder.notifyCircle.setBackground(background);
            holder.notificationBtn.setImageDrawable(resources.getDrawable(R.drawable.notification_specific_offer));
            holder.notifyHeader.setText("Outlet specific offer");
            holder.notifyDetail.setText(notificationData.getMessage());

        } else if(notificationData.getIcon().equalsIgnoreCase("submit_offer/report_problem on success")){
            background.setColor(resources.getColor(R.color.notification_light_red));
            holder.notifyCircle.setBackground(background);
            holder.notificationBtn.setImageDrawable(resources.getDrawable(R.drawable.notification_offer_icon));
            holder.notifyHeader.setText(notificationData.getIcon());
            holder.notifyDetail.setText(notificationData.getMessage());
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

        } else if(notificationData.getIcon().equalsIgnoreCase("winback")){
            background.setColor(resources.getColor(R.color.notification_orange));
            holder.notifyCircle.setBackground(background);
            holder.notificationBtn.setImageDrawable(resources.getDrawable(R.drawable.notification_voucher_winback));
            holder.notifyHeader.setText("winback");
            holder.notifyDetail.setText(notificationData.getMessage());
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

        }else if(notificationData.getIcon().equalsIgnoreCase("bill_rejected")){
            background.setColor(resources.getColor(R.color.notification_light_gray));
            holder.notifyCircle.setBackground(background);
            holder.notificationBtn.setImageDrawable(resources.getDrawable(R.drawable.notification_bill_reject));
            holder.notifyHeader.setText("Bill rejected");
            holder.notifyDetail.setText(notificationData.getMessage());
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

        }else if(notificationData.getIcon().equalsIgnoreCase("bill_approved")){
            background.setColor(resources.getColor(R.color.notification_orange));
            holder.notifyCircle.setBackground(background);
            holder.notificationBtn.setImageDrawable(resources.getDrawable(R.drawable.notification_bill_approved));
            holder.notifyHeader.setText("Bill approved");
            holder.notifyDetail.setText(notificationData.getMessage());
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

        } else if(notificationData.getIcon().equalsIgnoreCase("wallet")){
            background.setColor(resources.getColor(R.color.notification_red));
            holder.notifyCircle.setBackground(background);
            holder.notificationBtn.setImageDrawable(resources.getDrawable(R.drawable.notification_wallet_icon));
            holder.notifyHeader.setText("Wallet");
            holder.notifyDetail.setText(notificationData.getMessage());
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

        }



    }


    @Override
    public int getItemCount() {
        return items.size();
    }

    public static class NotificationViewHolder extends RecyclerView.ViewHolder{

        ImageView notificationBtn;
        RelativeLayout notifyCircle;
        LinearLayout notifyClick;
        TextView notifyHeader;
        TextView notifyDetail;

        public NotificationViewHolder(View itemView) {
            super(itemView);
            notifyCircle =(RelativeLayout)itemView.findViewById(R.id.notifyCircle);
            notificationBtn = (ImageView) itemView.findViewById(R.id.notificationBtn);
            notifyClick = (LinearLayout) itemView.findViewById(R.id.notifyClick);
            notifyHeader = (TextView) itemView.findViewById(R.id.notifyHeader);
            notifyDetail = (TextView) itemView.findViewById(R.id.notifyDetail);

        }
    }
}
