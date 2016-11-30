package com.app.splitCash.imageManager.conversion;

import android.content.Context;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;

import com.app.splitCash.constants.DBConstants;
import com.app.splitCash.dataBase.UserDBAdapter;

import java.io.ByteArrayOutputStream;

/*adopted from http://ruibm.com/2009/06/16/rounded-corner-bitmaps-on-android/*/

public class ImageConverterManager {

    private Context mContext;
    private UserDBAdapter userDBAdapter;
    private Bitmap imageBitMap;


    public ImageConverterManager(Context context) {
        mContext = context;
    }

//    public static Bitmap getRoundedCornerBitmap(Bitmap bitmap, int pixels) {
//        Bitmap output = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Bitmap.Config.RGB_565);
//        Canvas canvas = new Canvas(output);
//
//        final int color = 0xff424242;
//        final Paint paint = new Paint();
//        final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
//        final RectF rectF = new RectF(rect);
//        final float roundPx = pixels;
//
//        paint.setAntiAlias(true);
//        canvas.drawARGB(0, 0, 0, 0);
//        paint.setColor(color);
//        canvas.drawRoundRect(rectF, roundPx, roundPx, paint);
//
//        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
//        canvas.drawBitmap(bitmap, rect, rect, paint);
//
//        return output;
//    }

    public byte[] getByteArrayFromBitMap(Bitmap imageBitMap) {
        //bITMAP TO BYTE[]
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        imageBitMap.compress(Bitmap.CompressFormat.PNG, 100, stream);
        byte[] imageInBytes = stream.toByteArray();
        return imageInBytes;

    }


    public Bitmap getBitmapFromByteArray(byte[] image) {
        return BitmapFactory.decodeByteArray(image, 0, image.length);
    }


    public Bitmap getCircleBitmap(Bitmap bitmap) {
        Bitmap output;

        if (bitmap.getWidth() > bitmap.getHeight()) {
            output = Bitmap.createBitmap(bitmap.getHeight(), bitmap.getHeight(), Bitmap.Config.ARGB_8888);
        } else {
            output = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getWidth(), Bitmap.Config.ARGB_8888);
        }

        Canvas canvas = new Canvas(output);

        final int color = Color.RED;
        final Paint paint = new Paint();
        final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());


        paint.setAntiAlias(true);
        canvas.drawARGB(0, 0, 0, 0);
        paint.setColor(color);

        float radius = 0;
        if (bitmap.getWidth() > bitmap.getHeight()) {
            radius = bitmap.getHeight() / 2;
        } else {
            radius = bitmap.getWidth() / 2;
        }

        canvas.drawCircle(radius, radius, radius, paint);

        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(bitmap, rect, rect, paint);

        return output;
    }

    //Not working properly
    //Circular image using RoundedBitmapDrawable
    public RoundedBitmapDrawable getRoundedBitmap(Resources res, Bitmap bitmap){
        RoundedBitmapDrawable roundBitMap = RoundedBitmapDrawableFactory.create(res,bitmap);
        roundBitMap.setCornerRadius(Math.max(bitmap.getWidth(), bitmap.getHeight()) / 2.0f);
        roundBitMap.setCircular(true);
        roundBitMap.setAntiAlias(true);
        return roundBitMap;
    }
}
