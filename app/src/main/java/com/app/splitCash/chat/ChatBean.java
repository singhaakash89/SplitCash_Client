package com.app.splitCash.chat;

import android.content.ContentValues;

import com.app.splitCash.constants.DBConstants;

import java.util.UUID;

/**
 * Created by Aakash Singh on 18-09-2015.
 */
public class ChatBean
{
    private String uid;
    private String chatName;
    private String chatToPhoneNumber;
    private String chatToName;
    private String chatFromPhoneNumber;
    private String chatFromName;
    private byte[] chatToUserImage;

    public ChatBean()
    {
        setUid(getRandomUID());
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getChatName() {
        return chatName;
    }

    public void setChatName(String chatName) {
        this.chatName = chatName;
    }

    public String getChatToPhoneNumber() {
        return chatToPhoneNumber;
    }

    public void setChatToPhoneNumber(String chatToPhoneNumber) {
        this.chatToPhoneNumber = chatToPhoneNumber;
    }

    public String getChatToName() {
        return chatToName;
    }

    public void setChatToName(String chatToName) {
        this.chatToName = chatToName;
    }

    public String getChatFromPhoneNumber() {
        return chatFromPhoneNumber;
    }

    public void setChatFromPhoneNumber(String chatFromPhoneNumber) {
        this.chatFromPhoneNumber = chatFromPhoneNumber;
    }

    public String getChatFromName() {
        return chatFromName;
    }

    public void setChatFromName(String chatFromName) {
        this.chatFromName = chatFromName;
    }

    public String getRandomUID()
    {
        String uid = UUID.randomUUID().toString();
        return uid;
    }

    public byte[] getChatToUserImage() {
        return chatToUserImage;
    }

    public void setChatToUserImage(byte[] chatToUserImage) {
        this.chatToUserImage = chatToUserImage;
    }

    public ContentValues contentValues()
    {
        ContentValues row = new ContentValues();
        row.put(DBConstants.CHAT_UID, getUid());
        row.put(DBConstants.CHAT_NAME, getChatName());
        row.put(DBConstants.CHAT_TO_PHONE_NUMBER, getChatToPhoneNumber());
        row.put(DBConstants.CHAT_TO_NAME, getChatToName());
        row.put(DBConstants.CHAT_FROM_PHONE_NUMBER, getChatFromPhoneNumber());
        row.put(DBConstants.CHAT_FROM_NAME, getChatFromName());
        row.put(DBConstants.CHAT_TO_USER_IMAGE, getChatToUserImage());
        return row;
    }


}
