package in.twyst.util;

/**
 * Created by vivek on 06/08/15.
 */
public class GoogleConstants {
    public static String CLIENT_ID = "294028196551-74pbbkp0e2sav1dsb92g7s2akrf95i9s.apps.googleusercontent.com";
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
