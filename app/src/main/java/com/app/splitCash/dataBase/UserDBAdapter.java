package com.app.splitCash.dataBase;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.app.splitCash.constants.DBConstants;

public class UserDBAdapter {
    MasterDBAdapter masterDbAdapter;
    MemberDBAdapter memberDBAdapter;
    DBConstants dbConstants;
    Context context;

    public UserDBAdapter(Context context) {

        this.context = context;
        masterDbAdapter = new MasterDBAdapter(context);
        dbConstants = new DBConstants(context);
    }

    public long insert(ContentValues contentValues) {
        //Message.message(context, "Insert TableName Value : "+table_name);
        SQLiteDatabase db = masterDbAdapter.getDBConstantsHelper().getWritableDatabase();
        long id = db.insert(DBConstants.USER_TABLE_NAME, null, contentValues);
        Log.d("Insertion ID (AAKASH SINGH) : ",""+id);
        return id;
    }

    //Not using now instead using sharedprefernce for storing userName/Number
    public void updateUserTable(ContentValues contentValues) {
        SQLiteDatabase db = masterDbAdapter.getDBConstantsHelper().getWritableDatabase();
        String Where = DBConstants.USER_ID + " = ?";
        String[] Args = {"1"};
        //db.update(String table, ContentValues values, String whereClause, String[] whereArgs);
        long id = db.update(DBConstants.USER_TABLE_NAME, contentValues, Where, Args);
        Log.d("Insertion ID (AAKASH SINGH)_DETAILS : ",""+id);
    }

    public void updateUserImage(ContentValues contentValues) {
        SQLiteDatabase db = masterDbAdapter.getDBConstantsHelper().getWritableDatabase();
        String Where = DBConstants.USER_ID + " = ?";
        String[] Args = {"1"};
        //db.update(String table, ContentValues values, String whereClause, String[] whereArgs);
        long id = db.update(DBConstants.USER_TABLE_NAME, contentValues, Where, Args);
        Log.d("Insertion ID (AAKASH SINGH)_IMAGE : ",""+id);
    }


    public void createUserTable() {
        //Internally it will invoke MasterDBAdapter class and DB will be created with all TABLES REQUIRED.
        SQLiteDatabase db = masterDbAdapter.getDBConstantsHelper().getWritableDatabase();
//        Message.message(context, "createUserTable() Called");
    }

//    //Drop UserTable
//    public void dropUserTable()
//    {
//        SQLiteDatabase db = masterDbAdapter.getDBConstantsHelper().getWritableDatabase();
//
//        //CREATE USER TABLE
//        db.execSQL(USER_DROP_TABLE);
//        Message.message(context, "" + DBConstants.USER_TABLE_NAME + " Dropped");
//    }


    public Cursor cursor(Context context, String[] columns) {
        SQLiteDatabase db = masterDbAdapter.getDBConstantsHelper().getWritableDatabase();
        Cursor cursor = db.query(DBConstants.USER_TABLE_NAME, columns, null, null, null, null, null);
        return cursor;
    }


}
