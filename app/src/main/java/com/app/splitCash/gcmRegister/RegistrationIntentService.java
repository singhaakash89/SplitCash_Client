package com.app.splitCash.gcmRegister;

import android.app.IntentService;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.text.Html;
import android.util.Log;

import com.app.splitCash.R;
import com.app.splitCash.constants.DBConstants;
import com.app.splitCash.constants.ServerConstants;
import com.app.splitCash.dataBase.UserDBAdapter;
import com.app.splitCash.postToServer.PostToServer;
import com.app.splitCash.serverUtil.AppServer;
import com.app.splitCash.sharedPreferences.SharedPreferenceManager;
import com.app.splitCash.toast.ToastManager;
import com.app.splitCash.user.UserBean;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.google.android.gms.iid.InstanceID;

public class RegistrationIntentService extends IntentService {

    //boolean result = false;
    //static boolean postResult = false;
    private static final String TAG = "RegIntentService";
    static String token;

    // Fetching username from MainActivity
    private String userName;
    private String phoneNumber;
    private String userImageURL;

    private boolean isRegistraionSuccessful = false;
    private SharedPreferenceManager sharedPreferenceManager;

    public RegistrationIntentService() {
        super(TAG);

    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Log.e("Inside RegistrationIntentService()", "True");

        //Fetching userImageURL from intent
        userImageURL = intent.getStringExtra(ServerConstants.USER_IMAGE_URL);

        //Creating instance for sharedPreference
        sharedPreferenceManager = new SharedPreferenceManager(getApplicationContext());

        try {
            // In the (unlikely) event that multiple refresh operations occur
            // simultaneously,
            // ensure that they are processed sequentially.
            synchronized (getApplicationContext()) {
                Log.e("Inside Synchronized : ", "Inside Sync");

                // [START register_for_gcm]
                // Initially this call goes out to the network to retrieve the
                // token, subsequent calls
                // are local.
                // [START get_token]

                // ----defaultCode---------------------------
                InstanceID instanceID = InstanceID
                        .getInstance(getApplicationContext());
                token = instanceID.getToken(
                        getString(R.string.gcm_defaultSenderId),
                        GoogleCloudMessaging.INSTANCE_ID_SCOPE, null);

                Log.e("GeneratedToken = ", token);

                // ******Storing GCM id to SharedPreference*************************
                sharedPreferenceManager.putString(DBConstants.USER_GCM_ID, token);
                // ********************************************************************

                // ***************Fetching userName nd phoneNumber from sharedPrefernce*************************
                userName = sharedPreferenceManager.getString(DBConstants.USER_NAME);
                phoneNumber = sharedPreferenceManager.getString(DBConstants.USER_PHONE_NUMBER);
                Log.e(DBConstants.USER_NAME + " =", userName);
                Log.e(DBConstants.USER_PHONE_NUMBER + " = ", phoneNumber);
                Log.e(DBConstants.USER_GCM_ID + " = ", token);
                Log.e(ServerConstants.USER_IMAGE_URL + " = ", userImageURL);
                // ************************************************************************************


                //************Store USERNAME/GCM_ID/PHONE_NUMBER INTO DB
                UserDBAdapter userDBAdapter = new UserDBAdapter(getApplicationContext());
                UserBean userBean = new UserBean();
                userBean.setRegId(token);
                userBean.setUserName(userName);
                userBean.setPhoneNumber(phoneNumber);
                ContentValues cv = userBean.contentValues();
                userDBAdapter.updateUserTable(cv);

                //setting flag as true
                isRegistraionSuccessful = true;

                //Send Registration Details with  userImageURL to server now
                sendRegistrationToServer(userImageURL);
            }

        } catch (Exception e) {

            Log.e(TAG, "Failed to complete token refresh", e);
            // If an exception happens while fetching the new token or updating
            // our registration data
            // on a third-party server, this ensures that we'll attempt the
            // update at a later time.
        } finally {
            if (isRegistraionSuccessful == false) {
                Log.e("finally : ", "isRegistraionSuccessful : " + isRegistraionSuccessful);

                sharedPreferenceManager.putBoolean(
                        QuickstartPreferences.SENT_TOKEN_TO_SERVER, false);

                // Notify UI that registration has completed, so the
                // progress indicator can be hidden.
                Intent registrationFailed = new Intent(
                        QuickstartPreferences.REGISTRATION_FAILED);
                LocalBroadcastManager.getInstance(getApplicationContext())
                        .sendBroadcast(registrationFailed);

            }
        }
    }


