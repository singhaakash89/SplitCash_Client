package com.app.splitCash.group;

import java.util.ArrayList;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.app.splitCash.R;
import com.app.splitCash.constants.DBConstants;
import com.app.splitCash.constants.ServerConstants;
import com.app.splitCash.dataBase.MasterDBAdapter;
import com.app.splitCash.dataBase.MemberDBAdapter;
import com.app.splitCash.sharedPreferences.SharedPreferenceManager;

/**
 * Created by Aakash Singh on 20-07-2015.
 */
public class GroupUnKnown extends AppCompatActivity implements
		OnItemClickListener
{
	private boolean menuFlag = false;
	private boolean isCursorFound = false;
	private boolean totalAmountEqualsGroupAmount = false;
	private float groupExpenseCount;
	private float TotalExpenseCount;
	private TextView TotalMemberCountTextView;
	private TextView leftAmountTextView;
	private TextView emptyUnKnownMemberListTextView;
	private Float leftAmountCount;
	private ListView listView;
	private Toolbar toolbar;
	private Button moneyTimeUnKnownButton;
	private String groupName;
	private String groupNameWithNoSpace;

	private DBConstants dbConstants;
	private GroupUnKnown groupUnKnown;

	private Cursor cursor;

	ViewHolder viewHolder;
	ArrayList<MemberUnKnownBean> memberUnKnownBeanList;
	MemberUnKnownListCursorAdapter memberUnKnownListCursorAdapter;

	public GroupUnKnown()
	{
	}

	public float getTotalExpenseCount()
	{
		return TotalExpenseCount;
	}

	public void setTotalExpenseCount(float totalExpenseCount)
	{
		TotalExpenseCount += totalExpenseCount;
	}

	public void setTotalExpenseCountToZeroForResume()
	{
		TotalExpenseCount = 0.0f;
	}

	@Override
	public void onCreate(Bundle savedInstanceState)
	{

		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_group_unknown);

		// Fetching the value from SharedPreference by creating object of this
		// class
		groupUnKnown = new GroupUnKnown();

		// Using SharedPreference to fetch groupNAME value
		groupName = new SharedPreferenceManager(this).getString(String
				.valueOf(DBConstants.groupSharedPrefKey));
		// =================================================

		// ============Removing sPace from table name
		groupNameWithNoSpace = groupName.replaceAll("\\s+", "");

		// ---------ToolBar Implementation-------------
		toolbar = (Toolbar) findViewById(R.id.app_bar);
		// toolbar.setLogo(R.drawable.ic_conversation_icon);

		// sETTING tILE
		setTitle(groupName);
		setSupportActionBar(toolbar);
		// --------------------------------------------

		// ----HomeButtonEnabled-------------------------
		getSupportActionBar().setHomeButtonEnabled(true);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		// ---------------------------------------------

		// Initializing Butto Element to make it Visible/invisble as Needed
		moneyTimeUnKnownButton = (Button) findViewById(R.id.moneyTimeUnKnownButton);

		// ---------Group List Implementation Using Cursor/Array
		// Adapter---------------------------
		listView = (ListView) findViewById(R.id.groupUnKnownMemberListView);

		// ============Setting DBConstants.Group_table_name===========
		dbConstants = new DBConstants(this);
		dbConstants.setMEMBER_LIST_TABLE_NAME(groupNameWithNoSpace);

		// Creating obj of ListCursorAdapter
		memberUnKnownListCursorAdapter = new MemberUnKnownListCursorAdapter(
				this, dbConstants.getMEMBER_LIST_TABLE_NAME());

		// Hiding emptyMemberListTextView on successful retrieval of data from
		// List
		emptyUnKnownMemberListTextView = (TextView) findViewById(R.id.emptyUnKnownMemberListTextView);
		Cursor isCursor = memberUnKnownListCursorAdapter
				.getMemberListCursor(this);
		if (isCursor != null && isCursor.getCount() > 0)
		{
			emptyUnKnownMemberListTextView.setVisibility(TextView.INVISIBLE);
		} else
		{
			// Inserting Owner as First Member of the
			// Group***********************************
			// setting member details
			MemberDBAdapter memberDBAdapter = new MemberDBAdapter(this);

			MemberBean memberBean = new MemberBean();

			SharedPreferenceManager sharedPreferenceManager = new SharedPreferenceManager(
					this);
			
			
			//Storing GroupAdmin For Future Uses while adding member to groups
			//*********************************************************************************
			String groupAdmin = sharedPreferenceManager
					.getString(DBConstants.USER_NAME);
			
			String groupAdminPhoneNumber = sharedPreferenceManager
					.getString(DBConstants.USER_PHONE_NUMBER);
			
			sharedPreferenceManager.putString(ServerConstants.GROUP_ADMIN, groupAdmin);
			sharedPreferenceManager.putString(ServerConstants.GROUP_ADMIN_PHONE_NUMBER, groupAdminPhoneNumber);
			//*********************************************************************************
			
			memberBean.setUid(memberBean.getRandomUID());
			memberBean.setMemberName(sharedPreferenceManager
					.getString(DBConstants.USER_NAME));
			memberBean.setMemberAmountExpensed("0");
			memberBean.setMemberPhoneNumber(sharedPreferenceManager
					.getString(DBConstants.USER_PHONE_NUMBER));

			// Inserting VALUES
			long id = memberDBAdapter.insert(groupNameWithNoSpace,
					memberBean.contentValues());

			// Toast on successful row insertion
			if (id < 0)
			{
				Log.e("Member Added FAILED...!!!", "Member Added FAILED...!!!");
			} else
			{
				Log.e("Member Added SuccessFully", "Member Added SuccessFully");
				
				Toast.makeText(this, "You have been added as First Member of Group", Toast.LENGTH_LONG).show();
			}

			// *******************************************************************************

			// Hiding emptyMemberListTextView on successful retrieval of data
			// from
			// List
			emptyUnKnownMemberListTextView.setVisibility(TextView.INVISIBLE);
		}

		// Setting ListView For Each Row
		listView.setAdapter(memberUnKnownListCursorAdapter.getAdapter());

		// Setting OnitemClick Listener
		listView.setOnItemClickListener(this);

		// ----------Fetching the TotalExpenseCount (Each Row) for the Group
		// From GroupTable---------------
		// Group group = new Group();
		cursor = new MemberDBAdapter(this).getTotalExpenseCursor(new MasterDBAdapter(
				this), dbConstants.getMEMBER_LIST_TABLE_NAME());

		// Checking whether cursor has sOme Result or Not
		if (cursor != null && cursor.getCount() != 0)
		{
			isCursorFound = true;
		}

		if (isCursorFound)
		{
			// Setting cursor to point the first position in the TABLE
			cursor.moveToFirst();
			while (!cursor.isAfterLast())
			{
				groupUnKnown
						.setTotalExpenseCount(Float.valueOf(cursor.getString(cursor
								.getColumnIndex(DBConstants.MEMBER_LIST_EXPENSE))));
				cursor.moveToNext();
			}

			// Close Cursor In onResume()
			// cursor.close();

			// TotalCount
			TotalMemberCountTextView = (TextView) findViewById(R.id.totalUnKnownTextView);
			TotalMemberCountTextView.setText(String.valueOf(groupUnKnown
					.getTotalExpenseCount()));

			// Setting Unknown GroupTotal Expense
			// row.put(DBConstants.GROUP_LIST_EXPENSE, getExpenseInGroup());
			GroupBean groupBean = new GroupBean();
			groupBean.setExpenseInGroup(String.valueOf(groupUnKnown
					.getTotalExpenseCount()));
			ContentValues cv = groupBean.contentValuesExpense();
			new MasterDBAdapter(this).insertUnknownGroupTotalExpense(groupName, cv);

			// //checking of inserted value
			// //mETHOD TO RETURN THE Cursor holding the EXpense for the Group
			// Cursor cursorNew = new
			// MasterDBAdapter(this).getGroupExpenseCursor(groupName);
			// String GroupAmount = null;
			// cursorNew.moveToFirst();
			// while (!cursorNew.isAfterLast())
			// {
			// GroupAmount =
			// cursor.getString(cursor.getColumnIndex(DBConstants.GROUP_LIST_EXPENSE));
			// cursorNew.moveToNext();
			// }
			// cursorNew.close();
			// Message.message(this, "Expense : " + GroupAmount);
		}
		// ------------------------------------

		if (isCursorFound)
		{
			moneyTimeUnKnownButton.setVisibility(Button.VISIBLE);

			// mAKING aCTIONBAR ICON TO BE SHOWN NOW
			//menuFlag = true;
			//invalidateOptionsMenu();

		}

	}

	// Setting totalUnknownGroupExpense for Home activity
	public void setUnKnownGroupTotalExpense(ContentValues contentValues)
	{

		new MasterDBAdapter(this).insertUnknownGroupTotalExpense(
				dbConstants.getMEMBER_LIST_TABLE_NAME(), contentValues);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		// Inflate the menu; this adds items to the action bar if it is present.
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.menu_group, menu);
		
		//Checking for Invited Group
		SharedPreferenceManager sharedPreferenceManager = new SharedPreferenceManager(getApplicationContext());
		String flag = "";
		flag = sharedPreferenceManager.getString(groupName);
		
		Log.e("flag = ", "" +flag);

		// Menu AddMember Hiding
		if (flag.equals(groupName))
		{
			Log.e("flag(inSide Menu) = ", "" +flag);
			
			menu.findItem(R.id.addMemberToGroup).setEnabled(false);
			menu.findItem(R.id.addMemberToGroup).setVisible(false);
		}
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item)
	{
		int itemId = item.getItemId();
		if (itemId == R.id.settings)
		{
			Toast.makeText(getBaseContext(), R.string.toast_message_setting,
					Toast.LENGTH_SHORT).show();
			return false;
		} else if (itemId == R.id.contactus)
		{
			Toast.makeText(getBaseContext(), R.string.toast_message_contactus,
					Toast.LENGTH_SHORT).show();
			return false;
		} else if (itemId == R.id.about)
		{
			Toast.makeText(getBaseContext(), R.string.toast_message_about,
					Toast.LENGTH_SHORT).show();
			return false;
		} else if (itemId == R.id.addMemberToGroup)
		{
			Toast.makeText(this, "Enter Member Details", Toast.LENGTH_SHORT)
					.show();
			startActivity(new Intent(this, AddMemberToGroup.class));
		
			return false;
		} else if (itemId == R.id.editGroup)
		{
			Toast.makeText(this, "Edit Group", Toast.LENGTH_SHORT).show();
			startActivity(new Intent(this, EditGroup.class));
		
			return false;
		} else
		{
			return super.onOptionsItemSelected(item);
		}

	}

	@Override
	public void onBackPressed()
	{
		super.onBackPressed();
		// startActivity(new Intent(this, HomePageActivity_NO_USE.class));
		GroupUnKnown.this.finish();
	}

	@Override
	protected void onResume()
	{
		super.onResume();

		// Setting TotalExpenseCount To Zero For This onResume Method -- TO
		// AVOID cONFLICTS
		groupUnKnown.setTotalExpenseCountToZeroForResume();
		// isCursorFound = false;

		// Creating obj of ListCursorAdapter
		memberUnKnownListCursorAdapter = new MemberUnKnownListCursorAdapter(
				this, dbConstants.getMEMBER_LIST_TABLE_NAME());

		// Hiding emptyMemberListTextView on successful retrieval of data from
		// List
		emptyUnKnownMemberListTextView = (TextView) findViewById(R.id.emptyUnKnownMemberListTextView);
		Cursor isCursor = memberUnKnownListCursorAdapter
				.getMemberListCursor(this);
		if (isCursor != null && isCursor.getCount() > 0)
		{
			emptyUnKnownMemberListTextView.setVisibility(TextView.INVISIBLE);
		}

		// Setting ListView For Each Row
		listView.setAdapter(memberUnKnownListCursorAdapter.getAdapter());

		// Setting OnitemClick Listener
		listView.setOnItemClickListener(this);

		// ----------Fetching the TotalExpenseCount (Each Row) for the Group
		// From GroupTable---------------
		Cursor cursor = new MemberDBAdapter(this).getTotalExpenseCursor(
				new MasterDBAdapter(this), dbConstants.getMEMBER_LIST_TABLE_NAME());

		// Checking whether cursor has sOme Result or Not
		if (cursor != null && cursor.getCount() != 0)
		{
			isCursorFound = true;
		}

		if (isCursorFound)
		{
			// Setting cursor to point the first position in the TABLE
			cursor.moveToFirst();
			while (!cursor.isAfterLast())
			{
				// --------------------------------------------------------------------------------------------
				// here capture each row expense and subtract from the averge --
				// MoneyTime Activity
				// --------------------------------------------------------------------------------------------
				groupUnKnown
						.setTotalExpenseCount(Integer.valueOf(cursor.getString(cursor
								.getColumnIndex(DBConstants.MEMBER_LIST_EXPENSE))));
				cursor.moveToNext();

			}
			cursor.close();

			// TotalCount
			TotalMemberCountTextView = (TextView) findViewById(R.id.totalUnKnownTextView);
			TotalMemberCountTextView.setText(String.valueOf(groupUnKnown
					.getTotalExpenseCount()));

			// Setting Unknown GroupTotal Expense
			// row.put(DBConstants.GROUP_LIST_EXPENSE, getExpenseInGroup());
			GroupBean groupBean = new GroupBean();
			groupBean.setExpenseInGroup(String.valueOf(groupUnKnown
					.getTotalExpenseCount()));
			ContentValues cv = groupBean.contentValuesExpense();
			new MasterDBAdapter(this).insertUnknownGroupTotalExpense(groupName, cv);

			// //checking of inserted value
			// //mETHOD TO RETURN THE Cursor holding the EXpense for the Group
			// Cursor cursorNew = new
			// MasterDBAdapter(this).getGroupExpenseCursor(groupName);
			// String GroupAmount = null;
			// cursorNew.moveToFirst();
			// while (!cursorNew.isAfterLast())
			// {
			// GroupAmount =
			// cursor.getString(cursor.getColumnIndex(DBConstants.GROUP_LIST_EXPENSE));
			// cursorNew.moveToNext();
			// }
			// cursorNew.close();
			// Message.message(this, "Expense : " + GroupAmount);

		}
		// ------------------------------------

		// Now check Total Amount == Group Amount
		if (isCursorFound)
		{
			moneyTimeUnKnownButton.setVisibility(Button.VISIBLE);
			menuFlag = true;
			invalidateOptionsMenu();
		}

	}

	public void moneyTime(View view)
	{
		startActivity(new Intent(this, MoneyTimeActivity.class));
		// Group.this.finish();
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id)
	{
		viewHolder = new ViewHolder();
		memberUnKnownBeanList = memberUnKnownListCursorAdapter.getMemberList();
		String phoneNumber = viewHolder
				.populatePhoneNumber(memberUnKnownBeanList.get(position));
		// Message.message(this, "PhoneNumber : "+phoneNumber);

		Toast toast = Toast.makeText(getApplicationContext(), "PhoneNumber : "
				+ phoneNumber, Toast.LENGTH_SHORT);
		toast.setGravity(Gravity.CENTER_HORIZONTAL | Gravity.CENTER_VERTICAL,
				0, 0);
		toast.show();

	}

	class ViewHolder
	{
		public String populatePhoneNumber(MemberUnKnownBean memberUnKnownBean)
		{
			return memberUnKnownBean.getMemberPhoneNumber();
		}

	}

}
