package com.app.splitCash.user;


import android.content.ContentValues;

import com.app.splitCash.constants.DBConstants;
import com.app.splitCash.constants.ServerConstants;
import com.app.splitCash.sharedPreferences.SharedPreferenceManager;

public class UserBean {

    private String id;
    private String regId;
    private String userName;
    private String phoneNumber;
    private String userImagePath;
    private byte[] userImageData;

    public UserBean() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getRegId() {
        return regId;
    }

    public void setRegId(String regId) {
        this.regId = regId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public byte[] getUserImageData() {
        return userImageData;
    }

    public void setUserImageData(byte[] userImageData) {
        this.userImageData = userImageData;
    }


    public String getUserImagePath() {
        return userImagePath;
    }

    public void setUserImagePath(String userImagePath) {
        this.userImagePath = userImagePath;
    }


    public ContentValues contentValues() {
        ContentValues contentValues = new ContentValues();
        contentValues.put(DBConstants.USER_GCM_ID, getRegId());
        contentValues.put(DBConstants.USER_NAME, getUserName());
        contentValues.put(DBConstants.USER_PHONE_NUMBER, getPhoneNumber());
        return contentValues;
    }

    public ContentValues contentValuesForImage() {
        ContentValues contentValues = new ContentValues();
        contentValues.put(DBConstants.USER_IMAGE_PATH, getUserImagePath());
        contentValues.put(DBConstants.USER_IMAGE_DATA, getUserImageData());
        return contentValues;
    }

}
