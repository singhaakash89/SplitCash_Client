package com.app.splitCash.chat;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.app.splitCash.R;
import com.app.splitCash.constants.DBConstants;
import com.app.splitCash.constants.ServerConstants;
import com.app.splitCash.dataBase.ChatDBAdapter;
import com.app.splitCash.dataBase.ConversationDBAdapter;
import com.app.splitCash.imageManager.conversion.ImageConverterManager;
import com.app.splitCash.postToServer.PostToServer;
import com.app.splitCash.sharedPreferences.SharedPreferenceManager;
import com.app.splitCash.toast.ToastManager;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;


/**
 * Created by Aakash Singh on 24-06-2015.
 */
public class ConversationActivity extends AppCompatActivity {

    private TextView Temp;
    private Toolbar toolbar;
    private TextView chatNameTextView;

    private String chatName;
    private String chatNameWithNoSpace;
    private String chatNumber;
    private byte[] chatUserImageInByte;
    private Bitmap chatUserImageInBitmap;

    private String toUserPhoneNumber;
    private String fromUserPhoneNumber;
    private String toUserName;
    private String fromUserName;

    private String sentText;
    private EditText EnteredText;
    private ImageView chatSend;
    private ListView listView;
    private Cursor cursor;

    private ArrayList<ConversationBean> conversationBeanList;
    private ConversationListCursorAdapter conversationListCursorAdapter;
    private Map<String, String> chatMap;
    private ChatDBAdapter chatDBAdapter;

    private ImageConverterManager imageConverterManager;
    private CircleImageView circleImageView;

    private ToastManager toastManager;
    private final int SELECT_PHOTO = 100;
    private Bitmap yourSelectedImage;
    private String originalChatName;
    private SharedPreferenceManager sharedPreferenceManager;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        Log.d("onCreate", "onCreate Called");

        //---------ToolBar Implementation-------------
        toolbar = (Toolbar) findViewById(R.id.app_bar);
        setSupportActionBar(toolbar);
        //----HomeButtonEnabled-------------------------
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        //---------------------------------------------


        //intializing context for Toast
        toastManager = new ToastManager(this);

        //intializing context for ProgressDialog
        progressDialog = new ProgressDialog(this);

        // Using SharedPreference to fetch groupNAME value
        Log.d("DBConstants.groupSharedPrefKey", "" + DBConstants.groupSharedPrefKey);
        SharedPreferenceManager sharedPreferenceManager = new SharedPreferenceManager(getApplicationContext());
        if (DBConstants.groupSharedPrefKey == 0) {
            DBConstants.groupSharedPrefKey = Integer.parseInt(sharedPreferenceManager.getString("DBConstants.groupSharedPrefKey"));
            chatNumber = sharedPreferenceManager.getString(String
                    .valueOf(DBConstants.groupSharedPrefKey));
            Log.d("chatNumber(AKS_NEW_USING_onPause)", chatNumber);
            // =================================================

        } else {
            chatNumber = sharedPreferenceManager.getString(String
                    .valueOf(DBConstants.groupSharedPrefKey));
            Log.d("chatNumber(AKS_NEW_WithOut_USING_onPause)", chatNumber);
            // =================================================
        }


        //for capturing the original chat name without alteration for using to pass
        //as argument for chat tableName
        originalChatName = null;
        //****************Fetching chatNAME AND iMAGE USING nUMBER;*************8
        //For Cursor
        chatDBAdapter = new ChatDBAdapter(this);
        cursor = chatDBAdapter.getNameAndImageCursor(chatNumber);
        cursor.moveToFirst();
        //FETCHING NAME AND IMAGE
        if (cursor.getCount() > 0) {
            chatName = cursor.getString(cursor.getColumnIndex(DBConstants.CHAT_NAME));
            originalChatName = chatName;
            chatUserImageInByte = cursor.getBlob(cursor.getColumnIndex(DBConstants.CHAT_TO_USER_IMAGE));
            cursor.close();
        }
        //Setting NAME OF cHAT
        //cALCULATING CHATNAME SIZE FOR AppBar
        chatNameTextView = (TextView) findViewById(R.id.chatNameTextView);
        if (chatName.length() > 16) {
            //subString(start_index, end_index)
            //start_index - inclusive
            //end_index - exclusive
            chatName = chatName.substring(0, 17);
            chatName = chatName.concat("...");
            chatNameTextView.setText(chatName);
        } else {
            chatNameTextView.setText(chatName);
        }


