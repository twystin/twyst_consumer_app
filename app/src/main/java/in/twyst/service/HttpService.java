package in.twyst.service;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.LruCache;

import com.google.android.gms.analytics.Tracker;
import com.squareup.okhttp.Cache;
import com.squareup.okhttp.OkHttpClient;

import org.json.JSONArray;

import java.util.ArrayList;
import java.util.List;

import in.twyst.model.AuthToken;
import in.twyst.model.BaseResponse;
import in.twyst.model.CheckinData;
import in.twyst.model.Data;
import in.twyst.model.DiscoverData;
import in.twyst.model.Feedback;
import in.twyst.model.Friend;
import in.twyst.model.FriendData;
import in.twyst.model.GetFriend;
import in.twyst.model.LikeOffer;
import in.twyst.model.LocationData;
import in.twyst.model.OTPCode;
import in.twyst.model.Outlet;
import in.twyst.model.OutletDetailData;
import in.twyst.model.ProfileUpdate;
import in.twyst.model.Referral;
import in.twyst.model.ReportProblem;
import in.twyst.model.ShareOffer;
import in.twyst.model.ShareOutlet;
import in.twyst.model.SubmitOffer;
import in.twyst.model.Suggestion;
import in.twyst.model.UploadBill;
import in.twyst.model.UseOffer;
import in.twyst.model.UseOfferData;
import in.twyst.model.Voucher;
import in.twyst.model.WalletData;
import in.twyst.model.WriteToUs;
import in.twyst.util.AppConstants;
import retrofit.Callback;
import retrofit.RestAdapter;
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

    public void updateProfile(String token, String email,String deviceId,String version,String device,String model,String product, Callback<BaseResponse<ProfileUpdate>> callback) {
        twystService.updateProfile(token, email, deviceId, version, device, model, product, callback);
    }

    public void getRecommendedOutlets(String userToken, int start,String lat, String lng, String date, String time, Callback<BaseResponse<DiscoverData>> callback) {

        int end = start + AppConstants.DISCOVER_LIST_PAGESIZE - 1;
        twystService.getRecommendedOutlets(userToken, start, end, lat, lng, date, time, callback);
    }

    public void getOutletDetails(String outletId,String userToken,String lat, String lng ,Callback<BaseResponse<OutletDetailData>> callback) {
        twystService.getOutletDetails(outletId, userToken, lat, lng, callback);
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

    public void postLikeOffer(String token, LikeOffer likeOffer, Callback<BaseResponse> callback){
        twystService.postLikeOffer(token, likeOffer, callback);
    }

    public void postUnLikeOffer(String token, LikeOffer likeOffer, Callback<BaseResponse> callback){
        twystService.postUnLikeOffer(token, likeOffer, callback);
    }

    public void extendVoucher(String token, Voucher voucher, Callback<BaseResponse> callback){
        twystService.extendVoucher(token, voucher, callback);
    }

    public void uploadBill(String token, UploadBill uploadBill, Callback<BaseResponse> callback){
        twystService.uploadBill(token, uploadBill, callback);
    }

    public void getCoupons(String userToken, Callback<BaseResponse<WalletData>> callback) {
        twystService.getCoupons(userToken, callback);
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

    public void getProfile(String token, Callback<BaseResponse<GetFriend>> callback){
        twystService.getProfile(token, callback);
    }
}
