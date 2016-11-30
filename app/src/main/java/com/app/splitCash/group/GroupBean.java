package com.app.splitCash.group;

import android.content.ContentValues;

import com.app.splitCash.constants.DBConstants;

import java.util.UUID;

/**
 * Created by Aakash Singh on 14-07-2015.
 */
public class GroupBean
{
    private String uid;
    private String nameGroup;
    private String expenseInGroup;
    private String groupType;
    private String groupDescription;
    private String groupKnownUnknownType;

    public GroupBean()
    {
        setUid(getRandomUID());
    }

    public String getUid()
    {
        return uid;
    }

    public void setUid(String uid)
    {
        this.uid =  uid;
    }

    public String getNameGroup()
    {
        return nameGroup;
    }

    public void setNameGroup(String nameGroup)
    {
        this.nameGroup = nameGroup;
    }

    public String getExpenseInGroup()
    {
        return expenseInGroup;
    }

    public void setExpenseInGroup(String expenseInGroup)
    {
        this.expenseInGroup = expenseInGroup;
    }

    public String getGroupType()
    {
        return groupType;
    }

    public void setGroupType(String groupType)
    {
        this.groupType = groupType;
    }

    public String getGroupDescription()
    {
        return groupDescription;
    }

    public void setGroupDescription(String groupDescription)
    {
        this.groupDescription = groupDescription;
    }

    public String getGroupKnownUnknownType()
    {
        return groupKnownUnknownType;
    }

    public void setGroupKnownUnknownType(String groupKnownUnknownType)
    {
        this.groupKnownUnknownType = groupKnownUnknownType;
    }

    public String getRandomUID()
    {
        String uid = UUID.randomUUID().toString();
        return uid;
    }


    public ContentValues contentValues()
    {
        ContentValues row = new ContentValues();
        row.put(DBConstants.GROUP_lIST_UID, getUid());
        row.put(DBConstants.GROUP_LIST_NAME, getNameGroup());
        row.put(DBConstants.GROUP_LIST_EXPENSE, getExpenseInGroup());
        row.put(DBConstants.GROUP_LIST_TYPE, getGroupType());
        row.put(DBConstants.GROUP_LIST_DESCRIPTION, getGroupDescription());
        row.put(DBConstants.GROUP_LIST_KNOWN_UNKNOWN_TYPE, getGroupKnownUnknownType());
        return row;
    }


    public ContentValues contentValuesExpense()
    {
        ContentValues row = new ContentValues();
        row.put(DBConstants.GROUP_LIST_EXPENSE, getExpenseInGroup());
        return row;
    }



}
