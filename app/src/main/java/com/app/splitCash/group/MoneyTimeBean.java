package com.app.splitCash.group;

import android.content.ContentValues;

import com.app.splitCash.constants.DBConstants;

import java.util.UUID;

/**
 * Created by Aakash Singh on 17-07-2015.
 */
public class MoneyTimeBean
{
    private String uid;
    private String moneyTimeMemberName;
    private String moneyTimeMemberExpense;
    private String moneyTimeMemberToTake;
    private String moneyTimeMemberToGive;

    public MoneyTimeBean()
    {
        setUid(getRandomUID());
    }

    public String getUid()
    {
        return uid;
    }

    public void setUid(String uid)
    {
        this.uid = uid;
    }

    public String getMoneyTimeMemberName()
    {
        return moneyTimeMemberName;
    }

    public void setMoneyTimeMemberName(String moneyTimeMemberName)
    {
        this.moneyTimeMemberName = moneyTimeMemberName;
    }

    public String getMoneyTimeMemberExpense()
    {
        return moneyTimeMemberExpense;
    }

    public void setMoneyTimeMemberExpense(String moneyTimeMemberExpense)
    {
        this.moneyTimeMemberExpense = moneyTimeMemberExpense;
    }

    public String getMoneyTimeMemberToTake()
    {
        return moneyTimeMemberToTake;
    }

    public void setMoneyTimeMemberToTake(String moneyTimeMemberToTake)
    {
        this.moneyTimeMemberToTake = moneyTimeMemberToTake;
    }

    public String getMoneyTimeMemberToGive()
    {
        return moneyTimeMemberToGive;
    }

    public void setMoneyTimeMemberToGive(String moneyTimeMemberToGive)
    {
        this.moneyTimeMemberToGive = moneyTimeMemberToGive;
    }

    public String getRandomUID()
    {
        String uid = UUID.randomUUID().toString();
        return uid;
    }

    public ContentValues contentValues()
    {
        ContentValues row = new ContentValues();
        row.put(DBConstants.MONEYTIME_lIST_UID, getUid());
        row.put(DBConstants.MONEYTIME_LIST_NAME, getMoneyTimeMemberName());
        row.put(DBConstants.MONEYTIME_LIST_EXPENSE, getMoneyTimeMemberExpense());
        row.put(DBConstants.MONEYTIME_LIST_TO_TAKE_AMOUNT, getMoneyTimeMemberToTake());
        row.put(DBConstants.MONEYTIME_LIST_TO_GIVE_AMOUNT, getMoneyTimeMemberToGive());
        return row;
    }

}
