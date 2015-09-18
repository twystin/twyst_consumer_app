package in.twyst.adapters;

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

import in.twyst.R;
import in.twyst.activities.OfferDetailActivity;
import in.twyst.activities.OutletDetailsActivity;
import in.twyst.activities.SubmitOfferActivity;
import in.twyst.model.Offer;
import in.twyst.model.Outlet;
import in.twyst.util.AppConstants;

import static android.support.v7.widget.RecyclerView.*;

/**
 * Created by satish on 06/06/15.
 */
public class DiscoverOfferAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private List<Offer> items = new ArrayList<>();
    private static final int VIEW_NORMAL = 0;
    private static final int VIEW_FOOTER = 1;
    private Outlet outlet;
    private boolean hasMoreOffers;
    private String address;

    public DiscoverOfferAdapter(Outlet outlet, boolean hasMoreOffers, String address) {
        this.outlet = outlet;
        this.hasMoreOffers = hasMoreOffers;
        this.address = address;
    }

    public List<Offer> getItems() {
        return items;
    }

    public void setItems(List<Offer> items) {
        this.items = items;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == VIEW_NORMAL) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_offer, parent, false);

            OfferViewHolder vh = new OfferViewHolder(v);
            return vh;
        } else if (viewType == VIEW_FOOTER) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_footer_offer, parent, false);

            FooterViewHolder vh = new FooterViewHolder(v);
            return vh;
        }

        throw new RuntimeException("there is no type that matches the type " + viewType + " + make sure your using types correctly");

    }

    @Override
    public int getItemViewType(int position) {
        if (position == items.size()) {
            return VIEW_FOOTER;
        } else {
            return VIEW_NORMAL;
        }
    }

    @Override
    public void onBindViewHolder(ViewHolder holder1, final int position) {

        if (holder1 instanceof OfferViewHolder) {
            final OfferViewHolder holder = (OfferViewHolder) holder1;
            final Offer offer = items.get(position);
            final View view = holder.itemView;


            holder.text1.setText(offer.getHeader());

            if (TextUtils.isEmpty(offer.getLine1())) {
                holder.text2.setVisibility(View.GONE);
            } else {
                holder.text2.setVisibility(View.VISIBLE);
                holder.text2.setText(offer.getLine1());
            }

            if (TextUtils.isEmpty(offer.getLine2())) {
                holder.text3.setVisibility(View.GONE);
            } else {
                holder.text3.setVisibility(View.VISIBLE);
                holder.text3.setText(offer.getLine2());
            }


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
                                if(days == 0){
                                    lapseText = offer.getAvailableNext().getDay()+", "+offer.getAvailableNext().getTime();
                                }else if (days == 1){
                                    lapseText = offer.getAvailableNext().getDay()+", "+offer.getAvailableNext().getTime();
                                }else if(days > 7){
                                    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("d-MMM");
                                    lapseText = "next available " + simpleDateFormat.format(lapseDate).toLowerCase()+", "+offer.getAvailableNext().getTime();
                                }else {
                                    lapseText = offer.getAvailableNext().getDay()+", "+offer.getAvailableNext().getTime();
                                }

                            }else {
                                lapseText = null;
                            }
                        }

                    } catch (ParseException e) {
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
                                if(days == 0){
                                    expiryText = offer.getAvailableNext().getDay()+", "+offer.getAvailableNext().getTime();
                                }else if (days == 1){
                                    expiryText = offer.getAvailableNext().getDay()+", "+offer.getAvailableNext().getTime();
                                }else if(days > 7){
                                    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("d-MMM");
                                    expiryText = "next available " + simpleDateFormat.format(expiryDate).toLowerCase()+", "+offer.getAvailableNext().getTime();
                                }else {
                                    expiryText = offer.getAvailableNext().getDay()+", "+offer.getAvailableNext().getTime();
                                }
                            } else {
                                expiryText = null;
                            }
                        }

                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                }
            }

            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(view.getContext(), OfferDetailActivity.class);
                    intent.putExtra(AppConstants.INTENT_PARAM_OUTLET_OBJECT, outlet);
                    intent.putExtra(AppConstants.INTENT_PARAM_OFFER_OBJECT, offer);
                    view.getContext().startActivity(intent);
                }
            });

            holder.footerImageView.setVisibility(View.VISIBLE);

            Resources resources = view.getContext().getResources();
            if ("checkin".equalsIgnoreCase(offer.getType())) {
                view.setBackgroundResource(R.drawable.card_gray);

                holder.text1.setTextColor(resources.getColor(R.color.outlet_txt_color_grey));
                holder.text2.setTextColor(resources.getColor(R.color.outlet_txt_color_grey));
                holder.text3.setTextColor(resources.getColor(R.color.outlet_txt_color_grey));
                holder.footer.setBackgroundResource(R.color.button_disabled);
                holder.footer.setEnabled(false);
                holder.footerImageView.setImageDrawable(resources.getDrawable(R.drawable.icon_discover_offer_checkin_locked));

                if (offer.getNext() == 1) {
                    holder.footerText.setText(offer.getNext() + " check-in to go");
                } else {
                    holder.footerText.setText(offer.getNext() + " check-ins to go");
                }

                holder.time.setText(expiryText);

            } else if ("coupon".equalsIgnoreCase(offer.getType())) {
                holder.text1.setTextColor(resources.getColor(R.color.offer_color_red));
                holder.text2.setTextColor(resources.getColor(R.color.offer_color_red));
                holder.text3.setTextColor(resources.getColor(R.color.offer_color_red));
                holder.time.setText(lapseText);

                if (offer.isAvailableNow()) {
                    view.setBackgroundResource(R.drawable.card_red);


                    holder.footerText.setText("use offer");
                    holder.footerImageView.setImageDrawable(resources.getDrawable(R.drawable.icon_discover_offer_coupon_wallet));
                } else {
                    view.setBackgroundResource(R.drawable.card_gray);
                    holder.footerText.setText("remind me");
                    holder.footerImageView.setImageDrawable(resources.getDrawable(R.drawable.icon_discover_offer_clock_white));
                }


            } else if ("pool".equalsIgnoreCase(offer.getType())) {
                view.setBackgroundResource(R.drawable.card_yellow);
                holder.time.setText(expiryText);
                holder.text1.setTextColor(resources.getColor(R.color.offer_color_yellow));
                holder.text2.setTextColor(resources.getColor(R.color.offer_color_yellow));
                holder.text3.setTextColor(resources.getColor(R.color.offer_color_yellow));

                holder.footerText.setText("grab offer");
                holder.footerImageView.setVisibility(View.GONE);
                holder.buckTextImage.setVisibility(View.VISIBLE);
                holder.buckTextImage.setText("");//TODO offer.getcouponcost
                holder.detailLayout.setVisibility(View.VISIBLE);
                holder.detailIcon.setBackground(resources.getDrawable(R.drawable.icon_discover_offer_socialpool_grey));
                holder.detailText.setText(""); //TODO offer.getcouponsource(user)

            } else if ("offer".equalsIgnoreCase(offer.getType())) {
                holder.text1.setTextColor(resources.getColor(R.color.offer_color_green));
                holder.text2.setTextColor(resources.getColor(R.color.offer_color_green));
                holder.text3.setTextColor(resources.getColor(R.color.offer_color_green));
                holder.time.setText(expiryText);
                if (offer.isAvailableNow()) {
                    view.setBackgroundResource(R.drawable.card_green);

                    holder.footerText.setText("use offer");
                    if(offer.getOfferCost()>0) {
                        holder.buckTextImage.setVisibility(View.VISIBLE);
                        holder.buckTextImage.setText(String.valueOf(offer.getOfferCost()));
                    }else {
                        holder.buckTextImage.setVisibility(View.GONE);
                    }
                    holder.footerImageView.setVisibility(View.GONE);
                } else {
                    view.setBackgroundResource(R.drawable.card_gray);
                    holder.footerText.setText("remind me");
                    holder.footerImageView.setImageDrawable(resources.getDrawable(R.drawable.icon_discover_offer_clock_white));
                }

            } else if ("deal".equalsIgnoreCase(offer.getType())) {

                holder.text1.setTextColor(resources.getColor(R.color.offer_color_green));
                holder.text2.setTextColor(resources.getColor(R.color.offer_color_green));
                holder.text3.setTextColor(resources.getColor(R.color.offer_color_green));
                holder.time.setText(expiryText);
                if (offer.isAvailableNow()) {

                    view.setBackgroundResource(R.drawable.card_green);
                    holder.footerImageView.setVisibility(View.GONE);
                    holder.footerText.setText("use offer");
                    if(offer.getOfferCost()>0) {
                        holder.buckTextImage.setVisibility(View.VISIBLE);
                        holder.buckTextImage.setText(String.valueOf(offer.getOfferCost()));
                    }else {
                        holder.buckTextImage.setVisibility(View.GONE);
                    }

                } else {
                    view.setBackgroundResource(R.drawable.card_gray);
                    holder.footerText.setText("remind me");
                    holder.footerImageView.setImageDrawable(resources.getDrawable(R.drawable.icon_discover_offer_clock_white));
                }

            }else if("bank_deal".equalsIgnoreCase(offer.getType())){
                view.setBackgroundResource(R.drawable.card_blue);
                holder.time.setText(expiryText);
                holder.text1.setTextColor(resources.getColor(R.color.offer_color_blue));
                holder.text2.setTextColor(resources.getColor(R.color.offer_color_blue));
                holder.text3.setTextColor(resources.getColor(R.color.offer_color_blue));
                holder.footerImageView.setVisibility(View.GONE);
                holder.detailLayout.setVisibility(View.GONE);
                holder.detailIcon.setBackground(resources.getDrawable(R.drawable.icon_discover_offer_bank_creditcard_grey));
                holder.detailText.setText(""); //TODO offer.getbankdetail()

                if(offer.getOfferCost()>0){
                    holder.buckTextImage.setVisibility(View.VISIBLE);
                    holder.buckTextImage.setText(String.valueOf(offer.getOfferCost()));
                }else {
                    holder.buckTextImage.setVisibility(View.GONE);
                }
                holder.footerText.setText("use offer");
            }


            if ("flatoff".equalsIgnoreCase(offer.getMeta().getRewardType())) {

                holder.text1.setTextSize(TypedValue.COMPLEX_UNIT_SP, 25);

            } else if ("reducedprice".equalsIgnoreCase(offer.getMeta().getRewardType())) {


                holder.text1.setTextSize(TypedValue.COMPLEX_UNIT_SP, 25);
            } else if ("buffet".equalsIgnoreCase(offer.getMeta().getRewardType())) {

                holder.text1.setTextSize(TypedValue.COMPLEX_UNIT_SP, 32);

            } else if ("custom".equalsIgnoreCase(offer.getMeta().getRewardType())) {

                holder.text1.setTextSize(TypedValue.COMPLEX_UNIT_SP, 20);

            } else if ("onlyhappyhours".equalsIgnoreCase(offer.getMeta().getRewardType())) {

                holder.text1.setTextSize(TypedValue.COMPLEX_UNIT_SP, 25);
            } else {   //buy_one_get_one,free,discount,happyhours,unlimited,combo

                holder.text1.setTextSize(TypedValue.COMPLEX_UNIT_SP, 35);
            }

        } else {
            final FooterViewHolder holder = (FooterViewHolder) holder1;
            Log.i("position", "" + position + " " + holder.getPosition() + " " + holder.getOldPosition());

            if (hasMoreOffers) {
                holder.footerText.setText("view more");
                holder.emptyLayout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(v.getContext(), OutletDetailsActivity.class);
                        intent.putExtra(AppConstants.INTENT_PARAM_OUTLET_ID, outlet.get_id());
                        v.getContext().startActivity(intent);
                    }
                });

            } else {
                holder.footerText.setText("submit offer");
                holder.footerText.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Log.i("outletname", outlet.getName());
                        Intent intent = new Intent(v.getContext(), SubmitOfferActivity.class);
                        intent.putExtra(AppConstants.INTENT_PARAM_SUMIT_OFFER_OUTLET_NAME,outlet.getName());
                        intent.putExtra(AppConstants.INTENT_PARAM_SUMIT_OFFER_OUTLET_ADDRESS,outlet.getAddress());
                        v.getContext().startActivity(intent);

                    }
                });

            }
        }


    }

    @Override
    public int getItemCount() {
        return items.size() + 1;
    }

    public static class OfferViewHolder extends ViewHolder {
        ImageView footerImageView;
        TextView footerText;
        TextView text1;
        TextView text2;
        TextView text3;
        TextView time;
        TextView buckTextImage;
        LinearLayout detailLayout;
        ImageView detailIcon;
        TextView detailText;
        RelativeLayout footer;

        public OfferViewHolder(View itemView) {
            super(itemView);

            text1 = (TextView) itemView.findViewById(R.id.text1);
            text2 = (TextView) itemView.findViewById(R.id.text2);
            text3 = (TextView) itemView.findViewById(R.id.text3);
            time = (TextView) itemView.findViewById(R.id.availabilityText);
            footerText = (TextView) itemView.findViewById(R.id.footerText);
            footerImageView = (ImageView) itemView.findViewById(R.id.footerImageView);
            buckTextImage = (TextView) itemView.findViewById(R.id.buckTextImage);
            detailLayout = (LinearLayout) itemView.findViewById(R.id.detailLayout);
            detailIcon = (ImageView) itemView.findViewById(R.id.detailIcon);
            detailText = (TextView) itemView.findViewById(R.id.detailText);
            footer = (RelativeLayout) itemView.findViewById(R.id.footerLayout);

            text1.setText("");
            text2.setText("");
            text3.setText("");
            footerText.setText("");
            time.setText("");
            detailText.setText("");


        }
    }

    public static class FooterViewHolder extends ViewHolder {

        TextView footerText;
        RelativeLayout emptyLayout;

        public FooterViewHolder(View view) {
            super(view);
            footerText = (TextView) view.findViewById(R.id.footerText);
            emptyLayout = (RelativeLayout) view.findViewById(R.id.emptyLayout);
        }
    }
}
