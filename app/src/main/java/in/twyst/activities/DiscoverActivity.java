package in.twyst.activities;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.text.TextUtils;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.Transformation;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.balysv.materialripple.MaterialRippleLayout;
import com.getbase.floatingactionbutton.FloatingActionButton;
import com.getbase.floatingactionbutton.FloatingActionsMenu;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.location.places.ui.PlacePicker;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import in.twyst.R;
import in.twyst.adapters.DiscoverOutletAdapter;
import in.twyst.model.BaseResponse;
import in.twyst.model.DiscoverData;
import in.twyst.model.LocationData;
import in.twyst.model.Outlet;
import in.twyst.service.HttpService;
import in.twyst.util.AppConstants;
import in.twyst.util.Utils;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;


public class DiscoverActivity extends BaseActivity implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, ResultCallback<LocationSettingsResult> {
    private GoogleApiClient mGoogleApiClient;
    private static final int REQUEST_CHECK_SETTINGS = 0x1;
    private static final int PLACE_PICKER_REQUEST = 2;

    private ArrayAdapter<LocationData> mAdapter;

    private boolean fetchingOutlets;
    private boolean outletsNotFound;
    private DiscoverOutletAdapter discoverAdapter;

    private String placeNameSelected;
    private String latitudeStrSelected;
    private String longitudeStrSelected;

    private String placeNameSelectedAndUsed;
    private String latitudeStrSelectedAndUsed;
    private String longitudeStrSelectedAndUsed;
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
    private String date;
    private String time;
    TextView planAheadTime;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private RecyclerView mRecyclerView;
    private boolean firstLoad;
    int autoCompleteFlag ;

    Bitmap bitmap;
    private Date selectedDate;
    private ArrayList<LocationData> locations = new ArrayList<>(0);
    private LocationData selectedLocation;
    BroadcastReceiver broadcastReceiver;
    private static final int REQUEST_INVITE = 98;

    @Override
    protected String getTagName() {
        return this.getClass().getSimpleName();
    }

