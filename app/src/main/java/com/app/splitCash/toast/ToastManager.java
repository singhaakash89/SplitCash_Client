package com.app.splitCash.toast;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.graphics.Color;
import android.text.Html;
import android.view.Gravity;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.app.splitCash.R;

/**
 * Created by Aakash Singh on 19-05-2016.
 */
public class ToastManager extends Toast {

    Context mContext;
    /**
     * Construct an empty Toast object.  You must call {@link #setView} before you
     * can call {@link #show}.
     *
     * @param context The context to use.  Usually your {@link Application}
     *                or {@link Activity} object.
     */
    public ToastManager(Context context) {

        super(context);
        mContext = context;
    }

    public void createToast_Simple(String msg) {
        LinearLayout layout = new LinearLayout(mContext);
        layout.setBackgroundResource(R.drawable.layout_toast_background);

        TextView tv = new TextView(mContext);
        // set the TextView properties like color, size etc
        tv.setTextColor(Color.BLACK);
        tv.setTextSize(15);

        tv.setGravity(Gravity.CENTER_VERTICAL);

        // set the text you want to show in  Toast
        tv.setText(msg);

//        ImageView img=new ImageView(mContext);
//
//        // give the drawble resource for the ImageView
//        img.setImageResource(R.drawable.layout_background_white_color);

        // add both the Views TextView and ImageView in layout
//        layout.addView(img);
        layout.addView(tv);

        Toast toast = new Toast(mContext); //context is object of Context write "this" if you are an Activity
        // Set The layout as Toast View
        toast.setView(layout);

        // Position you toast here toast position is 50 dp from bottom you can give any integral value
        toast.setGravity(Gravity.BOTTOM, 0, 200);
        toast.setDuration(LENGTH_SHORT);
        toast.show();
    }


    public void createToast_Bold(Context mContext, String msg1, String msg2, String msg3) {
        LinearLayout layout = new LinearLayout(mContext);
        layout.setBackgroundResource(R.drawable.layout_toast_background);

        TextView tv = new TextView(mContext);
        // set the TextView properties like color, size etc
        tv.setTextColor(Color.BLACK);
        tv.setTextSize(15);

        tv.setGravity(Gravity.CENTER_VERTICAL);

        // set the text you want to show in  Toast
        tv.setText(msg1 + " " + Html.fromHtml("<b>" + msg2 + "</b>") + " " + msg3);

//        ImageView img=new ImageView(mContext);
//
//        // give the drawble resource for the ImageView
//        img.setImageResource(R.drawable.layout_background_white_color);

        // add both the Views TextView and ImageView in layout
//        layout.addView(img);
        layout.addView(tv);

        Toast toast = new Toast(mContext); //context is object of Context write "this" if you are an Activity
        // Set The layout as Toast View
        toast.setView(layout);

        // Position you toast here toast position is 50 dp from bottom you can give any integral value
        toast.setGravity(Gravity.BOTTOM, 0, 200);
        toast.setDuration(LENGTH_SHORT);
        toast.show();
    }
}
