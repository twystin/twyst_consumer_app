package com.twyst.app.android.util;

/**
 * Created by satish on 31/05/15.
 */
public class AppConstants {

//    public static final String HOST = "http://retwyst.twyst.in";
    public static final String HOST = "http://staging.twyst.in";
    public static final String HOST_SECURE = "https://www.twyst.in";

    public static final String GCM_PROJECT_ID = "216832068690";
    public static final String GOOGLE_ANALYTICS_ID = "UA-51763503-1";

    public static final boolean IS_DEVELOPMENT = false;
    public static final boolean DEGUG_PICASSO = false;

    public static final int DISCOVER_LIST_PAGESIZE = 20;
    public static final int MAX_WAIT_FOR_SMS_IN_SECONDS = (IS_DEVELOPMENT) ? 30 : 30;

    public static final String INTENT_PARAM_OTP_CODE = "otp_code";
    public static final String INTENT_PARAM_OTP_PHONE = "otp_phone_number";
    public static final String INTENT_PARAM_OUTLET_LOCATION_LAT ="lat";
    public static final String INTENT_PARAM_OUTLET_LOCATION_LONG ="lng";
    public static final String INTENT_PARAM_OUTLET_NAME = "outlet_name";
    public static final String INTENT_PARAM_OUTLET_ID = "outlet_id";
    public static final String INTENT_PARAM_OFFER_ID = "offer_id";
    public static final String INTENT_PARAM_OUTLET_OBJECT = "outlet_object";
    public static final String INTENT_PARAM_OFFER_OBJECT = "offer_object";
    public static final String INTENT_PARAM_USE_OFFER_DATA_OBJECT = "use_offer_object";
    public static final String PREFERENCE_PARAM_SEARCH_QUERY = "SEARCH_QUERY";

    public static final String PREFERENCE_SHARED_PREF_NAME = "in.twyst.preferences";
    public static final String PREFERENCE_IS_FIRST_RUN = "first_run";
    public static final String PREFERENCE_REGISTRATION_ID = "registration_id";
    public static final String PREFERENCE_DEVICE_ID = "deviceId";
    public static final String PREFERENCE_TUTORIAL_COUNT = "tutorial_count";
    public static final String PREFERENCE_EMAIL_VERIFIED = "email_verified";
    public static final String PREFERENCE_PHONE_VERIFIED = "phone_verified";
    public static final String PREFERENCE_TUTORIAL_SKIPPED = "tutorial_skipped";
    public static final String PREFERENCE_USER_TOKEN = "token_user";
    public static final String PREFERENCE_SMS_BODY = "sms_body";
    public static final String PREFERENCE_LAST_LOCATION_NAME = "last_location_name";
    public static final String PREFERENCE_LAST_LOCATION_LATITUDE = "last_location_latitude";
    public static final String PREFERENCE_LAST_LOCATION_LONGITUDE = "last_location_longitude";
    public static final String PREFERENCE_LAST_DRAWERITEM_CLICKED = "last_drawer_item";
    public static final String PREFERENCE_LAST_TWYST_BUCK = "twyst_buck";
    public static final String PREFERENCE_USER_PIC = "user_pic";
    public static final String PREFERENCE_USER_NAME = "user_name";
    public static final String PREFERENCE_USER_FULL_NAME ="user_full_name" ;
    public static final String PREFERENCE_USER_PHONE= "user_phone";
    public static final String PREFERENCE_USER_REFERRAL = "user_referral";
    public static final String PREFERENCE_NOTIFICATION_COUNT = "notification_count";
    public static final String PREFERENCE_CHANGE_LOCATION_NAME = "change_location_name";
    public static final String PREFERENCE_CHANGE_LOCATION_LATITUDE = "change_location_latitude";
    public static final String PREFERENCE_CHANGE_LOCATION_LONGITUDE = "change_location_longitude";

    public static final String PREFERENCE_IS_FACEBOOK_CONNECTED = "is_facebook_connected";
    public static final String PREFERENCE_IS_GOOGLE_CONNECTED = "is_google_connected";
    public static final String PREFERENCE_IS_PUSH_ENABLED = "is_push_enabled";

    public static final String SMS_FROM = "BW-TWYSTR";

    public static final String INTENT_PARAM_CHECKIN_HEADER = "checkin_header";
    public static final String INTENT_PARAM_CHECKIN_LINE1 = "check_in_line";
    public static final String INTENT_PARAM_CHECKIN_OUTLET_NAME = "checkin_outlet";
    public static final String INTENT_PARAM_CHECKIN_CODE = "checkin_code";
    public static final String INTENT_PARAM_CHECKIN_LINE2 = "line2";
    public static final String INTENT_PARAM_CHECKIN_OUTLET_ID = "checkin_outlet_id";
    public static final String INTENT_PARAM_CHECKIN_COUNT = "checkin_count";

    public static final String INTENT_PARAM_SUMIT_OFFER_OUTLET_NAME = "outlet_names";
    public static final String INTENT_PARAM_FROM_DRAWER = "from_drawer";
    public static final String INTENT_PARAM_FROM_PUSH_NOTIFICATION_CLICKED = "from_push_notification";
    public static final String INTENT_PARAM_SUMIT_OFFER_OUTLET_ADDRESS ="address" ;
    public static final String PREFERENCE_LOCALITY_SHOWN_DRAWER = "locality";
    public static final String PREFERENCE_PREVIOUS_LOCALITY_SHOWN_DRAWER = "previous_locality";
    public static final String PREFERENCE_CURRENT_LAT = "current_lat";
    public static final String PREFERENCE_CURRENT_LNG = "current_lng";
    public static final String PREFERENCE_CHECK_FIRST_LAUNCH = "check_first_launch";
}
