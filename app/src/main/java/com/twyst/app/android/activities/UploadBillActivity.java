package com.twyst.app.android.activities;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.view.GravityCompat;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Base64;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import com.appsflyer.AppsFlyerLib;
import com.twyst.app.android.R;
import com.twyst.app.android.model.BaseResponse;
import com.twyst.app.android.model.OutletList;
import com.twyst.app.android.model.UploadBill;
import com.twyst.app.android.model.UploadBillMeta;
import com.twyst.app.android.service.HttpService;
import com.twyst.app.android.util.AppConstants;
import com.twyst.app.android.util.TwystProgressHUD;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by rahuls on 6/8/15.
 */
public class UploadBillActivity extends BaseActivity {

    private boolean fromDrawer;
    private static final int REQUEST_CAMERA = 10;
    private static final int SELECT_FILE = 11;
    private ImageView attachImage;
    public String imagePath, fileName, encodedString;
    private String uploadingImage;
    private TextView editImageButton;
    public static int IMAGE_RESULTS = 100;
    private LinearLayout takePhotoLayoutBack;
    private Button uploadPhotoButton;
    private String mSelectedAddress = "";
    private String mSelectedName = "";
    private String mOutletID = "";

    @Override
    protected String getTagName() {
        return null;
    }

    @Override
    protected int getLayoutResource() {
        return R.layout.activity_upload_bill;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setupAsChild = true;
        super.onCreate(savedInstanceState);
        fromDrawer = getIntent().getBooleanExtra(AppConstants.INTENT_PARAM_FROM_DRAWER, false);
        hideProgressHUDInLayout();

        mOutletID = getIntent().getStringExtra(AppConstants.INTENT_PARAM_UPLOAD_BILL_OUTLET_ID);
        String outletName = getIntent().getStringExtra(AppConstants.INTENT_PARAM_UPLOAD_BILL_OUTLET_NAME);
        String outletAddress = getIntent().getStringExtra(AppConstants.INTENT_PARAM_UPLOAD_BILL_OUTLET_ADDRESS);
        final AutoCompleteTextView outletNameET = (AutoCompleteTextView) findViewById(R.id.outletNameET);
        final EditText locationET = (EditText) findViewById(R.id.locationEt);
        final TextView tvLocationLabel = (TextView) findViewById(R.id.tvLocationLabel);
        final EditText outletBillDate = (EditText) findViewById(R.id.outletBillET);
        final Spinner spinnerLocationList = (Spinner) findViewById(R.id.spinnerLocationList);
        editImageButton = (TextView) findViewById(R.id.editImageButton);
        takePhotoLayoutBack = (LinearLayout) findViewById(R.id.takePhotoLayout);
        if (!TextUtils.isEmpty(outletName)) {
            outletNameET.setKeyListener(null);
            outletNameET.setEnabled(false);
        }

        if (!TextUtils.isEmpty(outletName) || !TextUtils.isEmpty(outletAddress)) {
            spinnerLocationList.setVisibility(View.GONE);
            locationET.setVisibility(View.VISIBLE);
            outletNameET.setText(outletName);
            mSelectedAddress = outletAddress;
            mSelectedName = outletName;
            locationET.setText(outletAddress);
            outletNameET.setEnabled(false);
            outletNameET.setKeyListener(null);
            locationET.setEnabled(false);
            locationET.setKeyListener(null);

        }else{
            locationET.setVisibility(View.VISIBLE);
            locationET.setEnabled(false);
            spinnerLocationList.setVisibility(View.GONE);
            tvLocationLabel.setVisibility(View.VISIBLE);

            final ArrayList<OutletList> list = getOutletListsArray();

            outletNameET.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                    spinnerLocationList.setVisibility(View.GONE);
                    locationET.setVisibility(View.VISIBLE);
                    mSelectedName="";
                    mSelectedAddress ="";
                    mOutletID = "";
                }

                @Override
                public void afterTextChanged(Editable editable) {

                }
            });

            outletNameET.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                    String selectedName = adapterView.getItemAtPosition(i).toString();
                    ArrayList<OutletList> outletListFilteredAddressArray = getFilteredOutletListsAddressArray(list, selectedName);
                    ArrayList outletListLocations = getOutletListAddress(outletListFilteredAddressArray, selectedName);
                    spinnerLocationList.setVisibility(View.VISIBLE);
                    locationET.setVisibility(View.GONE);
                    View view1 = UploadBillActivity.this.getCurrentFocus();
                    if (view1 != null) {
                        InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                        imm.hideSoftInputFromWindow(view1.getWindowToken(), 0);
                    }
                    ArrayAdapter locationsAdapter = new ArrayAdapter(UploadBillActivity.this, R.layout.location_dropdown_item,outletListLocations);
                    spinnerLocationList.setAdapter(locationsAdapter);
                    spinnerLocationList.requestFocus();
                    mSelectedName = selectedName;
                    mSelectedAddress = spinnerLocationList.getSelectedItem().toString();
                    mOutletID = getOutletID(outletListFilteredAddressArray,mSelectedAddress);
                }
            });
            ArrayAdapter namesAdapter = new ArrayAdapter(this, R.layout.location_dropdown_item, getOutletListNames(list));
            outletNameET.setAdapter(namesAdapter);

            spinnerLocationList.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                    String selectedAddress = adapterView.getItemAtPosition(i).toString();
                    ArrayList<OutletList> outletListFilteredAddressArray = getFilteredOutletListsAddressArray(list, mSelectedName);
                    mSelectedAddress = selectedAddress;
                    mOutletID = getOutletID(outletListFilteredAddressArray,mSelectedAddress);
                }

                @Override
                public void onNothingSelected(AdapterView<?> adapterView) {

                }
            });
        }


        outletNameET.setText(outletName);

        attachImage = (ImageView) findViewById(R.id.attachImage);
        uploadPhotoButton = (Button) findViewById(R.id.uploadPhotoButton);

        findViewById(R.id.uploadBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                if (!TextUtils.isEmpty(mSelectedName) && !TextUtils.isEmpty(mSelectedAddress) && !TextUtils.isEmpty(outletBillDate.getText()) && attachImage.getDrawable() != null) {
                    final TwystProgressHUD twystProgressHUD = TwystProgressHUD.show(UploadBillActivity.this, false, null);

                    UploadBill uploadBill = new UploadBill();

                    uploadBill.setOutletId(mOutletID);
                    UploadBillMeta uploadBillMeta = new UploadBillMeta();
                    uploadBillMeta.setBillDate(outletBillDate.getText().toString());
                    uploadBillMeta.setOutletName(mSelectedName);
                    uploadBillMeta.setLocation(mSelectedAddress);

                    String imagePat = uploadingImage;

                    if (TextUtils.isEmpty(imagePat)) {
                        uploadBillMeta.setPhoto("");

                    } else {

                        uploadBillMeta.setPhoto(imagePat);
                    }

                    uploadBill.setUploadBillMeta(uploadBillMeta);

                    HttpService.getInstance().uploadBill(getUserToken(), uploadBill, new Callback<BaseResponse>() {
                        @Override
                        public void success(BaseResponse baseResponse, Response response) {
                            if (baseResponse.isResponse()) {
                                if (baseResponse.isResponse()) {
                                    twystProgressHUD.dismiss();
                                    Toast.makeText(UploadBillActivity.this, "Bill uploaded successfully!", Toast.LENGTH_LONG).show();
                                    Intent intent = new Intent(UploadBillActivity.this, DiscoverActivity.class);
                                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                    startActivity(intent);


                                } else {
                                    twystProgressHUD.dismiss();
                                    Toast.makeText(UploadBillActivity.this, "Unable to upload bill!", Toast.LENGTH_LONG).show();
                                }

                            }
                            takePhotoLayoutBack.setBackground(getResources().getDrawable(R.drawable.button_red));
                            takePhotoLayoutBack.setClickable(true);
                            takePhotoLayoutBack.setEnabled(true);
                            uploadPhotoButton.setBackground(getResources().getDrawable(R.drawable.button_grey));
                            editImageButton.setVisibility(View.GONE);

                        }

                        @Override
                        public void failure(RetrofitError error) {
                            twystProgressHUD.dismiss();
                            if (error.getKind() == RetrofitError.Kind.NETWORK) {
                                handleRetrofitError(error);
                            } else {
                                buildAndShowSnackbarWithMessage("Unable to upload bill!");
                            }
                        }
                    });

                }else if (TextUtils.isEmpty(mSelectedName)) {
                    outletNameET.setError("Please select outlet name from the list!");
                } else if (TextUtils.isEmpty(outletBillDate.getText())) {
                    outletBillDate.setError("Please select a date!");
                } else if (attachImage.getDrawable() == null) {
                    Toast.makeText(UploadBillActivity.this, "Please attach a photo or take photo!", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(UploadBillActivity.this, "Please enter all details with photo!", Toast.LENGTH_SHORT).show();
                }
            }
        });

        final Calendar c = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        String dateFormatted = sdf.format(c.getTime());
        outletBillDate.setText(dateFormatted);


        outletBillDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DialogFragment newFragment = new DatePickerFragment();
                newFragment.show(getSupportFragmentManager(), "datePicker");
            }
        });

        findViewById(R.id.calIcon).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DialogFragment newFragment = new DatePickerFragment();
                newFragment.show(getSupportFragmentManager(), "datePicker");
            }
        });

        findViewById(R.id.takePhoto).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                File f = new File(Environment.getExternalStorageDirectory(), "temp.jpg");
                intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(f));
                startActivityForResult(intent, REQUEST_CAMERA);


            }
        });

        editImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                File f = new File(Environment.getExternalStorageDirectory(), "temp.jpg");
                intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(f));
                startActivityForResult(intent, REQUEST_CAMERA);
            }
        });


    }

    @Override
    public void onResume() {
        super.onResume();
        AppsFlyerLib.onActivityResume(this);
    }
    @Override
    public void onPause() {
        super.onPause();
        AppsFlyerLib.onActivityPause(this);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.d(getClass().getSimpleName(), "onActivityResult called");

        if (resultCode == RESULT_OK) {
            if (requestCode == REQUEST_CAMERA) {
                Log.d(getClass().getSimpleName(), "picture taken");
                try {
                    File f = new File(Environment.getExternalStorageDirectory().toString());
                    for (File temp : f.listFiles()) {
                        if (temp.getName().equals("temp.jpg")) {
                            f = temp;
                            break;
                        }
                    }
                    BitmapFactory.Options btmapOptions = new BitmapFactory.Options();
                    Bitmap bm = BitmapFactory.decodeFile(f.getAbsolutePath(), btmapOptions);

                    attachImage.setImageBitmap(bm);


                    imagePath = f.getAbsolutePath();

                    Log.i("imagePath", "" + imagePath);
                    Toast.makeText(this, "You have clicked Image", Toast.LENGTH_LONG).show();
                    String fileNameSegments[] = imagePath.split("/");
                    fileName = fileNameSegments[fileNameSegments.length - 1];
                    if (imagePath != null && !imagePath.isEmpty()) {
                        takePhotoLayoutBack.setBackground(getResources().getDrawable(R.drawable.button_grey));
                        takePhotoLayoutBack.setClickable(false);
                        takePhotoLayoutBack.setEnabled(false);
                        uploadPhotoButton.setBackground(getResources().getDrawable(R.drawable.button_red));
                        editImageButton.setVisibility(View.VISIBLE);
                        // Convert image to String using Base64
                        encodeImagetoString();
                        // When Image is not selected from Gallery
                    }


                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        }
    }

    public String getPath(Uri uri, Activity activity) {
        String[] projection = {MediaStore.MediaColumns.DATA};
        Cursor cursor = activity.managedQuery(uri, projection, null, null, null);
        int column_index = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA);
        cursor.moveToFirst();
        return cursor.getString(column_index);
    }

    public void encodeImagetoString() {
        new AsyncTask<Void, Void, String>() {

            protected void onPreExecute() {
            }

            ;

            @Override
            protected String doInBackground(Void... params) {
                BitmapFactory.Options options = null;
                options = new BitmapFactory.Options();
                options.inSampleSize = 3;
                Bitmap bitmap = BitmapFactory.decodeFile(imagePath, options);

                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                ByteArrayOutputStream stream2 = new ByteArrayOutputStream();
                // Must compress the Image to reduce image size to make upload easy
                bitmap.compress(Bitmap.CompressFormat.JPEG, 50, stream);

                byte[] byte_arr = stream.toByteArray();
                // Encode Image to String
                encodedString = Base64.encodeToString(byte_arr, Base64.NO_WRAP);
                return "";
            }

            @Override
            protected void onPostExecute(String msg) {
                uploadingImage = encodedString;
            }
        }.execute(null, null, null);
    }

    public static class DatePickerFragment extends DialogFragment
            implements DatePickerDialog.OnDateSetListener {

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            // Use the current date as the default date in the picker
            final Calendar c = Calendar.getInstance();
            int year = c.get(Calendar.YEAR);
            int month = c.get(Calendar.MONTH);
            int day = c.get(Calendar.DAY_OF_MONTH);

            // Create a new instance of DatePickerDialog and return it
            DatePickerDialog datePickerDialog = new DatePickerDialog(getActivity(), android.R.style.Theme_Holo_Dialog_MinWidth, this, year, month, day);
            datePickerDialog.getDatePicker().setCalendarViewShown(false);
            datePickerDialog.getDatePicker().setSpinnersShown(true);
            DatePicker dp = datePickerDialog.getDatePicker();

            c.add(Calendar.DAY_OF_MONTH, -6);

            dp.setMinDate(c.getTimeInMillis());

            dp.setMaxDate(new Date().getTime());
            return datePickerDialog;
        }

        public void onDateSet(DatePicker view, int year, int month, int day) {
            Log.d(getClass().getSimpleName(), "year=" + year + ", month=" + month + ", day=" + day);
            TextView outletBillET = (TextView) getActivity().findViewById(R.id.outletBillET);

            Calendar calendar = Calendar.getInstance();
            calendar.set(Calendar.YEAR, year);
            calendar.set(Calendar.MONTH, month);
            calendar.set(Calendar.DAY_OF_MONTH, day);

            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
            String dateFormatted = sdf.format(calendar.getTime());
            outletBillET.setText(dateFormatted);
        }
    }

    @Override
    public void onBackPressed() {
        if (drawerOpened) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            if (fromDrawer) {
                //clear history and go to discover
                Intent intent = new Intent(getBaseContext(), DiscoverActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            } else {
                super.onBackPressed();
            }
        }
    }
}

