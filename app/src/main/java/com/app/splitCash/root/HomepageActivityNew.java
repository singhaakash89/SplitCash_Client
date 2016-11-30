package com.app.splitCash.root;

import android.app.Activity;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.BroadcastReceiver;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.ContactsContract;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.app.splitCash.R;
import com.app.splitCash.chat.ConversationActivity;
import com.app.splitCash.chat.StartChatManager;
import com.app.splitCash.constants.DBConstants;
import com.app.splitCash.fragments.ChatListActivityFragment;
import com.app.splitCash.fragments.HomePageActivityFragment;
import com.app.splitCash.group.CreateUnKnownExpenseGroup;
import com.app.splitCash.navDrawer.NavigationDrawerFragment;
import com.app.splitCash.sharedPreferences.SharedPreferenceManager;
import com.app.splitCash.toast.ToastManager;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created by Aakash Singh on 24-05-2016.
 */
public class HomepageActivityNew extends AppCompatActivity implements View.OnClickListener {

    private Toolbar toolbar;
    public static boolean menuFlag = false;
    private int backPressCount = 0;
    private String userName;
    private FragmentManager fragmentManager;
    private TextView fragmentNameForAppBar;
    private ImageView fragmentImageForAppBar;
    private ChatListActivityFragment chatListActivityFragment;
    private HomePageActivityFragment homePageActivityFragment;
    private static final String TAG = HomepageActivityNew.class.getSimpleName();
    private SharedPreferenceManager sharedPreferenceManager;
    private static final int REQUEST_CODE = 1;
    private byte[] displayPhoto;
    private ToastManager toastManager;
    private StartChatManager startChatManager;
    private Uri uriContact;
    private String contactID;     // contacts unique ID
    private ProgressBar progressBar;
    private FragmentTransaction fragmentTransaction;


    public HomepageActivityNew() {

    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_page_new);

        //---------ToolBar Implementation-------------
        toolbar = (Toolbar) findViewById(R.id.app_bar);
//        toolbar.setLogo(R.drawable.ic_logo);
        setSupportActionBar(toolbar);
        //--------------------------------------------

        //----HomeButtonEnabled-------------------------
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        //---------------------------------------------


        //Initializing
        //Empty Constructor Needed
        sharedPreferenceManager = new SharedPreferenceManager(this);
        //Intializing Fragment Manager
        fragmentManager = getFragmentManager();

        toastManager = new ToastManager(this);

        startChatManager = new StartChatManager(this);

        chatListActivityFragment = new ChatListActivityFragment();

        homePageActivityFragment = new HomePageActivityFragment();
//***************************************************************************************

        //Fetching userNamw for app quiting purpose
        userName = sharedPreferenceManager.getString(DBConstants.USER_NAME);

        //Appbar Reformatting requirment when new fragment slides in
        fragmentNameForAppBar = (TextView) findViewById(R.id.fragmentNameForAppBar);
        fragmentImageForAppBar = (ImageView) findViewById(R.id.fragmentImageForAppBar);

        //ProgressBar
        progressBar = (ProgressBar) findViewById(R.id.progressBarID);
        progressBar.setVisibility(ProgressBar.GONE);


        //--------------Navigation Drawer Implementation------------------
        NavigationDrawerFragment drawerFragment = (NavigationDrawerFragment) getFragmentManager().findFragmentById(R.id.fragment_navigation_drawer);

        drawerFragment.setUp(R.id.fragment_navigation_drawer, (DrawerLayout) findViewById(R.id.drawer_layout), toolbar, fragmentImageForAppBar);
        //----------------------------------------------------------------


        //registerLoaclBroadCastReceiver for inserting fragment from NAVIGATion drawer
        registerLocalBroadCastReceiver();

        //iNSERT HOMEPAGE FRAGMENT WHEN APP STARTS
        addHomepageFragmentToActivity();


    }

    private void registerLocalBroadCastReceiver() {

        //Chat Receiver
        LocalBroadcastManager.getInstance(this).registerReceiver(mMessageReceiver_chat,
                new IntentFilter("broadcast_ChatIntentFilter"));

        //HomePage Receiver
        LocalBroadcastManager.getInstance(this).registerReceiver(mMessageReceiver_homePage,
                new IntentFilter("broadcast_HomePageIntentFilter"));

    }


    //Settingup Receiver body
    //BroadcastReceivers
    //chat BC-Receiver
    private BroadcastReceiver mMessageReceiver_chat = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            addChatLlistFragmentToActivity();
            Log.d("receiver", "Got message: chat BC-Receiver");
        }
    };

    //HOMEpAGE BC-Receiver
    private BroadcastReceiver mMessageReceiver_homePage = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            addHomepageFragmentToActivity();
            Log.d("receiver", "Got message: HOMEPage BC-Receiver");
        }
    };


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_home_page, menu);

        if (menuFlag == true) {
            //Clearing menu
            menu.clear();

        }
        return true;

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();
        if (itemId == R.id.settings) {
            Toast.makeText(getBaseContext(), R.string.toast_message_setting,
                    Toast.LENGTH_SHORT).show();
            startActivity(new Intent(this, SettingsActivity.class));
            return false;
        } else if (itemId == R.id.contactus) {
            Toast.makeText(getBaseContext(), R.string.toast_message_contactus,
                    Toast.LENGTH_SHORT).show();
            return false;
        } else if (itemId == R.id.about) {
            Toast.makeText(getBaseContext(), R.string.toast_message_about, Toast.LENGTH_SHORT).show();
            return false;
        } else {
            return super.onOptionsItemSelected(item);
        }

    }


    @Override
    public void onBackPressed() {

        //Below code is for 2 second Pause for Exit Using HANDLER
        backPressCount++;
        if (backPressCount == 1) {
            toastManager.createToast_Simple("Press again to Exit");
        } else if (backPressCount == 2) {
            HomepageActivityNew.this.finish();
        }

        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                backPressCount = 0;
            }
        }, 2000);


