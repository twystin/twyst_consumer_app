package in.twyst.adapters;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import in.twyst.R;
import in.twyst.activities.DiscoverActivity;
import in.twyst.activities.OutletDetailsActivity;
import in.twyst.model.BaseResponse;
import in.twyst.model.Data;
import in.twyst.model.Offer;
import in.twyst.model.Outlet;
import in.twyst.service.HttpService;
import in.twyst.util.AppConstants;
import in.twyst.util.RoundedTransformation;
import in.twyst.util.TwystProgressHUD;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by satish on 06/06/15.
 */
public class DiscoverOutletAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<Outlet> items = new ArrayList<>();
    private OnViewHolderListener onViewHolderListener;

    private static final int VIEW_NORMAL = 0;
    private static final int VIEW_FOOTER = 1;

    private boolean outletsNotFound = false;

    public List<Outlet> getItems() {
        return items;
    }

    public void setItems(List<Outlet> items) {
        this.items = items;
    }

    public void setOnViewHolderListener(OnViewHolderListener onViewHolderListener) {
        this.onViewHolderListener = onViewHolderListener;
    }

    public interface OnViewHolderListener {
        void onRequestedLastItem();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        if (viewType == VIEW_NORMAL) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_outlet_card, parent, false);

            RecyclerView.LayoutParams layoutParams = new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            layoutParams.setMargins(10, 10, 10, -5);
            //layoutParams.height = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 170, actionBarActivity.getResources().getDisplayMetrics());
            v.setLayoutParams(layoutParams);

            OutletViewHolder vh = new OutletViewHolder(v);
            return vh;
        } else if (viewType == VIEW_FOOTER) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_discover_footer, parent, false);

            RecyclerView.LayoutParams layoutParams = new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            layoutParams.setMargins(0, 15, 0, 15);
            v.setLayoutParams(layoutParams);

            OutletViewHolderFooter vh = new OutletViewHolderFooter(v);
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
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {

        if (holder instanceof OutletViewHolder) {
            System.out.println("DiscoverOutletAdapter.onBindViewHolder data called... position: " + position);

            final OutletViewHolder outletViewHolder = (OutletViewHolder) holder;

            if (onViewHolderListener != null && position == getItemCount() - 5) {
                onViewHolderListener.onRequestedLastItem();
            }

            final Outlet outlet = items.get(position);
            View view = outletViewHolder.itemView;

            outletViewHolder.outletImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent= new Intent(view.getContext(), OutletDetailsActivity.class);
                    intent.putExtra(AppConstants.INTENT_PARAM_OUTLET_ID, outlet.get_id());
                    view.getContext().startActivity(intent);
                }
            });

            outletViewHolder.outletName.setText(outlet.getName());

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


            outletViewHolder.outletAddress.setText(address);

            if (TextUtils.isEmpty(outlet.getDistance())) {
                outletViewHolder.outletDistance.setText("");
                outletViewHolder.outletDistance.setVisibility(View.GONE);
            } else {
                outletViewHolder.outletDistance.setText(outlet.getDistance() + " km");
                outletViewHolder.outletDistance.setVisibility(View.VISIBLE);
            }

            List<Offer> subList;
            boolean hasMoreOffers = false;
            if (outlet.getOffers().size() > 5) {
                subList = outlet.getOffers().subList(0, 4);
                hasMoreOffers = true;
            } else {
                subList = outlet.getOffers();
                hasMoreOffers = false;
            }

            DiscoverOfferAdapter discoverOfferAdapter = new DiscoverOfferAdapter(outlet, hasMoreOffers, address);
            discoverOfferAdapter.setItems(subList);
            outletViewHolder.offerRecyclerView.setAdapter(discoverOfferAdapter);

            outletViewHolder.viewPager.setAdapter(new DiscoverOfferPagerAdapter(subList, outlet, hasMoreOffers));

            Picasso picasso = Picasso.with(view.getContext());
            picasso.setIndicatorsEnabled(AppConstants.DEGUG_PICASSO);
            picasso.setLoggingEnabled(AppConstants.DEGUG_PICASSO);
            picasso.load(outlet.getBackground())
                    .noFade()
                    .centerCrop()
                    .resize(250, 372)
                    .transform(new RoundedTransformation(24, 0))
                    .into(outletViewHolder.outletImage);

            if (outlet.isFollowing()) {
                outletViewHolder.followOutletBtn.setImageResource(R.drawable.icon_discover_follow_outlet);
            } else {
                outletViewHolder.followOutletBtn.setImageResource(R.drawable.icon_discover_follow_outlet_no);
            }
            SharedPreferences prefs = view.getContext().getSharedPreferences(AppConstants.PREFERENCE_SHARED_PREF_NAME, Context.MODE_PRIVATE);
            final String userToken = prefs.getString(AppConstants.PREFERENCE_USER_TOKEN, "");

            outletViewHolder.followOutletBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(final View view) {
                    final TwystProgressHUD twystProgressHUD = TwystProgressHUD.show(view.getContext(), false, null);
                    if (outlet.isFollowing()) {
                        outletViewHolder.followOutletBtn.setImageResource(R.drawable.icon_discover_follow_outlet_no);
                        HttpService.getInstance().unFollowEvent(userToken, outlet.get_id(), new Callback<BaseResponse<Data>>() {
                            @Override
                            public void success(BaseResponse<Data> dataBaseResponse, Response response) {
                                if (dataBaseResponse.isResponse()) {
                                    outlet.setFollowing(false);
                                    outletViewHolder.followOutletBtn.setImageResource(R.drawable.icon_discover_follow_outlet_no);
                                    twystProgressHUD.dismiss();
                                    Log.i("unfollow event response", "" + response);
                                } else {
                                    outletViewHolder.followOutletBtn.setImageResource(R.drawable.icon_discover_follow_outlet);
                                    Log.i("false response", "" + dataBaseResponse.getMessage());
                                }
                            }
                            @Override
                            public void failure(RetrofitError error) {
                                twystProgressHUD.dismiss();
                                DiscoverActivity discoverActivity = (DiscoverActivity) view.getContext();
                                discoverActivity.handleRetrofitError(error);
                            }
                        });


                    } else {
                        outletViewHolder.followOutletBtn.setImageResource(R.drawable.icon_discover_follow_outlet);
                        HttpService.getInstance().followEvent(userToken, outlet.get_id(), new Callback<BaseResponse<Data>>() {
                            @Override
                            public void success(BaseResponse<Data> dataBaseResponse, Response response) {
                                if (dataBaseResponse.isResponse()) {
                                    outletViewHolder.followOutletBtn.setImageResource(R.drawable.icon_discover_follow_outlet);
                                    outlet.setFollowing(true);
                                    twystProgressHUD.dismiss();
                                    Log.i("follow event response", "" + response);
                                } else {
                                    outletViewHolder.followOutletBtn.setImageResource(R.drawable.icon_discover_follow_outlet_no);
                                    Log.i("false response", "" + dataBaseResponse.getMessage());
                                }
                            }
                            @Override
                            public void failure(RetrofitError error) {
                                twystProgressHUD.dismiss();
                                DiscoverActivity discoverActivity = (DiscoverActivity) view.getContext();
                                discoverActivity.handleRetrofitError(error);
                            }
                        });

                    }
                }
            });

            outletViewHolder.callOutletBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(final View view) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());
                    builder.setTitle("Do you want to call "+outlet.getName() +"?")
                            .setPositiveButton("YES", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    String number = "tel:" + outlet.getPhone();
                                    view.getContext().startActivity(new Intent(Intent.ACTION_CALL, Uri.parse(number)));
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

        } else {

            System.out.println("DiscoverOutletAdapter.onBindViewHolder footer called... position: " + position + ", outletsNotFound: " + outletsNotFound);

            OutletViewHolderFooter outletViewHolderFooter = (OutletViewHolderFooter) holder;

            if (position > 0 && !outletsNotFound) {
                outletViewHolderFooter.itemView.findViewById(R.id.circularFooterProgressBar).setVisibility(View.VISIBLE);
            } else {
                outletViewHolderFooter.itemView.findViewById(R.id.circularFooterProgressBar).setVisibility(View.GONE);
            }
        }


    }

    @Override
    public int getItemCount() {
        return items.size() + 1;
    }

    public void setOutletsNotFound(boolean outletsNotFound) {
        this.outletsNotFound = outletsNotFound;
    }

    public static class OutletViewHolderFooter extends RecyclerView.ViewHolder {

        public OutletViewHolderFooter(View itemView) {
            super(itemView);
        }
    }

    public static class OutletViewHolder extends RecyclerView.ViewHolder {

        TextView outletName;
        TextView outletAddress;
        TextView outletDistance;

        ImageView outletImage;
        ImageView followOutletBtn;
        ImageView callOutletBtn;

        RecyclerView offerRecyclerView;
        ViewPager viewPager;

        public OutletViewHolder(View itemView) {
            super(itemView);

            outletName = (TextView) itemView.findViewById(R.id.outletName);
            outletAddress = (TextView) itemView.findViewById(R.id.outletAddress);
            outletDistance = (TextView) itemView.findViewById(R.id.distance);

            outletImage = (ImageView) itemView.findViewById(R.id.outletImage);
            followOutletBtn = (ImageView) itemView.findViewById(R.id.followOutletBtn);
            callOutletBtn = (ImageView) itemView.findViewById(R.id.callOutletBtn);

            offerRecyclerView = (RecyclerView) itemView.findViewById(R.id.offerRecyclerView);
            offerRecyclerView.setHasFixedSize(true);

            LinearLayoutManager mLayoutManager = new LinearLayoutManager(itemView.getContext());
            mLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
            offerRecyclerView.setLayoutManager(mLayoutManager);

            viewPager = (ViewPager) itemView.findViewById(R.id.pager);

        }
    }
}
