package com.app.splitCash.group;

import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.support.v7.app.AlertDialog;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.app.splitCash.R;
import com.app.splitCash.constants.DBConstants;
import com.app.splitCash.dataBase.MasterDBAdapter;
import com.app.splitCash.dataBase.MemberDBAdapter;
import com.app.splitCash.dataBase.Message;
import com.app.splitCash.dataBase.MoneyTimeDBAdapter;

import java.util.ArrayList;

/**
 * Created by Aakash Singh on 14-07-2015.
 */
public class GroupListCursorAdapter
{
    public GroupUnKnown groupUnKnown;
    public Context context;
    private MyAdapter myAdapter;
    private TextView textView;

    public GroupListCursorAdapter(Context context)
    {
        this.context = context;
        myAdapter = new MyAdapter(context, getGroupList());
    }

    public Cursor getGroupListCursor(Context context)
    {
        MasterDBAdapter masterDbAdapter = new MasterDBAdapter(context);
        Cursor cursor = masterDbAdapter.cursor(DBConstants.GROUP_LIST_TABLE_NAME, DBConstants.GROUP_LIST_TABLE_STRUCTURE);
        return cursor;
    }

    public ArrayList<GroupBean> getGroupList()
    {
        MasterDBAdapter masterDbAdapter = new MasterDBAdapter(context);
        ArrayList<GroupBean> groupBeanList = new ArrayList<GroupBean>();
        Cursor cursor = masterDbAdapter.cursor(DBConstants.GROUP_LIST_TABLE_NAME, DBConstants.GROUP_LIST_TABLE_STRUCTURE);

        if (cursor != null && cursor.getCount() > 0)
        {
            cursor.moveToLast();
            while (!cursor.isBeforeFirst())
            {
                GroupBean groupBean = createGroupListFromCursor(cursor);
                groupBeanList.add(groupBean);
                cursor.moveToPrevious();
            }
            cursor.close();

        }

        return groupBeanList;
    }

    private GroupBean createGroupListFromCursor(Cursor cursor)
    {
        //int id = cursor.getInt(cursor.getColumnIndex(DBConstants.GROUP_LIST_ID));
        //String uId = cursor.getString(cursor.getColumnIndex(DBConstants.GROUP_lIST_UID));
        String groupName = cursor.getString(cursor.getColumnIndex(DBConstants.GROUP_LIST_NAME));
        String expense = cursor.getString(cursor.getColumnIndex(DBConstants.GROUP_LIST_EXPENSE));
        String groupType = cursor.getString(cursor.getColumnIndex(DBConstants.GROUP_LIST_TYPE));
        String groupDescription = cursor.getString(cursor.getColumnIndex(DBConstants.GROUP_LIST_DESCRIPTION));
        String groupKnownUnKnownType = cursor.getString(cursor.getColumnIndex(DBConstants.GROUP_LIST_KNOWN_UNKNOWN_TYPE));

        GroupBean groupBean = new GroupBean();
        //groupBean.setUid(uId);
        groupBean.setNameGroup(groupName);
        groupBean.setExpenseInGroup(expense);


        groupBean.setGroupType(groupType);
        groupBean.setGroupDescription(groupDescription);
        groupBean.setGroupKnownUnknownType(groupKnownUnKnownType);

        return groupBean;
    }

    public MyAdapter getAdapter()
    {
        return myAdapter;
    }


    //-------------------------------MyAdapter Inner class----------------------------------------
    public class MyAdapter extends ArrayAdapter<GroupBean>
    {

        private Context context;
        private ArrayList<GroupBean> groupBeanList;
        
        private int position;

        

        public MyAdapter(Context context, ArrayList<GroupBean> groupBeanList)
        {
            super(context, 0, groupBeanList);
            this.context = context;
            this.groupBeanList = groupBeanList;
        }
        
