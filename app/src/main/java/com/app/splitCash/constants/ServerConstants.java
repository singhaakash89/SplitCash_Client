package com.app.splitCash.constants;

public class ServerConstants
{

	// -------------PARAMETERS FOR SERVER POST(SAME AS Google App Engine)----------//
	public static final String USER_GCM_ID = "userGcmId";
	public static final String USER_NAME = "userNameForRegistration";
	public static final String USER_PHONE_NUMBER = "phoneNumberForRegistration";
	public static final String USER_IMAGE_DATA = "userImageData";
	public static final String USER_IMAGE_URL = "userImageURL";
	// ---------------------------------------------------//

	// ================== SEVER DETAILS ====================================

	public static final String GROUP_ADMIN = "groupAdmin";
	public static final String GROUP_ADMIN_PHONE_NUMBER = "groupAdminPhoneNumber";

	public static final String GROUP_NAME = "groupName";
	public static final String GROUP_TYPE = "groupType";
	public static final String GROUP_DESCRIPTION = "groupDescription";

	public static final String MEMBER_NAME = "memberName";
	// public static final String MEMBER_GROUP_NAME = "memberGroupName";
	public static final String MEMBER_EXPENSE = "memberExpense";
	public static final String MEMBER_PHONE_NUMBER = "memberPhoneNumber";

	public static final String NA = "N/A";
	public static int MEMBER_NUMBER = 0;
	
	public static final String MEMBER_AMOUNT_UPDATE = "memberAmountUpdate";
	public static final String MEMBER_TO_UPDATE = "memberToUpdate";
	
	
	//Constant for operation Identification at SERVER Side
	public static final String OPERATION_TO_UPDATE = "operationToUpdate";
	public static final String ADDITION_AMOUNT_UPDATE = "additionToUpdate";
	public static final String SUBTRACTION_AMOUNT_UPDATE = "subtractionToUpdate";


	//VERY IMPORTANT TO IDENTIFY WHAT SERVER WANTS TO DO AT CLIENT SIDE
	public static final String SERVER_MOOD = "serverMood";

	//TO GET TO KNOW WHO IS UPDATING THE aMOUNT
	public static final String UPDATE_OWNER = "updateOwnerName";


	//Constants For Chat
	public static final String CHAT_STARTED = "chatStarted";
	public static final String CHAT_NAME = "chatName";
	public static final String CHAT_NAME_WITH_NO_SPACE = "chatWithNoSpace";
	public static final String CHAT_TO_PHONE_NUMBER = "chatToPhoneNumber";
	public static final String CHAT_TO_NAME = "chatToName";
	public static final String CHAT_FROM_PHONE_NUMBER = "chatFromPhoneNumber";
	public static final String CHAT_FROM_NAME = "chatFromName";
	public static final String CHAT_TEXT = "chatText";

}
