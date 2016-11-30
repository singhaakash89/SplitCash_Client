package com.app.splitCash.group;

import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.os.AsyncTask;
import android.support.v7.app.AlertDialog;
import android.text.Html;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.app.splitCash.R;
import com.app.splitCash.constants.DBConstants;
import com.app.splitCash.constants.ServerConstants;
import com.app.splitCash.dataBase.MasterDBAdapter;
import com.app.splitCash.dataBase.MemberDBAdapter;
import com.app.splitCash.dataBase.Message;
import com.app.splitCash.postToServer.PostToServer;
import com.app.splitCash.sharedPreferences.SharedPreferenceManager;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Aakash Singh on 20-07-2015.
 */
public class EditListCursorAdapter
{
    public Context context;
    private MyAdapter myAdapter;
    private Group group;
    private String MEMBER_LIST_TABLE_NAME;
    private DBConstants dbConstants;
    
    private Map<String, String> editUserMap;
    
    private String groupNameWithSpace;
    
    //taking (0.4) as initial value as .1, .2, .3 have already been taken for other purposes while Creating Groups and Adding Members to it.
    private BigDecimal LOCAL_MEMBER_PHONE_NUMBER_COUNT = new BigDecimal(0.4);

    public EditListCursorAdapter()
    {

    }

    public EditListCursorAdapter(Context context, DBConstants dbConstants, String MEMBER_LIST_TABLE_NAME, String groupNameWithSpace)
    {
        this.context = context;
        //to use in btn clickListener in the last of the Adapter
        this.dbConstants = dbConstants;
        //======================================================
        this.MEMBER_LIST_TABLE_NAME = MEMBER_LIST_TABLE_NAME;
        
        //***To use for Hiding Delete Member Button for Invited Group**********
        this.groupNameWithSpace = groupNameWithSpace;
        this.editUserMap = new HashMap<String, String>();
        
        myAdapter = new MyAdapter(context, getMemberList());
        
    }
    

    public Cursor getMemberListCursor(Context context)
    {
        MemberDBAdapter memberDBAdapter = new MemberDBAdapter(context);
        Cursor cursor = memberDBAdapter.cursor(context, MEMBER_LIST_TABLE_NAME, DBConstants.MEMBER_LIST_TABLE_STRUCTURE);
        return cursor;
    }

    public ArrayList<MemberBean> getMemberList()
    {
        MemberDBAdapter memberDBAdapter = new MemberDBAdapter(context);
        ArrayList<MemberBean> memberBeanList = new ArrayList<MemberBean>();

//        Message.message(context, "ListCursortableName : "+MEMBER_LIST_TABLE_NAME);

        Cursor cursor = memberDBAdapter.cursor(context, MEMBER_LIST_TABLE_NAME, DBConstants.MEMBER_LIST_TABLE_STRUCTURE);

        if (cursor != null && cursor.getCount() > 0)
        {
            cursor.moveToLast();
            while (!cursor.isBeforeFirst())
            {
                MemberBean memberBean = createGroupListFromCursor(cursor);
                memberBeanList.add(memberBean);
                cursor.moveToPrevious();
            }
            cursor.close();

        }

        return memberBeanList;
    }

    private MemberBean createGroupListFromCursor(Cursor cursor)
    {
        //int id = cursor.getInt(cursor.getColumnIndex(DBConstants.GROUP_LIST_ID));
        //String uId = cursor.getString(cursor.getColumnIndex(DBConstants.GROUP_lIST_UID));
        String memberName = cursor.getString(cursor.getColumnIndex(DBConstants.MEMBER_LIST_NAME));
        String memberAmountExpensed = cursor.getString(cursor.getColumnIndex(DBConstants.MEMBER_LIST_EXPENSE));
        String memberPhoneNumber = cursor.getString(cursor.getColumnIndex(DBConstants.MEMBER_LIST_PHONE_NUMBER));
        
        MemberBean memberBean = new MemberBean();
        //groupBean.setUid(uId);
        memberBean.setMemberName(memberName);
        memberBean.setMemberPhoneNumber(memberPhoneNumber);
        memberBean.setMemberAmountExpensed(memberAmountExpensed);

        return memberBean;
    }


