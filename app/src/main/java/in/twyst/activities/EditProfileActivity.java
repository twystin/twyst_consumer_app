package in.twyst.activities;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.view.GravityCompat;
import android.support.v7.widget.SwitchCompat;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

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

import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import in.twyst.R;
import in.twyst.model.BaseResponse;
import in.twyst.model.Friend;
import in.twyst.model.Profile;
import in.twyst.model.ProfileUpdate;
import in.twyst.service.HttpService;
import in.twyst.util.AppConstants;
import in.twyst.util.RoundedTransformation;
import in.twyst.util.TwystProgressHUD;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by rahuls on 2/9/15.
 */
public class EditProfileActivity extends BaseActivity implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener{

    private boolean fromDrawer;
    private String source;
    private CallbackManager callbackManager;
    private static final int RC_SIGN_IN = 0;
    private ProfileTracker profileTracker;
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
    private String id,fbid,linkUri;
    private Friend friend;
    private List<Friend.Friends> friendsList;
    private Friend.Friends friends;
    /* Is there a ConnectionResult resolution in progress? */
    private boolean mIsResolving = false;

    /* Should we automatically resolve ConnectionResults when possible? */
    private boolean mShouldResolve = false;


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
        setupAsChild=true;
        super.onCreate(savedInstanceState);
        callbackManager = CallbackManager.Factory.create();
        fromDrawer = getIntent().getBooleanExtra(AppConstants.INTENT_PARAM_FROM_DRAWER, false);
        SharedPreferences prefs = getSharedPreferences(AppConstants.PREFERENCE_SHARED_PREF_NAME, Context.MODE_PRIVATE);
        final String name = prefs.getString(AppConstants.PREFERENCE_USER_FULL_NAME, "");
        final String pic = prefs.getString(AppConstants.PREFERENCE_USER_PIC, "");

