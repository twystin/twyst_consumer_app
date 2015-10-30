package com.twyst.app.android.activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.view.GravityCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.appsflyer.AppsFlyerLib;
import com.getbase.floatingactionbutton.FloatingActionButton;
import com.getbase.floatingactionbutton.FloatingActionsMenu;

import java.util.ArrayList;
import java.util.List;

import com.twyst.app.android.R;
import com.twyst.app.android.adapters.NotificationAdapter;
import com.twyst.app.android.model.BaseResponse;
import com.twyst.app.android.model.NotificationData;
import com.twyst.app.android.service.HttpService;
import com.twyst.app.android.util.AppConstants;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by rahuls on 23/7/15.
 */
public class NotificationActivity extends BaseActivity{

    private RecyclerView notifyRecyclerView;
    NotificationAdapter adapter;
    private FloatingActionButton submitFab;
    private FloatingActionButton checkinFab;
    private FloatingActionsMenu fabMenu;
    private RelativeLayout obstructor;
    private boolean fromDrawer, fromPushNotificationClicked;

    @Override
    protected String getTagName() {
        return null;
    }

    @Override
    protected int getLayoutResource() {
        return R.layout.activity_notification;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setupAsChild= true;
        super.onCreate(savedInstanceState);

        fromDrawer = getIntent().getBooleanExtra(AppConstants.INTENT_PARAM_FROM_DRAWER, false);
        notifyRecyclerView = (RecyclerView) findViewById(R.id.notifyRecyclerView);
        notifyRecyclerView.setHasFixedSize(true);

        LinearLayoutManager mLayoutManager = new LinearLayoutManager(NotificationActivity.this, LinearLayoutManager.VERTICAL, false);
        notifyRecyclerView.setLayoutManager(mLayoutManager);

        adapter = new NotificationAdapter();
		notifyRecyclerView.setAdapter(adapter);

        fabMenu = (FloatingActionsMenu) findViewById(R.id.fabMenu);
        obstructor = (RelativeLayout) findViewById(R.id.obstructor);
        submitFab = (FloatingActionButton)findViewById(R.id.submitFab);
        checkinFab = (FloatingActionButton)findViewById(R.id.checkinFab);

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
                        Intent intent = new Intent(NotificationActivity.this, ScannerActivity.class);
                        startActivity(intent);
                    }
                }, 100);
            }
        });

        submitFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (obstructor.getVisibility() == View.VISIBLE) {
                    Intent intent = new Intent(NotificationActivity.this, SubmitOfferActivity.class);
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
                        Intent intent = new Intent(NotificationActivity.this, ScannerActivity.class);
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

                        Intent intent = new Intent(NotificationActivity.this, UploadBillActivity.class);
                        intent.setAction("setChildYes");
                        startActivity(intent);
                    }
                }, 100);
            }
        });

        getNotifications();

    }

    @Override
    public void onResume() {
        super.onResume();
        AppsFlyerLib.onActivityResume(this);
    }
    @Override
    public void onPause() {
        super.onPause();
        AppsFlyerLib.onActivityPause(this);
    }

    private void getNotifications(){
        HttpService.getInstance().getNotification(getUserToken(), new Callback<BaseResponse<ArrayList<NotificationData>>>() {
            @Override
            public void success(BaseResponse<ArrayList<NotificationData>> notificationDataBaseResponse, Response response) {
                hideProgressHUDInLayout();
                if(notificationDataBaseResponse.isResponse()){
                    List<NotificationData> notificationData = notificationDataBaseResponse.getData();
                    if(notificationData.size()>0){
                        adapter.setItems(notificationData);
                        adapter.notifyDataSetChanged();

                        findViewById(R.id.blankDataLayout).setVisibility(View.GONE);

                    }else {
                        findViewById(R.id.ivNoData).setBackgroundResource(R.drawable.no_notification);
                        ((TextView) findViewById(R.id.tvNoData)).setText("You have no notifications!");
                        findViewById(R.id.blankDataLayout).setVisibility(View.VISIBLE);
//                        Toast.makeText(NotificationActivity.this,"You have no notifications!",Toast.LENGTH_SHORT).show();
                    }

                }else {

                    Toast.makeText(NotificationActivity.this,notificationDataBaseResponse.getMessage(),Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void failure(RetrofitError error) {
                hideProgressHUDInLayout();
                if (error.getKind() == RetrofitError.Kind.NETWORK) {
                    handleRetrofitError(error);
                } else {
                    buildAndShowSnackbarWithMessage("Unable to load notifications!");
                }
            }
        });
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        fromDrawer = intent.getBooleanExtra(AppConstants.INTENT_PARAM_FROM_DRAWER, false);
        fromPushNotificationClicked = intent.getBooleanExtra(AppConstants.INTENT_PARAM_FROM_PUSH_NOTIFICATION_CLICKED, false);
        if (fromPushNotificationClicked){
            getNotifications();
        }

    }


    @Override
    public void onBackPressed() {
        if (drawerOpened) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            if (fromDrawer || fromPushNotificationClicked) {
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
