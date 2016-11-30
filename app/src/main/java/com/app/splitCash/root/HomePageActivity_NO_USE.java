package com.app.splitCash.root;


import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.app.splitCash.R;
import com.app.splitCash.constants.DBConstants;
import com.app.splitCash.group.CreateUnKnownExpenseGroup;
import com.app.splitCash.group.Group;
import com.app.splitCash.group.GroupBean;
import com.app.splitCash.group.GroupListCursorAdapter;
import com.app.splitCash.group.GroupUnKnown;
import com.app.splitCash.navDrawer.NavigationDrawerFragment;
import com.app.splitCash.sharedPreferences.SharedPreferenceManager;

import java.util.ArrayList;


public class HomePageActivity_NO_USE extends AppCompatActivity implements AdapterView.OnItemClickListener {
    private Toolbar toolbar;
    private ListView listView;
    TextView emptyGroupListTextView;
    private GroupListCursorAdapter groupListCursorAdapter;
    private SharedPreferenceManager sharedPreferenceManager;

    //String for GroupName to save in SharedPreference
    private String groupName;

    //String for storing Group Known/Unknown Type
    private String groupKnownUnKnownType;

    ArrayList<GroupBean> groupBeanList;
    ViewHolder viewHolder;

    private int backPressCount = 0;
    private String userName;

    public HomePageActivity_NO_USE() {
        //Empty Constructor Needed
        sharedPreferenceManager = new SharedPreferenceManager(this);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_fragment_app_bar);

        //---------ToolBar Implementation-------------
        toolbar = (Toolbar) findViewById(R.id.app_bar);
//        toolbar.setLogo(R.drawable.ic_logo);
        setSupportActionBar(toolbar);
        //--------------------------------------------

        //----HomeButtonEnabled-------------------------
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        //---------------------------------------------

        //Fetching userNamw for app quiting purpose
        userName = sharedPreferenceManager.getString(DBConstants.USER_NAME);


        //--------------Navigation Drawer Implementation------------------
//        NavigationDrawerFragment drawerFragment = (NavigationDrawerFragment) getFragmentManager().findFragmentById(R.id.fragment_navigation_drawer);
//        drawerFragment.setUp(R.id.fragment_navigation_drawer, (DrawerLayout) findViewById(R.id.drawer_layout), toolbar);
        //----------------------------------------------------------------


        //---------Group List Implementation Using Cursor/Array Adapter---------------------------
        listView = (ListView) findViewById(R.id.groupListView);

        groupListCursorAdapter = new GroupListCursorAdapter(this);

        //Hiding emptyGroupListTextView if DATA eXIST IN lIST
        emptyGroupListTextView = (TextView) findViewById(R.id.emptyGroupListTextView);
        Cursor isCursor = groupListCursorAdapter.getGroupListCursor(this);
        if (isCursor != null && isCursor.getCount() > 0) {
            emptyGroupListTextView.setVisibility(TextView.INVISIBLE);
        }

        //Setting Adapter
        listView.setAdapter(groupListCursorAdapter.getAdapter());

        //Adding OnItemclick Listener
        listView.setOnItemClickListener(this);


    }

    @Override
    public void onBackPressed() {

        //Below code is for 2 second Pause for Exit Using HANDLER
//        backPressCount++;
//        if(backPressCount == 1)
//        {
//            Message.message(this, "Press again to Exit");
//        }
//        else if(backPressCount == 2)
//        {
//            HomePageActivity_NO_USE.this.finish();
//        }
//
//        new Handler().postDelayed(new Runnable() {
//
//            @Override
//            public void run()
//            {
//                backPressCount = 0;
//            }
//        }, 2000);


        //Dialog box Implementation
        new AlertDialog.Builder(this)
                .setTitle(Html.fromHtml("Bye <b>" +userName+ "</b>"))
                .setMessage("Quit WidUapp?")
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        HomePageActivity_NO_USE.this.finish();
                    }
                })
                .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {

                    }
                })
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();


    }

    @Override
    protected void onResume() {
        super.onResume();

        //MAKING "add group to ic_splitcash" visible"
        emptyGroupListTextView.setVisibility(TextView.VISIBLE);

        //SettinG aDAPTER FOR THE SECOND TIME
        groupListCursorAdapter = new GroupListCursorAdapter(this);

        //Hiding emptyGroupListTextView if DATA eXIST IN lIST
        emptyGroupListTextView = (TextView) findViewById(R.id.emptyGroupListTextView);
        Cursor isCursor = groupListCursorAdapter.getGroupListCursor(this);
        if (isCursor != null && isCursor.getCount() > 0) {
            emptyGroupListTextView.setVisibility(TextView.INVISIBLE);
        }

        //SettinG aDAPTER FOR THE SECOND TIME
        listView.setAdapter(groupListCursorAdapter.getAdapter());

        //Adding OnItemclick Listener
        listView.setOnItemClickListener(this);

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_home_page, menu);
        return true;

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();
        if (itemId == R.id.settings) {
            Toast.makeText(getBaseContext(), R.string.toast_message_setting,
                    Toast.LENGTH_SHORT).show();
            return false;
        } else if (itemId == R.id.contactus) {
            Toast.makeText(getBaseContext(), R.string.toast_message_contactus,
                    Toast.LENGTH_SHORT).show();
            return false;
        } else if (itemId == R.id.about) {
            Toast.makeText(getBaseContext(), R.string.toast_message_about, Toast.LENGTH_SHORT).show();
            return false;
        } else {
            return super.onOptionsItemSelected(item);
        }

    }


    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

        viewHolder = new ViewHolder();
        groupBeanList = groupListCursorAdapter.getGroupList();

        //Fetching Group NAME fROM table using ARRAYlIST OF lISTaDAPTER
        groupName = viewHolder.populateName(groupBeanList.get(position));

        //uSING sharedpreference to pass groupname
        DBConstants.groupSharedPrefKey++;
        sharedPreferenceManager.putString(String.valueOf(DBConstants.groupSharedPrefKey), groupName);
        //----------------------------------------

        //to start Activity Group ON tHE BASIS OFkNOWN/uNKNOWN gROUP tYPE
        groupKnownUnKnownType = viewHolder.populateGroupKnownUnKnownType(groupBeanList.get(position));

        if (groupKnownUnKnownType.equals(DBConstants.GROUP_KNOWN)) {
            startActivity(new Intent(this, Group.class));
            //Message.message(this, "GroupKnown Started");
        } else if (groupKnownUnKnownType.equals(DBConstants.GROUP_UNKNOWN)) {
            startActivity(new Intent(this, GroupUnKnown.class));
            //Message.message(this, "GroupUnknown Started");
        }


    }

    class ViewHolder {
        public String populateName(GroupBean groupBean) {
            return groupBean.getNameGroup();
        }

        public String populateGroupKnownUnKnownType(GroupBean groupBean) {
            return groupBean.getGroupKnownUnknownType();
        }
    }

    public void createGroup(View view)
    {
        startActivity(new Intent(this, CreateUnKnownExpenseGroup.class));
    }

}

