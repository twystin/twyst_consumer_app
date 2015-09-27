package in.twyst.activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.view.GravityCompat;
import android.text.TextUtils;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import in.twyst.R;
import in.twyst.model.BaseResponse;
import in.twyst.model.Profile;
import in.twyst.service.HttpService;
import in.twyst.util.AppConstants;
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

        fromDrawer = getIntent().getBooleanExtra(AppConstants.INTENT_PARAM_FROM_DRAWER, false);

        final ListView inviteTwystList = (ListView)findViewById(R.id.inviteTwystList);


        HttpService.getInstance().getProfile(getUserToken(), new Callback<BaseResponse<Profile>>() {
            @Override
            public void success(BaseResponse<Profile> friendDataBaseResponse, Response response) {
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
