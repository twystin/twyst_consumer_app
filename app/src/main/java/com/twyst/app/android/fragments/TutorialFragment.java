package com.twyst.app.android.fragments;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.twyst.app.android.R;
import com.twyst.app.android.activities.DiscoverActivity;
import com.twyst.app.android.activities.LoginActivity;
import com.twyst.app.android.activities.PhoneVerificationActivity;
import com.twyst.app.android.activities.TutorialActivity;
import com.twyst.app.android.util.AppConstants;

/**
 * Created by satish on 31/05/15.
 */
public class TutorialFragment extends Fragment {
    private int position;

    public static Fragment newInstance(int position) {
        TutorialFragment tutorialFragment = new TutorialFragment();
        Bundle args = new Bundle();
        args.putInt("position", position);
        tutorialFragment.setArguments(args);

        return tutorialFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle arguments = getArguments();
        position = arguments.getInt("position");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, final Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_tutorial, container, false);

        ImageView centerLogo = (ImageView) view.findViewById(R.id.centerLogo);
        TextView textView1 = (TextView) view.findViewById(R.id.textView1);
        TextView textView2 = (TextView) view.findViewById(R.id.textView2);
        Button letsTwyst = (Button) view.findViewById(R.id.letsTwystBtn);

        switch (position) {
            case 0:
                centerLogo.setImageResource(R.drawable.tutorial_center_logo_1);
                textView1.setText("DISCOVER THE BEST FOOD & DRINK OFFERS AROUND YOU!");
                textView2.setText("We’re on a mission to bring you the best and the widest range of Offers & Deals on Food and Drink around you!");
                letsTwyst.setVisibility(View.GONE);
                break;
            case 1:
                centerLogo.setImageResource(R.drawable.tutorial_center_logo_2);
                textView1.setText("AND THE OFFERS GET BETTER!");
                textView2.setText("Check-in at your favourite Twyst Partner outlets and unlock exclusive offers. Its always great to get a little extra at the places you love!");
                letsTwyst.setVisibility(View.GONE);
                break;
            case 2:
                centerLogo.setImageResource(R.drawable.tutorial_center_logo_3);
                textView1.setText("AND EVEN BETTER WITH FRIENDS!");
                textView2.setText("Never let an exclusive offer go waste! Set up your network on Twyst to share offers with friends. \nTIP: That means you can use the exclusive offers your friends unlocked too!");
                letsTwyst.setVisibility(View.GONE);
                break;
            case 3:
                centerLogo.setImageResource(R.drawable.tutorial_center_logo_4);
                textView1.setText("TWYST BEFORE YOU EAT!");
                textView2.setText("Now make sure you check out the offers running around you every time you eat out or order in. Be Smart - Get More & Pay Less!");
                letsTwyst.setVisibility(View.VISIBLE);

                final TutorialActivity tutorialActivity = (TutorialActivity) view.getContext();

                letsTwyst.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        SharedPreferences prefs = tutorialActivity.getSharedPreferences(AppConstants.PREFERENCE_SHARED_PREF_NAME, Context.MODE_PRIVATE);
                        boolean phoneVerified = prefs.getBoolean(AppConstants.PREFERENCE_PHONE_VERIFIED, false);
                        boolean emailVerified = prefs.getBoolean(AppConstants.PREFERENCE_EMAIL_VERIFIED, false);

                        if (!phoneVerified) {
                            startActivity(new Intent(tutorialActivity, PhoneVerificationActivity.class));
                            tutorialActivity.finish();
                        } else if (!emailVerified) {
                            startActivity(new Intent(tutorialActivity, LoginActivity.class));
                            tutorialActivity.finish();
                        } else {
                            Intent intent = new Intent(tutorialActivity, DiscoverActivity.class);
                            intent.setAction("setChildNo");
                            intent.putExtra("Search", false);
                            startActivity(intent);
                            tutorialActivity.finish();
                        }
                    }
                });
                break;
        }


        return view;
    }

}
