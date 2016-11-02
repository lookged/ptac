package com.example.kanda.ptacproject.adepter;

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

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.example.kanda.ptacproject.R;
import com.example.kanda.ptacproject.activity.DestinationMapActivity;
import com.example.kanda.ptacproject.activity.MainActivity;
import com.example.kanda.ptacproject.app.AppConfig;
import com.example.kanda.ptacproject.app.AppController;
import com.example.kanda.ptacproject.helper.SQLiteHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by NamPeung on 30-Oct-16.
 */

public class SmsContactAdepter extends BaseAdapter {
    public static final String TAG = SmsContactAdepter.class.getSimpleName();
    private Context mContext;
    private ArrayList<String[]> friendList;
    public int phonenofriend;
    public String emailfriend;
    private SQLiteHandler db;
    String uiduser ;



    public class ViewHolder {
        Button friendLocation;
        TextView friendNameTV;

        public ViewHolder(View view) {
            friendNameTV = (TextView) view.findViewById(R.id.friend_name);
            friendLocation =(Button) view.findViewById(R.id.check_friendno);




        }
    }

    public SmsContactAdepter(Context context, ArrayList<String[]> friendList) {
        this.mContext = context;
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
            view = LayoutInflater.from(mContext).inflate(R.layout.friendlistsms, viewGroup, false);
        }
        final String[] str = getItem(i);
        if (str != null) {
             final ViewHolder viewHolder = new ViewHolder(view);
            viewHolder.friendNameTV.setText(str[0]);


//                        Toast.makeText(mContext,
//                                "you can add this user", Toast.LENGTH_LONG).show();

            viewHolder.friendLocation.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    try {

                        String emailfriend = str[0].trim();
                        String emailuid = MainActivity.session.getLoginId();
//                       Toast.makeText(mContext,emailuid+emailfriend, Toast.LENGTH_LONG).show();
                        syncFriendNumber(emailfriend,emailuid);




                    }catch (Exception e){
                        Log.d(TAG, " "+e);
                    }

                }
            });
        }
        return view;
    }


    private void syncFriendNumber(final String friendemail, final String uid) {

        String tag_string_req = "req_marker_list";
        StringRequest strReq = new StringRequest(Request.Method.POST,
                AppConfig.URL_SEARCH_NUMBERFRIEND, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                try {
                    JSONArray arr;
                    arr = new JSONArray(response);
                    if (arr.length() != 0) {
                        friendList = new ArrayList<>();
                        for (int i = 0; i < arr.length(); i++) {
                            JSONObject obj = (JSONObject) arr.get(i);

                            emailfriend = obj.getString("email");
                            phonenofriend = obj.getInt("phoneno");

                            String email = MainActivity.session.getLoginEmail();

                            addNumberFriend(email,phonenofriend,emailfriend);
//                            Toast.makeText(mContext, "no friend"+email, Toast.LENGTH_LONG).show();
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
                params.put("friendemail", friendemail);
                params.put("uid", uid);
                return params;
            }
        };
        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(strReq, tag_string_req);

    }

    public void addNumberFriend(final String email,final int friendnumber ,final String friendemail) {
        String tag_string_req = "req_searchfriend";



        StringRequest strReq = new StringRequest(Request.Method.POST,
                AppConfig.URL_ADD_FRIENDNUMBER, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.d(TAG, "Request Response: " + response);

                try {
                    JSONObject jObj = new JSONObject(response);
                    boolean error = jObj.getBoolean("error");

                    // Check for error node in json
                    if (!error) {


                    } else {

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
                params.put("friendnumber", Integer.toString(friendnumber));
                params.put("friendemail", friendemail);
                return params;
            }

        };

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(strReq, tag_string_req);
    }
}
