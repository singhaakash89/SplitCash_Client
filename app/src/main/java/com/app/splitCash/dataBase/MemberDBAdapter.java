package com.app.splitCash.dataBase;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.app.splitCash.constants.DBConstants;

/**
 * Created by Aakash Singh on 15-07-2015.
 */
public class MemberDBAdapter {
    private String groupName;
    MasterDBAdapter masterDbAdapter;
    MemberDBAdapter memberDBAdapter;
    DBConstants dbConstants;
    Context context;


//    private String MEMBER_LIST_DROP_TABLE = "DROP TABLE IF EXISTS " +dbConstants.getMEMBER_LIST_TABLE_NAME();

    public MemberDBAdapter() {
        //Empty Constructor
    }

    public MemberDBAdapter(Context context) {

        this.context = context;
        masterDbAdapter = new MasterDBAdapter(context);
        dbConstants = new DBConstants(context);
    }

    public long insert(String table_name, ContentValues contentValues) {
        //Message.message(context, "Insert TableName Value : "+table_name);
        SQLiteDatabase db = masterDbAdapter.getDBConstantsHelper().getWritableDatabase();
        long id = db.insert(table_name, null, contentValues);

        return id;
    }

    public void callCreateTable(String tableName) {
        SQLiteDatabase db = masterDbAdapter.getDBConstantsHelper().getWritableDatabase();
        memberDBAdapter = new MemberDBAdapter(context);
        memberDBAdapter.createMemberTable(tableName, db);

    }

    public Cursor cursor(Context context, String tableName, String[] columns) {
        SQLiteDatabase db = masterDbAdapter.getDBConstantsHelper().getWritableDatabase();
        memberDBAdapter = new MemberDBAdapter(context);
        memberDBAdapter.createMemberTable(tableName, db);
        Cursor cursor = db.query(tableName, columns, null, null, null, null, null);
        return cursor;
    }

    public Cursor queryCursorWithDBAdapter(Context context, MasterDBAdapter masterDbAdapter, String tableName, String[] columns) {
        SQLiteDatabase db = masterDbAdapter.getDBConstantsHelper().getWritableDatabase();
        memberDBAdapter = new MemberDBAdapter(context);
        memberDBAdapter.createMemberTable(tableName, db);
        Cursor cursor = db.query(tableName, columns, null, null, null, null, null);
        return cursor;
    }


    //Method for Evaluating TotalGroupExpense
    public Cursor getTotalExpenseCursor(MasterDBAdapter masterDbAdapter, String tableName) {
        String[] Column = {DBConstants.MEMBER_LIST_EXPENSE};
        SQLiteDatabase db = masterDbAdapter.getDBConstantsHelper().getWritableDatabase();
        Cursor cursor = db.query(tableName, Column, null, null, null, null, null);
        return cursor;
    }

    //Method for Evaluating PhoneNumber
    public Cursor getPhoneNumber(MasterDBAdapter masterDbAdapter, String tableName) {
        String[] Column = {DBConstants.MEMBER_LIST_PHONE_NUMBER};
        SQLiteDatabase db = masterDbAdapter.getDBConstantsHelper().getWritableDatabase();
        Cursor cursor = db.query(tableName, Column, null, null, null, null, null);
        return cursor;
    }


    public void createMemberTable(String tableName, SQLiteDatabase db) {
        dbConstants.setMEMBER_LIST_TABLE_NAME(tableName);
        //    Message.message(context, "SQLCreate tbName : " +dbConstants.getMEMBER_LIST_TABLE_NAME());

        String MEMBER_LIST_CREATE_TABLE = "CREATE TABLE IF NOT EXISTS " + dbConstants.getMEMBER_LIST_TABLE_NAME() + " (" + DBConstants.MEMBER_LIST_ID + " INTEGER PRIMARY KEY, " + DBConstants.MEMBER_lIST_UID + " VARCHAR(255), " + DBConstants.MEMBER_LIST_NAME + " VARCHAR(255), " + DBConstants.MEMBER_LIST_PHONE_NUMBER + " VARCHAR(255) UNIQUE, " + DBConstants.MEMBER_LIST_EXPENSE + " VARCHAR(255));";
        db.execSQL(MEMBER_LIST_CREATE_TABLE);


    }

//    DELETE FROM table_name
//    WHERE [condition];

    public void deleteRowFromTable(MasterDBAdapter masterDbAdapter, String tableName, String memberPhoneNumber) {
        SQLiteDatabase db = masterDbAdapter.getDBConstantsHelper().getWritableDatabase();
        String Where = DBConstants.MEMBER_LIST_PHONE_NUMBER + " = ?";
        String[] Args = {memberPhoneNumber};
        db.delete(tableName, Where, Args);

    }

    public void editExpenseColumn(MasterDBAdapter masterDbAdapter, String tableName, String memberPhoneNumber, ContentValues contentValues) {
        SQLiteDatabase db = masterDbAdapter.getDBConstantsHelper().getWritableDatabase();
        String Where = DBConstants.MEMBER_LIST_PHONE_NUMBER + " = ?";
        String[] Args = {memberPhoneNumber};
        //db.update(String table, ContentValues values, String whereClause, String[] whereArgs);
        db.update(tableName, contentValues, Where, Args);

    }


    public void dropTableIfExists(MasterDBAdapter masterDbAdapter, String tableName) {
        SQLiteDatabase db = masterDbAdapter.getDBConstantsHelper().getWritableDatabase();

        String MEMBER_LIST_DROP_TABLE = "DROP TABLE IF EXISTS " + tableName;

        db.execSQL(MEMBER_LIST_DROP_TABLE);

        //Message.message(context, dbConstants.getMONEYTIME_LIST_TABLE_NAME()+ "Table Dropped");

    }

}




