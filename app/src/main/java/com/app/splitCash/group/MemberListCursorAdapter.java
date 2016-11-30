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
 * Created by Aakash Singh on 15-07-2015.
 */
public class MemberListCursorAdapter
{
	public Context context;
	private MyAdapter myAdapter;
	private Group group;
	private String MEMBER_LIST_TABLE_NAME;

	public MemberListCursorAdapter()
	{

	}

	public MemberListCursorAdapter(Context context,
			String MEMBER_LIST_TABLE_NAME)
	{
		this.context = context;
		this.MEMBER_LIST_TABLE_NAME = MEMBER_LIST_TABLE_NAME;
		myAdapter = new MyAdapter(context, getMemberList());
	}

	public Cursor getMemberListCursor(Context context)
	{
		MemberDBAdapter memberDBAdapter = new MemberDBAdapter(context);
		Cursor cursor = memberDBAdapter.cursor(context, MEMBER_LIST_TABLE_NAME,
				DBConstants.MEMBER_LIST_TABLE_STRUCTURE);
		return cursor;
	}

	public ArrayList<MemberBean> getMemberList()
	{
		MemberDBAdapter memberDBAdapter = new MemberDBAdapter(context);
		ArrayList<MemberBean> memberBeanList = new ArrayList<MemberBean>();

		// Message.message(context,
		// "ListCursortableName : "+MEMBER_LIST_TABLE_NAME);

		Cursor cursor = memberDBAdapter.cursor(context, MEMBER_LIST_TABLE_NAME,
				DBConstants.MEMBER_LIST_TABLE_STRUCTURE);

		if (cursor != null && cursor.getCount() > 0)
		{
			cursor.moveToFirst();
			while (!cursor.isAfterLast())
			{
				MemberBean memberBean = createGroupListFromCursor(cursor);
				memberBeanList.add(memberBean);
				cursor.moveToNext();
			}
			cursor.close();

		}

		return memberBeanList;
	}

	private MemberBean createGroupListFromCursor(Cursor cursor)
	{
		// int id =
		// cursor.getInt(cursor.getColumnIndex(DBConstants.GROUP_LIST_ID));
		// String uId =
		// cursor.getString(cursor.getColumnIndex(DBConstants.GROUP_lIST_UID));
		String memberName = cursor.getString(cursor
				.getColumnIndex(DBConstants.MEMBER_LIST_NAME));
		String memberPhoneNumber = cursor.getString(cursor
				.getColumnIndex(DBConstants.MEMBER_LIST_PHONE_NUMBER));
		String memberAmountExpensed = cursor.getString(cursor
				.getColumnIndex(DBConstants.MEMBER_LIST_EXPENSE));

		// //Setting TotalAmount Expensed
		// //Setting Expense Count
		// group = new Group();
		// group.setTotalExpenseCount(Float.valueOf(memberAmountExpensed));

		MemberBean memberBean = new MemberBean();
		// groupBean.setUid(uId);
		memberBean.setMemberName(memberName);
		memberBean.setMemberPhoneNumber(memberPhoneNumber);
		memberBean.setMemberAmountExpensed(memberAmountExpensed);

		return memberBean;
	}

	// new MyAdapter(this,new GroupListCursorAdapter().getGroupList(this));

	public MyAdapter getAdapter()
	{
		return myAdapter;
	}

	// -------------------------------MyAdapter Inner
	// class----------------------------------------
	class MyAdapter extends ArrayAdapter<MemberBean>
	{

		private Context context;
		private ArrayList<MemberBean> memberBeanList;

		public MyAdapter(Context context, ArrayList<MemberBean> memberBeanList)
		{
			super(context, 0, memberBeanList);
			this.context = context;
			this.memberBeanList = memberBeanList;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent)
		{

			ViewHolder viewHolder;

			if (convertView == null)
			{
				LayoutInflater inflater = (LayoutInflater) context
						.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				convertView = inflater.inflate(
						R.layout.group_member_row_layout, parent, false);

				viewHolder = new ViewHolder(convertView);
				// SetTag() used to save convertView in the memory until its
				// pool is full
				// Once pool is full, it create a new convertView with NULL
				// value
				convertView.setTag(viewHolder);
			} else
			{
				// Android uses getTag() to retrieve existing convertView in its
				// pool
				viewHolder = (ViewHolder) convertView.getTag();
			}

			viewHolder.populateFrom(memberBeanList.get(position));

			notifyDataSetChanged();

			return convertView;

		}

		// -------------------------------ViewHolder Inner
		// Class------------------------------
		class ViewHolder
		{
			public TextView memberNameTextView;
			public TextView memberAmountExpensedTextView;

			public ViewHolder(View row)
			{
				memberNameTextView = (TextView) row
						.findViewById(R.id.memberNameTextView);
				memberAmountExpensedTextView = (TextView) row
						.findViewById(R.id.memberExpenseTextView);
			}

			public void populateFrom(MemberBean memberBean)
			{
				memberNameTextView.setText(memberBean.getMemberName());
				memberAmountExpensedTextView.setText(memberBean
						.getMemberAmountExpensed());

			}

		}

	}

}
