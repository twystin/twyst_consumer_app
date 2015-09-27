package com.twyst.app.android.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.twyst.app.android.R;

/**
 * Created by vivek on 05/08/15.
 */
public class InviteTwystFragment extends Fragment {
    public InviteTwystFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_invite_twyst, container, false);
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

        return view;
    }
}
