/**
 * Copyright 2015 Google Inc. All Rights Reserved.
 * <p/>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p/>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p/>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.app.splitCash.gcmRegister;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.app.splitCash.R;
import com.app.splitCash.constants.DBConstants;
import com.app.splitCash.constants.ServerConstants;
import com.app.splitCash.downFromServer.DownFromServer;
import com.app.splitCash.postToServer.PostToServer;
import com.app.splitCash.root.HomepageActivityNew;
import com.app.splitCash.root.RegisterActivity;
import com.app.splitCash.sharedPreferences.SharedPreferenceManager;
import com.app.splitCash.toast.ToastManager;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;

import android.support.design.widget.Snackbar;

public class MainActivity extends AppCompatActivity {

    private String dummyVar = null;
    //    private String dummyVar2 = null;
    private static final String TAG = "MainActivity";
    private static final String isRetryActivityRequired = "isRetryActivityRequired";

    Toolbar toolbar;
    private Button startNewActivityButton, retryButton;

    // variables for Google play Services
    private static final int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;

    // vars for Broadcast Receiver and ProgressBAR
    private BroadcastReceiver mRegistrationBroadcastReceiver;
    private ProgressBar mRegistrationProgressBar;

    // TextView vars for name and phoneNumber
    private TextView userNameTextView;
    private TextView phoneNumberTextView;
    private TextView developInfoTextView;

    private FrameLayout processFrameLayout;
    private TextView processTV;

    boolean result = false;
    static String token;
    SharedPreferenceManager sharedPreferenceManager;

    private String userName;
    private String phoneNumber;
    private String gcmId;

    private ToastManager toastManager;
    private ImageView userImageView;
    private ProgressDialog progressDialog;
    private FrameLayout mainFrameLayout;

    private Context mContext;

    public MainActivity() {
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mContext = getApplicationContext();

        // ------------Setting Toolbar-------------------
        toolbar = (Toolbar) findViewById(R.id.app_start_bar);
        setSupportActionBar(toolbar);
        // -----------------------------------------------

        //Intanciating ProgressDialog
        progressDialog = new ProgressDialog(this);
        //Intanciating sharedPreferenceManager
        sharedPreferenceManager = new SharedPreferenceManager(this);

        //Fetching PhoneNumber for Setting as PUBLI_ID for cloudInary image Registration
        phoneNumber = sharedPreferenceManager.getString(DBConstants.USER_PHONE_NUMBER);


        //broadcast for userIamge from server
        LocalBroadcastManager.getInstance(this).registerReceiver(
                mUserImageBroadcastReceiver,
                new IntentFilter("userImage"));

        // ******************************************************************************************************
        userNameTextView = (TextView) findViewById(R.id.userNameTextView);
        phoneNumberTextView = (TextView) findViewById(R.id.phoneNumberTextView);
        startNewActivityButton = (Button) findViewById(R.id.continueButton);
        retryButton = (Button) findViewById(R.id.retryButton);
        userImageView = (ImageView) findViewById(R.id.userImageView);
        mainFrameLayout = (FrameLayout)findViewById(R.id.mainFrameLayout);
        // ******************************************************************************************************


        //Checking Connection and play services
        // ******************************************************************************************************
        if (checkPlayServices() && isNetworkAvailable()) {
            Log.e("checkPlayServices() = ", "True");

            //Send Userimage to CloudInary Server for URL generation
            //Now receiving user image
            final Context mContextFinal = mContext;
            AsyncTask<Void, Void, String> mUserImageURLgeneration = new AsyncTask<Void, Void, String>() {

                @Override
                protected void onPreExecute() {
                    super.onPreExecute();
                    progressDialog.setMessage("Generating Image URL...");
                    progressDialog.show();
                }

                @Override
                protected String doInBackground(Void... params) {
                    String userImageURL = null;
                    PostToServer postToServer = new PostToServer();
                    userImageURL = postToServer.registerUserImageOnCloudInary(mContextFinal, phoneNumber);
                    if (userImageURL != null) {
                        Log.d("userImageURL is ", userImageURL);
                        return userImageURL;
                    } else {
                        Log.d("userImageURL is ", userImageURL);
                        return null;
                    }

                }

                @Override
                protected void onPostExecute(String userImageURL) {
                    if (userImageURL != null) {
                        toastManager = new ToastManager(getApplicationContext());
                        toastManager.createToast_Simple("Image URL generated");
                        Log.d("onPostExecute", "Image URL generated");
                        progressDialog.dismiss();

                        // Start IntentService to register this application with GCM.
                        Intent intent = new Intent(mContext, RegistrationIntentService.class);
                        intent.putExtra(ServerConstants.USER_IMAGE_URL, userImageURL);
                        // Starting IntentService
                        Log.e("RegistrationIntentService = ", "True");

                        //Changing progressDialog Text
                        progressDialog.setMessage("Sending data to Google Server...");
                        progressDialog.show();
                        //***************************
                        startService(intent);

                    } else {
                        toastManager.createToast_Simple("Image URL Generation FAILED!!!");
                    }
                }
            };

            mUserImageURLgeneration.execute();
        } else {
            //Showing SnackBar*****************************************************************************
            View parentLayout = findViewById(R.id.main_layout);
            Snackbar.make(parentLayout, "       Check your Internet Connection", Snackbar.LENGTH_LONG)
                    .setAction("", new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            //write code if "CLOSE" is clicked on SnackBar
                        }
                    })
                    .setActionTextColor(getResources().getColor(android.R.color.holo_red_light))
                    .show();

            //SHOWING tOAST
            toastManager = new ToastManager(getApplicationContext());
            toastManager.createToast_Simple("No Connection Available");
            //CHANGING TEXT FOR no internet connection
            userNameTextView.setText(getString(R.string.token_error_message));
            phoneNumberTextView.setVisibility(TextView.GONE);
            //Enabling retryButton
            retryButton.setVisibility(Button.VISIBLE);
            //store flag in SharedPreference for identifying that NO NETWORK CONDITION DETECTED
            sharedPreferenceManager.putBoolean(isRetryActivityRequired, true);
        }

        // ******************************************************************************************************


        //*******************************************************************************
        //Now on receive of BROADCAST of token update UI
        mRegistrationBroadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                Log.e("mRegistrationBroadcastReceiver", "onReceive");

                boolean sentToken = sharedPreferenceManager
                        .getBoolean(QuickstartPreferences.SENT_TOKEN_TO_SERVER);
                Log.e("mRegistrationBroadcastReceiver TOKEN", "sentToken :" + sentToken);

                if (sentToken) {
                    //update UI
                    updateUIonSucessfulRegistration();
                } else {
                    //Showing SnackBar*****************************************************************************
                    View parentLayout = findViewById(R.id.main_layout);
                    Snackbar.make(parentLayout, "       Check your Internet Connection", Snackbar.LENGTH_LONG)
                            .setAction("", new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    //write code if "CLOSE" is clicked on SnackBar
                                }
                            })
                            .setActionTextColor(getResources().getColor(android.R.color.holo_red_light))
                            .show();

                    //SHOWING tOAST
                    toastManager = new ToastManager(getApplicationContext());
                    toastManager.createToast_Simple("No Connection Available");
                    //CHANGING TEXT FOR no internet connection
                    userNameTextView.setText(getString(R.string.token_error_message));
                    phoneNumberTextView.setVisibility(TextView.GONE);
                    //Enabling retryButton
                    retryButton.setVisibility(Button.VISIBLE);
                    //store flag in SharedPreference for identifying that NO NETWORK CONDITION DETECTED
                    sharedPreferenceManager.putBoolean(isRetryActivityRequired, true);
                }
            }
        };

        // ******************************************************************************************************


    }


    public void updateUIonSucessfulRegistration() {
        //MAKING FrameLayout visible so that userTextview/phoneTextView become visible
        mainFrameLayout.setVisibility( FrameLayout.VISIBLE);
        //hiding progressDialog;
        progressDialog.dismiss();

        userName = sharedPreferenceManager.getString(DBConstants.USER_NAME);
        phoneNumber = sharedPreferenceManager.getString(DBConstants.USER_PHONE_NUMBER);
        gcmId = token;

        //Hiding progressbar and other things
        progressDialog.dismiss();

        // userNameTextView.setTextColor(Color.parseColor("#ffffff"));
        userNameTextView.setText(Html
                .fromHtml("Welcome to WidUapp <b>" + userName
                        + "</b>"));
        // phoneNumberTextView.setTextColor(Color.parseColor("#ffffff"));
        phoneNumberTextView.setText(Html
                .fromHtml("\nYour registered PhoneNumber is <b>"
                        + phoneNumber + "</b>"));

        //making resgistration flag true so that splash activity can start with homePage
        //Activity next time when app starts
        sharedPreferenceManager.putBoolean("hasRegistered", true);

        // Making Button Visible
        startNewActivityButton.setVisibility(Button.VISIBLE);

        //SHOWING tOAST
        toastManager = new ToastManager(getApplicationContext());
        toastManager.createToast_Simple("      Congratulations!!!\n\nRegistration Successful");
    }

    private BroadcastReceiver mUserImageBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.d("onReceive(Context context) : ", "Inside Broadcast Receiver");
            Bundle extras = intent.getExtras();
            final String userImageURL = extras.getString(ServerConstants.USER_IMAGE_URL);
            Log.d("userImageURL", "" + userImageURL);

            //editing URL for Circular Image
            String first_part_userImageURL = userImageURL.substring(0,49);
            String second_userImageURL = userImageURL.substring(49);
            final String CIRCULAR_CROP_IMAGE_userImageURL = "w_250,h_250,r_max/";

            //merging all forms of URL
            final String finalMerged_userImageURL = first_part_userImageURL + CIRCULAR_CROP_IMAGE_userImageURL + second_userImageURL;


            //Receiving Image fromURL
            if (finalMerged_userImageURL != null) {
                AsyncTask<Void, Void, Bitmap> mUserImageRetrival = new AsyncTask<Void, Void, Bitmap>() {

                    @Override
                    protected void onPreExecute() {
                        super.onPreExecute();
                        progressDialog.setMessage("Updating your profile picture...");
                        progressDialog.show();
                    }

                    @Override
                    protected Bitmap doInBackground(Void... params) {
                        DownFromServer downFromServer = new DownFromServer(getApplicationContext());
                        final Bitmap bitmap = downFromServer.receiveImageFromServer(finalMerged_userImageURL);
                        Log.d("Bitmap", "" + bitmap);
                        return bitmap;
                    }

                    @Override
                    protected void onPostExecute(Bitmap bitmap) {
                        if (bitmap != null) {
                            Log.d("Recvd bitmap is ", "" + bitmap);
                            progressDialog.dismiss();
                            sendNotificationOnUserImageReceived(bitmap);
                            userImageView.setVisibility(View.VISIBLE);
                            userImageView.setImageBitmap(bitmap);

                        } else {
                            Log.d("Recvd bitmap is ", "" + bitmap);
                        }

                    }
                };

                mUserImageRetrival.execute();


            }
        }
    };


    public void retryActivity(View view) {
        startActivity(new Intent(this, RegisterActivity.class));
        MainActivity.this.finish();
    }

    public void startNewActivity(View view) {
        startActivity(new Intent(this, HomepageActivityNew.class));
        MainActivity.this.finish();
    }

    @Override
    protected void onResume() {
        super.onResume();
        //Registering receivers for succesful/unsuccessful token generation events
        LocalBroadcastManager.getInstance(this).registerReceiver(
                mRegistrationBroadcastReceiver,
                new IntentFilter(QuickstartPreferences.REGISTRATION_COMPLETE));

        LocalBroadcastManager.getInstance(this).registerReceiver(
                mRegistrationBroadcastReceiver,
                new IntentFilter(QuickstartPreferences.REGISTRATION_FAILED));

        //broadcast for userIamge from server
        LocalBroadcastManager.getInstance(this).registerReceiver(
                mUserImageBroadcastReceiver,
                new IntentFilter("userImage"));


    }

    @Override
    protected void onPause() {
        super.onPause();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(
                mRegistrationBroadcastReceiver);

        //broadcast for userIamge from server
        LocalBroadcastManager.getInstance(this).registerReceiver(
                mUserImageBroadcastReceiver,
                new IntentFilter("userImage"));

    }

    private boolean checkPlayServices() {
        int resultCode = GooglePlayServicesUtil
                .isGooglePlayServicesAvailable(this);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (GooglePlayServicesUtil.isUserRecoverableError(resultCode)) {
                GooglePlayServicesUtil.getErrorDialog(resultCode, this,
                        PLAY_SERVICES_RESOLUTION_REQUEST).show();
            } else {
                Log.i(TAG, "This device is not supported.");
                finish();
            }
            return false;
        }
        return true;
    }


    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    private void sendNotificationOnUserImageReceived(Bitmap userBitmap) {
        Log.e("sendNotificationOnUserImageReceived", "Called");

//        ImageConverterManager imageConverterManager = new ImageConverterManager(getApplicationContext());
//        userBitmap = imageConverterManager.getCircleBitmap(userBitmap);

        Intent intent = new Intent(mContext, DownFromServer.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(), 0,
                intent, PendingIntent.FLAG_ONE_SHOT);
        // Custom Notification
        Uri customSoundUri = Uri.parse("android.resource://"
                + "com.app.splitCash" + "/" + R.raw.notification);

        // Uri defaultSoundUri = RingtoneManager
        // .getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(
                getApplicationContext());

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            notificationBuilder.setLargeIcon(userBitmap)
                    .setContentTitle(userName)
                    .setContentText("Your image received")
                    .setAutoCancel(true).setSound(customSoundUri)
                    .setContentIntent(pendingIntent)
                    .setDefaults(Notification.DEFAULT_VIBRATE);//vibration

        } else {
            notificationBuilder
                    .setSmallIcon(R.drawable.ic_launcher_small)
                    .setLargeIcon(userBitmap)
                    .setContentTitle(userName)
                    .setContentText("Your image received")
                    .setAutoCancel(true)
                    .setSound(customSoundUri)
                    .setContentIntent(pendingIntent)
                    .setDefaults(Notification.DEFAULT_VIBRATE)//vibration
                    .setColor(getApplicationContext().getResources().getColor(R.color.black));
        }

        NotificationManager notificationManager = (NotificationManager) getApplicationContext()
                .getSystemService(Context.NOTIFICATION_SERVICE);

        notificationManager.notify(0 /* ID of notification */,
                notificationBuilder.build());

        return;
    }


}