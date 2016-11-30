package com.app.splitCash.group;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.app.splitCash.R;
import com.app.splitCash.constants.DBConstants;
import com.app.splitCash.dataBase.MasterDBAdapter;
import com.app.splitCash.dataBase.MemberDBAdapter;
import com.app.splitCash.dataBase.Message;
import com.app.splitCash.dataBase.MoneyTimeDBAdapter;
import com.app.splitCash.sharedPreferences.SharedPreferenceManager;

public class MoneyTimeActivity extends AppCompatActivity
{
    private float memberCount = 0;
    private float moneyTimeMemberExpense;
    private float moneyTimeAverageAmount;

    private String groupName;
    private ListView listView;
    private TextView averageTextView;
    private Toolbar toolbar;
    private String moneyTimeListTableNameWithNoSpace;

    private Context context;
    private Cursor cursor;
    private MoneyTimeActivity moneyTimeActivity;

    private String groupKnownUnKnownType;

    //Testing Variable
    private int insertionCount = 0;

    public String getGroupKnownUnKnownType()
    {
        return groupKnownUnKnownType;
    }

    public void setGroupKnownUnKnownType(String groupKnownUnKnownType)
    {
        this.groupKnownUnKnownType = groupKnownUnKnownType;
    }

    public String getMoneyTimeListTableNameWithNoSpace()
    {
        return moneyTimeListTableNameWithNoSpace;
    }

    public MoneyTimeActivity()
    {
        //Empty Constructor
    }