    public void sendRegistrationToServer(final String userImageURL) {

        final ToastManager toastManager = new ToastManager(getApplicationContext());
        final AppServer appServer = new AppServer(getApplicationContext());
        final ProgressDialog progressDialog = new ProgressDialog(getApplicationContext());
        // Posting request For registration
        // Using Async Task For Posting And Registration
        AsyncTask<Void, Void, Boolean> mRegisterTask = new AsyncTask<Void, Void, Boolean>() {

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                //toastManager.createToast_Simple(getApplicationContext(), "Now Registering with Google Server");
                Log.d("onPreExecute", "Now Registering with Google Server");
            }

            @Override
            protected Boolean doInBackground(Void... params) {
                boolean postResult = false;
                postResult = appServer.register(userImageURL);
                Log.e("AKS", "from doInBackground()");
                Log.e("postResult = ", "" + postResult);
                return postResult;
            }

            @Override
            protected void onPostExecute(Boolean result) {
                if (result) {
                    //Invoke method to retrieve URL from GoogleAppEngine to download image
                    Log.d("phoneNumber_IMPORTANT_CASE(INSIDE ON_POSTEXECUTE)", phoneNumber);
                    receiveUserImageURL(phoneNumber);
                } else {
                    Log.d("onPostExecute", "Registeration UnSuccessful(From Async)");
                }

            }

        };

        mRegisterTask.execute();

    }

    public void receiveUserImageURL(final String phoneNumber) {
        AsyncTask<Void, Void, Boolean> mreceiveUserImageURL = new AsyncTask<Void, Void, Boolean>() {

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                Log.d("onPreExecute", "mreceiveUserImageURL");
            }

            @Override
            protected Boolean doInBackground(Void... params) {
                final PostToServer postToServer = new PostToServer();
                boolean resultNew = postToServer.receiveUserImageURL(phoneNumber);
                if (resultNew) {
                    Log.d("onPostExecute", "Image retrieval successful");
                } else {
                    Log.d("onPostExecute", "Image retrieval failed");
                }

                return resultNew;
            }


            @Override
            protected void onPostExecute(Boolean result) {
                if (result) {
                    //updating UI
                    updateUI(result);
                } else {
                    Log.d("onPostExecute", "Image Retrival UnSuccessful(From Async)");
                }

            }

        };

        mreceiveUserImageURL.execute();


        //Invoking sendUserImageURL on Google app engine


    }

    public void updateUI(boolean isRegistraionSuccessful) {

        //***Updating UI*********************************
        if (isRegistraionSuccessful) {
            // sETTING ProgressBar as Invisible
            sharedPreferenceManager.putBoolean(
                    QuickstartPreferences.SENT_TOKEN_TO_SERVER, true);

            // Notify UI that registration has completed, so the
            // progress indicator can be hidden.
            Intent registrationComplete = new Intent(
                    QuickstartPreferences.REGISTRATION_COMPLETE);
            LocalBroadcastManager.getInstance(getApplicationContext())
                    .sendBroadcast(registrationComplete);

            // Fetching userName value from the intent
            userName = sharedPreferenceManager.getString(DBConstants.USER_NAME);
            Log.e("userName(PostResult_tRUE)", userName);

            // Sending Notification of Registration to Device
            sendNotification(userName);
            // ************************************************************
            Log.e("updateUI() = ", "successful");
        }
    }

    private void sendNotification(String userName) {
        Intent intentNew = new Intent(this, MainActivity.class);
        intentNew.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0,
                intentNew, PendingIntent.FLAG_UPDATE_CURRENT);

        //Custom Notification
        Uri customSoundUri = Uri.parse("android.resource://" + "com.app.splitCash" + "/" + R.raw.notification);


//		Uri defaultSoundUri = RingtoneManager
//				.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);


        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(getApplicationContext());


        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            notificationBuilder.setSmallIcon(R.drawable.ic_launcher)
                    .setContentTitle(Html.fromHtml("<b>WidUapp</b>"))
                    .setContentText(Html.fromHtml("Welcome on WidUapp\n\n" + "<b>" + userName + "</b>"))
                    .setAutoCancel(true).setSound(customSoundUri)
                    .setContentIntent(pendingIntent);

        } else {

            Resources res = getApplicationContext().getResources();
            notificationBuilder.setSmallIcon(R.drawable.ic_launcher_small)
                    .setLargeIcon(BitmapFactory.decodeResource(res, R.drawable.ic_launcher))
                    .setContentTitle(Html.fromHtml("<b>WidUapp</b>"))
                    .setContentText(Html.fromHtml("Welcome on WidUapp\n\n" + "<b>" + userName + "</b>"))
                    .setAutoCancel(true).setSound(customSoundUri)
                    .setContentIntent(pendingIntent)
                    .setColor(getApplicationContext().getResources().getColor(R.color.primary));
        }


        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        notificationManager.notify(0 /* ID of notification */,
                notificationBuilder.build());
    }

}