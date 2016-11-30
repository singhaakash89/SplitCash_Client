/**
 * Copyright 2015 Google Inc. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.app.splitCash.gcmRegister;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

import android.annotation.SuppressLint;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.text.Html;
import android.util.Base64;
import android.util.Log;
import android.widget.ListView;

import com.app.splitCash.R;
import com.app.splitCash.constants.DBConstants;
import com.app.splitCash.constants.ServerConstants;
import com.app.splitCash.dataBase.Message;
import com.app.splitCash.downFromServer.DownFromServer;
import com.app.splitCash.postToServer.PostToServer;
import com.app.splitCash.sharedPreferences.SharedPreferenceManager;
import com.google.android.gms.gcm.GcmListenerService;

public class MyGcmListenerService extends GcmListenerService
{

	private static final String TAG = "MyGcmListenerService";

	/**
	 * Called when message is received.
	 *
	 * @param from
	 *            SenderID of the sender.
	 * @param data
	 *            Data bundle containing message data as key/value pairs. For
	 *            Set of keys use data.keySet().
	 */
	// [START receive_message]
	@SuppressLint("LongLogTag")
	@Override
	public void onMessageReceived(String from, Bundle data)
	{
		//Log
		Log.d("onMessageReceived(Aakash Singh) : ", "Reached here");

		// DEFINING SERVER MOOD
		final String serverMood_CreateGroup_Or_AddMember = "serverMood_CreateGroup_Or_AddMember";

		// DEFINING SERVER MOOD
		final String serverMood_EditAmount = "serverMood_EditAmount";

		// DEFINING SERVER MOOD
		final String serverMood_deleteMember = "serverMood_deleteMember";

		//DEFINING SERVER MOOD
		final String serverMood_chat = "serverMood_chat";

		//DEFINING SERVER MOOD
		final String serverMood_userImage = "serverMood_userImageURL";


		// Fetching Server Mood
		final String serverMood = data.getString(ServerConstants.SERVER_MOOD);
		Log.d("serverMood(GCMListener)", ""+serverMood);

		if (serverMood.equals(serverMood_CreateGroup_Or_AddMember))
		{

			// *************************************************************************************************
			// ****************Code for GroupCreation n AddingMembers To
			// Group**********************************
			// *************************************************************************************************

			BigDecimal LOCAL_OTHER_MEMBER_NAME_COUNT = new BigDecimal(0.1);
			BigDecimal LOCAL_OTHER_MEMBER_PHONE_NUMBER_COUNT = new BigDecimal(
					0.2);
			BigDecimal LOCAL_OTHER_MEMBER_EXPENSE_COUNT = new BigDecimal(0.3);

			final String groupAdmin = data
					.getString(ServerConstants.GROUP_ADMIN);
			final String groupAdminPhoneNumber = data
					.getString(ServerConstants.GROUP_ADMIN_PHONE_NUMBER);

			final String groupName = data.getString(ServerConstants.GROUP_NAME);
			final String groupType = data.getString(ServerConstants.GROUP_TYPE);
			final String groupDescription = data
					.getString(ServerConstants.GROUP_DESCRIPTION);

			// Commenting coz use memberName from MemberNumber sent in Message
			// from
			// Server
			// final String memberName =
			// data.getString(ServerConstants.MEMBER_NAME);
			final String memberExpense = data
					.getString(ServerConstants.MEMBER_EXPENSE);
			final String memberPhoneNumber = data
					.getString(ServerConstants.MEMBER_PHONE_NUMBER);

			// Fetch other Members from Bundle using MemberNumber
			Map<BigDecimal, String> memberMap = new HashMap<BigDecimal, String>();

			try
			{
				while (data.getString(
						String.valueOf(LOCAL_OTHER_MEMBER_NAME_COUNT)).equals(
						null) == false)
				{
					memberMap.put(LOCAL_OTHER_MEMBER_NAME_COUNT, data
							.getString(String
									.valueOf(LOCAL_OTHER_MEMBER_NAME_COUNT)));

					memberMap
							.put(LOCAL_OTHER_MEMBER_PHONE_NUMBER_COUNT,
									data.getString(String
											.valueOf(LOCAL_OTHER_MEMBER_PHONE_NUMBER_COUNT)));

					memberMap
							.put(LOCAL_OTHER_MEMBER_EXPENSE_COUNT,
									data.getString(String
											.valueOf(LOCAL_OTHER_MEMBER_EXPENSE_COUNT)));

					Log.e("LOCAL_OTHER_MEMBER_NAME_COUNT : ",
							""
									+ data.getString(String
											.valueOf(LOCAL_OTHER_MEMBER_NAME_COUNT)));
					Log.e("LOCAL_OTHER_MEMBER_PHONE_NUMBER_COUNT : ",
							""
									+ data.getString(String
											.valueOf(LOCAL_OTHER_MEMBER_PHONE_NUMBER_COUNT)));
					Log.e("LOCAL_OTHER_MEMBER_EXPENSE_COUNT : ",
							""
									+ data.getString(String
											.valueOf(LOCAL_OTHER_MEMBER_EXPENSE_COUNT)));

					// Incrementing BigDecimal
					LOCAL_OTHER_MEMBER_NAME_COUNT = LOCAL_OTHER_MEMBER_NAME_COUNT
							.add(new BigDecimal(1));

					LOCAL_OTHER_MEMBER_PHONE_NUMBER_COUNT = LOCAL_OTHER_MEMBER_PHONE_NUMBER_COUNT
							.add(new BigDecimal(1));

					LOCAL_OTHER_MEMBER_EXPENSE_COUNT = LOCAL_OTHER_MEMBER_EXPENSE_COUNT
							.add(new BigDecimal(1));

				}

			} catch (NullPointerException ne)
			{
				ne.printStackTrace();
			} finally
			{

				Log.e(TAG, "groupAdmin: " + groupAdmin);
				Log.e(TAG, "groupAdminPhoneNumber: " + groupAdminPhoneNumber);

				Log.e(TAG, "groupName: " + groupName);
				Log.e(TAG, "groupType: " + groupType);
				Log.e(TAG, "groupDescription: " + groupDescription);

				// Log.e(TAG, "memberName: " + memberName);
				Log.e(TAG, "memberExpense: " + memberExpense);

				// Creating Group in Device Remotely
				AsyncTask<Map<BigDecimal, String>, Void, Boolean> mPostTask = new AsyncTask<Map<BigDecimal, String>, Void, Boolean>()
				{

					@Override
					protected Boolean doInBackground(
							Map<BigDecimal, String>... memberMap)
					{
						final Map<BigDecimal, String> memberMapNew = memberMap[0];

						Map<String, String> userMap = new HashMap<String, String>();

						userMap.put(ServerConstants.GROUP_ADMIN, groupAdmin);
						userMap.put(ServerConstants.GROUP_ADMIN_PHONE_NUMBER,
								groupAdminPhoneNumber);

						userMap.put(ServerConstants.GROUP_NAME, groupName);
						userMap.put(ServerConstants.GROUP_TYPE, groupType);
						userMap.put(ServerConstants.GROUP_DESCRIPTION,
								groupDescription);
						// userMap.put(ServerConstants.MEMBER_NAME, memberName);
						userMap.put(ServerConstants.MEMBER_EXPENSE,
								memberExpense);
						userMap.put(ServerConstants.MEMBER_PHONE_NUMBER,
								memberPhoneNumber);

						Log.e("Before Object Creation = ",
								"Before Object Creation");
						DownFromServer downFromServer = new DownFromServer(
								getApplicationContext());

						Log.e("Before GroupCreated = ", "Before GroupCreated");
						boolean result = downFromServer.createGroupInDevice(
								userMap, memberMapNew);
						Log.e("After GroupCreated = ", "" + result);

						if (result == true)
						{
							// Sending Notification to Device
							// sendNotification(groupName);
						}
						return result;
					}

					@Override
					protected void onPostExecute(Boolean result)
					{
						if (result)
						{
							Message.message(getApplicationContext(),
									"GroupCreation Successful");

							// storing in sharedPreference for uses in
							// GroupUnknwon.java
							// to disable "addMemberToGroup" from ActionBar for
							// Invited Group
							SharedPreferenceManager sharedPreferenceManager = new SharedPreferenceManager(
									getApplicationContext());
							sharedPreferenceManager.putString(groupName,
									groupName);
							// **********************************************************************************************************

							Log.e("GroupCreation = ", "Successful");

						} else
						{
							Message.message(getApplicationContext(),
									"GroupCreation UnSuccessful");

							Log.e("GroupCreation = ", "UnSuccessful");
						}

					}

				};

				mPostTask.execute(memberMap);
			}

		} else if (serverMood.equals(serverMood_EditAmount))
		{
			// *************************************************************************************************
			// ****************Code for EditAmount IN
			// Group**********************************
			// *************************************************************************************************

			final String groupName = data.getString(ServerConstants.GROUP_NAME);
			;
			final String groupMemberPhoneNumber = data
					.getString(ServerConstants.MEMBER_TO_UPDATE);
			final String editAmount = data
					.getString(ServerConstants.MEMBER_AMOUNT_UPDATE);

			// uPDATE operation Identification at CLIENT Side
			final String updateOperation = data
					.getString(ServerConstants.OPERATION_TO_UPDATE);

			final String updateOwnerName = data
					.getString(ServerConstants.UPDATE_OWNER);

			Log.e("groupName(GCM_Listener)", "" + groupName);
			Log.e("groupMemberPhoneNumber(GCM_Listener)", ""
					+ groupMemberPhoneNumber);
			Log.e("editAmount(GCM_Listener)", "" + editAmount);
			Log.e("updateOperation(GCM_Listener)", "" + updateOperation);

			// // For OtherMembers Fetching
			// BigDecimal LOCAL_MEMBER_PHONE_NUMBER_COUNT = new BigDecimal(0.4);

			// Updating Member Amount In Device
			AsyncTask<Void, Void, Boolean> mPostTask = new AsyncTask<Void, Void, Boolean>()
			{

				@Override
				protected Boolean doInBackground(Void... args)
				{
					DownFromServer downFromServer = new DownFromServer(
							getApplicationContext());

					boolean result = downFromServer.editAmount(groupName,
							groupMemberPhoneNumber, editAmount,
							updateOperation, updateOwnerName);

					Log.e("After EditAmount = ", "" + result);

					if (result == true)
					{
						// Sending Notification to Device
						// sendNotification(groupName);
					}
					return result;
				}

				@Override
				protected void onPostExecute(Boolean result)
				{
					if (result)
					{
						Message.message(getApplicationContext(),
								"Amount Updated Successfully");
						Log.e("Amount Edited = ", "Successful");

					} else
					{
						Message.message(getApplicationContext(),
								"Amount Update FAILED...!!!");
						Log.e("Amount Edited = ", "UnSuccessful");
					}

				}

			};

			mPostTask.execute();

		} else if (serverMood.equals(serverMood_deleteMember))
		{
			final String groupName = data.getString(ServerConstants.GROUP_NAME);

			final String groupMemberPhoneNumber = data
					.getString(ServerConstants.MEMBER_TO_UPDATE);

			final String groupAdmin = data
					.getString(ServerConstants.GROUP_ADMIN);

			Log.e("groupName(GCM_Listener)", "" + groupName);
			Log.e("groupMemberPhoneNumber(GCM_Listener)", ""
					+ groupMemberPhoneNumber);
			Log.e("GROUP_ADMIN(GCM_Listener)", "" + groupAdmin);

			// Updating Member Amount In Device
			AsyncTask<Void, Void, Boolean> mPostTask = new AsyncTask<Void, Void, Boolean>()
			{

				@Override
				protected Boolean doInBackground(Void... args)
				{
					final String USER_DATA = "MoneyTimePrefsFile";
					final String STRING_DEFAULT = "N/A";

					SharedPreferences sharedPreferences = getApplicationContext()
							.getSharedPreferences(USER_DATA, 0);

					final String ownerPhoneNumber = sharedPreferences
							.getString(DBConstants.USER_PHONE_NUMBER,
									STRING_DEFAULT);

					DownFromServer downFromServer = new DownFromServer(
							getApplicationContext());

					boolean result = downFromServer.deleteMember(
							ownerPhoneNumber, groupName,
							groupMemberPhoneNumber, groupAdmin);

					Log.e("After DeleteMember = ", "" + result);

					if (result == true)
					{
						// Sending Notification to Device
						// sendNotification(groupName);
					}
					return result;
				}

				@Override
				protected void onPostExecute(Boolean result)
				{
					if (result)
					{
						Message.message(getApplicationContext(),
								"Member Deleted Successfully");
						Log.e("Member Deleted = ", "Successful");

					} else
					{
						Message.message(getApplicationContext(),
								"Member Deletion FAILED...!!!");
						Log.e("Member Deleted = ", "UnSuccessful");
					}

				}

			};

			mPostTask.execute();

		} else if (serverMood.equals(serverMood_chat))
		{
			Map<String, String> chatMap = new HashMap<String, String>();

			String chatNameWithNoSpace = data.getString(ServerConstants.CHAT_NAME_WITH_NO_SPACE);
			String toUserPhoneNumber = data.getString(ServerConstants.CHAT_TO_PHONE_NUMBER);
			String fromUserPhoneNumber = data.getString(ServerConstants.CHAT_FROM_PHONE_NUMBER);
			String toUserName = data.getString(ServerConstants.CHAT_TO_NAME);
			String fromUserName = data.getString(ServerConstants.CHAT_FROM_NAME);
			String chatText = data.getString(ServerConstants.CHAT_TEXT);

			Log.e("chatWithNoSpace", "" + chatNameWithNoSpace);
			Log.e("toUserPhoneNumber", "" + toUserPhoneNumber);
			Log.e("fromUserPhoneNumber", "" + fromUserPhoneNumber);
			Log.e("toUserName", "" + toUserName);
			Log.e("fromUserName", "" + fromUserName);
			Log.e("chatText", "" + chatText);

			chatMap.put(ServerConstants.CHAT_NAME_WITH_NO_SPACE, chatNameWithNoSpace);
			chatMap.put(ServerConstants.CHAT_TO_PHONE_NUMBER, toUserPhoneNumber);
			chatMap.put(ServerConstants.CHAT_FROM_PHONE_NUMBER, fromUserPhoneNumber);
			chatMap.put(ServerConstants.CHAT_TO_NAME, toUserName);
			chatMap.put(ServerConstants.CHAT_FROM_NAME, fromUserName);
			chatMap.put(ServerConstants.CHAT_TEXT, chatText);


			// Updating Member Amount In Device
			AsyncTask<Map<String, String>, Void, Boolean> mPostTask = new AsyncTask<Map<String, String>, Void, Boolean>()
			{

				@Override
				protected Boolean doInBackground(Map<String, String>... chatMap)
				{
					final Map<String, String> chatMapNew = chatMap[0];

					DownFromServer downFromServer = new DownFromServer(
							getApplicationContext());

					boolean result = downFromServer.createChatInDevice(chatMapNew);

					Log.e("Chat ReCeived = ", "" + result);

					if (result == true)
					{
						// Sending Notification to Device
						// sendNotification(groupName);
					}
					return result;
				}

				@Override
				protected void onPostExecute(Boolean result)
				{
					if (result)
					{
						Message.message(getApplicationContext(), "Chat Received Successfully");
						Log.e("Member Deleted = ", "Successful");

					} else
					{
						Message.message(getApplicationContext(),
								"Chat Received FAILED...!!!");
						Log.e("Member Deleted = ", "UnSuccessful");
					}

				}

			};

			mPostTask.execute(chatMap);

		}else if(serverMood.equals(serverMood_userImage))
		{
			Log.d("serverMood_userImage(AKS)", "Reached here");

			String userImageURL = data
					.getString(ServerConstants.USER_IMAGE_URL);
			Log.d("USER_IMAGE_URL(from_server)", ""+userImageURL);

			Intent userImageIntent = new Intent(
					"userImage");
			userImageIntent.putExtra(ServerConstants.USER_IMAGE_URL, userImageURL);
			LocalBroadcastManager.getInstance(getApplicationContext())
					.sendBroadcast(userImageIntent);

		}

	}

}