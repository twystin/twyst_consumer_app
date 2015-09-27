package com.twyst.app.android.fragments;

import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.twyst.app.android.R;

/**
 * Created by vivek on 05/08/15.
 */
public class InvitePhonebookFragment extends Fragment {
    public InvitePhonebookFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_invite_phonebook, container, false);
        ListView inviteTwystList = (ListView) view.findViewById(R.id.inviteTwystList);

        String[] contacts = {
                "Jonathan Rhys Meyers",
                "Jonathan Rhys Meyers",
                "Jonathan Rhys Meyers",
                "Jonathan Rhys Meyers",
                "Jonathan Rhys Meyers",
                "Jonathan Rhys Meyers",
                "Jonathan Rhys Meyers",
                "Jonathan Rhys Meyers",
                "Jonathan Rhys Meyers",
                "Jonathan Rhys Meyers",
                "Jonathan Rhys Meyers",
                "Jonathan Rhys Meyers",
                "Jonathan Rhys Meyers",
                "Jonathan Rhys Meyers",
                "Jonathan Rhys Meyers",
                "Jonathan Rhys Meyers",
                "Jonathan Rhys Meyers",
                "Jonathan Rhys Meyers",
                "Jonathan Rhys Meyers",
                "Jonathan Rhys Meyers",
                "Jonathan Rhys Meyers",
                "Jonathan Rhys Meyers",
                "Jonathan Rhys Meyers",
                "Jonathan Rhys Meyers",
                "Jonathan Rhys Meyers",
                "Jonathan Rhys Meyers",
                "Jonathan Rhys Meyers"
        };

        inviteTwystList.setAdapter(new ArrayAdapter<String>(view.getContext(), R.layout.listview_item_invite_contact, R.id.contactName, contacts));

        view.findViewById(R.id.topLayout).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                PackageManager pm=getActivity().getPackageManager();
                try {

                    Intent waIntent = new Intent(Intent.ACTION_SEND);
                    waIntent.setType("text/plain");
                    String text = "Twyst - cool app";

                    PackageInfo info= pm.getPackageInfo("com.whatsapp", PackageManager.GET_META_DATA);
                    //Check if package exists or not. If not then code
                    //in catch block will be called
                    waIntent.setPackage("com.whatsapp");
                    waIntent.putExtra(Intent.EXTRA_TEXT, text);
                    startActivity(waIntent);

                } catch (PackageManager.NameNotFoundException e) {
                    Toast.makeText(getActivity(), "WhatsApp not Installed", Toast.LENGTH_SHORT).show();
                }
               /* Intent sendIntent = new Intent();
                sendIntent.setAction(Intent.ACTION_SEND);
                sendIntent.setPackage("com.whatsapp");
                sendIntent.putExtra(Intent.EXTRA_TEXT, "Twyst - cool app");
                sendIntent.setType("text/plain");
                startActivity(sendIntent);*/
            }
        });

        return view;
    }
}
