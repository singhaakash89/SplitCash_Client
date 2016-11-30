package com.app.splitCash.serverUtil;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;


import android.content.Context;
import android.util.Base64;
import android.util.Log;


import com.app.splitCash.constants.Configuration;
import com.app.splitCash.constants.DBConstants;
import com.app.splitCash.constants.ServerConstants;
import com.app.splitCash.sharedPreferences.SharedPreferenceManager;


public class AppServer {

    static boolean flag = true;
    //Context mContext;
    static boolean success = false;
    static boolean successFlag = false;
    // boolean postResult = false;
    private final static String serverURL = Configuration.SERVER_URL
            + "/register";
    private final static String serverURLforImage = Configuration.SERVER_URL + "/addUserImage";

    SharedPreferenceManager sharedPreferenceManager;

    public AppServer(Context mContext) {
        //this.mContext = mContext;

        Log.e("SharedPreferenceManager(AppServer)", "Before");
        sharedPreferenceManager = new SharedPreferenceManager(mContext);
        Log.e("SharedPreferenceManager(AppServer)", "Aftr");

    }

    public boolean register(String userImageURL) {
        Log.e("from Register", "true");

        // MAP for stroing user info
        Map<String, String> userMap = new HashMap<String, String>();

        // *********Using SharedPrefernce for Fetching userName AND
        // phoneNumber*****************

        String regId = sharedPreferenceManager
                .getString(DBConstants.USER_GCM_ID);
        Log.d("regId(AppServer) = ", regId);

        String userName = sharedPreferenceManager
                .getString(DBConstants.USER_NAME);
        Log.d("userName(AppServer) = ", userName);

        String phoneNumber = sharedPreferenceManager
                .getString(DBConstants.USER_PHONE_NUMBER);
        Log.d("phoneNumber(AppServer) = ", phoneNumber);

        // Storing user info in MAP
        userMap.put(ServerConstants.USER_GCM_ID, regId);
        userMap.put(ServerConstants.USER_NAME, userName);
        userMap.put(ServerConstants.USER_PHONE_NUMBER, phoneNumber);
        userMap.put(ServerConstants.USER_IMAGE_URL, userImageURL);


        Log.d("userMap Created", "True");

        return postForRegitration(userMap, serverURL);

    }

    public boolean postForRegitration(Map<String, String> userMap,
                                      String endpoint) {

        // /pOSTING TO Server
        URL url;
        try {
            url = new URL(endpoint);
        } catch (MalformedURLException e) {
            throw new IllegalArgumentException("invalid url: " + endpoint);
        }
        StringBuilder bodyBuilder = new StringBuilder();
        Iterator<Entry<String, String>> iterator = userMap.entrySet()
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
            Log.e("conn.getInputStream() = ", "" + response);
            in.close();

            if (status != 200) {
                throw new IOException("Post failed with error code " + status);
            }
        } catch (ProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (conn != null) {
                conn.disconnect();
            }
        }
        return success;
    }

}
