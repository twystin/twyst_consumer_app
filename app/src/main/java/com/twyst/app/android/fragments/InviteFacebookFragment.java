package com.twyst.app.android.fragments;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.applinks.AppLinkData;
import com.facebook.share.model.AppInviteContent;
import com.facebook.share.widget.AppInviteDialog;

import bolts.AppLinks;
import com.twyst.app.android.R;

/**
 * Created by vivek on 05/08/15.
 */
public class InviteFacebookFragment extends Fragment {

    String appLinkUrl, previewImageUrl;
    private CallbackManager sCallbackManager;

    public InviteFacebookFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        FacebookSdk.sdkInitialize(getActivity().getApplicationContext());
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_invite_facebook, container, false);

        appLinkUrl = "https://fb.me/390796804452162?referral=123456789";
        previewImageUrl = "http://www.flint-group.com/wp-content/uploads/2014/09/SocialMediaAdvertising_FeaturedImage_a.png";
        sCallbackManager = CallbackManager.Factory.create();
        view.findViewById(R.id.facebookInvite).setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
               if (AppInviteDialog.canShow()) {
                   AppInviteContent content = new AppInviteContent.Builder()
                           .setApplinkUrl(appLinkUrl)
                           .setPreviewImageUrl(previewImageUrl)
                           .build();
                   AppInviteDialog appInviteDialog = new AppInviteDialog(getActivity());
                   appInviteDialog.registerCallback(sCallbackManager,
                           new FacebookCallback<AppInviteDialog.Result>() {
                               @Override
                               public void onSuccess(AppInviteDialog.Result result) {
                                   Log.d("Invitation", "Invitation Sent Successfully");
                               }

                               @Override
                               public void onCancel() {
                                   Log.d("Invitation", "Invitation Cancellled");
                               }

                               @Override
                               public void onError(FacebookException e) {
                                   Log.d("Invitation", "Error Occured");
                               }
                           });

                   appInviteDialog.show(content);
               }
           }
        });

        Uri targetUrl = AppLinks.getTargetUrlFromInboundIntent(getActivity().getApplicationContext(), getActivity().getIntent());
        Log.i("Activity", "targetUrl: " + targetUrl);
        if (targetUrl != null) {

            String refCode = targetUrl.getQueryParameter("referral");
            Log.i("Activity", "Referral Code: " + refCode);
        } else {
            AppLinkData.fetchDeferredAppLinkData(
                    getActivity(),
                    new AppLinkData.CompletionHandler() {
                        @Override
                        public void onDeferredAppLinkDataFetched(AppLinkData appLinkData) {
                            //process applink data
                            Log.i("Activity", "appLinkData: " + appLinkData);
                        }
                    });
        }

        return view;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        sCallbackManager.onActivityResult(requestCode, resultCode, data);
    }
}
