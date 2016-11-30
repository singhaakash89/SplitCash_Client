package com.app.splitCash.root;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.app.splitCash.R;
import com.app.splitCash.notifications.NotificationManager;

public class SettingsActivity extends AppCompatActivity {

    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

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
    public void onBackPressed() {
        super.onBackPressed();
        SettingsActivity.this.finish();
    }

    public void fireNotification(View view)
    {
        NotificationManager notificationManager = new NotificationManager(getApplicationContext());
        notificationManager.sendNotification("This is a BigTextStyle for notification,\nKindly enjoy it as this is our Brand new feature.\n\nHappy Androiding...");
    }
}
