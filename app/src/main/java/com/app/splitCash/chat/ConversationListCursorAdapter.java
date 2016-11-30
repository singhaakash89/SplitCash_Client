package com.app.splitCash.chat;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.app.splitCash.imageManager.LruCache_memeory_management.DiskLruImageCache;
import com.app.splitCash.imageManager.LruCache_memeory_management.MemoryLruCache;
import com.app.splitCash.R;
import com.app.splitCash.constants.DBConstants;
import com.app.splitCash.dataBase.ConversationDBAdapter;

import java.util.ArrayList;

/**
 * Created by Aakash Singh on 18-09-2015.
 */
public class ConversationListCursorAdapter {
    private Context context;
    private MyAdapter myAdapter;
    private TextView textView;
    private String conversationTableName;

    public ConversationListCursorAdapter(Context context, String conversationTableName) {
        this.context = context;
        this.conversationTableName = conversationTableName;
        myAdapter = new MyAdapter(context, getConversationList());
    }


//    public Cursor getConversationListCursor(Context context) {
//        ConversationDBAdapter conversationDBAdapter = new ConversationDBAdapter(context);
//        Cursor cursor = conversationDBAdapter.cursor(conversationTableName);
//        return cursor;
//    }

    public ArrayList<ConversationBean> getConversationList() {
//        Log.e(" ", "Inside getChatList");
        ConversationDBAdapter conversationDBAdapter = new ConversationDBAdapter(context);
        ArrayList<ConversationBean> conversationBeanList = new ArrayList<ConversationBean>();
        Cursor cursor = conversationDBAdapter.cursor(conversationTableName);

        if (cursor != null && cursor.getCount() > 0) {
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                ConversationBean conversationBean = createConversationListFromCursor(cursor);
                conversationBeanList.add(conversationBean);
                cursor.moveToNext();
            }
            cursor.close();


        }

