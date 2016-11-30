package com.app.splitCash.root;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import com.app.splitCash.R;
import com.app.splitCash.constants.DBConstants;
import com.app.splitCash.dataBase.MasterDBAdapter;
import com.app.splitCash.dataBase.UserDBAdapter;
import com.app.splitCash.gcmRegister.MainActivity;
import com.app.splitCash.sharedPreferences.SharedPreferenceManager;
import com.app.splitCash.toast.ToastManager;

/**
 * Created by Aakash Singh on 15-05-2016.
 */
public class RegisterActivity extends AppCompatActivity {

    private static final String isUserImageSelected = "isUserImageSelected";
    private SharedPreferenceManager sharedPreferenceManager;

    private Toolbar toolbar;

    private EditText nameET;
    private EditText phoneET;

    private String userName;
    private String phoneNumber;

    private boolean imageSelectFlag = false;
    private ToastManager toastManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.registration_main_new);

        //------------Setting Toolbar-------------------
        toolbar = (Toolbar) findViewById(R.id.app_start_bar);
        setSupportActionBar(toolbar);
        //-----------------------------------------------

        sharedPreferenceManager = new SharedPreferenceManager(this);
        imageSelectFlag = sharedPreferenceManager.getBoolean(isUserImageSelected);

        //toastManager for displaying toast
        toastManager = new ToastManager(this);

        //Create DB AT THIS PHASE SO THAT IMAGE AND OTHERUSER REGISTRATION DATA CAN BE STORED.
        MasterDBAdapter masterDbAdapter = new MasterDBAdapter(this);
        UserDBAdapter userDBAdapter = new UserDBAdapter(this);
        userDBAdapter.createUserTable();


    }

    //this method will invoke on button click
    public void registerUser(View view) {
        imageSelectFlag = sharedPreferenceManager.getBoolean(isUserImageSelected);
        Log.d("imageSelectFlag",""+imageSelectFlag);

        Intent intent = new Intent(this, MainActivity.class);
        Bundle bundle = new Bundle();

        nameET = (EditText) findViewById(R.id.nameET);
        phoneET = (EditText) findViewById(R.id.phoneET);

        userName = nameET.getText().toString();
        phoneNumber = phoneET.getText().toString();

        //Image, Name and Number Validation

        if (imageSelectFlag == false) {
            toastManager.createToast_Simple("Please select user image");
        } else if (userName.isEmpty()) {
            toastManager.createToast_Simple("Please enter your name");
        } else if (phoneNumber.isEmpty()) {
            toastManager.createToast_Simple("Please enter your number");
        } else if (phoneNumber.length() != 10) {
            toastManager.createToast_Simple("Please enter your 10-digit number correctly");
        } else {
            //storing username and password in sharedPreference if no internet connection occurs
            sharedPreferenceManager.putString(DBConstants.USER_NAME, userName);
            sharedPreferenceManager.putString(DBConstants.USER_PHONE_NUMBER, phoneNumber);

            //sending data through Bundle
            bundle.putString("userName_Message", userName);
            bundle.putString("phoneNumber_Message", phoneNumber);
            intent.putExtras(bundle);

            startActivity(intent);
            RegisterActivity.this.finish();
        }
    }


}
