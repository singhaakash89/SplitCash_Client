package com.app.splitCash.contacts;

import android.database.Cursor;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v4.widget.SimpleCursorAdapter;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.app.splitCash.R;


public class ContactDisplayActivity extends AppCompatActivity implements AdapterView.OnItemClickListener
{
    private String Dummy = null;  //waste dummy var

    private Toolbar toolbar;
    private ListView listView;
    private Cursor cursor = null;

//---------------------------new code------------------------------------
//
//    public MyAdapter myAdapter;
//    public ArrayList<HashMap<String, String>> hashMapsArrayList = new ArrayList<HashMap<String,String>>();
//    String name, phoneNumber, image, email;

//---------------------------------------------------------------


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact_display);


        //---------ToolBar Implementation-------------
        toolbar = (Toolbar) findViewById(R.id.app_bar);
        setSupportActionBar(toolbar);
        //--------------------------------------------

        //----HomeButtonEnabled-------------------------
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        //---------------------------------------------

//-------------------------------------------------OLD CODE-----------------------------------------------------------------------

        listView = (ListView) findViewById(R.id.listView);

        cursor = getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                null, null, null, ContactsContract.Contacts.DISPLAY_NAME + " ASC ");

        startManagingCursor(cursor);

//        cursor.moveToFirst();
//
//        //Fetching values from cursor
//        int nameIdx = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME);
//        int photoIdIdx = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.PHOTO_URI);
//
//        HashMap<String, String> hashMap = new HashMap<String, String>();
//
//        do
//        {
//            name = cursor.getString(nameIdx);
//            image = cursor.getString(photoIdIdx);
//
//            hashMap.put("name", "" + name);
//            hashMap.put("image", "" + image);
//
//            hashMapsArrayList.add(hashMap);
//
//        } while (cursor.moveToNext());
//
//
//        myAdapter = new MyAdapter(getApplicationContext());
//        listView.setAdapter(myAdapter);
//        listView.setOnItemClickListener(this);


//        //Displaying Name
//      String[] from = {ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME,
//                ContactsContract.CommonDataKinds.Phone.NUMBER,
//                ContactsContract.CommonDataKinds.Phone._ID};

//      int[] to = {android.R.id.text1, android.R.id.text2};

        String[] from = {ContactsContract.CommonDataKinds.Phone.PHOTO_URI,
                ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME};

        int[] to = {R.id.imageView, R.id.nameTextView};


        //SimpleCursorAdapter cursorAdapter = new SimpleCursorAdapter(this, android.R.layout.simple_list_item_2, cursor, from, to);
        SimpleCursorAdapter cursorAdapter = new SimpleCursorAdapter(this, R.layout.contact_list__row_layout, cursor, from, to);

        listView.setAdapter(cursorAdapter);


        //Adding Onclick Listener
        listView.setOnItemClickListener(this);


		//listView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);

//---------------------------------------------ENDS---------------------------------------------------------------------------





//------------------------------------------------------------------------------------------------------------------------------------
//-----------------------------------------------------NEW CODE FOR CURSOR-------------------------------------------------------------------------------
//
//        listView = (ListView) findViewById(R.id.listView);
//
//        try
//        {
//
//            cursor = getApplicationContext().getContentResolver()
//                    .query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
//                            null,
//                            null,
//                            null,
//                            ContactsContract.Contacts.DISPLAY_NAME + " ASC ");
//
//            int nameIdx = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME);
//            int phoneNumberIdx = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER);
//            int photoIdIdx = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.PHOTO_URI);
//
//        cursor.moveToFirst();
//        startManagingCursor(cursor);

      //  HashMap<String, String> hashMap = new HashMap<String, String>();

//        do
//            {
//
//                name = cursor.getString(nameIdx);
//                //phoneNumber = cursor.getString(phoneNumberIdx);
//                image = cursor.getString(photoIdIdx);
//                // email=cursor.getString(emailIDx);
//
//                //if (!phoneNumber.contains("*"))
//
//                hashMap.put("name", "" + name);
//                //hashMap.put("phoneNumber", "" + phoneNumber);
//                hashMap.put("image", "" + image);
//                // hashMap.put("email", ""+email);
//                hashMapsArrayList.add(hashMap);
//
//
//            } while (cursor.moveToNext());


