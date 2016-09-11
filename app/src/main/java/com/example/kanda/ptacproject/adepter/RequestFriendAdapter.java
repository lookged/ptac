package com.example.kanda.ptacproject.adepter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.kanda.ptacproject.R;

import java.util.ArrayList;

public class RequestFriendAdapter extends BaseAdapter {
    public static final String TAG = FriendListAdepter.class.getSimpleName();
    private Context mContext;
    private ArrayList<String[]> friendRequest;

    public RequestFriendAdapter(Context context, ArrayList<String[]> friendRequest) {
        mContext = context;
        this.friendRequest = friendRequest;
        Log.d(TAG, "Constructor");
    }

    @Override
    public int getCount() {
        Log.d(TAG, "getCount: " + friendRequest.size());
        return friendRequest.size();
    }

    @Override
    public String[] getItem(int i) {
        return friendRequest.get(i);
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
            view = LayoutInflater.from(mContext).inflate(R.layout.request_list, viewGroup, false);
        }
        String[] str = getItem(i);
        if (str != null) {
            ViewHolder viewHolder = new ViewHolder(view);
            viewHolder.friendNameTV.setText(str[0]);
        }
        return view;
    }

    public static class ViewHolder {
        TextView friendNameTV;

        public ViewHolder(View view) {
            friendNameTV = (TextView) view.findViewById(R.id.friend_name);
        }
    }
}