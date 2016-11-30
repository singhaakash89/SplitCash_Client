package com.app.splitCash.downFromServer;

import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Map;

import android.annotation.SuppressLint;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.app.splitCash.R;
import com.app.splitCash.chat.ChatBean;
import com.app.splitCash.chat.ConversationBean;
import com.app.splitCash.constants.DBConstants;
import com.app.splitCash.constants.ServerConstants;
import com.app.splitCash.dataBase.ChatDBAdapter;
import com.app.splitCash.dataBase.ConversationDBAdapter;
import com.app.splitCash.dataBase.MasterDBAdapter;
import com.app.splitCash.dataBase.MemberDBAdapter;
import com.app.splitCash.dataBase.MoneyTimeDBAdapter;
import com.app.splitCash.group.GroupBean;
import com.app.splitCash.group.MemberBean;
import com.app.splitCash.imageManager.conversion.ImageConverterManager;
import com.app.splitCash.sharedPreferences.SharedPreferenceManager;

public class DownFromServer {
    boolean result = false;

    Context mContext;

    private String groupAdmin;
    private String groupAdminPhoneNumber;

    private String groupName;
    private String groupType;
    private String groupDescription;

    private String memberName;
    private String memberExpense;
    private String memberPhoneNumber;

    private BigDecimal LOCAL_OTHER_MEMBER_NAME_COUNT = new BigDecimal(0.1);
    private BigDecimal LOCAL_OTHER_MEMBER_PHONE_NUMBER_COUNT = new BigDecimal(
            0.2);
    private BigDecimal LOCAL_OTHER_MEMBER_EXPENSE_COUNT = new BigDecimal(0.3);

    private boolean groupFlag = false;
    private boolean memberFlag = false;

    SharedPreferenceManager sharedPreferenceManager;

    public DownFromServer(Context mContext) {
        this.mContext = mContext;
    }

    @SuppressLint("LongLogTag")
    public boolean createGroupInDevice(Map<String, String> userMap,
                                       Map<BigDecimal, String> memberMap) {

        Log.e("From createGroupInDevice", "From createGroupInDevice");

        try {
            while (memberMap.get(LOCAL_OTHER_MEMBER_NAME_COUNT).equals(null) == false) {
                Log.e("memberMap : ",
                        "" + memberMap.get(LOCAL_OTHER_MEMBER_NAME_COUNT));

                LOCAL_OTHER_MEMBER_NAME_COUNT = LOCAL_OTHER_MEMBER_NAME_COUNT
                        .add(new BigDecimal(1));
            }

        } catch (NullPointerException ne) {
            Log.e("catch(NullPointerException ne)",
                    "catch(NullPointerException ne)");

            ne.printStackTrace();
        } finally {
            Log.e("Finally", "Finally");

            LOCAL_OTHER_MEMBER_NAME_COUNT = new BigDecimal(0.1);
        }

        // TO BE USED in invite Groups
        groupAdmin = userMap.get(ServerConstants.GROUP_ADMIN);
        groupAdminPhoneNumber = userMap
                .get(ServerConstants.GROUP_ADMIN_PHONE_NUMBER);

        groupName = userMap.get(ServerConstants.GROUP_NAME);
        groupType = userMap.get(ServerConstants.GROUP_TYPE);
        groupDescription = userMap.get(ServerConstants.GROUP_DESCRIPTION);
        // memberName = userMap.get(ServerConstants.MEMBER_NAME);
        // memberExpense = userMap.get(ServerConstants.MEMBER_EXPENSE);

        // will be used while adding member to Group
        // memberPhoneNumber = userMap.get(ServerConstants.MEMBER_PHONE_NUMBER);

        GroupBean groupBean = new GroupBean();

        groupBean.setNameGroup(groupName);
        Log.e("setNameGroup : ", "setNameGroup");

        groupBean.setUid(groupBean.getUid());
        Log.e("setUid :", "" + groupBean.getUid());

        groupBean.setGroupType(groupType);
        Log.e("setGroupType : ", "setGroupType");

        groupBean.setGroupDescription(groupDescription);
        Log.e("setGroupDescription : ", "setGroupDescription");

        groupBean.setGroupKnownUnknownType(DBConstants.GROUP_UNKNOWN);
        Log.e("setGroupKnownUnknownType : ", "setGroupKnownUnknownType");

        // Log.e("createGroupInDevice = ", "groupName = " + groupName
        // + ", groupUid = " + groupBean.getUid() + ", groupType = "
        // + groupType);

        Log.e("GroupBean SAVED", "GroupBean SAVED");

        MasterDBAdapter masterDbAdapter = new MasterDBAdapter(mContext);

        // //**********************Dropping MemberTABLE iF
        // eXISTS***************************
        // MemberDBAdapter memberDBAdapter = new MemberDBAdapter(mContext);
        //
        // String groupNameWithOutSpace = groupName.replaceAll("\\s+", "");
        //
        // memberDBAdapter.dropTableIfExists(masterDbAdapter, groupNameWithOutSpace);
        // //*******************************************************************************

        long id = masterDbAdapter.insert(DBConstants.GROUP_LIST_TABLE_NAME,
                groupBean.contentValues());

        // Toast on successful row insertion
        if (id < 0) {
            Log.e("Group Creation = ", "FAILED...!!!");
        } else {
            Log.e("Group Created = ", "" + groupName);
            groupFlag = true;

//			//Updating Cursor
//			LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
//			View view = inflater.inflate(R.layout.activity_home_fragment_app_bar, null, false);
//			ListView listView = (ListView) view.findViewById(R.id.groupListView);
//			GroupListCursorAdapter groupListCursorAdapter = new GroupListCursorAdapter(mContext);
//			listView.setAdapter(groupListCursorAdapter.getAdapter());
            // //****************************Notifying Adapter for Data
            // changed*****************************************************************
            // GroupListCursorAdapter groupListCursorAdapter = new
            // GroupListCursorAdapter(mContext);
            //
            // ArrayList<GroupBean> groupBeanList =
            // groupListCursorAdapter.getGroupList();
            //
            // GroupListCursorAdapter.MyAdapter myAdapter =
            // groupListCursorAdapter.new MyAdapter(mContext, groupBeanList);
            //
            // //GroupListCursorAdapter groupListCursorAdapterNew = new
            // GroupListCursorAdapter(mContext);
            //
            // myAdapter.notifyAdapterForDataChange(groupBean, groupBeanList);
            //
            // (groupListCursorAdapter.getAdapter()).notifyDataSetChanged();
            // //****************************************************************************************************

        }

        // Adding Member Remotely
        result = addMemberToGroup(memberMap);

        return result;
    }

