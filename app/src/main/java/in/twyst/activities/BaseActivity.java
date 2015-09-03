package in.twyst.activities;

import android.app.SearchManager;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ResolveInfo;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.nispok.snackbar.Snackbar;
import com.nispok.snackbar.SnackbarManager;
import com.nispok.snackbar.enums.SnackbarType;
import com.nispok.snackbar.listeners.ActionClickListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import fr.castorflex.android.circularprogressbar.CircularProgressBar;
import in.twyst.R;
import in.twyst.adapters.DrawerListAdapter;
import in.twyst.model.DrawerItem;
import in.twyst.util.AppConstants;
import in.twyst.util.RoundedTransformation;
import retrofit.RetrofitError;

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

    protected abstract String getTagName();

    protected abstract int getLayoutResource();

    public void hideProgressHUDInLayout() {
        if (circularProgressBar != null) {
            circularProgressBar.progressiveStop();
            circularProgressBar.setVisibility(View.GONE);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getLayoutResource());

        circularProgressBar = (CircularProgressBar) findViewById(R.id.circularProgressBar);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

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
                drawerOpened = false;
            }

            public void onDrawerOpened(View drawerView) {
                invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
                supportInvalidateOptionsMenu();
                getSupportActionBar().setTitle(mTitle);
                drawerOpened = true;
            }
        };

        drawerLayout.setDrawerListener(mDrawerToggle);
        drawerLayout.setStatusBarBackgroundColor(getResources().getColor(R.color.colorPrimaryDark));

        drawerList = (ListView) findViewById(R.id.drawer_listview);
        View list_header = getLayoutInflater().inflate(R.layout.drawerlist_header, null);

        SharedPreferences prefs = getSharedPreferences(AppConstants.PREFERENCE_SHARED_PREF_NAME, Context.MODE_PRIVATE);
        String name = prefs.getString(AppConstants.PREFERENCE_USER_NAME, "");
        String pic = prefs.getString(AppConstants.PREFERENCE_USER_PIC, "");

        TextView userName = (TextView)list_header.findViewById(R.id.userName);
        ImageView backImage = (ImageView)list_header.findViewById(R.id.backImage);
        ImageView userImage = (ImageView)list_header.findViewById(R.id.userImage);
        userName.setText(name);

        if(!TextUtils.isEmpty(pic)){
            backImage.setVisibility(View.VISIBLE);
            userImage.setVisibility(View.VISIBLE);
            Picasso picasso = Picasso.with(this);
            picasso.setIndicatorsEnabled(AppConstants.DEGUG_PICASSO);
            picasso.setLoggingEnabled(AppConstants.DEGUG_PICASSO);
            picasso.load(pic)
                    .noFade()
                    .centerCrop()
                    .resize(200,200)
                    .transform(new RoundedTransformation(100, 0))
                    .into(userImage);

        }else {
            backImage.setVisibility(View.GONE);
            userImage.setVisibility(View.VISIBLE);
        }



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
                    if(getLayoutResource() == R.layout.activity_outlet_details || getLayoutResource() == R.layout.redeem_voucher_activity){
                        Intent intent = new Intent(getBaseContext(),DiscoverActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);
                    }else {
                        finish();
                    }
                }
            });
        }

        setTitle(mTitle);
    }

    private ArrayList<DrawerItem> getDrawerItems(int pos) {
        ArrayList<DrawerItem> drawerItems = new ArrayList<>();

        DrawerItem invite = new DrawerItem();
        invite.setTitle("INVITE FRIENDS");
        invite.setIcon(R.drawable.drawer_item_icon_invite_friends);
        invite.setSelectedIcon(R.drawable.drawer_item_icon_invite_friends_selected);
        drawerItems.add(invite);

        DrawerItem faq = new DrawerItem();
        faq.setTitle("FAQs");
        faq.setIcon(R.drawable.drawer_item_icon_faqs);
        faq.setSelectedIcon(R.drawable.drawer_item_icon_faqs_selected);
        drawerItems.add(faq);

        DrawerItem wallet = new DrawerItem();
        wallet.setTitle("MY WALLET");
        wallet.setIcon(R.drawable.drawer_item_icon_wallet);
        wallet.setSelectedIcon(R.drawable.drawer_item_icon_wallet_selected);
        drawerItems.add(wallet);

        DrawerItem bill = new DrawerItem();
        bill.setTitle("UPLOAD BILL");
        bill.setIcon(R.drawable.drawer_item_icon_upload_bill);
        bill.setSelectedIcon(R.drawable.drawer_item_icon_upload_bill_selected);
        drawerItems.add(bill);

        DrawerItem notifications = new DrawerItem();
        notifications.setTitle("NOTIFICATIONS");
        notifications.setIcon(R.drawable.drawer_item_icon_notifications);
        notifications.setSelectedIcon(R.drawable.drawer_item_icon_notifications_selected);
        drawerItems.add(notifications);

        DrawerItem submitOffer = new DrawerItem();
        submitOffer.setTitle("SUBMIT AN OFFER");
        submitOffer.setIcon(R.drawable.drawer_item_icon_submit_offer);
        submitOffer.setSelectedIcon(R.drawable.drawer_item_icon_submit_offer_selected);
        drawerItems.add(submitOffer);

        DrawerItem suggestOutlet = new DrawerItem();
        suggestOutlet.setTitle("SUGGEST AN OUTLET");
        suggestOutlet.setIcon(R.drawable.drawer_item_icon_suggest_outlet);
        suggestOutlet.setSelectedIcon(R.drawable.drawer_item_icon_suggest_outlet_selected);
        drawerItems.add(suggestOutlet);

        DrawerItem write = new DrawerItem();
        write.setTitle("WRITE TO US");
        write.setIcon(R.drawable.drawer_item_icon_write_to_us);
        write.setSelectedIcon(R.drawable.drawer_item_icon_write_to_us_selected);
        drawerItems.add(write);

        DrawerItem rate = new DrawerItem();
        rate.setTitle("RATE TWYST");
        rate.setIcon(R.drawable.drawer_item_icon_rate);
        rate.setSelectedIcon(R.drawable.drawer_item_icon_rate_selected);
        drawerItems.add(rate);

        for(DrawerItem drawerItem : drawerItems){
            if(pos == drawerItems.indexOf(drawerItem)){
                drawerItem.setSelected(false);
            }
        }
        if(pos>-1) {
            DrawerItem drawerItem = drawerItems.get(pos);
            drawerItem.setSelected(true);
        }

        return drawerItems;
    }

    protected void createDrawerProfileBanner() {

    }

    private void showNotifications() {

        Intent intent = new Intent(this , NotificationActivity.class);
        startActivity(intent);
    }

    private void showWallet() {

        Intent intent = new Intent(this , WalletActivity.class);
        startActivity(intent);
    }

    private void showHome() {

        Intent intent = new Intent(this , DiscoverActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }

    @Override
    public void setTitle(CharSequence title) {
        mTitle = title;
        getSupportActionBar().setTitle(mTitle);
    }


    @Override
    public boolean onQueryTextSubmit(String s) {
        Log.d(BaseActivity.class.getSimpleName(), "onQueryTextSubmit q= " + s);

        searchView.setQuery("", true);
        searchView.setIconified(true);
        searchView.clearFocus();
        searchView.setSuggestionsAdapter(null);

        if (s.length() < 3) {
            return true;
        }

//        Intent intent = new Intent(getBaseContext(), ProductListActivity.class);
//        intent.putExtra(AppConstants.INTENT_PARAM_SEARCH_QUERY, s);
//        startActivity(intent);

        return true;
    }

    @Override
    public boolean onQueryTextChange(final String s) {
        Log.d(BaseActivity.class.getSimpleName(), "onQueryTextChange q= " + s);

        if (s.length() < 3) {
            searchView.setSuggestionsAdapter(null);
            return true;
        }


//        HttpService.getInstance().searchProductsByText(s, new Callback<List<ProductJson>>() {
//            @Override
//            public void success(List<ProductJson> result, Response response) {
//                Log.d(getTagName(), "result " + result.size());
//
//                String[] columns = new String[]{"_id", "text"};
//                Object[] temp = new Object[]{0, "default"};
//
//                MatrixCursor cursor = new MatrixCursor(columns);
//                for (int i = 0; i < result.size(); i++) {
//                    temp[0] = i;
//                    temp[1] = result.get(i).getPname();
//
//                    cursor.addRow(temp);
//
//                }
//
//                CursorAdapter suggestionsAdapter = searchView.getSuggestionsAdapter();
//                if (suggestionsAdapter != null) {
//                    suggestionsAdapter.notifyDataSetChanged();
//                }
//
//                SearchCursorAdapter searchCursorAdapter = new SearchCursorAdapter(getBaseContext(), cursor, result);
//                searchView.setSuggestionsAdapter(searchCursorAdapter);
//
//            }
//
//            @Override
//            public void failure(RetrofitError error) {
//                Log.e(getTagName(), "failure", error);
//            }
//        });


        return true;
    }

    @Override
    public boolean onSuggestionClick(int i) {
        Log.d(getTagName(), "onSuggestionClick item: " + i);
//        SearchCursorAdapter suggestionsAdapter = (SearchCursorAdapter) searchView.getSuggestionsAdapter();
//        ProductJson product = suggestionsAdapter.getProducts().get(i);
//
//        searchView.setQuery("", true);
//        searchView.setIconified(true);
//        searchView.clearFocus();
//        searchView.setSuggestionsAdapter(null);
//
//        Intent intent = new Intent(getBaseContext(), ProductDetailsActivity.class);
//        intent.putExtra(AppConstants.INTENT_PARAM_PRODUCTID, product.getProductId());
//        startActivity(intent);

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
            if(position>0){
                DrawerItem drawerItem = drawerItems.get(position-1);
                drawerItem.setSelected(true);

            }
            updateDrawer();
            SharedPreferences.Editor sharedPreferences = getSharedPreferences(AppConstants.PREFERENCE_SHARED_PREF_NAME, Context.MODE_PRIVATE).edit();
            sharedPreferences.putInt(AppConstants.PREFERENCE_LAST_DRAWERITEM_CLICKED,position-1);
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
                        //header image click
                        editProfile();
                        return;

                    case 1:
                        //invite friends
                        inviteFriends();
                        return;

                    case 2:
                        //faq
                        intent = new Intent(getBaseContext(), FaqActivity.class);
                        break;

                    case 3:
                        //my wallet
                        intent = new Intent(getBaseContext(), WalletActivity.class);
                        break;

                    case 4:
                        //upload bill
                        intent = new Intent(getBaseContext(), UploadBillActivity.class);
                        break;

                    case 5:
                        //notification
                        intent = new Intent(getBaseContext(), NotificationActivity.class);
                        break;

                    case 6:
                        //submit an offer
                        intent = new Intent(getBaseContext(), SubmitOfferActivity.class);
                        break;

                    case 7:
                        //suggest an outlet
                        intent = new Intent(getBaseContext(), SuggestOutletActivity.class);
                        break;

                    case 8:
                        //write to us
                        intent = new Intent(getBaseContext(), WriteToUsActivity.class);
                        break;

                    case 9:
                        //Rate
                        rateApp();
                        return;

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


        if(getLayoutResource() == R.layout.activity_wallet){
            homeMenuItem.setVisible(true);
            walletMenuItem.setVisible(false);
        }else if(getLayoutResource() == R.layout.activity_notification){
            homeMenuItem.setVisible(true);
            notificationsMenuItem.setVisible(false);
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
                    if(getLayoutResource() == R.layout.activity_wallet){
                        homeMenuItem.setVisible(true);
                        walletMenuItem.setVisible(false);
                    }else if(getLayoutResource() == R.layout.activity_notification){
                        homeMenuItem.setVisible(true);
                        notificationsMenuItem.setVisible(false);
                    }else {
                        notificationsMenuItem.setVisible(true);
                        walletMenuItem.setVisible(true);
                    }

                } else {
                    if(getLayoutResource() == R.layout.activity_wallet){
                        homeMenuItem.setVisible(true);
                        walletMenuItem.setVisible(false);
                    }else if(getLayoutResource() == R.layout.activity_notification){
                        homeMenuItem.setVisible(true);
                        notificationsMenuItem.setVisible(false);
                    }else {
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
    }

    public int getScreenHeight() {
        return findViewById(android.R.id.content).getHeight();
    }

    public int getScreenWidth() {
        return findViewById(android.R.id.content).getWidth();
    }

    private void rateApp() {
        Uri uri = Uri.parse("market://details?id=com.twyst.app.android");
        Intent goToMarket = new Intent(Intent.ACTION_VIEW, uri);
        try {
            startActivity(goToMarket);
        } catch (ActivityNotFoundException e) {
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=com.twyst.app.android")));
        }

    }


    private void editProfile() {

    }

    private void inviteFriends() {
        Intent intent = new Intent(getBaseContext(), InviteFriendsActivity.class);
        startActivity(intent);
    }

    @Override
    public void onBackPressed() {
        if (drawerOpened) {
            drawerLayout.closeDrawer(Gravity.LEFT);
        }else {
            super.onBackPressed();
        }
    }

    protected void showShareIntents(String title, String text) {
        List<Intent> targetedShareIntents = new ArrayList<Intent>();
        Intent facebookIntent = getShareIntent("facebook", "", text);
        if(facebookIntent != null) {
            targetedShareIntents.add(facebookIntent);
        }

        Intent whatsappIntent = getShareIntent("whatsapp", "", text);
        if(whatsappIntent != null) {
            targetedShareIntents.add(whatsappIntent);
        }

        Intent twIntent = getShareIntent("twitter", "", text);
        if(twIntent != null) {
            targetedShareIntents.add(twIntent);
        }

        Intent gmailIntent = getShareIntent("gmail", "", text);
        if(gmailIntent != null) {
            targetedShareIntents.add(gmailIntent);
        }

        Intent mmsIntent = getShareIntent("mms", "", text);
        if(mmsIntent != null) {
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
        if (!resInfo.isEmpty()){
            for (ResolveInfo info : resInfo) {
                if (info.activityInfo.packageName.toLowerCase().contains(type) ||
                        info.activityInfo.name.toLowerCase().contains(type) ) {
                    share.putExtra(Intent.EXTRA_SUBJECT,  subject);
                    share.putExtra(Intent.EXTRA_TEXT,     text);
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
}