        public void notifyAdapterForDataChange(GroupBean groupBean, ArrayList<GroupBean> groupBeanListNew)
        {
        	Log.e("notifyAdapterForDataChange(Inside) : ", "notifyAdapterForDataChange");
        	
           
        	//Notifying Adapter for data change
            //nOTIFYING THE LiST aDAPTER FOR cHANGE
        	
        	groupBeanList = groupBeanListNew;        	
        	
        	groupBeanList.remove(position);
        	
        	groupBeanList.add(position, groupBean);
        	
        	notifyDataSetChanged();
        	
        	
        	//**********************************
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent)
        {
        	this.position = position;
        	
            final int pos = position;
            final ViewHolder viewHolder;

            if (convertView == null)
            {
                LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = inflater.inflate(R.layout.group_list_row_layout, parent, false);

                viewHolder = new ViewHolder(convertView);
                //SetTag() used to save convertView in the memory until its pool is full
                //Once pool is full, it create a new convertView with NULL value
                convertView.setTag(viewHolder);
            } else
            {
                //Android uses getTag() to retrieve existing convertView in its pool
                viewHolder = (ViewHolder) convertView.getTag();
            }
                        

            
            viewHolder.populateFrom(groupBeanList.get(position));

            
            
            
             //dELETE gROUP fUNCTIONALITY
            //ADDING DELETE and Add functionality to List
            ImageView deleteBtn = (ImageView) convertView.findViewById(R.id.deleteMemberRow);
//            ImageView addBtn = (ImageView) convertView.findViewById(R.id.addAmountToRow);

            deleteBtn.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    final String groupName = viewHolder.populateName(groupBeanList.get(pos));

                    new AlertDialog.Builder(context)
                            .setTitle("Delete Group")
                             //Making part of text as Bold
                            //setMessage(Html.fromHtml("Hello "+"<b>"+"World"+"</b>"));
                            .setMessage(Html.fromHtml("Do you want to delete <b>" + groupName + "</b> ?"))
                            .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener()
                            {
                                public void onClick(DialogInterface dialog, int which)
                                {
                                    // continue with delete

                                    //Calling Method of MEMber Adapter to delete the row
                                    new MasterDBAdapter(context).deleteRowFromTable(groupName);

                                    //deleting from the List
                                    groupBeanList.remove(pos);

                                    //Also delete group with this name in Member and MoneyTime TABLE=============
                                    //Member table dropped
                                    new MemberDBAdapter(context).dropTableIfExists(new MasterDBAdapter(context), groupName.replaceAll("\\s+",""));
                                    //Message.message(context, "Member Table Dropped");

                                    //MoneyTime table Dropped
                                    DBConstants dbConstants = new DBConstants(context);
                                    String groupNameNew = groupName.replaceAll("\\s+","").concat(dbConstants.MONEYTIME_LIST_TABLE_NAME);
                                    dbConstants.setMEMBER_LIST_TABLE_NAME(groupNameNew);
                                    new MoneyTimeDBAdapter(context).dropTableIfExists(new MasterDBAdapter(context), dbConstants);
                                    //Message.message(context, "MoneyTime Table Dropped");
                                    //===============================================================

                                    //Notifying TABLE FOR tHE CHANGE happened
                                    notifyDataSetChanged();
                                    Message.message(context, ""+Html.fromHtml("Group <b>"+groupName+"</b> Deleted "));

                                }
                            })
                            .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener()
                            {
                                public void onClick(DialogInterface dialog, int which)
                                {
                                    Message.message(context, "Deletion Cancelled");
                                }
                            })
                            .setIcon(android.R.drawable.ic_dialog_alert)
                            .show();


                }
            });



            return convertView;

        }


        //-------------------------------ViewHolder Inner Class------------------------------
        class ViewHolder
        {
            private TextView groupNameTextView;
            private TextView expenseTextView;
            private TextView groupTypeTextView;
            private TextView groupDescriptionTextView;

            public ViewHolder(View row)
            {
                groupNameTextView = (TextView) row.findViewById(R.id.groupNameTextView);
                expenseTextView = (TextView) row.findViewById(R.id.expenseTextView);
                groupTypeTextView = (TextView) row.findViewById(R.id.groupSpinnerTextView);
                groupDescriptionTextView = (TextView) row.findViewById(R.id.groupDescriptionTextView);
            }

            void populateFrom(GroupBean groupBean)
            {
                groupNameTextView.setText(groupBean.getNameGroup());
                expenseTextView.setText(groupBean.getExpenseInGroup());
                groupTypeTextView.setText(groupBean.getGroupType());
                groupDescriptionTextView.setText(groupBean.getGroupDescription());
            }

            public String populateName(GroupBean groupBean)
            {
                return groupBean.getNameGroup();
            }
        }

    }


}