    @SuppressLint("LongLogTag")
    public boolean addMemberToGroup(Map<BigDecimal, String> memberMap) {
        Log.e("ROM addMemberToGroup :", "fROM addMemberToGroup");

        boolean result = false;

        String groupNameWithOutSpace = groupName.replaceAll("\\s+", "");

        ArrayList<String> memberPhoneNumberFlag = new ArrayList<String>();

        MemberDBAdapter memberDBAdapter = new MemberDBAdapter(mContext);
        MemberBean memberBean = new MemberBean();

        // creating table
        memberDBAdapter.callCreateTable(groupNameWithOutSpace);

        // Checking whether table is already created or NOT
        Cursor cursor = memberDBAdapter.cursor(mContext, groupNameWithOutSpace,
                DBConstants.MEMBER_LIST_TABLE_STRUCTURE);

        cursor.moveToFirst();

        if (cursor == null || cursor.getCount() == 0) {
            // Setting and Inserting ADMIN details
            memberBean.setUid(memberBean.getRandomUID());
            memberBean.setMemberName(groupAdmin);
            memberBean.setMemberAmountExpensed("0");
            memberBean.setMemberPhoneNumber(groupAdminPhoneNumber);

            // Inserting VALUES
            long id = memberDBAdapter.insert(groupNameWithOutSpace,
                    memberBean.contentValues());

            // Toast on successful row insertion
            if (id < 0) {
                Log.e("Admin Added FAILED...!!!", "Admin Added FAILED...!!!");
                cursor.close();
            } else {
                Log.e("Admin Added SuccessFully", "Admin Added SuccessFully");
                cursor.close();
            }

        }

        // ************nOW INSERTING iNVITED MEMBER INTO tABLE

        try {
            // Making all Constant Keys to Default for Accurate Key/VALUE
            // oPERATION
            BigDecimal LOCAL_OTHER_MEMBER_NAME_COUNT = new BigDecimal(0.1);
            BigDecimal LOCAL_OTHER_MEMBER_PHONE_NUMBER_COUNT = new BigDecimal(
                    0.2);
            BigDecimal LOCAL_OTHER_MEMBER_EXPENSE_COUNT = new BigDecimal(0.3);

            cursor = memberDBAdapter.cursor(mContext, groupNameWithOutSpace,
                    DBConstants.MEMBER_LIST_TABLE_STRUCTURE);

            while (memberMap.get(LOCAL_OTHER_MEMBER_NAME_COUNT).equals(null) == false) {
                // flag for adding Members Not Present in the MEMBER_TABLE
                boolean flag = false;

                // // making result false everyTime to get to know the last
                // insertion
                // // status
                // result = false;

                cursor.moveToFirst();

                while (!cursor.isAfterLast()) {
                    memberPhoneNumberFlag
                            .add(cursor.getString(cursor
                                    .getColumnIndex(DBConstants.MEMBER_LIST_PHONE_NUMBER)));

                    Log.e("memberPhoneNumberFlag.contains(memberMap.get(LOCAL_OTHER_MEMBER_PHONE_NUMBER_COUNT))",
                            ""
                                    + memberPhoneNumberFlag.contains(memberMap
                                    .get(LOCAL_OTHER_MEMBER_PHONE_NUMBER_COUNT)));

                    if (memberPhoneNumberFlag.contains(memberMap
                            .get(LOCAL_OTHER_MEMBER_PHONE_NUMBER_COUNT)) == false) {
                        flag = true;
                    }

                    cursor.moveToNext();
                }

                Log.e("flag : ", "" + flag);

                if (flag) {

                    // Fetching member Details from MAP
                    memberName = memberMap.get(LOCAL_OTHER_MEMBER_NAME_COUNT);
                    memberExpense = memberMap
                            .get(LOCAL_OTHER_MEMBER_EXPENSE_COUNT);
                    memberPhoneNumber = memberMap
                            .get(LOCAL_OTHER_MEMBER_PHONE_NUMBER_COUNT);

                    Log.e("memberName(Insertion) : ", "" + memberName);
                    Log.e("memberExpense(Insertion) : ", "" + memberExpense);
                    Log.e("memberPhoneNumber(Insertion) : ", ""
                            + memberPhoneNumber);

                    // setting member details
                    memberBean.setUid(memberBean.getUid());
                    memberBean.setMemberName(memberName);
                    memberBean.setMemberAmountExpensed(memberExpense);
                    memberBean.setMemberPhoneNumber(memberPhoneNumber);

                    // Inserting VALUES
                    long id = memberDBAdapter.insert(groupNameWithOutSpace,
                            memberBean.contentValues());

                    // Toast on successful row insertion
                    if (id < 0) {
                        Log.e("Member Added FAILED...!!!",
                                "Member Added FAILED...!!!");
                    } else {
                        Log.e("Member Added SuccessFully",
                                "Member Added SuccessFully");
                        memberFlag = true;
                        result = true;
                    }

                }

                // Incrementing BigDecimal
                LOCAL_OTHER_MEMBER_NAME_COUNT = LOCAL_OTHER_MEMBER_NAME_COUNT
                        .add(new BigDecimal(1));

                LOCAL_OTHER_MEMBER_PHONE_NUMBER_COUNT = LOCAL_OTHER_MEMBER_PHONE_NUMBER_COUNT
                        .add(new BigDecimal(1));

                LOCAL_OTHER_MEMBER_EXPENSE_COUNT = LOCAL_OTHER_MEMBER_EXPENSE_COUNT
                        .add(new BigDecimal(1));

            }

        } catch (NullPointerException ne) {
            Log.e("Last Catch", "Last Catch");

            ne.printStackTrace();
        } finally {

            Log.e("Last finally", "Last finally");

            // Sending Notification for Initial Group Created Succesfully
            if (groupFlag) {
                sendNotificationOnGroupAdded(groupAdmin, groupName);
            }

            // Sending Notification for Only Member Added to Existing Group
            if (!groupFlag && memberFlag) {
                sendNotificationOnMemberAdded(groupAdmin, groupName);
            }

            return result;
        }

    }

