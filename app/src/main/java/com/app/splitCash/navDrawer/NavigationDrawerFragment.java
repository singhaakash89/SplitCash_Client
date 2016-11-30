package com.app.splitCash.navDrawer;


import android.app.Fragment;
import android.content.Context;
import android.content.SharedPreferences;

import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.app.splitCash.R;
import com.app.splitCash.root.HomepageActivityNew;

import java.util.ArrayList;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */
public class NavigationDrawerFragment extends Fragment
{
    private RecyclerView recyclerView;
    private ListView listView;

    //Nav Adapter reference
    private NavigationAdapter navigationAdapter;

    //Sahredpreference File
    public static final String PREF_FILE_NAME = "testpref";

    //Key for sharedPreferences
    public static final String KEY_USER_LEARNED_DRAWER = "user_learned_drawer";

    private View containerView;

    private ActionBarDrawerToggle mDrawerToggle;
    private DrawerLayout mDrawerLayout;

    private boolean mUserLearnedDrawer;
    private boolean mFromSavedInstanceState;

    private ImageView fragmentImageForAppBar;


    public NavigationDrawerFragment() {
        // Required empty public constructor

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        mUserLearnedDrawer = Boolean.valueOf(readFromPreferences(getActivity(), KEY_USER_LEARNED_DRAWER, "false"));

        Log.e("CHECK :: ", "INSIDE (onCreate)");

        if (savedInstanceState != null) {
            mFromSavedInstanceState = true;
        }




    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        Log.e("CHECK :: ", "INSIDE (onCreateView)");

        // Inflate the layout for this fragment
        View layout = inflater.inflate(R.layout.fragment_navigation_drawer, container, false);

        recyclerView = (RecyclerView) layout.findViewById(R.id.drawerList);

        mDrawerLayout = (DrawerLayout) layout.findViewById(R.id.drawer_layout);

        navigationAdapter = new NavigationAdapter(getActivity(), getData(), recyclerView, mDrawerLayout);

        recyclerView.setAdapter(navigationAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        return layout;
    }

    public static List<rowData> getData()
    {
        Log.e("CHECK :: ", "INSIDE (List<rowData> getData)");

        List<rowData> ArrayListData = new ArrayList();
        int []icons = {R.drawable.ic_group_nav, R.drawable.ic_chat_nav, R.drawable.ic_settings_nav, R.drawable.ic_aboutus_nav};
        //String []titles = {"Home", "Chats", "Groups", "Setings", "About us", "Contact us"};
        String []titles = {"WidUapp Groups", "WidUapp Chats", "WidUapp Settings", "About WidUapp"};
        for (int i=0; i<titles.length && i<icons.length; i++)
        {
            rowData currentData = new rowData();
            currentData.iconId = icons[i];
            currentData.titleId = titles[i];
            ArrayListData.add(currentData);
        }

        return ArrayListData;
    }



    //------------Method setup()-------------
    public void setUp(int fragmentId, final DrawerLayout drawerLayout, final Toolbar toolbar, final ImageView fragmentImageForAppBar) {

        Log.e("CHECK :: ", "INSIDE (setUp)");

        containerView = getActivity().findViewById(fragmentId);
        mDrawerLayout = drawerLayout;
        this.fragmentImageForAppBar = fragmentImageForAppBar;

//        //Sending refernce of drawerLayout to Adapter
//        final DrawerLayout drawerLayoutNew = drawerLayout;

        mDrawerToggle = new ActionBarDrawerToggle(getActivity(), drawerLayout, toolbar, R.string.drawer_open, R.string.drawer_close) {
            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);

                Log.e("CHECK :: ", "INSIDE (onDrawerOpened)");

//                if (!mUserLearnedDrawer) {
//                    mUserLearnedDrawer = true;
//                    //Converting the mUserLearnedDrawer from Boolean to String  -> hence using  <mUserLearnedDrawer + " ">
//                    saveToPreferences(getActivity(), KEY_USER_LEARNED_DRAWER, mUserLearnedDrawer + "");
//                }

                //ReDrawing the options Menu
                //getActivity().invalidateOptionsMenu();

                //Hiding appBar ImageView icon when drawer is opened
                fragmentImageForAppBar.setVisibility(ImageView.GONE);

                //Hiding appBar menu when drawer is opened
                //Making flag true to hide menu when drawer is opened
                HomepageActivityNew.menuFlag = true;
                getActivity().invalidateOptionsMenu();

                //Sending drawer context to navAdapter for closing drawer
                NavigationAdapter.reIntializeDrawerLayout(drawerLayout);

                // =========================================

            }

            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);

                Log.e("CHECK :: ", "INSIDE (onDrawerClosed)");

                //Showing appBar ImageView icon when drawer is opened
                fragmentImageForAppBar.setVisibility(ImageView.VISIBLE);

                //ReDrawing the options Menu
                HomepageActivityNew.menuFlag = false;
                getActivity().invalidateOptionsMenu();

            }

//-------------Uncomment this snippet to let toolbar fade-out when drawer comes-in
//            @Override
//            public void onDrawerSlide(View drawerView, float slideOffset)
//            {
//                if(slideOffset < 0.4)
//                {
//                    toolbar.setAlpha(1 - slideOffset);
//                }
//
//            }
        };


        //Checking if the app is opened for the first time and user nvr seen drawer---
        //if (!mUserLearnedDrawer && !mFromSavedInstanceState)
//        if (false)       //For Rectification
//        {
//            drawerLayout.openDrawer(containerView);
//        }
//
        mDrawerLayout.setDrawerListener(mDrawerToggle);
        mDrawerLayout.post(new Runnable() {
            @Override
            public void run() {
                mDrawerToggle.syncState();
                Log.e("CHECK :: ", "INSIDE (syncState)");
            }
        });
    }
    //----------------------------------------


    //Returning the reference for "mDrawerLayout"
    public DrawerLayout getmDrawerLayout()
    {
        Log.e("CHECK :: ", "INSIDE (getmDrawerLayout)");
        return mDrawerLayout;
    }


//    //--------Saving Drawer to sharedPreferences-----------------
//    public static void saveToPreferences(Context context, String preferenceName, String preferenceValue) {
//        //Creating object for sharedPreferences
//        SharedPreferences sharedPreferences = context.getSharedPreferences(PREF_FILE_NAME, Context.MODE_PRIVATE);
//
//        //edit() gives us the editor for the sharedpreference
//        SharedPreferences.Editor editor = sharedPreferences.edit();
//
//        //Saving key and value to the sharedPreference
//        editor.putString(preferenceName, preferenceValue);
//
//        //commit our sharedpreference file
//        //editor.commit();    //synchronous - checks whether the data is persisted to the memory or NOT
//        editor.apply();    //Asynchronous - Doesn't check, and Faster
//
//    }
//    //-----------------------------------------------------------
//
//    //-----------------Reading From the preference----------------
//    public static String readFromPreferences(Context context, String preferenceName, String defaultValue) {
//        //Creating object for sharedPreferences
//        SharedPreferences sharedPreferences = context.getSharedPreferences(PREF_FILE_NAME, Context.MODE_PRIVATE);
//
//        return sharedPreferences.getString(preferenceName, defaultValue);
//    }

}


