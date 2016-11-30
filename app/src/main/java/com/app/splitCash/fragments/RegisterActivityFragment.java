package com.app.splitCash.fragments;

import android.app.Activity;
import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.app.splitCash.R;
import com.app.splitCash.constants.DBConstants;
import com.app.splitCash.imageManager.data_storage.ImageDBManager;
import com.app.splitCash.dataBase.Message;
import com.app.splitCash.dataBase.UserDBAdapter;
import com.app.splitCash.imageManager.conversion.ImageConverterManager;
import com.app.splitCash.sharedPreferences.SharedPreferenceManager;
import com.app.splitCash.toast.ToastManager;

import java.io.FileNotFoundException;
import java.io.InputStream;

/**
 * Created by Aakash Singh on 15-05-2016.
 */
public class RegisterActivityFragment extends Fragment implements View.OnClickListener {

    private final static String userNameForRegistration = "userNameForRegistration";
    private final static String phoneNumberForRegistration = "phoneNumberForRegistration";
    private static final String isRetryActivityRequired = "isRetryActivityRequired";
    private static final String isUserImageSelected = "isUserImageSelected";
    private SharedPreferenceManager sharedPreferenceManager;

    private boolean isRetryActivityRequiredFlagTrue;

    private EditText nameET;
    private EditText phoneET;

    private String userName;
    private String phoneNumber;
    private ImageView userImageView;
    private TextView userImageTextView;

    private final int SELECT_PHOTO = 100;
    private Bitmap yourSelectedImage;
    private boolean isImageSelectedFlag = false;
    private UserDBAdapter userDBAdapter;

    private Bitmap imageBitMap;
    private ImageDBManager imageDBManager;
    private ImageConverterManager imageConverterManager;
    private ProgressDialog progressDialog;
    private String imagePath;
    private ToastManager toastManager;

    private static boolean result = false;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.registration_main_fragment_circular_image, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        //CREATING rEGISTERaCTIVITY OBJECT TO HIDE/SHOW PROGRESSbAR
        //registerActivity = new RegisterActivity();
        progressDialog = new ProgressDialog(getActivity());
        toastManager = new ToastManager(getActivity());

        progressDialog.setMessage("Loading");

        //creating object of ImageDBManager to store image in DB
        imageDBManager = new ImageDBManager(getActivity());
        imageConverterManager = new ImageConverterManager(getActivity());

        //Now binding Imageview to java object
        userImageView = (ImageView) getActivity().findViewById(R.id.userImageView);
        userImageTextView = (TextView) getActivity().findViewById(R.id.userImageTextView);

        //Setting userImageview and userImageTextView
        userImageView.setImageResource(R.drawable.user_imageview_thumbnail);
        userImageTextView.setText("Select profile picture");

        //setting up onclickListener
        userImageView.setOnClickListener(this);

        sharedPreferenceManager = new SharedPreferenceManager(getActivity());
        isRetryActivityRequiredFlagTrue = sharedPreferenceManager.getBoolean(isRetryActivityRequired);

        //making imageSelect flag as FALse to let RegisterActvity know in case of NO IMAGE SELECTED BY USER
        sharedPreferenceManager.putBoolean(isUserImageSelected, false);

