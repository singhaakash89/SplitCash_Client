package com.app.splitCash.group;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

import android.content.Intent;
import android.database.Cursor;
import android.support.v7.app.AppCompatActivity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import com.app.splitCash.R;
import com.app.splitCash.constants.DBConstants;
import com.app.splitCash.constants.ServerConstants;
import com.app.splitCash.dataBase.MasterDBAdapter;
import com.app.splitCash.dataBase.MemberDBAdapter;
import com.app.splitCash.dataBase.Message;
import com.app.splitCash.postToServer.PostToServer;
import com.app.splitCash.root.HomePageActivity_NO_USE;
import com.app.splitCash.sharedPreferences.SharedPreferenceManager;

public class AddMemberToGroup extends AppCompatActivity
{

	private MemberDBAdapter memberDBAdapter;
	private HomePageActivity_NO_USE homePageActivity;

	Toolbar toolbar;
	EditText memberNameEditText;
	EditText memberExpensedEditText;
	EditText memberPhoneEditText;

	Intent intent;
	private String groupAdmin;
	private String groupAdminPhoneNumber;

	private String groupName;
	private String groupNameWithOutSpace;
	private AddMemberToGroup addMemberToGroup;

	private String groupType;
	private String groupDescription;

	String memberName;
	String memberAmountExpensed;
	String memberPhoneNumber;

	private String otherMemberName;
	private String otherMemberPhoneNumber;
	private String otherMemberExpense;
	
//	//BigDecimal no = new BigDecimal(10); //you can add like this also
//	no = no.add(new BigDecimal(10));
//	System.out.println(no);
	
	private BigDecimal LOCAL_OTHER_MEMBER_NAME_COUNT = new BigDecimal(0.1);
	private BigDecimal LOCAL_OTHER_MEMBER_PHONE_NUMBER_COUNT = new BigDecimal(0.2);
	private BigDecimal LOCAL_OTHER_MEMBER_EXPENSE_COUNT = new BigDecimal(0.3);

	public AddMemberToGroup()
	{
	}

	public String getGroupNameWithOutSpace()
	{
		return groupNameWithOutSpace;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_add_member_to_group);

		// creating object of this class to access getters.
		addMemberToGroup = new AddMemberToGroup();

		// Using SharedPreference to fetch groupNAME value
		groupName = new SharedPreferenceManager(this).getString(String
				.valueOf(DBConstants.groupSharedPrefKey));
		// =================================================

		// ============Removing sPace from table name
		groupNameWithOutSpace = groupName.replaceAll("\\s+", "");

		// ---------ToolBar Implementation-------------
		toolbar = (Toolbar) findViewById(R.id.app_bar);
		// toolbar.setLogo(R.drawable.ic_conversation_icon);
		// toolbar.setTitle("Group Name");
		setSupportActionBar(toolbar);
		// --------------------------------------------

		// ----HomeButtonEnabled-------------------------
		getSupportActionBar().setHomeButtonEnabled(true);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		// ---------------------------------------------

