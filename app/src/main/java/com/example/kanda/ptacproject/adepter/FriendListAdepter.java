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
import java.util.HashMap;
import java.util.Map;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.example.kanda.ptacproject.R;
import com.example.kanda.ptacproject.activity.DestinationMapActivity;
import com.example.kanda.ptacproject.activity.MainActivity;
import com.example.kanda.ptacproject.app.AppConfig;
import com.example.kanda.ptacproject.app.AppController;
import com.example.kanda.ptacproject.fragments.TwoFragment;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class FriendListAdepter extends BaseAdapter {
    public static final String TAG = FriendListAdepter.class.getSimpleName();
    private Context mContext;
    private ArrayList<String[]> friendList;
    public String latdestination ;
    public String lngdestination ;
    public String latcurrent ;
    public String lngcurrent ;


    public static class ViewHolder {
         Button friendLocation;
        TextView friendNameTV;

        public ViewHolder(View view) {
            friendNameTV = (TextView) view.findViewById(R.id.friend_name);
            friendLocation =(Button) view.findViewById(R.id.check_location);
            friendLocation.setVisibility(View.INVISIBLE);
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
        final String[] str = getItem(i);
        if (str != null) {
            final ViewHolder viewHolder = new ViewHolder(view);
            final String fnamefriend = str[1];
            String emailfriend = str[0];
            if (fnamefriend.equalsIgnoreCase("null")){

                int num = emailfriend.indexOf("@");

                viewHolder.friendNameTV.setText(emailfriend.substring(0,num+1));

            }else {
                viewHolder.friendNameTV.setText(str[1]);

            }
            checkDestination(emailfriend,view);
            viewHolder.friendLocation.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    try {
                        String emailfriend = str[0].trim();
                        syncFriendLocation(emailfriend,fnamefriend);
//                        Toast.makeText(mContext, "no friend"+latdestination, Toast.LENGTH_LONG).show();



                    }catch (Exception e){
                        Log.d(TAG, " "+e);
                    }

                }
            });
        }
        return view;
    }
    private void syncFriendLocation(final String emailfriend, final String fnamefriend) {

        String tag_string_req = "req_marker_list";
        StringRequest strReq = new StringRequest(Request.Method.POST,
                AppConfig.URL_LOCATIONFRIEND_LIST, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                try {
                    JSONArray arr;
                    arr = new JSONArray(response);
                    if (arr.length() != 0) {
                        friendList = new ArrayList<>();
                        for (int i = 0; i < arr.length(); i++) {
                            JSONObject obj = (JSONObject) arr.get(i);

                            latdestination = obj.getString("latdestination");
                            lngdestination = obj.getString("lngdestination");
                            latcurrent = obj.getString("latcurrentlocation");
                            lngcurrent = obj.getString("lngcurrentlocation");
                            Intent intent=new Intent(mContext,DestinationMapActivity.class).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            intent.putExtra("emailfriend", emailfriend.toString());
                            intent.putExtra("fnamefriend", fnamefriend.toString());
                            intent.putExtra("latdestination", latdestination.toString());
                            intent.putExtra("lngdestination", lngdestination.toString());
                            intent.putExtra("latcurrent", latcurrent.toString());
                            intent.putExtra("lngcurrent", lngcurrent.toString());

                            mContext.startActivity(intent);
//                            Toast.makeText(mContext, "no friend"+latdestination, Toast.LENGTH_LONG).show();
                        }


                    }
                } catch (JSONException e) {
//                    e.printStackTrace();
//                    Log.e(TAG, "Json error: " + e.getMessage());
//                    Toast.makeText(mContext, "no friend", Toast.LENGTH_LONG).show();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "Load Friend List Error: " + error.getMessage());
                //Toast.makeText(getActivity(), (error.getMessage() == null ? "haha" : "eiei"), Toast.LENGTH_LONG).show();

            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("emailfriend", emailfriend);

                return params;
            }
        };
        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(strReq, tag_string_req);

    }

    public void checkDestination(final String email,View convertView) {
        String tag_string_req = "req_searchfriend";
        final View view = convertView;


        StringRequest strReq = new StringRequest(Request.Method.POST,
                AppConfig.URL_SEARCH_DESTINATION, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.d(TAG, "Request Response: " + response);

                try {
                    JSONObject jObj = new JSONObject(response);
                    boolean error = jObj.getBoolean("error");

                    // Check for error node in json
                    if (!error) {
                        final ViewHolder viewHolder = new ViewHolder(view);
//                        Toast.makeText(mContext,
//                                "you can add this user", Toast.LENGTH_LONG).show();
                       viewHolder.friendLocation.setVisibility(View.VISIBLE);
                    } else {
                        // Error in login. Get the error message
                        String errorMsg = "user not found";
//                        Toast.makeText(mContext,
//                                errorMsg, Toast.LENGTH_LONG).show();
                    }
                } catch (JSONException e) {
                    // JSON error
                    e.printStackTrace();
                    Log.d(TAG, response);
//                    Toast.makeText(mContext, "Json error Add Friend: " + e.getMessage(), Toast.LENGTH_LONG).show();
                }

            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "Request Error: " + error.getMessage());
//                Toast.makeText(mContext,
//                        error.getMessage(), Toast.LENGTH_LONG).show();

            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                // Posting parameters to login url
                Map<String, String> params = new HashMap<String, String>();
                params.put("email", email);

                return params;
            }

        };

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(strReq, tag_string_req);
    }
}