package com.example.kanda.ptacproject.fragments;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.example.kanda.ptacproject.R;
import com.example.kanda.ptacproject.activity.MainActivity;
import com.example.kanda.ptacproject.adepter.FriendListAdepter;
import com.example.kanda.ptacproject.app.AppConfig;
import com.example.kanda.ptacproject.app.AppController;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class TwoFragment extends Fragment {
    private static final String TAG = TwoFragment.class.getSimpleName();
    public ListView friendListView;
    public View rootView;
    public Button checkLocation;
    public ArrayList<String[]> friendList = null;
    public FriendListAdepter friendListAdepter;
    protected String loginId;
    private ProgressDialog pDialog;
    private TextView textName ;
    public TwoFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        pDialog = new ProgressDialog(getActivity());
        pDialog.setCancelable(false);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_two, container, false);
        friendListView = (ListView) rootView.findViewById(R.id.friend_list_view);
        textName = (TextView) rootView.findViewById(R.id.nameuser);
        checkLocation = (Button) rootView.findViewById(R.id.check_location);
        textName.setText(MainActivity.session.getLoginEmail());
        loginId = MainActivity.session.getLoginId();

        requestFriendList(loginId);

        return rootView;
    }

    private void requestFriendList(final String loginId) {
        pDialog.setMessage("Requesting friend list ...");
        showDialog();
        String tag_string_req = "req_friend_list";
        StringRequest strReq = new StringRequest(Request.Method.POST,
                AppConfig.URL_FRIEND_LIST, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                hideDialog();
                try {
                    JSONArray arr;
                    arr = new JSONArray(response);
                    if (arr.length() != 0) {
                        friendList = new ArrayList<>();
                        for (int i = 0; i < arr.length(); i++) {
                            JSONObject obj = (JSONObject) arr.get(i);
                            String str[] = new String[3];
                            str[0] = obj.getString("email");
                            str[1] = obj.getString("fname");
                            str[2] = obj.getString("uid");
                            friendList.add(str);
                        }

                        friendListAdepter = new FriendListAdepter(getActivity(), friendList);
                        friendListView.setAdapter(friendListAdepter);
                    }
                } catch (JSONException e) {
//                    e.printStackTrace();
//                    Log.e(TAG, "Json error: " + e.getMessage());
//                    Toast.makeText(getActivity(), "no friend", Toast.LENGTH_LONG).show();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "Load Friend List Error: " + error.getMessage());
                //Toast.makeText(getActivity(), (error.getMessage() == null ? "haha" : "eiei"), Toast.LENGTH_LONG).show();
                hideDialog();
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("loginid", loginId);

                return params;
            }
        };
        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(strReq, tag_string_req);
        hideDialog();
    }

    private void showDialog() {
        if (!pDialog.isShowing())
            pDialog.show();
    }

    private void hideDialog() {
        if (pDialog.isShowing())
            pDialog.dismiss();
    }



}
