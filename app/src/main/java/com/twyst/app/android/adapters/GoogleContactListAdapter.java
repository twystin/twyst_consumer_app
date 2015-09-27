package com.twyst.app.android.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import com.twyst.app.android.R;
import com.twyst.app.android.util.GoogleContact;

/**
 * Created by vivek on 07/08/15.
 */
public class GoogleContactListAdapter extends BaseAdapter {
    private LayoutInflater layoutInflater;
    private List<GoogleContact> contactList;

    public GoogleContactListAdapter(LayoutInflater layoutInflater, List<GoogleContact> contactList) {
        this.layoutInflater = layoutInflater;
        this.contactList = contactList;
    }

    public List<GoogleContact> getContactList() {
        return contactList;
    }

    @Override
    public int getCount() {
        return contactList.size();
    }

    @Override
    public Object getItem(int position) {
        return contactList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        GoogleContactViewHolder holder = null;
        if (convertView == null) {
            convertView = layoutInflater.inflate(R.layout.listview_item_invite_contact_email, parent, false);
            holder = new GoogleContactViewHolder();
            holder.nameTextView = (TextView) convertView.findViewById(R.id.contactName);
            holder.emailTextView = (TextView) convertView.findViewById(R.id.contactEmail);
            holder.checkIcon = (ImageView) convertView.findViewById(R.id.checkIcon);
            convertView.setTag(holder);
        } else {
            holder = (GoogleContactViewHolder) convertView.getTag();
        }

        GoogleContact contact = contactList.get(position);
        holder.nameTextView.setText(contact.getName());
        holder.emailTextView.setText(contact.getEmail().toLowerCase());

        if (contact.isSelected()) {
            holder.checkIcon.setImageResource(R.drawable.icon_check_checked);
        } else {
            holder.checkIcon.setImageResource(R.drawable.icon_check_unchecked);
        }

        return convertView;
    }

    static class GoogleContactViewHolder {
        TextView nameTextView;
        TextView emailTextView;
        ImageView checkIcon;
    }
}
