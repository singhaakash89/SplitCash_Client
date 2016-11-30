package com.app.splitCash.chat;

import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.support.v7.app.AlertDialog;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.app.splitCash.R;
import com.app.splitCash.constants.DBConstants;
import com.app.splitCash.dataBase.ChatDBAdapter;
import com.app.splitCash.dataBase.ConversationDBAdapter;
import com.app.splitCash.dataBase.MasterDBAdapter;
import com.app.splitCash.dataBase.Message;
import com.app.splitCash.imageManager.conversion.ImageConverterManager;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by Aakash Singh on 18-09-2015.
 */
public class ChatListCursorAdapter {
    public Context context;
    private MyAdapter myAdapter;
    private ImageConverterManager imageConverterManager;
    private Bitmap bitmap;

    public ChatListCursorAdapter(Context context) {

        this.context = context;

        //Creating ImageManagerConverter class Object as well to show image with each row
        imageConverterManager = new ImageConverterManager(context);

        //*****************Important*******************************8
        //starting point for ListAdapter
        myAdapter = new MyAdapter(context, getChatList());

    }

    //for hiding start chat Textview in Activity
    public Cursor getChatListCursor(Context context) {
        ChatDBAdapter chatDBAdapter = new ChatDBAdapter(context);
        Cursor cursor = chatDBAdapter.cursor(DBConstants.CHAT_TABLE_NAME_STRUCTURE);
        return cursor;
    }

    public ArrayList<ChatBean> getChatList() {

        ChatDBAdapter chatDBAdapter = new ChatDBAdapter(context);
        ArrayList<ChatBean> chatBeanList = new ArrayList<ChatBean>();
        Cursor cursor = chatDBAdapter.cursor(DBConstants.CHAT_TABLE_NAME_STRUCTURE);

        if (cursor != null && cursor.getCount() > 0) {
            cursor.moveToLast();
            while (!cursor.isBeforeFirst()) {
                ChatBean chatBean = createChatListFromCursor(cursor);
                chatBeanList.add(chatBean);
                cursor.moveToPrevious();
            }
            cursor.close();


        }

        return chatBeanList;
    }

    private ChatBean createChatListFromCursor(Cursor cursor) {
        //int id = cursor.getInt(cursor.getColumnIndex(DBConstants.GROUP_LIST_ID));
        //String uId = cursor.getString(cursor.getColumnIndex(DBConstants.GROUP_lIST_UID));

        //Retrieving chatname and  userIamge from DB to show in custom row
        String chatName = cursor.getString(cursor.getColumnIndex(DBConstants.CHAT_NAME));
        String chatNumber = cursor.getString(cursor.getColumnIndex(DBConstants.CHAT_TO_PHONE_NUMBER));
        byte[] displayPhoto = cursor.getBlob(cursor.getColumnIndex(DBConstants.CHAT_TO_USER_IMAGE));

        ChatBean chatBean = new ChatBean();

        chatBean.setChatName(chatName);
        chatBean.setChatToPhoneNumber(chatNumber);
        chatBean.setChatToUserImage(displayPhoto);

        return chatBean;
    }


    public MyAdapter getAdapter() {
        return myAdapter;
    }


    //-------------------------------MyAdapter Inner class----------------------------------------
    public class MyAdapter extends ArrayAdapter<ChatBean> {

        private Context context;
        private ArrayList<ChatBean> chatBeanList;

        private int position;


        public MyAdapter(Context context, ArrayList<ChatBean> chatBeanList) {
            super(context, 0, chatBeanList);
            this.context = context;
            this.chatBeanList = chatBeanList;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            this.position = position;

            final int pos = position;
            final ViewHolder viewHolder;

            if (convertView == null) {
                LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = inflater.inflate(R.layout.chat_list_row_layout, parent, false);

                viewHolder = new ViewHolder(convertView);
                //SetTag() used to save convertView in the memory until its pool is full
                //Once pool is full, it create a new convertView with NULL value
                convertView.setTag(viewHolder);

            } else {
                //Android uses getTag() to retrieve existing convertView in its pool
                viewHolder = (ViewHolder) convertView.getTag();


            }

            //fetching each ELEMENT FROM ArrayList using built-in "get" ArrayList method
            viewHolder.populateFrom(chatBeanList.get(position));


            //dELETE gROUP fUNCTIONALITY
            //ADDING DELETE and Add functionality to List
            ImageView deleteBtn = (ImageView) convertView.findViewById(R.id.deleteChatRow);
//            ImageView addBtn = (ImageView) convertView.findViewById(R.id.addAmountToRow);

            deleteBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    final String chatName = viewHolder.populateName(chatBeanList.get(pos));

                    new AlertDialog.Builder(context)
                            .setTitle("Delete Chat")
                                    //Making part of text as Bold
                                    //setMessage(Html.fromHtml("Hello "+"<b>"+"World"+"</b>"));
                            .setMessage(Html.fromHtml("Do you want to delete <b>" + chatName + "</b> ?"))
                            .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    // continue with delete

                                    //Calling Method of MEMber Adapter to delete the row
                                    new MasterDBAdapter(context).deleteRowFromChatTable(chatName);

                                    //deleting from the List
                                    chatBeanList.remove(pos);

                                    //Also delete conversation table
                                    new ConversationDBAdapter(context).dropTableIfExists(new MasterDBAdapter(context), chatName.replaceAll("\\s+", ""));

//                                    //Also delete group with this name in Member and MoneyTime TABLE=============
//                                    //Member table dropped
//                                    new MemberDBAdapter(context).dropTableIfExists(new MasterDBAdapter(context), chatName.replaceAll("\\s+", ""));
//                                    //Message.message(context, "Member Table Dropped");
//
//                                    //MoneyTime table Dropped
//                                    DBConstants dbConstants = new DBConstants(context);
//                                    String groupNameNew = groupName.replaceAll("\\s+", "").concat(dbConstants.MONEYTIME_LIST_TABLE_NAME);
//                                    dbConstants.setMEMBER_LIST_TABLE_NAME(groupNameNew);
//                                    new MoneyTimeDBAdapter(context).dropTableIfExists(new MasterDBAdapter(context), dbConstants);
//                                    //Message.message(context, "MoneyTime Table Dropped");
//                                    //===============================================================

                                    //Notifying TABLE FOR tHE CHANGE happened
                                    notifyDataSetChanged();
                                    Message.message(context, "" + Html.fromHtml("Chat <b>" + chatName + "</b> deleted "));

                                }
                            })
                            .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    Message.message(context, "Deletion Cancelled");
                                }
                            })
                            .setIcon(android.R.drawable.ic_dialog_alert)
                            .show();


                }
            });


            return convertView;

        }


        //-------------------------------ViewHolder Inner Class------------------------------
        class ViewHolder {
            private TextView chatNameTextView;
            private CircleImageView circleImageView;

            public ViewHolder(View row) {
                //Chat name
                chatNameTextView = (TextView) row.findViewById(R.id.chatNameTextView);
                //CircularImageView for each row
                circleImageView = (CircleImageView) row.findViewById(R.id.circleView);
            }

            void populateFrom(ChatBean chatBean) {


                chatNameTextView.setText(chatBean.getChatName());

                //converting byteArray to Bitmap
                circleImageView.setImageBitmap(imageConverterManager.getBitmapFromByteArray(chatBean.getChatToUserImage()));
            }

            public String populateName(ChatBean chatBean) {
                return chatBean.getChatName();
            }
        }

    }


}
