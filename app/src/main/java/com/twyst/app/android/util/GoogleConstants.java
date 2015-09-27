package com.twyst.app.android.util;

/**
 * Created by vivek on 06/08/15.
 */
public class GoogleConstants {
    public static String CLIENT_ID = "216832068690-72hsih81arcfahksiq6v80c1jjaadj7n.apps.googleusercontent.com";


    // Release android client
//    public static String CLIENT_ID = "216832068690-ijfc8kju5dk3j0jo70ndghhlusnfhff0.apps.googleusercontent.com";
    // Use your own client id

    public static String CLIENT_SECRET = "dxGAMA3lWQInqHziNonWUyWu";
    // Use your own client secret

    public static String REDIRECT_URI = "http://localhost";
    public static String GRANT_TYPE = "authorization_code";
    public static String TOKEN_URL = "https://accounts.google.com/o/oauth2/token";
    public static String OAUTH_URL = "https://accounts.google.com/o/oauth2/auth";
    public static String OAUTH_SCOPE = "https://www.googleapis.com/auth/contacts.readonly";

    public static final String CONTACTS_URL = "https://www.google.com/m8/feeds/contacts/default/full";
    public static final int MAX_NB_CONTACTS = 1000;
    public static final String APP = "Twyst";
}
