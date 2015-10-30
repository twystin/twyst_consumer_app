package com.twyst.app.android.activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.appsflyer.AppsFlyerLib;
import com.facebook.AccessToken;
import com.facebook.AccessTokenTracker;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphRequestBatch;
import com.facebook.GraphResponse;
import com.facebook.Profile;
import com.facebook.ProfileTracker;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.CommonStatusCodes;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.plus.People;
import com.google.android.gms.plus.Plus;
import com.google.android.gms.plus.model.people.Person;
import com.google.android.gms.plus.model.people.PersonBuffer;
import com.google.gson.Gson;
import com.nispok.snackbar.Snackbar;
import com.nispok.snackbar.SnackbarManager;
import com.nispok.snackbar.enums.SnackbarType;
import com.nispok.snackbar.listeners.ActionClickListener;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.regex.Pattern;
import com.twyst.app.android.R;
import com.twyst.app.android.model.BaseResponse;
import com.twyst.app.android.model.Friend;
import com.twyst.app.android.model.ProfileUpdate;
import com.twyst.app.android.model.Referral;
import com.twyst.app.android.model.ReferralMeta;
import com.twyst.app.android.model.UpdateProfile;
import com.twyst.app.android.service.HttpService;
import com.twyst.app.android.util.AppConstants;
import com.twyst.app.android.util.PhoneBookContacts;
import com.twyst.app.android.util.TwystProgressHUD;
import com.twyst.app.android.util.Utils;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by satish on 06/06/15.
 */
