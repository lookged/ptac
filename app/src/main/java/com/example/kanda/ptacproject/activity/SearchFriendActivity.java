package com.example.kanda.ptacproject.activity;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.example.kanda.ptacproject.R;
import com.example.kanda.ptacproject.app.AppConfig;
import com.example.kanda.ptacproject.app.AppController;
import com.example.kanda.ptacproject.helper.SQLiteHandler;
import com.example.kanda.ptacproject.helper.SessionManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Kanda on 9/7/2016.
 */
public class SearchFriendActivity extends Activity {
    private static final String TAG = SearchFriendActivity.class.getSimpleName();
    private Button btnSearch;
    private Button btnAdd;
    private EditText inputEmailFriend;
    private SessionManager session;
    private SQLiteHandler db;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.go_to_searchfriend);
        btnSearch = (Button) findViewById(R.id.btnSearch);
        btnAdd = (Button) findViewById(R.id.btnAdd);
        btnAdd.setVisibility(View.INVISIBLE);
        inputEmailFriend = (EditText) findViewById(R.id.inputEmailFriend);
        db = new SQLiteHandler(getApplicationContext());
        session = new SessionManager(getApplicationContext());
        btnSearch.setOnClickListener(new View.OnClickListener() {

            public void onClick(View view) {
                String friendEmail = inputEmailFriend.getText().toString().trim();

                checkExistUser(friendEmail);


            }

        });
        btnAdd.setOnClickListener(new View.OnClickListener() {

            public void onClick(View view) {
                String friendEmail = inputEmailFriend.getText().toString().trim();

                addFriend(friendEmail);


            }

        });
    }
    public void checkExistUser(final String email){
        String tag_string_req = "req_searchfriend";


        StringRequest strReq = new StringRequest(Request.Method.POST,
                AppConfig.URL_SEARCH_FRIEND, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.d(TAG, "Request Response: " + response);

                try {
                    JSONObject jObj = new JSONObject(response);
                    boolean error = jObj.getBoolean("error");

                    // Check for error node in json
                    if (!error) {
                        Toast.makeText(getApplicationContext(),
                                "you can add this user", Toast.LENGTH_LONG).show();
                        btnAdd.setVisibility(View.VISIBLE);
                    } else {
                        // Error in login. Get the error message
                        String errorMsg = "user not found";
                        Toast.makeText(getApplicationContext(),
                                errorMsg, Toast.LENGTH_LONG).show();
                    }
                } catch (JSONException e) {
                    // JSON error
                    e.printStackTrace();
                    Log.d(TAG, response);
                    Toast.makeText(getApplicationContext(), "Json error Add Friend: " + e.getMessage(), Toast.LENGTH_LONG).show();
                }

            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "Request Error: " + error.getMessage());
                Toast.makeText(getApplicationContext(),
                        error.getMessage(), Toast.LENGTH_LONG).show();

            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                // Posting parameters to login url
                Map<String, String> params = new HashMap<String, String>();
                params.put("email",email);

                return params;
            }

        };

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(strReq, tag_string_req);
    }


    private void addFriend(final String friendEmail) {
        // Tag used to cancel the request
        String tag_string_req = "req_addfriend";
        StringRequest strReq = new StringRequest(Request.Method.POST,
                AppConfig.URL_ADD_FRIEND, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.d(TAG, "Register Response: " + response.toString());

                try {
                    JSONObject jObj = new JSONObject(response);
                    boolean error = jObj.getBoolean("error");
                    if (!error) {
                        Toast.makeText(getApplicationContext(), "Add Friend successfully", Toast.LENGTH_LONG).show();
                    } else {
                        // Error occurred add friend. Get the error
                        // message
                        String errorMsg = jObj.getString("error_msg");
                        Toast.makeText(getApplicationContext(),
                                "error_msg Add Friend " + errorMsg, Toast.LENGTH_LONG).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "Add Friend Error: " + error.getMessage());
                Toast.makeText(getApplicationContext(),
                        "error_msg Add Friend " + error.getMessage(), Toast.LENGTH_LONG).show();
            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                // Posting params to register url
                Map<String, String> params = new HashMap<String, String>();
                params.put("loginid", MainActivity.session.getLoginId());
                params.put("email", friendEmail);

                return params;
            }

        };

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(strReq, tag_string_req);
    }

}

