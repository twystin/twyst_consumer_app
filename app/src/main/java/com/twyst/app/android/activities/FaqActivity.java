package com.twyst.app.android.activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.MailTo;
import android.os.Bundle;
import android.support.v4.view.GravityCompat;
import android.text.Html;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.appsflyer.AppsFlyerLib;
import com.twyst.app.android.R;
import com.twyst.app.android.util.AppConstants;
import com.twyst.app.android.util.TwystProgressHUD;

/**
 * Created by rahuls on 20/8/15.
 */
public class FaqActivity extends BaseActivity {
    private boolean fromDrawer;

    @Override
    protected String getTagName() {
        return null;
    }

    @Override
    protected int getLayoutResource() {
        return R.layout.activity_faq;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setupAsChild=true;
        super.onCreate(savedInstanceState);
        fromDrawer = getIntent().getBooleanExtra(AppConstants.INTENT_PARAM_FROM_DRAWER, false);

        final ScrollView scrollView = (ScrollView) findViewById(R.id.scrollView);

        TextView tvFAQ = (TextView)findViewById(R.id.tvFAQ);
        tvFAQ.setText(Html.fromHtml(getString(R.string.faq_body)));

        final WebView webView = (WebView) findViewById(R.id.webView);
        webView.getSettings().setJavaScriptEnabled(true); // enable javascript
        final TwystProgressHUD twystProgressHUD = TwystProgressHUD.show(this, false, null);

        webView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                // TODO show you progress image
                super.onPageStarted(view, url, favicon);
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                twystProgressHUD.dismiss();
                // TODO hide your progress image
                super.onPageFinished(view, url);
            }

            @Override
            public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
                twystProgressHUD.dismiss();
                scrollView.setVisibility(View.VISIBLE);
                webView.setVisibility(View.INVISIBLE);
//                Toast.makeText(FaqActivity.this, description, Toast.LENGTH_SHORT).show();
            }

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                if (url.startsWith("mailto:")) {
                        MailTo mt = MailTo.parse(url);
                        Intent i = newEmailIntent(FaqActivity.this, mt.getTo(), mt.getSubject(), mt.getBody(), mt.getCc());
                        FaqActivity.this.startActivity(i);
                        view.reload();
                        return true;

                } else {
                    view.loadUrl(url);
                }
                return true;
            }

        });

        webView .loadUrl(AppConstants.HOST + "/api/v4/faq");
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

    private Intent newEmailIntent(Context context, String address, String subject, String body, String cc) {
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.putExtra(Intent.EXTRA_EMAIL, new String[] { address });
        intent.putExtra(Intent.EXTRA_TEXT, body);
        intent.putExtra(Intent.EXTRA_SUBJECT, subject);
        intent.putExtra(Intent.EXTRA_CC, cc);
        intent.setType("message/rfc822");
        return intent;
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
