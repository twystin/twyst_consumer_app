package in.twyst.activities;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import in.twyst.R;
import in.twyst.model.BaseResponse;
import in.twyst.model.FriendData;
import in.twyst.model.GetFriend;
import in.twyst.service.HttpService;
import in.twyst.util.AppConstants;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by vivek on 05/08/15.
 */
public class InviteFriendsActivity extends BaseActivity {

    private List<FriendData.FriendLists> friendLists;

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
        final ListView inviteTwystList = (ListView)findViewById(R.id.inviteTwystList);


        HttpService.getInstance().getProfile(getUserToken(), new Callback<BaseResponse<GetFriend>>() {
            @Override
            public void success(BaseResponse<GetFriend> friendDataBaseResponse, Response response) {

                if (friendDataBaseResponse.isResponse()) {
                    if (friendDataBaseResponse.getData() != null) {

                        FriendData friendData = friendDataBaseResponse.getData().getFriendData();
                        if (friendData.getList() != null) {
                            friendLists = friendData.getList();

                            inviteTwystList.setAdapter(new ArrayAdapter<FriendData.FriendLists>(InviteFriendsActivity.this, R.layout.listview_item_invite_contact, R.id.contactName, friendLists));
                        } else {
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
                String text = "Twyst App: https://play.google.com/store/apps/details?id="+getApplication().getPackageName()+"&ah=-smMDxRK7pmXEK32N7mSNcbZ2ZM&referrer="+referral;

                showShareIntents("Invite using", text);
            }
        });


        hideProgressHUDInLayout();
    }

    private String getUserToken() {
        SharedPreferences prefs = this.getSharedPreferences(AppConstants.PREFERENCE_SHARED_PREF_NAME, Context.MODE_PRIVATE);
        return prefs.getString(AppConstants.PREFERENCE_USER_TOKEN, "");

    }
}
