package com.app.splitCash.dataBase;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.app.splitCash.constants.DBConstants;

import java.io.File;

public class MasterDBAdapter
{


    DBConstantsHelper DBConstantsHelper;
    SQLiteDatabase db;

    public MasterDBAdapter(Context context)
    {
        DBConstantsHelper = new DBConstantsHelper(context);
//        Message.message(context, "Constructor Called");
    }

    public DBConstantsHelper getDBConstantsHelper()
    {
        return DBConstantsHelper;
    }


    public long insert(String table_name, ContentValues contentValues)
    {
        //BELOW LINE IS IMPORtant statement for creating DB EVEN
        db = DBConstantsHelper.getWritableDatabase();
        long id = db.insert(table_name, null, contentValues);
        return id;
    }


    public Cursor cursor(String tableName, String[] columns)
    {
        db = DBConstantsHelper.getWritableDatabase();
        Cursor cursor = db.query(tableName, columns, null, null, null, null, null);
        return cursor;
    }
    
    //method returning the cursor holding Type and Description of the Group
    public Cursor getGroupTypeAndDescriptionCursor(String GROUP_LIST_NAME)
    {
        String tableName = DBConstants.GROUP_LIST_TABLE_NAME;
        String[] Column = {DBConstants.GROUP_LIST_TYPE, DBConstants.GROUP_LIST_DESCRIPTION};
        String Where = DBConstants.GROUP_LIST_NAME+ " = ?";
        String [] Args = {GROUP_LIST_NAME};
        db = DBConstantsHelper.getWritableDatabase();
        Cursor cursor = db.query(tableName, Column, Where, Args, null, null, null);
        return cursor;
    }
    

    //mETHOD TO RETURN THE Cursor holding the EXpense for the Group
    public Cursor getGroupExpenseCursor(String GROUP_LIST_NAME)
    {
        String tableName = DBConstants.GROUP_LIST_TABLE_NAME;
        String[] Column = {DBConstants.GROUP_LIST_EXPENSE};
        String Where = DBConstants.GROUP_LIST_NAME+ " = ?";
        String [] Args = {GROUP_LIST_NAME};
        db = DBConstantsHelper.getWritableDatabase();
        Cursor cursor = db.query(tableName, Column, Where, Args, null, null, null);
        return cursor;
    }

    //mETHOD TO RETURN THE Cursor holding the Known/Unknown Value for the Group
    public Cursor getGroupKnownUnknownCursor(String GROUP_LIST_NAME)
    {
        String tableName = DBConstants.GROUP_LIST_TABLE_NAME;
        String[] Column = {DBConstants.GROUP_LIST_KNOWN_UNKNOWN_TYPE}; //COLUMNS U WANT TO BE SELECTED
        String Where = DBConstants.GROUP_LIST_NAME+ " = ?";
        String [] Args = {GROUP_LIST_NAME};
        db = DBConstantsHelper.getWritableDatabase();
        Cursor cursor = db.query(tableName, Column, Where, Args, null, null, null);
        return cursor;
    }

    //method for deleting row from table
    public void deleteRowFromTable(String groupName)
    {
        db = DBConstantsHelper.getWritableDatabase();
        String Where = DBConstants.GROUP_LIST_NAME+ " = ?";
        String [] Args = {groupName};
        db.delete(DBConstants.GROUP_LIST_TABLE_NAME, Where, Args);

    }

    //method for deleting row from table
    public void deleteRowFromChatTable(String chatName)
    {
        db = DBConstantsHelper.getWritableDatabase();
        String Where = DBConstants.CHAT_NAME+ " = ?";
        String [] Args = {chatName};
        db.delete(DBConstants.CHAT_LIST_TABLE_NAME, Where, Args);

    }


    //to insert GroupUnknownTotalExpense
    public void insertUnknownGroupTotalExpense(String memberName , ContentValues values)
    {
        db = DBConstantsHelper.getWritableDatabase();
        String Where = DBConstants.GROUP_LIST_NAME+ " = ?";
        String [] Args = {memberName};
        //db.update(String table, ContentValues values, String whereClause, String[] whereArgs);
        db.update(DBConstants.GROUP_LIST_TABLE_NAME, values, Where, Args);

    }


    public void closeDB()
    {
        db.close();
    }

    static class DBConstantsHelper extends SQLiteOpenHelper
    {
        //Group List Table
        private static final String GROUP_LIST_CREATE_TABLE = "CREATE TABLE " + DBConstants.GROUP_LIST_TABLE_NAME + " (" + DBConstants.GROUP_LIST_ID + " INTEGER PRIMARY KEY, " + DBConstants.GROUP_lIST_UID + " VARCHAR(255), " + DBConstants.GROUP_LIST_NAME + " VARCHAR(255) NOT NULL UNIQUE, " + DBConstants.GROUP_LIST_EXPENSE + " VARCHAR(255), " + DBConstants.GROUP_LIST_TYPE + " VARCHAR(255), " + DBConstants.GROUP_LIST_DESCRIPTION + " VARCHAR(500), " + DBConstants.GROUP_LIST_KNOWN_UNKNOWN_TYPE + " VARCHAR(255));";
        private static final String GROUP_LIST_DROP_TABLE = "DROP TABLE IF EXISTS " + DBConstants.GROUP_LIST_TABLE_NAME;


