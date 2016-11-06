package com.example.kanda.ptacproject.adepter;

import android.content.Context;
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
import com.example.kanda.ptacproject.activity.MainActivity;
import com.example.kanda.ptacproject.app.AppConfig;
import com.example.kanda.ptacproject.app.AppController;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class RequestFriendAdapter extends BaseAdapter  {
    public static final String TAG = RequestFriendAdapter.class.getSimpleName();
    private Context mContext;
    private ArrayList<String[]> friendRequest;
    ViewHolder viewHolder;
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
        final String[] str = getItem(i);
        if (str != null) {
            viewHolder = new ViewHolder(view);
            final String fnamefriend = str[2];
            String emailfriend = str[1];
            if (fnamefriend.equalsIgnoreCase("null")){

                int num = emailfriend.indexOf("@");

                viewHolder.requestNameTV.setText(emailfriend.substring(0,num+1));

            }else {
                String lname = str[3];

                if (lname.equalsIgnoreCase("null")){
                    lname = "";
                }

                viewHolder.requestNameTV.setText(str[2]+"  "+lname);

            }
            final String requestedId = str[0];
            final String loginId = MainActivity.session.getLoginId();
            viewHolder.btnAccept.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    viewHolder.btnAccept.setVisibility(View.INVISIBLE);
                    viewHolder.btnDeny.setVisibility(View.INVISIBLE);
                    try {


                        acceptFriendRequest(loginId, requestedId);

                    }catch (Exception e){
                        Log.d(TAG, " "+e);
                    }

                }
            });
            viewHolder.btnDeny.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    viewHolder.btnAccept.setVisibility(View.INVISIBLE);
                    viewHolder.btnDeny.setVisibility(View.INVISIBLE);
                    try {


                        denyFriendRequest(loginId, requestedId);

                    }catch (Exception e){
                        Log.d(TAG, " "+e);
                    }

                }
            });
//            viewHolder.btnAccept.setContentDescription(str[0]);
//            viewHolder.btnDeny.setContentDescription(str[0]);
//            viewHolder.btnAccept.setOnClickListener(this);
//            viewHolder.btnDeny.setOnClickListener(this);
        }
        return view;
    }

//    @Override
//    public void onClick(View view) {
//
//        switch (view.getId()) {
//            case R.id.accept_request:
//
//
//                break;
//            case R.id.deny_request:
//                viewHolder.btnAccept.setVisibility(View.INVISIBLE);
//                viewHolder.btnDeny.setVisibility(View.INVISIBLE);
//                denyFriendRequest(loginId, requestedId);
//
//                break;
//        }
//
//    }

    private void acceptFriendRequest(final String loginId, final String requestedId) {
        String tag_string_req = "accept_request";

        StringRequest strReq = new StringRequest(Request.Method.POST,
                AppConfig.URL_UPDATE_STATUS, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.d(TAG, "Register Response: " + response.toString());

                try {
                    JSONObject jObj = new JSONObject(response);
                    boolean error = jObj.getBoolean("error");
                    if (!error) {


                        Toast.makeText((mContext), "accept this user", Toast.LENGTH_LONG).show();

                    } else {

                        // Error occurred in registration. Get the error
                        // message
                        String errorMsg = jObj.getString("error_msg");
                        Toast.makeText((mContext), errorMsg, Toast.LENGTH_LONG).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                Toast.makeText((mContext), "accept this user", Toast.LENGTH_LONG).show();
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "Registration Error: " + error.getMessage());
//                Toast.makeText(mContext,
//                        error.getMessage(), Toast.LENGTH_LONG).show();

            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                // Posting params to register url
                Map<String, String> params = new HashMap<String, String>();
                params.put("loginid", loginId);
                params.put("requestid", requestedId);

                return params;
            }

        };

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(strReq, tag_string_req);
    }

    private void denyFriendRequest(final String loginId, final String requestedId) {
        String tag_string_req = "deny_request";

        StringRequest strReq = new StringRequest(Request.Method.POST,
                AppConfig.URL_REMOVE_REQUEST, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.d(TAG, "Register Response: " + response.toString());

                try {
                    JSONObject jObj = new JSONObject(response);
                    boolean error = jObj.getBoolean("error");
                    if (!error) {


                        Toast.makeText((mContext), "remove from request", Toast.LENGTH_LONG).show();

                    } else {

                        // Error occurred in registration. Get the error
                        // message
                        String errorMsg = jObj.getString("error_msg");
                        Toast.makeText((mContext), errorMsg, Toast.LENGTH_LONG).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                Toast.makeText((mContext), "remove from request", Toast.LENGTH_LONG).show();
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "Registration Error: " + error.getMessage());
//                Toast.makeText(mContext,
//                        error.getMessage(), Toast.LENGTH_LONG).show();

            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                // Posting params to register url
                Map<String, String> params = new HashMap<String, String>();
                params.put("loginid", loginId);
                params.put("requestid", requestedId);

                return params;
            }

        };

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(strReq, tag_string_req);
    }

    public static class ViewHolder {
        TextView requestNameTV;
        Button btnAccept;
        Button btnDeny;

        public ViewHolder(View view) {
            requestNameTV = (TextView) view.findViewById(R.id.request_name);
            btnAccept = (Button) view.findViewById(R.id.accept_request);
            btnDeny = (Button) view.findViewById(R.id.deny_request);

        }
    }
}