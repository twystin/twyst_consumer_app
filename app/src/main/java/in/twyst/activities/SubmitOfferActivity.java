package in.twyst.activities;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
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
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

import java.io.ByteArrayOutputStream;
import java.io.File;

import in.twyst.R;
import in.twyst.model.BaseResponse;
import in.twyst.model.SubmitOffer;
import in.twyst.model.SubmitOfferMeta;
import in.twyst.service.HttpService;
import in.twyst.util.AppConstants;
import in.twyst.util.TwystProgressHUD;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by rahuls on 7/8/15.
 */
public class SubmitOfferActivity extends BaseActivity {

    private static final int REQUEST_CAMERA = 12;
    private static final int SELECT_FILE = 13;
    private ImageView attachImage;
    public String imagePath, fileName, encodedString;
    public static String uploadingImage;
    private boolean fromDrawer;
    public static int IMAGE_RESULTS = 103;
    private LinearLayout takePhotoLayout;
    private LinearLayout attachImageLayout;
    private Button submitBtn;
    private TextView editImageButton;

    @Override
    protected String getTagName() {
        return null;
    }

    @Override
    protected int getLayoutResource() {
        return R.layout.activity_submit_offer;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setupAsChild = true;
        super.onCreate(savedInstanceState);
        hideProgressHUDInLayout();
        fromDrawer = getIntent().getBooleanExtra(AppConstants.INTENT_PARAM_FROM_DRAWER, false);
        String outletName = getIntent().getStringExtra(AppConstants.INTENT_PARAM_SUMIT_OFFER_OUTLET_NAME);
        String outletAddress = getIntent().getStringExtra(AppConstants.INTENT_PARAM_SUMIT_OFFER_OUTLET_ADDRESS);
        final EditText outletNameET = (EditText) findViewById(R.id.outletNameET);
        final EditText offerET = (EditText) findViewById(R.id.offerET);
        final EditText locationET = (EditText) findViewById(R.id.locationEt);
        takePhotoLayout = (LinearLayout) findViewById(R.id.takePhotoLayout);
        attachImageLayout = (LinearLayout) findViewById(R.id.attachImageLayout);
        submitBtn = (Button) findViewById(R.id.submitButton);
        editImageButton = (TextView) findViewById(R.id.editImageButton);
        if (!TextUtils.isEmpty(outletName) || !TextUtils.isEmpty(outletAddress)) {
            outletNameET.setText(outletName);
            locationET.setText(outletAddress);
            outletNameET.setEnabled(false);
            outletNameET.setKeyListener(null);
            locationET.setEnabled(false);
            locationET.setKeyListener(null);
            offerET.requestFocus();

        }


        attachImage = (ImageView) findViewById(R.id.attachImage);

        findViewById(R.id.submitOfferBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (!TextUtils.isEmpty(offerET.getText()) && !TextUtils.isEmpty(outletNameET.getText()) && !TextUtils.isEmpty(locationET.getText())) {
                    final TwystProgressHUD twystProgressHUD = TwystProgressHUD.show(SubmitOfferActivity.this, false, null);

                    SubmitOffer submitOffer = new SubmitOffer();
                    submitOffer.setOutletId(null);
                    SubmitOfferMeta submitOfferMeta = new SubmitOfferMeta();
                    submitOfferMeta.setOffer(outletNameET.getText().toString());
                    submitOfferMeta.setOutlet(offerET.getText().toString());
                    submitOfferMeta.setLocation(locationET.getText().toString());

                    String imagePat = uploadingImage;

                    if (TextUtils.isEmpty(imagePat)) {
                        submitOfferMeta.setPhoto("");

                    } else {

                        submitOfferMeta.setPhoto(imagePat);
                    }


                    submitOffer.setSubmitOfferMeta(submitOfferMeta);

                    HttpService.getInstance().postSubmitOffer(getUserToken(), submitOffer, new Callback<BaseResponse>() {
                        @Override
                        public void success(BaseResponse baseResponse, Response response) {

                            if (baseResponse.isResponse()) {
                                if (baseResponse.isResponse()) {
                                    twystProgressHUD.dismiss();
                                    Toast.makeText(SubmitOfferActivity.this, "Your offer is submitted successfully!", Toast.LENGTH_LONG).show();
                                    finish();
                                } else {
                                    twystProgressHUD.dismiss();
                                    Toast.makeText(SubmitOfferActivity.this, "Unable to submit offer!", Toast.LENGTH_LONG).show();
                                }

                            }
                        }

                        @Override
                        public void failure(RetrofitError error) {
                            twystProgressHUD.dismiss();
                            if (error.getKind() == RetrofitError.Kind.NETWORK) {
                                handleRetrofitError(error);
                            } else {
                                buildAndShowSnackbarWithMessage("Unable to submit offer!");
                            }
                        }
                    });
                } else if (TextUtils.isEmpty(offerET.getText()) && !TextUtils.isEmpty(outletNameET.getText()) && !TextUtils.isEmpty(locationET.getText())) {
                    offerET.setError("Please enter the offer!");
                } else if (!TextUtils.isEmpty(offerET.getText()) && TextUtils.isEmpty(outletNameET.getText()) && !TextUtils.isEmpty(locationET.getText())) {
                    outletNameET.setError("Please enter outlet name!");
                } else if (!TextUtils.isEmpty(offerET.getText()) && !TextUtils.isEmpty(outletNameET.getText()) && TextUtils.isEmpty(locationET.getText())) {
                    locationET.setError("Please enter outlet location!");
                } else {
                    offerET.setError("Please enter the offer!");
                    outletNameET.setError("Please enter outlet name!");
                    locationET.setError("Please enter outlet location!");
                }

            }
        });

        editImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final CharSequence[] items = {"Take Photo", "Choose Photo", "Cancel"};
                AlertDialog.Builder builder = new AlertDialog.Builder(SubmitOfferActivity.this);
                builder.setTitle("Add Photo!");
                builder.setItems(items, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int item) {
                        if (items[item].equals("Take Photo")) {
                            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                            File f = new File(Environment.getExternalStorageDirectory(), "temp.jpg");
                            intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(f));
                            startActivityForResult(intent, REQUEST_CAMERA);
                        } else if (items[item].equals("Choose Photo")) {
                            Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                            intent.setType("image/*");
                            startActivityForResult(Intent.createChooser(intent, "Select File"), SELECT_FILE);
                        } else if (items[item].equals("Cancel")) {
                            dialog.dismiss();
                        }
                    }
                });
                builder.show();


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

        findViewById(R.id.attachBill).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                intent.setType("image/*");
                startActivityForResult(Intent.createChooser(intent, "Select File"), SELECT_FILE);
            }
        });


        findViewById(R.id.submitCancelBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
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

                    Log.i("imagePath", "" + imagePath);
                    Toast.makeText(this, "You have clicked Image", Toast.LENGTH_LONG).show();
                    String fileNameSegments[] = imagePath.split("/");
                    fileName = fileNameSegments[fileNameSegments.length - 1];
                    if (imagePath != null && !imagePath.isEmpty()) {
                        takePhotoLayout.setBackground(getResources().getDrawable(R.drawable.button_grey));
                        takePhotoLayout.setClickable(false);
                        takePhotoLayout.setEnabled(false);
                        attachImageLayout.setBackground(getResources().getDrawable(R.drawable.button_grey));
                        attachImageLayout.setClickable(false);
                        attachImageLayout.setEnabled(false);
                        submitBtn.setBackground(getResources().getDrawable(R.drawable.button_red));
                        editImageButton.setVisibility(View.VISIBLE);
                        // Convert image to String using Base64
                        encodeImagetoString();
                        // When Image is not selected from Gallery
                    }


                } catch (Exception e) {
                    e.printStackTrace();
                }

            } else if (requestCode == SELECT_FILE) {

                Uri selectedImageUri = data.getData();
                String tempPath = getPath(selectedImageUri, SubmitOfferActivity.this);
                Bitmap bm;
                BitmapFactory.Options btmapOptions = new BitmapFactory.Options();
                bm = BitmapFactory.decodeFile(tempPath, btmapOptions);
                attachImage.setImageBitmap(bm);


                String[] filePath = {MediaStore.Images.Media.DATA};
                Cursor cursor = getContentResolver().query(selectedImageUri, filePath, null, null, null);
                cursor.moveToFirst();
                imagePath = cursor.getString(cursor.getColumnIndex(filePath[0]));

                Log.i("imagePath", "" + imagePath);
                Toast.makeText(this, "You have picked Image", Toast.LENGTH_LONG).show();
                String fileNameSegments[] = imagePath.split("/");
                fileName = fileNameSegments[fileNameSegments.length - 1];
                if (imagePath != null && !imagePath.isEmpty()) {
                    takePhotoLayout.setBackground(getResources().getDrawable(R.drawable.button_grey));
                    takePhotoLayout.setClickable(false);
                    takePhotoLayout.setEnabled(false);
                    attachImageLayout.setBackground(getResources().getDrawable(R.drawable.button_grey));
                    attachImageLayout.setClickable(false);
                    attachImageLayout.setEnabled(false);
                    submitBtn.setBackground(getResources().getDrawable(R.drawable.button_red));
                    editImageButton.setVisibility(View.VISIBLE);
                    // Convert image to String using Base64
                    encodeImagetoString();
                    // When Image is not selected from Gallery
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
