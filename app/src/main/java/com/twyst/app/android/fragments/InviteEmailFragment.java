package com.twyst.app.android.fragments;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import com.google.gdata.client.Query;
import com.google.gdata.client.contacts.ContactsService;
import com.google.gdata.data.contacts.ContactEntry;
import com.google.gdata.data.contacts.ContactFeed;
import com.google.gdata.data.extensions.Email;
import com.google.gdata.data.extensions.Name;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import com.twyst.app.android.R;
import com.twyst.app.android.adapters.GoogleContactListAdapter;
import com.twyst.app.android.util.GetAccessToken;
import com.twyst.app.android.util.GoogleConstants;
import com.twyst.app.android.util.GoogleContact;
import com.twyst.app.android.util.Utils;

/**
 * Created by vivek on 05/08/15.
 */
public class InviteEmailFragment extends Fragment {
    final String TAG = getClass().getName();

    private Dialog auth_dialog;
    private ListView list;

    private View view;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_invite_email, container, false);
        view.findViewById(R.id.email).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                launchAuthDialog();
            }
        });




        return view;
    }

    private void launchAuthDialog() {
        final Context context = getActivity();
        auth_dialog = new Dialog(context);
        auth_dialog.setTitle("Google Connect");
        auth_dialog.setCancelable(true);
        auth_dialog.setContentView(R.layout.auth_dialog);

        auth_dialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                getActivity().finish();
            }
        });

        WebView web = (WebView) auth_dialog.findViewById(R.id.webv);
        web.getSettings().setJavaScriptEnabled(true);
        web.loadUrl(GoogleConstants.OAUTH_URL + "?redirect_uri="
                + GoogleConstants.REDIRECT_URI
                + "&response_type=code&client_id=" + GoogleConstants.CLIENT_ID
                + "&scope=" + GoogleConstants.OAUTH_SCOPE);
        web.setWebViewClient(new WebViewClient() {
            boolean authComplete = false;

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                if (url.contains("?code=") && authComplete != true) {
                    Uri uri = Uri.parse(url);
                    String authCode = uri.getQueryParameter("code");
                    authComplete = true;
                    auth_dialog.dismiss();
                    new GoogleAuthToken(context).execute(authCode);
                } else if (url.contains("error=access_denied")) {
                    Log.i("", "ACCESS_DENIED_HERE");
                    authComplete = true;
                    auth_dialog.dismiss();
                }
            }
        });
        auth_dialog.show();
    }

    private class GetGoogleContacts extends
            AsyncTask<String, String, List<ContactEntry>> {

        private ProgressDialog pDialog;
        private Context context;

        public GetGoogleContacts(Context context) {
            this.context = context;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(context);
            pDialog.setMessage("Authenticated. Getting Google Contacts ...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
                @Override
                public void onCancel(DialogInterface dialog) {
                    getActivity().finish();
                }
            });
            pDialog.show();
        }

        @Override
        protected List<ContactEntry> doInBackground(String... args) {
            String accessToken = args[0];
            ContactsService contactsService = new ContactsService(GoogleConstants.APP);
            contactsService.setHeader("Authorization", "Bearer " + accessToken);
            contactsService.setHeader("GData-Version", "3.0");
            List<ContactEntry> contactEntries = null;
            try {
                URL feedUrl = new URL(GoogleConstants.CONTACTS_URL);
                Query myQuery = new Query(feedUrl);
                myQuery.setMaxResults(5000);
                ContactFeed resultFeed = contactsService.getFeed(myQuery, ContactFeed.class);
                contactEntries = resultFeed.getEntries();
            } catch (Exception e) {
                pDialog.dismiss();
                Toast.makeText(context, "Failed to get Contacts",
                        Toast.LENGTH_SHORT).show();
            }
            return contactEntries;
        }

        @Override
        protected void onPostExecute(List<ContactEntry> googleContacts) {
            if (null != googleContacts && googleContacts.size() > 0) {
                List<GoogleContact> contacts = new ArrayList<GoogleContact>();

                for (ContactEntry contactEntry : googleContacts) {
                    String name = "";
                    String email = "";

                    if (contactEntry.hasName()) {
                        Name tmpName = contactEntry.getName();
                        if (tmpName.hasFullName()) {
                            name = tmpName.getFullName().getValue();
                        } else {
                            if (tmpName.hasGivenName()) {
                                name = tmpName.getGivenName().getValue();
                                if (tmpName.getGivenName().hasYomi()) {
                                    name += " ("
                                            + tmpName.getGivenName().getYomi()
                                            + ")";
                                }
                                if (tmpName.hasFamilyName()) {
                                    name += tmpName.getFamilyName().getValue();
                                    if (tmpName.getFamilyName().hasYomi()) {
                                        name += " ("
                                                + tmpName.getFamilyName()
                                                .getYomi() + ")";
                                    }
                                }
                            }
                        }
                    }
                    List<Email> emails = contactEntry.getEmailAddresses();
                    if (null != emails && emails.size() > 0) {
                        Email tempEmail = (Email) emails.get(0);
                        email = tempEmail.getAddress();
                    }

                    if (TextUtils.isEmpty(name) || TextUtils.isEmpty(email)) {
                        continue;
                    }

                    GoogleContact googleContact = new GoogleContact(Utils.capitalize(name), email);
                    contacts.add(googleContact);
                }

                setContactList(contacts);

            } else {
                Log.e(TAG, "No GoogleContact Found.");
                Toast.makeText(context, "No GoogleContact Found.", Toast.LENGTH_SHORT).show();
            }
            pDialog.dismiss();
        }

    }

    private void setContactList(final List<GoogleContact> contacts) {
        Collections.sort(contacts, new Comparator<GoogleContact>() {
            @Override
            public int compare(GoogleContact lhs, GoogleContact rhs) {
                return lhs.getName().compareTo(rhs.getName());
            }
        });

        Log.d(getClass().getSimpleName(), "googleContacts.size:" + contacts.size());
        Log.d(getClass().getSimpleName(), "googleContacts:" + contacts);

        view.findViewById(R.id.emailLayout).setVisibility(View.GONE);
        view.findViewById(R.id.emailContactsLayout).setVisibility(View.VISIBLE);

        list = (ListView) view.findViewById(R.id.emailTwystList);
        //list.setAdapter(new ArrayAdapter<>(view.getContext(), R.layout.listview_item_invite_contact_email, R.id.contactName, contacts));

        list.setAdapter(new GoogleContactListAdapter(getActivity().getLayoutInflater(), contacts));
        list.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
        list.setItemsCanFocus(false);

        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                //view.setSelected(true);

//                for (int j = 0; j < adapterView.getChildCount(); j++) {
//                    View childAt = adapterView.getChildAt(j);
//                    ImageView checkIcon = (ImageView) childAt.findViewById(R.id.checkIcon);
//                    checkIcon.setImageResource(R.drawable.icon_check_unchecked);
//                }

                ImageView checkIcon = (ImageView) view.findViewById(R.id.checkIcon);
                GoogleContact contact = (GoogleContact) adapterView.getItemAtPosition(i);
                if (contact.isSelected()) {
                    contact.setSelected(false);
                    checkIcon.setImageResource(R.drawable.icon_check_unchecked);
                } else {
                    contact.setSelected(true);
                    checkIcon.setImageResource(R.drawable.icon_check_checked);
                }

            }
        });

        view.findViewById(R.id.selectAllBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                for (GoogleContact contact : contacts) {
                    contact.setSelected(true);
                    GoogleContactListAdapter adapter = (GoogleContactListAdapter) list.getAdapter();
                    adapter.notifyDataSetChanged();
                }

            }
        });

        view.findViewById(R.id.inviteBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final SparseBooleanArray checkedItems = list.getCheckedItemPositions();
                Log.d(getClass().getSimpleName(), "checked items: " + checkedItems.size());

                ArrayList<GoogleContact> selected = new ArrayList<>();
                for (GoogleContact contact: contacts) {
                    if (contact.isSelected()) {
                        selected.add(contact);
                    }
                }
                Log.d(getClass().getSimpleName(), "selected items: " + selected.size());
                Log.d(getClass().getSimpleName(), "selected items: " + selected);
            }
        });

        //ArrayAdapter<GoogleContact> adapter = new ArrayAdapter<GoogleContact>(getActivity(), android.R.layout.simple_list_item_multiple_choice, contacts);

        //list.setAdapter(adapter);
    }

    private class GoogleAuthToken extends AsyncTask<String, String, JSONObject> {
        private ProgressDialog pDialog;
        private Context context;

        public GoogleAuthToken(Context context) {
            this.context = context;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(context);
            pDialog.setMessage("Contacting Google ...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
                @Override
                public void onCancel(DialogInterface dialog) {
                    getActivity().finish();
                }
            });
            pDialog.show();
        }

        @Override
        protected JSONObject doInBackground(String... args) {
            String authCode = args[0];
            GetAccessToken jParser = new GetAccessToken();
            JSONObject json = jParser.gettoken(GoogleConstants.TOKEN_URL,
                    authCode, GoogleConstants.CLIENT_ID,
                    GoogleConstants.CLIENT_SECRET,
                    GoogleConstants.REDIRECT_URI, GoogleConstants.GRANT_TYPE);
            return json;
        }

        @Override
        protected void onPostExecute(JSONObject json) {
            pDialog.dismiss();
            if (json != null) {
                try {
                    String tok = json.getString("access_token");
                    String expire = json.getString("expires_in");
                    String refresh = json.getString("refresh_token");
                    new GetGoogleContacts(context).execute(tok);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
