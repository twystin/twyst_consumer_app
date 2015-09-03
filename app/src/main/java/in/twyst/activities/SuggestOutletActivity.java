package in.twyst.activities;

import android.app.AlertDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import in.twyst.R;
import in.twyst.model.BaseResponse;
import in.twyst.model.Suggestion;
import in.twyst.model.SuggestionMeta;
import in.twyst.service.HttpService;
import in.twyst.util.AppConstants;
import in.twyst.util.TwystProgressHUD;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by rahuls on 7/8/15.
 */
public class SuggestOutletActivity extends BaseActivity{
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

        hideProgressHUDInLayout();


        final EditText outletNameET = (EditText)findViewById(R.id.outletNameET);
        final EditText outletLocET = (EditText)findViewById(R.id.outletLocET);
        final EditText commentEt = (EditText)findViewById(R.id.commentEt);

        findViewById(R.id.suggestOfferBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (!TextUtils.isEmpty(outletLocET.getText()) && !TextUtils.isEmpty(outletNameET.getText())) {
                    final TwystProgressHUD twystProgressHUD = TwystProgressHUD.show(SuggestOutletActivity.this, false, null);
                    SharedPreferences prefs = getSharedPreferences(AppConstants.PREFERENCE_SHARED_PREF_NAME, Context.MODE_PRIVATE);
                    String usertoken = prefs.getString(AppConstants.PREFERENCE_USER_TOKEN, "");

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

                    HttpService.getInstance().postSuggestion(usertoken, suggestion, new Callback<BaseResponse>() {
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
}
