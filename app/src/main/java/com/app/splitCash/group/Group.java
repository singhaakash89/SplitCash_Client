package com.app.splitCash.group;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
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
import com.app.splitCash.dataBase.MasterDBAdapter;
import com.app.splitCash.dataBase.MemberDBAdapter;
import com.app.splitCash.dataBase.Message;
import com.app.splitCash.sharedPreferences.SharedPreferenceManager;

import java.util.ArrayList;

/**
 * Created by Aakash Singh on 14-07-2015.
 */
public class Group extends AppCompatActivity implements
		OnItemClickListener
{
	private boolean menuFlag;
	private boolean menuEditGroupTrue;
	private boolean isCursorFound = false;
	private boolean totalAmountEqualsGroupAmount = false;
	private float groupExpenseCount;
	private float TotalExpenseCount;
	private TextView TotalMemberCountTextView;
	private TextView leftAmountTextView;
	TextView emptyMemberListTextView;
	private Float leftAmountCount;
	private ListView listView;
	private Toolbar toolbar;
	private Button moneyTimeButton;
	private String groupName;
	private String groupNameWithNoSpace;

	private DBConstants dbConstants;
	private Group group;

	ViewHolder viewHolder;
	ArrayList<MemberBean> memberBeanList;
	MemberListCursorAdapter memberListCursorAdapter;

	public Group()
	{

	}

	public String getGroupName()
	{
		return groupName;
	}

	public String getGroupNameWithNoSpace()
	{
		return groupNameWithNoSpace;
	}

	public float getGroupExpenseCount()
	{
		return groupExpenseCount;
	}

	public void setGroupExpenseCount(float groupExpenseCount)
	{
		this.groupExpenseCount = groupExpenseCount;
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
		setContentView(R.layout.activity_group);

		// Fetching the value from SharedPreference by creating object of this
		// class
		group = new Group();

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
		moneyTimeButton = (Button) findViewById(R.id.moneyTimeButton);

		// ---------Group List Implementation Using Cursor/Array
		// Adapter---------------------------
		listView = (ListView) findViewById(R.id.groupMemberListView);

		// ============Setting DBConstants.Group_table_name===========
		dbConstants = new DBConstants(this);
		dbConstants.setMEMBER_LIST_TABLE_NAME(groupNameWithNoSpace);

		// Creating obj of ListCursorAdapter
		memberListCursorAdapter = new MemberListCursorAdapter(this,
				dbConstants.getMEMBER_LIST_TABLE_NAME());

		// Hiding emptyMemberListTextView on successful retrieval of data from
		// List
		emptyMemberListTextView = (TextView) findViewById(R.id.emptyMemberListTextView);
		Cursor isCursor = memberListCursorAdapter.getMemberListCursor(this);
		if (isCursor != null && isCursor.getCount() > 0)
		{
			emptyMemberListTextView.setVisibility(TextView.INVISIBLE);
		}

		// Setting ListView For Each Row
		listView.setAdapter(memberListCursorAdapter.getAdapter());

		// OnItemClick Listener
		listView.setOnItemClickListener(this);

		// ****************Fetching the TotalExpenseCount (Each Row) for the
		// Group From GroupTable***************
		// Group group = new Group();
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
				group.setTotalExpenseCount(Float.valueOf(cursor.getString(cursor
						.getColumnIndex(DBConstants.MEMBER_LIST_EXPENSE))));
				cursor.moveToNext();
			}

			// Close Cursor In onResume()
			// cursor.close();

			// TotalCount
			TotalMemberCountTextView = (TextView) findViewById(R.id.totalTextView);
			TotalMemberCountTextView.setText(String.valueOf(group
					.getTotalExpenseCount()));

			// making EditgRoup Menu Visible
			menuEditGroupTrue = true;
			invalidateOptionsMenu();

		}
		// ------------------------------------

		// ------------Fecthing the Group Expensed Amount------------
		cursor = new MasterDBAdapter(this).getGroupExpenseCursor(groupName);
		cursor.moveToFirst();
		while (!cursor.isAfterLast())
		{
			group.setGroupExpenseCount(Float.valueOf(cursor.getString(cursor
					.getColumnIndex(DBConstants.GROUP_LIST_EXPENSE))));
			cursor.moveToNext();
		}

		// Close Cursor In onResume()
		// cursor.close();

		// Setting Left Amount
		// leftAmount
		// BalanceAmount Display
		leftAmountTextView = (TextView) findViewById(R.id.leftAmountTextView);
		leftAmountCount = (group.getGroupExpenseCount() - group
				.getTotalExpenseCount());

		if (isCursorFound)
		{
			if (leftAmountCount != 0)
			{
				leftAmountTextView.setText(String.valueOf(leftAmountCount));
			}
		} else
		{
			leftAmountTextView.setText(String.valueOf(group
					.getGroupExpenseCount()));
		}

		// Message.message(this, "Group Expense Count : "
		// +group.getGroupExpenseCount());
		// ----------------------------------------------------------

		// Comparing both the values
		if (group.getGroupExpenseCount() == group.getTotalExpenseCount())
		{
			totalAmountEqualsGroupAmount = true;
		}

		// Now check Total Amount == Group Amount
		if (isCursorFound)
		{
			if (totalAmountEqualsGroupAmount)
			{
				moneyTimeButton.setVisibility(Button.VISIBLE);
				// Message.message(this, "Total Expense Matched");
				// Message.message(this, "Now Do MoneyTime");

				// Triggering the onCreateOption() for disabling the "Addmember"
				// Bttoun from Actionbar
				menuFlag = true;
				invalidateOptionsMenu();

			} else
			{
				// Message.message(this,
				// "Please Match with Total GroupExpense");
				// moneyTimeButton.setVisibility(Button.INVISIBLE);
			}

		}

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		// Inflate the menu; this adds items to the action bar if it is present.
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.menu_group, menu);

		// =========Hiding editGroup Menu FOR rESUME() ACTIVITY================

		menu.findItem(R.id.editGroup).setEnabled(false);
		menu.findItem(R.id.editGroup).setVisible(false);
		// =========================================

		// Menu AddMember Hiding
		if (menuFlag)
		{
			menu.findItem(R.id.addMemberToGroup).setEnabled(false);
			menu.findItem(R.id.addMemberToGroup).setVisible(false);
		} else
		{
			menu.findItem(R.id.addMemberToGroup).setEnabled(true);
			menu.findItem(R.id.addMemberToGroup).setVisible(true);

		}

		// Menu Editgroup Showing
		if (menuEditGroupTrue)
		{
			menu.findItem(R.id.editGroup).setEnabled(true);
			menu.findItem(R.id.editGroup).setVisible(true);
		} else
		{
			menu.findItem(R.id.editGroup).setEnabled(false);
			menu.findItem(R.id.editGroup).setVisible(false);

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
			// Group.this.finish();
			return false;
		} else if (itemId == R.id.editGroup)
		{
			// Toast.makeText(this, "Functionality will come soon",
			// Toast.LENGTH_SHORT).show();
			startActivity(new Intent(this, EditGroup.class));
			// Group.this.finish();
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
		Group.this.finish();
	}

	@Override
	protected void onResume()
	{
		super.onResume();

		// Hiding EditMenu Icon when No Item Left In the Group
		menuFlag = false;
		menuEditGroupTrue = false;
		invalidateOptionsMenu();

		// Enabling the "Add member to Group" vISIBLE
		emptyMemberListTextView.setVisibility(TextView.VISIBLE);

		// Setting TotalExpenseCount To Zero For This onResume Method -- TO
		// AVOID cONFLICTS
		group.setTotalExpenseCountToZeroForResume();
		// isCursorFound = false;

		// Creating obj of ListCursorAdapter
		memberListCursorAdapter = new MemberListCursorAdapter(this,
				dbConstants.getMEMBER_LIST_TABLE_NAME());

		// Hiding emptyMemberListTextView on successful retrieval of data from
		// List
		emptyMemberListTextView = (TextView) findViewById(R.id.emptyMemberListTextView);
		Cursor isCursor = memberListCursorAdapter.getMemberListCursor(this);
		if (isCursor != null && isCursor.getCount() > 0)
		{
			emptyMemberListTextView.setVisibility(TextView.INVISIBLE);
		}

		// Setting ListView For Each Row
		listView.setAdapter(memberListCursorAdapter.getAdapter());

		// OnItemClick Listener
		listView.setOnItemClickListener((OnItemClickListener) this);

		// ----------Fetching the TotalExpenseCount (Each Row) for the Group
		// From GroupTable---------------
		Cursor cursor = new MemberDBAdapter(this).getTotalExpenseCursor(
				new MasterDBAdapter(this), dbConstants.getMEMBER_LIST_TABLE_NAME());

		// Checking whether cursor has sOme Result or Not
		if (cursor != null && cursor.getCount() != 0)
		{
			isCursorFound = true;

			// making EditgRoup Menu Visible
			menuEditGroupTrue = true;
			invalidateOptionsMenu();

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
				group.setTotalExpenseCount(Integer.valueOf(cursor.getString(cursor
						.getColumnIndex(DBConstants.MEMBER_LIST_EXPENSE))));
				cursor.moveToNext();

			}
			cursor.close();

			// TotalCount
			TotalMemberCountTextView = (TextView) findViewById(R.id.totalTextView);
			TotalMemberCountTextView.setText(String.valueOf(group
					.getTotalExpenseCount()));

			// making EditgRoup Menu Visible
			menuEditGroupTrue = true;
			invalidateOptionsMenu();

		}
		// ------------------------------------

		// ------------Fecthing the Group Expensed Amount------------
		cursor = new MasterDBAdapter(this).getGroupExpenseCursor(groupName);
		cursor.moveToFirst();
		while (!cursor.isAfterLast())
		{
			group.setGroupExpenseCount(Integer.valueOf(cursor.getString(cursor
					.getColumnIndex(DBConstants.GROUP_LIST_EXPENSE))));
			cursor.moveToNext();
		}
		cursor.close();

		// ============Setting Left Amount====================
		// leftAmount
		// BalanceAmount Display
		leftAmountTextView = (TextView) findViewById(R.id.leftAmountTextView);
		leftAmountCount = (group.getGroupExpenseCount() - group
				.getTotalExpenseCount());

		if (isCursorFound)
		{
			if (leftAmountCount != 0)
			{
				leftAmountTextView.setText(String.valueOf(leftAmountCount));
			} else
			{
				leftAmountTextView.setText("Nill");
			}
		} else
		{
			leftAmountTextView.setText(String.valueOf(group
					.getGroupExpenseCount()));
		}

		// Message.message(this, "Group Expense Count : "
		// +group.getGroupExpenseCount());
		// ----------------------------------------------------------

		// Comparing both the values
		if (group.getGroupExpenseCount() == group.getTotalExpenseCount())
		{
			totalAmountEqualsGroupAmount = true;
		}

		// Now check Total Amount == Group Amount
		if (isCursorFound)
		{
			if (totalAmountEqualsGroupAmount)
			{
				moneyTimeButton.setVisibility(Button.VISIBLE);
				// Message.message(this, "Total Expense Matched");
				// Message.message(this, "Now Do MoneyTime");

				// Triggering the onCreateOption() for disabling the "Addmember"
				// Button from Actionbar
				menuFlag = true;
				invalidateOptionsMenu();
			} else
			{
				// Message.message(this,
				// "Please Match with Total GroupExpense");
				// moneyTimeButton.setVisibility(Button.INVISIBLE);
			}

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
		Message.message(this, "Aakash Singh");
	}

	class ViewHolder
	{
		public String populatePhoneNumber(MemberBean memberBean)
		{
			return memberBean.getMemberPhoneNumber();
		}

	}

}
