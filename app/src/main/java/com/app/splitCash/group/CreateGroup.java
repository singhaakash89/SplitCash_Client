package com.app.splitCash.group;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.app.splitCash.R;

/**
 * Created by Aakash Singh on 20-07-2015.
 */
public class CreateGroup extends AppCompatActivity
{
    Toolbar toolbar;

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_group);

        //---------ToolBar Implementation-------------
        toolbar = (Toolbar) findViewById(R.id.app_bar);
        setSupportActionBar(toolbar);
        //--------------------------------------------

        //----HomeButtonEnabled-------------------------
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        //---------------------------------------------

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        // Inflate the menu; this adds items to the action bar if it is present.
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_create_group, menu);
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


    public void createKnownExpenseGroup(View view)
    {
        startActivity(new Intent(this, CreateKnownExpenseGroup.class));
        CreateGroup.this.finish();
    }


    public void createUnKnownExpenseGroup(View view)
    {
        startActivity(new Intent(this, CreateUnKnownExpenseGroup.class));
        CreateGroup.this.finish();
    }

    @Override
    protected void onResume()
    {
        super.onResume();
    }





    @Override
    public void onBackPressed()
    {
        super.onBackPressed();
        CreateGroup.this.finish();
    }


}
