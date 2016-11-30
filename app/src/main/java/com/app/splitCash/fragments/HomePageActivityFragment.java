package com.app.splitCash.fragments;

import android.app.Fragment;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.app.splitCash.R;
import com.app.splitCash.constants.DBConstants;
import com.app.splitCash.group.CreateUnKnownExpenseGroup;
import com.app.splitCash.group.Group;
import com.app.splitCash.group.GroupBean;
import com.app.splitCash.group.GroupListCursorAdapter;
import com.app.splitCash.group.GroupUnKnown;
import com.app.splitCash.sharedPreferences.SharedPreferenceManager;

import java.util.ArrayList;

/**
 * Created by Aakash Singh on 24-05-2016.
 */
public class HomePageActivityFragment extends Fragment implements AdapterView.OnItemClickListener {

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

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.activity_home_page_fragment, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        //---------Group List Implementation Using Cursor/Array Adapter---------------------------
        listView = (ListView) getActivity().findViewById(R.id.groupListView);

        groupListCursorAdapter = new GroupListCursorAdapter(getActivity());

        //Hiding emptyGroupListTextView if DATA eXIST IN lIST
        emptyGroupListTextView = (TextView) getActivity().findViewById(R.id.emptyGroupListTextView);
        Cursor isCursor = groupListCursorAdapter.getGroupListCursor(getActivity());
        if (isCursor != null && isCursor.getCount() > 0) {
            emptyGroupListTextView.setVisibility(TextView.INVISIBLE);
        }

        //Setting Adapter
        listView.setAdapter(groupListCursorAdapter.getAdapter());

        //Adding OnItemclick Listener
        listView.setOnItemClickListener(this);


    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

        viewHolder = new ViewHolder();
        groupBeanList = groupListCursorAdapter.getGroupList();

        //Fetching Group NAME fROM table using ARRAYlIST OF lISTaDAPTER
        groupName = viewHolder.populateName(groupBeanList.get(position));

        //uSING sharedpreference to pass groupname
        DBConstants.groupSharedPrefKey++;
        SharedPreferenceManager sharedPreferenceManager = new SharedPreferenceManager(getActivity());
        sharedPreferenceManager.putString(String.valueOf(DBConstants.groupSharedPrefKey), groupName);
        //----------------------------------------

        //to start Activity Group ON tHE BASIS OFkNOWN/uNKNOWN gROUP tYPE
        groupKnownUnKnownType = viewHolder.populateGroupKnownUnKnownType(groupBeanList.get(position));

        if (groupKnownUnKnownType.equals(DBConstants.GROUP_KNOWN)) {
            startActivity(new Intent(getActivity(), Group.class));
            //Message.message(this, "GroupKnown Started");
        } else if (groupKnownUnKnownType.equals(DBConstants.GROUP_UNKNOWN)) {
            startActivity(new Intent(getActivity(), GroupUnKnown.class));
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

//    public void createGroup(View view)
//    {
//        startActivity(new Intent(getActivity(), CreateUnKnownExpenseGroup.class));
//    }


}