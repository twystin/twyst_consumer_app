package in.twyst.activities;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Base64;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import in.twyst.R;
import in.twyst.model.BaseResponse;
import in.twyst.model.UploadBill;
import in.twyst.model.UploadBillMeta;
import in.twyst.service.HttpService;
import in.twyst.util.AppConstants;
import in.twyst.util.TwystProgressHUD;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by rahuls on 6/8/15.
 */
public class UploadBillActivity extends BaseActivity {


    private static final int REQUEST_CAMERA = 10;
    private static final int SELECT_FILE = 11;
    private ImageView attachImage;
    public String imagePath,fileName,encodedString;
    public static String uploadingImage;

    public static int IMAGE_RESULTS = 100;

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
        setupAsChild= true;
        super.onCreate(savedInstanceState);

        hideProgressHUDInLayout();

        String outletName = getIntent().getStringExtra(AppConstants.INTENT_PARAM_SUMIT_OFFER_OUTLET_NAME);


        final EditText outletNameET = (EditText) findViewById(R.id.outletNameET);
        final EditText outletBillDate = (EditText) findViewById(R.id.outletBillET);

        outletNameET.setText(outletName);

        attachImage = (ImageView) findViewById(R.id.attachImage);

        findViewById(R.id.uploadBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                if (!TextUtils.isEmpty(outletNameET.getText()) && !TextUtils.isEmpty(outletBillDate.getText()) && attachImage.getDrawable()!=null) {
                    final TwystProgressHUD twystProgressHUD = TwystProgressHUD.show(UploadBillActivity.this, false, null);
                    SharedPreferences prefs = getSharedPreferences(AppConstants.PREFERENCE_SHARED_PREF_NAME, Context.MODE_PRIVATE);
                    String usertoken = prefs.getString(AppConstants.PREFERENCE_USER_TOKEN, "");

                    UploadBill uploadBill = new UploadBill();

                    uploadBill.setOutletId(null);
                    UploadBillMeta uploadBillMeta = new UploadBillMeta();
                    uploadBillMeta.setBillDate(outletBillDate.getText().toString());
                    uploadBillMeta.setOutletName(outletNameET.getText().toString());

                    String imagePat = uploadingImage;

                    if (TextUtils.isEmpty(imagePat)) {
                        uploadBillMeta.setPhoto("");

                    } else {

                        uploadBillMeta.setPhoto(imagePat);
                    }

                    uploadBill.setUploadBillMeta(uploadBillMeta);

                    HttpService.getInstance().uploadBill(usertoken, uploadBill, new Callback<BaseResponse>() {
                        @Override
                        public void success(BaseResponse baseResponse, Response response) {
                            if (baseResponse.isResponse()) {
                                if(baseResponse.isResponse()) {
                                    twystProgressHUD.dismiss();
                                    Toast.makeText(UploadBillActivity.this, "Bill uploaded successfully!", Toast.LENGTH_LONG).show();
                                    startActivity(new Intent(UploadBillActivity.this, DiscoverActivity.class));
                                }else {
                                    twystProgressHUD.dismiss();
                                    Toast.makeText(UploadBillActivity.this, "Unable to upload bill!", Toast.LENGTH_LONG).show();
                                }

                            }
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

                }else if (TextUtils.isEmpty(outletNameET.getText()) && !TextUtils.isEmpty(outletBillDate.getText()) && attachImage.getDrawable()!=null) {
                    outletNameET.setError("Please enter outlet name!");
                } else if (!TextUtils.isEmpty(outletNameET.getText()) && TextUtils.isEmpty(outletBillDate.getText()) && attachImage.getDrawable()!=null) {
                    outletBillDate.setError("Please select a date!");
                } else if (!TextUtils.isEmpty(outletNameET.getText()) && !TextUtils.isEmpty(outletBillDate.getText()) && attachImage.getDrawable()==null) {
                    Toast.makeText(UploadBillActivity.this, "Please attach a photo or take photo!",Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(UploadBillActivity.this, "Please enter all details with photo!",Toast.LENGTH_SHORT).show();
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

                    Log.i("imagePath",""+imagePath);
                    Toast.makeText(this, "You have clicked Image", Toast.LENGTH_LONG).show();
                    String fileNameSegments[] = imagePath.split("/");
                    fileName = fileNameSegments[fileNameSegments.length - 1];
                    if (imagePath != null && !imagePath.isEmpty()) {

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

            protected void onPreExecute() {};

            @Override
            protected String doInBackground(Void... params) {
                BitmapFactory.Options options = null;
                options = new BitmapFactory.Options();
                options.inSampleSize = 3;
                Bitmap bitmap = BitmapFactory.decodeFile(imagePath,options);

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
            DatePickerDialog datePickerDialog = new DatePickerDialog(getActivity(),android.R.style.Theme_Holo_Dialog_MinWidth,this,year,month, day);
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
}