//        //Dialog box Implementation
//        new AlertDialog.Builder(this)
//                .setTitle(Html.fromHtml("Bye <b>" + userName + "</b>"))
//                .setMessage("Quit WidUapp?")
//                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
//                    public void onClick(DialogInterface dialog, int which) {
//                        HomepageActivityNew.this.finish();
//                    }
//                })
//                .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
//                    public void onClick(DialogInterface dialog, int which) {
//
//                    }
//                })
//                .setIcon(android.R.drawable.ic_dialog_alert)
//                .show();


    }

    @Override
    protected void onResume() {
        super.onResume();

        if (chatListActivityFragment.isAdded() && chatListActivityFragment != null) {
            chatListActivityFragment = new ChatListActivityFragment();
            fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.mainContentFrameLayout, chatListActivityFragment, "chatListActivityFragment");
            //***************IMPORTANT***********************
            //when adding or performing the FragmentTransaction BELOW was causing the Exception.
            //fragmentTransaction.commit();
            //TO AVOID EXCEPTION
            fragmentTransaction.commitAllowingStateLoss();

            //Changing appbAR tEXT/IMAGE
            fragmentNameForAppBar.setText("WidUapp Chats");
            fragmentImageForAppBar.setImageResource(R.drawable.ic_chat);

            //Making progressbar visible
            progressBar.setVisibility(ProgressBar.GONE);

        } else if (homePageActivityFragment.isAdded() && homePageActivityFragment != null) {
            homePageActivityFragment = new HomePageActivityFragment();
            fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.mainContentFrameLayout, homePageActivityFragment, "homePageActivityFragment");
            //***************IMPORTANT***********************
            //when adding or performing the FragmentTransaction BELOW was causing the Exception.
            //fragmentTransaction.commit();
            //TO AVOID EXCEPTION
            fragmentTransaction.commitAllowingStateLoss();


            //Changing appbAR tEXT/IMAGE
            fragmentNameForAppBar.setText("WidUapp Groups");
            fragmentImageForAppBar.setImageResource(R.drawable.ic_add_group);

            //Making progressbar visible
            progressBar.setVisibility(ProgressBar.GONE);

        }

        fragmentImageForAppBar.setOnClickListener(this);
    }


    //Snippet for adding the chatListFragment to the activity
    public void addChatLlistFragmentToActivity() {
        chatListActivityFragment = new ChatListActivityFragment();
        fragmentTransaction = fragmentManager.beginTransaction();

        //sHWOING PROGRESSBAR
        progressBar.setVisibility(ProgressBar.VISIBLE);

        //Delaying Fragment insertion so that drawer can close properly
        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                fragmentTransaction.replace(R.id.mainContentFrameLayout, chatListActivityFragment, "chatListActivityFragment");
                //***************IMPORTANT***********************
                //when adding or performing the FragmentTransaction BELOW was causing the Exception.
                //fragmentTransaction.commit();
                //TO AVOID EXCEPTION
                fragmentTransaction.commitAllowingStateLoss();

                progressBar.setVisibility(ProgressBar.GONE);
            }
        }, 500);

        //Changing appbAR tEXT/IMAGE
        fragmentNameForAppBar.setText("WidUapp Chats");
        fragmentImageForAppBar.setImageResource(R.drawable.ic_chat);

        fragmentImageForAppBar.setOnClickListener(this);

    }

    //Snippet for adding the HomePAGEFragment to the activity
    public void addHomepageFragmentToActivity() {
        homePageActivityFragment = new HomePageActivityFragment();
        fragmentTransaction = fragmentManager.beginTransaction();

        //sHWOING PROGRESSBAR
        progressBar.setVisibility(ProgressBar.VISIBLE);

        //Delaying Fragment insertion so that drawer can close properly
        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                fragmentTransaction.replace(R.id.mainContentFrameLayout, homePageActivityFragment, "homePageActivityFragment");
                //***************IMPORTANT***********************
                //when adding or performing the FragmentTransaction BELOW was causing the Exception.
                //fragmentTransaction.commit();
                //TO AVOID EXCEPTION
                fragmentTransaction.commitAllowingStateLoss();

                progressBar.setVisibility(ProgressBar.GONE);
            }
        }, 500);


        //Changing appbAR tEXT/IMAGE
        fragmentNameForAppBar.setText("WidUapp Groups");
        fragmentImageForAppBar.setImageResource(R.drawable.ic_add_group);

        fragmentImageForAppBar.setOnClickListener(this);
    }

    //Snippet for adding onClick functionality to the appBar icon which decides the whether to open
    //chatStartActivity or CreateGroupActivity
    @Override
    public void onClick(View v) {

        String appBarTextName = fragmentNameForAppBar.getText().toString();
        switch (appBarTextName) {
            case "WidUapp Chats":
                startChat();
                break;

            case "WidUapp Groups":
                startActivity(new Intent(this, CreateUnKnownExpenseGroup.class));
                break;


            default:
                break;
        }
    }


    public void startChat() {
        //New Contact picker code
        startActivityForResult(new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI), REQUEST_CODE);
        //************************

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            Log.d(TAG, "Response(Aks): " + data.toString());
            uriContact = data.getData();

            String name = retrieveContactName();
            String number = retrieveContactNumber();
            displayPhoto = retrieveContactPhoto();

            //                //Storing username and number in sharedPref for next activity.
            sharedPreferenceManager.putString(DBConstants.TO_CHAT_USER_NAME, name);
            sharedPreferenceManager.putString(DBConstants.TO_CHAT_USER_NUMBER, number);

            Log.d("++AKS++", "++AKS number++ : " + number + " , name : " + name);

            //Starting Chat Now*****************************8888
            if (startChatManager.startChatFromContactPicker(displayPhoto)) {
                startActivity(new Intent(this, ConversationActivity.class));

                //MAKE FLAG TRUE TO MAKE PROGRESSBAR VISIBLE WHEN ACTIVTY REUME FROM

            } else {
                toastManager.createToast_Simple("Contact picker Error!!!");
            }


        }
    }

    private String retrieveContactName() {

        String contactName = null;

        // querying contact data store
        Cursor cursor = getContentResolver().query(uriContact, null, null, null, null);

        if (cursor.moveToFirst()) {

            // DISPLAY_NAME = The display name for the contact.
            // HAS_PHONE_NUMBER =   An indicator of whether this contact has at least one phone number.

            contactName = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
        }

        cursor.close();

        Log.d(TAG, "Contact Name(AKS): " + contactName);

        return contactName;
    }

    private String retrieveContactNumber() {

        String contactNumber = null;

        // getting contacts ID
        Cursor cursorID = getContentResolver().query(uriContact,
                new String[]{ContactsContract.Contacts._ID},
                null, null, null);

        if (cursorID.moveToFirst()) {

            contactID = cursorID.getString(cursorID.getColumnIndex(ContactsContract.Contacts._ID));
        }

        cursorID.close();

        Log.d(TAG, "Contact ID(AKS): " + contactID);


        // Using the contact ID now we will get contact phone number
        Cursor cursorPhone = getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                new String[]{ContactsContract.CommonDataKinds.Phone.NUMBER},

                ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ? AND " +
                        ContactsContract.CommonDataKinds.Phone.TYPE + " = " +
                        ContactsContract.CommonDataKinds.Phone.TYPE_MOBILE,

                new String[]{contactID},
                null);

        if (cursorPhone.moveToFirst()) {
            contactNumber = cursorPhone.getString(cursorPhone.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
        }

        cursorPhone.close();

        Log.d(TAG, "Contact Phone Number(AKS): " + contactNumber);

        return contactNumber;
    }


    private byte[] retrieveContactPhoto() {

        Bitmap bitMapPhoto = null;

        try {
            InputStream inputStream = ContactsContract.Contacts.openContactPhotoInputStream(this.getContentResolver(),
                    ContentUris.withAppendedId(ContactsContract.Contacts.CONTENT_URI, new Long(contactID)));

            if (inputStream != null) {
                bitMapPhoto = BitmapFactory.decodeStream(inputStream);

                //Converting into byte[] for storing in DB
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                bitMapPhoto.compress(Bitmap.CompressFormat.PNG, 100, stream);
                displayPhoto = stream.toByteArray();


                assert inputStream != null;
                inputStream.close();

            } else {
                Drawable d = getResources().getDrawable(R.drawable.ic_chat_list_person);
                bitMapPhoto = ((BitmapDrawable) d).getBitmap();

                //Converting into byte[] for storing in DB
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                bitMapPhoto.compress(Bitmap.CompressFormat.PNG, 100, stream);
                displayPhoto = stream.toByteArray();

            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        return displayPhoto;

    }


}