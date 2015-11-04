package com.twyst.app.android.service;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;
import android.util.LruCache;

import com.google.android.gms.analytics.Tracker;
import com.squareup.okhttp.Cache;
import com.squareup.okhttp.OkHttpClient;

import java.util.ArrayList;
import java.util.Map;

import com.twyst.app.android.model.AuthToken;
import com.twyst.app.android.model.BaseResponse;
import com.twyst.app.android.model.CheckinData;
import com.twyst.app.android.model.Data;
import com.twyst.app.android.model.DiscoverData;
import com.twyst.app.android.model.Feedback;
import com.twyst.app.android.model.Friend;
import com.twyst.app.android.model.GrabOffer;
import com.twyst.app.android.model.NotificationData;
import com.twyst.app.android.model.Profile;
import com.twyst.app.android.model.LikeOffer;
import com.twyst.app.android.model.LocationData;
import com.twyst.app.android.model.OTPCode;
import com.twyst.app.android.model.OutletDetailData;
import com.twyst.app.android.model.ProfileUpdate;
import com.twyst.app.android.model.Referral;
import com.twyst.app.android.model.ReportProblem;
import com.twyst.app.android.model.ShareOffer;
import com.twyst.app.android.model.ShareOutlet;
import com.twyst.app.android.model.SubmitOffer;
import com.twyst.app.android.model.Suggestion;
import com.twyst.app.android.model.UpdateProfile;
import com.twyst.app.android.model.UploadBill;
import com.twyst.app.android.model.UseOffer;
import com.twyst.app.android.model.UserLocation;
import com.twyst.app.android.model.Voucher;
import com.twyst.app.android.model.WalletData;
import com.twyst.app.android.model.WriteToUs;
import com.twyst.app.android.util.AppConstants;
import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.android.AndroidLog;
import retrofit.client.OkClient;
import retrofit.client.Response;
/**
 * Created by satish on 30/11/14.
 */
public class HttpService {
    private static HttpService instance = new HttpService();
    private Context context;
    private Tracker tracker;
    private LruCache<String, Object> cache = new LruCache<>(4 * 1024 * 1024);
    private TwystService twystService;

    private HttpService() {
    }

    public static HttpService getInstance() {
        return instance;
    }

    public void setup(Context context, Tracker tracker) {
        this.context = context;
        this.tracker = tracker;


        OkHttpClient okHttpClient = new OkHttpClient();
        OkClient okClient = new OkClient(okHttpClient);

        Cache cache = new Cache(context.getCacheDir(), 1024);
        okHttpClient.setCache(cache);
        RestAdapter jsonRestAdapter = new RestAdapter.Builder()
//                .setLogLevel(RestAdapter.LogLevel.FULL).setLog(new AndroidLog("Retrofit"))
                .setLogLevel((AppConstants.IS_DEVELOPMENT) ? RestAdapter.LogLevel.FULL : RestAdapter.LogLevel.NONE)
                .setEndpoint(AppConstants.HOST)
                .setClient(okClient)
                .build();
        twystService = jsonRestAdapter.create(TwystService.class);
    }

    public SharedPreferences getSharedPreferences() {
        return context.getSharedPreferences(AppConstants.PREFERENCE_SHARED_PREF_NAME, Context.MODE_PRIVATE);
    }

    public void registerDevice(String appName, String appVersion, String deviceUUID, String deviceToken, String deviceModel, String deviceVersion, Callback<Response> callback) {

    }


    /**
     * ************************** TwsytServices Start********************************
     */

    public void getMobileAuthCode(String mobile, Callback<BaseResponse<OTPCode>> callback) {
        twystService.getMobileAuthCode(mobile, callback);
    }


    public void userAuthToken(String code, String phone, Callback<BaseResponse<AuthToken>> callback) {
        twystService.userAuthToken(code, phone, callback);
    }

    public void updateProfile(String token, UpdateProfile updateProfile, Callback<BaseResponse<ProfileUpdate>> callback) {
        twystService.updateProfile(token, updateProfile, callback);
    }

    public void getRecommendedOutlets(String userToken, int start,int end, String lat, String lng, String date, String time, Callback<BaseResponse<DiscoverData>> callback) {

        if (TextUtils.isEmpty(date) && TextUtils.isEmpty(time)){
            date = null;
            time = null;
        }
        twystService.getRecommendedOutlets(userToken, start, end, lat, lng, date, time, callback);
    }

