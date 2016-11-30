package com.app.splitCash.notifications;

import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;

import com.app.splitCash.R;
import com.app.splitCash.imageManager.conversion.ImageConverterManager;
import com.app.splitCash.root.ContactUs;

/**
 * Created by Aakash Singh on 05-06-2016.
 */
public class NotificationManager {

    Context mContext;

    public NotificationManager(Context context) {
        mContext = context;
    }

    public void sendNotification(String msg) {

        Resources res = mContext.getResources();

        //For invoking Circular Bitmap method
        ImageConverterManager imageConverterManager = new ImageConverterManager(mContext);

        //Circular Bitmap Creation
        Bitmap bitmap = BitmapFactory.decodeResource(mContext.getResources(), R.drawable.aks);
        Bitmap bitmap_new = imageConverterManager.getCircleBitmap(bitmap);

        //sETTING Intent for this Notification
        // Sets an ID for the notification
        int mNotificationId = 1;

        //Intent resultIntent = new Intent(mContext, ContactUs.class);

        // Sets up the Snooze and Dismiss action buttons that will appear in the
        // big view of the notification.
        Intent dismissIntent = new Intent(mContext, ContactUs.class);
        dismissIntent.setAction("Dismiss");
        PendingIntent piDismiss = PendingIntent.getService(mContext, 0, dismissIntent, 0);

        Intent snoozeIntent = new Intent(mContext, ContactUs.class);
        snoozeIntent.setAction("Open Activity");
        PendingIntent piSnooze = PendingIntent.getService(mContext, 0, snoozeIntent, 0);

        TaskStackBuilder stackBuilder = TaskStackBuilder.create(mContext);
        // Adds the back stack
        stackBuilder.addParentStack(ContactUs.class);
        // Adds the Intent to the top of the stack
        stackBuilder.addNextIntent(snoozeIntent);
        // Gets a PendingIntent containing the entire back stack
        PendingIntent resultPendingIntent =
                stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);

        //default Notification
        // Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        // Custom Notification
        Uri customSoundUri = Uri.parse("android.resource://com.app.splitCash/" + R.raw.notification);

        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(mContext);

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            notificationBuilder.setLargeIcon(BitmapFactory.decodeResource(res, R.drawable.ic_launcher))
                    .setContentTitle("WidUapp")
                    .setContentText("New notification")
                    .setAutoCancel(true).setSound(customSoundUri)
                    .setSound(customSoundUri)
                    .setDefaults(Notification.DEFAULT_VIBRATE);//vibration


        } else {
            notificationBuilder
                    .setSmallIcon(R.drawable.ic_launcher_small, 0)// int resource needed
//                    .setLargeIcon(BitmapFactory.decodeResource(res, R.drawable.ic_launcher))//Bitmap resource needed
                    .setLargeIcon(bitmap_new)
                    .setContentTitle("WidUapp")
                    .setContentText("New notification")
                    .setSound(customSoundUri)
                    .setAutoCancel(true)
                    .setDefaults(Notification.DEFAULT_VIBRATE)//vibration
                    .setColor(mContext.getResources().getColor(R.color.black));


//            notificationBuilder.setStyle(new NotificationCompat.BigPictureStyle().bigPicture(BitmapFactory.decodeResource(res, R.drawable.ic_notification_image)));

            notificationBuilder.setStyle(new NotificationCompat.BigTextStyle().setSummaryText("Summary").bigText(msg));

//                    notificationBuilder.setSubText("AKS")
//                    .setTicker("Ticker")
//                    .setContentInfo("CO");


//                    notificationBuilder.addAction (R.drawable.ic_dismiss,
//                            "Dismiss", piDismiss)
//                    .addAction (R.drawable.ic_open,
//                            "Open", piSnooze);


        }

        notificationBuilder.setContentIntent(resultPendingIntent);
        // Gets an instance of the NotificationManager service
        android.app.NotificationManager notificationManager =
                (android.app.NotificationManager) mContext.getSystemService(mContext.NOTIFICATION_SERVICE);
        // Builds the notification and issues it.
        notificationManager.notify(mNotificationId, notificationBuilder.build());

        return;
    }

}

