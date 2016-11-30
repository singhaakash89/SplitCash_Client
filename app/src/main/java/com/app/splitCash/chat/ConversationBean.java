package com.app.splitCash.chat;

import android.content.ContentValues;

import com.app.splitCash.constants.DBConstants;

import java.util.UUID;

/**
 * Created by Aakash Singh on 18-09-2015.
 */
public class ConversationBean
{
    private String uid;
    private String conversationText;
    private byte[] conversationImage;
    private String conversationTimeStamp;
    private String isIncomingOrOutgoingConversation;
    private ImageAvailableForRow isImageAvailableInRow;
    private ChatStatus conversationStatus;

    public ConversationBean()
    {
        setUid(getRandomUID());
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getConversationText() {
        return conversationText;
    }

    public void setConversationText(String conversationText) {
        this.conversationText = conversationText;
    }

    public String getIsIncomingOrOutgoingConversation() {
        return isIncomingOrOutgoingConversation;
    }

    public void setIsIncomingOrOutgoingConversation(String isIncomingOrOutgoingConversation) {
        this.isIncomingOrOutgoingConversation = isIncomingOrOutgoingConversation;
    }

    public byte[] getConversationImage() {
        return conversationImage;
    }

    public void setConversationImage(byte[] conversationImage) {
        this.conversationImage = conversationImage;
    }

    public String getConversationTimeStamp() {
        return conversationTimeStamp;
    }

    public void setConversationTimeStamp(String conversationTimeStamp) {
        this.conversationTimeStamp = conversationTimeStamp;
    }

    public ImageAvailableForRow getIsImageAvailableInRow() {
        return isImageAvailableInRow;
    }

    public void setIsImageAvailableInRow(ImageAvailableForRow isImageAvailableInRow) {
        this.isImageAvailableInRow = isImageAvailableInRow;
    }

    public ChatStatus getConversationStatus() {
        return conversationStatus;
    }

    public void setConversationStatus(ChatStatus conversationStatus) {
        this.conversationStatus = conversationStatus;
    }

    public String getRandomUID()
    {
        String uid = UUID.randomUUID().toString();
        return uid;
    }

    public ContentValues contentValues()
    {
        ContentValues row = new ContentValues();
        row.put(DBConstants.CONVERSATION_UID, getUid());
        row.put(DBConstants.CONVERSATION_TEXT, getConversationText());
        row.put(DBConstants.CONVERSATION_IMAGE, getConversationImage());
        row.put(DBConstants.CONVERSATION_TIME_STAMP, getConversationTimeStamp());
        row.put(DBConstants.CONVERSATION_IMAGE_AVAILABLE_IN_ROW, String.valueOf(getIsImageAvailableInRow()));
        row.put(DBConstants.CONVERSATION_INCOMING_OR_OUTGOING, getIsIncomingOrOutgoingConversation());
        row.put(DBConstants.CONVERSATION_STATUS, String.valueOf(getConversationStatus()));
        return row;
    }


}
