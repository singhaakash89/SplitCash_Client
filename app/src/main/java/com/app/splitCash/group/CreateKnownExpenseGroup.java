package com.app.splitCash.group;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;


import com.app.splitCash.R;
import com.app.splitCash.constants.DBConstants;
import com.app.splitCash.dataBase.MasterDBAdapter;
import com.app.splitCash.dataBase.Message;

public class CreateKnownExpenseGroup extends AppCompatActivity
{
//    private String uid;
//    private String nameGroup;
//    private String expenseInGroup;

    private EditText nameGroupEditText;
    private EditText expenseInGroupEditText;
    private Spinner groupTypeSpinner;
    private EditText groupDescriptionEditText;

    private Toolbar toolbar;
    private TextView textView;
    private MasterDBAdapter masterDbAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_group_known);

        //---------ToolBar Implementation-------------
        toolbar = (Toolbar) findViewById(R.id.app_bar);
        setSupportActionBar(toolbar);
        //--------------------------------------------

        //----HomeButtonEnabled-------------------------
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        //---------------------------------------------


        nameGroupEditText = (EditText) findViewById(R.id.groupNameEditText);
        expenseInGroupEditText = (EditText) findViewById(R.id.expenseEditText);
        groupTypeSpinner = (Spinner) findViewById(R.id.groupTypeSpinner);
        groupDescriptionEditText = (EditText) findViewById(R.id.groupDescriptionEditText);
    }

    public void createGroup(View view)
    {
        //for counting No. of Digits
        int count=0;
        String nameGroup = nameGroupEditText.getText().toString();
        String expenseInGroup = expenseInGroupEditText.getText().toString();
        String groupType = (String) groupTypeSpinner.getSelectedItem();
        String groupDescription = groupDescriptionEditText.getText().toString();

        //===============Counting No. of Digits=================================================
        for (int i = 0; i < expenseInGroup.length(); i++) {
            if (Character.isDigit(expenseInGroup.charAt(i))) {
                count++;
            }
        }
        //=====================================================================================

        if (nameGroup.replaceAll("\\s+","").isEmpty())
        {
            Message.message(this, "Please Enter Group Name");

        }
        else if(expenseInGroup.replaceAll("\\s+","").isEmpty())
        {
            Message.message(this, "Please Enter Group Expenses");
        }
        else if(groupType.isEmpty())
        {
            Message.message(this, "Please Enter Group Type");
        }
        else if(groupDescription.replaceAll("\\s+","").isEmpty())
        {
            Message.message(this, "Please Enter Group Description");
        }
        else if(count >= 8)
        {
            Message.message(this, "Please Enter Amount Within 7 Digits Only");
        }
        else
        {
            //setting group details
            GroupBean groupBean = new GroupBean();
            groupBean.setNameGroup(nameGroup);
            groupBean.setExpenseInGroup(expenseInGroup);
            groupBean.setUid(groupBean.getRandomUID());
            groupBean.setGroupType(groupType);
            groupBean.setGroupDescription(groupDescription);
            groupBean.setGroupKnownUnknownType(DBConstants.GROUP_KNOWN);

            masterDbAdapter = new MasterDBAdapter(this);
            long id = masterDbAdapter.insert(DBConstants.GROUP_LIST_TABLE_NAME, groupBean.contentValues());

            //Toast on successful row insertion
            if (id < 0)
            {
                Message.message(this, "Group Creation Error...!!!");
                //startActivity(new Intent(this, HomePageActivity_NO_USE.class));
                CreateKnownExpenseGroup.this.finish();
            } else
            {
                Message.message(this, "Group Created Successfully");
                //startActivity(new Intent(this, HomePageActivity_NO_USE.class));
                CreateKnownExpenseGroup.this.finish();
            }

        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        // Inflate the menu; this adds items to the action bar if it is present.
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_contact, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        switch (item.getItemId())
        {
            case R.id.settings:
                Toast.makeText(getBaseContext(), R.string.toast_message_setting,
                        Toast.LENGTH_SHORT).show();
                return false;

            case R.id.contactus:
                Toast.makeText(getBaseContext(), R.string.toast_message_contactus,
                        Toast.LENGTH_SHORT).show();
                return false;

            case R.id.about:
                Toast.makeText(getBaseContext(), R.string.toast_message_about, Toast.LENGTH_SHORT).show();
                return false;

            default:
                return super.onOptionsItemSelected(item);
        }

    }

    @Override
    public void onBackPressed()
    {
        super.onBackPressed();
        CreateKnownExpenseGroup.this.finish();
    }

}