        return conversationBeanList;
    }

    private ConversationBean createConversationListFromCursor(Cursor cursor) {
        //int id = cursor.getInt(cursor.getColumnIndex(DBConstants.GROUP_LIST_ID));
        //String uId = cursor.getString(cursor.getColumnIndex(DBConstants.GROUP_lIST_UID));
//        Log.e(" ", "Inside createList");
        String conversationUID = cursor.getString(cursor.getColumnIndex(DBConstants.CONVERSATION_UID));
        String conversationText = cursor.getString(cursor.getColumnIndex(DBConstants.CONVERSATION_TEXT));
        String conversationTimeStamp = cursor.getString(cursor.getColumnIndex(DBConstants.CONVERSATION_TIME_STAMP));
        String isConversationIncomingOrOutgoing = cursor.getString(cursor.getColumnIndex(DBConstants.CONVERSATION_INCOMING_OR_OUTGOING));
        byte[] conversationImage = cursor.getBlob(cursor.getColumnIndex(DBConstants.CONVERSATION_IMAGE));
        String isImageAvailableInRow = cursor.getString(cursor.getColumnIndex(DBConstants.CONVERSATION_IMAGE_AVAILABLE_IN_ROW));
//        Log.d("isImageAvailableInRow :", "" + isImageAvailableInRow);
        String conversationStatus = cursor.getString(cursor.getColumnIndex(DBConstants.CONVERSATION_STATUS));
//        Log.d("conversationStatus :", "" + conversationStatus);

        ConversationBean conversationBean = new ConversationBean();
        conversationBean.setUid(conversationUID);
        conversationBean.setConversationText(conversationText);
        conversationBean.setConversationTimeStamp(conversationTimeStamp);
        conversationBean.setIsIncomingOrOutgoingConversation(isConversationIncomingOrOutgoing);

        conversationBean.setConversationImage(conversationImage);
        conversationBean.setIsImageAvailableInRow(ImageAvailableForRow.valueOf(isImageAvailableInRow));
        conversationBean.setConversationStatus(ChatStatus.valueOf(conversationStatus));
        return conversationBean;
    }


    public MyAdapter getAdapter() {
        return myAdapter;
    }


    //-------------------------------MyAdapter Inner class----------------------------------------
    public class MyAdapter extends ArrayAdapter<ConversationBean> {

        private Context context;
        private ArrayList<ConversationBean> conversationBeanList;
        private ConversationBean conversationBean;

        private String isImageAvailable = null;
        private boolean isImageViewInitialized = false;
        private boolean isImageLayoutRequired = false;
        private boolean isTextLayoutRequired = false;
        private ViewHolder v = null;

        private final String AVAILABLE = "AVAILABLE";
        private final String NOT_AVAILABLE = "NOT_AVAILABLE";

        public MyAdapter(Context context, ArrayList<ConversationBean> conversationBeanList) {
            super(context, 0, conversationBeanList);
            this.context = context;
            this.conversationBeanList = conversationBeanList;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            ViewHolder viewHolder = null;
            conversationBean = conversationBeanList.get(position);
            //Initializing Inflator for INFLATING view
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            Log.d("ConvertView : ", "" + convertView);

            //initializing variable for convertView.getTag() to keep condition statement small in length
            if (convertView != null) {
                v = (ViewHolder) convertView.getTag();
                Log.d("ViewHolder v", "" + v);

                //Doing all operations on convertView to get to know
                //whether inflating view is the appropriate one or NOT?
                isImageAvailable = String.valueOf(conversationBean.getIsImageAvailableInRow());
                isImageViewInitialized = v.getconversationImageView();
                isImageLayoutRequired = (isImageAvailable.equals(AVAILABLE) && isImageViewInitialized == false);
                isTextLayoutRequired = (isImageAvailable.equals(NOT_AVAILABLE) && isImageViewInitialized == true);

            } else {
                isImageAvailable = String.valueOf(conversationBean.getIsImageAvailableInRow());
                isImageLayoutRequired = (isImageAvailable.equals(AVAILABLE) && isImageViewInitialized == false);
                isTextLayoutRequired = (isImageAvailable.equals(NOT_AVAILABLE) && isImageViewInitialized == true);
            }

            isImageAvailable = String.valueOf(conversationBean.getIsImageAvailableInRow());
            Log.d("isImageAvailable", "" + isImageAvailable);

            if (convertView == null || v == null || isImageLayoutRequired || isTextLayoutRequired) {
                if (conversationBean.getIsIncomingOrOutgoingConversation().equals(DBConstants.CONVERSATION_SELF)) {
                    //check if image available for row
                    if (isImageAvailable.equals(AVAILABLE)) {
                        Log.d("", "Inflating Image ");
                        convertView = inflater.inflate(R.layout.conversation_list_image_outgoing_layout, parent, false);
                        viewHolder = new ViewHolder(context, convertView);

                        Log.d("Inflating_position_IMAGE", "" + position);
                        viewHolder.populateImage(conversationBeanList.get(position));

                        //SetTag() used to save convertView in the memory until its pool is full
                        //Once pool is full, it create a new convertView with NULL value
                        convertView.setTag(viewHolder);
                    } else if (isImageAvailable.equals(NOT_AVAILABLE)) {
                        Log.d("", "Inflating Text ");
                        convertView = inflater.inflate(R.layout.conversation_list_item_outgoing_layout, parent, false);
                        viewHolder = new ViewHolder(context, convertView);

                        Log.d("Inflating_position_TEXT", "" + position);
                        viewHolder.populateText(conversationBeanList.get(position));

                        //SetTag() used to save convertView in the memory until its pool is full
                        //Once pool is full, it create a new convertView with NULL value
                        convertView.setTag(viewHolder);
                    }

                } else if (conversationBean.getIsIncomingOrOutgoingConversation().equals(DBConstants.CONVERSATION_OTHER)) {
                    convertView = inflater.inflate(R.layout.conversation_list_item_incoming_layout, parent, false);
                }

            } else {
                //Android uses getTag() to retrieve existing convertView in its pool
                //Using above "ViewHolder v" -> to fetch convertView.get()

                if (isImageViewInitialized == true) {
                    Log.d("Inflating_position_IMAGE - getTag()", "" + position);
                    v.populateImage(conversationBeanList.get(position));

                } else {
                    Log.d("Inflating_position_TEXT - getTag()", "" + position);
                    v.populateText(conversationBeanList.get(position));

                }

            }

            return convertView;

        }


        //-------------------------------ViewHolder Inner Class------------------------------
        class ViewHolder {
            private Context mContext;
            private TextView conversationTextView;
            private TextView conversationTimeTextView;
            private ImageView conversationImageView;

            private boolean imageFlag = false;
            private boolean isDiskCacheChecked = false;
            private String uID;

            private MemoryLruCache memoryLruCache;
            private DiskLruImageCache diskLruImageCache;

            private Bitmap bitmapFromCache;

            public boolean getconversationImageView() {
                if (conversationImageView != null) {
                    imageFlag = true;
                }

                return imageFlag;
            }

            public ViewHolder(Context mContext, View row)
            {
                Log.d("ViewHolder_Constructor : ", "Inside Constructor");

                this.mContext = mContext;
                conversationTextView = (TextView) row.findViewById(R.id.chatText);
                conversationTimeTextView = (TextView) row.findViewById(R.id.chatTime);
                conversationImageView = (ImageView) row.findViewById(R.id.conversationImageView);
                Log.d("from_constructor_conversationImageView : ", "" + conversationImageView);

                memoryLruCache = new MemoryLruCache();
            }

            void populateText(ConversationBean conversationBean) {
//                Log.e(" ", "Inside setText");
                conversationTextView.setText(conversationBean.getConversationText());
                conversationTimeTextView.setText(conversationBean.getConversationTimeStamp());
            }

            void populateImage(final ConversationBean conversationBean) {
                uID = conversationBean.getUid();
                Log.d("uID", " " + uID);

                try
                {
                    //checking in memory cache
                    bitmapFromCache = memoryLruCache.getBitmapFromMemCache(uID);

                    if (bitmapFromCache != null)
                    {
                        Log.d("bitmapFromCache","'''''''''From MemoryCache''''''''");
                        conversationImageView.setImageBitmap(bitmapFromCache);
                        conversationTimeTextView.setText(conversationBean.getConversationTimeStamp());

                    }
                    else
                    {
                        //checking in disk cache
                        //diskLruImageCache = new DiskLruImageCache(mContext, uID, 1024 * 8);
                        //bitmapFromCache = diskLruImageCache.getBitmap(uID);

                        Log.d("bitmapFromCache","'''''''''From DiskCache''''''''" + bitmapFromCache + " : bitmapFromCache");
//                        isDiskCacheChecked = true;
                    }


                    if(bitmapFromCache == null)
                    {
                        // Image loading is slower with another class as constructor takes time to initialize the fields
                        //BitmapWorkerTask bitmapWorkerTask = new BitmapWorkerTask(conversationBean, conversationImageView, conversationTimeTextView);
                        //bitmapWorkerTask.execute(uID);
                        new AsyncTask<String, Void, Bitmap>() {
                            @Override
                            protected void onPreExecute() {
                                super.onPreExecute();
                                Log.d("onPreExecute", "''''''''''Executed''''''''''''''''''");
                                conversationImageView.setImageResource(R.drawable.placeholder);
                            }

                            @Override
                            protected Bitmap doInBackground(String... uID) {
                                Log.d("doInBackground", "''''''''''Executed'''''''''''''''");
                                byte[] image = conversationBean.getConversationImage();
                                Log.d("byte[] image : ", "'''''" + image);
                                Bitmap bitmap = BitmapFactory.decodeByteArray(image, 0, image.length);
                                Log.d("Bitmap bitmap : ", "''''''" + bitmap);
                                //Store it in MemoryLruCache
                                memoryLruCache.addBitmapToMemoryCache(uID[0], bitmap);
//                            //Store it in DiskLruLruCache
                                //diskLruImageCache.putBitmap(uID[0], bitmap);
                                return bitmap;
                            }

                            @Override
                            protected void onPostExecute(Bitmap bitmap) {
                                super.onPostExecute(bitmap);
                                Log.d("onPostExecute", "..................................");
                                Log.d("from_populateImage_conversationImageView : ", "" + conversationImageView);
                                conversationImageView.setImageBitmap(bitmap);
                                conversationTimeTextView.setText(conversationBean.getConversationTimeStamp());
                                //calling garbage collector
                                System.gc();

                            }
                        }.execute(uID);

                    }
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                }
            }

        }


    }


}
