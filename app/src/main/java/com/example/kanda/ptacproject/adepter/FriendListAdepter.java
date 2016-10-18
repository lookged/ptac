package com.example.kanda.ptacproject.adepter;

/**
 * Created by Kanda on 9/3/2016.
 */

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import com.example.kanda.ptacproject.R;
import com.example.kanda.ptacproject.activity.DestinationMapActivity;
import com.example.kanda.ptacproject.activity.MainActivity;
import com.example.kanda.ptacproject.fragments.TwoFragment;

public class FriendListAdepter extends BaseAdapter {
    public static final String TAG = FriendListAdepter.class.getSimpleName();
    private Context mContext;
    private ArrayList<String[]> friendList;


    public static class ViewHolder {
        TextView friendNameTV;
        Button friendLocation;
        public ViewHolder(View view) {
            friendNameTV = (TextView) view.findViewById(R.id.friend_name);
            friendLocation =(Button) view.findViewById(R.id.check_location);
        }
    }

    public FriendListAdepter(Context context, ArrayList<String[]> friendList) {
        mContext = context;
        this.friendList = friendList;
        Log.d(TAG, "Constructor");
    }

    @Override
    public int getCount() {
        Log.d(TAG, "getCount: "+ friendList.size());
        return friendList.size();
    }

    @Override
    public String[] getItem(int i) {
        return friendList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View convertView, ViewGroup viewGroup) {
        Log.d(TAG, "getView");
        View view = convertView;
        if (view == null) {
            view = LayoutInflater.from(mContext).inflate(R.layout.friend_list, viewGroup, false);
        }
        String[] str = getItem(i);
        if (str != null) {
            final ViewHolder viewHolder = new ViewHolder(view);
            viewHolder.friendNameTV.setText(str[0]);
            viewHolder.friendLocation.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    try {
//                        mContext.startActivity(new Intent(mContext, DestinationMapActivity.class));
                        mContext.startActivity(new Intent(mContext, DestinationMapActivity.class));
                    }catch (Exception e){
                        Log.d(TAG, " "+e);
                    }

                }
            });
        }
        return view;
    }
}