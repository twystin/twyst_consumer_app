package com.twyst.app.android.activities;

import android.app.AlertDialog;
import android.app.SearchManager;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ResolveInfo;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Parcelable;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.gson.Gson;
import com.nispok.snackbar.Snackbar;
import com.nispok.snackbar.SnackbarManager;
import com.nispok.snackbar.enums.SnackbarType;
import com.nispok.snackbar.listeners.ActionClickListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import fr.castorflex.android.circularprogressbar.CircularProgressBar;
import com.twyst.app.android.R;
import com.twyst.app.android.adapters.DrawerListAdapter;
import com.twyst.app.android.model.DrawerItem;
import com.twyst.app.android.model.OutletList;
import com.twyst.app.android.util.AppConstants;
import com.twyst.app.android.util.RoundedTransformation;
import com.twyst.app.android.util.TwystProgressHUD;

import org.json.JSONException;
import org.json.JSONObject;
import org.mortbay.util.ajax.JSON;

import retrofit.RetrofitError;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

/**
 * Created by satish on 04/12/14.
 */
public abstract class BaseActivity extends ActionBarActivity
        implements SearchView.OnQueryTextListener, SearchView.OnSuggestionListener {

    protected static final int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
    protected ActionBarDrawerToggle mDrawerToggle;
    protected Toolbar toolbar;
    private SearchView searchView;
    public Menu menu;
    private CharSequence mTitle;
    private ArrayList<DrawerItem> drawerItems;
    private ListView drawerList;
    protected DrawerLayout drawerLayout;
    private CircularProgressBar circularProgressBar;
    protected boolean setupAsChild;
    protected boolean drawerOpened;
    private DrawerItem invite,faq,bill,wallet,notifications,submitOffer,suggestOutlet,write,rate;
    protected SharedPreferences.Editor sharedPreferences;

    TextView localityDrawer;
    TextView userName;
    ImageView backImage;
    ImageView userImage;

    protected abstract String getTagName();

    protected abstract int getLayoutResource();

    public void hideProgressHUDInLayout() {
        if (circularProgressBar != null) {
            circularProgressBar.progressiveStop();
            circularProgressBar.setVisibility(View.GONE);
        }
    }

    //Applying font to the BaseActivity
    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getLayoutResource());

        circularProgressBar = (CircularProgressBar) findViewById(R.id.circularProgressBar);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        sharedPreferences = getSharedPreferences(AppConstants.PREFERENCE_SHARED_PREF_NAME, Context.MODE_PRIVATE).edit();
        // Set an OnMenuItemClickListener to handle menu item clicks
        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                Log.d(BaseActivity.class.getSimpleName(), "Toolbar item clicked: " + item.getItemId());
                switch (item.getItemId()) {
                    case R.id.action_notifications:
                        showNotifications();
                        break;

                    case R.id.action_wallet:
                        showWallet();
                        break;

                    case R.id.action_home:
                        showHome();
                        break;

                }

                return true;
            }
        });

        mTitle = getTitle();
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawerLayout.setDrawerShadow(R.drawable.drawer_shadow, GravityCompat.START);

        mDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.drawer_open, R.string.drawer_close) {
            public void onDrawerClosed(View view) {
                invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
                supportInvalidateOptionsMenu();
                invite.setSelected(false);
                rate.setSelected(false);
                faq.setSelected(false);
                notifications.setSelected(false);
                bill.setSelected(false);
                wallet.setSelected(false);
                submitOffer.setSelected(false);
                suggestOutlet.setSelected(false);
                write.setSelected(false);
                rate.setSelected(false);
                drawerOpened = false;
            }

            public void onDrawerOpened(View drawerView) {
                invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
                supportInvalidateOptionsMenu();
                getSupportActionBar().setTitle(mTitle);
                invite.setSelected(false);
                rate.setSelected(false);
                faq.setSelected(false);
                notifications.setSelected(false);
                bill.setSelected(false);
                wallet.setSelected(false);
                submitOffer.setSelected(false);
                suggestOutlet.setSelected(false);
                write.setSelected(false);
                rate.setSelected(false);
                drawerOpened = true;
            }
        };

        drawerLayout.setDrawerListener(mDrawerToggle);
        drawerLayout.setStatusBarBackgroundColor(getResources().getColor(R.color.colorPrimaryDark));

        drawerList = (ListView) findViewById(R.id.drawer_listview);
        View list_header = getLayoutInflater().inflate(R.layout.drawerlist_header, null);

        userName = (TextView) list_header.findViewById(R.id.userName);
        backImage = (ImageView) list_header.findViewById(R.id.backImage);
        userImage = (ImageView) list_header.findViewById(R.id.userImage);
        TextView editProfile = (TextView) list_header.findViewById(R.id.editProfile);
        localityDrawer = (TextView) list_header.findViewById(R.id.localityDrawer);

        SharedPreferences prefs = getSharedPreferences(AppConstants.PREFERENCE_SHARED_PREF_NAME, Context.MODE_PRIVATE);
        String locality = prefs.getString(AppConstants.PREFERENCE_CURRENT_USED_LOCATION_NAME, "");
        localityDrawer.setText(locality);

        updatePicName();

        editProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (drawerOpened) {
                    drawerLayout.closeDrawer(GravityCompat.START);
                }
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        Intent intent = new Intent(getBaseContext(), EditProfileActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                        intent.putExtra(AppConstants.INTENT_PARAM_FROM_DRAWER, true);
                        startActivity(intent);
                    }
                }, 40);

            }
        });


        drawerList.addHeaderView(list_header);

        createDrawerProfileBanner();
        //drawerList.setAdapter(new ArrayAdapter<String>(this, R.layout.drawer_list_item, navigationTitles));

        final int pos = prefs.getInt(AppConstants.PREFERENCE_LAST_DRAWERITEM_CLICKED, -1);
        drawerItems = getDrawerItems(pos);
        drawerList.setAdapter(new DrawerListAdapter(this, getLayoutInflater(), drawerItems));
        drawerList.setOnItemClickListener(new DrawerItemClickListener());

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        if (setupAsChild) {
            mDrawerToggle.setDrawerIndicatorEnabled(false);
            //drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
            toolbar.setNavigationIcon(R.drawable.abc_ic_ab_back_mtrl_am_alpha);
            toolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onBackPressed();
                }
            });
        }

        if(drawerOpened){
            invite.setSelected(false);
            rate.setSelected(false);
            faq.setSelected(false);
            notifications.setSelected(false);
            bill.setSelected(false);
            wallet.setSelected(false);
            submitOffer.setSelected(false);
            suggestOutlet.setSelected(false);
            write.setSelected(false);
            rate.setSelected(false);
        }else {
            invite.setSelected(false);
            rate.setSelected(false);
            faq.setSelected(false);
            notifications.setSelected(false);
            bill.setSelected(false);
            wallet.setSelected(false);
            submitOffer.setSelected(false);
            suggestOutlet.setSelected(false);
            write.setSelected(false);
            rate.setSelected(false);
        }

        setTitle(mTitle);
    }

    private ArrayList<DrawerItem> getDrawerItems(int pos) {
        ArrayList<DrawerItem> drawerItems = new ArrayList<>();

        invite = new DrawerItem();
        invite.setTitle("INVITE FRIENDS");
        invite.setIcon(R.drawable.drawer_item_icon_invite_friends);
        invite.setSelectedIcon(R.drawable.drawer_item_icon_invite_friends_selected);
        drawerItems.add(invite);

        bill = new DrawerItem();
        bill.setTitle("UPLOAD BILL");
        bill.setIcon(R.drawable.drawer_item_icon_upload_bill);
        bill.setSelectedIcon(R.drawable.drawer_item_icon_upload_bill_selected);
        drawerItems.add(bill);

        wallet = new DrawerItem();
        wallet.setTitle("MY WALLET");
        wallet.setIcon(R.drawable.drawer_item_icon_wallet);
        wallet.setSelectedIcon(R.drawable.drawer_item_icon_wallet_selected);
        drawerItems.add(wallet);

        submitOffer = new DrawerItem();
        submitOffer.setTitle("SUBMIT AN OFFER");
        submitOffer.setIcon(R.drawable.drawer_item_icon_submit_offer);
        submitOffer.setSelectedIcon(R.drawable.drawer_item_icon_submit_offer_selected);
        drawerItems.add(submitOffer);

        suggestOutlet = new DrawerItem();
        suggestOutlet.setTitle("SUGGEST AN OUTLET");
        suggestOutlet.setIcon(R.drawable.drawer_item_icon_suggest_outlet);
        suggestOutlet.setSelectedIcon(R.drawable.drawer_item_icon_suggest_outlet_selected);
        drawerItems.add(suggestOutlet);

        write = new DrawerItem();
        write.setTitle("WRITE TO US");
        write.setIcon(R.drawable.drawer_item_icon_write_to_us);
        write.setSelectedIcon(R.drawable.drawer_item_icon_write_to_us_selected);
        drawerItems.add(write);

        notifications = new DrawerItem();
        notifications.setTitle("NOTIFICATIONS");
        notifications.setIcon(R.drawable.drawer_item_icon_notifications);
        notifications.setSelectedIcon(R.drawable.drawer_item_icon_notifications_selected);
        drawerItems.add(notifications);

        faq = new DrawerItem();
        faq.setTitle("FAQs");
        faq.setIcon(R.drawable.drawer_item_icon_faqs);
        faq.setSelectedIcon(R.drawable.drawer_item_icon_faqs_selected);
        drawerItems.add(faq);

        rate = new DrawerItem();
        rate.setTitle("RATE TWYST");
        rate.setIcon(R.drawable.drawer_item_icon_rate);
        rate.setSelectedIcon(R.drawable.drawer_item_icon_rate_selected);
        drawerItems.add(rate);

        for (DrawerItem drawerItem : drawerItems) {
            if (pos == drawerItems.indexOf(drawerItem)) {
                drawerItem.setSelected(false);
            }
        }
        if (pos > -1) {
            DrawerItem drawerItem = drawerItems.get(pos);
            drawerItem.setSelected(true);
        }

        return drawerItems;
    }

    protected void createDrawerProfileBanner() {

    }

    private void showNotifications() {

        Intent intent = new Intent(this, NotificationActivity.class);
        startActivity(intent);
    }

    private void showWallet() {

        Intent intent = new Intent(this, WalletActivity.class);
        startActivity(intent);
    }

    private void showHome() {

        Intent intent = new Intent(this, DiscoverActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtra("Search", false);
        intent.setAction("setChildNo");
        startActivity(intent);
    }

    @Override
    public void setTitle(CharSequence title) {
        mTitle = title;
        getSupportActionBar().setTitle(mTitle);
    }


    @Override
    public boolean onQueryTextSubmit(final String s) {
        Log.d(BaseActivity.class.getSimpleName(), "onQueryTextSubmit q= " + s);

        searchView.setQuery("", true);
        searchView.setIconified(true);
        searchView.clearFocus();
        searchView.setSuggestionsAdapter(null);

       /* if (s.length() < 3) {
            return true;
        }*/
        SharedPreferences.Editor sharedPreferences = getSharedPreferences(AppConstants.PREFERENCE_SHARED_PREF_NAME, Context.MODE_PRIVATE).edit();
        sharedPreferences.putString(AppConstants.PREFERENCE_PARAM_SEARCH_QUERY, s);
        sharedPreferences.commit();
        Intent intent = new Intent(getBaseContext(), SearchActivity.class);
        intent.putExtra("Search", true);
        intent.setAction("setChildYes");
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);

        return true;
    }

    @Override
    public boolean onQueryTextChange(final String s) {
        Log.d(BaseActivity.class.getSimpleName(), "onQueryTextChange q= " + s);

        if (s.length() < 3) {
            searchView.setSuggestionsAdapter(null);
            return true;
        }

        return true;
    }

    @Override
    public boolean onSuggestionClick(int i) {
        Log.d(getTagName(), "onSuggestionClick item: " + i);

        return true;


    }

    @Override
    public boolean onSuggestionSelect(int i) {
        Log.d(getTagName(), "onSuggestionSelect item: " + i);
        return false;
    }

    private class DrawerItemClickListener implements ListView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            Log.d(BaseActivity.class.getSimpleName(), "Drawer item clicked at position " + position);


            /*for (DrawerItem drawerItem : drawerItems) {
                drawerItem.setSelected(false);
            }

            DrawerItem drawerItem = drawerItems.get(position - 1);
            drawerItem.setSelected(true);

            HeaderViewListAdapter headerViewListAdapter = (HeaderViewListAdapter) drawerList.getAdapter();
            DrawerListAdapter drawerListAdapter = (DrawerListAdapter) headerViewListAdapter.getWrappedAdapter();
            drawerListAdapter.notifyDataSetChanged();
*/
            selectItem(position);
            for (DrawerItem drawerItem : drawerItems) {
                drawerItem.setSelected(false);
            }
            if (position > 0) {
                DrawerItem drawerItem = drawerItems.get(position - 1);
                drawerItem.setSelected(true);

            }
            updateDrawer();
            SharedPreferences.Editor sharedPreferences = getSharedPreferences(AppConstants.PREFERENCE_SHARED_PREF_NAME, Context.MODE_PRIVATE).edit();
            sharedPreferences.putInt(AppConstants.PREFERENCE_LAST_DRAWERITEM_CLICKED, position - 1);
            sharedPreferences.commit();

        }
    }


    private void selectItem(final int position) {
        //updateTitle = false;
        drawerLayout.closeDrawer(findViewById(R.id.drawer_list));
        //drawerList.setItemChecked(position, true);
        drawerList.setItemChecked(position, false);

        drawerLayout.postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent = null;

                switch (position) {
                    case 0:
                        intent = new Intent(getBaseContext(), EditProfileActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                        intent.putExtra(AppConstants.INTENT_PARAM_FROM_DRAWER, true);
                        startActivity(intent);
                        break;
                    //header image click
//                        return;

                    case 1:
                        //invite friends
                        intent = new Intent(getBaseContext(), InviteFriendsActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                        break;

                    case 2:
                        //upload bill
                        intent = new Intent(getBaseContext(), UploadBillActivity.class);
                        break;

                    case 3:
                        //my wallet
                        intent = new Intent(getBaseContext(), WalletActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                        break;

                    case 4:
                        //submit an offer
                        intent = new Intent(getBaseContext(), SubmitOfferActivity.class);
                        break;

                    case 5:
                        //suggest an outlet
                        intent = new Intent(getBaseContext(), SuggestOutletActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                        break;

                    case 6:
                        //write to us
                        intent = new Intent(getBaseContext(), WriteToUsActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                        break;

                    case 7:
                        //notification
                        intent = new Intent(getBaseContext(), NotificationActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                        break;

                    case 8:
                        //faq
                        intent = new Intent(getBaseContext(), FaqActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                        break;

                    case 9:
                        //Rate
                        rateApp();
                        return;

                }
                if (intent != null) {
                    intent.putExtra(AppConstants.INTENT_PARAM_FROM_DRAWER, true);
                }
                startActivity(intent);
            }
        }, 250);
    }


    public void updateDrawer() {
        drawerList.setAdapter(new DrawerListAdapter(this, getLayoutInflater(), drawerItems));
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        // Sync the toggle state after onRestoreInstanceState has occurred.
        mDrawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        // Pass any configuration change to the drawerLayout toggl
        mDrawerToggle.onConfigurationChanged(newConfig);
    }


    @Override
    public boolean onCreateOptionsMenu(final Menu menu) {

        this.menu = menu;

        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu, menu);
        final MenuItem notificationsMenuItem = menu.findItem(R.id.action_notifications);
        final MenuItem walletMenuItem = menu.findItem(R.id.action_wallet);
        final MenuItem searchMenuItem = menu.findItem(R.id.action_search);
        final MenuItem homeMenuItem = menu.findItem(R.id.action_home);


        if (getLayoutResource() == R.layout.activity_edit_profile) {
            homeMenuItem.setVisible(false);
            walletMenuItem.setVisible(false);
            notificationsMenuItem.setVisible(false);
            searchMenuItem.setVisible(false);
        } else if (getLayoutResource() == R.layout.redeem_voucher_activity) {
            homeMenuItem.setVisible(false);
            walletMenuItem.setVisible(false);
            notificationsMenuItem.setVisible(false);
            searchMenuItem.setVisible(false);
        } else if (getLayoutResource() == R.layout.activity_wallet) {
            homeMenuItem.setVisible(true);
            walletMenuItem.setVisible(false);
        } else if (getLayoutResource() == R.layout.activity_notification) {
            homeMenuItem.setVisible(true);
            notificationsMenuItem.setVisible(false);

            //Hide all action buttons
        } else if ((getLayoutResource() == R.layout.activity_upload_bill)
                || (getLayoutResource() == R.layout.activity_write_to_us)
                || (getLayoutResource() == R.layout.activity_suggest_outlet)
                || (getLayoutResource() == R.layout.checkin_succes_layout)
                || (getLayoutResource() == R.layout.activity_submit_offer)) {
            //Hide all action buttons
            homeMenuItem.setVisible(false);
            notificationsMenuItem.setVisible(false);
            walletMenuItem.setVisible(false);
            searchMenuItem.setVisible(false);
        }


        searchView = (SearchView) MenuItemCompat.getActionView(menu.findItem(R.id.action_search));
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        // Assumes current activity is the searchable activity
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        //searchView.setIconifiedByDefault(false); // Do not iconify the widget; expand it by default

        searchView.setOnQueryTextListener(this);
        searchView.setOnSuggestionListener(this);
        searchView.setOnQueryTextFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                searchView.setSuggestionsAdapter(null);
                if (b) {
                    if (getLayoutResource() == R.layout.activity_wallet) {
                        homeMenuItem.setVisible(true);
                        walletMenuItem.setVisible(false);
                    } else if (getLayoutResource() == R.layout.activity_notification) {
                        homeMenuItem.setVisible(true);
                        notificationsMenuItem.setVisible(false);
                    } else {
                        notificationsMenuItem.setVisible(true);
                        walletMenuItem.setVisible(true);
                    }

                } else {
                    if (getLayoutResource() == R.layout.activity_wallet) {
                        homeMenuItem.setVisible(true);
                        walletMenuItem.setVisible(false);
                    } else if (getLayoutResource() == R.layout.activity_notification) {
                        homeMenuItem.setVisible(true);
                        notificationsMenuItem.setVisible(false);
                    } else {
                        notificationsMenuItem.setVisible(true);
                        walletMenuItem.setVisible(true);
                    }
                }
            }
        });
        searchView.setQueryHint("Search");

        SearchView.SearchAutoComplete autoCompleteTextView = (SearchView.SearchAutoComplete) searchView.findViewById(R.id.search_src_text);
        if (autoCompleteTextView != null) {
            autoCompleteTextView.setDropDownBackgroundDrawable(getResources().getDrawable(R.drawable.abc_popup_background_mtrl_mult));
        }
        View searchPlate = searchView.findViewById(android.support.v7.appcompat.R.id.search_plate);

        if (searchPlate != null) {
            searchPlate.setBackgroundResource(R.drawable.textfield_searchview_holo_light);
        }


        return true;

    }


    /* Called whenever we call invalidateOptionsMenu() */
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        // If the nav drawerLayout is open, hide action items related to the content view
        //boolean drawerOpen = mDrawerLayout.isDrawerOpen(mDrawerList);
        //menu.findItem(R.id.action_websearch).setVisible(!drawerOpen);
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (mDrawerToggle.isDrawerIndicatorEnabled() &&
                mDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }

        // The action bar home/up action should open or close the drawerLayout.
        // ActionBarDrawerToggle will take care of this.
        if (mDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }

        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        switch (item.getItemId()) {
            // Handle home button in non-drawerLayout mode
            case android.R.id.home:
                onBackPressed();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }

    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
        updateDrawer();
    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_MENU) {
            return true;
        }
        return super.onKeyUp(keyCode, event);
    }

    public void handleRetrofitError(RetrofitError error) {
        if (error.getKind() == RetrofitError.Kind.NETWORK) {
            buildAndShowSnackbarWithMessage("No internet connection.");
        } else {
            buildAndShowSnackbarWithMessage("An unexpected error has occurred.");
        }
        Log.e(getTagName(), "failure", error);
    }

    public void buildAndShowSnackbarWithMessage(String msg) {
        final Snackbar snackbar = Snackbar.with(getApplicationContext())
                .type(SnackbarType.MULTI_LINE)
                        //.color(getResources().getColor(android.R.color.black))
                .text(msg)
                .actionLabel("RETRY") // action button label
                .duration(Snackbar.SnackbarDuration.LENGTH_INDEFINITE)
                .swipeToDismiss(false)
                .actionListener(new ActionClickListener() {
                    @Override
                    public void onActionClicked(Snackbar snackbar) {
                        Intent intent = getIntent();
                        overridePendingTransition(0, 0);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                        finish();
                        overridePendingTransition(0, 0);
                        startActivity(intent);
                    }
                });
        snackbar.postDelayed(new Runnable() {
            @Override
            public void run() {
                showSnackbar(snackbar); // activity where it is displayed
            }
        }, 500);

    }

    protected void showSnackbar(Snackbar snackbar) {
        SnackbarManager.show(snackbar, this);
    }

    public void hideSnackbar() {
        SnackbarManager.dismiss();
    }


    /**
     * Check the device to make sure it has the Google Play Services APK. If
     * it doesn't, display a dialog that allows users to download the APK from
     * the Google Play Store or enable it in the device's system settings.
     */
    protected boolean checkPlayServices() {
        if (AppConstants.IS_DEVELOPMENT) {
            return true;
        }

        int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (GooglePlayServicesUtil.isUserRecoverableError(resultCode)) {
                GooglePlayServicesUtil.getErrorDialog(resultCode, this, PLAY_SERVICES_RESOLUTION_REQUEST).show();
            } else {
                Log.i(getTagName(), "This device is not supported.");
                finish();
            }
            return false;
        }
        return true;
    }

    @Override
    protected void onResume() {
        super.onResume();
        checkPlayServices();
        updatePicName();
    }

    public int getScreenHeight() {
        return findViewById(android.R.id.content).getHeight();
    }

    public int getScreenWidth() {
        return findViewById(android.R.id.content).getWidth();
    }

    private void rateApp() {
        Uri uri = Uri.parse("market://details?id=" + getApplication().getPackageName());
        Intent goToMarket = new Intent(Intent.ACTION_VIEW, uri);
        try {
            startActivity(goToMarket);
        } catch (ActivityNotFoundException e) {
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id="+getApplication().getPackageName()+"&ah=-smMDxRK7pmXEK32N7mSNcbZ2ZM")));
        }

    }


    @Override
    public void onBackPressed() {
        if (drawerOpened) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    protected void showShareIntents(String title, String text) {
        List<Intent> targetedShareIntents = new ArrayList<Intent>();
        Intent facebookIntent = getShareIntent("facebook", "", text);
        if (facebookIntent != null) {
            targetedShareIntents.add(facebookIntent);
        }

        Intent whatsappIntent = getShareIntent("whatsapp", "", text);
        if (whatsappIntent != null) {
            targetedShareIntents.add(whatsappIntent);
        }

        Intent twIntent = getShareIntent("twitter", "", text);
        if (twIntent != null) {
            targetedShareIntents.add(twIntent);
        }

        Intent gmailIntent = getShareIntent("gmail", "", text);
        if (gmailIntent != null) {
            targetedShareIntents.add(gmailIntent);
        }

        Intent mmsIntent = getShareIntent("mms", "", text);
        if (mmsIntent != null) {
            targetedShareIntents.add(mmsIntent);
        }

        Intent chooser = Intent.createChooser(targetedShareIntents.remove(0), title);
        chooser.putExtra(Intent.EXTRA_INITIAL_INTENTS, targetedShareIntents.toArray(new Parcelable[]{}));
        startActivity(chooser);
    }

    private Intent getShareIntent(String type, String subject, String text) {
        boolean found = false;
        Intent share = new Intent(android.content.Intent.ACTION_SEND);
        share.setType("text/plain");

        // gets the list of intents that can be loaded.
        List<ResolveInfo> resInfo = this.getPackageManager().queryIntentActivities(share, 0);
        System.out.println("resinfo: " + resInfo);
        if (!resInfo.isEmpty()) {
            for (ResolveInfo info : resInfo) {
                if (info.activityInfo.packageName.toLowerCase().contains(type) ||
                        info.activityInfo.name.toLowerCase().contains(type)) {
                    share.putExtra(Intent.EXTRA_SUBJECT, subject);
                    share.putExtra(Intent.EXTRA_TEXT, text);
                    share.setPackage(info.activityInfo.packageName);
                    found = true;
                    break;
                }
            }
            if (!found)
                return null;

            return share;
        }
        return null;
    }

    public String getUserToken() {
        SharedPreferences prefs = this.getSharedPreferences(AppConstants.PREFERENCE_SHARED_PREF_NAME, Context.MODE_PRIVATE);
        return prefs.getString(AppConstants.PREFERENCE_USER_TOKEN, "");

    }

    public void showEarnMoreInstructions() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater li = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View dialogView = li.inflate(R.layout.dialog_earn_more, null);
//        TextView tvEarnMore = (TextView) dialogView.findViewById(R.id.tvEarnMore);
//        tvEarnMore.setText(Html.fromHtml(getString(R.string.earn_more_body)));

        WebView webView = (WebView) dialogView.findViewById(R.id.webView);
        webView.getSettings().setJavaScriptEnabled(true); // enable javascript
        final TwystProgressHUD twystProgressHUD = TwystProgressHUD.show(this, false, null);

        webView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon)
            {
                // TODO show you progress image
                super.onPageStarted(view, url, favicon);
            }

            @Override
            public void onPageFinished(WebView view, String url)
            {
                twystProgressHUD.dismiss();
                // TODO hide your progress image
                super.onPageFinished(view, url);
            }

            @Override
            public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
                twystProgressHUD.dismiss();
