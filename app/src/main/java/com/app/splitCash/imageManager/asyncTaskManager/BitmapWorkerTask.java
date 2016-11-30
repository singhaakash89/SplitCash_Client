package com.app.splitCash.imageManager.asyncTaskManager;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import com.app.splitCash.imageManager.LruCache_memeory_management.MemoryLruCache;
import com.app.splitCash.R;
import com.app.splitCash.chat.ConversationBean;

/**
 * Created by Aakash Singh on 08-07-2016.
 */
public class BitmapWorkerTask extends AsyncTask<String, Void, Bitmap> {

    private ImageView conversationImageView;
    private TextView conversationTimeTextView;
    private ConversationBean conversationBean;
    private MemoryLruCache memoryLruCache;

    public BitmapWorkerTask(ConversationBean conversationBean, ImageView conversationImageView, TextView conversationTimeTextView) {
        this.conversationBean = conversationBean;
        this.conversationImageView = conversationImageView;
        this.conversationTimeTextView = conversationTimeTextView;
        memoryLruCache = new MemoryLruCache();
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        Log.d("onPreExecute", "..................................");
        conversationImageView.setImageResource(R.drawable.placeholder_old);
    }

    @Override
    protected Bitmap doInBackground(String... uID) {
        Log.d("doInBackground", "..................................");
        byte[] image = conversationBean.getConversationImage();
        Log.d("byte[] image : ", "" + image);

        Bitmap bitmap = BitmapFactory.decodeByteArray(image, 0, image.length);
        Log.d("Bitmap bitmap : ", "" + bitmap);

        //Store it in LruCache
        memoryLruCache.addBitmapToMemoryCache(uID[0], bitmap);

        return bitmap;
    }

    @Override
    protected void onPostExecute(Bitmap bitmap) {
        super.onPostExecute(bitmap);
        Log.d("onPostExecute", "..................................");

        Log.d("from_populateImage_conversationImageView : ", "" + conversationImageView);
        conversationImageView.setImageBitmap(bitmap);
        conversationTimeTextView.setText(conversationBean.getConversationTimeStamp());

        //calling garbage collector
        System.gc();

    }

}
