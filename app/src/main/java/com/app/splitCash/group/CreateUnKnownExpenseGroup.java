package com.app.splitCash.group;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.app.splitCash.R;
import com.app.splitCash.constants.DBConstants;
import com.app.splitCash.dataBase.MasterDBAdapter;
import com.app.splitCash.dataBase.Message;

/**
 * Created by Aakash Singh on 20-07-2015.
 */
public class CreateUnKnownExpenseGroup extends AppCompatActivity
{

    private EditText nameGroupUnKnownEditText;
    private Spinner groupUnKnownTypeSpinner;
    private EditText groupDescriptionUnKnownEditText;

    private Toolbar toolbar;
    private TextView textView;
    private MasterDBAdapter masterDbAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_group_unknown);

        //---------ToolBar Implementation-------------
        toolbar = (Toolbar) findViewById(R.id.app_bar);
        setSupportActionBar(toolbar);
        //--------------------------------------------

        //----HomeButtonEnabled-------------------------
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        //---------------------------------------------

        nameGroupUnKnownEditText = (EditText) findViewById(R.id.groupNameUnKnownEditText);
        groupUnKnownTypeSpinner = (Spinner) findViewById(R.id.groupUnKnownTypeSpinner);
        groupDescriptionUnKnownEditText = (EditText) findViewById(R.id.groupDescriptionUnKnownEditText);


    }

    public void createGroup(View view)
    {
        String nameGroup = nameGroupUnKnownEditText.getText().toString();
        String groupType = (String) groupUnKnownTypeSpinner.getSelectedItem();
        String groupDescription = groupDescriptionUnKnownEditText.getText().toString();


        //Checking for Null VALUES
        if (nameGroup.replaceAll("\\s+","").isEmpty())
        {
            Message.message(this, "Please Enter Group Name");

        }
        else if(groupType.isEmpty())
        {
            Message.message(this, "Please Enter Group Type");
        }
        else if(groupDescription.replaceAll("\\s+","").isEmpty())
        {
            Message.message(this, "Please Enter Group Description");
        }
        else
        {
            //setting group details
            GroupBean groupBean = new GroupBean();
            groupBean.setNameGroup(nameGroup);
            groupBean.setUid(groupBean.getRandomUID());
            groupBean.setGroupType(groupType);
            groupBean.setGroupDescription(groupDescription);
            groupBean.setGroupKnownUnknownType(DBConstants.GROUP_UNKNOWN);

            masterDbAdapter = new MasterDBAdapter(this);
            long id = masterDbAdapter.insert(DBConstants.GROUP_LIST_TABLE_NAME, groupBean.contentValues());

            //Toast on successful row insertion
            if (id < 0)
            {
                Message.message(this, "Group Creation Error...!!!");
                Message.messageBold(this, "Group", ""+groupBean.getNameGroup(), "already exists");
                //startActivity(new Intent(this, HomePageActivity_NO_USE.class));
                //CreateUnKnownExpenseGroup.this.finish();
            } else
            {
                Message.message(this, "Group Created Successfully");
                //startActivity(new Intent(this, HomePageActivity_NO_USE.class));
                CreateUnKnownExpenseGroup.this.finish();
            }
        }
    }


//    @Override
//    public boolean onCreateOptionsMenu(Menu menu)
//    {
//        // Inflate the menu; this adds items to the action bar if it is present.
//        MenuInflater inflater = getMenuInflater();
//        inflater.inflate(R.menu.menu_create_group, menu);
//        return true;
//    }
//
//    @Override
//    public boolean onOptionsItemSelected(MenuItem item)
//    {
//        // Handle action bar item clicks here. The action bar will
//        // automatically handle clicks on the Home/Up button, so long
//        // as you specify a parent activity in AndroidManifest.xml.
//        switch (item.getItemId())
//        {
//            case R.id.settings:
//                Toast.makeText(getBaseContext(), R.string.toast_message_setting,
//                        Toast.LENGTH_SHORT).show();
//                return false;
//
//            case R.id.contactus:
//                Toast.makeText(getBaseContext(), R.string.toast_message_contactus,
//                        Toast.LENGTH_SHORT).show();
//                return false;
//
//            case R.id.about:
//                Toast.makeText(getBaseContext(), R.string.toast_message_about, Toast.LENGTH_SHORT).show();
//                return false;
//
//            default:
//                return super.onOptionsItemSelected(item);
//        }
//
//    }

    @Override
    public void onBackPressed()
    {
        super.onBackPressed();
        CreateUnKnownExpenseGroup.this.finish();
    }

}