		memberNameEditText = (EditText) findViewById(R.id.memberNameEditText);
		memberExpensedEditText = (EditText) findViewById(R.id.memberExpensedEditText);
		memberPhoneEditText = (EditText) findViewById(R.id.memberPhoneEditText);

	}

	public void addMemberToGroup(View view)
	{
		// for counting No. of Digits
		int count = 0;
		memberName = memberNameEditText.getText().toString();
		memberAmountExpensed = memberExpensedEditText.getText().toString();
		memberPhoneNumber = memberPhoneEditText.getText().toString();

		// ===============Counting No. of
		// Digits=================================================
		for (int i = 0; i < memberAmountExpensed.length(); i++)
		{
			if (Character.isDigit(memberAmountExpensed.charAt(i)))
			{
				count++;
			}
		}
		// =====================================================================================

		if (memberName.replaceAll("\\s+", "").isEmpty())
		{
			Message.message(this, "Please Enter Amount Expensed");
		} else if (memberAmountExpensed.replaceAll("\\s+", "").isEmpty())
		{
			Message.message(this, "Please Enter Amount Expensed");
		} else if (memberPhoneNumber.isEmpty())
		{
			Message.message(this, "Please enter your Number");
		} else if (memberPhoneNumber.length() != 10)
		{
			Message.message(this,
					"Please enter your 10-digits Number correctly");
		} else if (count >= 8)
		{
			Message.message(this, "Please Enter Amount Within 7 Digits Only");
		} else
		{
			// setting member details
			MemberBean memberBean = new MemberBean();

			memberBean.setUid(memberBean.getRandomUID());
			memberBean.setMemberName(memberName);
			memberBean.setMemberPhoneNumber(memberPhoneNumber);
			memberBean.setMemberAmountExpensed(memberAmountExpensed);

			memberDBAdapter = new MemberDBAdapter(this);
			// Message.message(this, "Add_group_Activity TABLE : " +groupName);
			long id = memberDBAdapter.insert(groupNameWithOutSpace,
					memberBean.contentValues());

			// Toast on successful row insertion
			if (id < 0)
			{
				Message.message(this, "Error - Insertion Error");
				Message.message(this, "Member already exists...!!!");
				// startActivity(new Intent(this, Group.class));
				AddMemberToGroup.this.finish();
			} else
			{
				Message.message(this, "Member Added Successfully");

				// *****************Fetching Group
				// Type/Description**********************
				MasterDBAdapter masterDbAdapter = new MasterDBAdapter(getApplicationContext());
				Cursor cursor = masterDbAdapter
						.getGroupTypeAndDescriptionCursor(groupName);

				cursor.moveToFirst();

				if (cursor != null && cursor.getCount() > 0)
				{
					groupType = cursor.getString(cursor
							.getColumnIndex(DBConstants.GROUP_LIST_TYPE));
					groupDescription = cursor
							.getString(cursor
									.getColumnIndex(DBConstants.GROUP_LIST_DESCRIPTION));

					Log.e("groupType(cursor) = ", "" + groupType);

					Log.e("groupDescription(cursor) = ", "" + groupDescription);
				} else
				{
					Log.e("Cursor is NuLL", "Cursor is NuLL");
					groupType = "N/A";
					groupDescription = "N/A";
				}

				// **********************************************************************

				// ************Fectchinh Group Admin*******************
				SharedPreferenceManager sharedPreferenceManager = new SharedPreferenceManager(
						getApplicationContext());
				groupAdmin = sharedPreferenceManager
						.getString(ServerConstants.GROUP_ADMIN);
				groupAdminPhoneNumber = sharedPreferenceManager
						.getString(ServerConstants.GROUP_ADMIN_PHONE_NUMBER);

				Log.e("groupAdmin = ", "" + groupAdmin);
				Log.e("groupAdminPhoneNumber = ", "" + groupAdminPhoneNumber);
				// ****************************************************
				
			
				// ******Send Member details to Server for Post************
				// ******Using Async Task****************

				// Posting request For registration
				// Using Async Task For Posting And Registration
				AsyncTask<Void, Void, Boolean> mPostTask = new AsyncTask<Void, Void, Boolean>()
				{

					@Override
					protected Boolean doInBackground(Void... params)
					{
						
						// ******************Fetching Other Members of the
						// Group***********************************
						Map<String, String> memberMap = new HashMap<String, String>();

//						memberDBAdapter = new MemberDBAdapter(
//								getApplicationContext());

						Cursor cursor = memberDBAdapter.cursor(
								getApplicationContext(), groupNameWithOutSpace,
								DBConstants.MEMBER_LIST_TABLE_STRUCTURE);
						
						cursor.moveToFirst();
						while (!cursor.isAfterLast())
						{
							otherMemberName = cursor
									.getString(cursor
											.getColumnIndex(DBConstants.MEMBER_LIST_NAME));
							
							otherMemberPhoneNumber = cursor
									.getString(cursor
											.getColumnIndex(DBConstants.MEMBER_LIST_PHONE_NUMBER));
							
							otherMemberExpense = cursor
									.getString(cursor
											.getColumnIndex(DBConstants.MEMBER_LIST_EXPENSE));
							
							if (groupAdminPhoneNumber.equals(otherMemberPhoneNumber) == false)
							{
								Log.e("otherMemberName : ", ""+otherMemberName);

								memberMap.put(String.valueOf(LOCAL_OTHER_MEMBER_NAME_COUNT),
										otherMemberName);
								
								memberMap.put(String.valueOf(LOCAL_OTHER_MEMBER_PHONE_NUMBER_COUNT),
										otherMemberPhoneNumber);
								
								memberMap.put(String.valueOf(LOCAL_OTHER_MEMBER_EXPENSE_COUNT),
										otherMemberExpense);
	
								
								Log.e("memberMap : ", ""+memberMap.get(String.valueOf(LOCAL_OTHER_MEMBER_NAME_COUNT)));
								

								//Incrementing BigDecimal
								LOCAL_OTHER_MEMBER_NAME_COUNT = LOCAL_OTHER_MEMBER_NAME_COUNT.add(new BigDecimal(1));
								
								LOCAL_OTHER_MEMBER_PHONE_NUMBER_COUNT = LOCAL_OTHER_MEMBER_PHONE_NUMBER_COUNT.add(new BigDecimal(1));
								
								LOCAL_OTHER_MEMBER_EXPENSE_COUNT = LOCAL_OTHER_MEMBER_EXPENSE_COUNT.add(new BigDecimal(1));

							}
							cursor.moveToNext();
						}

						cursor.close();
						// ****************************************************************************************

						
						
						
						Map<String, String> postMap = new HashMap<String, String>();

						postMap.put(ServerConstants.GROUP_ADMIN, groupAdmin);
						postMap.put(ServerConstants.GROUP_ADMIN_PHONE_NUMBER,
								groupAdminPhoneNumber);

						postMap.put(ServerConstants.GROUP_NAME, groupName);
						postMap.put(ServerConstants.GROUP_TYPE, groupType);
						postMap.put(ServerConstants.GROUP_DESCRIPTION,
								groupDescription);
						postMap.put(ServerConstants.MEMBER_NAME, memberName);
						postMap.put(ServerConstants.MEMBER_EXPENSE,
								memberAmountExpensed);
						postMap.put(ServerConstants.MEMBER_PHONE_NUMBER,
								memberPhoneNumber);

						PostToServer postToServer = new PostToServer();

						// ***********************Merging
						// MAPS***********************
						postMap.putAll(memberMap);
						
						//Decrementing LOCAL_OTHER_MEMBER_NAME_COUNT
						LOCAL_OTHER_MEMBER_NAME_COUNT = LOCAL_OTHER_MEMBER_NAME_COUNT.add(new BigDecimal(-1));
						
						Log.e("postMap : ", ""+postMap.get(String.valueOf(LOCAL_OTHER_MEMBER_NAME_COUNT)));
						// **********************************************************

						boolean postResult = postToServer.sendToServer(postMap);
						Log.e("PostToServer = ", "" + postResult);
						return postResult;
					}

					@Override
					protected void onPostExecute(Boolean result)
					{
						if (result)
						{
							Message.message(getApplicationContext(),
									"Post Successful");

						} else
						{
							Message.message(getApplicationContext(),
									"Post UnSuccessful");

							Log.e("updateUI() = ", "UnSuccessful");
						}

					}

				};

				mPostTask.execute();

				// ********************************************************

				AddMemberToGroup.this.finish();
			}

		}
	}

