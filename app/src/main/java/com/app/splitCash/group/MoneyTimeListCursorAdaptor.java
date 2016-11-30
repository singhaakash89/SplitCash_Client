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
import com.app.splitCash.dataBase.Message;
import com.app.splitCash.dataBase.MoneyTimeDBAdapter;

import java.util.ArrayList;

/**
 * Created by Aakash Singh on 17-07-2015.
 */
public class MoneyTimeListCursorAdaptor
{
    public Context context;
    private MyAdapter myAdapter;
    private Group group;
    private String MONEYTIME_LIST_TABLE_NAME;

    public MoneyTimeListCursorAdaptor()
    {

    }

    public MoneyTimeListCursorAdaptor(Context context, String MONEYTIME_LIST_TABLE_NAME)
    {
        this.context = context;
        this.MONEYTIME_LIST_TABLE_NAME = MONEYTIME_LIST_TABLE_NAME;
        myAdapter = new MyAdapter(context,getMemberList());
    }

    public ArrayList<MoneyTimeBean> getMemberList()
    {
        MoneyTimeDBAdapter moneyTimeDBAdapter = new MoneyTimeDBAdapter(context);
        ArrayList<MoneyTimeBean> moneyTimeBeanList = new ArrayList<MoneyTimeBean>();

        //Message.message(context, "MONEYTIME_LIST_CursortableName : " + MONEYTIME_LIST_TABLE_NAME);

        Cursor cursor = moneyTimeDBAdapter.cursor(context, MONEYTIME_LIST_TABLE_NAME, DBConstants.MONEYTIME_LIST_TABLE_STRUCTURE);

        if(cursor != null && cursor.getCount() > 0)
        {
            cursor.moveToLast();
            while(!cursor.isBeforeFirst())
            {
                MoneyTimeBean moneyTimeBean = createGroupListFromCursor(cursor);
                moneyTimeBeanList.add(moneyTimeBean);
                cursor.moveToPrevious();
            }
            cursor.close();

        }

        return moneyTimeBeanList;
    }

    private MoneyTimeBean createGroupListFromCursor(Cursor cursor)
    {
        //int id = cursor.getInt(cursor.getColumnIndex(DBConstants.GROUP_LIST_ID));
        //String uId = cursor.getString(cursor.getColumnIndex(DBConstants.GROUP_lIST_UID));

        String moneyTimeName = cursor.getString(cursor.getColumnIndex(DBConstants.MONEYTIME_LIST_NAME));
        String moneyTimeMemberExpense = cursor.getString(cursor.getColumnIndex(DBConstants.MONEYTIME_LIST_EXPENSE));

        String moneyTimeAmountToTake = cursor.getString(cursor.getColumnIndex(DBConstants.MONEYTIME_LIST_TO_TAKE_AMOUNT));
        String moneyTimeAmountToGive = cursor.getString(cursor.getColumnIndex(DBConstants.MONEYTIME_LIST_TO_GIVE_AMOUNT));

        MoneyTimeBean moneyTimeBean = new MoneyTimeBean();
        //groupBean.setUid(uId);
        moneyTimeBean.setMoneyTimeMemberName(moneyTimeName);
        moneyTimeBean.setMoneyTimeMemberExpense(moneyTimeMemberExpense);
        moneyTimeBean.setMoneyTimeMemberToTake(moneyTimeAmountToTake);
        moneyTimeBean.setMoneyTimeMemberToGive(moneyTimeAmountToGive);

        return moneyTimeBean;
    }

//    public void calculateToTakeAndToGive(String moneyTimeMemberExpense)
//    {
//
//    }

    //new MyAdapter(this,new GroupListCursorAdapter().getGroupList(this));

    public MyAdapter getAdapter()
    {
        return myAdapter;
    }


    //-------------------------------MyAdapter Inner class----------------------------------------
    class MyAdapter extends ArrayAdapter<MoneyTimeBean>
    {

        private Context context;
        private ArrayList<MoneyTimeBean> moneyTimeBeanList;

        public MyAdapter(Context context, ArrayList<MoneyTimeBean> moneyTimeBeanList)
        {
            super(context, 0, moneyTimeBeanList);
            this.context = context;
            this.moneyTimeBeanList = moneyTimeBeanList;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent)
        {

            ViewHolder viewHolder;

            if(convertView == null)
            {
                LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = inflater.inflate(R.layout.moneytime_list_row_layout,parent, false);

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

            viewHolder.populateFrom(moneyTimeBeanList.get(position));
            return convertView;

        }


        //-------------------------------ViewHolder Inner Class------------------------------
        class ViewHolder
        {
            public TextView moneyTimeNameTextView;
            public TextView moneyTimeMemberExpenseTextView;
            public TextView moneyTimeAmountToTake;
            public TextView moneyTimeAmountToGive;

            public ViewHolder(View row)
            {
                moneyTimeNameTextView = (TextView) row.findViewById(R.id.moneyTimeMemberNameTextView);
                moneyTimeMemberExpenseTextView = (TextView) row.findViewById(R.id.moneyTimeMemberExpenseTextView);
                moneyTimeAmountToTake = (TextView) row.findViewById(R.id.moneyTimeMemberToTakeTextView);
                moneyTimeAmountToGive = (TextView) row.findViewById(R.id.moneyTimeMemberToGiveTextView);
            }

            void populateFrom(MoneyTimeBean moneyTimeBean)
            {
                moneyTimeNameTextView.setText(moneyTimeBean.getMoneyTimeMemberName());
                moneyTimeMemberExpenseTextView.setText(moneyTimeBean.getMoneyTimeMemberExpense());
                moneyTimeAmountToTake.setText(moneyTimeBean.getMoneyTimeMemberToTake());
                moneyTimeAmountToGive.setText(moneyTimeBean.getMoneyTimeMemberToGive());

            }
        }

    }



}