public class LoginActivity extends Activity implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    private String getTagName() {
        return this.getClass().getSimpleName();
    }

    private CallbackManager callbackManager;

    private EditText email;
    private String userImage;
    private String firstName;
    private String middleName;
    private String lastName;
    private String dob;
    private String city;
    private String source = "phonebook";
    private String socialEmail;
    private String id,fbid,linkUri;
    private Friend friend;
    private List<Friend.Friends> friendsList;
    private Friend.Friends friends;
    private ProfileTracker profileTracker;
    private AccessTokenTracker accessTokenTracker;
    private SharedPreferences.Editor sharedPreferences;


    //Google +
    /* Request code used to invoke sign in user interactions. */
    private static final int RC_SIGN_IN = 0;

    private GoogleApiClient mGoogleApiClient;

    /* Is there a ConnectionResult resolution in progress? */
    private boolean mIsResolving = false;

    /* Should we automatically resolve ConnectionResults when possible? */
    private boolean mShouldResolve = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        FacebookSdk.sdkInitialize(getApplicationContext());

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_login);

        callbackManager = CallbackManager.Factory.create();

        sharedPreferences = getSharedPreferences(AppConstants.PREFERENCE_SHARED_PREF_NAME, Context.MODE_PRIVATE).edit();
        LoginManager.getInstance().registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {

                AccessToken accessToken = loginResult.getAccessToken();

                GraphRequestBatch batch = new GraphRequestBatch(GraphRequest.newMeRequest(accessToken, new GraphRequest.GraphJSONObjectCallback() {
                    @Override
                    public void onCompleted(JSONObject jsonObject, GraphResponse response) {

                        try {
                            id = jsonObject.getString("id");
                            socialEmail = jsonObject.getString("email");

                        } catch (JSONException jsone) {
                            jsone.printStackTrace();
                        }
                        try {
                            dob = jsonObject.getString("birthday");

                        } catch (JSONException jsone) {
                            jsone.printStackTrace();
                        }

                    }
                }),
                        GraphRequest.newMyFriendsRequest(accessToken, new GraphRequest.GraphJSONArrayCallback() {
                            @Override
                            public void onCompleted(JSONArray jsonArray, GraphResponse response) {

                                friendsList = new ArrayList<Friend.Friends>();

                                for (int i = 0; i < jsonArray.length(); i++) {
                                    try {
                                        friends = new Friend.Friends();
                                        friends.setId(jsonArray.getJSONObject(i).getString("id"));
                                        friends.setName(jsonArray.getJSONObject(i).getString("name"));
                                        friends.setPhone(null);
                                        friendsList.add(friends);
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                }
                            }
                        })
                );
                batch.addCallback(new GraphRequestBatch.Callback() {
                    @Override
                    public void onBatchCompleted(GraphRequestBatch graphRequests) {
                        // Application code for when the batch finishes
                        updateUserEmail();

                    }
                });
                batch.executeAsync();


            }

            @Override
            public void onCancel() {
                System.out.println("LoginActivity.onCancel called");
            }

            @Override
            public void onError(FacebookException e) {
                e.printStackTrace();
            }
        });

        profileTracker = new ProfileTracker() {
            @Override
            protected void onCurrentProfileChanged(Profile oldProfile, Profile currentProfile) {
                Profile.fetchProfileForCurrentAccessToken();

                userImage = String.valueOf(currentProfile.getProfilePictureUri(250, 250));
                firstName = currentProfile.getFirstName();
                middleName = currentProfile.getMiddleName();
                lastName = currentProfile.getLastName();
                fbid = currentProfile.getId();
                linkUri = String.valueOf(currentProfile.getLinkUri());
                sharedPreferences.putString(AppConstants.PREFERENCE_USER_PIC, String.valueOf(currentProfile.getProfilePictureUri(250, 250)));
                sharedPreferences.putString(AppConstants.PREFERENCE_USER_NAME, currentProfile.getFirstName());
                sharedPreferences.putString(AppConstants.PREFERENCE_USER_FULL_NAME,currentProfile.getName());
                sharedPreferences.putBoolean(AppConstants.PREFERENCE_IS_FACEBOOK_CONNECTED,true);
                sharedPreferences.apply();
            }

        };



        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(Plus.API)
                .addScope(Plus.SCOPE_PLUS_LOGIN)
                .build();


        final String EMAIL_REGEX = "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";
        email = (EditText) findViewById(R.id.email);
        ImageView fbLogin = (ImageView) findViewById(R.id.fbLogin);

        findViewById(R.id.loginBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (TextUtils.isEmpty(email.getText().toString()) || !Pattern.matches(EMAIL_REGEX, email.getText().toString())) {
                    email.setError("Invalid email");
                } else {
                    email.setError(null);
                    socialEmail = email.getText().toString();
                    source = "phonebook";
                    userImage = "";
                    firstName = "";
                    middleName = "";
                    lastName = "";
                    dob = "";
                    id = "";
                    final SharedPreferences prefs = getSharedPreferences(AppConstants.PREFERENCE_SHARED_PREF_NAME, Context.MODE_PRIVATE);
                    friendsList = PhoneBookContacts.getInstance().getPhoneContactList();
                    sharedPreferences = getSharedPreferences(AppConstants.PREFERENCE_SHARED_PREF_NAME, Context.MODE_PRIVATE).edit();
                    sharedPreferences.putString(AppConstants.PREFERENCE_USER_PIC, "");
                    sharedPreferences.putString(AppConstants.PREFERENCE_USER_NAME, prefs.getString(AppConstants.PREFERENCE_USER_PHONE,""));
                    sharedPreferences.putString(AppConstants.PREFERENCE_USER_FULL_NAME, prefs.getString(AppConstants.PREFERENCE_USER_PHONE,""));
                    sharedPreferences.apply();
                    updateUserEmail();

                }
            }
        });

        fbLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                source = "FACEBOOK";
                facebookLogin();
            }
        });

        ImageView gPlusLogin = (ImageView) findViewById(R.id.gPlusLogin);
        gPlusLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                source = "GOOGLE";

                // User clicked the sign-in button, so begin the sign-in process and automatically
                // attempt to resolve any errors that occur.
                mShouldResolve = true;
                mGoogleApiClient.connect();
            }
        });

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

    private void showDiscoverScreen() {
        sharedPreferences.putBoolean(AppConstants.PREFERENCE_EMAIL_VERIFIED, true);
        sharedPreferences.commit();

        Intent intent = new Intent(getBaseContext(), DiscoverActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.setAction("setChildNo");
        intent.putExtra("Search", false);
        startActivity(intent);
        finish();
    }

    private void updateUserEmail() {
        final SharedPreferences prefs = getSharedPreferences(AppConstants.PREFERENCE_SHARED_PREF_NAME, Context.MODE_PRIVATE);
        final String token = prefs.getString(AppConstants.PREFERENCE_USER_TOKEN, "");
        String deviceId = null;

        deviceId = prefs.getString(AppConstants.PREFERENCE_REGISTRATION_ID,"");


            final TwystProgressHUD twystProgressHUD = TwystProgressHUD.show(this, false, null);
            final String email;
            if(TextUtils.isEmpty(socialEmail)){
                email = "";
            }else {
                email = socialEmail;
            }
        city = prefs.getString(AppConstants.PREFERENCE_LOCALITY_SHOWN_DRAWER,"");
        Log.i("dob", dob + " firstName" + firstName + " lastName" + lastName + " middleName" + middleName + " city" + city + " image" + userImage);
        String facebookUri = null;
        String googleplusUri = null;
        if(source.equalsIgnoreCase("FACEBOOK")) {
            facebookUri = linkUri;

        }else if(source.equalsIgnoreCase("GOOGLE")){
            googleplusUri = linkUri;
        }

        UpdateProfile updateProfile = new UpdateProfile();
        updateProfile.setEmail(email);
        updateProfile.setImage(userImage);
        updateProfile.setFname(firstName);
        updateProfile.setMname(middleName);
        updateProfile.setLname(lastName);
        updateProfile.setCity(city);
        updateProfile.setId(id);
        updateProfile.setSource(source);
        updateProfile.setFacebookUri(facebookUri);
        updateProfile.setGoogleplusUri(googleplusUri);
        updateProfile.setDeviceId(deviceId);
        updateProfile.setVersion(Utils.getbuildVersionStringBuilder().toString());
        updateProfile.setDevice(android.os.Build.DEVICE);
        updateProfile.setModel(android.os.Build.MODEL);
        updateProfile.setProduct(android.os.Build.PRODUCT);

        HttpService.getInstance().updateProfile(token, updateProfile, new Callback<BaseResponse<ProfileUpdate>>() {
            @Override
            public void success(BaseResponse<ProfileUpdate> loginDataBaseResponse, Response response) {
                twystProgressHUD.dismiss();
                if (loginDataBaseResponse.isResponse()) {
                    final String code = prefs.getString(AppConstants.PREFERENCE_USER_REFERRAL, "");
                    if (!TextUtils.isEmpty(code)) {
                        postReferral(token, code);
                    } else {
                        saveSocialFriendListLocally(token);
                    }

                } else {
                    Log.d(getTagName(), "" + loginDataBaseResponse.getMessage());
                }
            }

            @Override
            public void failure(RetrofitError error) {
                twystProgressHUD.dismiss();
                handleRetrofitError(error);
            }
        });
    }

    private void postReferral(final String token, final String code){
        final TwystProgressHUD twystProgressHUD = TwystProgressHUD.show(this, false, null);
        Referral referral = new Referral();
        ReferralMeta referralMeta = new ReferralMeta();
        referralMeta.setReferralCode(code);
        referralMeta.setSource(source);
        referral.setReferralMeta(referralMeta);

        HttpService.getInstance().postReferral(token, referral, new Callback<BaseResponse>() {
            @Override
            public void success(BaseResponse baseResponse, Response response) {
                twystProgressHUD.dismiss();
                saveSocialFriendListLocally(token);
//                Toast.makeText(LoginActivity.this,"Referrer code "+"( "+code+" )"+" used successfully!",Toast.LENGTH_LONG).show();
            }

            @Override
            public void failure(RetrofitError error) {
                twystProgressHUD.dismiss();
                handleRetrofitError(error);
            }
        });
    }

    private void saveSocialFriendListLocally(String token) {
        friend = new Friend();
        friend.setSource(source);
        friend.setList(friendsList);

        Gson gson = new Gson();
        String json = gson.toJson(friend);

        sharedPreferences = getSharedPreferences(AppConstants.PREFERENCE_SHARED_PREF_NAME, Context.MODE_PRIVATE).edit();
        sharedPreferences.putString(AppConstants.PREFERENCE_FRIEND_LIST, json);
        if (sharedPreferences.commit()){
            showDiscoverScreen();
        }

    }


    public void facebookLogin() {

        Collection<String> permissions = Arrays.asList("email", "user_friends", "public_profile","user_birthday");
        LoginManager.getInstance().logInWithReadPermissions(this, permissions);

    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {
            // If the error resolution was not successful we should not resolve further.
            if (resultCode != RESULT_OK) {
                mShouldResolve = false;
            }

            mIsResolving = false;
            mGoogleApiClient.connect();
        }

    }


    @Override
    public void onConnected(Bundle bundle) {

        // onConnected indicates that an account was selected on the device, that the selected
        // account has granted any requested permissions to our app and that we were able to
        // establish a service connection to Google Play services.
        Log.d(getTagName(), "onConnected:" + bundle);
        mShouldResolve = false;

        // Show the signed-in UI
        socialEmail = Plus.AccountApi.getAccountName(mGoogleApiClient);

        System.out.println("LoginActivity.onConnected google email : " + email);

        try {
            if (Plus.PeopleApi.getCurrentPerson(mGoogleApiClient) != null) {
                Person currentPerson = Plus.PeopleApi
                        .getCurrentPerson(mGoogleApiClient);
                String personFullName =currentPerson.getDisplayName();
                String personName = currentPerson.getName().getGivenName();
                String personPhotoUrl = currentPerson.getImage().getUrl();
                linkUri = currentPerson.getUrl();
                Log.i("LoginActivity", "Name: " + personFullName
                        + ", Image: " + personPhotoUrl);

                userImage = personPhotoUrl;
                firstName = personName;
                middleName = currentPerson.getName().getMiddleName();
                lastName = currentPerson.getName().getFamilyName();
                dob = currentPerson.getBirthday();
                id = currentPerson.getId();
                sharedPreferences = getSharedPreferences(AppConstants.PREFERENCE_SHARED_PREF_NAME, Context.MODE_PRIVATE).edit();
                sharedPreferences.putBoolean(AppConstants.PREFERENCE_IS_GOOGLE_CONNECTED, true);
                sharedPreferences.putString(AppConstants.PREFERENCE_USER_PIC, personPhotoUrl);
                sharedPreferences.putString(AppConstants.PREFERENCE_USER_NAME,personName);
                sharedPreferences.putString(AppConstants.PREFERENCE_USER_FULL_NAME,personFullName);
                sharedPreferences.apply();
//

            }
        }catch (Exception e) {
            e.printStackTrace();
        }

        Plus.PeopleApi.loadVisible(mGoogleApiClient, null).setResultCallback(new ResultCallback<People.LoadPeopleResult>() {
            @Override
            public void onResult(People.LoadPeopleResult loadPeopleResult) {
                if (loadPeopleResult.getStatus().getStatusCode() == CommonStatusCodes.SUCCESS) {
                    PersonBuffer personBuffer = loadPeopleResult.getPersonBuffer();
                    friendsList = new ArrayList<Friend.Friends>();
                    try {
                        int count = personBuffer.getCount();
                        for (int i = 0; i < count; i++) {
                            Log.d(getTagName(), "Person " + i + " name: " + personBuffer.get(i).getDisplayName() + " - id: " + personBuffer.get(i).getId());
                            friends = new Friend.Friends();
                            friends.setId(personBuffer.get(i).getId());
                            friends.setName(personBuffer.get(i).getDisplayName());
                            friends.setPhone(null);
                            friendsList.add(friends);
                        }

                        updateUserEmail();

                    } finally {
                        personBuffer.close();
                    }


                } else {
                    Log.e(getTagName(), "Error while getting friends from G+");
                }
            }
        });
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
// Could not connect to Google Play Services.  The user needs to select an account,
        // grant permissions or resolve an error in order to sign in. Refer to the javadoc for
        // ConnectionResult to see possible error codes.
        Log.d(getTagName(), "onConnectionFailed:" + connectionResult);

        if (!mIsResolving && mShouldResolve) {
            if (connectionResult.hasResolution()) {
                try {
                    connectionResult.startResolutionForResult(this, RC_SIGN_IN);
                    mIsResolving = true;
                } catch (IntentSender.SendIntentException e) {
                    Log.e(getTagName(), "Could not resolve ConnectionResult.", e);
                    mIsResolving = false;
                    mGoogleApiClient.connect();
                }
            } else {
                // Could not resolve the connection result, show the user an
                // error dialog.
                //showErrorDialog(connectionResult);
            }
        } else {
            // Show the signed-out UI
            //showSignedOutUI();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
       // mGoogleApiClient.connect();
    }

    @Override
    protected void onStop() {
        super.onStop();

        if (mGoogleApiClient.isConnected()) {
            mGoogleApiClient.disconnect();
        }
    }

    public void handleRetrofitError(RetrofitError error) {
        if (error.getKind() == RetrofitError.Kind.NETWORK) {
            buildAndShowSnackbarWithMessage("No internet connection.");
        } else {
            buildAndShowSnackbarWithMessage("An unexpected error has occurred. Please try after some time.");
        }
        Log.e(getTagName(), "failure", error);
    }

    public void buildAndShowSnackbarWithMessage(String msg) {
        final Snackbar snackbar = Snackbar.with(getApplicationContext())
                .type(SnackbarType.MULTI_LINE)
//                .color(getResources().getColor(R.color.snackbar_bg))
                .text(msg)
                .actionLabel("RETRY") // action button label
                .duration(Snackbar.SnackbarDuration.LENGTH_INDEFINITE)
                .swipeToDismiss(false)
                .actionListener(new ActionClickListener() {
                    @Override
                    public void onActionClicked(Snackbar snackbar) {
                        Intent intent = getIntent();
                        overridePendingTransition(0, 0);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                        finish();
                        overridePendingTransition(0, 0);
                        startActivity(intent);
                    }
                });
        snackbar.postDelayed(new Runnable() {
            @Override
            public void run() {
                showSnackbar(snackbar); // activity where it is displayed
            }
        }, 500);

    }

    protected void showSnackbar(Snackbar snackbar) {
        SnackbarManager.show(snackbar, this);
    }


}
