package com.twyst.app.android.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.GravityCompat;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.appsflyer.AppsFlyerLib;
import com.twyst.app.android.R;
import com.twyst.app.android.model.BaseResponse;
import com.twyst.app.android.model.Suggestion;
import com.twyst.app.android.model.SuggestionMeta;
import com.twyst.app.android.service.HttpService;
import com.twyst.app.android.util.AppConstants;
import com.twyst.app.android.util.TwystProgressHUD;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by rahuls on 7/8/15.
 */
public class SuggestOutletActivity extends BaseActivity{

    private boolean fromDrawer;

    @Override
    protected String getTagName() {
        return null;
    }

    @Override
    protected int getLayoutResource() {
        return R.layout.activity_suggest_outlet;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setupAsChild= true;
        super.onCreate(savedInstanceState);
        fromDrawer = getIntent().getBooleanExtra(AppConstants.INTENT_PARAM_FROM_DRAWER, false);
        hideProgressHUDInLayout();


        final EditText outletNameET = (EditText)findViewById(R.id.outletNameET);
        final EditText outletLocET = (EditText)findViewById(R.id.outletLocET);
        final EditText commentEt = (EditText)findViewById(R.id.commentEt);

        findViewById(R.id.suggestOfferBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (!TextUtils.isEmpty(outletLocET.getText()) && !TextUtils.isEmpty(outletNameET.getText())) {
                    final TwystProgressHUD twystProgressHUD = TwystProgressHUD.show(SuggestOutletActivity.this, false, null);

                    Suggestion suggestion = new Suggestion();
                    SuggestionMeta suggestionMeta = new SuggestionMeta();
                    suggestionMeta.setOutlet(outletNameET.getText().toString());
                    suggestionMeta.setLocation(outletLocET.getText().toString());
                    if(TextUtils.isEmpty(commentEt.getText())){
                        suggestionMeta.setComment("");
                    }else{
                        suggestionMeta.setComment(commentEt.getText().toString());
                    }

                    suggestion.setSuggestionMeta(suggestionMeta);

                    HttpService.getInstance().postSuggestion(getUserToken(), suggestion, new Callback<BaseResponse>() {
                        @Override
                        public void success(BaseResponse baseResponse, Response response) {

                            if (baseResponse.isResponse()) {
                                Toast.makeText(SuggestOutletActivity.this, "Outlet suggestion sent to Twyst.", Toast.LENGTH_LONG).show();
                                twystProgressHUD.dismiss();
                                finish();
                            }
                        }

                        @Override
                        public void failure(RetrofitError error) {
                            twystProgressHUD.dismiss();
                            if (error.getKind() == RetrofitError.Kind.NETWORK) {
                                handleRetrofitError(error);
                            } else {
                                buildAndShowSnackbarWithMessage("Unable to submit outlet!");
                            }
                        }
                    });
                } else if (TextUtils.isEmpty(outletLocET.getText()) && !TextUtils.isEmpty(outletNameET.getText())) {
                    outletLocET.setError("Outlet location required");
                } else if (!TextUtils.isEmpty(outletLocET.getText()) && TextUtils.isEmpty(outletNameET.getText())) {
                    outletNameET.setError("Outlet name required");
                }  else {
                    outletNameET.setError("Outlet name required");
                    outletLocET.setError("Outlet location required");
                }

            }
        });

        findViewById(R.id.suggestCancelBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
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
}