        //checking if NO NETWORK CONDITION RAISED OR NOT
        if (isRetryActivityRequiredFlagTrue) {

            //AGAIN MAKE IT TRUE IF IMAGE IS ALREADY SELECTED AND APP IS OPENED AGAIN
            //making imageSelect flag as FALse to let RegisterActvity know in case of NO IMAGE SELECTED BY USER
            sharedPreferenceManager.putBoolean(isUserImageSelected, true);

            AsyncTask<Void, Void, Bitmap> mResumeTask = new AsyncTask<Void, Void, Bitmap>() {

                @Override
                protected void onPreExecute() {
                    super.onPreExecute();

                    //---Show userName and password editText with values which entered ealier from sharedPreference
                    userName = sharedPreferenceManager.getString(DBConstants.USER_NAME);
                    phoneNumber = sharedPreferenceManager.getString(DBConstants.USER_PHONE_NUMBER);

                    nameET = (EditText) getActivity().findViewById(R.id.nameET);
                    phoneET = (EditText) getActivity().findViewById(R.id.phoneET);

                    nameET.setText(userName);
                    phoneET.setText(phoneNumber);

                    //SHOWING pROGRESSBAR WHILE DATA LOADS
                    progressDialog.show();
                }

                @Override
                protected Bitmap doInBackground(Void... params) {
                    //********************important****************************
                    //Retrieving image fromDB TO SHOW AGAIN TO USER
                    ImageDBManager imageDBManager = new ImageDBManager(getActivity());
                    imageBitMap = imageDBManager.getBitmap();
                    return imageBitMap;
                }

                @Override
                protected void onPostExecute(Bitmap imageInBitMap) {
                    //MAKINH IMAGEVIEW TO BE SET WITH IMAGE FROM DB
                    setUserNameAndImage(imageInBitMap);
                }
            };

            mResumeTask.execute();
        }
    }

    @Override
    public void onResume() {
        super.onResume();

        //checking if NO NETWORK CONDITION RAISED OR NOT
        if (isRetryActivityRequiredFlagTrue) {

            //AGAIN MAKE IT TRUE IF IMAGE IS ALREADY SELECTED AND APP IS OPENED AGAIN
            //making imageSelect flag as FALse to let RegisterActvity know in case of NO IMAGE SELECTED BY USER
            sharedPreferenceManager.putBoolean(isUserImageSelected, true);

            AsyncTask<Void, Void, Bitmap> mResumeTask = new AsyncTask<Void, Void, Bitmap>() {

                @Override
                protected void onPreExecute() {
                    super.onPreExecute();

                    //---Show userName and password editText with values which entered ealier from sharedPreference
                    userName = sharedPreferenceManager.getString(DBConstants.USER_NAME);
                    phoneNumber = sharedPreferenceManager.getString(DBConstants.USER_PHONE_NUMBER);

                    nameET = (EditText) getActivity().findViewById(R.id.nameET);
                    phoneET = (EditText) getActivity().findViewById(R.id.phoneET);

                    nameET.setText(userName);
                    phoneET.setText(phoneNumber);

                    //SHOWING pROGRESSBAR WHILE DATA LOADS
                    progressDialog.show();
                }

                @Override
                protected Bitmap doInBackground(Void... params) {
                    //********************important****************************
                    //Retrieving image fromDB TO SHOW AGAIN TO USER
                    ImageDBManager imageDBManager = new ImageDBManager(getActivity());
                    imageBitMap = imageDBManager.getBitmap();
                    return imageBitMap;
                }

                @Override
                protected void onPostExecute(Bitmap imageInBitMap) {

                    //MAKINH IMAGEVIEW TO BE SET WITH IMAGE FROM DB
                    setUserNameAndImage(imageInBitMap);

                }
            };

            mResumeTask.execute();
        }

    }


    @Override
    public void onClick(View v) {

        Message.message(getActivity(), "Image Tapped");
        // 1. on click call ACTION_GET_CONTENT intent
        //Intent intent = new Intent(Intent.ACTION_PICK);
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        // 2. pick image only
        intent.setType("image/*");
        // 3. start activity
        startActivityForResult(intent, SELECT_PHOTO);

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent imageReturnedIntent) {
        super.onActivityResult(requestCode, resultCode, imageReturnedIntent);

        userImageTextView = (TextView) getActivity().findViewById(R.id.userImageTextView);
        userImageView = (ImageView) getActivity().findViewById(R.id.userImageView);

        //making it final so that it can be used inside thread
        final Intent imageReturnedIntentForThread = imageReturnedIntent;

        switch (requestCode) {
            case SELECT_PHOTO:
                if (resultCode == Activity.RESULT_OK) {


                    AsyncTask<Void, Void, Bitmap> mRegisterTask = new AsyncTask<Void, Void, Bitmap>() {

                        @Override
                        protected void onPreExecute() {
                            super.onPreExecute();
                            //MAKING Progressbar visible for the time being
                            progressDialog.show();
                        }

                        @Override
                        protected Bitmap doInBackground(Void... params) {
                            Uri selectedImage = imageReturnedIntentForThread.getData();
                            Log.d("selectedImage(URI)", "" + selectedImage);
                            InputStream imageStream = null;
                            try {
                                imageStream = getActivity().getContentResolver().openInputStream(selectedImage);
                            } catch (FileNotFoundException e) {
                                e.printStackTrace();
                            }
                            yourSelectedImage = BitmapFactory.decodeStream(imageStream);
                            //imagePath = selectedImage.getPath();
                            imagePath = getRealPathFromUri(getActivity(), selectedImage);
                            Log.d("imagePath (AAKASH SINGH(API>=19) : )", "" + imagePath);

                            if (yourSelectedImage != null) {
                                //****************Important*******************************************
                                //making flag true sothat if the image is already selected and picker is opened once
                                //again and simply closed without choosing any image then the previous image should retain.
                                isImageSelectedFlag = true;
                                //making imageSelect flag as FALse to let RegisterActvity know in case of NO IMAGE SELECTED BY USER
                                sharedPreferenceManager.putBoolean(isUserImageSelected, true);
                                //Calling method to store Image for user
                                imageDBManager.storeUserImageIntoDB(yourSelectedImage, imagePath);
                            }

                            return yourSelectedImage;

                        }

                        @Override
                        protected void onPostExecute(Bitmap image) {

                            //CALL METHOD TO SET BITMMAP TO IMAGE VIEW
                            setUserNameAndImage(image);
                            //showing path
                            toastManager.createToast_Simple(imagePath);
                            //sending image to upload image onto --CLOUD-I-NARY-- using image path.
                        }
                    };
                    mRegisterTask.execute();

                } else if (isImageSelectedFlag == false) {
                    //Setting userImageview and userImageTextView
                    userImageView.setImageResource(R.drawable.user_imageview_thumbnail);
                    userImageTextView.setText("Select profile picture");
                }
        }

    }

    public void setUserNameAndImage(Bitmap image) {
        userImageView.setImageBitmap(image);
        userImageTextView.setVisibility(TextView.GONE);
        progressDialog.dismiss();
    }

    public static String getRealPathFromUri(Context context, Uri contentUri) {
        Cursor cursor = null;
        try {
            String[] proj = {MediaStore.Images.Media.DATA};
            Log.d("contentUri(AAKASH SINGH)", "" + contentUri);

            cursor = context.getContentResolver().query(contentUri, proj, null, null, null);
            Log.d("cursor(AAKASH SINGH)", "" + cursor);

            cursor.moveToFirst();
            int column_index = cursor.getColumnIndex(MediaStore.Images.Media.DATA);
            Log.d("column_index(AAKASH SINGH)", "" + column_index);

            cursor.moveToFirst();
            Log.d("uri(AAKASH SINGH)", "" + cursor.getString(column_index));
            return cursor.getString(column_index);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }

}