        //cONVERTING IMAGE FORM byte array to bitmap
        //Fetching Image from DB for Corresponding contact selected
        imageConverterManager = new ImageConverterManager(this);
        chatUserImageInBitmap = imageConverterManager.getBitmapFromByteArray(chatUserImageInByte);
        circleImageView = (CircleImageView) findViewById(R.id.circleViewAppBar);
        circleImageView.setImageBitmap(chatUserImageInBitmap);
        //********************************************************

//        //For NotifydataSetChanged
//        conversationBeanList = new ArrayList<ConversationBean>();

        //for sending Chat to Sever
        chatMap = new HashMap<String, String>();

        EnteredText = (EditText) findViewById(R.id.chatEditText);
        listView = (ListView) findViewById(R.id.chat_list_view);
        chatSend = (ImageView) findViewById(R.id.chatSend);

        // ============Removing sPace from table name for DB
        chatNameWithNoSpace = originalChatName.replaceAll("\\s+", "");
        conversationListCursorAdapter = new ConversationListCursorAdapter(this, chatNameWithNoSpace);
        listView.setAdapter(conversationListCursorAdapter.getAdapter());

        //onClickListener to send chat on button press
        chatSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (view == chatSend) {
                    sentText = EnteredText.getText().toString();
                    sendChat(sentText);
                }