    @Override
    protected int getLayoutResource() {
        return R.layout.activity_discover;
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mGoogleApiClient != null && mGoogleApiClient.isConnected()) {
            mGoogleApiClient.disconnect();
        }
    }



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        HttpService.getInstance().getLocations(new Callback<BaseResponse<ArrayList<LocationData>>>() {
            @Override
            public void success(BaseResponse<ArrayList<LocationData>> arrayListBaseResponse, Response response) {

                locations = arrayListBaseResponse.getData();

                mGoogleApiClient = new GoogleApiClient.Builder(DiscoverActivity.this)
                        .addConnectionCallbacks(DiscoverActivity.this)
                        .addOnConnectionFailedListener(DiscoverActivity.this)
                        .addApi(Places.GEO_DATA_API)
                        .addApi(Places.PLACE_DETECTION_API)
                        .addApi(LocationServices.API)
                        .build();

                if (mGoogleApiClient != null) {
                    mGoogleApiClient.connect();
                }
            }

            @Override
            public void failure(RetrofitError error) {
                hideProgressHUDInLayout();
                handleRetrofitError(error);
            }
        });

        mRecyclerView = (RecyclerView) findViewById(R.id.outletRecyclerView);
        mRecyclerView.setHasFixedSize(true);

        LinearLayoutManager mLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        mRecyclerView.setLayoutManager(mLayoutManager);
        selectedLocationTxt = (TextView) findViewById(R.id.selectedLocationTxt);
        locationText =(TextView)findViewById(R.id.locationText);
        planAheadContent = findViewById(R.id.planAheadContent);
        editTextView =(TextView)findViewById(R.id.editTextView);
        final TextView planAheadLocation = (TextView) findViewById(R.id.planAheadLocation);
        fabMenu = (FloatingActionsMenu) findViewById(R.id.fabMenu);
        obstructor = (RelativeLayout) findViewById(R.id.obstructor);
        planAheadObstructor = (RelativeLayout) findViewById(R.id.planAheadObstructor);
        planAheadObstructor2 = (RelativeLayout) findViewById(R.id.planAheadObstructor2);

        submitFab = (FloatingActionButton)findViewById(R.id.submitFab);
        checkinFab = (FloatingActionButton)findViewById(R.id.checkinFab);

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

        findViewById(R.id.changeOnMapBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // showPlacesPicker();
                Intent intent = new Intent(DiscoverActivity.this, ChangeMapActivity.class);
                startActivityForResult(intent, PLACE_PICKER_REQUEST);
            }
        });

        mSwipeRefreshLayout = (SwipeRefreshLayout)findViewById(R.id.swipeRefreshLayout);

        mSwipeRefreshLayout.setColorScheme(R.color.button_orange);

        mSwipeRefreshLayout.setEnabled(false);

        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                // Refresh items
                if (TextUtils.isEmpty(date) && TextUtils.isEmpty(time)) {
                    fetchOutlets(1, latitudeStrSelectedAndUsed, longitudeStrSelectedAndUsed, null, null, true);
                } else {
                    fetchOutlets(1, latitudeStrSelectedAndUsed, longitudeStrSelectedAndUsed, date, time, true);
                }
            }
        });

        findViewById(R.id.planAheadSubmitBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (!TextUtils.isEmpty(placeNameSelected) || !TextUtils.isEmpty(latitudeStrSelected) || !TextUtils.isEmpty(longitudeStrSelected)) {
                    placeNameSelectedAndUsed = placeNameSelected;
                    latitudeStrSelectedAndUsed = latitudeStrSelected;
                    longitudeStrSelectedAndUsed = longitudeStrSelected;
                }

                selectedLocationTxt.setVisibility(View.VISIBLE);
                selectedLocationTxt.setText(placeNameSelectedAndUsed);

                collapse(planAheadContent);
                locationText.setVisibility(View.GONE);
                editTextView.setVisibility(View.VISIBLE);
                fabMenu.setVisibility(View.VISIBLE);
                fabMenu.setEnabled(true);

                findViewById(R.id.circularProgressBar).setVisibility(View.VISIBLE);
                Log.i("selected date", "= " + date + " time " + time);


                if(TextUtils.isEmpty(date) && TextUtils.isEmpty(time)){
                    fetchOutlets(1, latitudeStrSelectedAndUsed, longitudeStrSelectedAndUsed, null, null, true);
                }else{
                    fetchOutlets(1, latitudeStrSelectedAndUsed, longitudeStrSelectedAndUsed, date, time, true);
                }

            }
        });

        findViewById(R.id.planAheadCancelBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                collapse(planAheadContent);
                editTextView.setVisibility(View.VISIBLE);
                locationText.setVisibility(View.GONE);
                selectedLocationTxt.setVisibility(View.VISIBLE);

                fabMenu.setVisibility(View.VISIBLE);
                fabMenu.setEnabled(true);

                planAheadLocation.setText(placeNameSelectedAndUsed);
                fabMenu.setEnabled(true);
            }
        });

        discoverAdapter = new DiscoverOutletAdapter();

        mRecyclerView.setAdapter(discoverAdapter);
        discoverAdapter.setOnViewHolderListener(new DiscoverOutletAdapter.OnViewHolderListener() {
            @Override
            public void onRequestedLastItem() {
                if (!fetchingOutlets && !outletsNotFound) {
                    List<Outlet> outlets = discoverAdapter.getItems();
                    Log.d(getTagName(), "fetching more outlets, current size: " + outlets.size());
                    if (!TextUtils.isEmpty(date) && !TextUtils.isEmpty(time)) {
                        fetchOutlets(outlets.size() + 1, latitudeStrSelectedAndUsed, longitudeStrSelectedAndUsed, date, time, false);
                    } else if (TextUtils.isEmpty(date) && !TextUtils.isEmpty(time)) {
                        fetchOutlets(outlets.size() + 1, latitudeStrSelectedAndUsed, longitudeStrSelectedAndUsed, null, time, false);
                    } else if (!TextUtils.isEmpty(date) && TextUtils.isEmpty(time)) {
                        fetchOutlets(outlets.size() + 1, latitudeStrSelectedAndUsed, longitudeStrSelectedAndUsed, date, null, false);
                    } else {
                        fetchOutlets(outlets.size() + 1, latitudeStrSelectedAndUsed, longitudeStrSelectedAndUsed, null, null, false);
                    }
                }
            }
        });

        setupAsPlanAheadView();


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