    public boolean editAmount(String groupName, String groupMemberPhoneNumber,
                              String editAmount, String updateOperation, String updateOwnerName) {

        Log.e("groupName", "" + groupName);
        Log.e("groupMemberPhoneNumber", "" + groupMemberPhoneNumber);
        Log.e("editAmount", "" + editAmount);
        Log.e("updateOperation", "" + updateOperation);

        String groupNameWithNoSpace = groupName.replaceAll("\\s+", "");
        String groupMemberExistingAmount = "";
        String groupMemberName = "";

        Log.e("groupNameWithNoSpace", "" + groupNameWithNoSpace);

        MemberDBAdapter memberDBAdapter = new MemberDBAdapter(mContext);
        Cursor cursor = memberDBAdapter.cursor(mContext, groupNameWithNoSpace,
                DBConstants.MEMBER_LIST_TABLE_STRUCTURE);

        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            if (groupMemberPhoneNumber.equals(cursor.getString(cursor
                    .getColumnIndex(DBConstants.MEMBER_LIST_PHONE_NUMBER)))) {
                groupMemberExistingAmount = cursor.getString(cursor
                        .getColumnIndex(DBConstants.MEMBER_LIST_EXPENSE));
                groupMemberName = cursor.getString(cursor
                        .getColumnIndex(DBConstants.MEMBER_LIST_NAME));
            }
            cursor.moveToNext();
        }

