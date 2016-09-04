package com.example.kanda.ptacproject.adepter;

/**
 * Created by Kanda on 9/3/2016.
 */

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

import com.example.kanda.ptacproject.R;

public class FriendListAdepter extends BaseAdapter {
    public static final String TAG = FriendListAdepter.class.getSimpleName();
    private Context mContext;
    private ArrayList<String[]> friendList;

    public static class ViewHolder {
        TextView friendNameTV;

        public ViewHolder(View view) {
            friendNameTV = (TextView) view.findViewById(R.id.friend_name);
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
            ViewHolder viewHolder = new ViewHolder(view);
            viewHolder.friendNameTV.setText(str[0]);
        }
        return view;
    }
}