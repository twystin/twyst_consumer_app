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
import com.twyst.app.android.model.WriteMeta;
import com.twyst.app.android.model.WriteToUs;
import com.twyst.app.android.service.HttpService;
import com.twyst.app.android.util.AppConstants;
import com.twyst.app.android.util.TwystProgressHUD;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by rahuls on 7/8/15.
 */
public class WriteToUsActivity extends BaseActivity{
    private boolean fromDrawer;
    @Override
    protected String getTagName() {
        return null;
    }

    @Override
    protected int getLayoutResource() {
        return R.layout.activity_write_to_us;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setupAsChild = true;
        super.onCreate(savedInstanceState);
        fromDrawer = getIntent().getBooleanExtra(AppConstants.INTENT_PARAM_FROM_DRAWER, false);

        hideProgressHUDInLayout();
        final EditText commentEt = (EditText)findViewById(R.id.commentEt);

        findViewById(R.id.writeToUsBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final TwystProgressHUD twystProgressHUD = TwystProgressHUD.show(WriteToUsActivity.this, false, null);
                if(TextUtils.isEmpty(commentEt.getText())){
                    Toast.makeText(WriteToUsActivity.this,"Please fill all the required fields.",Toast.LENGTH_SHORT).show();
                    twystProgressHUD.dismiss();
                    commentEt.setError("Comments required");
                    twystProgressHUD.dismiss();
                }else {
                    WriteToUs writeToUs = new WriteToUs();
                    WriteMeta writeMeta = new WriteMeta();
                    writeMeta.setComments(commentEt.getText().toString());
                    writeToUs.setWriteMeta(writeMeta);
                    HttpService.getInstance().writeToUs(getUserToken(), writeToUs, new Callback<BaseResponse>() {
                        @Override
                        public void success(BaseResponse baseResponse, Response response) {
                            Toast.makeText(WriteToUsActivity.this,"Your comment has been sent to Twyst.",Toast.LENGTH_SHORT).show();
                            twystProgressHUD.dismiss();
                            finish();
                        }

                        @Override
                        public void failure(RetrofitError error) {
                            twystProgressHUD.dismiss();
                            handleRetrofitError(error);
                        }
                    });
                }

            }
        });

        findViewById(R.id.writeCancelBtn).setOnClickListener(new View.OnClickListener() {
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
