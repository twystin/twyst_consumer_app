package com.twyst.app.android.util;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.provider.ContactsContract;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import com.twyst.app.android.model.Friend;

/**
 * Created by rahuls on 29/6/15.
 */
public class PhoneBookContacts {

    private List<Friend.Friends> phoneContactList;

    public List<Friend.Friends> getPhoneContactList() {
        return phoneContactList;
    }

    private static final String PHONE_REGEX = "\\d{10}";

    private static PhoneBookContacts instance = new PhoneBookContacts();

    private PhoneBookContacts() {

    }

    public static PhoneBookContacts getInstance() {
        return instance;
    }



    public void loadContacts(final Context context) {

        List<String> lookupListTemp = new ArrayList<String>();
        if (context != null) {
            ContentResolver cr = context.getContentResolver();
            String[] PROJECTION = new String[]{
                    ContactsContract.Contacts._ID,
                    ContactsContract.Contacts.DISPLAY_NAME,
                    ContactsContract.CommonDataKinds.Phone.NUMBER,
                    ContactsContract.CommonDataKinds.Email.DATA,
                    ContactsContract.Contacts.LOOKUP_KEY
            };

            String order = ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME + " ASC ";
            Cursor cur = cr.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, PROJECTION, null, null, order);
            phoneContactList = new ArrayList<>();

            if (cur.moveToFirst()) {
                do {

                    String id = cur.getString(0);
                    String name = cur.getString(1);
                    String phone = cur.getString(2);
                    String lookUpKey = cur.getString(4);

                    String phoneStr = PhoneBookContacts.get10DigitPhoneNumber(phone);
                    Friend.Friends friends1 = new Friend.Friends();
                    if (phoneStr.length() == 10 && Pattern.matches(PHONE_REGEX, phoneStr) && !lookupListTemp.contains(lookUpKey)) {

                        friends1.setPhone(phoneStr);
                        friends1.setName(name);
                        friends1.setId(null);
                        phoneContactList.add(friends1);
                        lookupListTemp.add(lookUpKey);
                    }


                } while (cur.moveToNext());
            }

            cur.close();
        }

    }

    public int getContactsCount(Context context){
        ContentResolver cr = context.getContentResolver();
        String order = ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME + " ASC ";
        Cursor cur = cr.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, null, null, order);

        return cur.getCount();

    }


    public static String get10DigitPhoneNumber(String phone) {
        String phoneStr = phone.trim().replace(" ", "");
        phoneStr = phoneStr.toString().trim().replace("(", "");
        phoneStr = phoneStr.toString().trim().replace(")", "");
        phoneStr = phoneStr.toString().trim().replace("-", "");


        if (phoneStr.length() > 10) {
            phoneStr = phoneStr.substring(phoneStr.length() - 10);
        }
        return phoneStr;
    }
}