        Log.e("groupMemberExistingAmount", "" + groupMemberExistingAmount);
        Log.e("groupMemberName", "" + groupMemberName);

        if (updateOperation.equals(ServerConstants.ADDITION_AMOUNT_UPDATE)) {
            // Setting MemberBean for Expense VALUE AND then Returning the
            // ContentVALUES FOR uPDATE()
            MemberBean memberBean = new MemberBean();
            Integer NewExpense = Integer.valueOf(groupMemberExistingAmount)
                    + Integer.valueOf(editAmount);

            Log.e("NewExpense", "" + NewExpense);

            // //this is necessary to make memberList having the name and amount
            // together to make it working with the adapter at
            // NotifySetDATAChanged().
            // memberBean.setMemberName(String.valueOf(groupMemberName));

            // Main updation for amount added
            memberBean.setMemberAmountExpensed(String.valueOf(NewExpense));
            ContentValues cv = memberBean.contentValuesExpense();

            // cALLING UPDATE() OF mEMBER Adapter
            new MemberDBAdapter(mContext)
                    .editExpenseColumn(new MasterDBAdapter(mContext),
                            groupNameWithNoSpace, groupMemberPhoneNumber, cv);

            // Message.message(mContext, "Amount updated for "+groupMemberName+
            // " In "+groupName);

            // SendNotification
            sendNotificationOnAmountUpdate_Added(updateOwnerName, groupName,
                    editAmount, groupMemberName);

            result = true;
        } else if (updateOperation
                .equals(ServerConstants.SUBTRACTION_AMOUNT_UPDATE)) {
            // Setting MemberBean for Expense VALUE AND then Returning the
            // ContentVALUES FOR uPDATE()
            MemberBean memberBean = new MemberBean();
            Integer NewExpense = Integer.valueOf(groupMemberExistingAmount)
                    - Integer.valueOf(editAmount);

            Log.e("NewExpense", "" + NewExpense);

            // //this is necessary to make memberList having the name and amount
            // together to make it working with the adapter at
            // NotifySetDATAChanged().
            // memberBean.setMemberName(String.valueOf(groupMemberName));

            // Main updation for amount added
            memberBean.setMemberAmountExpensed(String.valueOf(NewExpense));
            ContentValues cv = memberBean.contentValuesExpense();

            // cALLING UPDATE() OF mEMBER Adapter
            new MemberDBAdapter(mContext)
                    .editExpenseColumn(new MasterDBAdapter(mContext),
                            groupNameWithNoSpace, groupMemberPhoneNumber, cv);

            // Message.message(mContext, "Amount updated for "+groupMemberName+
            // " In "+groupName);

            // SendNotification
            sendNotificationOnAmountUpdate_Subtracted(updateOwnerName,
                    groupName, editAmount, groupMemberName);

            result = true;

        }

