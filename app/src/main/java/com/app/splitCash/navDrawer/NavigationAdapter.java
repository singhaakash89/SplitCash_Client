package com.app.splitCash.navDrawer;

import android.annotation.SuppressLint;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.app.splitCash.R;
import com.app.splitCash.dataBase.Message;
import com.app.splitCash.root.HomePageActivity_NO_USE;
import com.app.splitCash.root.SettingsActivity;

import java.util.Collections;
import java.util.List;

/**
 * Created by Aakash Singh on 21-08-2015.
 */
public class NavigationAdapter extends RecyclerView.Adapter<NavigationAdapter.MyViewHolder> {

    private Context context;
    private LayoutInflater inflater;
    private DrawerLayout drawerLayout;
    List<rowData> arrayListData = Collections.emptyList();
    private RecyclerView drawerList;

    private FragmentManager fragmentManager;
    private FragmentTransaction fragmentTransaction;
    private TextView fragmentNameForAppBar;
    private ImageView fragmentImageForAppBar;
    private Intent intent;
    private static DrawerLayout mDrawerLayout;


//    @SuppressLint("LongLogTag")
//    public NavigationAdapter(DrawerLayout mDrawerLayout) {
//        this.mDrawerLayout = mDrawerLayout;
//        Log.e("mDrawerLayout(Constructor) : ", ""+mDrawerLayout);
//    }

    public static void reIntializeDrawerLayout(DrawerLayout drawerLayout) {
        mDrawerLayout = drawerLayout;
    }

    public NavigationAdapter(Context context, List<rowData> arrayListData, RecyclerView drawerList, DrawerLayout drawerLayout) {

        Log.e("CHECK :: ", "INSIDE (NavigationAdapter CONSTRUCTOR)");

        this.context = context;
        this.inflater = LayoutInflater.from(context);
        this.arrayListData = arrayListData;
        this.drawerList = drawerList;

        this.drawerLayout = drawerLayout;

        this.fragmentManager = fragmentManager;

    }

    @SuppressLint("LongLogTag")
    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        Log.e("CHECK :: ", "INSIDE (onCreateViewHolder)");

        View view = inflater.inflate(R.layout.nav_custom_row, parent, false);
        MyViewHolder myViewHolder = new MyViewHolder(context, view);
        return myViewHolder;
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, final int position) {

        Log.e("CHECK :: ", "INSIDE (onBindViewHolder)");

        //fecthing data from ArrayList into Bean of Text/Image type
        rowData currentData = arrayListData.get(position);

        //setting data from Bean into Holder
        holder.title.setText(currentData.titleId);
        holder.icon.setImageResource(currentData.iconId);


//        //settinguP lISTENER
//        holder.icon.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Message.message(context, Html.fromHtml(String.valueOf(arrayListData.get(position))) + " is pressed");
//            }
//        });
    }

    @Override
    public int getItemCount() {

        Log.e("CHECK :: ", "INSIDE (getItemCount)");

        return arrayListData.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView title;
        ImageView icon;

        public MyViewHolder(Context context, View itemView) {
            super(itemView);

            Log.e("CHECK :: ", "INSIDE (MyViewHolder constructor)");

            title = (TextView) itemView.findViewById(R.id.navCustomRowTextView);
            icon = (ImageView) itemView.findViewById(R.id.navCustomRowImageView);


            //setting onItemClick Listener
            icon.setOnClickListener(this);
            title.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {

            //closing drawer
            Log.d("drawerLayout : ", "" + mDrawerLayout);
            Log.d("drawerList : ", "" + drawerList);
            if (mDrawerLayout != null) {
                mDrawerLayout.closeDrawers();
            }

            Log.e("CHECK :: ", "INSIDE (onClick)");

            String onPressed = title.getText().toString();
            Message.message(context, "" + onPressed + " is pressed");

            //switch for opening corresponding Activity on Click
            switch (onPressed) {
                case "WidUapp Groups":

                    //Invoking BroadCast Receiver
                    intent = new Intent("broadcast_HomePageIntentFilter");
                    LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
                    break;

                case "WidUapp Chats":
                    intent = new Intent("broadcast_ChatIntentFilter");
                    LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
                    break;


                case "WidUapp Settings":

//                    context.startActivity(new Intent(context, SettingsActivity.class));
                    break;


                case "About WidUapp":


                    break;


            }
        }
    }
}