                EnteredText.setText("");

            }
        });

        //TextWatcherListener for text change event happens inside EditText
        EnteredText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int start, int before, int count) {
                if (EnteredText.getText().toString().equals("")) {

                } else {
                    chatSend.setImageResource(R.drawable.ic_chat_send);

                }
            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (editable.length() == 0) {
                    chatSend.setImageResource(R.drawable.ic_chat_send);
                } else {
                    chatSend.setImageResource(R.drawable.ic_chat_send_active);
                }
            }
        });

    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.d("", "onStart Called");
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d("", "onPause Called");

        chatNumber = sharedPreferenceManager.getString(String
                .valueOf(DBConstants.groupSharedPrefKey));

        Log.d("chatNumber_From_onPause : ", "" + chatNumber);

        //Storing groupSharedPrefKey into sharedpref for fetching it when return froonActivityResult
        sharedPreferenceManager.putString("DBConstants.groupSharedPrefKey", "" + DBConstants.groupSharedPrefKey);
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.d("", "onStop Called");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d("", "onDestroy Called");
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d("", "onResume Called");
        //---------ToolBar Implementation-------------
        toolbar = (Toolbar) findViewById(R.id.app_bar);
        setSupportActionBar(toolbar);
        //----HomeButtonEnabled-------------------------
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        //---------------------------------------------

        // Using SharedPreference to fetch groupNAME value
        Log.d("DBConstants.groupSharedPrefKey_RESUME", "" + DBConstants.groupSharedPrefKey);

        sharedPreferenceManager = new SharedPreferenceManager(getApplicationContext());
        Log.d("SharedPreferenceManager_RESUME", "" + sharedPreferenceManager);

        chatNumber = sharedPreferenceManager.getString(String
                .valueOf(DBConstants.groupSharedPrefKey));
        Log.d("chatNumber(AKS_RESUME)", chatNumber);
        // =================================================

        //for capturing the original chat name without alteration for using to pass
        //as argument for chat tableName
        String originalChatName = null;
        //****************Fetching chatNAME AND iMAGE USING nUMBER;*************8
        //For Cursor
        chatDBAdapter = new ChatDBAdapter(this);
        cursor = chatDBAdapter.getNameAndImageCursor(chatNumber);
        cursor.moveToFirst();

        //FETCHING NAME AND IMAGE
        if (cursor.getCount() > 0) {
            chatName = cursor.getString(cursor.getColumnIndex(DBConstants.CHAT_NAME));
            originalChatName = chatName;
            chatUserImageInByte = cursor.getBlob(cursor.getColumnIndex(DBConstants.CHAT_TO_USER_IMAGE));
            cursor.close();
        }
        //Setting NAME OF cHAT
        //cALCULATING CHATNAME SIZE FOR aPPbAR
        chatNameTextView = (TextView) findViewById(R.id.chatNameTextView);
        if (chatName.length() > 16) {
            //subString(start_index, end_index)
            //start_index - inclusive
            //end_index - exclusive
            chatName = chatName.substring(0, 17);
            chatName = chatName.concat("...");
            chatNameTextView.setText(chatName);
        } else {
            chatNameTextView.setText(chatName);
        }

        //cONVERTING IMAGE FORM byte array to bitmap
        //Fetching Image from DB for Corresponding contact selected
        imageConverterManager = new ImageConverterManager(this);
        chatUserImageInBitmap = imageConverterManager.getBitmapFromByteArray(chatUserImageInByte);
        circleImageView = (CircleImageView) findViewById(R.id.circleViewAppBar);
        circleImageView.setImageBitmap(chatUserImageInBitmap);

        //********************************************************8


        EnteredText = (EditText) findViewById(R.id.chatEditText);
        listView = (ListView) findViewById(R.id.chat_list_view);
        chatSend = (ImageView) findViewById(R.id.chatSend);

        // ============Removing sPace from table name
        chatNameWithNoSpace = originalChatName.replaceAll("\\s+", "");
        conversationListCursorAdapter = new ConversationListCursorAdapter(this, chatNameWithNoSpace);
        listView.setAdapter(conversationListCursorAdapter.getAdapter());

        //onClickListener to send chat on button press
        chatSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (view == chatSend) {
                    sentText = EnteredText.getText().toString();
                    sendChat(sentText);
                }

                EnteredText.setText("");

            }
        });

        //TextWatcherListener for text change event happens inside EditText
        EnteredText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int start, int before, int count) {
                if (EnteredText.getText().toString().equals("")) {

                } else {
                    chatSend.setImageResource(R.drawable.ic_chat_send);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (editable.length() == 0) {
                    chatSend.setImageResource(R.drawable.ic_chat_send);
                } else {
                    chatSend.setImageResource(R.drawable.ic_chat_send_active);
                }
            }
        });

    }

    @Override
    protected void onRestart() {
        super.onRestart();
        Log.d("", "onRestart Called");
        //---------ToolBar Implementation-------------
        toolbar = (Toolbar) findViewById(R.id.app_bar);
        setSupportActionBar(toolbar);
        //----HomeButtonEnabled-------------------------
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        //---------------------------------------------

        // Using SharedPreference to fetch groupNAME value
        chatNumber = new SharedPreferenceManager(this).getString(String
                .valueOf(DBConstants.groupSharedPrefKey));

        Log.d("chatNumber(AKS_Restart)", chatNumber);
        // =================================================

        //for capturing the original chat name without alteration for using to pass
        //as argument for chat tableName
        String originalChatName = null;
        //****************Fetching chatNAME AND iMAGE USING nUMBER;*************8
        //For Cursor
        chatDBAdapter = new ChatDBAdapter(this);
        cursor = chatDBAdapter.getNameAndImageCursor(chatNumber);
        cursor.moveToFirst();

        //FETCHING NAME AND IMAGE
        if (cursor.getCount() > 0) {
            chatName = cursor.getString(cursor.getColumnIndex(DBConstants.CHAT_NAME));
            originalChatName = chatName;
            chatUserImageInByte = cursor.getBlob(cursor.getColumnIndex(DBConstants.CHAT_TO_USER_IMAGE));
            cursor.close();
        }
        //Setting NAME OF cHAT
        //cALCULATING CHATNAME SIZE FOR aPPbAR
        chatNameTextView = (TextView) findViewById(R.id.chatNameTextView);
        if (chatName.length() > 16) {
            //subString(start_index, end_index)
            //start_index - inclusive
            //end_index - exclusive
            chatName = chatName.substring(0, 17);
            chatName = chatName.concat("...");
            chatNameTextView.setText(chatName);
        } else {
            chatNameTextView.setText(chatName);
        }

        //cONVERTING IMAGE FORM byte array to bitmap
        //Fetching Image from DB for Corresponding contact selected
        imageConverterManager = new ImageConverterManager(this);
        chatUserImageInBitmap = imageConverterManager.getBitmapFromByteArray(chatUserImageInByte);
        circleImageView = (CircleImageView) findViewById(R.id.circleViewAppBar);
        circleImageView.setImageBitmap(chatUserImageInBitmap);

        //********************************************************8


        EnteredText = (EditText) findViewById(R.id.chatEditText);
        listView = (ListView) findViewById(R.id.chat_list_view);
        chatSend = (ImageView) findViewById(R.id.chatSend);

        // ============Removing sPace from table name
        chatNameWithNoSpace = originalChatName.replaceAll("\\s+", "");
        conversationListCursorAdapter = new ConversationListCursorAdapter(this, chatNameWithNoSpace);
        listView.setAdapter(conversationListCursorAdapter.getAdapter());

        //onClickListener to send chat on button press
        chatSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (view == chatSend) {
                    sentText = EnteredText.getText().toString();
                    sendChat(sentText);
                }

                EnteredText.setText("");

            }
        });

        //TextWatcherListener for text change event happens inside EditText
        EnteredText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int start, int before, int count) {
                if (EnteredText.getText().toString().equals("")) {

                } else {
                    chatSend.setImageResource(R.drawable.ic_chat_send);

                }
            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (editable.length() == 0) {
                    chatSend.setImageResource(R.drawable.ic_chat_send);
                } else {
                    chatSend.setImageResource(R.drawable.ic_chat_send_active);
                }
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_chat, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        switch (item.getItemId()) {
            case R.id.settings:
                Toast.makeText(getBaseContext(), R.string.toast_message_setting,
                        Toast.LENGTH_SHORT).show();
                return false;

            default:
                return super.onOptionsItemSelected(item);
        }

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        // startActivity(new Intent(this, Group.class));
        ConversationActivity.this.finish();
    }


    public void sendChat(final String sentText) {
        long res = 0;
        //if text is not empty
        if (!sentText.isEmpty()) {
            //Preparing isImage variable to store image as NULL hwne text is present
            String isImage = "NO";
            byte[] image = isImage.getBytes();
            res = updateInChatTable(sentText, image, false);

            if (res == -1) {
                Log.d("ROW_IN_CHAT_TABLE_INSERTED:error", "Insertion Error");
            } else {
                //building chat
                //false - coz no image only text sending
                Map<String, String> chatMap = buildChatToSend(false);

                //sending chat to server
                if (chatMap != null) {
                    sendChatToServer(chatMap);
                }
            }

        } else {
            Log.d("Text is Empty", "Text is Empty");
        }


    }

    public void addPhoto(View view) {
        toastManager.createToast_Simple("select Image");
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        startActivityForResult(intent, SELECT_PHOTO);

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent imageReturnedIntent) {
        super.onActivityResult(requestCode, resultCode, imageReturnedIntent);
        Log.d("onActivityResult", "onActivityResult");

        //making it final so that it can be used inside thread
        final Intent imageReturnedIntentForThread = imageReturnedIntent;

        switch (requestCode) {
            case SELECT_PHOTO:
                if (resultCode == Activity.RESULT_OK) {
                    //AsyncTask<Params, Progress, Result> execute(Params... params)
                    AsyncTask<Void, Void, Long> mImagePicker = new AsyncTask<Void, Void, Long>() {

                        @Override
                        protected void onPreExecute() {
                            super.onPreExecute();
                            //MAKING Progressbar visible for the time being
                            progressDialog.setMessage("Processing...");
                            progressDialog.show();
                        }

                        @Override
                        protected Long doInBackground(Void... params) {
                            Long insertionResult = 0L;
                            Uri selectedImage = imageReturnedIntentForThread.getData();
                            InputStream imageStream = null;
                            try {
                                imageStream = getContentResolver().openInputStream(selectedImage);
                            } catch (FileNotFoundException e) {
                                e.printStackTrace();
                            }
                            yourSelectedImage = BitmapFactory.decodeStream(imageStream);

                            if (yourSelectedImage != null) {
                                Log.d("yourSelectedImage : ", "yourSelectedImage Received from picker");

                                //CONVERTING from bitmap to byte[]
                                ImageConverterManager imageConverterManager = new ImageConverterManager(getApplicationContext());
                                byte[] imageSelected = imageConverterManager.getByteArrayFromBitMap(yourSelectedImage);

                                //building chat
                                String isChatTextAvailable = "No";
                                insertionResult = updateInChatTable(isChatTextAvailable, imageSelected, true);
                            }

                            return insertionResult;

                        }

                        @Override
                        protected void onPostExecute(Long insertionResult)
                        {
                            //build chat if DB updated successfully
                            if (insertionResult == -1)
                            {
                                Log.d("ROW_IN_CHAT_TABLE_INSERTED:error", "Insertion Error");
                            }
                            else
                            {
                                Log.d("ROW_IN_CHAT_TABLE_INSERTED:success", "Insertion Successfully");
                                //"true" coz sending image NOT text
                                Map<String, String> chatMap = buildChatToSend(true);
                                if (chatMap != null) {
                                    sendChatToServer(chatMap);
                            }

                            }
                            progressDialog.dismiss();

                        }
                    };
                    mImagePicker.execute();
                }
        }


    }


    public long updateInChatTable(final String sentText, final byte[] image, boolean isImage) {
        long insertionId = 0;
        ConversationBean conversationBean = new ConversationBean();

        //TimeStamp
        Date date = new Date();
        long time = date.getTime();
        Log.d("time", "" + time);
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm");
        final String NEW_TIME = simpleDateFormat.format(time);
        Log.d("NEW_TIME", "" + NEW_TIME);


        if (isImage == true) {
            Log.d("isImage : ","True");
            conversationBean.setConversationTimeStamp(NEW_TIME);
            conversationBean.setIsIncomingOrOutgoingConversation(DBConstants.CONVERSATION_SELF);
            conversationBean.setConversationStatus(ChatStatus.PENDING);
            conversationBean.setConversationText(sentText);
            conversationBean.setIsImageAvailableInRow(ImageAvailableForRow.AVAILABLE);
            conversationBean.setConversationImage(image);
            ConversationDBAdapter conversationDBAdapter = new ConversationDBAdapter(getApplicationContext());
            insertionId = conversationDBAdapter.insert(chatNameWithNoSpace, conversationBean.contentValues());
            Log.d("insertionId : ", ""+insertionId);

        } else if (!sentText.isEmpty()) {
            conversationBean.setConversationTimeStamp(NEW_TIME);
            conversationBean.setIsIncomingOrOutgoingConversation(DBConstants.CONVERSATION_SELF);
            conversationBean.setConversationStatus(ChatStatus.PENDING);
            conversationBean.setConversationText(sentText);
            conversationBean.setIsImageAvailableInRow(ImageAvailableForRow.NOT_AVAILABLE);
            conversationBean.setConversationImage(image);
            ConversationDBAdapter conversationDBAdapter = new ConversationDBAdapter(this);
            insertionId = conversationDBAdapter.insert(chatNameWithNoSpace, conversationBean.contentValues());
            if (insertionId < 0) {
                Log.d("ROW_IN_CHAT_TABLE_INSERTED:error", "Insertion Error");
            } else {
                Log.d("ROW_IN_CHAT_TABLE_INSERTED:success", "Insertion Successfully");

            }
        }
        return insertionId;
    }

    public Map<String, String> buildChatToSend(boolean isImage) {

        //for sending Chat to Sever
        Map<String, String> chatMap = new HashMap<String, String>();

        //checking if sending text OR image
        if (isImage == true) {

            //1* upload to cloudInary
            //2* get URL
            //3* SEND url to googleAppEngine

        } else {
            //**********************************************
            //**Constructing Map for Sever POST*************
            //**********************************************
            cursor = chatDBAdapter.cursor(DBConstants.CHAT_TABLE_NAME_STRUCTURE);
            cursor.moveToFirst();

            while (!cursor.isAfterLast()) {
                if (cursor.getString(cursor.getColumnIndex(ServerConstants.CHAT_NAME)).equals(chatName)) {
                    toUserPhoneNumber = cursor.getString(cursor.getColumnIndex(ServerConstants.CHAT_TO_PHONE_NUMBER));
                    fromUserPhoneNumber = cursor.getString(cursor.getColumnIndex(ServerConstants.CHAT_FROM_PHONE_NUMBER));
                    toUserName = cursor.getString(cursor.getColumnIndex(ServerConstants.CHAT_TO_NAME));
                    fromUserName = cursor.getString(cursor.getColumnIndex(ServerConstants.CHAT_FROM_NAME));

                    Log.e("chat : ", "" + chatNameWithNoSpace);
                    Log.e("toUserPhoneNumber : ", "" + toUserPhoneNumber);
                    Log.e("fromUserPhoneNumber : ", "" + fromUserPhoneNumber);
                    Log.e("toUserName : ", "" + toUserName);
                    Log.e("fromUserName : ", "" + fromUserName);

                    chatMap.put(ServerConstants.CHAT_NAME_WITH_NO_SPACE, chatNameWithNoSpace);
                    chatMap.put(ServerConstants.CHAT_TO_PHONE_NUMBER, toUserPhoneNumber);
                    chatMap.put(ServerConstants.CHAT_FROM_PHONE_NUMBER, fromUserPhoneNumber);
                    chatMap.put(ServerConstants.CHAT_TO_NAME, toUserName);
                    chatMap.put(ServerConstants.CHAT_FROM_NAME, fromUserName);
                    chatMap.put(ServerConstants.CHAT_TEXT, sentText);

                }

                cursor.moveToNext();

            }
            cursor.close();
            //**********************************************
        }

        return chatMap;
    }


    public void sendChatToServer(final Map<String, String> chatMap) {
        //Post chat to Sever
        //AsyncTask<Params, Progress, Result> execute(Params... params)
        AsyncTask<Void, Void, Boolean> mPostTask = new AsyncTask<Void, Void, Boolean>() {

            @Override
            protected Boolean doInBackground(Void... params) {
                boolean postResult = false;
                PostToServer postToServer = new PostToServer();
                postResult = postToServer.sendChatToServer(chatMap);

                Log.e("postResult : ", "" + postResult);
                return postResult;
            }

            @Override
            protected void onPostExecute(Boolean result) {
                if (result) {
                    toastManager.createToast_Simple("Chat sent successfully");

                } else {
                    toastManager.createToast_Simple("Chat sending FAILED...!!!");

                }

            }

        };

        mPostTask.execute();
        //************************************************************

    }
}