package com.app.splitCash.group;

import android.content.res.Resources;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.util.Xml;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.app.splitCash.R;
import com.app.splitCash.constants.DBConstants;
import com.app.splitCash.dataBase.MemberDBAdapter;
import com.app.splitCash.dataBase.Message;
import com.app.splitCash.sharedPreferences.SharedPreferenceManager;

/**
 * Created by Aakash Singh on 20-07-2015.
 */
public class EditGroup extends AppCompatActivity implements AdapterView.OnItemClickListener
{
    private boolean menuFlag;
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
    
	private ImageView deleteImageView;

    //for add /delete Functionality
    private MemberDBAdapter memberDBAdapter;


    @Override
    public void onCreate(Bundle savedInstanceState)
    {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_group_unknown);

        //Fetching the value from SharedPreference by creating object of this class
        groupUnKnown = new GroupUnKnown();

        //Using SharedPreference to fetch groupNAME value
        groupName = new SharedPreferenceManager(this).getString(String.valueOf(DBConstants.groupSharedPrefKey));
        //=================================================

        //============Removing sPace from table name
        groupNameWithNoSpace = groupName.replaceAll("\\s+", "");

        //==========For Add/Delete Functionality=====
        memberDBAdapter = new MemberDBAdapter(this);
        //===========================================

        //---------ToolBar Implementation-------------
        toolbar = (Toolbar) findViewById(R.id.app_bar);
        //toolbar.setLogo(R.drawable.ic_conversation_icon);

        //sETTING tILE
        setTitle("Edit Group");
        setSupportActionBar(toolbar);
        //--------------------------------------------

        //----HomeButtonEnabled-------------------------
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        //---------------------------------------------


        //---------Group List Implementation Using Cursor/Array Adapter---------------------------
//        Resources res = getApplicationContext().getResources();
//        deleteImageView = res.getLayout(R.id.deleteMemberRow);
        
        listView = (ListView) findViewById(R.id.groupUnKnownMemberEditListView);


        //============Setting DBConstants.Group_table_name===========
        dbConstants = new DBConstants(this);
        dbConstants.setMEMBER_LIST_TABLE_NAME(groupNameWithNoSpace);


        //Creating obj of ListCursorAdapter
        EditListCursorAdapter editListCursorAdapter = new EditListCursorAdapter(this, dbConstants , dbConstants.getMEMBER_LIST_TABLE_NAME(), groupName);

        //Setting ListView For Each Row
        listView.setAdapter(editListCursorAdapter.getAdapter());

        //OnclickListener on List
        listView.setOnItemClickListener(this);
        
}

    @Override
    protected void onResume()
    {
        super.onResume();

        //---------Group List Implementation Using Cursor/Array Adapter---------------------------
        listView = (ListView) findViewById(R.id.groupUnKnownMemberEditListView);


        //============Setting DBConstants.Group_table_name===========
        dbConstants = new DBConstants(this);
        dbConstants.setMEMBER_LIST_TABLE_NAME(groupNameWithNoSpace);


        //Creating obj of ListCursorAdapter
        EditListCursorAdapter editListCursorAdapter = new EditListCursorAdapter(this, dbConstants , dbConstants.getMEMBER_LIST_TABLE_NAME(), groupName);

        //Setting ListView For Each Row
        listView.setAdapter(editListCursorAdapter.getAdapter());

        //OnclickListener on List
        listView.setOnItemClickListener(this);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        // Inflate the menu; this adds items to the action bar if it is present.
        //MenuInflater inflater = getMenuInflater();
        //inflater.inflate(R.menu.menu_edit_activity, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {

//    	int itemId = item.getItemId();
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
            return super.onOptionsItemSelected(item);
//        }

    }

    @Override
    public void onBackPressed()
    {
        super.onBackPressed();
        //startActivity(new Intent(this, HomePageActivity_NO_USE.class));
        EditGroup.this.finish();
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

    }

}