    public void insertFromGroupTableToMoneyTimeListTable(MoneyTimeActivity moneyTimeActivity, DBConstants dbConstants, String groupName)
    {
        //TAKING aVERAGE FOR TOtAKE AND toGive
        moneyTimeAverageAmount = getAverageExpense();

        //used for setter and getters
        MoneyTimeBean moneyTimeBean = new MoneyTimeBean();

        //Checking whether Group is Known/Unknown
        cursor = new MasterDBAdapter(this).getGroupKnownUnknownCursor(groupName);
        cursor.moveToFirst();
        if (cursor.getCount() > 0)
        {
            while (!cursor.isAfterLast())
            {
                moneyTimeActivity.setGroupKnownUnKnownType(cursor.getString(cursor.getColumnIndex(DBConstants.GROUP_LIST_KNOWN_UNKNOWN_TYPE)));
                cursor.moveToNext();
            }
        }
        cursor.close();

        //Checking for which group Type MoneyTime is Being Opened
        //Message.message(this, moneyTimeActivity.getGroupKnownUnKnownType());
        //=======================================================================================================================


        //----------Implement Cursor For Group Table AND parallely Store DATA TO mOEYtIME tABLE----------------

        //Checking if data in MoneyTable Exists Already
        cursor = new MoneyTimeDBAdapter(this).queryCursorWithDBAdapter(this, new MasterDBAdapter(this), dbConstants.MONEYTIME_LIST_TABLE_NAME, DBConstants.MONEYTIME_LIST_TABLE_STRUCTURE);
        int moneyTimeListTableDataExist = cursor.getCount();
        //=======================================================================================================================


        //if data in MoneyTable Exists Already so True Else False
        if (moneyTimeListTableDataExist == 0)
        {
            cursor = new MemberDBAdapter(this).queryCursorWithDBAdapter(this, new MasterDBAdapter(this), groupName.replaceAll("\\s+", ""), DBConstants.MEMBER_LIST_TABLE_STRUCTURE);
            cursor.moveToFirst();
            while (!cursor.isAfterLast())
            {
                //Now use Setter Method of MoneyTimeBean to set VALUES cOMING fROM cURSOR
                moneyTimeBean.setMoneyTimeMemberName(cursor.getString(cursor.getColumnIndex(DBConstants.MEMBER_LIST_NAME)));
                moneyTimeBean.setMoneyTimeMemberExpense(cursor.getString(cursor.getColumnIndex(DBConstants.MEMBER_LIST_EXPENSE)));

                //-----------Logic For Totake And ToGive---------------
                moneyTimeMemberExpense = Float.valueOf(moneyTimeBean.getMoneyTimeMemberExpense());
                //------------------------------------------------------

                //=====Setting toTake And TOgIVE IN MoneyBean====
                if (moneyTimeAverageAmount > moneyTimeMemberExpense)
                {
                    moneyTimeBean.setMoneyTimeMemberToGive(String.valueOf(moneyTimeAverageAmount - moneyTimeMemberExpense));
                    moneyTimeBean.setMoneyTimeMemberToTake("0.0");
                } else if (moneyTimeMemberExpense > moneyTimeAverageAmount)
                {
                    moneyTimeBean.setMoneyTimeMemberToTake(String.valueOf(moneyTimeMemberExpense - moneyTimeAverageAmount));
                    moneyTimeBean.setMoneyTimeMemberToGive(String.valueOf(0.0f));

                } else
                {
                    moneyTimeBean.setMoneyTimeMemberToTake(String.valueOf(0.0f));
                    moneyTimeBean.setMoneyTimeMemberToGive(String.valueOf(0.0f));
                }
                //===============================================


                //Now use Content VALUES TO iNSERT tHE rOW TO THE "MoneyTimeTABLE"
                long id = new MoneyTimeDBAdapter(this).insert(new MasterDBAdapter(this), dbConstants.getMONEYTIME_LIST_TABLE_NAME(), moneyTimeBean.contentValues());

                //Toast on successful row insertion
                if (id < 0)
                {
                    Message.message(this, "Error - Insertion Error");
                } else
                {
                    //Message.message(this, "Member Added Successfully");
                    //For Testing Purpose....Increasing insertion Count for each time a rOW iS Inserted
                    insertionCount++;
                }

                cursor.moveToNext();
            }

            cursor.close();
            //To Show How Many Rows Inserted
            //Message.message(this, " " + insertionCount);
            //Using else section for Unknown Group type
        } else if (moneyTimeListTableDataExist != 0 && moneyTimeActivity.getGroupKnownUnKnownType().equals(DBConstants.GROUP_UNKNOWN))
        {
            //Now here put th logiv for everytime insertion for the Uknown Group

            //Before Inserting Drop the TABLE
            MoneyTimeDBAdapter moneyTimeDBAdapter = new MoneyTimeDBAdapter(this);
            moneyTimeDBAdapter.dropTableIfExists(new MasterDBAdapter(this), dbConstants);

            //Creatin TABLE
            moneyTimeDBAdapter.createMoneyTimeTable(new MasterDBAdapter(this), dbConstants);


            //Now Fetch VALUES fROM THE mEMBER table to Insert IN mONEYTIME tABLE
            cursor = new MemberDBAdapter(this).queryCursorWithDBAdapter(this, new MasterDBAdapter(this), groupName.replaceAll("\\s+", ""), DBConstants.MEMBER_LIST_TABLE_STRUCTURE);
            cursor.moveToFirst();
            while (!cursor.isAfterLast())
            {
                //Now use Setter Method of MoneyTimeBean to set VALUES cOMING fROM cURSOR
                moneyTimeBean.setMoneyTimeMemberName(cursor.getString(cursor.getColumnIndex(DBConstants.MEMBER_LIST_NAME)));
                moneyTimeBean.setMoneyTimeMemberExpense(cursor.getString(cursor.getColumnIndex(DBConstants.MEMBER_LIST_EXPENSE)));

                //-----------Logic For Totake And ToGive---------------
                moneyTimeMemberExpense = Float.valueOf(moneyTimeBean.getMoneyTimeMemberExpense());
                //------------------------------------------------------

                //=====Setting toTake And TOgIVE IN MoneyBean====
                if (moneyTimeAverageAmount > moneyTimeMemberExpense)
                {
                    moneyTimeBean.setMoneyTimeMemberToGive(String.valueOf(moneyTimeAverageAmount - moneyTimeMemberExpense));
                    moneyTimeBean.setMoneyTimeMemberToTake("0.0");
                } else if (moneyTimeMemberExpense > moneyTimeAverageAmount)
                {
                    moneyTimeBean.setMoneyTimeMemberToTake(String.valueOf(moneyTimeMemberExpense - moneyTimeAverageAmount));
                    moneyTimeBean.setMoneyTimeMemberToGive(String.valueOf(0.0f));

                } else
                {
                    moneyTimeBean.setMoneyTimeMemberToTake(String.valueOf(0.0f));
                    moneyTimeBean.setMoneyTimeMemberToGive(String.valueOf(0.0f));
                }
                //===============================================


                //=========================================================================


                //Now use Content VALUES TO iNSERT tHE rOW TO THE "MoneyTimeTABLE"
                long id = new MoneyTimeDBAdapter(this).insert(new MasterDBAdapter(this), dbConstants.getMONEYTIME_LIST_TABLE_NAME(), moneyTimeBean.contentValues());

                //Toast on successful row insertion
                if (id < 0)
                {
                    Message.message(this, "Error - Insertion Error");
                } else
                {
                    //Message.message(this, "Member Added Successfully");
                    //For Testing Purpose....Increasing insertion Count for each time a rOW iS Inserted
                    insertionCount++;
                }

                cursor.moveToNext();
            }

            cursor.close();

        }
        //-----------------------------------------------------------------------------------------------------

    }