    public MyAdapter getAdapter()
    {
        return myAdapter;
    }


    //-------------------------------MyAdapter Inner class----------------------------------------
    class MyAdapter extends ArrayAdapter<MemberBean>
    {

        private Context context;
        //for NotifysetDataChanged()
        private MyAdapter myAdapterNew;
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
            final int pos = position;
            final ViewHolder viewHolder;
            
            //Fetching Device Owner
            SharedPreferenceManager sharedPreferenceManager = new SharedPreferenceManager(context);
            final String updateOwnerNumber = sharedPreferenceManager.getString(DBConstants.USER_PHONE_NUMBER);
            final String updateOwnerName = sharedPreferenceManager.getString(DBConstants.USER_NAME);

            if (convertView == null)
            {
                LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = inflater.inflate(R.layout.group_member_edit_row_layout, parent, false);

                //Passing ConverView to ViewHolder for Setting data to Row
                viewHolder = new ViewHolder(convertView);


                //SetTag() used to save convertView in the memory until its pool is full
                //Once pool is full, it create a new convertView with NULL value
                convertView.setTag(viewHolder);

            } else
            {
                //Android uses getTag() to retrieve existing convertView in its pool
                viewHolder = (ViewHolder) convertView.getTag();
            }

            viewHolder.populateFrom(memberBeanList.get(position));


            //ADDING DELETE and Add functionality to List
            ImageView deleteBtn = (ImageView) convertView.findViewById(R.id.deleteMemberRow);
            ImageView addBtn = (ImageView) convertView.findViewById(R.id.addAmountToRow);
            ImageView subBtn = (ImageView) convertView.findViewById(R.id.subtractAmountFromRow);
            
            
    		//*******************Checking for Invited Group**************************************************
//    		SharedPreferenceManager sharedPreferenceManager = new SharedPreferenceManager(context);
    		String flag = "";
    		flag = sharedPreferenceManager.getString(groupNameWithSpace);
    		
    		//Log.e("flag = ", "" +flag);

    		// Hiding Delete Button For Invited Group
    		if (flag.equals(groupNameWithSpace))
    		{
    			Log.e("flag(inSide Menu) = ", "" +flag);
    			deleteBtn.setVisibility(ImageView.GONE);
    		}
    		//**************************************************************************************************



            deleteBtn.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    final String groupMemberName = viewHolder.populateName(memberBeanList.get(pos));
                    final String groupMemberPhoneNumber = viewHolder.populatePhoneNumber(memberBeanList.get(pos));
                    
                    LOCAL_MEMBER_PHONE_NUMBER_COUNT = new BigDecimal(0.4);
                    