        ImageView backImage = (ImageView)findViewById(R.id.editProfileBackImage);
        final ImageView image = (ImageView)findViewById(R.id.editProfileUserImage);
        if(!TextUtils.isEmpty(pic)){
            backImage.setVisibility(View.VISIBLE);
            image.setVisibility(View.VISIBLE);
            Picasso picasso = Picasso.with(this);
            picasso.setIndicatorsEnabled(AppConstants.DEGUG_PICASSO);
            picasso.setLoggingEnabled(AppConstants.DEGUG_PICASSO);
            picasso.load(pic)
                    .noFade()
                    .centerCrop()
                    .resize(150,150)
                    .transform(new RoundedTransformation(100, 0))
                    .into(image);

        }else {
            backImage.setVisibility(View.GONE);
            image.setVisibility(View.VISIBLE);
        }
        final EditText editProfileMail = (EditText)findViewById(R.id.editProfileMail);
        final EditText editProfileDob = (EditText)findViewById(R.id.editProfileDob);
        final EditText editProfileAnniversary = (EditText)findViewById(R.id.editProfileAnniversary);
        final EditText editProfileCity = (EditText)findViewById(R.id.editProfileCity);
        final TextView profileName = (TextView)findViewById(R.id.profileName);
        final TextView editProfileMoNo = (TextView)findViewById(R.id.editProfileMoNo);
        final ImageView editEmail = (ImageView)findViewById(R.id.editEmail);
        final ImageView dobEditbtn = (ImageView)findViewById(R.id.dobEditbtn);
        final ImageView anniversaryEditBtn = (ImageView)findViewById(R.id.anniversaryEditBtn);
        final ImageView cityEditBtn = (ImageView)findViewById(R.id.cityEditBtn);
        final SwitchCompat facebookSwitchBTn = (SwitchCompat)findViewById(R.id.facebookSwitchBTn);
        final SwitchCompat googlePlusSwitchBtn = (SwitchCompat)findViewById(R.id.googlePlusSwitchIcon);
        final SwitchCompat pushSwitchBtn = (SwitchCompat)findViewById(R.id.pushNotificationSwitch);
        final TextView facebookTxt = (TextView)findViewById(R.id.facebookTxt);
        final TextView googleTxt = (TextView)findViewById(R.id.googleTxt);
        final TextView pushNotifiyTxt = (TextView)findViewById(R.id.pushNotificTxt);

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
                       // updateUserEmail();TODO

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
                sharedPreferences.putString(AppConstants.PREFERENCE_USER_FULL_NAME,currentProfile.getName());
                sharedPreferences.apply();
            }

        };

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
                    Profile profile = profileBaseResponse.getData();
                    profileName.setText(name);

                    findViewById(R.id.layout).setVisibility(View.VISIBLE);

                    editProfileMail.setFocusableInTouchMode(false);
                    editProfileDob.setFocusableInTouchMode(false);
                    editProfileAnniversary.setFocusableInTouchMode(false);
                    editProfileCity.setFocusableInTouchMode(false);

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

                    if (profileBaseResponse.getData().isFacebookConnect()) {
                        facebookTxt.setText("Connected");
                        facebookTxt.setCompoundDrawablesWithIntrinsicBounds(getResources().getDrawable(R.drawable.edit_profile_image_facebook_icon_blue), null, null, null);
                    } else {
                        facebookTxt.setText("Connect");
                        facebookTxt.setCompoundDrawablesWithIntrinsicBounds(getResources().getDrawable(R.drawable.edit_profile_image_facebook_icon_gray), null, null, null);
                    }

                    if (profileBaseResponse.getData().isGoogleConnect()) {
                        googleTxt.setText("Connected");
                        googleTxt.setCompoundDrawablesWithIntrinsicBounds(getResources().getDrawable(R.drawable.edit_profile_google_plus_red), null, null, null);
                    } else {
                        googleTxt.setText("Connect");
                        googleTxt.setCompoundDrawablesWithIntrinsicBounds(getResources().getDrawable(R.drawable.edit_profile_google_plus_white_icon), null, null, null);
                    }

                    facebookSwitchBTn.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                        @Override
                        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                            if (!isChecked && profileBaseResponse.getData().isFacebookConnect()) {
                                facebookTxt.setText("Connect");
                                facebookTxt.setCompoundDrawablesWithIntrinsicBounds(getResources().getDrawable(R.drawable.edit_profile_image_facebook_icon_gray), null, null, null);
                                profileBaseResponse.getData().setFacebookConnect(false);
                            } else if (isChecked && profileBaseResponse.getData().isFacebookConnect()) {
                                facebookTxt.setText("Connected");
                                facebookTxt.setCompoundDrawablesWithIntrinsicBounds(getResources().getDrawable(R.drawable.edit_profile_image_facebook_icon_blue), null, null, null);
                                profileBaseResponse.getData().setFacebookConnect(false);
                            } else {
                                source = "FACEBOOK";
                                facebookLogin();
                                facebookTxt.setText("Connected");
                                facebookTxt.setCompoundDrawablesWithIntrinsicBounds(getResources().getDrawable(R.drawable.edit_profile_image_facebook_icon_blue), null, null, null);
                                profileBaseResponse.getData().setFacebookConnect(true);
                            }
                        }
                    });

                    googlePlusSwitchBtn.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                        @Override
                        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                            if (!isChecked && profileBaseResponse.getData().isGoogleConnect()) {
                                googleTxt.setText("Connect");
                                googleTxt.setCompoundDrawablesWithIntrinsicBounds(getResources().getDrawable(R.drawable.edit_profile_google_plus_white_icon), null, null, null);
                                profileBaseResponse.getData().setGoogleConnect(false);
                            }else if(isChecked && profileBaseResponse.getData().isGoogleConnect()){
                                googleTxt.setText("Connected");
                                googleTxt.setCompoundDrawablesWithIntrinsicBounds(getResources().getDrawable(R.drawable.edit_profile_google_plus_red), null, null, null);
                            }
                            else {
                                source = "GOOGLE";
                                mShouldResolve = true;
                                mGoogleApiClient.connect();
                                googleTxt.setText("Connected");
                                googleTxt.setCompoundDrawablesWithIntrinsicBounds(getResources().getDrawable(R.drawable.edit_profile_google_plus_red), null, null, null);
                                profileBaseResponse.getData().setGoogleConnect(true);
                            }
                        }
                    });

                    pushSwitchBtn.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                        @Override
                        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                            if (isChecked) {
                                pushNotifiyTxt.setTextColor(getResources().getColor(R.color.edit_profile_text_color));

                            } else {
                                pushNotifiyTxt.setTextColor(getResources().getColor(R.color.edit_profile_hint_color));
                            }
                        }
                    });

                    findViewById(R.id.updateProf).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            updateProfile();
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

                        //updateUserEmail();TODO

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

    public static class DobDatePickerFragment extends DialogFragment
            implements DatePickerDialog.OnDateSetListener {

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            // Use the current date as the default date in the picker
            final Calendar c = Calendar.getInstance();
            int year = c.get(Calendar.YEAR);
            int month = c.get(Calendar.MONTH);
            int day = c.get(Calendar.DAY_OF_MONTH);

            // Create a new instance of DatePickerDialog and return it
            DatePickerDialog datePickerDialog = new DatePickerDialog(getActivity(), android.R.style.Theme_Holo_Dialog_MinWidth, this, year, month, day);
            datePickerDialog.getDatePicker().setCalendarViewShown(false);
            datePickerDialog.getDatePicker().setSpinnersShown(true);
            DatePicker dp = datePickerDialog.getDatePicker();

            return datePickerDialog;
        }

        public void onDateSet(DatePicker view, int year, int month, int day) {
            Log.d(getClass().getSimpleName(), "year=" + year + ", month=" + month + ", day=" + day);
            TextView editProfileDob = (EditText)getActivity().findViewById(R.id.editProfileDob);
            Calendar calendar = Calendar.getInstance();
            calendar.set(Calendar.YEAR, year);
            calendar.set(Calendar.MONTH, month);
            calendar.set(Calendar.DAY_OF_MONTH, day);

            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
            String dateFormatted = sdf.format(calendar.getTime());
            editProfileDob.setText(dateFormatted);
        }
    }


    public static class AnniversaryDatePickerFragment extends DialogFragment
            implements DatePickerDialog.OnDateSetListener {

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            // Use the current date as the default date in the picker
            final Calendar c = Calendar.getInstance();
            int year = c.get(Calendar.YEAR);
            int month = c.get(Calendar.MONTH);
            int day = c.get(Calendar.DAY_OF_MONTH);

            // Create a new instance of DatePickerDialog and return it
            DatePickerDialog datePickerDialog = new DatePickerDialog(getActivity(), android.R.style.Theme_Holo_Dialog_MinWidth, this, year, month, day);
            datePickerDialog.getDatePicker().setCalendarViewShown(false);
            datePickerDialog.getDatePicker().setSpinnersShown(true);
            DatePicker dp = datePickerDialog.getDatePicker();

            return datePickerDialog;
        }

        public void onDateSet(DatePicker view, int year, int month, int day) {
            Log.d(getClass().getSimpleName(), "year=" + year + ", month=" + month + ", day=" + day);
            TextView editProfileAnniversary = (EditText)getActivity().findViewById(R.id.editProfileAnniversary);
            Calendar calendar = Calendar.getInstance();
            calendar.set(Calendar.YEAR, year);
            calendar.set(Calendar.MONTH, month);
            calendar.set(Calendar.DAY_OF_MONTH, day);

            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
            String dateFormatted = sdf.format(calendar.getTime());
            editProfileAnniversary.setText(dateFormatted);
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

    public void facebookLogin() {

        Collection<String> permissions = Arrays.asList("email", "user_friends", "public_profile", "user_birthday");
        LoginManager.getInstance().logInWithReadPermissions(this, permissions);

    }

    public void updateProfile(){

    }

    private void updateUserEmail() {
        final SharedPreferences prefs = getSharedPreferences(AppConstants.PREFERENCE_SHARED_PREF_NAME, Context.MODE_PRIVATE);

        String deviceId = prefs.getString(AppConstants.PREFERENCE_REGISTRATION_ID,"");
        StringBuilder builder = new StringBuilder();

        Field[] fields = Build.VERSION_CODES.class.getFields();
        for (Field field : fields) {
            String fieldName = field.getName();
            int fieldValue = -1;

            try {
                fieldValue = field.getInt(new Object());
            } catch (IllegalArgumentException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (NullPointerException e) {
                e.printStackTrace();
            }

            if (fieldValue == Build.VERSION.SDK_INT) {
                builder.append(fieldName).append(" : ").append(Build.VERSION.RELEASE);
                break;
            }
        }

        final TwystProgressHUD twystProgressHUD = TwystProgressHUD.show(this, false, null);
        final String email;
        if(TextUtils.isEmpty(socialEmail)){
            email = "";
        }else {
            email = socialEmail;
        }
        city = prefs.getString(AppConstants.PREFERENCE_LOCALITY_SHOWN_DRAWER,"");
        Log.i("dob", dob + " firstName" + firstName + " lastName" + lastName + " middleName" + middleName + " city" + city + " image" + userImage);
        JSONObject facebookdata = new JSONObject();
        JSONObject googledata = new JSONObject();
        if(source.equalsIgnoreCase("FACEBOOK")) {
            try {
                facebookdata.put("id", fbid);
                facebookdata.put("linkUri", linkUri);
                googledata.put("linkUri", null);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }else if(source.equalsIgnoreCase("GOOGLE")){

            try {
                facebookdata.put("id", null);
                facebookdata.put("linkUri", null);
                googledata.put("linkUri", linkUri);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

      /*  HttpService.getInstance().updateProfile(token, email, userImage, firstName, middleName, lastName, city,id,source,facebookdata,googledata, deviceId, builder.toString(), android.os.Build.DEVICE, android.os.Build.MODEL, android.os.Build.PRODUCT, new Callback<BaseResponse<ProfileUpdate>>() {
            @Override
            public void success(BaseResponse<ProfileUpdate> loginDataBaseResponse, Response response) {
                twystProgressHUD.dismiss();
                if (loginDataBaseResponse.isResponse()) {


                    final String code = prefs.getString(AppConstants.PREFERENCE_USER_REFERRAL, "");
                    if (!TextUtils.isEmpty(code)) {
                        postReferral(token, code);
                    } else {
                        updateSocialFriendList(token);
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
        });*/

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
}
