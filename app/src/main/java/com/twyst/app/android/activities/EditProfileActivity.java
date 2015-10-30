package com.twyst.app.android.activities;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.view.GravityCompat;
import android.support.v7.widget.SwitchCompat;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
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
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.List;
import java.util.regex.Pattern;

import com.twyst.app.android.R;
import com.twyst.app.android.model.BaseResponse;
import com.twyst.app.android.model.EventDate;
import com.twyst.app.android.model.Friend;
import com.twyst.app.android.model.LifeEvents;
import com.twyst.app.android.model.Profile;
import com.twyst.app.android.model.ProfileUpdate;
import com.twyst.app.android.model.UpdateProfile;
import com.twyst.app.android.service.HttpService;
import com.twyst.app.android.util.AppConstants;
import com.twyst.app.android.util.RoundedTransformation;
import com.twyst.app.android.util.TwystProgressHUD;
import com.twyst.app.android.util.Utils;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by rahuls on 2/9/15.
 */
public class EditProfileActivity extends BaseActivity implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    private boolean fromDrawer;
    private String source = "";
    private CallbackManager callbackManager;
    private static final int RC_SIGN_IN = 0;
    private AccessTokenTracker accessTokenTracker;
    private GoogleApiClient mGoogleApiClient;
    private EditText email;
    private String userImage;
    private String firstName;
    private String middleName;
    private String lastName;
    private String dob;
    private String city;
    private String socialEmail;
    private String id, fbid, linkUri;
    private String facebookUri = "", googleplusUri = "";
    private Friend friend;
    private List<Friend.Friends> friendsList;
    private Friend.Friends friends;
    /* Is there a ConnectionResult resolution in progress? */
    private boolean mIsResolving = false;

    private ProfileTracker profileTracker;
    Profile profile;
    /* Should we automatically resolve ConnectionResults when possible? */
    private boolean mShouldResolve = false;

    TextView editProfileMail;
    EditText editProfileDob;
    EditText editProfileAnniversary;
    EditText editProfileCity;
    TextView profileName;
    TextView editProfileMoNo;
    ImageView editEmail;
    ImageView dobEditbtn;
    ImageView anniversaryEditBtn;
    ImageView cityEditBtn;
    SwitchCompat facebookSwitchBTn;
    SwitchCompat googlePlusSwitchBtn;
    SwitchCompat pushSwitchBtn;
    TextView facebookTxt;
    TextView googleTxt;
    TextView pushNotifiyTxt;


    @Override
    protected String getTagName() {
        return null;
    }

    @Override
    protected int getLayoutResource() {
        return R.layout.activity_edit_profile;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        FacebookSdk.sdkInitialize(getApplicationContext());
        setupAsChild = true;
        super.onCreate(savedInstanceState);
        callbackManager = CallbackManager.Factory.create();
        fromDrawer = getIntent().getBooleanExtra(AppConstants.INTENT_PARAM_FROM_DRAWER, false);
        final SharedPreferences prefs = getSharedPreferences(AppConstants.PREFERENCE_SHARED_PREF_NAME, Context.MODE_PRIVATE);

        editProfileMail = (EditText) findViewById(R.id.editProfileMail);
        editProfileDob = (EditText) findViewById(R.id.editProfileDob);
        editProfileAnniversary = (EditText) findViewById(R.id.editProfileAnniversary);
        editProfileCity = (EditText) findViewById(R.id.editProfileCity);
        profileName = (TextView) findViewById(R.id.profileName);
        editProfileMoNo = (TextView) findViewById(R.id.editProfileMoNo);
        editEmail = (ImageView) findViewById(R.id.editEmail);
        dobEditbtn = (ImageView) findViewById(R.id.dobEditbtn);
        anniversaryEditBtn = (ImageView) findViewById(R.id.anniversaryEditBtn);
        cityEditBtn = (ImageView) findViewById(R.id.cityEditBtn);
        facebookSwitchBTn = (SwitchCompat) findViewById(R.id.facebookSwitchBTn);
        googlePlusSwitchBtn = (SwitchCompat) findViewById(R.id.googlePlusSwitchIcon);
        pushSwitchBtn = (SwitchCompat) findViewById(R.id.pushNotificationSwitch);
        facebookTxt = (TextView) findViewById(R.id.facebookTxt);
        googleTxt = (TextView) findViewById(R.id.googleTxt);
        pushNotifiyTxt = (TextView) findViewById(R.id.pushNotificTxt);

        final InputMethodManager inputMethodManager = (InputMethodManager) this.getSystemService(Context.INPUT_METHOD_SERVICE);
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
                        setFacebookConnected(true);

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
                setFacebookConnected(false);
                System.out.println("LoginActivity.onCancel called");
            }

            @Override
            public void onError(FacebookException e) {
                setFacebookConnected(false);
                e.printStackTrace();
            }
        });

        profileTracker = new ProfileTracker() {
            @Override
            protected void onCurrentProfileChanged(com.facebook.Profile oldProfile, com.facebook.Profile currentProfile) {
                com.facebook.Profile.fetchProfileForCurrentAccessToken();

                userImage = String.valueOf(currentProfile.getProfilePictureUri(250, 250));
                firstName = currentProfile.getFirstName();
                middleName = currentProfile.getMiddleName();
                lastName = currentProfile.getLastName();
                fbid = currentProfile.getId();
                linkUri = String.valueOf(currentProfile.getLinkUri());
                sharedPreferences.putString(AppConstants.PREFERENCE_USER_PIC, String.valueOf(currentProfile.getProfilePictureUri(250, 250)));
                sharedPreferences.putString(AppConstants.PREFERENCE_USER_NAME, currentProfile.getFirstName());
                sharedPreferences.putString(AppConstants.PREFERENCE_USER_FULL_NAME, currentProfile.getName());
                sharedPreferences.apply();

                updatePicNameLocal();
            }

        };

        profileTracker.startTracking();

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(Plus.API)
                .addScope(Plus.SCOPE_PLUS_LOGIN)
                .build();

        HttpService.getInstance().getProfile(getUserToken(), new Callback<BaseResponse<Profile>>() {
            @Override
            public void success(final BaseResponse<Profile> profileBaseResponse, Response response) {

                if (profileBaseResponse.isResponse()) {
                    hideProgressHUDInLayout();
                    profile = profileBaseResponse.getData();

                    updatePicNameLocal();
                    findViewById(R.id.layout).setVisibility(View.VISIBLE);

//                    editProfileMail.setFocusableInTouchMode(false);
                    editProfileDob.setFocusableInTouchMode(false);
                    editProfileAnniversary.setFocusableInTouchMode(false);
//                    editProfileCity.setFocusableInTouchMode(false);

                    editProfileAnniversary.setText(prefs.getString(AppConstants.PREFERENCE_ANNIVERSARY, "Anniversary"));
                    editProfileDob.setText(prefs.getString(AppConstants.PREFERENCE_DOB,"Date of Birth"));

                    if (!TextUtils.isEmpty(profile.getPhone())) {
                        editProfileMoNo.setText(profile.getPhone());
                    }

                    if (!TextUtils.isEmpty(profile.getEmail())) {
                        editProfileMail.setText(profile.getEmail());
                    }

                    editEmail.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            editProfileMail.setFocusableInTouchMode(true);
                            editProfileMail.requestFocus();
                            inputMethodManager.showSoftInput(editProfileMail, InputMethodManager.SHOW_FORCED);
                        }
                    });

                    dobEditbtn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            editProfileDob.setFocusableInTouchMode(true);
                            DialogFragment newFragment = new DobDatePickerFragment();
                            newFragment.show(getSupportFragmentManager(), "datePicker");
                        }
                    });

                    anniversaryEditBtn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            editProfileAnniversary.setFocusableInTouchMode(true);
                            DialogFragment newFragment = new AnniversaryDatePickerFragment();
                            newFragment.show(getSupportFragmentManager(), "datePicker");

                        }
                    });

                    cityEditBtn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            editProfileCity.setFocusableInTouchMode(true);
                            editProfileCity.requestFocus();
                            inputMethodManager.showSoftInput(editProfileCity, InputMethodManager.SHOW_FORCED);
                        }
                    });

                    setFacebookConnected(prefs.getBoolean(AppConstants.PREFERENCE_IS_FACEBOOK_CONNECTED, false));
                    setGoogleConnected(prefs.getBoolean(AppConstants.PREFERENCE_IS_GOOGLE_CONNECTED, false));
                    setPushEnabled(prefs.getBoolean(AppConstants.PREFERENCE_IS_PUSH_ENABLED, false));

                    facebookSwitchBTn.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                        @Override
                        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