//        }
//
//        catch (Exception e)
//        {
//            e.printStackTrace();
//        }
//        finally
//        {
//            if (cursor != null)
//            {
//                cursor.close();
//            }
//        }
//
//        myAdapter = new MyAdapter(this);
//        listView.setAdapter(myAdapter);
//        listView.setOnItemClickListener(this);
//    //    myAdapter.notifyDataSetChanged();


    }

//------------------------------------------------------------------------------------------------------------------------------------
//-----------------------------------------------------------END-------------------------------------------------------------------------------


    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        // Inflate the menu; this adds items to the action bar if it is present.
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_contact, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        switch (item.getItemId())
        {
            case R.id.settings:
                Toast.makeText(getBaseContext(), R.string.toast_message_setting,
                        Toast.LENGTH_SHORT).show();
                return false;

            case R.id.contactus:
                Toast.makeText(getBaseContext(), R.string.toast_message_contactus,
                        Toast.LENGTH_SHORT).show();
                return false;

            case R.id.about:
                Toast.makeText(getBaseContext(), R.string.toast_message_about, Toast.LENGTH_SHORT).show();
                return false;

            default:
                return super.onOptionsItemSelected(item);
        }

    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id)
    {
//        Intent intent = new Intent(this, ConversationActivity.class);
//        startActivity(intent);


//        Intent intent = new Intent(this,CreateKnownExpenseGroup.class );
//        Bundle bundle = new Bundle();
//
//
//        TextView textView = (TextView) findViewById(R.id.memberTextView);
//
//        ArrayList<String> memberList = new ArrayList<String>();
//        memberList.add(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME);
//
//
//
//        bundle.putString("phoneNumber_Message", phoneNumber);
//
//        intent.putExtras(bundle);
//
//        startActivity(intent);


    }
}



//---------------------------------------------Adapter Class--------------------------------------------------------------------------------

//class MyAdapter extends BaseAdapter
//{
//    private TextView nameTextView;
//    private ImageView imageView;
//    public Context context;
//
//    ContactDisplayActivity contactDisplayActivity = new ContactDisplayActivity();
//    ArrayList<HashMap<String, String>> hashMapsArrayList = new ArrayList<HashMap<String, String>>();
//
//
//    public MyAdapter(Context context)
//    {
//
//        this.context = context;
//        //hashMapsArrayList = contactDisplayActivity.hashMapsArrayList;
//    }
//
//
//    @Override
//    public int getCount() {
//        // TODO Auto-generated method stub
//        return contactDisplayActivity.hashMapsArrayList.size();
//    }
//
//    @Override
//    public Object getItem(int position) {
//        // TODO Auto-generated method stub
//        return contactDisplayActivity.hashMapsArrayList.get(position);
//    }
//
//    @Override
//    public long getItemId(int position) {
//        // TODO Auto-generated method stub
//        return position;
//    }
//
//    @Override
//    public View getView(int position, View convertView, ViewGroup parent)
//    {
//        View row = null;
//        if (convertView == null)
//        {
//            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
//            row = inflater.inflate(R.layout.contact_list__row_layout, null);
//        }
//        else
//        {
//            row = convertView;
//        }
//
//        nameTextView = (TextView) row.findViewById(R.id.nameTextView);
//        imageView = (ImageView) row.findViewById(R.id.imageView);
//
//
//        nameTextView.setText(hashMapsArrayList.get(position).get("name").toString());
//        String string_imageCheck = hashMapsArrayList.get(position).get("image").toString();
//
//        //Checking for an empty Image For Contact
//        if (string_imageCheck.equalsIgnoreCase("null"))
//        {
//            imageView.setImageResource(R.drawable.ic_action_person);
//
//        }
//        else
//        {
//            imageView.setImageURI(Uri.parse(hashMapsArrayList.get(position).get("image")));
//        }
//
//
//
//            return convertView;
//    }
//
//
//
//}

//---------------------------------------------------------------------------------------------------------------------------------




