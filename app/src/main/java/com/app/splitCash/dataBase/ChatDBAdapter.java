package com.app.splitCash.dataBase;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.app.splitCash.constants.DBConstants;

/**
 * Created by Aakash Singh on 18-09-2015.
 */
public class ChatDBAdapter
{
    Context context;
    MasterDBAdapter masterDbAdapter;
    DBConstants dbConstants;
    SQLiteDatabase db;

    public ChatDBAdapter(Context context)
    {

        this.context = context;
        masterDbAdapter = new MasterDBAdapter(context);
        dbConstants = new DBConstants(context);
    }


    public long insert(String table_name, ContentValues contentValues)
    {
        db = masterDbAdapter.getDBConstantsHelper().getWritableDatabase();
        long id = db.insert(table_name, null, contentValues);
        return id;
    }


    public Cursor cursor(String[] columns)
    {
        db = masterDbAdapter.getDBConstantsHelper().getWritableDatabase();
        Cursor cursor = db.query(DBConstants.CHAT_LIST_TABLE_NAME, columns, null, null, null, null, null);
        return cursor;
    }


    //mETHOD TO RETURN THE Cursor holding the Known/Unknown Value for the Group
    public Cursor getNameAndImageCursor(String NUMBER)
    {
        String tableName = DBConstants.CHAT_LIST_TABLE_NAME;
        String[] Column = {DBConstants.CHAT_NAME, DBConstants.CHAT_TO_USER_IMAGE}; //COLUMNS U WANT TO BE SELECTED
        String Where = DBConstants.CHAT_TO_PHONE_NUMBER+ " = ?";
        String [] Args = {NUMBER};
        db = masterDbAdapter.getDBConstantsHelper().getWritableDatabase();
        Cursor cursor = db.query(tableName, Column, Where, Args, null, null, null);
        return cursor;
    }


    //method for deleting row from table
    public void deleteRowFromTable(String chatName)
    {
        db = masterDbAdapter.getDBConstantsHelper().getWritableDatabase();
        String Where = DBConstants.CHAT_NAME+ " = ?";
        String [] Args = {chatName};
        db.delete(DBConstants.CHAT_LIST_TABLE_NAME, Where, Args);

    }

//    public void closeDB()
//    {
//        db.close();
//    }


}