//                Toast.makeText(BaseActivity.this, description, Toast.LENGTH_SHORT).show();
            }

        });

        webView .loadUrl(AppConstants.HOST + "/api/v4/earn/more");

        builder.setView(dialogView);

        final AlertDialog dialog = builder.create();
        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(true);
        dialog.show();

        dialogView.findViewById(R.id.okButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
    }

    public void viewMoreDialog(String termsToShow) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater li = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View dialogView = li.inflate(R.layout.dialog_viewmore_tnc_offer_details, null);
        TextView tvTerms = (TextView) dialogView.findViewById(R.id.tvTerms);
        tvTerms.setText(termsToShow);

        builder.setView(dialogView);

        final AlertDialog dialog = builder.create();
        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(true);
        dialog.show();

        dialogView.findViewById(R.id.closeButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
    }

    public String getFormattedTermsConditions(String termsFromOffer) {
        String terms;

        Character newLine = '\n';
        Character bullet = '\u2022';
        String space = " ";
        if (!TextUtils.isEmpty(termsFromOffer)) {
            if (termsFromOffer.contains(newLine.toString())){
                termsFromOffer = termsFromOffer.replace(newLine.toString(),newLine.toString()+ space + bullet.toString() + space);
            }
            terms = space + bullet + space + termsFromOffer + newLine + space + getString(R.string.terms_conditions);
        } else {
            terms = space + getString(R.string.terms_conditions);
        }
        return terms;
    }

    public int getTwystBucks() {
        SharedPreferences prefs = this.getSharedPreferences(AppConstants.PREFERENCE_SHARED_PREF_NAME, Context.MODE_PRIVATE);
        return prefs.getInt(AppConstants.PREFERENCE_LAST_TWYST_BUCK, 0);
    }

    public void setTwystBucks(int twystBucks) {
        sharedPreferences.putInt(AppConstants.PREFERENCE_LAST_TWYST_BUCK, twystBucks);
        sharedPreferences.commit();
    }

    public void updatePicName(){
        SharedPreferences prefs = getSharedPreferences(AppConstants.PREFERENCE_SHARED_PREF_NAME, Context.MODE_PRIVATE);
        String name = prefs.getString(AppConstants.PREFERENCE_USER_NAME, "");
        String pic = prefs.getString(AppConstants.PREFERENCE_USER_PIC, "");
        userName.setText(name);

        if (!TextUtils.isEmpty(pic)) {
            backImage.setVisibility(View.VISIBLE);
            userImage.setVisibility(View.VISIBLE);
            Picasso picasso = Picasso.with(this);
            picasso.setIndicatorsEnabled(AppConstants.DEGUG_PICASSO);
            picasso.setLoggingEnabled(AppConstants.DEGUG_PICASSO);
            picasso.load(pic)
                    .noFade()
                    .centerCrop()
                    .resize(200, 200)
                    .transform(new RoundedTransformation(100, 0))
                    .into(userImage);

        } else {
            backImage.setVisibility(View.GONE);
            userImage.setVisibility(View.VISIBLE);
        }


    }

    public ArrayList<OutletList> getOutletListsArray() {
        final SharedPreferences preferences = getSharedPreferences(AppConstants.PREFERENCE_SHARED_PREF_NAME, Context.MODE_PRIVATE);
        String outletListString = preferences.getString(AppConstants.PREFERENCE_OUTLETS_LIST, "");

        ArrayList<OutletList> list = new ArrayList<OutletList>();
        try{
            JSONObject jsonObject = new JSONObject(outletListString);
            Iterator<String> iter = jsonObject.keys();
            while (iter.hasNext()) {
                String key = iter.next();
                try {
                    JSONObject jsonObject1 = jsonObject.getJSONObject(key);
                    String name = jsonObject1.getString("name");
                    String address = jsonObject1.getString("address");

                    OutletList outletList = new OutletList();
                    outletList.set_id(key);
                    outletList.setName(name);
                    outletList.setAddress(address);

                    list.add(outletList);
                } catch (JSONException e) {
                    // Something went wrong!
                }
            }
        }catch (JSONException e){
        }

        return list;
    }

    public ArrayList getOutletListNames(ArrayList<OutletList> outletListArrayList) {
        ArrayList outletListNames = new ArrayList();
        for (OutletList outletList : outletListArrayList) {
            String name = outletList.getName();
            // repeated additions:
            if (!outletListNames.contains(name)){
                outletListNames.add(name);
            }
        }
        return outletListNames;
    }

    public ArrayList<OutletList> getFilteredOutletListsAddressArray(ArrayList<OutletList> outletListArrayList, String selectedName) {
        ArrayList<OutletList> list = new ArrayList<OutletList>();
        for (OutletList outletList : outletListArrayList) {
            String name = outletList.getName();
            if (name.equals(selectedName)){
                list.add(outletList);
            }
        }
        return list;
    }

    public ArrayList getOutletListAddress(ArrayList<OutletList> outletListArrayList, String selectedName) {
        ArrayList outletListLocations = new ArrayList();
        for (OutletList outletList : outletListArrayList) {
            outletListLocations.add(outletList.getAddress());
        }
        return outletListLocations;
    }

    public String getOutletID(ArrayList<OutletList> outletListArrayList, String selectedAddress) {
        String outletID="";
        for (OutletList outletList : outletListArrayList) {
            String address = outletList.getAddress();
            if (address.equals(selectedAddress)){
                outletID = outletList.get_id();
                return outletID;
            }

        }
        return outletID;
    }

    public SharedPreferences getPrefs(){
       return getSharedPreferences(AppConstants.PREFERENCE_SHARED_PREF_NAME, Context.MODE_PRIVATE);
    }

}