                    new AlertDialog.Builder(context)
                            .setTitle("Delete Member")
                            .setMessage(Html.fromHtml("Do you want to delete <b>" + groupMemberName + "</b> ?"))
                            .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener()
                            {
                                public void onClick(DialogInterface dialog, int which)
                                {
                                    // continue with delete

                                    //Calling Method of MEMber Adapter to delete the row
                                    new MemberDBAdapter().deleteRowFromTable(new MasterDBAdapter(context), dbConstants.getMEMBER_LIST_TABLE_NAME(), groupMemberPhoneNumber);

                                    //deleting from the List
                                    memberBeanList.remove(pos);
                                    //Notifying TABLE FOR tHE CHANGE happened
                                    notifyDataSetChanged();
                                    Message.message(context, Html.fromHtml("Member <b>" + groupMemberName + "</b>") + " Deleted");
                                    
                                    //**********Sending Update to Server**************************
                                    editUserMap.put(ServerConstants.GROUP_NAME, groupNameWithSpace);
                                    editUserMap.put(ServerConstants.MEMBER_TO_UPDATE, groupMemberPhoneNumber);

                                    //to get to know who is aDMIN
                                    editUserMap.put(ServerConstants.GROUP_ADMIN, updateOwnerName);                                 
                                    editUserMap.put(ServerConstants.GROUP_ADMIN_PHONE_NUMBER, updateOwnerNumber);

                                    
                                    //Log.e("groupMemberPhoneNumber(From Sender Client Device)", ""+groupMemberPhoneNumber);
                                    
                                    Log.e("updateOwnerName", ""+updateOwnerName);
                                    Log.e("upateOwnerNumber", ""+updateOwnerNumber);
                                    
                                    Log.e("groupNameWithSpace", ""+groupNameWithSpace);
                                    
                                    //Now Adiing All Users of the Group to MAP
                                    MemberDBAdapter memberDBAdapter = new MemberDBAdapter(context);
                                    
            						Cursor cursor = memberDBAdapter.cursor(
            								context, MEMBER_LIST_TABLE_NAME,
            								DBConstants.MEMBER_LIST_TABLE_STRUCTURE);
            						
            						
            						cursor.moveToFirst();
            						
            						while (!cursor.isAfterLast())
            						{
            							Log.e("updateOwnerNumber", ""+updateOwnerNumber);
            							
            							//Adding only Members 
            							if(cursor.getString(cursor.getColumnIndex(DBConstants.MEMBER_LIST_PHONE_NUMBER)).equals(updateOwnerNumber) == false)
            							{
                							editUserMap.put(String.valueOf(LOCAL_MEMBER_PHONE_NUMBER_COUNT),
                									cursor.getString(cursor.getColumnIndex(DBConstants.MEMBER_LIST_PHONE_NUMBER)));
            							
               								//Incrementing BigDecimal
                							LOCAL_MEMBER_PHONE_NUMBER_COUNT = LOCAL_MEMBER_PHONE_NUMBER_COUNT.add(new BigDecimal(1));

            							}

            							
            							cursor.moveToNext();
            						}

            						cursor.close();
            						
            						//Now Adding 

                                    
                                    //Posting to Server
                    				AsyncTask<Map<String, String>, Void, Boolean> mPostTask = new AsyncTask<Map<String, String>, Void, Boolean>()
                    						{

                    							@Override
                    							protected Boolean doInBackground(Map<String, String>... editUserMap)
                    							{
                    								boolean postResult = false;
                    								
                    								final Map<String, String> editUserMapNew = editUserMap[0];
                    								
                    								PostToServer postToServer = new PostToServer();
                    								postResult = postToServer.sendToServerOnMemberDelete(editUserMapNew);
                    										
                    								Log.e("postResult : ", "" +postResult);		
                    								return postResult;
                    							}

                    							@Override
                    							protected void onPostExecute(Boolean result)
                    							{
                    								if (result)
                    								{
                    									Message.message(context, "Member Deleted Suucessfully");

                    								} else
                    								{
                    									Message.message(context, "Member Deletion FAILED...!!!");

                    								}

                    							}

                    						};

                    						mPostTask.execute(editUserMap);
                                    //************************************************************

                                    

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

            addBtn.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {

                	//Fetching the Member Name and  Earlier Expense
                    final String groupMemberName = viewHolder.populateName(memberBeanList.get(pos));
                    final String groupMemberExpense = viewHolder.populateExpense(memberBeanList.get(pos));
                    final String groupMemberPhoneNumber = viewHolder.populatePhoneNumber(memberBeanList.get(pos));
                    
                    LOCAL_MEMBER_PHONE_NUMBER_COUNT = new BigDecimal(0.4);
                    
                    Log.e("pos : ", ""+pos);
                    Log.e("groupMemberName : ", ""+viewHolder.populateName(memberBeanList.get(pos)));
                    Log.e("groupMemberExpense : ", ""+viewHolder.populateExpense(memberBeanList.get(pos)));
                    Log.e("groupMemberPhoneNumber : ", ""+viewHolder.populatePhoneNumber(memberBeanList.get(pos)));


                    AlertDialog.Builder builder = new AlertDialog.Builder(context);
                    builder.setTitle("Edit Amount");
                    builder.setMessage(Html.fromHtml("Add more amount to <b>" + groupMemberName + "</b> ?"));

                    // Set up the input
                    final EditText input = new EditText(context);

                    // Specify the type of input expected; this, for example, sets the input as a password, and will mask the text
                    input.setInputType(InputType.TYPE_CLASS_NUMBER);
                    builder.setView(input);

                    // Set up the buttons
                    builder.setPositiveButton("OK", new DialogInterface.OnClickListener()
                    {

                        @Override
                        public void onClick(DialogInterface dialog, int which)
                        {
                            final Context mContext = context;
                            final String textInput = input.getText().toString();
                            
                            if (textInput.isEmpty())
                            {
                                Message.message(context, "No Amount Entered");
                            } else
                            {
                                //Setting MemberBean for Expense VALUE AND then Returning the ContentVALUES FOR uPDATE()
                                MemberBean memberBean = new MemberBean();
                                int NewExpense = Integer.parseInt(groupMemberExpense) + Integer.parseInt(textInput);

                                //this is necessary to make memberList having the name and amount together to make it working with the adapter at NotifySetDATAChanged().
                                memberBean.setMemberName(String.valueOf(groupMemberName));
                                
                                //V.IMP - it this step gets missed, groupMemberPhoneNunber will become "Null" after each update
                                //Always save all attributes of Bean While saving it.
                                memberBean.setMemberPhoneNumber(String.valueOf(groupMemberPhoneNumber));
                                
                                //Main updation for amount added
                                memberBean.setMemberAmountExpensed(String.valueOf(NewExpense));
                                
                                //Creating Content VALUES
                                ContentValues cv = memberBean.contentValuesExpense();

                                //cALLING UPDATE() OF mEMBER Adapter
                                new MemberDBAdapter().editExpenseColumn(new MasterDBAdapter(context), dbConstants.getMEMBER_LIST_TABLE_NAME(), groupMemberPhoneNumber, cv);

                                //nOTIFYING THE LiST aDAPTER FOR cHANGE
                                memberBeanList.remove(pos);
                                memberBeanList.add(pos, memberBean);
                                notifyDataSetChanged();

                                Message.message(context, "" + Html.fromHtml("Amount Added to <b>" + groupMemberName + "</b>"));
                            
                                //**********Sending Update to Server**************************
                                editUserMap.put(ServerConstants.GROUP_NAME, MEMBER_LIST_TABLE_NAME);
                                editUserMap.put(ServerConstants.MEMBER_TO_UPDATE, groupMemberPhoneNumber);
                                
                                Log.e("groupMemberPhoneNumber(From Sender Client Device)", ""+groupMemberPhoneNumber);
                                
                                editUserMap.put(ServerConstants.MEMBER_AMOUNT_UPDATE, String.valueOf(textInput));
                                editUserMap.put(ServerConstants.OPERATION_TO_UPDATE, ServerConstants.ADDITION_AMOUNT_UPDATE);
                                
                                //to get to know who is updating the Amount
                                editUserMap.put(ServerConstants.UPDATE_OWNER, updateOwnerName);
                                
                                //Now Adiing All Users of the Group to MAP
                                MemberDBAdapter memberDBAdapter = new MemberDBAdapter(context);
                                
        						Cursor cursor = memberDBAdapter.cursor(
        								context, MEMBER_LIST_TABLE_NAME,
        								DBConstants.MEMBER_LIST_TABLE_STRUCTURE);
        						
        						
        						cursor.moveToFirst();
        						
        						while (!cursor.isAfterLast())
        						{
        							Log.e("updateOwnerNumber", ""+updateOwnerNumber);
        							
        							//Adding only Members 
        							if(cursor.getString(cursor.getColumnIndex(DBConstants.MEMBER_LIST_PHONE_NUMBER)).equals(updateOwnerNumber) == false)
        							{
            							editUserMap.put(String.valueOf(LOCAL_MEMBER_PHONE_NUMBER_COUNT),
            									cursor.getString(cursor.getColumnIndex(DBConstants.MEMBER_LIST_PHONE_NUMBER)));
        							
           								//Incrementing BigDecimal
            							LOCAL_MEMBER_PHONE_NUMBER_COUNT = LOCAL_MEMBER_PHONE_NUMBER_COUNT.add(new BigDecimal(1));

        							}

        							
        							cursor.moveToNext();
        						}

        						cursor.close();
        						
        						//Now Adding 

                                
                                //Posting to Server
                				AsyncTask<Map<String, String>, Void, Boolean> mPostTask = new AsyncTask<Map<String, String>, Void, Boolean>()
                						{

                							@Override
                							protected Boolean doInBackground(Map<String, String>... editUserMap)
                							{
                								boolean postResult = false;
                								
                								final Map<String, String> editUserMapNew = editUserMap[0];
                								
                								PostToServer postToServer = new PostToServer();
                								postResult = postToServer.sendToServerForMemberAmountUpdate(editUserMapNew);
                										
                								Log.e("postResult : ", "" +postResult);		
                								return postResult;
                							}

                							@Override
                							protected void onPostExecute(Boolean result)
                							{
                								if (result)
                								{
                									Message.message(context, "Amount Updated");

                								} else
                								{
                									Message.message(context, "Amount Not Updated");

                								}

                							}

                						};

                						mPostTask.execute(editUserMap);
                                //************************************************************
                            }
                        }

                    });

                    builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener()
                    {
                        @Override
                        public void onClick(DialogInterface dialog, int which)
                        {
                            dialog.cancel();
                        }
                    });

                    builder.show();


                }
            });


