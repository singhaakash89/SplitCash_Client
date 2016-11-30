package com.app.splitCash.dataBase;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.app.splitCash.constants.DBConstants;

/**
 * Created by Aakash Singh on 17-07-2015.
 */
public class MoneyTimeDBAdapter {
    private String groupName;
    MasterDBAdapter masterDbAdapter;
    MoneyTimeDBAdapter moneyTimeDBAdapter;
    DBConstants dbConstants;
    Context context;

    public MoneyTimeDBAdapter() {
        //Empty Constructor
    }

    public MoneyTimeDBAdapter(Context context) {

        this.context = context;
        masterDbAdapter = new MasterDBAdapter(context);
        dbConstants = new DBConstants(context);
    }

    public long insert(MasterDBAdapter masterDbAdapter, String table_name, ContentValues contentValues) {
        //Message.message(context, "Insert TableName Value : "+table_name);
        SQLiteDatabase db = masterDbAdapter.getDBConstantsHelper().getWritableDatabase();
        long id = db.insert(table_name, null, contentValues);
        return id;
    }

    public Cursor cursor(Context context, String tableName, String[] columns) {
        SQLiteDatabase db = masterDbAdapter.getDBConstantsHelper().getWritableDatabase();
        moneyTimeDBAdapter = new MoneyTimeDBAdapter(context);
        moneyTimeDBAdapter.createMemberTable(tableName, db);
        Cursor cursor = db.query(tableName, columns, null, null, null, null, null);
        return cursor;
    }

    //This method will be used when this class is accessed o/s its CursorAdapter
    public Cursor queryCursorWithDBAdapter(Context context, MasterDBAdapter masterDbAdapter, String tableName, String[] columns) {
        SQLiteDatabase db = masterDbAdapter.getDBConstantsHelper().getWritableDatabase();
        moneyTimeDBAdapter = new MoneyTimeDBAdapter(context);
        moneyTimeDBAdapter.createMemberTable(tableName, db);
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

    public void createMemberTable(String tableName, SQLiteDatabase db) {
        dbConstants.setMONEYTIME_LIST_TABLE_NAME(tableName);
        //    Message.message(context, "SQLCreate tbName : " +dbConstants.getMEMBER_LIST_TABLE_NAME());

        String MONEYTIME_LIST_CREATE_TABLE = "CREATE TABLE IF NOT EXISTS " + dbConstants.getMONEYTIME_LIST_TABLE_NAME() + " (" + DBConstants.MONEYTIME_LIST_ID + " INTEGER PRIMARY KEY, " + DBConstants.MONEYTIME_lIST_UID + " VARCHAR(255), " + DBConstants.MONEYTIME_LIST_NAME + " VARCHAR(255), " + DBConstants.MONEYTIME_LIST_EXPENSE + " VARCHAR(255), " + DBConstants.MONEYTIME_LIST_TO_TAKE_AMOUNT + " VARCHAR(255), " + DBConstants.MONEYTIME_LIST_TO_GIVE_AMOUNT + " VARCHAR(255));";

        db.execSQL(MONEYTIME_LIST_CREATE_TABLE);
        //Message.message(context, "" +dbConstants.getMONEYTIME_LIST_TABLE_NAME()+ " Created Successfully");

    }

    public void dropTableIfExists(MasterDBAdapter masterDbAdapter, DBConstants dbConstants) {
        SQLiteDatabase db = masterDbAdapter.getDBConstantsHelper().getWritableDatabase();

        String MEMBER_LIST_DROP_TABLE = "DROP TABLE IF EXISTS " + dbConstants.getMONEYTIME_LIST_TABLE_NAME();

        db.execSQL(MEMBER_LIST_DROP_TABLE);

        //Message.message(context, dbConstants.getMONEYTIME_LIST_TABLE_NAME()+ "Table Dropped");

    }

    public void createMoneyTimeTable(MasterDBAdapter masterDbAdapter, DBConstants dbConstants) {
        SQLiteDatabase db = masterDbAdapter.getDBConstantsHelper().getWritableDatabase();

        String MONEYTIME_LIST_CREATE_TABLE = "CREATE TABLE IF NOT EXISTS " + dbConstants.getMONEYTIME_LIST_TABLE_NAME() + " (" + DBConstants.MONEYTIME_LIST_ID + " INTEGER PRIMARY KEY, " + DBConstants.MONEYTIME_lIST_UID + " VARCHAR(255), " + DBConstants.MONEYTIME_LIST_NAME + " VARCHAR(255), " + DBConstants.MONEYTIME_LIST_EXPENSE + " VARCHAR(255), " + DBConstants.MONEYTIME_LIST_TO_TAKE_AMOUNT + " VARCHAR(255), " + DBConstants.MONEYTIME_LIST_TO_GIVE_AMOUNT + " VARCHAR(255));";

        db.execSQL(MONEYTIME_LIST_CREATE_TABLE + "Table Created");

        //Message.message(context, dbConstants.getMONEYTIME_LIST_TABLE_NAME());

    }

}









