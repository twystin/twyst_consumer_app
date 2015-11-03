package com.twyst.app.android.activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.Transformation;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.appsflyer.AppsFlyerLib;
import com.getbase.floatingactionbutton.FloatingActionButton;
import com.getbase.floatingactionbutton.FloatingActionsMenu;
import com.github.ksoichiro.android.observablescrollview.ObservableRecyclerView;
import com.github.ksoichiro.android.observablescrollview.ObservableScrollViewCallbacks;
import com.github.ksoichiro.android.observablescrollview.ScrollState;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;

import com.twyst.app.android.R;
import com.twyst.app.android.adapters.WalletAdapter;
import com.twyst.app.android.model.BaseResponse;
import com.twyst.app.android.model.Outlet;
import com.twyst.app.android.model.WalletData;
import com.twyst.app.android.service.HttpService;
import com.twyst.app.android.util.AppConstants;
import com.twyst.app.android.util.Utils;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by rahuls on 10/7/15.
 */
public class WalletActivity extends BaseActivity implements ObservableScrollViewCallbacks {

    private WalletAdapter walletAdapter;
    private Spinner sortSpinner;
    private Calendar calendar;
    private FloatingActionButton submitFab;
    private FloatingActionButton checkinFab;
    private FloatingActionsMenu fabMenu;
    private RelativeLayout obstructor;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private ObservableRecyclerView recyclerView;
    private RelativeLayout hideSpinner;
    private boolean fromDrawer;
    final public static int REQ_CODE = 1;

    @Override
    protected String getTagName() {
        return this.getClass().getSimpleName();
    }

    @Override
    protected int getLayoutResource() {
        return R.layout.activity_wallet;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setupAsChild = true;
        super.onCreate(savedInstanceState);
        fromDrawer = getIntent().getBooleanExtra(AppConstants.INTENT_PARAM_FROM_DRAWER, false);

        recyclerView = (ObservableRecyclerView) findViewById(R.id.walletRecycleView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setScrollViewCallbacks(this);

        LinearLayoutManager mLayoutManager = new LinearLayoutManager(WalletActivity.this, LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(mLayoutManager);

        mSwipeRefreshLayout = (SwipeRefreshLayout)findViewById(R.id.swipeRefreshLayout);

        mSwipeRefreshLayout.setColorSchemeResources(R.color.button_orange);

        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                // Refresh items
                fetchCoupons();
            }
        });

