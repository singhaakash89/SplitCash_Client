package com.app.splitCash.postToServer;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import com.app.splitCash.constants.Configuration;
import com.app.splitCash.constants.DBConstants;
import com.app.splitCash.constants.ServerConstants;
import com.app.splitCash.dataBase.UserDBAdapter;
import com.app.splitCash.group.AddMemberToGroup;
import com.app.splitCash.sharedPreferences.SharedPreferenceManager;
import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;

import android.annotation.SuppressLint;
import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.util.Log;

public class PostToServer {

    int i;
    static boolean success = false;
    static boolean result = false;
    private Bitmap bitmap;


    public PostToServer() {
    }


    public boolean receiveUserImageURL(String phoneNumber)
    {
        Map<String,String> postMap = new HashMap<>();
        postMap.put(ServerConstants.USER_PHONE_NUMBER,phoneNumber);
        String serverURL = Configuration.SERVER_URL
                + "/sendUserImageToDevice";

        Log.e("Inside receiveUserImageURL", "Inside receiveUserImageURL()");


        // /pOSTING TO Server
        URL url;
        try {
            url = new URL(serverURL);
        } catch (MalformedURLException e) {
            throw new IllegalArgumentException("invalid url: " + serverURL);
        }
        StringBuilder bodyBuilder = new StringBuilder();
        Iterator<Entry<String, String>> iterator = postMap.entrySet()
                .iterator();

        // constructs the POST body using the parameters
        while (iterator.hasNext()) {
            Entry<String, String> param = iterator.next();
            bodyBuilder.append(param.getKey()).append('=')
                    .append(param.getValue());
            if (iterator.hasNext()) {
                bodyBuilder.append('&');
            }
        }

        String body = bodyBuilder.toString();

        Log.e("AppServer = ", "Posting [" + body + "] to " + url);

        byte[] bytes = body.getBytes();

        HttpURLConnection conn = null;

        try {
            conn = (HttpURLConnection) url.openConnection();
            conn.setDoOutput(true);
            conn.setDoInput(true);
            conn.setUseCaches(false);
            conn.setFixedLengthStreamingMode(bytes.length);
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type",
                    "application/x-www-form-urlencoded;charset=UTF-8");

            // post the request
            OutputStream out = conn.getOutputStream();
            out.write(bytes);
            out.close();
            Log.e("HTTP = ", "Bytes written");

            // handle the response
            int status = conn.getResponseCode();
            Log.e("status = ", "" + status);

            String response_msg = conn.getResponseMessage();
            Log.e("ResponseMsg = ", response_msg);

            if (response_msg.equals("OK")) {
                success = true;
            }

            BufferedReader in = new BufferedReader(new InputStreamReader(
                    conn.getInputStream()));
            String inputLine;
            StringBuffer response = new StringBuffer();

            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }

            // Log.e("response(from Server) = ", response_msg.toString());

            in.close();

            if (status != 200) {
                throw new IOException("Post failed with error code " + status);
            }
        } finally {
            if (conn != null) {
                conn.disconnect();

            }
            return success;
        }

    }

    public String registerUserImageOnCloudInary(Context mContext, String userPhoneNumberForRegistration)
    {
        //for URL
        String userImageURL = null;
        //for imagePath from DB
        String imagePath = null;
        //for storing URL coming from server
        Map<String, Object> cloudInaryMap = new HashMap<>();
        //for Initializing CloudInary Server Deatails
        Map<String, String> imageMap = new HashMap<String, String>();
        //Fetching userImagePath
        UserDBAdapter userDBAdapter = new UserDBAdapter(mContext);
        Cursor cursor = userDBAdapter.cursor(mContext, DBConstants.USER_TABLE_NAME_STRUCTURE);
        if ((cursor.getCount() > 0) && cursor.isBeforeFirst()) {
            cursor.moveToFirst();
            imagePath = cursor.getString(cursor.getColumnIndex(DBConstants.USER_IMAGE_PATH));
        }
        cursor.close();

        Log.d("userPhoneNumberForRegistration : ", ""+userPhoneNumberForRegistration);

        //Sending imagePath to CloudInary Server for URL
        File file = new File(imagePath);
        //storing Server details in MAP
        imageMap.put("cloud_name",Configuration.CLOUD_NAME);
        imageMap.put("api_key",Configuration.API_KEY);
        imageMap.put("api_secret",Configuration.API_SECRET);
        Cloudinary cloudinary = new Cloudinary(imageMap);
        try {
            //cloudInaryMap = cloudinary.uploader().upload(file, ObjectUtils.emptyMap());
            Map params = ObjectUtils.asMap("public_id", userPhoneNumberForRegistration);
            cloudInaryMap = cloudinary.uploader().upload(file, params);
            if(cloudinary!=null)
            {
                for(Map.Entry<String, Object> entry : cloudInaryMap.entrySet())
                {   //print keys and values
                    Log.d("(CLOUDiNARY_MAP) key : ",""+entry.getKey()+ ", value : " +entry.getValue()+ "\n\n");
                    //System.out.println(entry.getKey() + " : " +entry.getValue());
                }

                userImageURL = (String) cloudInaryMap.get("url");
                Log.d("userImageURL",userImageURL);
            }
            Log.d("cloudInary :","Succesful");
        } catch (IOException e) {
            e.printStackTrace();
        }

        return userImageURL;

    }

