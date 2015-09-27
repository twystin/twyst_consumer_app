package com.twyst.app.android.adapters;

import android.database.DataSetObserver;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v7.app.ActionBarActivity;

import com.twyst.app.android.R;
import com.twyst.app.android.fragments.InviteEmailFragment;
import com.twyst.app.android.fragments.InviteFacebookFragment;
import com.twyst.app.android.fragments.InvitePhonebookFragment;
import com.twyst.app.android.fragments.InviteTwystFragment;
import com.twyst.app.android.util.SlidingTabLayout;


/**
 * Created by rahuls on 20/10/14.
 */
public class InviteFriendsTabPagerAdapter extends FragmentPagerAdapter implements SlidingTabLayout.TabIconProvider {

    private static final int iconRes[] = {
            R.drawable.tab_twyst,
            R.drawable.tab_phonebook,
            R.drawable.tab_facebook,
            R.drawable.tab_mail
    };

    private static final int iconResSelected[] = {
            R.drawable.tab_twyst_active,
            R.drawable.tab_phonebook_active,
            R.drawable.tab_facebook_active,
            R.drawable.tab_mail_active
    };

    private static final String iconTxt[] = {
            "Camera",
            "Viedeo"
    };

    public InviteFriendsTabPagerAdapter(ActionBarActivity activity) {
        super(activity.getSupportFragmentManager());

    }

    @Override
    public void registerDataSetObserver(DataSetObserver observer) {
        super.registerDataSetObserver(observer);
    }

    @Override
    public Fragment getItem(int i) {
        switch (i) {
            case 0:
                return new InviteTwystFragment();

            case 1:
                return new InvitePhonebookFragment();

            case 2:
                return new InviteFacebookFragment();

            case 3:
                return new InviteEmailFragment();

        }
        return null;
    }

    @Override
    public int getCount() {
        return 4; //No of Tabs
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return iconTxt[position];
    }

    @Override
    public int getPageIconResId(int position) {
        return iconRes[position];
    }

    @Override
    public int getSelectedPageIconResId(int position) {
        return iconResSelected[position];
    }
}