            //Deleting Amount from the Group
            subBtn.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    //Fetching the Member Name and  Earlier Expense
                    final String groupMemberName = viewHolder.populateName(memberBeanList.get(pos));
                    final String groupMemberExpense = viewHolder.populateExpense(memberBeanList.get(pos));
                    final String groupMemberPhoneNumber = viewHolder.populatePhoneNumber(memberBeanList.get(pos));
                    
                    LOCAL_MEMBER_PHONE_NUMBER_COUNT = new BigDecimal(0.4);


                    AlertDialog.Builder builder = new AlertDialog.Builder(context);
                    builder.setTitle("Edit Amount");
                    builder.setMessage(Html.fromHtml("Delete amount from <b>" + groupMemberName + "</b> ?"));

                    // Set up the input
                    final EditText input = new EditText(context);

                    // Specify the type of input expected; this, for example, sets the input as a password, and will mask the text
                    input.setInputType(InputType.TYPE_CLASS_NUMBER);
                    builder.setView(input);

                    // Set up the buttons
                    builder.setPositiveButton("OK", new DialogInterface.OnClickListener()
                    {

                        @Override
                        public void onClick(DialogInterface dialog, int which)
                        {
                            final Context mContext = context;
                            final String textInput = input.getText().toString();

                            if (textInput.isEmpty())
                            {
                                Message.message(context, "No Amount Entered");
                            } else
                            {
                                //Setting MemberBean for Expense VALUE AND then Returning the ContentVALUES FOR uPDATE()
                                MemberBean memberBean = new MemberBean();
                                int NewExpense = Integer.parseInt(groupMemberExpense) - Integer.parseInt(textInput);

                                //this is necessary to make memberList having the name and amount together to make it working with the adapter at NotifySetDATAChanged().
                                memberBean.setMemberName(String.valueOf(groupMemberName));
                                
                                //V.IMP - it this step gets missed, groupMemberPhoneNunber will become "Null" after each update
                                //Always save all attributes of Bean While saving it.
                                memberBean.setMemberPhoneNumber(String.valueOf(groupMemberPhoneNumber));


                                //Main updation for amount added
                                memberBean.setMemberAmountExpensed(String.valueOf(NewExpense));
                                ContentValues cv = memberBean.contentValuesExpense();

                                //cALLING UPDATE() OF mEMBER Adapter
                                new MemberDBAdapter().editExpenseColumn(new MasterDBAdapter(context), dbConstants.getMEMBER_LIST_TABLE_NAME(), groupMemberPhoneNumber, cv);

                                //nOTIFYING THE iST aDAPTER FOR cHANGE
                                memberBeanList.remove(pos);
                                memberBeanList.add(pos, memberBean);
                                notifyDataSetChanged();

                                Message.message(context, "" + Html.fromHtml("Amount Deleted from <b>" + groupMemberName + "</b>"));
                                
                                
                                //**********Sending Update to Server**************************
                                editUserMap.put(ServerConstants.GROUP_NAME, MEMBER_LIST_TABLE_NAME);
                                editUserMap.put(ServerConstants.MEMBER_TO_UPDATE, groupMemberPhoneNumber);
                                
                                Log.e("groupMemberPhoneNumber(From Sender Client Device)", ""+groupMemberPhoneNumber);
                                
                                editUserMap.put(ServerConstants.MEMBER_AMOUNT_UPDATE, String.valueOf(textInput));
                                editUserMap.put(ServerConstants.OPERATION_TO_UPDATE, ServerConstants.SUBTRACTION_AMOUNT_UPDATE);
                                
                                //to get to know who is updating the Amount
                                editUserMap.put(ServerConstants.UPDATE_OWNER, updateOwnerName);
                                
                                //Now Adiing All Users of the Group to MAP
                                MemberDBAdapter memberDBAdapter = new MemberDBAdapter(context);
                                
        						Cursor cursor = memberDBAdapter.cursor(
        								context, MEMBER_LIST_TABLE_NAME,
        								DBConstants.MEMBER_LIST_TABLE_STRUCTURE);
        						
        						
        						cursor.moveToFirst();
        						
        						while (!cursor.isAfterLast())
        						{
        							Log.e("updateOwnerNumber", ""+updateOwnerNumber);
        							
        							//Adding only Members 
        							if(cursor.getString(cursor.getColumnIndex(DBConstants.MEMBER_LIST_PHONE_NUMBER)).equals(updateOwnerNumber) == false)
        							{
            							editUserMap.put(String.valueOf(LOCAL_MEMBER_PHONE_NUMBER_COUNT),
            									cursor.getString(cursor.getColumnIndex(DBConstants.MEMBER_LIST_PHONE_NUMBER)));
        							
           								//Incrementing BigDecimal
            							LOCAL_MEMBER_PHONE_NUMBER_COUNT = LOCAL_MEMBER_PHONE_NUMBER_COUNT.add(new BigDecimal(1));

        							}

        							
        							cursor.moveToNext();
        						}

        						cursor.close();
        						
        						//Now Adding 

                                
                                //Posting to Server
                				AsyncTask<Map<String, String>, Void, Boolean> mPostTask = new AsyncTask<Map<String, String>, Void, Boolean>()
                						{

                							@Override
                							protected Boolean doInBackground(Map<String, String>... editUserMap)
                							{
                								boolean postResult = false;
                								
                								final Map<String, String> editUserMapNew = editUserMap[0];
                								
                								PostToServer postToServer = new PostToServer();
                								postResult = postToServer.sendToServerForMemberAmountUpdate(editUserMapNew);
                										
                								Log.e("postResult : ", "" +postResult);		
                								return postResult;
                							}

                							@Override
                							protected void onPostExecute(Boolean result)
                							{
                								if (result)
                								{
                									Message.message(context, "Amount Updated");

                								} else
                								{
                									Message.message(context, "Amount Not Updated");

                								}

                							}

                						};

                						mPostTask.execute(editUserMap);
                                //************************************************************

                                
                            }
                        }

                    });

