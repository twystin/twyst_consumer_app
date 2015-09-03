package in.twyst.service;

import com.squareup.okhttp.Call;

import org.json.JSONArray;

import java.util.ArrayList;
import java.util.List;

import in.twyst.model.AuthToken;
import in.twyst.model.BaseResponse;
import in.twyst.model.CheckinData;
import in.twyst.model.Data;
import in.twyst.model.Friend;
import in.twyst.model.FriendData;
import in.twyst.model.GetFriend;
import in.twyst.model.Referral;
import in.twyst.model.ReportProblem;
import in.twyst.model.UseOffer;
import in.twyst.model.DiscoverData;
import in.twyst.model.Feedback;
import in.twyst.model.LikeOffer;
import in.twyst.model.LocationData;
import in.twyst.model.OTPCode;
import in.twyst.model.OutletDetailData;
import in.twyst.model.ProfileUpdate;
import in.twyst.model.ShareOffer;
import in.twyst.model.ShareOutlet;
import in.twyst.model.SubmitOffer;
import in.twyst.model.Suggestion;
import in.twyst.model.UploadBill;
import in.twyst.model.UseOfferData;
import in.twyst.model.Voucher;
import in.twyst.model.WalletData;
import in.twyst.model.WriteToUs;
import retrofit.Callback;
import retrofit.http.Body;
import retrofit.http.Field;
import retrofit.http.FormUrlEncoded;
import retrofit.http.GET;
import retrofit.http.POST;
import retrofit.http.PUT;
import retrofit.http.Path;
import retrofit.http.Query;

/**
 * Created by satishk on 5/6/15.
 */
public interface TwystService {

    @GET("/api/v4/authcode/{mobileno}")
    public void getMobileAuthCode(@Path("mobileno") String mobile, Callback<BaseResponse<OTPCode>> callback);

    @FormUrlEncoded
    @POST("/api/v4/authcode")
    public void userAuthToken(@Field("code") String code, @Field("phone") String phone, Callback<BaseResponse<AuthToken>> callback);

    @FormUrlEncoded
    @PUT("/api/v4/profile")
    public void updateProfile(@Query("token") String token, @Field("email") String email, @Field("gcmId") String deviceId,@Field("os_version") String version ,@Field("device") String device,@Field("model") String model,@Field("product") String product, Callback<BaseResponse<ProfileUpdate>> callback);

    @GET("/api/v4/recos")
    public void getRecommendedOutlets(@Query("token") String token, @Query("start") int start, @Query("end") int end, @Query("lat") String lat, @Query("long") String lng, @Query("date") String date,@Query("time") String time, Callback<BaseResponse<DiscoverData>> callback);

    @GET("/api/v4/outlets/{outlet_id}")
    public void getOutletDetails(@Path("outlet_id") String outletId, @Query("token")String token, @Query("lat") String lat, @Query("long") String lng,Callback<BaseResponse<OutletDetailData>> callback);

    @PUT("/api/v4/friends")
    public void updateSocialFriends(@Query("token") String token ,@Body Friend friend, Callback<BaseResponse<ProfileUpdate>> callback);

    @FormUrlEncoded
    @POST("/api/v4/outlet/follow")
    public void followEvent(@Query("token") String token, @Field("event_outlet") String outletId, Callback<BaseResponse<Data>> callback);

    @FormUrlEncoded
    @POST("/api/v4/outlet/unfollow")
    public void unFollowEvent(@Query("token") String token, @Field("event_outlet") String outletId, Callback<BaseResponse<Data>> callback);

    @POST("/api/v4/outlet/feedback")
    public void outletFeedback(@Query("token") String token, @Body() Feedback feedback, Callback<BaseResponse> callback);

    @GET("/api/v4/coupons")
    public void getCoupons(@Query("token") String token, Callback<BaseResponse<WalletData>> callback);

    @GET("/api/v4/locations")
    public void getLocations(Callback<BaseResponse<ArrayList<LocationData>>> callback);

    @POST("/api/v4/outlet/suggestion")
    public void postSuggestion(@Query("token") String token, @Body() Suggestion suggestion, Callback<BaseResponse> callback);

    @POST("/api/v4/offer/submit")
    public void postSubmitOffer(@Query("token") String token, @Body() SubmitOffer submitOffer, Callback<BaseResponse> callback);

    @POST("/api/v4/checkin/qr")
    public void postCheckin(@Query("token") String token, @Body() CheckinData checkinData, Callback<BaseResponse> callback);

    @POST("/api/v4/offer/like")
    public void postLikeOffer(@Query("token") String token, @Body() LikeOffer likeOffer, Callback<BaseResponse> callback);

    @POST("/api/v4/offer/unlike")
    public void postUnLikeOffer(@Query("token") String token, @Body() LikeOffer likeOffer, Callback<BaseResponse> callback);

    @POST("/api/v4/outlet/share")
    public void shareOutlet(@Query("token") String token, @Body() ShareOutlet shareOutlet, Callback<BaseResponse> callback);

    @POST("/api/v4/offer/share")
    public void shareOffer(@Query("token") String token, @Body() ShareOffer shareOffer, Callback<BaseResponse> callback);

    @POST("/api/v4/offer/extend")
    public void extendVoucher(@Query("token") String token, @Body() Voucher voucher, Callback<BaseResponse> callback);

    @POST("/api/v4/checkin/bill")
    public void uploadBill(@Query("token") String token, @Body() UploadBill uploadBill, Callback<BaseResponse> callback);

    @POST("/api/v4/comments")
    public void writeToUs(@Query("token") String token, @Body() WriteToUs writeToUs, Callback<BaseResponse> callback);

    @POST("/api/v4/deal/log")
    public void postDealLog(@Query("token") String token, @Body() UseOffer useOffer, Callback<BaseResponse> callback);

    @POST("/api/v4/offer/generate/coupon")
    public void postGenerateCoupon(@Query("token") String token, @Body() UseOffer useOffer, Callback<BaseResponse> callback);

    @POST("/api/v4/coupon/redeem")
    public void postRedeemCoupon(@Query("token") String token, @Body() UseOffer useOffer, Callback<BaseResponse> callback);


    @POST("/api/v4/referral/join")
    public void postReferral(@Query("token") String token, @Body() Referral referral, Callback<BaseResponse> callback);


    @POST("/api/v4/offer/report/problem")
    public void reportProblem(@Query("token") String token,@Body()ReportProblem reportProblem,Callback<BaseResponse> callback);

    @GET("/api/v4/profile")
    public void getProfile(@Query("token") String token, Callback<BaseResponse<GetFriend>> callback);

}
