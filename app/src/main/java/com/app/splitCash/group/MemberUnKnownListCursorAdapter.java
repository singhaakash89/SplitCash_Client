package com.app.splitCash.group;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.app.splitCash.R;
import com.app.splitCash.constants.DBConstants;
import com.app.splitCash.dataBase.MemberDBAdapter;

import java.util.ArrayList;

/**
 * Created by Aakash Singh on 20-07-2015.
 */
public class MemberUnKnownListCursorAdapter
{
    public Context context;
    private MyAdapter myAdapter;
    private Group group;
    private String MEMBER_LIST_TABLE_NAME;

    public MemberUnKnownListCursorAdapter()
    {

    }

    public MemberUnKnownListCursorAdapter(Context context, String MEMBER_LIST_TABLE_NAME)
    {
        this.context = context;
        this.MEMBER_LIST_TABLE_NAME = MEMBER_LIST_TABLE_NAME;
        myAdapter = new MyAdapter(context,getMemberList());
    }

    public Cursor getMemberListCursor(Context context)
    {
        MemberDBAdapter memberDBAdapter = new MemberDBAdapter(context);
        Cursor cursor = memberDBAdapter.cursor(context, MEMBER_LIST_TABLE_NAME, DBConstants.MEMBER_LIST_TABLE_STRUCTURE);
        return cursor;
    }

    public ArrayList<MemberUnKnownBean> getMemberList()
    {
        MemberDBAdapter memberDBAdapter = new MemberDBAdapter(context);
        ArrayList<MemberUnKnownBean> memberUnKnownBeanList = new ArrayList<MemberUnKnownBean>();

//        Message.message(context, "ListCursortableName : "+MEMBER_LIST_TABLE_NAME);

        Cursor cursor = memberDBAdapter.cursor(context, MEMBER_LIST_TABLE_NAME, DBConstants.MEMBER_LIST_TABLE_STRUCTURE);

        if(cursor != null && cursor.getCount() > 0)
        {
            cursor.moveToLast();
            while(!cursor.isBeforeFirst())
            {
            	MemberUnKnownBean memberUnKnownBean = createGroupListFromCursor(cursor);
            	memberUnKnownBeanList.add(memberUnKnownBean);
                cursor.moveToPrevious();
            }
            cursor.close();

        }

        return memberUnKnownBeanList;
    }

    private MemberUnKnownBean createGroupListFromCursor(Cursor cursor)
    {
        //int id = cursor.getInt(cursor.getColumnIndex(DBConstants.GROUP_LIST_ID));
        //String uId = cursor.getString(cursor.getColumnIndex(DBConstants.GROUP_lIST_UID));
        String memberName = cursor.getString(cursor.getColumnIndex(DBConstants.MEMBER_LIST_NAME));
        String memberPhoneNumber = cursor.getString(cursor.getColumnIndex(DBConstants.MEMBER_LIST_PHONE_NUMBER));
        String memberAmountExpensed = cursor.getString(cursor.getColumnIndex(DBConstants.MEMBER_LIST_EXPENSE));

        MemberUnKnownBean memberUnKnownBean = new MemberUnKnownBean();
        //groupBean.setUid(uId);
        memberUnKnownBean.setMemberName(memberName);
        memberUnKnownBean.setMemberPhoneNumber(memberPhoneNumber);
        memberUnKnownBean.setMemberAmountExpensed(memberAmountExpensed);

        return memberUnKnownBean;
    }


    public MyAdapter getAdapter()
    {
        return myAdapter;
    }


    //-------------------------------MyAdapter Inner class----------------------------------------
    class MyAdapter extends ArrayAdapter<MemberUnKnownBean>
    {

        private Context context;
        private ArrayList<MemberUnKnownBean> memberUnKnownBeanList;

        public MyAdapter(Context context, ArrayList<MemberUnKnownBean> memberUnKnownBeanList)
        {
            super(context, 0, memberUnKnownBeanList);
            this.context = context;
            this.memberUnKnownBeanList = memberUnKnownBeanList;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent)
        {

            ViewHolder viewHolder;

            if(convertView == null)
            {
                LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = inflater.inflate(R.layout.group_member_row_layout,parent, false);

                viewHolder = new ViewHolder(convertView);
                //SetTag() used to save convertView in the memory until its pool is full
                //Once pool is full, it create a new convertView with NULL value
                convertView.setTag(viewHolder);
            }
            else
            {
                //Android uses getTag() to retrieve existing convertView in its pool
                viewHolder = (ViewHolder) convertView.getTag();
            }

            viewHolder.populateFrom(memberUnKnownBeanList.get(position));
            return convertView;

        }


        //-------------------------------ViewHolder Inner Class------------------------------
        class ViewHolder
        {
            public TextView memberNameTextView;
            public TextView memberAmountExpensedTextView;

            public ViewHolder(View row)
            {
                memberNameTextView = (TextView) row.findViewById(R.id.memberNameTextView);
                memberAmountExpensedTextView = (TextView) row.findViewById(R.id.memberExpenseTextView);
            }

            void populateFrom(MemberUnKnownBean memberUnKnownBean)
            {
                memberNameTextView.setText(memberUnKnownBean.getMemberName());
                memberAmountExpensedTextView.setText(memberUnKnownBean.getMemberAmountExpensed());

            }
        }

    }




}