                    builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener()
                    {
                        @Override
                        public void onClick(DialogInterface dialog, int which)
                        {
                            dialog.cancel();
                        }
                    });

                    builder.show();


                }
            });


            return convertView;

        }


        //-------------------------------ViewHolder Inner Class------------------------------
        class ViewHolder
        {
            public TextView memberNameTextView;
            public TextView memberAmountExpensedTextView;

            public ViewHolder(View row)
            {
                memberNameTextView = (TextView) row.findViewById(R.id.memberNameTextViewEdit);
                memberAmountExpensedTextView = (TextView) row.findViewById(R.id.memberExpenseTextViewEdit);
            }

            void populateFrom(MemberBean memberBean)
            {
                memberNameTextView.setText(memberBean.getMemberName());
                memberAmountExpensedTextView.setText(memberBean.getMemberAmountExpensed());

            }

            public String populateName(MemberBean memberBean)
            {
            	Log.e("InSide populateName : ", ""+memberBean);
                return memberBean.getMemberName();
            }

            public String populateExpense(MemberBean memberBean)
            {
                return memberBean.getMemberAmountExpensed();
            }
            
            public String populatePhoneNumber(MemberBean memberBean)
            {
            	Log.e("InSide populatePhoneNumber : ", ""+memberBean);
            	return memberBean.getMemberPhoneNumber();
            }

        }

    }


}