    public float getAverageExpense()
    {
        //----------Fetching the TotalExpenseCount (Each Row) for the Group From GroupTable---------------
        memberCount = 0;
        Group group = new Group();
        Cursor cursor = new MemberDBAdapter(this).getTotalExpenseCursor(new MasterDBAdapter(this), groupName.replaceAll("\\s+", ""));
        cursor.moveToFirst();
        while (!cursor.isAfterLast())
        {
            //--------------------------------------------------------------------------------------------
            //here capture each row expense and subtract from the averge  -- MoneyTime Activity
            //--------------------------------------------------------------------------------------------
            group.setTotalExpenseCount(Integer.valueOf(cursor.getString(cursor.getColumnIndex(DBConstants.MEMBER_LIST_EXPENSE))));
            cursor.moveToNext();

            memberCount++; //calculating Total Members in group
        }
        cursor.close();

        return (group.getTotalExpenseCount() / memberCount);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        //Making object of MoneyTimeActivity to set the Application Context which will be used in "insertFromGroupToMoneyTime()"
        moneyTimeActivity = new MoneyTimeActivity();

        //Fetching GroupName From SharedPrefernce
        groupName = new SharedPreferenceManager(this).getString(String.valueOf(DBConstants.groupSharedPrefKey));

        //Removing space from tableName
        moneyTimeListTableNameWithNoSpace = groupName.replaceAll("\\s+", "");
        ;

        //Set TableName For MoneyTimeListTable
        DBConstants dbConstants = new DBConstants(this);
        moneyTimeListTableNameWithNoSpace = moneyTimeListTableNameWithNoSpace.concat(dbConstants.MONEYTIME_LIST_TABLE_NAME);

        //nOW STORE IT TO DBConstants.MONEYTIME_LIST_TABLE_NAME;
        dbConstants.MONEYTIME_LIST_TABLE_NAME = moneyTimeListTableNameWithNoSpace;

        //Insert Value From Group Table To MoneyTime Table
        insertFromGroupTableToMoneyTimeListTable(moneyTimeActivity, dbConstants, groupName);

//===============================================================================================================================================

        //Now Set the View for LAYOUT
        setContentView(R.layout.activity_money_time);

        //---------ToolBar Implementation-------------
        toolbar = (Toolbar) findViewById(R.id.app_bar);
        //toolbar.setLogo(R.drawable.ic_conversation_icon);

        //sETTING tILE
        setTitle("SplitCash");
        setSupportActionBar(toolbar);
        //--------------------------------------------

        //----HomeButtonEnabled-------------------------
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        //---------------------------------------------


        //Calling Cursor
        //---------Group List Implementation Using Cursor/Array Adapter---------------------------
        listView = (ListView) findViewById(R.id.moneyTimeMemberListView);

        //Creating obj of ListCursorAdapter
        MoneyTimeListCursorAdaptor moneyTimeListCursorAdaptor = new MoneyTimeListCursorAdaptor(this, dbConstants.getMONEYTIME_LIST_TABLE_NAME());

        //Setting ListView For Each Row
        listView.setAdapter(moneyTimeListCursorAdaptor.getAdapter());

        //CALCULATING AVRge amount to display
        averageTextView = (TextView) findViewById(R.id.averageTextView);
        averageTextView.setText(String.valueOf(getAverageExpense()));
    }

//    @Override
//    public boolean onCreateOptionsMenu(Menu menu)
//    {
//        // Inflate the menu; this adds items to the action bar if it is present.
//        MenuInflater inflater = getMenuInflater();
//        inflater.inflate(R.menu.menu_money_time, menu);
//        return true;
//    }
//
//    @Override
//    public boolean onOptionsItemSelected(MenuItem item)
//    {
//        int itemId = item.getItemId();
//        if (itemId == R.id.settings)
//        {
//            Toast.makeText(getBaseContext(), R.string.toast_message_setting,
//                    Toast.LENGTH_SHORT).show();
//            return false;
//        } else if (itemId == R.id.contactus)
//        {
//            Toast.makeText(getBaseContext(), R.string.toast_message_contactus,
//                    Toast.LENGTH_SHORT).show();
//            return false;
//        } else if (itemId == R.id.about)
//        {
//            Toast.makeText(getBaseContext(), R.string.toast_message_about, Toast.LENGTH_SHORT).show();
//            return false;
//        } else
//        {
//            return super.onOptionsItemSelected(item);
//        }
//
//    }

    @Override
    public void onBackPressed()
    {
        super.onBackPressed();
        //startActivity(new Intent(this, Group.class));
        MoneyTimeActivity.this.finish();
    }

    public void moneyTimeMail(View view)
    {
        Intent emailIntent = new Intent(Intent.ACTION_SENDTO);
        emailIntent.setData(Uri.parse("mailto:" + "singhaakash89@gmail.com"));
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, "My email's subject");
        emailIntent.putExtra(Intent.EXTRA_TEXT, "My email's body");

        try
        {
            startActivity(Intent.createChooser(emailIntent, "Choose Email Client"));
        } catch (android.content.ActivityNotFoundException ex)
        {
            Toast.makeText(this, "No email clients installed.", Toast.LENGTH_SHORT).show();
        }
    }

}
