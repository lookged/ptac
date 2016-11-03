package com.example.kanda.ptacproject.activity;

import android.app.Fragment;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.example.kanda.ptacproject.R;

import com.example.kanda.ptacproject.adepter.SmsContactAdepter;
import com.example.kanda.ptacproject.app.AppConfig;
import com.example.kanda.ptacproject.app.AppController;
import com.example.kanda.ptacproject.fragments.TwoFragment;
import com.example.kanda.ptacproject.helper.SQLiteHandler;
import com.example.kanda.ptacproject.helper.SessionManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by NamPeung on 30-Oct-16.
 */

public class SmsContactActivity extends FragmentActivity {
    private static final String TAG = TwoFragment.class.getSimpleName();
    public ListView friendListView;
    public View rootView;
    public Button checkLocation;
    public ArrayList<String[]> friendList = null;
    public SmsContactAdepter smsContactAdepter ;

    protected String loginId;

    private TextView textName ;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.smscontact);
        friendListView = (ListView) findViewById(R.id.friend_list_view);

        checkLocation = (Button) findViewById(R.id.check_location);

        loginId = MainActivity.session.getLoginId();

        requestFriendList(loginId);
    }
    private void requestFriendList(final String loginId) {

        String tag_string_req = "req_friend_list";
        StringRequest strReq = new StringRequest(Request.Method.POST,
                AppConfig.URL_FRIEND_LIST, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

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

                        smsContactAdepter = new SmsContactAdepter(getApplication(), friendList);
                        friendListView.setAdapter(smsContactAdepter);
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
               ;
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
