package com.twyst.app.android.activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.view.GravityCompat;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

import com.appsflyer.AppsFlyerLib;
import com.twyst.app.android.R;
import com.twyst.app.android.model.BaseResponse;
import com.twyst.app.android.model.Profile;
import com.twyst.app.android.service.HttpService;
import com.twyst.app.android.util.AppConstants;
import com.twyst.app.android.util.TwystProgressHUD;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by vivek on 05/08/15.
 */
public class InviteFriendsActivity extends BaseActivity {

    private List<Profile.FriendLists> friendLists;
    private boolean fromDrawer;

    @Override
    protected String getTagName() {
        return null;
    }

    @Override
    protected int getLayoutResource() {
        return R.layout.activity_invite_friends;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setupAsChild= true;
        super.onCreate(savedInstanceState);

        final SharedPreferences prefs = this.getSharedPreferences(AppConstants.PREFERENCE_SHARED_PREF_NAME, Context.MODE_PRIVATE);
        int bucksTDisplay = prefs.getInt(AppConstants.PREFERENCE_TWYST_BUCKS_INVITE_FRIENDS, AppConstants.TWYST_BUCKS_INVITE_FRIENDS);
        String inviteText = this.getResources().getString(R.string.invite_friends_text);
        String inviteTextFormatted = String.format(inviteText, bucksTDisplay);
        TextView textView1 = (TextView) findViewById(R.id.textView1);
        textView1.setText(inviteTextFormatted);

        fromDrawer = getIntent().getBooleanExtra(AppConstants.INTENT_PARAM_FROM_DRAWER, false);

        final ListView inviteTwystList = (ListView)findViewById(R.id.inviteTwystList);

        final TwystProgressHUD twystProgressHUD = TwystProgressHUD.show(this, false, null);

        HttpService.getInstance().getProfile(getUserToken(), new Callback<BaseResponse<Profile>>() {
            @Override
            public void success(BaseResponse<Profile> friendDataBaseResponse, Response response) {
                twystProgressHUD.dismiss();
                if (friendDataBaseResponse.isResponse()) {
                    if (friendDataBaseResponse.getData() != null) {
                        friendLists = friendDataBaseResponse.getData().getTwystFriendLists();
                        if(friendLists.size()>0) {

                            inviteTwystList.setAdapter(new ArrayAdapter<Profile.FriendLists>(InviteFriendsActivity.this, R.layout.listview_item_invite_contact, R.id.contactName, friendLists));

                        }else {
                            Toast.makeText(InviteFriendsActivity.this, "Friend list is empty!", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
            }

            @Override
            public void failure(RetrofitError error) {
                twystProgressHUD.dismiss();
                handleRetrofitError(error);
            }
        });



        findViewById(R.id.inviteBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences prefs = getSharedPreferences(AppConstants.PREFERENCE_SHARED_PREF_NAME, Context.MODE_PRIVATE);
                String phone = prefs.getString(AppConstants.PREFERENCE_USER_PHONE, "");
                String referral = "promotion%3Dappinvite%26code%3D"+phone;
                String text = "Hey! I wanted to tell you about Twyst - a great app that shows you the best offers and deals on food and drink at places around you. I found it super useful and I think you will too! Download it using the link below and get 500 Twyst Bucks when you start - https://play.google.com/store/apps/details?id="+getApplication().getPackageName()+"&referrer="+referral;
//                String text = "Hey! I wanted to tell you about Twyst - a great app that shows you the best offers and deals on food and drink at places around you. I found it super useful and I think you will too! Download it using the link below and get 500 Twyst Bucks when you start - https://play.google.com/store/apps/details?id="+getApplication().getPackageName()+"&ah=-smMDxRK7pmXEK32N7mSNcbZ2ZM&referrer="+referral;

                showShareIntents("Invite using", text);
            }
        });


        hideProgressHUDInLayout();
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
