package com.app.splitCash.root;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import com.app.splitCash.R;

/**
 * Created by Aakash Singh on 05-06-2016.
 */
public class ContactUs extends AppCompatActivity {
    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact_us);

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
        ContactUs.this.finish();
    }

}