//        planAheadObstructor.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//
//                Log.d(getTagName(), "planAheadObstructor clicked .... ");
//                togglePlanAhead();
//            }
//        });

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

        //fetchOutlets(1, "28.446398", "77.04344", false);
    }

    void onItemsLoadComplete() {

        // Stop refresh animation
        mSwipeRefreshLayout.setRefreshing(false);
    }

    private void togglePlanAhead() {
        if (planAheadContent.getVisibility() == View.VISIBLE) {

            collapse(planAheadContent);
            editTextView.setVisibility(View.VISIBLE);
            locationText.setVisibility(View.GONE);
            selectedLocationTxt.setVisibility(View.VISIBLE);

            fabMenu.setVisibility(View.VISIBLE);

        } else {
            expand(planAheadContent);

            editTextView.setVisibility(View.GONE);
            locationText.setVisibility(View.VISIBLE);
            selectedLocationTxt.setVisibility(View.GONE);


            fabMenu.setVisibility(View.GONE);
        }
    }

    private void setupAsPlanAheadView() {
        Calendar calendar = Calendar.getInstance();
        int dayOfWeekInt = calendar.get(Calendar.DAY_OF_WEEK);

        planAheadTime = (TextView) findViewById(R.id.planAheadTime);

        final TextView day1 = (TextView) findViewById(R.id.day1);
        day1.setText("today");
        final Date currentDateTime = calendar.getTime();
        day1.setTag(currentDateTime);
        date = (String) DateFormat.format("dd-MM-yyyy",currentDateTime );
        selectedDate = currentDateTime;

        day1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                deselectDays();
                day1.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
                day1.setTextColor(Color.WHITE);
                Log.d(getTagName(), "selected date: " + day1.getTag());
                date = (String) DateFormat.format("dd-MM-yyyy", currentDateTime);
                selectedDate = (Date) day1.getTag();
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
                date = (String) DateFormat.format("dd-MM-yyyy",(Date) day2.getTag() );
                Log.d(getTagName(), "selected date: " + day2.getTag()+" = "+date);
                selectedDate = (Date) day2.getTag();
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
                date = (String) DateFormat.format("dd-MM-yyyy",(Date) day3.getTag() );
                Log.d(getTagName(), "selected date: " + day3.getTag()+" = "+date);
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
                date = (String) DateFormat.format("dd-MM-yyyy",(Date) day4.getTag() );
                Log.d(getTagName(), "selected date: " + day4.getTag()+" = "+date);
                selectedDate = (Date) day4.getTag();
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
                date = (String) DateFormat.format("dd-MM-yyyy",(Date) day5.getTag() );
                Log.d(getTagName(), "selected date: " + day5.getTag()+" = "+date);
                selectedDate = (Date) day5.getTag();
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
                date = (String) DateFormat.format("dd-MM-yyyy",(Date) day6.getTag() );
                Log.d(getTagName(), "selected date: " + day6.getTag()+" = "+date);
                selectedDate = (Date) day6.getTag();
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
                date = (String) DateFormat.format("dd-MM-yyyy",(Date) day7.getTag() );
                Log.d(getTagName(), "selected date: " + day7.getTag()+" = "+date);
                selectedDate = (Date) day7.getTag();
            }
        });

        int hours = calendar.get(Calendar.HOUR);
        int minutes = calendar.get(Calendar.MINUTE);

        if (minutes > 30) {
            calendar.add(Calendar.HOUR, 1);
            hours = calendar.get(Calendar.HOUR);
        }

        int ampm = calendar.get(Calendar.AM_PM);

        time = convertHoursToString(hours) + ":" + convertMinutesToString(minutes);

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

                time = convertHoursToString(hours) + ":" + convertMinutesToString(minutes);
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

    private void showPlacesPicker() {

        try {
            PlacePicker.IntentBuilder builder = new PlacePicker.IntentBuilder();
            startActivityForResult(builder.build(this), PLACE_PICKER_REQUEST);
        } catch (Exception e) {
            e.printStackTrace();
        }
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

        mAdapter = new ArrayAdapter<LocationData>(this, R.layout.location_dropdown_item, locations);

        mAutocompleteView.setAdapter(mAdapter);

        builder.setView(dialogView);

        final AlertDialog dialog = builder.create();
        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();

        dialogView.findViewById(R.id.customLocationSubmitBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(autoCompleteFlag == 1) {

                    placeNameSelectedAndUsed = selectedLocation.toString();
                    latitudeStrSelectedAndUsed = selectedLocation.getLat();
                    longitudeStrSelectedAndUsed = selectedLocation.getLng();

                    SharedPreferences sharedPreferences = getSharedPreferences(AppConstants.PREFERENCE_SHARED_PREF_NAME, Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString(AppConstants.PREFERENCE_LAST_LOCATION_LATITUDE, latitudeStrSelectedAndUsed);
                    editor.putString(AppConstants.PREFERENCE_LAST_LOCATION_LONGITUDE, longitudeStrSelectedAndUsed);
                    editor.putString(AppConstants.PREFERENCE_LAST_LOCATION_NAME, placeNameSelectedAndUsed);
                    editor.commit();

                    selectedLocationTxt.setText(Html.fromHtml(placeNameSelectedAndUsed + " <b><font color=\"#ffffff\">(current)</font></b>"));

                    TextView planAheadLocation = (TextView) findViewById(R.id.planAheadLocation);
                    planAheadLocation.setText(Html.fromHtml(placeNameSelectedAndUsed + " <b><font color=\"#636363\">(current)</font></b>"));

                    findViewById(R.id.circularProgressBar).setVisibility(View.VISIBLE);
                    fetchOutlets(1, latitudeStrSelectedAndUsed, longitudeStrSelected, null, null, true);


                    dialog.dismiss();
                }else {
                    mAutocompleteView.setError("Address required");
                }
            }
        });
    }

    private void fetchOutlets(int start, String latitude, String longitude, String date, String time, final boolean clear) {
        Log.d(getTagName(), "Going to fetch outlets with, start: " + start + ", lat " + latitude + ", long: " + longitude + ", date: " + date + ", time: " + time);
        fetchingOutlets = true;
        mSwipeRefreshLayout.setEnabled(true);

        HttpService.getInstance().getRecommendedOutlets(getUserToken(), start, latitude, longitude, date, time, new Callback<BaseResponse<DiscoverData>>() {
            @Override
            public void success(BaseResponse<DiscoverData> arrayListBaseResponse, Response response) {
                fetchingOutlets = false;

                ArrayList<Outlet> outlets = arrayListBaseResponse.getData().getOutlets();
                String twystBucks = arrayListBaseResponse.getData().getTwystBucks();

                if(!TextUtils.isEmpty(twystBucks)) {
                    SharedPreferences.Editor sharedPreferences = getSharedPreferences(AppConstants.PREFERENCE_SHARED_PREF_NAME, Context.MODE_PRIVATE).edit();
                    sharedPreferences.putInt(AppConstants.PREFERENCE_LAST_TWYST_BUCK, Integer.parseInt(twystBucks));
                    sharedPreferences.commit();
                }

                if(outlets!=null) {
                    if (outlets.isEmpty()) {
                        outletsNotFound = true;
                        discoverAdapter.setOutletsNotFound(true);
                    } else {
                        if (clear) {
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

                if(planAheadContent.getVisibility()==View.VISIBLE){
                    fabMenu.setVisibility(View.GONE);
                }else {
                    fabMenu.setVisibility(View.VISIBLE);
                }

                findViewById(R.id.planAhead).setVisibility(View.VISIBLE);
            }

            @Override
            public void failure(RetrofitError error) {
                fetchingOutlets = false;

                error.getCause().printStackTrace();
                Log.i(getTagName(), "Error: error while trying to fetch outlets  :-" + error.getStackTrace().toString());
                hideProgressHUDInLayout();
                onItemsLoadComplete();
                handleRetrofitError(error);
            }
        });

    }

    private String getUserToken() {
        SharedPreferences prefs = this.getSharedPreferences(AppConstants.PREFERENCE_SHARED_PREF_NAME, Context.MODE_PRIVATE);
        return prefs.getString(AppConstants.PREFERENCE_USER_TOKEN, "");

    }

    @Override
    public void onConnected(Bundle bundle) {
        LocationRequest mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(5000);
        mLocationRequest.setFastestInterval(1000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                .addLocationRequest(mLocationRequest)
                .setAlwaysShow(true);

        LocationSettingsRequest locationSettingsRequest = builder.build();
        PendingResult<LocationSettingsResult> result =
                LocationServices.SettingsApi.checkLocationSettings(mGoogleApiClient, locationSettingsRequest);
        result.setResultCallback(this);
    }

    @Override
    public void onConnectionSuspended(int i) {
        mGoogleApiClient.connect();
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Log.i(getTagName(), "Connection failed: ConnectionResult.getErrorCode() = " + connectionResult.getErrorCode());
    }

    private void retrieveLocation() {
        Log.i(getTagName(), "retrieveLocation called isConnected: " + mGoogleApiClient.isConnected());
        Location mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);

        if (mLastLocation != null) {
            double latitude = mLastLocation.getLatitude();
            double longitude = mLastLocation.getLongitude();

            placeNameSelectedAndUsed = findNearestOutletName(latitude, longitude);
            latitudeStrSelectedAndUsed = String.valueOf(latitude);
            longitudeStrSelectedAndUsed = String.valueOf(longitude);


            SharedPreferences sharedPreferences = getSharedPreferences(AppConstants.PREFERENCE_SHARED_PREF_NAME, Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString(AppConstants.PREFERENCE_LAST_LOCATION_LATITUDE, latitudeStrSelectedAndUsed);
            editor.putString(AppConstants.PREFERENCE_LAST_LOCATION_LONGITUDE, longitudeStrSelectedAndUsed);
            editor.putString(AppConstants.PREFERENCE_LAST_LOCATION_NAME, placeNameSelectedAndUsed);
            editor.commit();

            TextView selectedLocationTxt = (TextView) findViewById(R.id.selectedLocationTxt);
            selectedLocationTxt.setText(placeNameSelectedAndUsed);

            TextView planAheadLocation = (TextView) findViewById(R.id.planAheadLocation);
            planAheadLocation.setText(Html.fromHtml(placeNameSelectedAndUsed + " <b><font color=\"#636363\">(current)</font></b>"));

            findViewById(R.id.circularProgressBar).setVisibility(View.VISIBLE);
            if(firstLoad){
                fetchOutlets(1, latitudeStrSelectedAndUsed, longitudeStrSelectedAndUsed,null, null, true);
            }

        } else {
            askLocationFromUser();
        }

    }

    private LocationData findNearestOutletLocation(double latitude, double longitude) {
        ArrayList<LocationData> locations2 = new ArrayList<>(locations);
        for (LocationData locationData : locations2) {
            double distance = Utils.distance(latitude, longitude, Double.valueOf(locationData.getLat()), Double.valueOf(locationData.getLng()), 'K');
            locationData.setDistance(distance);
            Log.d(getTagName(), "findNearestOutlet: distance: " + distance);
        }


        Log.d(getTagName(), "findNearestOutlet: before sort: " + locations2);
        Collections.sort(locations2, new Comparator<LocationData>() {
            @Override
            public int compare(LocationData locationData1, LocationData locationData2) {
                return Double.valueOf(locationData1.getDistance()).compareTo(Double.valueOf(locationData2.getDistance()));
            }
        });
        Log.d(getTagName(), "findNearestOutlet: after sort: " + locations2);

        LocationData minLocation = locations2.get(0);
        return minLocation;
    }

    private String findNearestOutletName(double latitude, double longitude) {

        LocationData minLocation = findNearestOutletLocation(latitude, longitude);
        return minLocation.toString();
    }


    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.d(getTagName(), "onActivityResult: requestCode: " + requestCode + ", resultcode: " + resultCode);

        if (requestCode == PLACE_PICKER_REQUEST) {
            if (resultCode == RESULT_OK) {

                Bundle extras = data.getExtras();
                LocationData locationData = (LocationData) extras.getSerializable("locationData");
                Double lat = Double.valueOf(locationData.getLat());
                Double lng = Double.valueOf(locationData.getLng());
                LocationData nearestOutletLocation =  findNearestOutletLocation(lat, lng);
                String locationName = nearestOutletLocation.toString();

                placeNameSelected = locationName;
                latitudeStrSelected = nearestOutletLocation.getLat();
                longitudeStrSelected = nearestOutletLocation.getLng();

                Log.d(getTagName(), "onActivityResult.. places picker: placeName: " + locationName);

                TextView planAheadLocation = (TextView) findViewById(R.id.planAheadLocation);
                planAheadLocation.setText(locationName);
            }
        }

        if (requestCode == REQUEST_CHECK_SETTINGS) {
            switch (resultCode) {
                case Activity.RESULT_OK:
                    Log.i(getTagName(), "User agreed to make required location settings changes.");
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            retrieveLocation();
                        }
                    }, 2500);
                    break;
                case Activity.RESULT_CANCELED:
                    Log.i(getTagName(), "User chose not to make required location settings changes.");
                    //buildAndShowSnackbarWithMessage("Please enable location services and retry again.");
                    askLocationFromUser();
                    break;
            }
        }

    }


    @Override
    public void onResult(LocationSettingsResult locationSettingsResult) {
        final Status status = locationSettingsResult.getStatus();
        switch (status.getStatusCode()) {
            case LocationSettingsStatusCodes.SUCCESS:
                Log.i(getTagName(), "All location settings are satisfied.");
                retrieveLocation();
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

   /* @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ((keyCode == KeyEvent.KEYCODE_BACK))
        {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Are you sure you want to exit ?")
                    .setPositiveButton("YES", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                            finish();
                        }
                    })
                    .setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            // User cancelled the dialog
                        }
                    });
            builder.create().show();

        }
        return super.onKeyDown(keyCode, event);

    }*/

    @Override
    protected void onResume() {
        super.onResume();
        if(firstLoad) {
            fetchOutlets(1, latitudeStrSelectedAndUsed, longitudeStrSelectedAndUsed, date, time, true);
        }else {
            firstLoad = true;
        }
    }

    public void expand(final View v) {
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
                        : (int)(targetHeight * interpolatedTime);
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

    public void collapse(final View v) {
        final int initialHeight = v.getMeasuredHeight();
        final AlphaAnimation animation1 = new AlphaAnimation(0.7f, 0.0f);
        animation1.setDuration(75);
        //animation1.setStartOffset(10);
        planAheadObstructor.setVisibility(View.GONE);
        planAheadObstructor2.setVisibility(View.GONE);
        Animation a = new Animation()
        {
            @Override
            protected void applyTransformation(float interpolatedTime, Transformation t) {
                if(interpolatedTime == 1){
                    v.setVisibility(View.GONE);
                }else{
                    v.getLayoutParams().height = initialHeight - (int)(initialHeight * interpolatedTime);
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

    @Override
    public void onBackPressed() {
        if(obstructor.getVisibility()==View.VISIBLE){
            fabMenu.collapse();

        }else if( findViewById(R.id.checkinObstructor).getVisibility() == View.VISIBLE){
            fabMenu.collapse();
            findViewById(R.id.checkinObstructor).setVisibility(View.GONE);
            findViewById(R.id.checkinObstructor2).setVisibility(View.GONE);

        } else if (drawerOpened) {
            drawerLayout.closeDrawer(Gravity.LEFT);
        } else {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Are you sure you want to exit ?")
                    .setPositiveButton("YES", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                            finish();
                        }
                    })
                    .setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            // User cancelled the dialog
                        }
                    });
            builder.create().show();
        }

    }
}
