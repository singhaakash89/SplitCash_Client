package com.app.splitCash.sharedPreferences;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;

public class SharedPreferenceManager extends Activity
{
    public static final String USER_DATA = "SplitCashSharedPreferenceFile";
    public static final String STRING_DEFAULT = "N/A";
    public static final boolean BOOLEAN_DEFAULT = false;
    SharedPreferences sharedPreferences;
    private Context context;
    
    //static vars for storing name and phoneNumber to sharedpreferences
    public static String ID;
    public static String NAME;
    public static String PHONE_NUMBER;

//    public SharedPreferenceManager(){}
    
    public SharedPreferenceManager(Context context)
    {
    	this.context = context;
    }
    
    public void putString(String key , String value)
    {    	
    	//Opening sharedPreferences File to write
    	//sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
    	sharedPreferences = context.getSharedPreferences(USER_DATA, 0); //0 - for private Mode

        //---Using SharedPreference for saving Data----
        SharedPreferences.Editor editor = sharedPreferences.edit();
        
        //Opening editor to write into sharedpreference
        editor.putString(key, value);
        
        //commiting changes
        editor.commit();
   	
    }

    public void putBoolean(String key , boolean value)
    {    	
    	//Opening sharedPreferences File to write
    	//sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
    	sharedPreferences = context.getSharedPreferences(USER_DATA, 0); //0 - for private Mode

        //---Using SharedPreference for saving Data----
        SharedPreferences.Editor editor = sharedPreferences.edit();
        
        //Opening editor to write into sharedpreference
        editor.putBoolean(String.valueOf(key), value);
        
        //commiting changes
        editor.commit();
   	
    }

    
    
    public boolean getBoolean(String key)
    {
    	//sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
    	sharedPreferences = context.getSharedPreferences(USER_DATA, 0); //0 - for private Mode
    	return (sharedPreferences.getBoolean(String.valueOf(key), BOOLEAN_DEFAULT));
    }
 

    public String getString(String key)
    {
    	//sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
    	sharedPreferences = context.getSharedPreferences(USER_DATA, 0); //0 - for private Mode
    	return (sharedPreferences.getString(key, STRING_DEFAULT));
    }
    
}
