package com.app.splitCash.contacts;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.provider.ContactsContract;
import android.util.Log;

/**
 * Created by Aakash Singh on 22-05-2016.
 */
public class ContactPickerManager {

    private static final int REQUEST_CODE = 1;

//    public void getContactInformation()
//    {
//        // do your code
//        Uri uri = Uri.parse("content://contacts");
//        Intent intent = new Intent(Intent.ACTION_PICK, uri);
//        intent.setType(ContactsContract.CommonDataKinds.Phone.CONTENT_TYPE);
////        startActivityForResult(intent, REQUEST_CODE);
//
//    }
//
//    @Override
//    public void onActivityResult(int requestCode, int resultCode,
//                                 Intent intent) {
//
////        if (requestCode == REQUEST_CODE) {
////            if (resultCode == Activity.RESULT_OK) {
////                Uri uri = intent.getData();
////                String[] projection = {ContactsContract.CommonDataKinds.Phone.NUMBER, ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME};
////
////                Cursor cursor = getActivity().getContentResolver().query(uri, projection,
////                        null, null, null);
////                cursor.moveToFirst();
////
////                int numberColumnIndex = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER);
////                String number = cursor.getString(numberColumnIndex);
////
////                int nameColumnIndex = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME);
////                String name = cursor.getString(nameColumnIndex);
////
////                Log.d("++AKS++", "++AKS number++ : " + number + " , name : " + name);
////
//
////            }
////        }
//    }

}
