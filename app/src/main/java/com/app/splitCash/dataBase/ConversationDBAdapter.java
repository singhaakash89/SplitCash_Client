package com.app.splitCash.dataBase;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.app.splitCash.constants.DBConstants;

/**
 * Created by Aakash Singh on 18-09-2015.
 */
public class ConversationDBAdapter
{
    Context context;
    MasterDBAdapter masterDbAdapter;
    DBConstants dbConstants;
    SQLiteDatabase db;

    public ConversationDBAdapter(Context context)
    {

        this.context = context;
        masterDbAdapter = new MasterDBAdapter(context);
    }


    public long insert(String table_name, ContentValues contentValues)
    {
        db = masterDbAdapter.getDBConstantsHelper().getWritableDatabase();
        long id = db.insert(table_name, null, contentValues);
        return id;
    }


    public Cursor cursor(String tableName)
    {
        db = masterDbAdapter.getDBConstantsHelper().getWritableDatabase();
        Cursor cursor = db.query(tableName, DBConstants.CONVERSATION_TABLE_NAME_STRUCTURE, null, null, null, null, null);
        return cursor;
    }

    public void callCreateTable(String tableName)
    {
        db = masterDbAdapter.getDBConstantsHelper().getWritableDatabase();
        createMemberTable(tableName, db);

    }

    private void createMemberTable(String tableName, SQLiteDatabase db)
    {
        String CONVERSATION_CREATE_TABLE = "CREATE TABLE IF NOT EXISTS " +tableName+ " (" + DBConstants.CONVERSATION_ID + " INTEGER PRIMARY KEY, " + DBConstants.CONVERSATION_UID + " VARCHAR(100), " + DBConstants.CONVERSATION_TEXT+ " VARCHAR(1000), " +DBConstants.CONVERSATION_IMAGE+ " BLOB, " +DBConstants.CONVERSATION_TIME_STAMP + " VARCHAR(20), " +DBConstants.CONVERSATION_IMAGE_AVAILABLE_IN_ROW + " VARCHAR(20), " +DBConstants.CONVERSATION_INCOMING_OR_OUTGOING + " VARCHAR(10), "+DBConstants.CONVERSATION_STATUS+ " VARCHAR(10));";
        db.execSQL(CONVERSATION_CREATE_TABLE);
        //Message.messageCenter(context, "Chat table created");

    }

    public void dropTableIfExists(MasterDBAdapter masterDbAdapter, String tableName)
    {
        db = masterDbAdapter.getDBConstantsHelper().getWritableDatabase();

        String CONVERSATION_TABLE = "DROP TABLE IF EXISTS " +tableName;

        db.execSQL(CONVERSATION_TABLE);

        //Message.message(context, dbConstants.getMONEYTIME_LIST_TABLE_NAME()+ "Table Dropped");

    }

//    public void closeDB()
//    {
//        db.close();
//    }

}
