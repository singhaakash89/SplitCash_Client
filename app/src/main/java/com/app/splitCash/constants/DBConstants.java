package com.app.splitCash.constants;

import android.content.Context;
import android.net.Uri;


//Class contains only DB constants
public class DBConstants {
    public DBConstants(Context context) {

    }

    //**********************DB Details****************************************************************
    //Group DB Details
    public static final String DATABASE_NAME = "SplitCashClientDataBase";
    public static final int DATABASE_VERSION = 7;

    public static final String DB_AUTHORITY = "com.app.splitCash.dataBase";

    public static final String DB_CLIENT_TABLE_NAME = "clientTableName";
    public static final String DB_SERVER_TABLE_NAME = "serverTableName";

    public static final Uri DB_CLIENT_TABLE_URI = Uri.parse("content://com.app.splitCash.bogetteDataBase/" + DB_CLIENT_TABLE_NAME);
    public static final Uri DB_SERVER_TABLE_URI = Uri.parse("content://com.app.splitCash.bogetteDataBase/" + DB_SERVER_TABLE_NAME);


    //----------------------Group Constants--------------------------------------------------------

    //GLOBAL COUNT VARIABLE FOR USING WITH SHAREDPREFERENCE TO PASS GROUPNAME
    public static int groupSharedPrefKey = 0;

    //cONSTANT FOR USING AS VALUE WITH THE uNKNOWN/kNOWN gROUP.
    public static final String GROUP_KNOWN = "k";
    public static final String GROUP_UNKNOWN = "uk";

    //GROUP_LIST TABLE sTRUCTURE
    public final static String GROUP_LIST_TABLE_NAME = "groupListTableName";

    public static final String GROUP_lIST_UID = "uid";
    public static final String GROUP_LIST_ID = "_id";
    public static final String GROUP_LIST_NAME = "groupListName";
    public static final String GROUP_LIST_EXPENSE = "groupListExpense";
    public static final String GROUP_LIST_TYPE = "groupListType";
    public static final String GROUP_LIST_DESCRIPTION = "groupListDescription";
    public static final String GROUP_LIST_KNOWN_UNKNOWN_TYPE = "groupListKnownUnKnownType";
    //table columns Projection
    public static final String[] GROUP_LIST_TABLE_STRUCTURE = new String[]{
            GROUP_LIST_ID, GROUP_lIST_UID, GROUP_LIST_NAME, GROUP_LIST_EXPENSE, GROUP_LIST_TYPE, GROUP_LIST_DESCRIPTION, GROUP_LIST_KNOWN_UNKNOWN_TYPE
    };


    //Group Member Table Structure
    public String MEMBER_LIST_TABLE_NAME = "memberListTableName";

    public static final String MEMBER_lIST_UID = "uid";
    public static final String MEMBER_LIST_ID = "_id";
    public static final String MEMBER_LIST_NAME = "memberName";
    public static final String MEMBER_LIST_PHONE_NUMBER = "memberPhoneNumber";
    public static final String MEMBER_LIST_EXPENSE = "memberExpense";

    //Getter And Setters for the Member_List_Table_Name
    public String getMEMBER_LIST_TABLE_NAME() {
        return MEMBER_LIST_TABLE_NAME;
    }

    public void setMEMBER_LIST_TABLE_NAME(String MEMBER_LIST_TABLE_NAME) {
        this.MEMBER_LIST_TABLE_NAME = MEMBER_LIST_TABLE_NAME;
    }

    //table columns Projection
    public static final String[] MEMBER_LIST_TABLE_STRUCTURE = new String[]{
            MEMBER_LIST_ID, MEMBER_lIST_UID, MEMBER_LIST_NAME, MEMBER_LIST_PHONE_NUMBER, MEMBER_LIST_EXPENSE
    };


    //SPLITCASH_LIST TABLE sTRUCTURE
    public String MONEYTIME_LIST_TABLE_NAME = "moneyTimeListTableName";

    public static final String MONEYTIME_lIST_UID = "uid";
    public static final String MONEYTIME_LIST_ID = "_id";
    public static final String MONEYTIME_LIST_NAME = "moneyTimeMemberName";
    public static final String MONEYTIME_LIST_EXPENSE = "moneyTimeMemberExpense";
    public static final String MONEYTIME_LIST_TO_TAKE_AMOUNT = "toTakeAmount";
    public static final String MONEYTIME_LIST_TO_GIVE_AMOUNT = "toGiveAmount";

