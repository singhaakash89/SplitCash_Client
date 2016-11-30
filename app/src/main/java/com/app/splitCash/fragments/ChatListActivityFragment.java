package com.app.splitCash.fragments;

import android.app.Activity;
import android.app.Fragment;
import android.content.ContentUris;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.annotation.Nullable;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.app.splitCash.R;
import com.app.splitCash.chat.ConversationActivity;
import com.app.splitCash.chat.ChatBean;
import com.app.splitCash.chat.ChatListCursorAdapter;
import com.app.splitCash.chat.StartChatManager;
import com.app.splitCash.constants.DBConstants;
import com.app.splitCash.sharedPreferences.SharedPreferenceManager;
import com.app.splitCash.toast.ToastManager;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

/**
 * Created by Aakash Singh on 20-05-2016.
 */
public class ChatListActivityFragment extends Fragment implements AdapterView.OnItemClickListener {

    private static final String TAG = ChatListActivityFragment.class.getSimpleName();

    private Toolbar toolbar;
    private ListView listView;
    private TextView emptyChatListTextView;
    private ChatListCursorAdapter chatListCursorAdapter;
    private ViewHolder viewHolder;
    private ArrayList<ChatBean> chatBeanList;
    private String chatName;
    private String chatNumber;
    private SharedPreferenceManager sharedPreferenceManager;
    private static final int REQUEST_CODE = 1;
    private StartChatManager startChatManager;
    private ToastManager toastManager;
    private byte[] displayPhoto;

    private Uri uriContact;
    private String contactID;     // contacts unique ID


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.activity_chat_list_fragment, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);


        //Intializing startChatManager and ToastManger
        startChatManager = new StartChatManager(getActivity());
        toastManager = new ToastManager(getActivity());

        //Initializing SharedPrefernces
        sharedPreferenceManager = new SharedPreferenceManager(getActivity());

        //---------Group List Implementation Using Cursor/Array Adapter---------------------------
        listView = (ListView) getActivity().findViewById(R.id.chatListView);

        Log.e(" ", "Before chatListCursorAdapter");
        chatListCursorAdapter = new ChatListCursorAdapter(getActivity());
        Log.e(" ", "After chatListCursorAdapter");

        //Hiding emptyGroupListTextView if DATA eXIST IN lIST
        emptyChatListTextView = (TextView) getActivity().findViewById(R.id.emptyChatListTextView);
        Cursor isCursor = chatListCursorAdapter.getChatListCursor(getActivity());
        if (isCursor != null && isCursor.getCount() > 0) {
            emptyChatListTextView.setVisibility(TextView.INVISIBLE);
        }


        //Setting Adapter
        listView.setAdapter(chatListCursorAdapter.getAdapter());

//        Adding OnItemclick Listener
        listView.setOnItemClickListener(this);



    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

        Log.d("position(AKS)", " : "+position);

        viewHolder = new ViewHolder();
        chatBeanList = chatListCursorAdapter.getChatList();

        //Fetching Group NAME and Number fROM table using ARRAYlIST OF lISTaDAPTER
        chatName = viewHolder.populateName(chatBeanList.get(position));
        chatNumber = viewHolder.populateNumber(chatBeanList.get(position));

        Log.d("chatName(AKS)", " : "+chatName);
        Log.d("chatNumber(AKS)", " : " + chatNumber);

        //uSING sharedpreference to pass groupname
        DBConstants.groupSharedPrefKey++;
        sharedPreferenceManager.putString(String.valueOf(DBConstants.groupSharedPrefKey), chatNumber);
        //----------------------------------------

        startActivity(new Intent(getActivity(), ConversationActivity.class));
        //Message.message(this, "GroupUnknown Started");


    }

    class ViewHolder {
        public String populateName(ChatBean chatBean) {
            return chatBean.getChatName();
        }

        public String populateNumber(ChatBean chatBean) {
            return chatBean.getChatToPhoneNumber();
        }
    }


    public void startChat() {
        //Implementing Contact Picker
        // do your code

//        Uri uri = Uri.parse("content://contacts");
//        Intent intent = new Intent(Intent.ACTION_PICK, uri);
//        intent.setType(ContactsContract.CommonDataKinds.Phone.CONTENT_TYPE);
//        startActivityForResult(intent, REQUEST_CODE);
//******************************************************************************************************

        //New Contact picker code
        startActivityForResult(new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI), REQUEST_CODE);
        //************************

    }

//    @Override
//    public void onActivityResult(int requestCode, int resultCode,
//                                 Intent intent) {
//        if (requestCode == REQUEST_CODE) {
//            if (resultCode == Activity.RESULT_OK) {
//                Uri uri = intent.getData();
//                String[] projection = {ContactsContract.CommonDataKinds.Phone.NUMBER, ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME};
//
//
//                Cursor cursor = this.getContentResolver().query(uri, projection,
//                        null, null, null);
//                cursor.moveToFirst();
//
//                int numberColumnIndex = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER);
//                String number = cursor.getString(numberColumnIndex);
//
//                int nameColumnIndex = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME);
//                String name = cursor.getString(nameColumnIndex);
//
//                //Storing username and number in sharedPref for next activity.
//                sharedPreferenceManager.putString(DBConstants.TO_CHAT_USER_NAME, name);
//                sharedPreferenceManager.putString(DBConstants.TO_CHAT_USER_NUMBER, number);
//
//                Log.d("++AKS++", "++AKS number++ : " + number + " , name : " + name);
//
////                //Starting Chat Now*****************************8888
//                if (startChatManager.startChatFromContactPicker(displayPhoto)) {
//                    startActivity(new Intent(this, ConversationActivity.class));
////                    ChatListActivity_NO_USE.this.finish();
//                } else {
//                    toastManager.createToast_Simple(this, "Contact picker Error!!!");
//                }
//
//
//            }
//        }

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
                startActivity(new Intent(getActivity(), ConversationActivity.class));

            } else {
                toastManager.createToast_Simple("Contact picker Error!!!");
            }


        }
    }

    private String retrieveContactName() {

        String contactName = null;

        // querying contact data store
        Cursor cursor = getActivity().getContentResolver().query(uriContact, null, null, null, null);

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
        Cursor cursorID = getActivity().getContentResolver().query(uriContact,
                new String[]{ContactsContract.Contacts._ID},
                null, null, null);

        if (cursorID.moveToFirst()) {

            contactID = cursorID.getString(cursorID.getColumnIndex(ContactsContract.Contacts._ID));
        }

        cursorID.close();

        Log.d(TAG, "Contact ID(AKS): " + contactID);


        // Using the contact ID now we will get contact phone number
        Cursor cursorPhone = getActivity().getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
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
            InputStream inputStream = ContactsContract.Contacts.openContactPhotoInputStream(getActivity().getContentResolver(),
                    ContentUris.withAppendedId(ContactsContract.Contacts.CONTENT_URI, new Long(contactID)));

            if (inputStream != null) {
                bitMapPhoto = BitmapFactory.decodeStream(inputStream);

                //Converting into byte[] for storing in DB
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                bitMapPhoto.compress(Bitmap.CompressFormat.PNG, 100, stream);
                displayPhoto = stream.toByteArray();


                assert inputStream != null;
                inputStream.close();

            }
            else
            {
                Drawable d = getResources().getDrawable(R.drawable.ic_chat_list_person);
                bitMapPhoto = ((BitmapDrawable)d).getBitmap();

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