        findViewById(R.id.earnMoreBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showEarnMoreInstructions();
            }
        });
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        fromDrawer = intent.getBooleanExtra(AppConstants.INTENT_PARAM_FROM_DRAWER, false);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQ_CODE) {
            if (resultCode == RESULT_OK) {
                if (walletAdapter!=null){
                    walletAdapter.notifyDataSetChanged();
                }
                fetchCoupons();
            }
        }
    }

    private void fetchCoupons() {
        SharedPreferences prefs = getSharedPreferences(AppConstants.PREFERENCE_SHARED_PREF_NAME, Context.MODE_PRIVATE);
        String lat = prefs.getString(AppConstants.PREFERENCE_CURRENT_USED_LAT, "");
        String lng = prefs.getString(AppConstants.PREFERENCE_CURRENT_USED_LNG, "");

        if (TextUtils.isEmpty(lat) || TextUtils.isEmpty(lng)){
            lat = prefs.getString(AppConstants.PREFERENCE_UPDATED_LAT, "");
            lng = prefs.getString(AppConstants.PREFERENCE_UPDATED_LNG, "");
        }

        HttpService.getInstance().getCoupons(getUserToken(),lat,lng, new Callback<BaseResponse<WalletData>>() {
            @Override
            public void success(BaseResponse<WalletData> arrayListBaseResponse, Response response) {
                if (arrayListBaseResponse.isResponse()) {

                    final ArrayList<Outlet> outlets = arrayListBaseResponse.getData().getOutlets();
                    String twystBucks = arrayListBaseResponse.getData().getTwystBucks();

                    if (TextUtils.isEmpty(twystBucks)) {
                        twystBucks = "0";
                    } else {
                        sharedPreferences = getSharedPreferences(AppConstants.PREFERENCE_SHARED_PREF_NAME, Context.MODE_PRIVATE).edit();
                        sharedPreferences.putInt(AppConstants.PREFERENCE_LAST_TWYST_BUCK, Integer.parseInt(twystBucks));
                        sharedPreferences.commit();
                    }



                    fabMenu = (FloatingActionsMenu) findViewById(R.id.fabMenu);
                    obstructor = (RelativeLayout) findViewById(R.id.obstructor);
                    submitFab = (FloatingActionButton) findViewById(R.id.submitFab);
                    checkinFab = (FloatingActionButton) findViewById(R.id.checkinFab);
                    TextView buckText = (TextView) findViewById(R.id.buckText);

                    hideProgressHUDInLayout();
                    onItemsLoadComplete();
                    fabMenu.setVisibility(View.VISIBLE);

                    fabMenu.setOnFloatingActionsMenuUpdateListener(new FloatingActionsMenu.OnFloatingActionsMenuUpdateListener() {
                        @Override
                        public void onMenuExpanded() {
                            if (obstructor.getVisibility() == View.INVISIBLE) {
                                obstructor.setVisibility(View.VISIBLE);
                            }

                        }

                        @Override
                        public void onMenuCollapsed() {
                            if (obstructor.getVisibility() == View.VISIBLE) {
                                obstructor.setVisibility(View.INVISIBLE);
                            }
                        }
                    });

                    obstructor.setOnTouchListener(new View.OnTouchListener() {
                        @Override
                        public boolean onTouch(View v, MotionEvent event) {
                            Log.d(getTagName(), "fab menu obstructor clicked .... ");

                            if (obstructor.getVisibility() == View.VISIBLE) {
                                fabMenu.collapse();
                                return true;
                            }

                            return false;
                        }
                    });

                    checkinFab.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            fabMenu.collapse();
                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    //IntentIntegrator integrator = new IntentIntegrator(DiscoverActivity.this);
                                    //integrator.initiateScan(IntentIntegrator.QR_CODE_TYPES);
                                    Intent intent = new Intent(WalletActivity.this, ScannerActivity.class);
                                    startActivity(intent);
                                }
                            }, 100);
                        }
                    });

                    submitFab.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (obstructor.getVisibility() == View.VISIBLE) {
                                Intent intent = new Intent(WalletActivity.this, SubmitOfferActivity.class);
                                intent.setAction("setChildYes");
                                startActivity(intent);
                                fabMenu.collapse();

                            }
                        }
                    });

                    final View checkinObstructor = findViewById(R.id.checkinObstructor);
                    final View checkinObstructor2 = findViewById(R.id.checkinObstructor2);

                    checkinObstructor.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            checkinObstructor.setVisibility(View.GONE);
                            checkinObstructor2.setVisibility(View.GONE);
                        }
                    });

                    checkinFab.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            fabMenu.collapse();
                            checkinObstructor.setVisibility(View.VISIBLE);
                            checkinObstructor2.setVisibility(View.VISIBLE);
                        }
                    });

                    findViewById(R.id.scanBtn).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            checkinObstructor.setVisibility(View.GONE);
                            checkinObstructor2.setVisibility(View.GONE);

                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    //IntentIntegrator integrator = new IntentIntegrator(DiscoverActivity.this);
                                    //integrator.initiateScan(IntentIntegrator.QR_CODE_TYPES);
                                    Intent intent = new Intent(WalletActivity.this, ScannerActivity.class);
                                    startActivity(intent);
                                }
                            }, 100);
                        }
                    });

                    findViewById(R.id.uploadBillBtn).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            checkinObstructor.setVisibility(View.GONE);
                            checkinObstructor2.setVisibility(View.GONE);

                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {

                                    Intent intent = new Intent(WalletActivity.this, UploadBillActivity.class);
                                    intent.setAction("setChildYes");
                                    startActivity(intent);
                                }
                            }, 100);
                        }
                    });


                    final LinearLayout twystBucksLayout = (LinearLayout) findViewById(R.id.twystBucksLayout);
                    hideSpinner = (RelativeLayout)findViewById(R.id.hideSpinner);
                    twystBucksLayout.setVisibility(View.VISIBLE);
                    buckText.setText(twystBucks);

                    if(outlets.size()>0){

                        hideSpinner.setVisibility(View.VISIBLE);
                        walletAdapter = new WalletAdapter();
                        walletAdapter.setItems(outlets);

                        recyclerView.setAdapter(walletAdapter);

                        sortSpinner = (Spinner) findViewById(R.id.spinner1);
                        final String[] sortList = {"Ending Soonest", "Distance", "Available Now", "Most Recent"};

                        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(WalletActivity.this, R.layout.custom_spinner, sortList);
                        dataAdapter.setDropDownViewResource(R.layout.popup_spinner_view);
                        sortSpinner.setAdapter(dataAdapter);


                        sortSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                                                                  @Override
                                                                  public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                                                                      if (position == 0) {

                                                                          Collections.sort(walletAdapter.getItems(), new Comparator<Outlet>() {
                                                                              @Override
                                                                              public int compare(Outlet lhs, Outlet rhs) {
                                                                                  SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
                                                                                  Date expiryDate1 = null;
                                                                                  try {
                                                                                      Log.i(getTagName(), "lhs.getOffer" + lhs.getOffers());
                                                                                      Log.d(getClass().getSimpleName(), "expiry " + lhs.getOffers().get(0).getExpiry());
                                                                                      expiryDate1 = sdf.parse(lhs.getOffers().get(0).getExpiry());
                                                                                  } catch (ParseException e) {
                                                                                      e.printStackTrace();
                                                                                  }
                                                                                  Date expiryDate2 = null;
                                                                                  try {
                                                                                      expiryDate2 = sdf.parse(rhs.getOffers().get(0).getExpiry());
                                                                                  } catch (ParseException e) {
                                                                                      e.printStackTrace();
                                                                                  }
                                                                                  return expiryDate1.compareTo(expiryDate2);
                                                                              }
                                                                          });
                                                                          walletAdapter.notifyDataSetChanged();

                                                                      } else if (position == 1) {
                                                                          Collections.sort(walletAdapter.getItems(), new Comparator<Outlet>() {
                                                                              @Override
                                                                              public int compare(Outlet lhs, Outlet rhs) {
                                                                                  if (!TextUtils.isEmpty(lhs.getDistance()) && !TextUtils.isEmpty(rhs.getDistance())) {
                                                                                      return Double.valueOf(lhs.getDistance()).compareTo(Double.valueOf(rhs.getDistance()));
                                                                                  } else {
                                                                                      return 0;
                                                                                  }

                                                                              }
                                                                          });
                                                                          walletAdapter.notifyDataSetChanged();


                                                                      } else if (position == 2) {

                                                                          ArrayList<Outlet> availableNowList = new ArrayList<>();
                                                                          ArrayList<Outlet> notAvailableList = new ArrayList<>();
                                                                          //sort both list
                                                                          Utils utils = new Utils();
                                                                          calendar = Calendar.getInstance();
                                                                          int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);
                                                                          for (Outlet outlet : walletAdapter.getItems()) {
                                                                              if (outlet.getOffers().get(0).isAvailableNow()) {
                                                                                  availableNowList.add(outlet);
                                                                                  if (!TextUtils.isEmpty(outlet.getDistance())) {
                                                                                      Collections.sort(availableNowList, new Comparator<Outlet>() {
                                                                                          @Override
                                                                                          public int compare(Outlet lhs, Outlet rhs) {
                                                                                              return Double.valueOf(lhs.getDistance()).compareTo(Double.valueOf(rhs.getDistance()));
                                                                                          }
                                                                                      });
                                                                                  }


                                                                              }
                                                                              if (!outlet.getOffers().get(0).isAvailableNow()) {

                                                                                  if (outlet.getOffers().get(0).getAvailableNext() != null) {

                                                                                      if (outlet.getOffers().get(0).getAvailableNext().getDay().equalsIgnoreCase("SUN")) {
                                                                                          calendar = utils.nextDayOfWeek(dayOfWeek);
                                                                                      } else if (outlet.getOffers().get(0).getAvailableNext().getDay().equalsIgnoreCase("MON")) {
                                                                                          calendar = utils.nextDayOfWeek(dayOfWeek);
                                                                                      } else if (outlet.getOffers().get(0).getAvailableNext().getDay().equalsIgnoreCase("TUE")) {
                                                                                          calendar = utils.nextDayOfWeek(dayOfWeek);
                                                                                      } else if (outlet.getOffers().get(0).getAvailableNext().getDay().equalsIgnoreCase("WED")) {
                                                                                          calendar = utils.nextDayOfWeek(dayOfWeek);
                                                                                      } else if (outlet.getOffers().get(0).getAvailableNext().getDay().equalsIgnoreCase("THU")) {
                                                                                          calendar = utils.nextDayOfWeek(dayOfWeek);
                                                                                      } else if (outlet.getOffers().get(0).getAvailableNext().getDay().equalsIgnoreCase("FRI")) {
                                                                                          calendar = utils.nextDayOfWeek(dayOfWeek);
                                                                                      } else if (outlet.getOffers().get(0).getAvailableNext().getDay().equalsIgnoreCase("SAT")) {
                                                                                          calendar = utils.nextDayOfWeek(dayOfWeek);
                                                                                      }

                                                                                      notAvailableList.add(outlet);

                                                                                      Collections.sort(notAvailableList, new Comparator<Outlet>() {
                                                                                          @Override
                                                                                          public int compare(Outlet lhs, Outlet rhs) {
                                                                                              Calendar date = Calendar.getInstance();
                                                                                              return date.compareTo(calendar);
                                                                                          }
                                                                                      });

                                                                                  }
                                                                              }
                                                                          }

                                                                          availableNowList.addAll(notAvailableList);

                                                                          walletAdapter.setItems(availableNowList);
                                                                          walletAdapter.notifyDataSetChanged();

                                                                      } else if (position == 3) {

                                                                          Collections.sort(walletAdapter.getItems(), new Comparator<Outlet>() {
                                                                              @Override
                                                                              public int compare(Outlet lhs, Outlet rhs) {
                                                                                  SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
                                                                                  Date issueDate1 = null;
                                                                                  try {
                                                                                      issueDate1 = sdf.parse(lhs.getOffers().get(0).getIssuedDate());
                                                                                  } catch (ParseException e) {
                                                                                      e.printStackTrace();
                                                                                  }
                                                                                  Date issueDate2 = null;
                                                                                  try {
                                                                                      issueDate2 = sdf.parse(rhs.getOffers().get(0).getIssuedDate());
                                                                                  } catch (ParseException e) {
                                                                                      e.printStackTrace();
                                                                                  }
                                                                                  return issueDate2.compareTo(issueDate1);
                                                                              }
                                                                          });
                                                                          walletAdapter.notifyDataSetChanged();
                                                                      }
                                                                  }

                                                                  @Override
                                                                  public void onNothingSelected(AdapterView<?> parent) {

                                                                  }
                                                              }

                        );

                        findViewById(R.id.blankDataLayout).setVisibility(View.GONE);

                        }else{
                        hideSpinner.setVisibility(View.GONE);
                        findViewById(R.id.ivNoData).setBackgroundResource(R.drawable.no_voucher);
                        ((TextView) findViewById(R.id.tvNoData)).setText("You have no active vouchers!");
                        findViewById(R.id.blankDataLayout).setVisibility(View.VISIBLE);
//                             Toast.makeText(WalletActivity.this, arrayListBaseResponse.getMessage(), Toast.LENGTH_SHORT).show();

                        }

                    } else {
                        Toast.makeText(WalletActivity.this, arrayListBaseResponse.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void failure (RetrofitError error){
                    hideProgressHUDInLayout();
                    onItemsLoadComplete();
                    if (error.getKind() == RetrofitError.Kind.NETWORK) {
                        handleRetrofitError(error);
                    } else {
                        buildAndShowSnackbarWithMessage("Unable to load coupons!");
                    }
                }
            }

            );

        }


    void onItemsLoadComplete() {

        // Stop refresh animation
        mSwipeRefreshLayout.setRefreshing(false);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (walletAdapter!=null){
            walletAdapter.notifyDataSetChanged();
        }
//        if(!firstLoad) {
            fetchCoupons();
//        }else {
//            firstLoad = false;
//        }
        AppsFlyerLib.onActivityResume(this);
    }

    @Override
    public void onPause() {
        super.onPause();
        AppsFlyerLib.onActivityPause(this);
    }

    @Override
    public void onScrollChanged(int scrollY, boolean firstScroll, boolean dragging) {
    }

    @Override
    public void onDownMotionEvent() {
    }

    @Override
    public void onUpOrCancelMotionEvent(ScrollState scrollState) {
        if (scrollState == ScrollState.UP) {
            if (detailsIsShown()) {
                hideDetails();
            }
        } else if (scrollState == ScrollState.DOWN) {
            if (detailsIsHidden()) {
                showDetails();
            }
        }
    }

    private boolean detailsIsShown() {
        View hideableLayout = findViewById(R.id.hideableLayout);
        return (hideableLayout.getVisibility() == View.VISIBLE && hideableLayout.getAlpha() == 1);
    }

    private boolean detailsIsHidden() {
        View hideableLayout = findViewById(R.id.hideableLayout);
        return (hideableLayout.getVisibility() == View.INVISIBLE || hideableLayout.getVisibility() == View.GONE || hideableLayout.getAlpha() == 0 || hideableLayout.getLayoutParams().height == 0);
    }

    private void hideDetails() {

        final View divider = findViewById(R.id.divider);
        final View hideableLayout = findViewById(R.id.hideableLayout);
        final int initialHeight = hideableLayout.getMeasuredHeight();

        Animation a = new Animation() {
            @Override
            protected void applyTransformation(float interpolatedTime, Transformation t) {
                if(interpolatedTime == 1){
                    hideableLayout.setVisibility(View.GONE);
                    //hideableLayout.getLayoutParams().height = 0;
                    //hideableLayout.requestLayout();

                    hideableLayout.setAlpha(0f);
                    divider.setAlpha(0f);
                }else{
                    hideableLayout.getLayoutParams().height = initialHeight - (int)(initialHeight * interpolatedTime);
                    hideableLayout.setAlpha(1f - interpolatedTime);
                    hideableLayout.requestLayout();

                    divider.setAlpha(1f - interpolatedTime);
                }
            }

            @Override
            public boolean willChangeBounds() {
                return true;
            }
        };

        // 1dp/ms
        //a.setDuration((int)(initialHeight / layout.getContext().getResources().getDisplayMetrics().density));
        a.setDuration(((int)(initialHeight / hideableLayout.getContext().getResources().getDisplayMetrics().density)) * 4);
        hideableLayout.startAnimation(a);

    }


    private void showDetails() {
        final View divider = findViewById(R.id.divider);
        final View hideableLayout = findViewById(R.id.hideableLayout);

        hideableLayout.measure(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        final int targetHeight = hideableLayout.getMeasuredHeight();


        // Older versions of android (pre API 21) cancel animations for views with a height of 0.
        hideableLayout.getLayoutParams().height = 1;
        hideableLayout.setVisibility(View.VISIBLE);
        Animation a = new Animation() {
            @Override
            protected void applyTransformation(float interpolatedTime, Transformation t) {
                hideableLayout.getLayoutParams().height = interpolatedTime == 1
                        ? LinearLayout.LayoutParams.WRAP_CONTENT
                        : (int)(targetHeight * interpolatedTime);
                hideableLayout.setAlpha(0f + interpolatedTime);
                hideableLayout.requestLayout();
                divider.setAlpha(0f + interpolatedTime);
            }

            @Override
            public boolean willChangeBounds() {
                return true;
            }
        };

        // 1dp/ms
        //a.setDuration((int)(targetHeight / hideableLayout.getContext().getResources().getDisplayMetrics().density));
        a.setDuration(((int)(targetHeight / hideableLayout.getContext().getResources().getDisplayMetrics().density)) * 4);
        hideableLayout.startAnimation(a);
    }

    @Override
    public void onBackPressed() {
        if (drawerOpened) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            if (fromDrawer) {
                //clear history and go to discover
                Intent intent = new Intent(getBaseContext(), DiscoverActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            } else {
                super.onBackPressed();
            }
        }
    }

}
