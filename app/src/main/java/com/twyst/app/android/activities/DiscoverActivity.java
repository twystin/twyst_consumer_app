package com.twyst.app.android.activities;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.appsflyer.AppsFlyerLib;
import com.getbase.floatingactionbutton.FloatingActionButton;
import com.getbase.floatingactionbutton.FloatingActionsMenu;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.twyst.app.android.R;
import com.twyst.app.android.adapters.DiscoverOutletAdapter;
import com.twyst.app.android.model.LocationData;
import com.twyst.app.android.util.AppConstants;

import java.util.ArrayList;
import java.util.Date;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Handler;
import android.support.v4.view.GravityCompat;
import android.text.Html;
import android.text.format.DateFormat;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.Transformation;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.SeekBar;

import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import com.google.gson.Gson;
import com.twyst.app.android.model.BaseResponse;
import com.twyst.app.android.model.DiscoverData;
import com.twyst.app.android.model.Friend;
import com.twyst.app.android.model.Outlet;
import com.twyst.app.android.model.ProfileUpdate;
import com.twyst.app.android.service.HttpService;
import com.twyst.app.android.util.Utils;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class DiscoverActivity extends BaseActivity implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener, LocationListener, ResultCallback<LocationSettingsResult> {

    private ArrayAdapter<LocationData> mAdapter;
    private boolean fetchingOutlets;
    private boolean outletsNotFound;
    private DiscoverOutletAdapter discoverAdapter;
    private FloatingActionsMenu fabMenu;
    private RelativeLayout obstructor;
    private RelativeLayout planAheadObstructor;
    private RelativeLayout planAheadObstructor2;
    private TextView selectedLocationTxt;
    private TextView locationText;
    private TextView editTextView;
    private FloatingActionButton submitFab;
    private FloatingActionButton checkinFab;
    private View planAheadContent;
    private String mDate;
    private String mTime;
    private boolean dateSelected;
    TextView planAheadTime;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private RecyclerView mRecyclerView;
    int autoCompleteFlag;
    private boolean mLocationsFetched = false;
    private String searchedItem;
    private Date selectedDate;
    private LocationData selectedLocation;
    TextView day1;
    private boolean clearDateTime, planAheadChanged, fromchangeLoc, planAheadChangeConfirm;
    private ImageView closeBtn;
    private TextView planAheadLocation;
    private boolean fromSplashScreenDownloading = false;

    private static final int GPS_TRY_AGAIN_TIME = 2500;

    private String mSelectedLat, mSelectedLng, mSelectedLocationName;
    private static final int LOCATION_CURRENT = 0;
    private static final int LOCATION_SELECTED = 1;
    private static final int LOCATION_SELECTED_PLAN_AHEAD = 2;
    private static final int LOCATION_LAST_KNOWN = 3;

    private static final String LOCATION_CURRENT_HTML = " <b><font color=\"#ffffff\">(Current)</font></b>";
    private static final String LOCATION_CURRENT_HTML_PLAN_AHEAD = " <b><font color=\"#636363\">(Current)</font></b>";
    private static final String LOCATION_SELECTED_HTML = " <b><font color=\"#ffffff\">(Selected)</font></b>";
    private static final String LOCATION_SELECTED_HTML_PLAN_AHEAD = " <b><font color=\"#636363\">(Selected)</font></b>";
    private static final String LOCATION_LAST_KNOWN_HTML = " <b><font color=\"#ffffff\">(Last Known)</font></b>";
    private static final String LOCATION_LAST_KNOWN_HTML_PLAN_AHEAD = " <b><font color=\"#636363\">(Last Known)</font></b>";

    private boolean isAskedToTurnOnGPS = false;

    private static final int REQUEST_CHECK_SETTINGS = 0x1;
    private static final int PLACE_PICKER_REQUEST = 2;

    private ArrayList<LocationData> mLocationList = new ArrayList<>(0);

    // Google client to interact with Google API
    private GoogleApiClient mGoogleApiClient;
    private LocationRequest mLocationRequest;

    // Location updates intervals in sec
    private static int UPDATE_INTERVAL = 10000; // 10 sec
    private static int FATEST_INTERVAL = 5000; // 5 sec
    private static int DISPLACEMENT = 10; // 10 meters

    protected boolean search;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        if (getIntent().getAction().equalsIgnoreCase("setChildYes")) {
            setupAsChild = true;
        } else {
            setupAsChild = false;
        }
        fromSplashScreenDownloading = getIntent().getBooleanExtra(AppConstants.INTENT_FROM_SPLASH_DOWNLOADING,false);
        super.onCreate(savedInstanceState);
        // First we need to check availability of play services
        if (checkPlayServices()) {

            // Building the GoogleApi client
            buildGoogleApiClient();
            createLocationRequest();
        }

        if (!search) {
            getLocationList();
            updateSocialFriendList();
        }

        mRecyclerView = (RecyclerView) findViewById(R.id.outletRecyclerView);
        mRecyclerView.setHasFixedSize(true);

        //Assigning resources
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        mRecyclerView.setLayoutManager(mLayoutManager);
        selectedLocationTxt = (TextView) findViewById(R.id.selectedLocationTxt);
        locationText = (TextView) findViewById(R.id.locationText);
        planAheadContent = findViewById(R.id.planAheadContent);
        editTextView = (TextView) findViewById(R.id.editTextView);
        closeBtn = (ImageView) findViewById(R.id.closeBtn);
        planAheadLocation = (TextView) findViewById(R.id.planAheadLocation);
        fabMenu = (FloatingActionsMenu) findViewById(R.id.fabMenu);
        obstructor = (RelativeLayout) findViewById(R.id.obstructor);
        planAheadObstructor = (RelativeLayout) findViewById(R.id.planAheadObstructor);
        planAheadObstructor2 = (RelativeLayout) findViewById(R.id.planAheadObstructor2);
        day1 = (TextView) findViewById(R.id.day1);
        submitFab = (FloatingActionButton) findViewById(R.id.submitFab);
        checkinFab = (FloatingActionButton) findViewById(R.id.checkinFab);

        setupSwipeRefresh();
        setupDiscoverAdapter();
        setOnClickListeners();


    }

    private void getLocationList() {
        HttpService.getInstance().getLocations(new Callback<BaseResponse<ArrayList<LocationData>>>() {
            @Override
            public void success(BaseResponse<ArrayList<LocationData>> arrayListBaseResponse, Response response) {
                mLocationList = arrayListBaseResponse.getData();
                if (mGoogleApiClient != null) {
                    mGoogleApiClient.connect();
                }
                if (fromSplashScreenDownloading){
                    registerReceiver();
                    sendBroadCastToSplashDownload();
                }
                mLocationsFetched = true;
            }

            @Override
            public void failure(RetrofitError error) {
                hideProgressHUDInLayout();
                handleRetrofitError(error);
            }
        });
    }

    private void refreshDiscoverAdapter() {
        if (discoverAdapter != null && discoverAdapter.getItems().size() > 0) {
            refreshOutletsBackground();
        }
    }

    private void setupDiscoverAdapter() {
        discoverAdapter = new DiscoverOutletAdapter();
        mRecyclerView.setAdapter(discoverAdapter);

        if (search) {
            editTextView.setText("");
            editTextView.setVisibility(View.VISIBLE);
            locationText.setText("");
            selectedLocationTxt.setCompoundDrawables(null, null, null, null);
            fetchSearchedOutlets();

        } else {
            planAheadObstructor2.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    togglePlanAhead();
                }
            });

            findViewById(R.id.planAhead).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    togglePlanAhead();
                }
            });

            discoverAdapter.setOnViewHolderListener(new DiscoverOutletAdapter.OnViewHolderListener() {
                @Override
                public void onRequestedLastItem() {
                    if (!fetchingOutlets && !outletsNotFound) {
                        List<Outlet> outlets = discoverAdapter.getItems();
                        fetchOutlets(outlets.size() + 1);
                    }
                }
            });
            setupAsPlanAheadView();
        }
    }

    private void setupSwipeRefresh() {
        mSwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipeRefreshLayout);
        mSwipeRefreshLayout.setColorSchemeResources(R.color.button_orange);
        mSwipeRefreshLayout.setEnabled(false);

        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (search) {
                    fetchSearchedOutlets();
                } else {
                    if (selectedLocationTxt.getText().toString().contains("(Selected)")) {
                        fetchOutlets(1);
                    } else {
                        getLocationFetch(true, false);
                    }

                }
            }
        });
    }

    private void setOnClickListeners() {
        //Change Map onClick
        findViewById(R.id.changeOnMapBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // showPlacesPicker();
                Intent intent = new Intent(DiscoverActivity.this, ChangeMapActivity.class);
                startActivityForResult(intent, PLACE_PICKER_REQUEST);
                fromchangeLoc = true;
            }
        });


        findViewById(R.id.planAheadSubmitBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectedLocationTxt.setVisibility(View.VISIBLE);
                if (planAheadChanged) {
                    closeBtn.setVisibility(View.VISIBLE);
                    editTextView.setVisibility(View.GONE);
                    clearDateTime = true;
                    setTextLocationFetch(LOCATION_SELECTED, null, null,true);
                    planAheadChangeConfirm = true;
                } else {
                    editTextView.setVisibility(View.VISIBLE);
                    closeBtn.setVisibility(View.GONE);
                }

                collapse(planAheadContent);
                locationText.setVisibility(View.GONE);
                fabMenu.setVisibility(View.VISIBLE);
                fabMenu.setEnabled(true);
            }
        });


        findViewById(R.id.planAheadCancelBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                collapse(planAheadContent);
                if (clearDateTime) {
                    closeBtn.setVisibility(View.VISIBLE);
                    editTextView.setVisibility(View.GONE);
                } else {
                    editTextView.setVisibility(View.VISIBLE);
                    closeBtn.setVisibility(View.GONE);
                }
                if (closeBtn.getVisibility() == View.VISIBLE) {
                    editTextView.setVisibility(View.VISIBLE);
                    closeBtn.setVisibility(View.GONE);
                }
                locationText.setVisibility(View.GONE);
                selectedLocationTxt.setVisibility(View.VISIBLE);

                fabMenu.setVisibility(View.VISIBLE);
                fabMenu.setEnabled(true);

            }
        });


        closeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (planAheadChanged) {
                    findViewById(R.id.circularProgressBar).setVisibility(View.VISIBLE);
                    editTextView.setVisibility(View.VISIBLE);
                    closeBtn.setVisibility(View.GONE);

                    refreshDateTime();
                    getLocationFetch(true, false);

                }
            }
        });

        fabMenu.setOnFloatingActionsMenuUpdateListener(new FloatingActionsMenu.OnFloatingActionsMenuUpdateListener() {
            @Override
            public void onMenuExpanded() {
                if (obstructor.getVisibility() == View.INVISIBLE) {
                    obstructor.setVisibility(View.VISIBLE);
                    mRecyclerView.setEnabled(false);
                }

            }

            @Override
            public void onMenuCollapsed() {
                if (obstructor.getVisibility() == View.VISIBLE) {
                    obstructor.setVisibility(View.INVISIBLE);
                    mRecyclerView.setEnabled(true);
                }
            }
        });


        obstructor.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                if (obstructor.getVisibility() == View.VISIBLE) {
                    fabMenu.collapse();
                    return true;
                }

                return false;
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
                        Intent intent = new Intent(DiscoverActivity.this, ScannerActivity.class);
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

                        Intent intent = new Intent(DiscoverActivity.this, UploadBillActivity.class);
                        intent.setAction("setChildYes");
                        startActivity(intent);
                    }
                }, 100);
            }
        });

        submitFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (obstructor.getVisibility() == View.VISIBLE) {
                    Intent intent = new Intent(DiscoverActivity.this, SubmitOfferActivity.class);
                    intent.setAction("setChildYes");
                    startActivity(intent);
                    fabMenu.collapse();
                }
            }
        });

    }

    private void updateSocialFriendList() {

        if (getPrefs().getBoolean(AppConstants.PREFERENCE_IS_FRIEND_LIST_UPDATED, false)) {
            return;
        }

        String friendString = getPrefs().getString(AppConstants.PREFERENCE_FRIEND_LIST, "");
        Gson gson = new Gson();
        Friend friend = gson.fromJson(friendString, Friend.class);

        if (friend == null) return;

        HttpService.getInstance().updateSocialFriends(getUserToken(), friend, new Callback<BaseResponse<ProfileUpdate>>() {
            @Override
            public void success(BaseResponse<ProfileUpdate> profileUpdateBaseResponse, Response response) {
                if (profileUpdateBaseResponse.isResponse()) {
                    Log.d(getTagName(), "" + profileUpdateBaseResponse.getMessage());
                    getPrefs().edit().putBoolean(AppConstants.PREFERENCE_IS_FRIEND_LIST_UPDATED, true).apply();
                    getPrefs().edit().putString(AppConstants.PREFERENCE_FRIEND_LIST, "").apply();
                } else {
                    Log.d(getTagName(), "" + profileUpdateBaseResponse.getMessage());
                }
            }

            @Override
            public void failure(RetrofitError error) {
//                handleRetrofitError(error);
            }
        });
    }

    private void onItemsLoadComplete() {
        mSwipeRefreshLayout.setRefreshing(false);
    }

    private void togglePlanAhead() {
        if (planAheadContent.getVisibility() == View.VISIBLE) {

            collapse(planAheadContent);

            if (clearDateTime) {
                closeBtn.setVisibility(View.VISIBLE);
                editTextView.setVisibility(View.GONE);
            } else {
                editTextView.setVisibility(View.VISIBLE);
                closeBtn.setVisibility(View.GONE);
            }

            locationText.setVisibility(View.GONE);
            selectedLocationTxt.setVisibility(View.VISIBLE);

            fabMenu.setVisibility(View.VISIBLE);

        } else {
            expand(planAheadContent);
            closeBtn.setVisibility(View.GONE);
            editTextView.setVisibility(View.INVISIBLE);
            locationText.setVisibility(View.VISIBLE);
            selectedLocationTxt.setVisibility(View.GONE);
            fabMenu.setVisibility(View.GONE);
        }
    }

    private void setupAsPlanAheadView() {
        Calendar calendar = Calendar.getInstance();
        int dayOfWeekInt = calendar.get(Calendar.DAY_OF_WEEK);

        planAheadTime = (TextView) findViewById(R.id.planAheadTime);
        day1.setText("today");
        final Date currentDateTime = calendar.getTime();
        day1.setTag(currentDateTime);
        mDate = (String) DateFormat.format("MM-dd-yyyy", currentDateTime);
        selectedDate = currentDateTime;

        day1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                deselectDays();
                day1.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
                day1.setTextColor(Color.WHITE);
                mDate = (String) DateFormat.format("MM-dd-yyyy", currentDateTime);
                dateSelected = true;
                selectedDate = (Date) day1.getTag();
                planAheadChanged = true;
            }
        });

        calendar.set(Calendar.DATE, calendar.get(Calendar.DATE) + 1);
        final TextView day2 = (TextView) findViewById(R.id.day2);
        day2.setText(dayOfWeek(++dayOfWeekInt % 7));
        day2.setTag(calendar.getTime());
        day2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                deselectDays();
                day2.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
                day2.setTextColor(Color.WHITE);
                mDate = (String) DateFormat.format("MM-dd-yyyy", (Date) day2.getTag());
                dateSelected = true;
                selectedDate = (Date) day2.getTag();
                planAheadChanged = true;
            }
        });

        calendar.set(Calendar.DATE, calendar.get(Calendar.DATE) + 1);
        final TextView day3 = (TextView) findViewById(R.id.day3);
        day3.setText(dayOfWeek(++dayOfWeekInt % 7));
        day3.setTag(calendar.getTime());
        day3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                deselectDays();
                day3.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
                day3.setTextColor(Color.WHITE);
                mDate = (String) DateFormat.format("MM-dd-yyyy", (Date) day3.getTag());
                dateSelected = true;
                planAheadChanged = true;
                selectedDate = (Date) day3.getTag();
            }
        });

        calendar.set(Calendar.DATE, calendar.get(Calendar.DATE) + 1);
        final TextView day4 = (TextView) findViewById(R.id.day4);
        day4.setText(dayOfWeek(++dayOfWeekInt % 7));
        day4.setTag(calendar.getTime());
        day4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                deselectDays();
                day4.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
                day4.setTextColor(Color.WHITE);
                mDate = (String) DateFormat.format("MM-dd-yyyy", (Date) day4.getTag());
                dateSelected = true;
                selectedDate = (Date) day4.getTag();
                planAheadChanged = true;
            }
        });

        calendar.set(Calendar.DATE, calendar.get(Calendar.DATE) + 1);
        final TextView day5 = (TextView) findViewById(R.id.day5);
        day5.setText(dayOfWeek(++dayOfWeekInt % 7));
        day5.setTag(calendar.getTime());
        day5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                deselectDays();
                day5.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
                day5.setTextColor(Color.WHITE);
                mDate = (String) DateFormat.format("MM-dd-yyyy", (Date) day5.getTag());
                dateSelected = true;
                selectedDate = (Date) day5.getTag();
                planAheadChanged = true;
            }
        });

        calendar.set(Calendar.DATE, calendar.get(Calendar.DATE) + 1);
        final TextView day6 = (TextView) findViewById(R.id.day6);
        day6.setText(dayOfWeek(++dayOfWeekInt % 7));
        day6.setTag(calendar.getTime());
        day6.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                deselectDays();
                day6.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
                day6.setTextColor(Color.WHITE);
                mDate = (String) DateFormat.format("MM-dd-yyyy", (Date) day6.getTag());
                dateSelected = true;
                selectedDate = (Date) day6.getTag();
                planAheadChanged = true;
            }
        });

        calendar.set(Calendar.DATE, calendar.get(Calendar.DATE) + 1);
        final TextView day7 = (TextView) findViewById(R.id.day7);
        day7.setText(dayOfWeek(++dayOfWeekInt % 7));
        day7.setTag(calendar.getTime());
        day7.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                deselectDays();
                day7.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
                day7.setTextColor(Color.WHITE);
                mDate = (String) DateFormat.format("MM-dd-yyyy", (Date) day7.getTag());
                dateSelected = true;
                selectedDate = (Date) day7.getTag();
                planAheadChanged = true;
            }
        });

        int hours = calendar.get(Calendar.HOUR);
        int minutes = calendar.get(Calendar.MINUTE);

        int hoursIn = calendar.get(Calendar.HOUR_OF_DAY);

        int ampm = calendar.get(Calendar.AM_PM);

        mTime = convertHoursToString(hoursIn) + ":" + convertMinutesToString(minutes);

        planAheadTime.setText(convertHoursToString(hours) + ":" + convertMinutesToString((minutes > 30) ? 0 : 30) + ((ampm == 0) ? "am" : "pm"));

        int progress = ((ampm == 0) ? hours : hours + 12) * 2 + ((minutes > 30) ? 1 : 0);

        SeekBar seekBar = (SeekBar) findViewById(R.id.seekBar);
        seekBar.setProgress(progress);

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean b) {

                if (progress == 0 || progress == 48)
                    return;

                int hours = progress / 2;
                int minutes = (progress % 2) * 30;

                mTime = convertHoursToString(hours) + ":" + convertMinutesToString(minutes);
                if (!dateSelected) {
                    selectedDate = (Date) day1.getTag();
                    dateSelected = true;

                }
                planAheadChanged = true;
                Log.d(getTagName(), "selected time: hour: " + hours + ", minutes: " + minutes + ", final: " + convertHoursToString(hours) + ":" + convertMinutesToString(minutes));
                planAheadTime.setText(convertHoursToString(((hours <= 12) ? hours : (hours % 12))) + ":" + convertMinutesToString(minutes) + ((hours < 12) ? "am" : "pm"));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
    }

    private String convertHoursToString(int hours) {
        String hoursStr = "";
        if (hours == 0 || hours == 24) {
            hoursStr = "00";
        } else if (hours < 10) {
            hoursStr = "0" + hours;
        } else {
            hoursStr = "" + hours;
        }

        return hoursStr;
    }

    private String convertMinutesToString(int minutes) {
        String timeStr = "";
        if (minutes == 0) {
            timeStr = "00";
        } else {
            timeStr = "" + minutes;
        }
        return timeStr;
    }

    private void deselectDays() {
        TextView day1 = (TextView) findViewById(R.id.day1);
        TextView day2 = (TextView) findViewById(R.id.day2);
        TextView day3 = (TextView) findViewById(R.id.day3);
        TextView day4 = (TextView) findViewById(R.id.day4);
        TextView day5 = (TextView) findViewById(R.id.day5);
        TextView day6 = (TextView) findViewById(R.id.day6);
        TextView day7 = (TextView) findViewById(R.id.day7);

        int color = getResources().getColor(android.R.color.transparent);
        day1.setBackgroundColor(color);
        day2.setBackgroundColor(color);
        day3.setBackgroundColor(color);
        day4.setBackgroundColor(color);
        day5.setBackgroundColor(color);
        day6.setBackgroundColor(color);
        day7.setBackgroundColor(color);

        int textColor = Color.parseColor("#636363");
        day1.setTextColor(textColor);
        day2.setTextColor(textColor);
        day3.setTextColor(textColor);
        day4.setTextColor(textColor);
        day5.setTextColor(textColor);
        day6.setTextColor(textColor);
        day7.setTextColor(textColor);
    }

    private String dayOfWeek(int dayOfWeekInt) {
        if (dayOfWeekInt == 0) {
            dayOfWeekInt = 7;
        }
        Log.d(getTagName(), "dayOfWeek() dayOfWeekInt: " + dayOfWeekInt);
        switch (dayOfWeekInt) {
            case 1:
                return "sun";
            case 2:
                return "mon";
            case 3:
                return "tue";
            case 4:
                return "wed";
            case 5:
                return "thu";
            case 6:
                return "fri";
            case 7:
                return "sat";

        }
        return "";
    }


    private void askLocationFromUser() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_location_notfound, null);

        final AutoCompleteTextView mAutocompleteView = (AutoCompleteTextView) dialogView.findViewById(R.id.customLocation);
        // Register a listener that receives callbacks when a suggestion has been selected
        mAutocompleteView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Log.d(getClass().getSimpleName(), "i=" + i + ", l=" + l);
                selectedLocation = (LocationData) adapterView.getItemAtPosition(i);
                Log.d(getTagName(), "selected location: " + selectedLocation);
                autoCompleteFlag = 1;
            }
        });

        mAdapter = new ArrayAdapter<LocationData>(this, R.layout.location_dropdown_item, mLocationList);
        mAutocompleteView.setAdapter(mAdapter);
        builder.setView(dialogView);

        final AlertDialog dialog = builder.create();
        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();


        dialog.setOnKeyListener(new Dialog.OnKeyListener() {

            @Override
            public boolean onKey(DialogInterface arg0, int keyCode,
                                 KeyEvent event) {
                // TODO Auto-generated method stub
                if (keyCode == KeyEvent.KEYCODE_BACK) {

                    SharedPreferences prefs = getSharedPreferences(AppConstants.PREFERENCE_SHARED_PREF_NAME, Context.MODE_PRIVATE);
                    String launched = prefs.getString(AppConstants.PREFERENCE_CHECK_FIRST_LAUNCH, "");
                    if (launched.equalsIgnoreCase("Yes")) {
                        setTextLocationFetch(LOCATION_LAST_KNOWN, null, null, true);
                        dialog.dismiss();
                    } else {
//                        DiscoverActivity.this.finish();

                    }
                }
                return true;
            }
        });

        SharedPreferences prefs = getSharedPreferences(AppConstants.PREFERENCE_SHARED_PREF_NAME, Context.MODE_PRIVATE);
        String launched = prefs.getString(AppConstants.PREFERENCE_CHECK_FIRST_LAUNCH, "");
        if (launched.equalsIgnoreCase("Yes")) {
            dialogView.findViewById(R.id.cancelBtn).setVisibility(View.VISIBLE);
        } else {
            dialogView.findViewById(R.id.cancelBtn).setVisibility(View.INVISIBLE);
        }

        dialogView.findViewById(R.id.cancelBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SharedPreferences prefs = getSharedPreferences(AppConstants.PREFERENCE_SHARED_PREF_NAME, Context.MODE_PRIVATE);
                String launched = prefs.getString(AppConstants.PREFERENCE_CHECK_FIRST_LAUNCH, "");
                if (launched.equalsIgnoreCase("Yes")) {
                    setTextLocationFetch(LOCATION_LAST_KNOWN, null, null,true);
                    dialog.dismiss();
                } else {
//                    DiscoverActivity.this.finish();
                }
            }
        });

        dialogView.findViewById(R.id.customLocationSubmitBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (autoCompleteFlag == 1) {
                    mSelectedLat = selectedLocation.getLat();
                    mSelectedLng = selectedLocation.getLng();
                    mSelectedLocationName = selectedLocation.toString();
                    planAheadLocation.setText(Html.fromHtml(mSelectedLocationName + LOCATION_SELECTED_HTML_PLAN_AHEAD));
                    setTextLocationFetch(LOCATION_SELECTED, null, null, true);
                    closeBtn.setVisibility(View.VISIBLE);
                    editTextView.setVisibility(View.GONE);
                    clearDateTime = true;
                    planAheadChanged = true;
                    dialog.dismiss();
                } else {
                    mAutocompleteView.setError("Address required");
                }
            }
        });
    }

    private void fetchOutlets(final int start) {
        String latitude = getPrefs().getString(AppConstants.PREFERENCE_CURRENT_USED_LAT, null);
        String longitude = getPrefs().getString(AppConstants.PREFERENCE_CURRENT_USED_LNG, null);

        Log.d(getTagName(), "Going to fetch outlets with, start: " + start + ", lat " + latitude + ", long: " + longitude + ", date: " + mDate + ", time: " + mTime);
        fetchingOutlets = true;
        mSwipeRefreshLayout.setEnabled(true);

        int end = start + AppConstants.DISCOVER_LIST_PAGESIZE - 1;

        HttpService.getInstance().getRecommendedOutlets(getUserToken(), start, end, latitude, longitude, mDate, mTime, new Callback<BaseResponse<DiscoverData>>() {
            @Override
            public void success(BaseResponse<DiscoverData> arrayListBaseResponse, Response response) {
                fetchingOutlets = false;

                ArrayList<Outlet> outlets = arrayListBaseResponse.getData().getOutlets();
                String twystBucks = arrayListBaseResponse.getData().getTwystBucks();
                if (!TextUtils.isEmpty(twystBucks)) {

                    sharedPreferences.putInt(AppConstants.PREFERENCE_LAST_TWYST_BUCK, Integer.parseInt(twystBucks));
                    sharedPreferences.apply();
                }

                sharedPreferences.putString(AppConstants.PREFERENCE_CHECK_FIRST_LAUNCH, "Yes");
                sharedPreferences.apply();
                if (outlets != null) {
                    if (outlets.isEmpty()) {
                        outletsNotFound = true;
                        discoverAdapter.setOutletsNotFound(true);
                    } else {
                        if (start == 1) {//Clear the items in adapter, fresh download
                            Log.d(getTagName(), "clearing outlets on discover screen");
                            discoverAdapter.getItems().clear();
                        }

                        if (outlets.size() < AppConstants.DISCOVER_LIST_PAGESIZE) {
                            outletsNotFound = true;
                            discoverAdapter.setOutletsNotFound(true);
                        } else {
                            outletsNotFound = false;
                            discoverAdapter.setOutletsNotFound(false);
                        }

                        discoverAdapter.getItems().addAll(outlets);
                        discoverAdapter.notifyDataSetChanged();
                    }
                }

                onItemsLoadComplete();
                hideProgressHUDInLayout();
                hideSnackbar();

                if (planAheadContent.getVisibility() == View.VISIBLE) {
                    fabMenu.setVisibility(View.GONE);
                } else {
                    fabMenu.setVisibility(View.VISIBLE);
                }

                findViewById(R.id.planAhead).setVisibility(View.VISIBLE);
            }

            @Override
            public void failure(RetrofitError error) {
                fetchingOutlets = false;
                hideProgressHUDInLayout();
                onItemsLoadComplete();
                handleRetrofitError(error);
            }
        });

    }

    private void refreshOutletsBackground() {
        String latitude = getPrefs().getString(AppConstants.PREFERENCE_CURRENT_USED_LAT, null);
        String longitude = getPrefs().getString(AppConstants.PREFERENCE_CURRENT_USED_LNG, null);

        mSwipeRefreshLayout.setRefreshing(true);
        fetchingOutlets = true;
        int end = discoverAdapter.getItems().size();

        HttpService.getInstance().getRecommendedOutlets(getUserToken(), 1, end, latitude, longitude, mDate, mTime, new Callback<BaseResponse<DiscoverData>>() {
            @Override
            public void success(BaseResponse<DiscoverData> arrayListBaseResponse, Response response) {
                fetchingOutlets = false;
                ArrayList<Outlet> outlets = arrayListBaseResponse.getData().getOutlets();
                String twystBucks = arrayListBaseResponse.getData().getTwystBucks();
                if (!TextUtils.isEmpty(twystBucks)) {

                    sharedPreferences.putInt(AppConstants.PREFERENCE_LAST_TWYST_BUCK, Integer.parseInt(twystBucks));
                    sharedPreferences.apply();
                }
                onItemsLoadComplete();
                hideProgressHUDInLayout();
                hideSnackbar();

                if (outlets != null) {
                    discoverAdapter.updateList(outlets);
                }
            }

            @Override
            public void failure(RetrofitError error) {
                fetchingOutlets = false;
                handleRetrofitError(error);
                onItemsLoadComplete();
                hideProgressHUDInLayout();
            }
        });

    }

    private void fetchSearchedOutlets() {
        mSwipeRefreshLayout.setEnabled(true);
        SharedPreferences prefs = getSharedPreferences(AppConstants.PREFERENCE_SHARED_PREF_NAME, Context.MODE_PRIVATE);
        searchedItem = prefs.getString(AppConstants.PREFERENCE_PARAM_SEARCH_QUERY, "");
        String lat = prefs.getString(AppConstants.PREFERENCE_CURRENT_USED_LAT, "");
        String lng = prefs.getString(AppConstants.PREFERENCE_CURRENT_USED_LNG, "");
        selectedLocationTxt.setText("Showing results for '" + searchedItem + "'");
        closeBtn.setVisibility(View.VISIBLE);

        Calendar calendar = Calendar.getInstance();
        Date currentDateTime = calendar.getTime();
        String date = (String) DateFormat.format("MM-dd-yyyy", currentDateTime);
        int hoursIn = calendar.get(Calendar.HOUR_OF_DAY);
        int minutes = calendar.get(Calendar.MINUTE);
        String time = convertHoursToString(hoursIn) + ":" + convertMinutesToString(minutes);

        if (!TextUtils.isEmpty(searchedItem)) {
            HttpService.getInstance().searchOffer(getUserToken(), searchedItem, lat, lng, date, time, new Callback<BaseResponse<DiscoverData>>() {
                @Override
                public void success(BaseResponse<DiscoverData> discoverDataBaseResponse, Response response) {

                    if (discoverDataBaseResponse.isResponse()) {

                        ArrayList<Outlet> outlets = discoverDataBaseResponse.getData().getOutlets();
                        String twystBucks = discoverDataBaseResponse.getData().getTwystBucks();

                        if (!TextUtils.isEmpty(twystBucks)) {
                            sharedPreferences.putInt(AppConstants.PREFERENCE_LAST_TWYST_BUCK, Integer.parseInt(twystBucks));
                            sharedPreferences.commit();
                        }

                        if (outlets != null) {
                            if (outlets.isEmpty()) {
                                outletsNotFound = true;
                                discoverAdapter.setOutletsNotFound(true);
                            } else {
                                discoverAdapter.getItems().clear();

                                if (outlets.size() < AppConstants.DISCOVER_LIST_PAGESIZE) {
                                    outletsNotFound = true;
                                    discoverAdapter.setOutletsNotFound(true);
                                } else {
                                    outletsNotFound = false;
                                    discoverAdapter.setOutletsNotFound(false);
                                }

                                discoverAdapter.getItems().addAll(outlets);
                                discoverAdapter.notifyDataSetChanged();
                            }
                        }

                        fabMenu.setVisibility(View.VISIBLE);
                        findViewById(R.id.planAhead).setVisibility(View.VISIBLE);

                        findViewById(R.id.blankDataLayout).setVisibility(View.GONE);

                    } else {
                        findViewById(R.id.ivNoData).setBackgroundResource(R.drawable.no_search);
                        ((TextView) findViewById(R.id.tvNoData)).setText("Sorry - we couldn't find anything for that query. Please try a different search term");
                        findViewById(R.id.blankDataLayout).setVisibility(View.VISIBLE);
//                        Toast.makeText(DiscoverActivity.this, discoverDataBaseResponse.getMessage(), Toast.LENGTH_SHORT).show();
                    }

                    onItemsLoadComplete();
                    hideProgressHUDInLayout();
                    hideSnackbar();
                }

                @Override
                public void failure(RetrofitError error) {
                    handleRetrofitError(error);
                    onItemsLoadComplete();
                    hideProgressHUDInLayout();
                }
            });
        }

    }

    private void getLocationFetch(final boolean tryAgain, final boolean isOnCreate) {
        Log.i(getTagName(), "getLocationFetch called isConnected: " + mGoogleApiClient.isConnected());
        Location mLastLocation = null;
        if (mGoogleApiClient != null) {
            mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        }
        if (mLastLocation != null) {
            // Got current location
            String lat = String.valueOf(mLastLocation.getLatitude());
            String lng = String.valueOf(mLastLocation.getLongitude());
            setTextLocationFetch(LOCATION_CURRENT, lat, lng, true);
        } else {

            //If location fetch is to be tried again
            if (tryAgain) {
                // Execute some code after 2 seconds have passed
                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    public void run() {
                        getLocationFetch(false, isOnCreate);
                        onItemsLoadComplete();
                    }
                }, GPS_TRY_AGAIN_TIME);
            } else {
                //If first time created, prompt to get location, otherwise use last known location
                if (isOnCreate) {
                    askLocationFromUser();
                    onItemsLoadComplete();
                } else {
                    setTextLocationFetch(LOCATION_LAST_KNOWN, null, null, true);
                }
            }

        }
    }

    private void setTextLocationFetch(final int locationCase, String lat, String lng, boolean toFetch) {
        String locationName;
        switch (locationCase) {
            case LOCATION_CURRENT:
                locationName = findNearestOutletName(Double.valueOf(lat), Double.valueOf(lng));
                sharedPreferences.putString(AppConstants.PREFERENCE_CURRENT_USED_LAT, lat);
                sharedPreferences.putString(AppConstants.PREFERENCE_UPDATED_LAT, lat);
                sharedPreferences.putString(AppConstants.PREFERENCE_CURRENT_USED_LNG, lng);
                sharedPreferences.putString(AppConstants.PREFERENCE_UPDATED_LNG, lng);
                sharedPreferences.putString(AppConstants.PREFERENCE_CURRENT_USED_LOCATION_NAME, locationName);
                sharedPreferences.commit();

                selectedLocationTxt.setText(Html.fromHtml(locationName + LOCATION_CURRENT_HTML));
                localityDrawer.setText(locationName);
                planAheadLocation.setText(Html.fromHtml(locationName + LOCATION_CURRENT_HTML_PLAN_AHEAD));

//                findViewById(R.id.circularProgressBar).setVisibility(View.VISIBLE);
                if (toFetch){
                    fetchOutlets(1);
                }
                break;

            case LOCATION_LAST_KNOWN:
                lat = getPrefs().getString(AppConstants.PREFERENCE_UPDATED_LAT, "");
                lng = getPrefs().getString(AppConstants.PREFERENCE_UPDATED_LNG, "");
                if (lat.equals("") || lng.equals("")) {
                    locationName = getPrefs().getString(AppConstants.PREFERENCE_CURRENT_USED_LOCATION_NAME, "");
                } else {
                    locationName = findNearestOutletName(Double.valueOf(lat), Double.valueOf(lng));
                    sharedPreferences.putString(AppConstants.PREFERENCE_CURRENT_USED_LAT, lat);
                    sharedPreferences.putString(AppConstants.PREFERENCE_CURRENT_USED_LNG, lng);
                    sharedPreferences.putString(AppConstants.PREFERENCE_CURRENT_USED_LOCATION_NAME, locationName);
                    sharedPreferences.commit();
                }

                selectedLocationTxt.setText(Html.fromHtml(locationName + LOCATION_LAST_KNOWN_HTML));
                localityDrawer.setText(locationName);
                planAheadLocation.setText(Html.fromHtml(locationName + LOCATION_LAST_KNOWN_HTML_PLAN_AHEAD));

                findViewById(R.id.circularProgressBar).setVisibility(View.VISIBLE);
                if (toFetch){
                    fetchOutlets(1);
                }
                break;

            case LOCATION_SELECTED:
                if (planAheadLocation.getText().toString().contains("(Selected)")) {
                    sharedPreferences.putString(AppConstants.PREFERENCE_CURRENT_USED_LAT, mSelectedLat);
                    sharedPreferences.putString(AppConstants.PREFERENCE_CURRENT_USED_LNG, mSelectedLng);
                    sharedPreferences.putString(AppConstants.PREFERENCE_CURRENT_USED_LOCATION_NAME, mSelectedLocationName);
                    sharedPreferences.commit();

                    selectedLocationTxt.setText(Html.fromHtml(mSelectedLocationName + LOCATION_SELECTED_HTML));
                    localityDrawer.setText(mSelectedLocationName);
                    planAheadLocation.setText(Html.fromHtml(mSelectedLocationName + LOCATION_SELECTED_HTML_PLAN_AHEAD));
                }
                findViewById(R.id.circularProgressBar).setVisibility(View.VISIBLE);
                if (toFetch){
                    fetchOutlets(1);
                }
                break;

            case LOCATION_SELECTED_PLAN_AHEAD:
                locationName = findNearestOutletName(Double.valueOf(lat), Double.valueOf(lng));

                String lastSavedLocationName = getPrefs().getString(AppConstants.PREFERENCE_CURRENT_USED_LOCATION_NAME, "");
                if (lastSavedLocationName.equalsIgnoreCase(locationName)) {
                    planAheadChanged = false;
                } else {
                    planAheadLocation.setText(Html.fromHtml(locationName + LOCATION_SELECTED_HTML_PLAN_AHEAD));
                    mSelectedLat = lat;
                    mSelectedLng = lng;
                    mSelectedLocationName = locationName;
                    planAheadChanged = true;
                }
                break;
        }


    }


    private LocationData findNearestOutletLocation(double latitude, double longitude) {
        ArrayList<LocationData> locations2 = new ArrayList<>(mLocationList);
        for (LocationData locationData : locations2) {
            double distance = Utils.distance(latitude, longitude, Double.valueOf(locationData.getLat()), Double.valueOf(locationData.getLng()), 'K');
            locationData.setDistance(distance);
        }

        Collections.sort(locations2, new Comparator<LocationData>() {
            @Override
            public int compare(LocationData locationData1, LocationData locationData2) {
                return Double.valueOf(locationData1.getDistance()).compareTo(Double.valueOf(locationData2.getDistance()));
            }
        });

        LocationData minLocation = locations2.get(0);
        return minLocation;
    }

    private String findNearestOutletName(double latitude, double longitude) {

        LocationData minLocation = findNearestOutletLocation(latitude, longitude);
        return minLocation.toString();
    }


    private void expand(final View v) {
        v.measure(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        final int targetHeight = v.getMeasuredHeight();

        // Older versions of android (pre API 21) cancel animations for views with a height of 0.
        v.getLayoutParams().height = 1;
        v.setVisibility(View.VISIBLE);
        final AlphaAnimation animation1 = new AlphaAnimation(0.0f, 0.7f);
        animation1.setDuration(50);
        //animation1.setStartOffset(10);
        planAheadObstructor.setVisibility(View.VISIBLE);
        planAheadObstructor2.setVisibility(View.VISIBLE);

        Animation a = new Animation() {
            @Override
            protected void applyTransformation(float interpolatedTime, Transformation t) {
                v.getLayoutParams().height = interpolatedTime == 1
                        ? ViewGroup.LayoutParams.WRAP_CONTENT
                        : (int) (targetHeight * interpolatedTime);
                v.requestLayout();

            }

            @Override
            public boolean willChangeBounds() {
                return true;
            }
        };

        // 1dp/ms
        //a.setDuration((int) (targetHeight / v.getContext().getResources().getDisplayMetrics().density));
        planAheadObstructor.startAnimation(animation1);
        a.setDuration(50);
        v.startAnimation(a);
    }

    private void collapse(final View v) {
        final int initialHeight = v.getMeasuredHeight();
        final AlphaAnimation animation1 = new AlphaAnimation(0.7f, 0.0f);
        animation1.setDuration(75);
        //animation1.setStartOffset(10);
        planAheadObstructor.setVisibility(View.GONE);
        planAheadObstructor2.setVisibility(View.GONE);
        Animation a = new Animation() {
            @Override
            protected void applyTransformation(float interpolatedTime, Transformation t) {
                if (interpolatedTime == 1) {
                    v.setVisibility(View.GONE);
                } else {
                    v.getLayoutParams().height = initialHeight - (int) (initialHeight * interpolatedTime);
                    v.requestLayout();
                }
            }

            @Override
            public boolean willChangeBounds() {
                return true;
            }
        };

        // 1dp/ms
        //a.setDuration((int) (initialHeight / v.getContext().getResources().getDisplayMetrics().density));
        a.setDuration(75);
        planAheadObstructor.startAnimation(animation1);
        v.startAnimation(a);
    }

    private void refreshDateTime() {
        SharedPreferences prefs = getSharedPreferences(AppConstants.PREFERENCE_SHARED_PREF_NAME, Context.MODE_PRIVATE);
        searchedItem = prefs.getString(AppConstants.PREFERENCE_PARAM_SEARCH_QUERY, "");
        Calendar calendar = Calendar.getInstance();
        Date currentDateTime = calendar.getTime();
        mDate = (String) DateFormat.format("MM-dd-yyyy", currentDateTime);
        int hoursIn = calendar.get(Calendar.HOUR_OF_DAY);
        int minutes = calendar.get(Calendar.MINUTE);
        mTime = convertHoursToString(hoursIn) + ":" + convertMinutesToString(minutes);

        clearDateTime = false;
        deselectDays();
        day1.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
        day1.setTextColor(Color.WHITE);
        dateSelected = true;

        selectedDate = (Date) day1.getTag();

        int hours = calendar.get(Calendar.HOUR);

        int ampm = calendar.get(Calendar.AM_PM);
        int progress = ((ampm == 0) ? hours : hours + 12) * 2 + ((minutes > 30) ? 1 : 0);

        SeekBar seekBar = (SeekBar) findViewById(R.id.seekBar);
        seekBar.setProgress(progress);

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean b) {

                if (progress == 0 || progress == 48)
                    return;

                int hours = progress / 2;
                int minutes = (progress % 2) * 30;

                mTime = convertHoursToString(hours) + ":" + convertMinutesToString(minutes);
                if (!dateSelected) {
                    selectedDate = (Date) day1.getTag();
                    dateSelected = true;

                }
                planAheadTime.setText(convertHoursToString(((hours <= 12) ? hours : (hours % 12))) + ":" + convertMinutesToString(minutes) + ((hours < 12) ? "am" : "pm"));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

    }


    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.d(getTagName(), "onActivityResult: requestCode: " + requestCode + ", resultcode: " + resultCode);

        if (requestCode == PLACE_PICKER_REQUEST) {
            if (resultCode == RESULT_OK) {

                Bundle extras = data.getExtras();
                LocationData locationData = (LocationData) extras.getSerializable("locationData");
                if (locationData != null) {
                    setTextLocationFetch(LOCATION_SELECTED_PLAN_AHEAD, locationData.getLat(), locationData.getLng(), true);
                }
            }
        }

        if (requestCode == REQUEST_CHECK_SETTINGS) {
            switch (resultCode) {
                case Activity.RESULT_OK:
                    Log.i(getTagName(), "User agreed to make required location settings changes.");
                    getLocationFetch(true, true);
                    break;
                case Activity.RESULT_CANCELED:
                    Log.i(getTagName(), "User chose not to make required location settings changes.");
                    //buildAndShowSnackbarWithMessage("Please enable location services and retry again.");
                    getLocationFetch(false, true);
                    break;
            }
        }

    }

    @Override
    public void onBackPressed() {
        if (obstructor.getVisibility() == View.VISIBLE) {
            fabMenu.collapse();

        } else if (findViewById(R.id.checkinObstructor).getVisibility() == View.VISIBLE) {
            fabMenu.collapse();
            findViewById(R.id.checkinObstructor).setVisibility(View.GONE);
            findViewById(R.id.checkinObstructor2).setVisibility(View.GONE);

        } else if (drawerOpened) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            DiscoverActivity.this.finish();
        }

    }

    @Override
    protected void onStart() {
        super.onStart();
        if (mGoogleApiClient != null && !mLocationList.isEmpty()) {
            mGoogleApiClient.connect();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mGoogleApiClient != null && mGoogleApiClient.isConnected()) {
            mGoogleApiClient.disconnect();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        checkPlayServices();
        refreshDiscoverAdapter();

        if (fromSplashScreenDownloading && mLocationsFetched){
            registerReceiver();
            sendBroadCastToSplashDownload();
        }
        // Resuming the periodic location updates
        if (mGoogleApiClient != null && mGoogleApiClient.isConnected()) {
            startLocationUpdates();
        }
        AppsFlyerLib.onActivityResume(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mGoogleApiClient != null && mGoogleApiClient.isConnected()) {
            stopLocationUpdates();
        }
        if (fromSplashScreenDownloading){
            unRegisterReceiver();
        }
        AppsFlyerLib.onActivityPause(this);
    }

    /**
     * Creating google api client object
     */
    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API).build();
    }

    /**
     * Creating location request object
     */
    protected void createLocationRequest() {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(UPDATE_INTERVAL);
        mLocationRequest.setFastestInterval(FATEST_INTERVAL);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setSmallestDisplacement(DISPLACEMENT);
    }

    /**
     * Starting the location updates
     */
    protected void startLocationUpdates() {
        LocationServices.FusedLocationApi.requestLocationUpdates(
                mGoogleApiClient, mLocationRequest, this);

    }

    /**
     * Stopping location updates
     */
    protected void stopLocationUpdates() {
        LocationServices.FusedLocationApi.removeLocationUpdates(
                mGoogleApiClient, this);
    }

    @Override
    protected String getTagName() {
        return DiscoverActivity.class.getSimpleName();
    }

    @Override
    protected int getLayoutResource() {
        return R.layout.activity_discover;
    }

    @Override
    public void onConnected(Bundle bundle) {
        startLocationUpdates();

        if (!isAskedToTurnOnGPS && !fromSplashScreenDownloading) {
            LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                    .addLocationRequest(mLocationRequest)
                    .setAlwaysShow(true);

            LocationSettingsRequest locationSettingsRequest = builder.build();
            PendingResult<LocationSettingsResult> result =
                    LocationServices.SettingsApi.checkLocationSettings(mGoogleApiClient, locationSettingsRequest);
            result.setResultCallback(this);
            isAskedToTurnOnGPS = true;
        }
    }

    @Override
    public void onConnectionSuspended(int i) {
        mGoogleApiClient.connect();
    }

    @Override
    public void onLocationChanged(Location location) {
        getPrefs().edit().putString(AppConstants.PREFERENCE_UPDATED_LAT, String.valueOf(location.getLatitude())).apply();
        getPrefs().edit().putString(AppConstants.PREFERENCE_UPDATED_LNG, String.valueOf(location.getLongitude())).apply();
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Log.i(getTagName(), "Connection failed: ConnectionResult.getErrorCode() = "
                + connectionResult.getErrorCode());
    }

    @Override
    public void onResult(LocationSettingsResult locationSettingsResult) {
        final Status status = locationSettingsResult.getStatus();
        switch (status.getStatusCode()) {
            case LocationSettingsStatusCodes.SUCCESS:
                Log.i(getTagName(), "All location settings are satisfied.");
                if (!search && !fromSplashScreenDownloading) {
                    getLocationFetch(true, true);
                    break;
                }

                break;
            case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                Log.i(getTagName(), "Location settings are not satisfied. Show the user a dialog to upgrade location settings ");

                try {
                    // Show the dialog by calling startResolutionForResult(), and check the result
                    // in onActivityResult().
                    status.startResolutionForResult(DiscoverActivity.this, REQUEST_CHECK_SETTINGS);
                } catch (IntentSender.SendIntentException e) {
                    Log.i(getTagName(), "PendingIntent unable to execute request.");
                }
                break;
            case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                Log.i(getTagName(), "Location settings are inadequate, and cannot be fixed here. Dialog not created.");
                buildAndShowSnackbarWithMessage("Please enable location services and retry again.");
                break;
        }
    }

    private void onSuccessDownload(ArrayList<Outlet> outlets) {
        if (outlets != null) { //success
            if (outlets.isEmpty()) {
                outletsNotFound = true;
                discoverAdapter.setOutletsNotFound(true);
            } else {
                if (outlets.size() < AppConstants.DISCOVER_LIST_PAGESIZE) {
                    outletsNotFound = true;
                    discoverAdapter.setOutletsNotFound(true);
                } else {
                    outletsNotFound = false;
                    discoverAdapter.setOutletsNotFound(false);
                }

                discoverAdapter.getItems().addAll(outlets);
                discoverAdapter.notifyDataSetChanged();
            }

            if (planAheadContent.getVisibility() == View.VISIBLE) {
                fabMenu.setVisibility(View.GONE);
            } else {
                fabMenu.setVisibility(View.VISIBLE);
            }

            findViewById(R.id.planAhead).setVisibility(View.VISIBLE);

            String lat = getPrefs().getString(AppConstants.PREFERENCE_CURRENT_USED_LAT,"");
            String lng = getPrefs().getString(AppConstants.PREFERENCE_CURRENT_USED_LNG,"");
            setTextLocationFetch(LOCATION_CURRENT, lat, lng, false);

        } else {
//            handleRetrofitError(new RetrofitError);
        }

        onItemsLoadComplete();
        hideProgressHUDInLayout();
    }

    private void unRegisterReceiver() {
        LocalBroadcastManager.getInstance(getApplicationContext()).unregisterReceiver(mReceiver);
    }

    private void registerReceiver() {
        LocalBroadcastManager.getInstance(getApplicationContext()).registerReceiver(mReceiver, mIntentFilter);
    }

    private IntentFilter mIntentFilter = new IntentFilter(AppConstants.INTENT_SPLASH_RECO_DOWNLOADED);

    private BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Bundle bundle = intent.getExtras();
            ArrayList<Outlet> outletList = (ArrayList<Outlet>) bundle.getSerializable(AppConstants.INTENT_SPLASH_OUTLET_LIST);
            onSuccessDownload(outletList);
            fromSplashScreenDownloading = false;
            unRegisterReceiver();
        }
    };

    private void sendBroadCastToSplashDownload() {
        Intent intent = new Intent(AppConstants.INTENT_DISCOVER_STARTED);
        LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(intent);
    }
}