        return result;
    }

    public boolean deleteMember(String ownerPhoneNumber, String groupName,
                                final String groupMemberPhoneNumber, final String groupAdmin) {
        Log.e("groupName", "" + groupName);
        Log.e("groupMemberPhoneNumber", "" + groupMemberPhoneNumber);
        Log.e("groupAdmin", "" + groupAdmin);
        Log.e("ownerPhoneNumber", "ownerPhoneNumber" + ownerPhoneNumber);

        String groupNameWithNoSpace = groupName.replaceAll("\\s+", "");
        String groupMemberName = "";

        boolean flag = false;

        Log.e("groupNameWithNoSpace", "" + groupNameWithNoSpace);

        MemberDBAdapter memberDBAdapter = new MemberDBAdapter(mContext);

        Cursor cursor = memberDBAdapter.cursor(mContext, groupNameWithNoSpace,
                DBConstants.MEMBER_LIST_TABLE_STRUCTURE);
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            if (groupMemberPhoneNumber.equals(cursor.getString(cursor
                    .getColumnIndex(DBConstants.MEMBER_LIST_PHONE_NUMBER)))) {
                groupMemberName = cursor.getString(cursor
                        .getColumnIndex(DBConstants.MEMBER_LIST_NAME));
            }
            cursor.moveToNext();
        }

        // nOW dELETING Member
        memberDBAdapter.deleteRowFromTable(new MasterDBAdapter(mContext),
                groupNameWithNoSpace, groupMemberPhoneNumber);

        // Now delete THE Member Table as well
        if (ownerPhoneNumber.equals(groupMemberPhoneNumber) == true) {
            // Calling Method of MEMber Adapter to delete the row
            new MasterDBAdapter(mContext).deleteRowFromTable(groupName);

            // Also delete group with this name in Member and MoneyTime
            // TABLE=============
            // Member table dropped
            new MemberDBAdapter(mContext).dropTableIfExists(new MasterDBAdapter(
                    mContext), groupName.replaceAll("\\s+", ""));
            // Message.message(context, "Member Table Dropped");

            // MoneyTime table Dropped
            DBConstants dbConstants = new DBConstants(mContext);
            String groupNameNew = groupName.replaceAll("\\s+", "").concat(
                    dbConstants.MONEYTIME_LIST_TABLE_NAME);
            dbConstants.setMEMBER_LIST_TABLE_NAME(groupNameNew);
            new MoneyTimeDBAdapter(mContext).dropTableIfExists(new MasterDBAdapter(
                    mContext), dbConstants);
            // Message.message(context, "MoneyTime Table Dropped");
            // ===============================================================

            // SendNotification
            sendNotificationOnMemberDelete_onSameDevice(groupName, groupAdmin);
        } else {
            // SendNotification
            sendNotificationOnMemberDelete_onOtherDevices(groupName,
                    groupMemberName, groupAdmin);
        }

        // ******************************************************************************************************************************

        result = true;

        return result;
    }


    public boolean createChatInDevice(Map<String, String> chatMap) {
        String chatWithNoSpace = chatMap.get(ServerConstants.CHAT_NAME_WITH_NO_SPACE);
        String toUserPhoneNumber = chatMap.get(ServerConstants.CHAT_TO_PHONE_NUMBER);
        String fromUserPhoneNumber = chatMap.get(ServerConstants.CHAT_FROM_PHONE_NUMBER);
        String toUserName = chatMap.get(ServerConstants.CHAT_TO_NAME);
        String fromUserName = chatMap.get(ServerConstants.CHAT_FROM_NAME);
        String chatText = chatMap.get(ServerConstants.CHAT_TEXT);

        //Storing Chat in DB
        //Changinf the values according to Reciver
        ChatBean chatBean = new ChatBean();
        chatBean.setChatName(fromUserName);
        chatBean.setChatToPhoneNumber(fromUserPhoneNumber);
        chatBean.setChatToName(fromUserName);
        chatBean.setChatFromPhoneNumber(toUserPhoneNumber);
        chatBean.setChatFromName(toUserName);

        ChatDBAdapter chatDBAdapter = new ChatDBAdapter(mContext);


        long id = chatDBAdapter.insert(DBConstants.CHAT_LIST_TABLE_NAME, chatBean.contentValues());

        //Toast on successful row insertion
        if (id < 0) {
//            Message.message(mContext, "Chat Creation Error...!!!");
//            Message.messageBold(mContext, "Chat ", "" + chatBean.getChatName(), "already exists");
            //startActivity(new Intent(this, HomePageActivity_NO_USE.class));
            //CreateUnKnownExpenseGroup.this.finish();
            Log.e("TABLE NOT CREATED", "TABLE NOT CREATED");
        } else {

            Log.e("TABLE CREATED", "TABLE CREATED");
        }

        // Message.messageCenter(mContext, "Starting chat with " + toUserName);

        //Creating CHAT table before starting Actual Chatting
        ConversationDBAdapter conversationDBAdapter = new ConversationDBAdapter(mContext);
        conversationDBAdapter.callCreateTable(fromUserName.replaceAll("\\s+", ""));
        //*****************************************************


        //**********Now Storing CHAT TEXT in DB
        ConversationBean conversationBean = new ConversationBean();
        conversationBean.setConversationText(chatText);
        conversationBean.setIsIncomingOrOutgoingConversation(DBConstants.CONVERSATION_OTHER);

        id = conversationDBAdapter.insert(fromUserName.replaceAll("\\s+", ""), conversationBean.contentValues());

        result = true;

        //Send Notification
        sendNotificationOnChatReceived(fromUserName, chatText);


        return result;
    }

    @SuppressLint("LongLogTag")
    public void sendNotificationOnChatReceived(String fromUserName, String chatText) {
        Log.e("sendNotificationOnChatReceived", "Called");

        Intent intent = new Intent(mContext, DownFromServer.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(mContext, 0,
                intent, PendingIntent.FLAG_ONE_SHOT);
        // Custom Notification
        Uri customSoundUri = Uri.parse("android.resource://"
                + "com.app.splitCash" + "/" + R.raw.notification);

        // Uri defaultSoundUri = RingtoneManager
        // .getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(
                mContext);

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            notificationBuilder.setSmallIcon(R.drawable.ic_launcher)
                    .setContentTitle(fromUserName)
                    .setContentText(chatText)
                    .setAutoCancel(true).setSound(customSoundUri)
                    .setContentIntent(pendingIntent);

        } else {

            Resources res = mContext.getResources();
            notificationBuilder
                    .setSmallIcon(R.drawable.ic_launcher_small)
                    .setLargeIcon(
                            BitmapFactory.decodeResource(res,
                                    R.drawable.ic_launcher))
                    .setContentTitle(fromUserName)
                    .setContentText(chatText)
                    .setAutoCancel(true)
                    .setSound(customSoundUri)
                    .setContentIntent(pendingIntent)
                    .setColor(mContext.getResources().getColor(R.color.primary));
        }

        NotificationManager notificationManager = (NotificationManager) mContext
                .getSystemService(Context.NOTIFICATION_SERVICE);

        notificationManager.notify(0 /* ID of notification */,
                notificationBuilder.build());

        return;


    }

    @SuppressLint("LongLogTag")
    private void sendNotificationOnMemberDelete_onSameDevice(String groupName,
                                                             String groupAdmin) {
        Log.e("sendNotificationOnMemberDelete", "Called");

        Intent intent = new Intent(mContext, DownFromServer.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(mContext, 0,
                intent, PendingIntent.FLAG_ONE_SHOT);
        // Custom Notification
        Uri customSoundUri = Uri.parse("android.resource://"
                + "com.app.splitCash" + "/" + R.raw.notification);

        // Uri defaultSoundUri = RingtoneManager
        // .getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(
                mContext);

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            notificationBuilder.setSmallIcon(R.drawable.ic_launcher)
                    .setContentTitle(groupName)
                    .setContentText(groupAdmin + " deleted you")
                    .setAutoCancel(true).setSound(customSoundUri)
                    .setContentIntent(pendingIntent);

        } else {

            Resources res = mContext.getResources();
            notificationBuilder
                    .setSmallIcon(R.drawable.ic_launcher_small)
                    .setLargeIcon(
                            BitmapFactory.decodeResource(res,
                                    R.drawable.ic_launcher))
                    .setContentTitle(groupName)
                    .setContentText(groupAdmin + " deleted you")
                    .setAutoCancel(true)
                    .setSound(customSoundUri)
                    .setContentIntent(pendingIntent)
                    .setColor(mContext.getResources().getColor(R.color.primary));
        }

        NotificationManager notificationManager = (NotificationManager) mContext
                .getSystemService(Context.NOTIFICATION_SERVICE);

        notificationManager.notify(0 /* ID of notification */,
                notificationBuilder.build());

        return;

    }

    private void sendNotificationOnMemberDelete_onOtherDevices(
            String groupName, String groupMemberName, String groupAdmin) {
        Log.e("sendNotificationOnMemberDelete", "Called");

        Intent intent = new Intent(mContext, DownFromServer.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(mContext, 0,
                intent, PendingIntent.FLAG_ONE_SHOT);
        // Custom Notification
        Uri customSoundUri = Uri.parse("android.resource://"
                + "com.app.splitCash" + "/" + R.raw.notification);

        // Uri defaultSoundUri = RingtoneManager
        // .getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(
                mContext);

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            notificationBuilder.setSmallIcon(R.drawable.ic_launcher)
                    .setContentTitle(groupName)
                    .setContentText(groupAdmin + " deleted " + groupMemberName)
                    .setAutoCancel(true).setSound(customSoundUri)
                    .setContentIntent(pendingIntent);

        } else {

            Resources res = mContext.getResources();
            notificationBuilder
                    .setSmallIcon(R.drawable.ic_launcher_small)
                    .setLargeIcon(
                            BitmapFactory.decodeResource(res,
                                    R.drawable.ic_launcher))
                    .setContentTitle(groupName)
                    .setContentText(groupAdmin + " deleted " + groupMemberName)
                    .setAutoCancel(true)
                    .setSound(customSoundUri)
                    .setContentIntent(pendingIntent)
                    .setColor(mContext.getResources().getColor(R.color.primary));
        }

        NotificationManager notificationManager = (NotificationManager) mContext
                .getSystemService(Context.NOTIFICATION_SERVICE);

        notificationManager.notify(0 /* ID of notification */,
                notificationBuilder.build());

        return;

    }

    private void sendNotificationOnAmountUpdate_Added(String updateOwnerName,
                                                      String groupName, String editAmount, String groupMemberName) {
        Log.e("sendNotificationOnGroupAdded", "Called");

        Intent intent = new Intent(mContext, DownFromServer.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(mContext, 0,
                intent, PendingIntent.FLAG_ONE_SHOT);
        // Custom Notification
        Uri customSoundUri = Uri.parse("android.resource://"
                + "com.app.splitCash" + "/" + R.raw.notification);

        // Uri defaultSoundUri = RingtoneManager
        // .getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(
                mContext);

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            notificationBuilder
                    .setSmallIcon(R.drawable.ic_launcher)
                    .setContentTitle(groupName)
                    .setContentText(
                            updateOwnerName + " added " + editAmount + " to "
                                    + groupMemberName).setAutoCancel(true)
                    .setSound(customSoundUri).setContentIntent(pendingIntent);

        } else {

            Resources res = mContext.getResources();
            notificationBuilder
                    .setSmallIcon(R.drawable.ic_launcher_small)
                    .setLargeIcon(
                            BitmapFactory.decodeResource(res,
                                    R.drawable.ic_launcher))
                    .setContentTitle(groupName)
                    .setContentText(
                            updateOwnerName + " added " + editAmount + " to "
                                    + groupMemberName)
                    .setAutoCancel(true)
                    .setSound(customSoundUri)
                    .setContentIntent(pendingIntent)
                    .setColor(mContext.getResources().getColor(R.color.primary));
        }

        NotificationManager notificationManager = (NotificationManager) mContext
                .getSystemService(Context.NOTIFICATION_SERVICE);

        notificationManager.notify(0 /* ID of notification */,
                notificationBuilder.build());

        return;
    }

    private void sendNotificationOnAmountUpdate_Subtracted(
            String updateOwnerName, String groupName, String editAmount,
            String groupMemberName) {
        Log.e("sendNotificationOnGroupAdded", "Called");

        Intent intent = new Intent(mContext, DownFromServer.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(mContext, 0,
                intent, PendingIntent.FLAG_ONE_SHOT);
        // Custom Notification
        Uri customSoundUri = Uri.parse("android.resource://"
                + "com.app.splitCash" + "/" + R.raw.notification);

        // Uri defaultSoundUri = RingtoneManager
        // .getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(
                mContext);

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            notificationBuilder
                    .setSmallIcon(R.drawable.ic_launcher)
                    .setContentTitle(groupName)
                    .setContentText(
                            updateOwnerName + " subtracted " + editAmount
                                    + " from " + groupMemberName)
                    .setAutoCancel(true).setSound(customSoundUri)
                    .setContentIntent(pendingIntent);

        } else {

            Resources res = mContext.getResources();
            notificationBuilder
                    .setSmallIcon(R.drawable.ic_launcher_small)
                    .setLargeIcon(
                            BitmapFactory.decodeResource(res,
                                    R.drawable.ic_launcher))
                    .setContentTitle(groupName)
                    .setContentText(
                            updateOwnerName + " subtracted " + editAmount
                                    + " from " + groupMemberName)
                    .setAutoCancel(true)
                    .setSound(customSoundUri)
                    .setContentIntent(pendingIntent)
                    .setColor(mContext.getResources().getColor(R.color.primary));
        }

        NotificationManager notificationManager = (NotificationManager) mContext
                .getSystemService(Context.NOTIFICATION_SERVICE);

        notificationManager.notify(0 /* ID of notification */,
                notificationBuilder.build());

        return;
    }

    @SuppressLint("LongLogTag")
    private void sendNotificationOnGroupAdded(String groupAdmin,
                                              String groupName) {
        Log.e("sendNotificationOnGroupAdded", "Called");

        Intent intent = new Intent(mContext, DownFromServer.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(mContext, 0,
                intent, PendingIntent.FLAG_ONE_SHOT);
        // Custom Notification
        Uri customSoundUri = Uri.parse("android.resource://"
                + "com.app.splitCash" + "/" + R.raw.notification);

        // Uri defaultSoundUri = RingtoneManager
        // .getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(
                mContext);

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            notificationBuilder.setSmallIcon(R.drawable.ic_launcher)
                    .setContentTitle(groupName)
                    .setContentText(groupAdmin + " added you")
                    .setAutoCancel(true).setSound(customSoundUri)
                    .setContentIntent(pendingIntent);

        } else {

            Resources res = mContext.getResources();
            notificationBuilder
                    .setSmallIcon(R.drawable.ic_launcher_small)
                    .setLargeIcon(
                            BitmapFactory.decodeResource(res,
                                    R.drawable.ic_launcher))
                    .setContentTitle(groupName)
                    .setContentText(groupAdmin + " added you")
                    .setAutoCancel(true)
                    .setSound(customSoundUri)
                    .setContentIntent(pendingIntent)
                    .setColor(mContext.getResources().getColor(R.color.primary));
        }

        NotificationManager notificationManager = (NotificationManager) mContext
                .getSystemService(Context.NOTIFICATION_SERVICE);

        notificationManager.notify(0 /* ID of notification */,
                notificationBuilder.build());

        return;
    }

    private void sendNotificationOnMemberAdded(String groupAdmin,
                                               String groupName) {
        Log.e("sendNotificationOnMemberAdded", "Called");

        Intent intent = new Intent(mContext, DownFromServer.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(mContext, 0,
                intent, PendingIntent.FLAG_ONE_SHOT);
        // Custom Notification
        Uri customSoundUri = Uri.parse("android.resource://"
                + "com.app.splitCash" + "/" + R.raw.notification);

        // Uri defaultSoundUri = RingtoneManager
        // .getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(
                mContext);

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            notificationBuilder.setSmallIcon(R.drawable.ic_launcher)
                    .setContentTitle(groupName)
                    .setContentText(groupAdmin + " added " + memberName)
                    .setAutoCancel(true).setSound(customSoundUri)
                    .setContentIntent(pendingIntent);

        } else {

            Resources res = mContext.getResources();
            notificationBuilder
                    .setSmallIcon(R.drawable.ic_launcher_small)
                    .setLargeIcon(
                            BitmapFactory.decodeResource(res,
                                    R.drawable.ic_launcher))
                    .setContentTitle(groupName)
                    .setContentText(groupAdmin + " added " + memberName)
                    .setAutoCancel(true)
                    .setSound(customSoundUri)
                    .setContentIntent(pendingIntent)
                    .setColor(mContext.getResources().getColor(R.color.primary));
        }

        NotificationManager notificationManager = (NotificationManager) mContext
                .getSystemService(Context.NOTIFICATION_SERVICE);

        notificationManager.notify(0 /* ID of notification */,
                notificationBuilder.build());

        return;
    }

    //WILL USE LATER
    public void saveUserImageOnRegistrationCompletion(byte [] userImage)
    {
        ImageConverterManager imageConverterManager = new ImageConverterManager(mContext);
        Bitmap bitmap = imageConverterManager.getBitmapFromByteArray(userImage);
    }


    public Bitmap receiveImageFromServer(String userImageUrl) {
        Log.d("Inside receiveImageFromServer", "Inside receiveImageFromServer");
        Bitmap bitmap = null;
        try {
            bitmap = BitmapFactory.decodeStream((InputStream) new URL(userImageUrl).getContent());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return bitmap;
    }


}
