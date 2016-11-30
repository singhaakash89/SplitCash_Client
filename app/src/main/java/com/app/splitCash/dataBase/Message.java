package com.app.splitCash.dataBase;

import android.content.Context;
import android.text.Html;
import android.view.Gravity;
import android.widget.Toast;

public class Message
{
    public static void message(Context context, String messageString)
    {
        Toast.makeText(context, messageString, Toast.LENGTH_SHORT).show();
    }


    public static void messageBold(Context context, String messageString1, String messageString2, String messageString3)
    {
        Toast.makeText(context, "" + messageString1 + " " + Html.fromHtml("<b>" + messageString2 + "</b>") + " " + messageString3, Toast.LENGTH_SHORT).show();
    }

    public static void messageCenter(Context context, String messageString)
    {
        Toast toast = Toast.makeText(context, messageString, Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.CENTER_HORIZONTAL | Gravity.CENTER_VERTICAL, 0, 0);
        toast.show();

    }

}