//	public boolean onCreateOptionsMenu(Menu menu)
//	{
//		// Inflate the menu; this adds items to the action bar if it is present.
//		MenuInflater inflater = getMenuInflater();
//		inflater.inflate(R.menu.menu_add_member_to_group, menu);
//		return true;
//
//	}
//
//	@Override
//	public boolean onOptionsItemSelected(MenuItem item)
//	{
//		int itemId = item.getItemId();
//		if (itemId == R.id.settings)
//		{
//			Toast.makeText(getBaseContext(), R.string.toast_message_setting,
//					Toast.LENGTH_SHORT).show();
//			return false;
//		} else if (itemId == R.id.contactus)
//		{
//			Toast.makeText(getBaseContext(), R.string.toast_message_contactus,
//					Toast.LENGTH_SHORT).show();
//			return false;
//		} else if (itemId == R.id.about)
//		{
//			Toast.makeText(getBaseContext(), R.string.toast_message_about,
//					Toast.LENGTH_SHORT).show();
//			return false;
//		} else
//		{
//			return super.onOptionsItemSelected(item);
//		}
//
//	}

	@Override
	public void onBackPressed()
	{
		super.onBackPressed();
		// startActivity(new Intent(this, Group.class));
		AddMemberToGroup.this.finish();
	}

}