    //table columns Projection
    public static final String[] MONEYTIME_LIST_TABLE_STRUCTURE = new String[]{MONEYTIME_LIST_ID,
            MONEYTIME_lIST_UID, MONEYTIME_LIST_NAME, MONEYTIME_LIST_EXPENSE, MONEYTIME_LIST_TO_TAKE_AMOUNT, MONEYTIME_LIST_TO_GIVE_AMOUNT
    };

    public String getMONEYTIME_LIST_TABLE_NAME() {
        return MONEYTIME_LIST_TABLE_NAME;
    }

    public void setMONEYTIME_LIST_TABLE_NAME(String MONEYTIME_LIST_TABLE_NAME) {
        this.MONEYTIME_LIST_TABLE_NAME = MONEYTIME_LIST_TABLE_NAME;
    }


    //User TABLE - Constants only for DB
    public static final String USER_TABLE_NAME = "userTableName";

    public static final String USER_ID = "_id";
    public static final String USER_GCM_ID = "userGcmId";
    public static final String USER_NAME = "userNameForRegistration";
    public static final String USER_PHONE_NUMBER = "phoneNumberForRegistration";
    public static final String USER_IMAGE_PATH = "userImagePath";
    public static final String USER_IMAGE_DATA = "userImageData";

    //table columns Projection
    public static final String[] USER_TABLE_NAME_STRUCTURE = new String[]{USER_ID, USER_GCM_ID, USER_NAME, USER_PHONE_NUMBER, USER_IMAGE_PATH, USER_IMAGE_DATA};


    //Chat TABLE
    public final static String CHAT_LIST_TABLE_NAME = "chatTableName";

    public static final String CHAT_ID = "_id";
    public static final String CHAT_UID = "uId";
    public static final String CHAT_NAME = "chatName";
    public static final String CHAT_TO_PHONE_NUMBER = "chatToPhoneNumber";
    public static final String CHAT_TO_NAME = "chatToName";
    public static final String CHAT_FROM_PHONE_NUMBER = "chatFromPhoneNumber";
    public static final String CHAT_FROM_NAME = "chatFromName";
    public static final String CHAT_TO_USER_IMAGE = "chatToUserImage";


    //table columns Projection
    public static final String[] CHAT_TABLE_NAME_STRUCTURE = new String[]{CHAT_ID, CHAT_NAME, CHAT_TO_PHONE_NUMBER, CHAT_TO_NAME, CHAT_FROM_PHONE_NUMBER, CHAT_FROM_NAME, CHAT_TO_USER_IMAGE};


    //Conversation TABLE
    public static String CONVERSATION_TABLE_NAME = "conversationTableName";

    public static final String CONVERSATION_ID = "_id";
    public static final String CONVERSATION_UID = "uId";
    public static final String CONVERSATION_TEXT = "conversationText";
    public static final String CONVERSATION_IMAGE = "conversationImage";
    public static final String CONVERSATION_TIME_STAMP = "conversationTimeStamp";
    public static final String CONVERSATION_IMAGE_AVAILABLE_IN_ROW = "conversationImageAvailableInRow";
    public static final String CONVERSATION_STATUS = "conversationStatus";
    public static final String CONVERSATION_INCOMING_OR_OUTGOING = "conversationIncomingOrOutgoing";

    public static final String CONVERSATION_OTHER = "conversationSend";
    public static final String CONVERSATION_SELF = "conversationReceive";

    //table columns Projection
    public static final String[] CONVERSATION_TABLE_NAME_STRUCTURE = new String[]{CONVERSATION_ID, CONVERSATION_UID, CONVERSATION_TEXT, CONVERSATION_IMAGE, CONVERSATION_TIME_STAMP, CONVERSATION_STATUS, CONVERSATION_IMAGE_AVAILABLE_IN_ROW, CONVERSATION_INCOMING_OR_OUTGOING};


    //Constant required for starting chat contact information
    public static final String TO_CHAT_USER_NAME = "toChatUserName";
    public static final String TO_CHAT_USER_NUMBER = "toChatUserNumber";

}
