package com.example.kanda.ptacproject.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.example.kanda.ptacproject.R;
import com.example.kanda.ptacproject.activity.MainActivity;
import com.example.kanda.ptacproject.adepter.RequestFriendAdapter;
import com.example.kanda.ptacproject.app.AppConfig;
import com.example.kanda.ptacproject.app.AppController;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class FourFragment extends Fragment {
    private static final String TAG = FourFragment.class.getSimpleName();
    //public ListView requestView;
    public View rootView;
    public ArrayList<String[]> checkRequest;
    public RequestFriendAdapter requestFriendAdapter;
    public ListView requestListView;

    public FourFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_four, container, false);
        requestListView = (ListView) rootView.findViewById(R.id.requestListView);
        checkRequest(MainActivity.session.getLoginId());
        return rootView;
    }

    private void checkRequest(final String loginId) {

        String tag_string_req = "req_check_request";
        StringRequest strReq = new StringRequest(Request.Method.POST,
                AppConfig.URL_CHECK_REQUEST, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d(TAG, "response: " + response);
                try {
                    JSONObject jObj = new JSONObject(response);
                    String requestedUsers = jObj.getString("requestusers");
                    Log.d(TAG, "requestedUsers: " + requestedUsers);

                    boolean error = jObj.getBoolean("error");

                    // Check for error node in json
                    if (!error) {
                        JSONArray arr;
                        arr = new JSONArray(requestedUsers);
                        if (arr.length() != 0) {
                            checkRequest = new ArrayList<>();
                            for (int i = 0; i < arr.length(); i++) {
                                JSONObject obj = (JSONObject) arr.get(i);
                                String str[] = new String[2];
                                str[0] = obj.getString("uid");
                                str[1] = obj.getString("email");
                                checkRequest.add(str);
                            }
                            requestFriendAdapter = new RequestFriendAdapter(getActivity(), checkRequest);
                            requestListView.setAdapter(requestFriendAdapter);
                        }
                    } else {
                        Log.d(TAG, "Json error: " + "something wrong");
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    Log.d(TAG, "Json error: " + e.getMessage());
//                    Toast.makeText(getActivity(), "Json error mm: " + e.getMessage(), Toast.LENGTH_LONG).show();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "Load Friend List Error: " + error.getMessage());
                Toast.makeText(getActivity(), (error.getMessage() == null ? "haha" : "eiei"), Toast.LENGTH_LONG).show();

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

    }

}