//    public Bitmap receiveUserImageFromServerNew(Context mContext) {
//
////        cloudInaryMap = new HashMap<>();
////
////        userDBAdapter = new UserDBAdapter(mContext);
////        Cursor cursor = userDBAdapter.cursor(mContext, DBConstants.USER_TABLE_NAME_STRUCTURE);
////        if ((cursor.getCount() > 0) && cursor.isBeforeFirst()) {
////            cursor.moveToFirst();
////            imagePath = cursor.getString(cursor.getColumnIndex(DBConstants.USER_IMAGE_PATH));
////        }
////
////        //File file = new File("/storage/9016-4EF8/shrilekha/936698_735392669848202_4892121200754617786_n.jpg");
////        File file = new File(imagePath);
////
////        imageMap = new HashMap<String, String>();
////        imageMap.put("cloud_name", "aks-singh");
////        imageMap.put("api_key", "218139359793645");
////        imageMap.put("api_secret", "yv3oMvfB5ml0NfGuGkz25uCKd3w");
////        Cloudinary cloudinary = new Cloudinary(imageMap);
////        try {
////            cloudInaryMap = cloudinary.uploader().upload(file, ObjectUtils.emptyMap());
////            if(cloudinary!=null)
////            {
////                for(Map.Entry<String, Object> entry : cloudInaryMap.entrySet())
////                {   //print keys and values
////                    Log.d("(CLOUDiNARY_MAP) key : ",""+entry.getKey()+ ", value : " +entry.getValue()+ "\n\n");
////                    //System.out.println(entry.getKey() + " : " +entry.getValue());
////                }
////            }
////            Log.d("cloudInary :","Succesful");
////        } catch (IOException e) {
////            e.printStackTrace();
////        }
//
//
////        try {
////            String imageUrl = "http://res.cloudinary.com/demo/image/upload/sample.jpg";
////            bitmap = BitmapFactory.decodeStream((InputStream) new URL(imageUrl).getContent());
////        } catch (MalformedURLException e) {
////            e.printStackTrace();
////        } catch (IOException e) {
////            e.printStackTrace();
////        }
//        return bitmap;
//    }

    //CreateGroup
    public boolean sendToServer(Map<String, String> postMap) {
        String serverURL = Configuration.SERVER_URL
                + "/createGroup";

        Log.e("Inside sendToServer", "Inside sendToServer");


        // /pOSTING TO Server
        URL url;
        try {
            url = new URL(serverURL);
        } catch (MalformedURLException e) {
            throw new IllegalArgumentException("invalid url: " + serverURL);
        }
        StringBuilder bodyBuilder = new StringBuilder();
        Iterator<Entry<String, String>> iterator = postMap.entrySet()
                .iterator();

        // constructs the POST body using the parameters
        while (iterator.hasNext()) {
            Entry<String, String> param = iterator.next();
            bodyBuilder.append(param.getKey()).append('=')
                    .append(param.getValue());
            if (iterator.hasNext()) {
                bodyBuilder.append('&');
            }
        }

        String body = bodyBuilder.toString();

        Log.e("AppServer = ", "Posting [" + body + "] to " + url);

        byte[] bytes = body.getBytes();

        HttpURLConnection conn = null;

        try {
            conn = (HttpURLConnection) url.openConnection();
            conn.setDoOutput(true);
            conn.setDoInput(true);
            conn.setUseCaches(false);
            conn.setFixedLengthStreamingMode(bytes.length);
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type",
                    "application/x-www-form-urlencoded;charset=UTF-8");

            // post the request
            OutputStream out = conn.getOutputStream();
            out.write(bytes);
            out.close();
            Log.e("HTTP = ", "Bytes written");

            // handle the response
            int status = conn.getResponseCode();
            Log.e("status = ", "" + status);

            String response_msg = conn.getResponseMessage();
            Log.e("ResponseMsg = ", response_msg);

            if (response_msg.equals("OK")) {
                success = true;
            }

            BufferedReader in = new BufferedReader(new InputStreamReader(
                    conn.getInputStream()));
            String inputLine;
            StringBuffer response = new StringBuffer();

            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }

            // Log.e("response(from Server) = ", response_msg.toString());

            in.close();

            if (status != 200) {
                throw new IOException("Post failed with error code " + status);
            }
        } finally {
            if (conn != null) {
                conn.disconnect();

            }
            return success;
        }

    }


    //Editing Member Amount
    @SuppressLint("LongLogTag")
    public boolean sendToServerForMemberAmountUpdate(Map<String, String> editMemberMap) {
        Log.e("Inside sendToServerForMemberAmountUpdate", "Inside sendToServerForMemberAmountUpdate");

        String serverURL = Configuration.SERVER_URL
                + "/editMemberAmount";


        // /pOSTING TO Server
        URL url;
        try {
            url = new URL(serverURL);
        } catch (MalformedURLException e) {
            throw new IllegalArgumentException("invalid url: " + serverURL);
        }
        StringBuilder bodyBuilder = new StringBuilder();
        Iterator<Entry<String, String>> iterator = editMemberMap.entrySet()
                .iterator();

        // constructs the POST body using the parameters
        while (iterator.hasNext()) {
            Entry<String, String> param = iterator.next();
            bodyBuilder.append(param.getKey()).append('=')
                    .append(param.getValue());
            if (iterator.hasNext()) {
                bodyBuilder.append('&');
            }
        }

        String body = bodyBuilder.toString();

        Log.e("AppServer = ", "Posting [" + body + "] to " + url);

        byte[] bytes = body.getBytes();

        HttpURLConnection conn = null;

        try {
            conn = (HttpURLConnection) url.openConnection();
            conn.setDoOutput(true);
            conn.setDoInput(true);
            conn.setUseCaches(false);
            conn.setFixedLengthStreamingMode(bytes.length);
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type",
                    "application/x-www-form-urlencoded;charset=UTF-8");

            // post the request
            OutputStream out = conn.getOutputStream();
            out.write(bytes);
            out.close();
            Log.e("HTTP = ", "Bytes written");

            // handle the response
            int status = conn.getResponseCode();
            Log.e("status = ", "" + status);

            String response_msg = conn.getResponseMessage();
            Log.e("ResponseMsg = ", response_msg);

            if (response_msg.equals("OK")) {
                result = true;
            }

            BufferedReader in = new BufferedReader(new InputStreamReader(
                    conn.getInputStream()));
            String inputLine;
            StringBuffer response = new StringBuffer();

            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }

            // Log.e("response(from Server) = ", response_msg.toString());

            in.close();

            if (status != 200) {
                throw new IOException("Post failed with error code " + status);
            }
        } finally {
            if (conn != null) {
                conn.disconnect();

            }
            return result;
        }


    }


    //Deleteing member from Group
    @SuppressLint("LongLogTag")
    public boolean sendToServerOnMemberDelete(Map<String, String> editMemberMap) {
        String serverURL = Configuration.SERVER_URL
                + "/deleteMember";

        Log.e("Inside sendToServerOnMemberDelete", "Inside sendToServerOnMemberDelete");


        // /pOSTING TO Server
        URL url;
        try {
            url = new URL(serverURL);
        } catch (MalformedURLException e) {
            throw new IllegalArgumentException("invalid url: " + serverURL);
        }
        StringBuilder bodyBuilder = new StringBuilder();
        Iterator<Entry<String, String>> iterator = editMemberMap.entrySet()
                .iterator();

        // constructs the POST body using the parameters
        while (iterator.hasNext()) {
            Entry<String, String> param = iterator.next();
            bodyBuilder.append(param.getKey()).append('=')
                    .append(param.getValue());
            if (iterator.hasNext()) {
                bodyBuilder.append('&');
            }
        }

        String body = bodyBuilder.toString();

        Log.e("AppServer = ", "Posting [" + body + "] to " + url);

        byte[] bytes = body.getBytes();

        HttpURLConnection conn = null;

        try {
            conn = (HttpURLConnection) url.openConnection();
            conn.setDoOutput(true);
            conn.setDoInput(true);
            conn.setUseCaches(false);
            conn.setFixedLengthStreamingMode(bytes.length);
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type",
                    "application/x-www-form-urlencoded;charset=UTF-8");

            // post the request
            OutputStream out = conn.getOutputStream();
            out.write(bytes);
            out.close();
            Log.e("HTTP = ", "Bytes written");

            // handle the response
            int status = conn.getResponseCode();
            Log.e("status = ", "" + status);

            String response_msg = conn.getResponseMessage();
            Log.e("ResponseMsg = ", response_msg);

            if (response_msg.equals("OK")) {
                result = true;
            }

            BufferedReader in = new BufferedReader(new InputStreamReader(
                    conn.getInputStream()));
            String inputLine;
            StringBuffer response = new StringBuffer();

            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }

            // Log.e("response(from Server) = ", response_msg.toString());

            in.close();

            if (status != 200) {
                throw new IOException("Post failed with error code " + status);
            }
        } finally {
            if (conn != null) {
                conn.disconnect();

            }
            return result;
        }

    }

    //Sending chat to server
    public boolean sendChatToServer(Map<String, String> chatMap) {

        String serverURL = Configuration.SERVER_URL
                + "/sendChat";

        Log.e("Inside sendChatToServer", "Inside sendChatToServer");


        // /pOSTING TO Server
        URL url;
        try {
            url = new URL(serverURL);
        } catch (MalformedURLException e) {
            throw new IllegalArgumentException("invalid url: " + serverURL);
        }
        StringBuilder bodyBuilder = new StringBuilder();
        Iterator<Entry<String, String>> iterator = chatMap.entrySet()
                .iterator();

        // constructs the POST body using the parameters
        while (iterator.hasNext()) {
            Entry<String, String> param = iterator.next();
            bodyBuilder.append(param.getKey()).append('=')
                    .append(param.getValue());
            if (iterator.hasNext()) {
                bodyBuilder.append('&');
            }
        }

        String body = bodyBuilder.toString();

        Log.e("AppServer = ", "Posting [" + body + "] to " + url);

        byte[] bytes = body.getBytes();

        HttpURLConnection conn = null;

        try {
            conn = (HttpURLConnection) url.openConnection();
            conn.setDoOutput(true);
            conn.setDoInput(true);
            conn.setUseCaches(false);
            conn.setFixedLengthStreamingMode(bytes.length);
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type",
                    "application/x-www-form-urlencoded;charset=UTF-8");

            // post the request
            OutputStream out = conn.getOutputStream();
            out.write(bytes);
            out.close();
            Log.e("HTTP = ", "Bytes written");

            // handle the response
            int status = conn.getResponseCode();
            Log.e("status = ", "" + status);

            String response_msg = conn.getResponseMessage();
            Log.e("ResponseMsg = ", response_msg);

            if (response_msg.equals("OK")) {
                result = true;
            }

            BufferedReader in = new BufferedReader(new InputStreamReader(
                    conn.getInputStream()));
            String inputLine;
            StringBuffer response = new StringBuffer();

            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }

            // Log.e("response(from Server) = ", response_msg.toString());

            in.close();

            if (status != 200) {
                throw new IOException("Post failed with error code " + status);
            }
        } finally {
            if (conn != null) {
                conn.disconnect();

            }
            return result;
        }

    }

}
