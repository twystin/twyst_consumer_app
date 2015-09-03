package in.twyst.activities;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import in.twyst.R;
import in.twyst.model.BaseResponse;
import in.twyst.model.WalletData;
import in.twyst.model.WriteMeta;
import in.twyst.model.WriteToUs;
import in.twyst.service.HttpService;
import in.twyst.util.AppConstants;
import in.twyst.util.TwystProgressHUD;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by rahuls on 7/8/15.
 */
public class WriteToUsActivity extends BaseActivity{
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


        hideProgressHUDInLayout();
        final EditText commentEt = (EditText)findViewById(R.id.commentEt);

        SharedPreferences prefs = this.getSharedPreferences(AppConstants.PREFERENCE_SHARED_PREF_NAME, Context.MODE_PRIVATE);
        final String token = prefs.getString(AppConstants.PREFERENCE_USER_TOKEN, "");
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
                    HttpService.getInstance().writeToUs(token, writeToUs, new Callback<BaseResponse>() {
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
}
