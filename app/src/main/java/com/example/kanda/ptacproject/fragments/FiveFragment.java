package com.example.kanda.ptacproject.fragments;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.example.kanda.ptacproject.R;
import com.example.kanda.ptacproject.activity.LoginActivity;
import com.example.kanda.ptacproject.activity.MainActivity;
import com.example.kanda.ptacproject.app.AppConfig;
import com.example.kanda.ptacproject.app.AppController;
import com.example.kanda.ptacproject.helper.SQLiteHandler;
import com.example.kanda.ptacproject.helper.SessionManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;


public class FiveFragment extends Fragment{
    private static final String TAG = FiveFragment.class.getSimpleName();
    public static SessionManager session;
    private Button btnAddFriend;
    private Button btnLogout;
    private Button btnEditproFile;
    private Button btnProfile;
    private Button btnSetFriend;
    private TextView txtName;
    private TextView txtAddress;
    private TextView txtPhonenumber;
    private Button btnSearch;
    private Button btnAdd;
    private EditText inputEmailFriend;
    private SQLiteHandler db;
    public FiveFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        db = new SQLiteHandler(getActivity());

        session = new SessionManager(getActivity());
        View rootView = inflater.inflate(R.layout.fragment_five, container, false);
        btnAddFriend = (Button) rootView.findViewById(R.id.btnAddFriend);
        btnLogout = (Button) rootView.findViewById(R.id.btnLogout);
        btnEditproFile = (Button) rootView.findViewById(R.id.btnEditProfile);
        btnProfile = (Button) rootView.findViewById(R.id.btnProfile);
        btnSetFriend = (Button) rootView.findViewById(R.id.btnSetfriend);
        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        final AlertDialog.Builder builderaddfriend = new AlertDialog.Builder(getActivity());
        final AlertDialog.Builder builderprofile = new AlertDialog.Builder(getActivity());
        final AlertDialog.Builder builderlogout = new AlertDialog.Builder(getActivity());
        final AlertDialog.Builder buildersetfriend = new AlertDialog.Builder(getActivity());

        btnSetFriend.setOnClickListener(new View.OnClickListener() {

            public void onClick(View view) {
                final View buildersetfriend = LayoutInflater.from(getActivity()).inflate(R.layout.setcontact, null);
                builder.setView(buildersetfriend);
                builder.setPositiveButton("Edit", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        Toast.makeText(getActivity(), " Complete. ", Toast.LENGTH_LONG).show();
                    }
                }).setNegativeButton("cancel", null).show();

            }
        });

        btnProfile.setOnClickListener(new View.OnClickListener() {

            public void onClick(View view) {
                final View dialogView = LayoutInflater.from(getActivity()).inflate(R.layout.activity_profile, null);
                builderprofile.setView(dialogView);
                txtName = (TextView) dialogView.findViewById(R.id.text_name);
                txtAddress = (TextView) dialogView.findViewById(R.id.text_address);
                txtPhonenumber = (TextView) dialogView.findViewById(R.id.text_phoneno);
                txtName.setText("Aphisit  Jankiaw");
                txtPhonenumber.setText("Phone Number :  0888888888");
                txtAddress.setText("Address :  3 ซอย.บางแค 16 แขวง  บางแค เขต บางแค กทม. 10160");
                builderprofile.show();

            }
        });
        btnEditproFile.setOnClickListener(new View.OnClickListener() {

            public void onClick(View view) {
                final View dialogView = LayoutInflater.from(getActivity()).inflate(R.layout.edit_profile, null);
                builder.setView(dialogView);
                builder.setPositiveButton("Edit", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        Toast.makeText(getActivity(), " Edit Complete. ", Toast.LENGTH_LONG).show();
                    }
                }).setNegativeButton("cancel", null).show();

            }
        });
        btnAddFriend.setOnClickListener(new View.OnClickListener() {

            public void onClick(View view) {
                final View dialogView = LayoutInflater.from(getActivity()).inflate(R.layout.go_to_searchfriend, null);

                builderaddfriend.setView(dialogView);

                btnSearch = (Button) dialogView.findViewById(R.id.btnSearch);
                btnAdd = (Button) dialogView.findViewById(R.id.btnAdd);
                btnAdd.setVisibility(View.INVISIBLE);
                inputEmailFriend = (EditText) dialogView.findViewById(R.id.inputEmailFriend);
                db = new SQLiteHandler(getActivity());
                session = new SessionManager(getActivity());
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
                builderaddfriend.show();
//                Intent i = new Intent(getActivity(),
//                        SearchFriendActivity.class);
//                startActivity(i);

            }
        });

        btnLogout.setOnClickListener(new View.OnClickListener() {

            public void onClick(View view) {
                builderlogout.setMessage("Do you want to exit?").setPositiveButton("Logout", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        try {
                            logoutUser();
                        } catch (Exception e) {
                            Log.d(TAG, "Logout : " + e);
                        }

                    }
                }).setNegativeButton("cancel", null).show();

            }
        });
        return rootView;
    }

    private void logoutUser() {
        session.setLogin(false);
        db.deleteUsers();
        // Launching the login activity
        Intent intent = new Intent(getContext(), LoginActivity.class);
        startActivity(intent);

    }

    public void checkExistUser(final String email) {
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
                        Toast.makeText(getActivity(),
                                "you can add this user", Toast.LENGTH_LONG).show();
                        btnAdd.setVisibility(View.VISIBLE);
                    } else {
                        // Error in login. Get the error message
                        String errorMsg = "user not found";
                        Toast.makeText(getActivity(),
                                errorMsg, Toast.LENGTH_LONG).show();
                    }
                } catch (JSONException e) {
                    // JSON error
                    e.printStackTrace();
                    Log.d(TAG, response);
                    Toast.makeText(getActivity(), "Json error Add Friend: " + e.getMessage(), Toast.LENGTH_LONG).show();
                }

            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "Request Error: " + error.getMessage());
                Toast.makeText(getActivity(),
                        error.getMessage(), Toast.LENGTH_LONG).show();

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
                        Toast.makeText(getActivity(), "Add Friend successfully", Toast.LENGTH_LONG).show();
                    } else {
                        // Error occurred add friend. Get the error
                        // message
                        String errorMsg = jObj.getString("error_msg");
                        Toast.makeText(getActivity(),
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
                Toast.makeText(getActivity(),
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
