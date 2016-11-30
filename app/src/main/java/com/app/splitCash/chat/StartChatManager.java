package com.app.splitCash.chat;

import android.content.Context;

import com.app.splitCash.constants.DBConstants;
import com.app.splitCash.dataBase.ChatDBAdapter;
import com.app.splitCash.dataBase.ConversationDBAdapter;
import com.app.splitCash.dataBase.Message;
import com.app.splitCash.sharedPreferences.SharedPreferenceManager;
import com.app.splitCash.toast.ToastManager;

public class StartChatManager {


    private String toUserName = "";
    private String toUserPhoneNumber = "";

    private String fromUserName = "";
    private String fromUserPhoneNumber = "";

    private byte[] displayPhoto;

    private ToastManager toastManager;

    private Context mContext;

    private SharedPreferenceManager sharedPreferenceManager;

    private boolean isChatCreated = false;

    public StartChatManager(Context context) {
        mContext = context;
        sharedPreferenceManager = new SharedPreferenceManager(context);
    }


    public boolean startChatFromContactPicker(byte[] displayPhoto) {

        //Fetching Owner Info
        fromUserName = sharedPreferenceManager.getString(DBConstants.USER_NAME);
        fromUserPhoneNumber = sharedPreferenceManager.getString(DBConstants.USER_PHONE_NUMBER);

        toUserName = sharedPreferenceManager.getString(DBConstants.TO_CHAT_USER_NAME);
        toUserPhoneNumber = sharedPreferenceManager.getString(DBConstants.TO_CHAT_USER_NUMBER);

        this.displayPhoto = displayPhoto;

        //Storing Chat in DB
        ChatBean chatBean = new ChatBean();
        chatBean.setChatName(toUserName);
        chatBean.setChatToPhoneNumber(toUserPhoneNumber);
        chatBean.setChatToName(toUserName);
        chatBean.setChatFromPhoneNumber(fromUserPhoneNumber);
        chatBean.setChatFromName(fromUserName);
        chatBean.setChatToUserImage(displayPhoto);

        ChatDBAdapter chatDBAdapter = new ChatDBAdapter(mContext);


        long id = chatDBAdapter.insert(DBConstants.CHAT_LIST_TABLE_NAME, chatBean.contentValues());

        //Toast on successful row insertion
        if (id < 0) {
            Message.message(mContext, "Chat Creation Error...!!!");
            Message.messageBold(mContext, "Chat ", "" + chatBean.getChatName(), "already exists");
            //startActivity(new Intent(this, HomePageActivity_NO_USE.class));
            //CreateUnKnownExpenseGroup.this.finish();
        } else {

            isChatCreated = true;
            toastManager = new ToastManager(mContext);
            toastManager.createToast_Simple("Starting chat with " + toUserName);

            //Creating CHAT table before starting Actual Chatting
            ConversationDBAdapter conversationDBAdapter = new ConversationDBAdapter(mContext);
            conversationDBAdapter.callCreateTable(toUserName.replaceAll("\\s+", ""));
            //*****************************************************

            //******************************************************
            //uSING sharedpreference to pass chatname
            DBConstants.groupSharedPrefKey++;
            //sharedPreferenceManager.putString(String.valueOf(DBConstants.groupSharedPrefKey), toUserName);
            sharedPreferenceManager.putString(String.valueOf(DBConstants.groupSharedPrefKey), toUserPhoneNumber);

            //******************************************************


        }

        return isChatCreated;


    }
}