    public void getOutletDetails(String outletId,String userToken,String lat, String lng ,Callback<BaseResponse<OutletDetailData>> callback) {
        twystService.getOutletDetails(outletId, userToken, lat, lng, callback);
    }

    public void getOutletsList(Callback<BaseResponse> callback) {
        twystService.getOutletsList(callback);
    }

    public void updateSocialFriends(String token, Friend friend, Callback<BaseResponse<ProfileUpdate>> callback){
        twystService.updateSocialFriends(token, friend, callback);
    }

    public void followEvent(String token, String outletId, Callback<BaseResponse<Data>> callback){
        twystService.followEvent(token, outletId, callback);
    }

    public void unFollowEvent(String token, String outletId, Callback<BaseResponse<Data>> callback){
        twystService.unFollowEvent(token, outletId, callback);
    }

    public void outletFeedback(String token, Feedback feedback, Callback<BaseResponse> callback){
        twystService.outletFeedback(token, feedback, callback);
    }


    public void getLocations(Callback<BaseResponse<ArrayList<LocationData>>> callback){
        twystService.getLocations(callback);
    }

    public void postSuggestion(String token, Suggestion suggestion, Callback<BaseResponse> callback){
        twystService.postSuggestion(token, suggestion, callback);
    }

    public void postSubmitOffer(String token, SubmitOffer submitOffer, Callback<BaseResponse> callback){
        twystService.postSubmitOffer(token, submitOffer, callback);
    }

    public void postCheckin(String token, CheckinData checkinData, Callback<BaseResponse> callback){
        twystService.postCheckin(token, checkinData, callback);
    }

    public void shareOutlet(String token, ShareOutlet shareOutlet, Callback<BaseResponse> callback){
        twystService.shareOutlet(token, shareOutlet, callback);
    }

    public void shareOffer(String token, ShareOffer shareOffer, Callback<BaseResponse> callback){
        twystService.shareOffer(token, shareOffer, callback);
    }

    public void postLikeOffer(String token, LikeOffer likeOffer, Callback<BaseResponse<Data>> callback){
        twystService.postLikeOffer(token, likeOffer, callback);
    }

    public void postUnLikeOffer(String token, LikeOffer likeOffer, Callback<BaseResponse> callback){
        twystService.postUnLikeOffer(token, likeOffer, callback);
    }

    public void extendVoucher(String token, Voucher voucher, Callback<BaseResponse> callback){
        twystService.extendVoucher(token, voucher, callback);
    }

    public void grabOffer(String token, GrabOffer grabOffer, Callback<BaseResponse> callback) {
        twystService.grabOffer(token, grabOffer, callback);
    }

    public void uploadBill(String token, UploadBill uploadBill, Callback<BaseResponse> callback){
        twystService.uploadBill(token, uploadBill, callback);
    }

    public void getCoupons(String userToken, String lat, String lng, Callback<BaseResponse<WalletData>> callback) {
        twystService.getCoupons(userToken,lat,lng, callback);
    }

    public void writeToUs(String token, WriteToUs writeToUs, Callback<BaseResponse> callback){
        twystService.writeToUs(token, writeToUs, callback);
    }

    public void postDealLog(String token, UseOffer useOffer, Callback<BaseResponse> callback){
        twystService.postDealLog(token, useOffer, callback);
    }

    public void postGenerateCoupon(String token, UseOffer useOffer, Callback<BaseResponse> callback){
        twystService.postGenerateCoupon(token, useOffer, callback);
    }

    public void postRedeemCoupon(String token, UseOffer useOffer, Callback<BaseResponse> callback){
        twystService.postRedeemCoupon(token, useOffer, callback);
    }

    public void postReferral(String token, Referral referral, Callback<BaseResponse> callback){
        twystService.postReferral(token, referral, callback);
    }

    public void reportProblem(String token,ReportProblem reportProblem,Callback<BaseResponse>callback){
        twystService.reportProblem(token, reportProblem, callback);
    }

    public void getProfile(String token, Callback<BaseResponse<Profile>> callback){
        twystService.getProfile(token, callback);
    }

    public void searchOffer(String token, String searchText, String lat,String lng, String date, String time , Callback<BaseResponse<DiscoverData>> callback){
        twystService.searchOffer(token, searchText,lat,lng,date,time, callback);
    }

    public void postLocation(String token, UserLocation userLocation, Callback<BaseResponse> callback){
        twystService.postLocation(token, userLocation, callback);
    }

    public void getNotification(String token, Callback<BaseResponse<ArrayList<NotificationData>>> callback){
        twystService.getNotification(token, callback);
    }

}