//                            if (!isChecked && profile.isFacebookConnect()) {
//                                facebookTxt.setText("Connect");
//                                facebookTxt.setCompoundDrawablesWithIntrinsicBounds(getResources().getDrawable(R.drawable.edit_profile_image_facebook_icon_gray), null, null, null);
////                                LoginManager.getInstance().logOut();
//                            } else if (isChecked && profile.isFacebookConnect()) {
//                                facebookTxt.setText("Connected");
//                                facebookTxt.setCompoundDrawablesWithIntrinsicBounds(getResources().getDrawable(R.drawable.edit_profile_image_facebook_icon_blue), null, null, null);
//                            }
                            if (!isChecked) {
                                profileTracker.stopTracking();
                                LoginManager.getInstance().logOut();
                            } else {
                                source = "FACEBOOK";
                                facebookLogin();
                            }
                            setFacebookConnected(isChecked);
                        }
                    });

                    googlePlusSwitchBtn.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                        @Override
                        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
//                            if (!isChecked && profile.isGoogleConnect()) {
//                                googleTxt.setText("Connect");
//                                googleTxt.setCompoundDrawablesWithIntrinsicBounds(getResources().getDrawable(R.drawable.edit_profile_google_plus_white_icon), null, null, null);
//                                if (mGoogleApiClient.isConnected()) {
//                                    mGoogleApiClient.disconnect();
//                                }
//                            } else if (isChecked && profile.isGoogleConnect()) {
//                                googleTxt.setText("Connected");
//                                googleTxt.setCompoundDrawablesWithIntrinsicBounds(getResources().getDrawable(R.drawable.edit_profile_google_plus_red), null, null, null);
//                            }
                            if (!isChecked) {
                                if (mGoogleApiClient.isConnected()) {
                                    mGoogleApiClient.disconnect();
                                }
                            } else {
                                source = "GOOGLE";
                                mShouldResolve = true;
                                mGoogleApiClient.connect();
                                googleTxt.setText("Connected");
                                googleTxt.setCompoundDrawablesWithIntrinsicBounds(getResources().getDrawable(R.drawable.edit_profile_google_plus_red), null, null, null);
                                profile.setGoogleConnect(true);
                            }
                            setGoogleConnected(isChecked);
                        }
                    });

                    pushSwitchBtn.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                        @Override
                        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                            setPushEnabled(isChecked);
                        }
                    });
                    final String EMAIL_REGEX = "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";

                    findViewById(R.id.updateProf).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (TextUtils.isEmpty(editProfileMail.getText().toString()) || !Pattern.matches(EMAIL_REGEX, editProfileMail.getText().toString())) {
                                editProfileMail.setError("Invalid email");
                            }else{
                                editProfileMail.setError(null);
                                updateProfile();
                            }
                        }
                    });

                } else {
                    hideProgressHUDInLayout();
                }
            }

            @Override
            public void failure(RetrofitError error) {
                hideProgressHUDInLayout();
                handleRetrofitError(error);
            }
        });

        city = getSharedPreferences(AppConstants.PREFERENCE_SHARED_PREF_NAME, Context.MODE_PRIVATE).getString(AppConstants.PREFERENCE_LOCALITY_SHOWN_DRAWER, "");
        editProfileCity.setText(city);

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
    public void onConnected(Bundle bundle) {
        // onConnected indicates that an account was selected on the device, that the selected
        // account has granted any requested permissions to our app and that we were able to
        // establish a service connection to Google Play services.
        Log.d(getTagName(), "onConnected:" + bundle);
        mShouldResolve = false;

        // Show the signed-in UI
        socialEmail = Plus.AccountApi.getAccountName(mGoogleApiClient);

        System.out.println("LoginActivity.onConnected google email : " + email);

        profile.setGoogleConnect(true);
        setGoogleConnected(true);
        try {
            if (Plus.PeopleApi.getCurrentPerson(mGoogleApiClient) != null) {
                Person currentPerson = Plus.PeopleApi
                        .getCurrentPerson(mGoogleApiClient);
                String personFullName = currentPerson.getDisplayName();
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
                sharedPreferences.putString(AppConstants.PREFERENCE_USER_PIC, personPhotoUrl);
                sharedPreferences.putString(AppConstants.PREFERENCE_USER_NAME, personName);
                sharedPreferences.putString(AppConstants.PREFERENCE_USER_FULL_NAME, personFullName);
                sharedPreferences.apply();

                updatePicNameLocal();
//

            }
        } catch (Exception e) {
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
        setGoogleConnected(false);

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
// Could not connect to Google Play Services.  The user needs to select an account,
        // grant permissions or resolve an error in order to sign in. Refer to the javadoc for
        // ConnectionResult to see possible error codes.
        Log.d(getTagName(), "onConnectionFailed:" + connectionResult);

        setGoogleConnected(false);
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
        profileTracker.stopTracking();
    }

    public static class DobDatePickerFragment extends DialogFragment
            implements DatePickerDialog.OnDateSetListener {

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            // Use the current date as the default date in the picker
            final Calendar c = Calendar.getInstance();
            SharedPreferences prefs = getActivity().getSharedPreferences(AppConstants.PREFERENCE_SHARED_PREF_NAME, Context.MODE_PRIVATE);
            int year = prefs.getInt(AppConstants.PREFERENCE_DOB_YEAR, c.get(Calendar.YEAR));
            int month = prefs.getInt(AppConstants.PREFERENCE_DOB_MONTH, c.get(Calendar.MONTH));
            int day = prefs.getInt(AppConstants.PREFERENCE_DOB_DAY, c.get(Calendar.DAY_OF_MONTH));

            // Create a new instance of DatePickerDialog and return it
            DatePickerDialog datePickerDialog = new DatePickerDialog(getActivity(), android.R.style.Theme_Holo_Dialog_MinWidth, this, year, month, day);
            datePickerDialog.getDatePicker().setCalendarViewShown(false);
            datePickerDialog.getDatePicker().setSpinnersShown(true);
            DatePicker dp = datePickerDialog.getDatePicker();

            return datePickerDialog;
        }

        public void onDateSet(DatePicker view, int year, int month, int day) {
            Log.d(getClass().getSimpleName(), "year=" + year + ", month=" + month + ", day=" + day);
            saveSharedPrefDOB(year,month,day);
            TextView editProfileDob = (EditText) getActivity().findViewById(R.id.editProfileDob);
            Calendar calendar = Calendar.getInstance();
            calendar.set(Calendar.YEAR, year);
            calendar.set(Calendar.MONTH, month);
            calendar.set(Calendar.DAY_OF_MONTH, day);

            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
            String dateFormatted = sdf.format(calendar.getTime());
            editProfileDob.setText(dateFormatted);
        }

        private void saveSharedPrefDOB(int year, int month, int day) {
            SharedPreferences prefs = getActivity().getSharedPreferences(AppConstants.PREFERENCE_SHARED_PREF_NAME, Context.MODE_PRIVATE);
            prefs.edit().putInt(AppConstants.PREFERENCE_DOB_DAY, day).apply();
            prefs.edit().putInt(AppConstants.PREFERENCE_DOB_MONTH, month).apply();
            prefs.edit().putInt(AppConstants.PREFERENCE_DOB_YEAR, year).apply();
        }
    }


    public static class AnniversaryDatePickerFragment extends DialogFragment
            implements DatePickerDialog.OnDateSetListener {

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            // Use the current date as the default date in the picker
            final Calendar c = Calendar.getInstance();
            SharedPreferences prefs = getActivity().getSharedPreferences(AppConstants.PREFERENCE_SHARED_PREF_NAME, Context.MODE_PRIVATE);
            int year = prefs.getInt(AppConstants.PREFERENCE_ANNIVERSARY_YEAR, c.get(Calendar.YEAR));
            int month = prefs.getInt(AppConstants.PREFERENCE_ANNIVERSARY_MONTH, c.get(Calendar.MONTH));
            int day = prefs.getInt(AppConstants.PREFERENCE_ANNIVERSARY_DAY, c.get(Calendar.DAY_OF_MONTH));

            // Create a new instance of DatePickerDialog and return it
            DatePickerDialog datePickerDialog = new DatePickerDialog(getActivity(), android.R.style.Theme_Holo_Dialog_MinWidth, this, year, month, day);
            datePickerDialog.getDatePicker().setCalendarViewShown(false);
            datePickerDialog.getDatePicker().setSpinnersShown(true);
            DatePicker dp = datePickerDialog.getDatePicker();

            return datePickerDialog;
        }

        public void onDateSet(DatePicker view, int year, int month, int day) {
            Log.d(getClass().getSimpleName(), "year=" + year + ", month=" + month + ", day=" + day);
            saveSharedPrefAnniversary(year, month, day);
            TextView editProfileAnniversary = (EditText) getActivity().findViewById(R.id.editProfileAnniversary);
            Calendar calendar = Calendar.getInstance();
            calendar.set(Calendar.YEAR, year);
            calendar.set(Calendar.MONTH, month);
            calendar.set(Calendar.DAY_OF_MONTH, day);

            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
            String dateFormatted = sdf.format(calendar.getTime());
            editProfileAnniversary.setText(dateFormatted);
        }

        private void saveSharedPrefAnniversary(int year, int month, int day) {
            SharedPreferences prefs = getActivity().getSharedPreferences(AppConstants.PREFERENCE_SHARED_PREF_NAME, Context.MODE_PRIVATE);
            prefs.edit().putInt(AppConstants.PREFERENCE_ANNIVERSARY_DAY, day).apply();
            prefs.edit().putInt(AppConstants.PREFERENCE_ANNIVERSARY_MONTH, month).apply();
            prefs.edit().putInt(AppConstants.PREFERENCE_ANNIVERSARY_YEAR, year).apply();
        }
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

    private void facebookLogin() {
        Collection<String> permissions = Arrays.asList("email", "user_friends", "public_profile", "user_birthday");
        LoginManager.getInstance().logInWithReadPermissions(this, permissions);
    }

    private void updateProfile() {
        String deviceId = getSharedPreferences(AppConstants.PREFERENCE_SHARED_PREF_NAME, Context.MODE_PRIVATE).getString(AppConstants.PREFERENCE_REGISTRATION_ID, "");
        final TwystProgressHUD twystProgressHUD = TwystProgressHUD.show(this, false, null);

        UpdateProfile updateProfile = new UpdateProfile();
        updateProfile.setEmail(editProfileMail.getText().toString());
        updateProfile.setImage(userImage);
        updateProfile.setFname(firstName);
        updateProfile.setMname(middleName);
        updateProfile.setLname(lastName);
        updateProfile.setCity(editProfileCity.getText().toString());
        updateProfile.setId(id);
        updateProfile.setSource(source);
        updateProfile.setFacebookUri(facebookUri);
        updateProfile.setGoogleplusUri(googleplusUri);
        updateProfile.setDeviceId(deviceId);
        updateProfile.setVersion(Utils.getbuildVersionStringBuilder().toString());
        updateProfile.setDevice(android.os.Build.DEVICE);
        updateProfile.setModel(android.os.Build.MODEL);
        updateProfile.setProduct(android.os.Build.PRODUCT);
        updateProfile.setLifeEvents(getLifeEvents());

        HttpService.getInstance().updateProfile(getUserToken(),updateProfile, new Callback<BaseResponse<ProfileUpdate>>() {
            @Override
            public void success(final BaseResponse<ProfileUpdate> profileUpdateBaseResponse, Response response) {
                twystProgressHUD.dismiss();
                if (profileUpdateBaseResponse.isResponse()) {
                    updateSocialFriendList(getUserToken());
                }
            }

            @Override
            public void failure(RetrofitError error) {
//                hideProgressHUDInLayout();
                twystProgressHUD.dismiss();
                handleRetrofitError(error);
            }
        });
    }

    private LifeEvents[] getLifeEvents() {

        LifeEvents[] lifeEvents = new LifeEvents[LifeEvents.TYPES_COUNT];

        final Calendar c = Calendar.getInstance();
        SharedPreferences prefs = getSharedPreferences(AppConstants.PREFERENCE_SHARED_PREF_NAME, Context.MODE_PRIVATE);

        int bYear = prefs.getInt(AppConstants.PREFERENCE_DOB_YEAR, c.get(Calendar.YEAR));
        int bMonth = prefs.getInt(AppConstants.PREFERENCE_DOB_MONTH, c.get(Calendar.MONTH));
        int bDay = prefs.getInt(AppConstants.PREFERENCE_DOB_DAY, c.get(Calendar.DAY_OF_MONTH));

        lifeEvents[0]= new LifeEvents();
        EventDate birthdayDate = new EventDate();
        birthdayDate.setY(bYear);
        birthdayDate.setM(bMonth+1);
        birthdayDate.setD(bDay);
        lifeEvents[0].setEventDate(birthdayDate);
        lifeEvents[0].setEventType(LifeEvents.BIRTHDAY);

        int aYear = prefs.getInt(AppConstants.PREFERENCE_ANNIVERSARY_YEAR, c.get(Calendar.YEAR));
        int aMonth = prefs.getInt(AppConstants.PREFERENCE_ANNIVERSARY_MONTH, c.get(Calendar.MONTH));
        int aDay = prefs.getInt(AppConstants.PREFERENCE_ANNIVERSARY_DAY, c.get(Calendar.DAY_OF_MONTH));

        lifeEvents[1]= new LifeEvents();
        EventDate anniversaryDate = new EventDate();
        anniversaryDate.setY(aYear);
        anniversaryDate.setM(aMonth+1);
        anniversaryDate.setD(aDay);
        lifeEvents[1].setEventDate(anniversaryDate);
        lifeEvents[1].setEventType(LifeEvents.ANNIVERSARY);

        return lifeEvents;
    }

    private void updateUserEmail() {

        if (source.equalsIgnoreCase("FACEBOOK")) {
            facebookUri = linkUri;

        } else if (source.equalsIgnoreCase("GOOGLE")) {
            googleplusUri = linkUri;
        }

        updateProfile();
//        JSONObject facebookdata = new JSONObject();
//        JSONObject googledata = new JSONObject();
//        if(source.equalsIgnoreCase("FACEBOOK")) {
//            try {
//                facebookdata.put("id", fbid);
//                facebookdata.put("linkUri", linkUri);
//                googledata.put("linkUri", null);
//            } catch (JSONException e) {
//                e.printStackTrace();
//            }
//        }else if(source.equalsIgnoreCase("GOOGLE")){
//
//            try {
//                facebookdata.put("id", null);
//                facebookdata.put("linkUri", null);
//                googledata.put("linkUri", linkUri);
//            } catch (JSONException e) {
//                e.printStackTrace();
//            }
//        }

    }

    private void updateSocialFriendList(String token) {
        final TwystProgressHUD twystProgressHUD = TwystProgressHUD.show(this, false, null);
        friend = new Friend();
        friend.setSource(source);
        friend.setList(friendsList);
        HttpService.getInstance().updateSocialFriends(token, friend, new Callback<BaseResponse<ProfileUpdate>>() {
            @Override
            public void success(BaseResponse<ProfileUpdate> profileUpdateBaseResponse, Response response) {
                twystProgressHUD.dismiss();
                if (profileUpdateBaseResponse.isResponse()) {
                    saveDOBAnniversaryLocally();
                    Toast.makeText(EditProfileActivity.this, "Profile updated successfully!",Toast.LENGTH_SHORT).show();
                    Log.d(getTagName(), "" + profileUpdateBaseResponse.getMessage());
                } else {
                    Log.d(getTagName(), "" + profileUpdateBaseResponse.getMessage());
                }
            }

            @Override
            public void failure(RetrofitError error) {
                twystProgressHUD.dismiss();
                handleRetrofitError(error);
            }
        });

    }

    private void saveDOBAnniversaryLocally() {
        SharedPreferences prefs = getSharedPreferences(AppConstants.PREFERENCE_SHARED_PREF_NAME, Context.MODE_PRIVATE);
        prefs.edit().putString(AppConstants.PREFERENCE_DOB, editProfileDob.getText().toString()).apply();
        prefs.edit().putString(AppConstants.PREFERENCE_ANNIVERSARY, editProfileAnniversary.getText().toString()).apply();
    }

    private void setFacebookConnected(boolean enabled) {
        final SharedPreferences prefs = getSharedPreferences(AppConstants.PREFERENCE_SHARED_PREF_NAME, Context.MODE_PRIVATE);
        profile.setFacebookConnect(enabled);
        if (enabled) {
            facebookSwitchBTn.setChecked(true);
            facebookTxt.setText("Connected");
            facebookTxt.setCompoundDrawablesWithIntrinsicBounds(getResources().getDrawable(R.drawable.edit_profile_image_facebook_icon_blue), null, null, null);
            prefs.edit().putBoolean(AppConstants.PREFERENCE_IS_FACEBOOK_CONNECTED, true).apply();
        } else {
            facebookSwitchBTn.setChecked(false);
            facebookTxt.setText("Connect");
            facebookTxt.setCompoundDrawablesWithIntrinsicBounds(getResources().getDrawable(R.drawable.edit_profile_image_facebook_icon_gray), null, null, null);
            prefs.edit().putBoolean(AppConstants.PREFERENCE_IS_FACEBOOK_CONNECTED, false).apply();
        }
    }

    private void setGoogleConnected(boolean enabled) {
        profile.setGoogleConnect(enabled);
        final SharedPreferences prefs = getSharedPreferences(AppConstants.PREFERENCE_SHARED_PREF_NAME, Context.MODE_PRIVATE);
        if (enabled) {
            googlePlusSwitchBtn.setChecked(true);
            googleTxt.setText("Connected");
            googleTxt.setCompoundDrawablesWithIntrinsicBounds(getResources().getDrawable(R.drawable.edit_profile_google_plus_red), null, null, null);
            prefs.edit().putBoolean(AppConstants.PREFERENCE_IS_GOOGLE_CONNECTED, true).apply();
        } else {
            googlePlusSwitchBtn.setChecked(false);
            googleTxt.setText("Connect");
            googleTxt.setCompoundDrawablesWithIntrinsicBounds(getResources().getDrawable(R.drawable.edit_profile_google_plus_white_icon), null, null, null);
            prefs.edit().putBoolean(AppConstants.PREFERENCE_IS_GOOGLE_CONNECTED, false).apply();
        }
    }

    private void setPushEnabled(boolean enabled) {
        final SharedPreferences prefs = getSharedPreferences(AppConstants.PREFERENCE_SHARED_PREF_NAME, Context.MODE_PRIVATE);
        if (enabled) {
            pushSwitchBtn.setChecked(true);
            pushNotifiyTxt.setTextColor(getResources().getColor(R.color.edit_profile_text_color));
            prefs.edit().putBoolean(AppConstants.PREFERENCE_IS_PUSH_ENABLED, true).apply();
        } else {
            pushSwitchBtn.setChecked(false);
            pushNotifiyTxt.setTextColor(getResources().getColor(R.color.edit_profile_hint_color));
            prefs.edit().putBoolean(AppConstants.PREFERENCE_IS_PUSH_ENABLED, false).apply();
        }
    }

    private void updatePicNameLocal(){
        final SharedPreferences prefs = getSharedPreferences(AppConstants.PREFERENCE_SHARED_PREF_NAME, Context.MODE_PRIVATE);
        final String name = prefs.getString(AppConstants.PREFERENCE_USER_FULL_NAME, "");
        final String pic = prefs.getString(AppConstants.PREFERENCE_USER_PIC, "");

        ImageView backImage = (ImageView) findViewById(R.id.editProfileBackImage);
        final ImageView image = (ImageView) findViewById(R.id.editProfileUserImage);
        if (!TextUtils.isEmpty(pic)) {
            backImage.setVisibility(View.VISIBLE);
            image.setVisibility(View.VISIBLE);
            Picasso picasso = Picasso.with(this);
            picasso.setIndicatorsEnabled(AppConstants.DEGUG_PICASSO);
            picasso.setLoggingEnabled(AppConstants.DEGUG_PICASSO);
            picasso.load(pic)
                    .noFade()
                    .centerCrop()
                    .resize(150, 150)
                    .transform(new RoundedTransformation(100, 0))
                    .into(image);

        } else {
            backImage.setVisibility(View.GONE);
            image.setVisibility(View.VISIBLE);
        }
        profileName.setText(name);

    }
}
