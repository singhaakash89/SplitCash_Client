package com.app.splitCash.imageManager.data_storage;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import com.app.splitCash.constants.DBConstants;
import com.app.splitCash.dataBase.ConversationDBAdapter;
import com.app.splitCash.dataBase.UserDBAdapter;
import com.app.splitCash.imageManager.conversion.ImageConverterManager;
import com.app.splitCash.user.UserBean;

/**
 * Created by Aakash Singh on 22-05-2016.
 */
public class ImageDBManager {

    private Cursor cursor;
    private Context mContext;
    private Bitmap imageBitMap;
    private UserDBAdapter userDBAdapter;
    private ConversationDBAdapter conversationDBAdapter;
    private ImageConverterManager imageConverterManager;

    public ImageDBManager(Context context) {
        mContext = context;
        imageConverterManager = new ImageConverterManager(mContext);
    }

    public Bitmap getBitmap() {
        //********************important****************************
        //Retrieving image fromDB TO SHOW AGAIN TO USER
        userDBAdapter = new UserDBAdapter(mContext);
        Cursor cursor = userDBAdapter.cursor(mContext, DBConstants.USER_TABLE_NAME_STRUCTURE);
        if ((cursor.getCount() > 0) && cursor.isBeforeFirst()) {
            cursor.moveToFirst();
            byte[] image = cursor.getBlob(cursor.getColumnIndex(DBConstants.USER_IMAGE_DATA));
            imageBitMap = BitmapFactory.decodeByteArray(image, 0, image.length);

        }else
        {
            imageBitMap = null;
        }

        return imageBitMap;

    }

    public Bitmap getBitmap(Context mContext, String tableName, String imageColumnName) {
        //********************important****************************

        //Retrieving image fromDB TO SHOW AGAIN TO USER
        switch (tableName) {
            case "conversationTableName" :
                conversationDBAdapter = new ConversationDBAdapter(mContext);
                cursor = conversationDBAdapter.cursor(tableName);
                if ((cursor.getCount() > 0) && cursor.isBeforeFirst()) {
                    cursor.moveToFirst();
                    byte[] image = cursor.getBlob(cursor.getColumnIndex(imageColumnName));
                    imageBitMap = BitmapFactory.decodeByteArray(image, 0, image.length);

                } else {
                    imageBitMap = null;
                }

                break;
            case "userTableName" :
                userDBAdapter = new UserDBAdapter(mContext);
                cursor = userDBAdapter.cursor(mContext, DBConstants.USER_TABLE_NAME_STRUCTURE);
                if ((cursor.getCount() > 0) && cursor.isBeforeFirst()) {
                    cursor.moveToFirst();
                    byte[] image = cursor.getBlob(cursor.getColumnIndex(imageColumnName));
                    imageBitMap = BitmapFactory.decodeByteArray(image, 0, image.length);

                } else {
                    imageBitMap = null;
                }

                break;

            default:
                break;
        }



        return imageBitMap;

    }

    public void storeUserImageIntoDB(Bitmap image, String imagePath)
    {

        //bITMAP TO BYTE[]
        byte[] imageInBytes = imageConverterManager.getByteArrayFromBitMap(image);

        //using setters
        UserBean userBean = new UserBean();
        userBean.setUserImagePath(imagePath);
        userBean.setUserImageData(imageInBytes);

        //setting ContentValues
        ContentValues cv = userBean.contentValuesForImage();

        //CHECKING CURSOR IS BLANK OR NOT, TO DECIDE FOR INSERTION OR UPDATION
        UserDBAdapter userDBAdapter = new UserDBAdapter(mContext);
        Cursor cursor = userDBAdapter.cursor(mContext, DBConstants.USER_TABLE_NAME_STRUCTURE);
        cursor.moveToFirst();

        Log.d("img_value", " " + (cursor.getCount() > 0));

        //Inserting in DB
        if ((cursor.getCount() > 0)) {
            //Incase image already exists in DB
            userDBAdapter = new UserDBAdapter(mContext);
            userDBAdapter.updateUserImage(cv);

        } else {
            //iNSERTING IMAGE FOR THE FIRST TIME IN DB
            userDBAdapter = new UserDBAdapter(mContext);
            userDBAdapter.insert(cv);

        }
        cursor.close();

    }

}
