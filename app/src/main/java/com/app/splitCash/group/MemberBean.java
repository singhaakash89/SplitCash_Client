package com.app.splitCash.group;

import android.content.ContentValues;

import com.app.splitCash.constants.DBConstants;

import java.util.UUID;

/**
 * Created by Aakash Singh on 15-07-2015.
 */
public class MemberBean {
    private String uid;
    private String memberName;
    private String memberPhoneNumber;
    private String memberAmountExpensed;

    public MemberBean() {
        setUid(getRandomUID());
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getMemberName() {
        return memberName;
    }

    public void setMemberName(String memberName) {
        this.memberName = memberName;
    }

    public String getMemberPhoneNumber() {
        return memberPhoneNumber;
    }

    public void setMemberPhoneNumber(String memberPhoneNumber) {
        this.memberPhoneNumber = memberPhoneNumber;
    }

    public String getMemberAmountExpensed() {
        return memberAmountExpensed;
    }

    public void setMemberAmountExpensed(String memberAmountExpensed) {
        this.memberAmountExpensed = memberAmountExpensed;
    }

    public String getRandomUID() {
        String uid = UUID.randomUUID().toString();
        return uid;
    }


    public ContentValues contentValues() {
        ContentValues row = new ContentValues();
        row.put(DBConstants.MEMBER_lIST_UID, getUid());
        row.put(DBConstants.MEMBER_LIST_NAME, getMemberName());
        row.put(DBConstants.MEMBER_LIST_PHONE_NUMBER, getMemberPhoneNumber());
        row.put(DBConstants.MEMBER_LIST_EXPENSE, getMemberAmountExpensed());
        return row;
    }


    public ContentValues contentValuesExpense() {
        ContentValues row = new ContentValues();
        row.put(DBConstants.MEMBER_LIST_EXPENSE, getMemberAmountExpensed());
        return row;
    }


}
