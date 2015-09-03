package in.twyst.fragments;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import in.twyst.R;
import in.twyst.activities.DiscoverActivity;
import in.twyst.activities.LoginActivity;
import in.twyst.activities.PhoneVerificationActivity;
import in.twyst.activities.TutorialActivity;
import in.twyst.util.AppConstants;

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
                textView1.setText("TWYST = DINING OFFERS UNLTD.");
                textView2.setText("Find the widest collection of dining offers across Delhi NCR.\nAlways hot, fresh and awesome!");
                letsTwyst.setVisibility(View.GONE);
                break;
            case 1:
                centerLogo.setImageResource(R.drawable.tutorial_center_logo_2);
                textView1.setText("AND THE OFFERS GET BETTER!");
                textView2.setText("Use coupons or check-in at your favorite places to unlock better, exclusive offers!");
                letsTwyst.setVisibility(View.GONE);
                break;
            case 2:
                centerLogo.setImageResource(R.drawable.tutorial_center_logo_3);
                textView1.setText("AND EVEN BETTER WITH FRIENDS!");
                textView2.setText("Set up your network on Twyst to share & use your friends' coupons!");
                letsTwyst.setVisibility(View.GONE);
                break;
            case 3:
                centerLogo.setImageResource(R.drawable.tutorial_center_logo_4);
                textView1.setText("SMART DINING IS HERE.");
                textView2.setText("Get Twyst-ing now!");
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
                            startActivity(new Intent(tutorialActivity, DiscoverActivity.class));
                            tutorialActivity.finish();
                        }
                    }
                });
                break;
        }


        return view;
    }

}