        //Chat List TABLE
        private static final String CHAT_LIST_CREATE_TABLE = "CREATE TABLE " + DBConstants.CHAT_LIST_TABLE_NAME + " (" + DBConstants.CHAT_ID + " INTEGER PRIMARY KEY, " + DBConstants.CHAT_UID + " VARCHAR(255), " + DBConstants.CHAT_NAME + " VARCHAR(255) NOT NULL UNIQUE," + DBConstants.CHAT_TO_PHONE_NUMBER + " VARCHAR(255) NOT NULL UNIQUE, " + DBConstants.CHAT_TO_NAME + " VARCHAR(255) NOT NULL UNIQUE, " + DBConstants.CHAT_FROM_NAME + " VARCHAR(255), " + DBConstants.CHAT_FROM_PHONE_NUMBER + " VARCHAR(250), " +DBConstants.CHAT_TO_USER_IMAGE + " BLOB);";
        private static final String CHAT_LIST_DROP_TABLE = "DROP TABLE IF EXISTS " + DBConstants.CHAT_LIST_TABLE_NAME;


        //User TABLE
        //private static final String USER_CREATE_TABLE = "CREATE TABLE " + DBConstants.USER_TABLE_NAME + " (" + DBConstants.USER_ID + " INTEGER PRIMARY KEY, " + DBConstants.USER_GCM_ID + " VARCHAR(255) NOT NULL UNIQUE, " + DBConstants.USER_NAME + " VARCHAR(255) NOT NULL UNIQUE, " + DBConstants.USER_PHONE_NUMBER + " VARCHAR(255), " +DBConstants.USER_IMAGE_NAME+ " VARCHAR(255), "+DBConstants.USER_IMAGE_DATA+ " BLOB);";
        private static final String USER_CREATE_TABLE = "CREATE TABLE " + DBConstants.USER_TABLE_NAME + " (" + DBConstants.USER_ID + " INTEGER PRIMARY KEY, " + DBConstants.USER_GCM_ID + " VARCHAR(255) , " + DBConstants.USER_NAME + " VARCHAR(255) , " + DBConstants.USER_PHONE_NUMBER + " VARCHAR(255), " +DBConstants.USER_IMAGE_PATH+ " VARCHAR(255), "+DBConstants.USER_IMAGE_DATA+ " BLOB);";
        private static final String USER_DROP_TABLE = "DROP TABLE IF EXISTS " + DBConstants.USER_TABLE_NAME;

        private Context context;

        public DBConstantsHelper(Context context)
        {
            //super(context, "DBConstantsName", cursor, DBConstantsVersion);
            super(context, DBConstants.DATABASE_NAME, null, DBConstants.DATABASE_VERSION);
            this.context = context;
            //Message.message(context, "Constructor Called");
        }

        @Override
        public void onCreate(SQLiteDatabase db)
        {
            try
            {
//                Message.message(context, "" + DBConstants.DATABASE_NAME + " Created");

                //GROUP_LIST table created
                db.execSQL(GROUP_LIST_CREATE_TABLE);
                //CHAT_LIST table created
                db.execSQL(CHAT_LIST_CREATE_TABLE);
                //USER TABLE created
                db.execSQL(USER_CREATE_TABLE);

//                Message.message(context, "" + DBConstants.GROUP_LIST_TABLE_NAME + " Created");
//                Message.message(context, "" + DBConstants.CHAT_LIST_TABLE_NAME + " Created");
//                Message.message(context, "" + DBConstants.USER_TABLE_NAME + " Created");
                
                Log.d("MasterDBAdapter", "query sent for" + DBConstants.GROUP_LIST_TABLE_NAME);

            } catch (SQLException e)
            {
                Message.message(context, "" + e);
            }

            //----------------DATABASE Path------------------------------------
            File DBConstantspath = context.getDatabasePath(DBConstants.DATABASE_NAME);
            DBConstantspath.setReadable(true);
            //Message.message(context, "DATABASE Path = " +DBConstantspath.toString());
            //Toast.makeText(context, "" + DBConstantspath.toString(), Toast.LENGTH_LONG).show();
            //--------------------------------------------------------------------


        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
        {
            try
            {
                //Drop GroupListTable
                db.execSQL(GROUP_LIST_DROP_TABLE);

                //dROP CHATlISTtABLE
                db.execSQL(CHAT_LIST_DROP_TABLE);

                //DROP user Table
                db.execSQL(USER_DROP_TABLE);
                
                //Creating both the Tables
                onCreate(db);

                Message.message(context, "onUpgrade() Called");
            } catch (SQLException e)
            {

                Message.message(context, "" + e);
            }
        }


    }
}
