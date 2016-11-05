package com.example.kanda.ptacproject.fragments;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
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
import com.example.kanda.ptacproject.activity.DestinationMapActivity;
import com.example.kanda.ptacproject.activity.LoginActivity;
import com.example.kanda.ptacproject.activity.MainActivity;
import com.example.kanda.ptacproject.activity.MarkDestinationActivity;
import com.example.kanda.ptacproject.activity.SmsContactActivity;
import com.example.kanda.ptacproject.app.AppConfig;
import com.example.kanda.ptacproject.app.AppController;
import com.example.kanda.ptacproject.helper.SQLiteHandler;
import com.example.kanda.ptacproject.helper.SessionManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


public class FiveFragment extends Fragment implements NavigationView.OnNavigationItemSelectedListener {
    private static final String TAG = FiveFragment.class.getSimpleName();
    public static SessionManager session;
    private TextView txtName;
    private TextView txtAddress;
    private TextView txtPhonenumber;
    private Button btnSearch;
    private Button btnAdd;
    private EditText inputEmailFriend;
    private ArrayList<String[]> informationUser;
    public String lnamedb;
    private ProgressDialog progressDialog;
    public String fnamedb;
    public String addressdb;
    public String phoneonuserdb;
    boolean CHECK = false;
    EditText editfname;
    EditText editlname;
    EditText editaddress;
    EditText editphone;

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
        NavigationView navigationView = (NavigationView) rootView.findViewById(R.id.nav_view);
        navigationView.setItemIconTintList(null);
        navigationView.setNavigationItemSelectedListener(this);


        return rootView;

    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        try {


        int id = item.getItemId();

        if (id == R.id.nav_Profile) {
            proFile();
        } else if (id == R.id.nav_EditProfile) {
            editProFile();
        } else if (id == R.id.nav_AddFriend) {
            addFriend();
        } else if (id == R.id.nav_Setfriend) {
            Intent i = new Intent(getActivity(), SmsContactActivity.class);


            startActivity(i);
        } else if (id == R.id.nav_Destination) {
            Intent i = new Intent(getActivity(), MarkDestinationActivity.class);


            startActivity(i);
//                            Toast.makeText(mContext, "no frien
        } else if (id == R.id.nav_Logout) {
            logoutUser();

        }



        }catch (Exception e){

        }
        return true;
    }
//
//    private void setDestination() {
//        AlertDialog.Builder buildersetfriend = new AlertDialog.Builder(getActivity());
//        final View setfriend = LayoutInflater.from(getActivity()).inflate(R.layout.setcontact, null);
//        buildersetfriend.setView(setfriend);
//        buildersetfriend.setPositiveButton("Edit", new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialog, int id) {
//                Toast.makeText(getActivity(), " Complete. ", Toast.LENGTH_LONG).show();
//            }
//        }).setNegativeButton("cancel", null).show();
//
//
//    }

//    private void setFriend() {
//        AlertDialog.Builder buildersetfriend = new AlertDialog.Builder(getActivity());
//        final View setfriend = LayoutInflater.from(getActivity()).inflate(R.layout.setcontact, null);
//        buildersetfriend.setView(setfriend);
//        buildersetfriend.setPositiveButton("Edit", new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialog, int id) {
//                Toast.makeText(getActivity(), " Complete. ", Toast.LENGTH_LONG).show();
//            }
//        }).setNegativeButton("cancel", null).show();
//
//
//    }

    private void proFile() {
        try {
        progressDialog = ProgressDialog.show(getActivity(), "Please wait.",
                "Finding Information..!", true);


        syncInformationUser(MainActivity.session.getLoginEmail());

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            public void run() {
                //your code here
                progressDialog.dismiss();
                AlertDialog.Builder builderprofile = new AlertDialog.Builder(getActivity());
                final View dialogView = LayoutInflater.from(getActivity()).inflate(R.layout.activity_profile, null);
                builderprofile.setView(dialogView);
                txtName = (TextView) dialogView.findViewById(R.id.text_name);
                txtAddress = (TextView) dialogView.findViewById(R.id.text_address);
                txtPhonenumber = (TextView) dialogView.findViewById(R.id.text_phoneno);
                try{
                    txtName.setText(fnamedb.toUpperCase()+"  "+lnamedb.toUpperCase());
                    txtPhonenumber.setText("Phone Number :  0"+phoneonuserdb);
                    txtAddress.setText("Address :  "+addressdb.toUpperCase());
                }catch (Exception e){

                }


                builderprofile.show();
            }
        }, 2000);
        } catch (Exception e) {
//            Toast.makeText(getActivity(), "Editprofile :  " + e.getMessage(), Toast.LENGTH_LONG).show();
        }










    }

    private void editProFile() {
        try {
            progressDialog = ProgressDialog.show(getActivity(), "Please wait.",
                    "Finding Information..!", true);


            syncInformationUser(MainActivity.session.getLoginEmail());

            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                public void run() {
                    //your code here
                    progressDialog.dismiss();
                    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                    final View dialogView = LayoutInflater.from(getActivity()).inflate(R.layout.edit_profile, null);
                    builder.setView(dialogView);
                    editfname = (EditText) dialogView.findViewById(R.id.text_first_name);
                    editlname = (EditText) dialogView.findViewById(R.id.text_last_name);
                    editaddress = (EditText) dialogView.findViewById(R.id.text_address);
                    editphone = (EditText) dialogView.findViewById(R.id.text_phoneno);
                    editfname.setHint("First Name : "+fnamedb);
                    editlname.setHint("Last Name : "+lnamedb);
                    editaddress.setHint("Address : "+addressdb);
                    editphone.setHint("Phone Number : " +"0"+phoneonuserdb);

                    builder.setPositiveButton("Edit", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int id) {


                            EditText editfname = (EditText) dialogView.findViewById(R.id.text_first_name);
                            EditText editlname = (EditText) dialogView.findViewById(R.id.text_last_name);
                            EditText editaddress = (EditText) dialogView.findViewById(R.id.text_address);
                            EditText editphone = (EditText) dialogView.findViewById(R.id.text_phoneno);

                            String fname = editfname.getText().toString();
                            String lname = editlname.getText().toString();
                            String address = editaddress.getText().toString();
                            String phoneno = editphone.getText().toString();
                            String email = MainActivity.session.getLoginEmail();
                            if(fname.isEmpty()){
                                fname = fnamedb;
                            }if (lname.isEmpty()){
                                lname = lnamedb;
                            }if (address.isEmpty()){
                                address = addressdb;
                            }if (phoneno.isEmpty()){
                                phoneno = phoneonuserdb;
                            }
                            try {
                            if (!fname.isEmpty()&&!lname.isEmpty()&&!address.isEmpty()&&!phoneno.isEmpty()){
                                updateProfile(fname, lname, address, phoneno, email);
                                Toast.makeText(getActivity(), " Edit Complete. ", Toast.LENGTH_LONG).show();
                            }
                            } catch (Exception e) {

                            }

                        }
                    }).setNegativeButton("cancel", null).show();
                }
            }, 1500);



        } catch (Exception e) {
            Toast.makeText(getActivity(), "Editprofile :  " + e.getMessage(), Toast.LENGTH_LONG).show();
        }


    }

    private void addFriend() {
        final AlertDialog.Builder builderaddfriend = new AlertDialog.Builder(getActivity());
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
                if(friendEmail.equalsIgnoreCase(MainActivity.session.getLoginEmail())){
                    Toast.makeText(getActivity(), "You can't add yourself add friend ", Toast.LENGTH_LONG).show();
                }else {
                    checkExistUser(friendEmail);
                }



            }

        });
        btnAdd.setOnClickListener(new View.OnClickListener() {

            public void onClick(View view) {
                String friendEmail = inputEmailFriend.getText().toString().trim();

                addFriend(friendEmail);
                btnAdd.setVisibility(View.INVISIBLE);

            }

        });
        builderaddfriend.show();
    }

    private void logoutUser() {
        AlertDialog.Builder builderlogout = new AlertDialog.Builder(getActivity());
        builderlogout.setMessage("Do you want to exit?").setPositiveButton("Logout", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                try {
                    session.setLogin(false);
                    db.deleteUsers();
                    // Launching the login activity
                    Intent intent = new Intent(getContext(), LoginActivity.class);
                    startActivity(intent);
                } catch (Exception e) {
                    Log.d(TAG, "Logout : " + e);
                }

            }
        }).setNegativeButton("cancel", null).show();


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
                Toast.makeText(getActivity(),"Internet  not found!!", Toast.LENGTH_LONG).show();

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
                        "error_msg Add Friend " + "Internet  not found!!", Toast.LENGTH_LONG).show();
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

    private void updateProfile(final String fname,
                               final String lname,
                               final String address,
                               final String phoneno,
                               final String email) {

        String tag_string_req = "add_mark";

        StringRequest strReq = new StringRequest(Request.Method.POST,
                AppConfig.URL_UPDATE_PROFILE, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {


                try {
                    Log.d(TAG, response);
                    JSONObject jObj = new JSONObject(response);
                    boolean error = jObj.getBoolean("error");
                    if (!error) {

                        JSONObject user = jObj.getJSONObject("user");

                        String fname = user.getString("fname");
                        String lname = user.getString("lname");
                        String address = user.getString("address");
                        int phoneno = user.optInt("phoneno");
                        String email = user.getString("email");


//                        db.syncMarker(accid, titelmarker, description, latmarker, lngmarker, Datemarker, ratemarkers, usermarker);
//                        ((MainActivity) getActivity()).markerList = db.getMarkerList();

                    } else {


                        String errorMsg = jObj.getString("error_msg");
                        Toast.makeText(getActivity(),
                                "error_msg" + errorMsg, Toast.LENGTH_LONG).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }


                Toast.makeText(getActivity(), "Marker completed", Toast.LENGTH_SHORT).show();
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "addMarker Error: " + error.getMessage());
                Toast.makeText(getActivity(),"Internet  not found!!", Toast.LENGTH_LONG).show();

            }
        }) {

            @Override
            protected Map<String, String> getParams() {

                Map<String, String> params = new HashMap<String, String>();
                params.put("fname", fname);
                params.put("lname", lname);
                params.put("address", address);
                params.put("phoneno", phoneno);
                params.put("email", email);

                return params;
            }

        };


        AppController.getInstance().addToRequestQueue(strReq, tag_string_req);
    }


    private void syncInformationUser(final String email) {

        String tag_string_req = "req_marker_list";
        StringRequest strReq = new StringRequest(Request.Method.POST,
                AppConfig.URL_Information, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                try {
                    JSONArray arr;
                    arr = new JSONArray(response);
                    if (arr.length() != 0) {
                        informationUser = new ArrayList<>();
                        for (int i = 0; i < arr.length(); i++) {
                            JSONObject obj = (JSONObject) arr.get(i);
                            fnamedb = obj.getString("fname");
                            lnamedb = obj.getString("lname");
                            addressdb = obj.getString("address");
                            phoneonuserdb = obj.getString("phoneno");
                            CHECK = true;


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
                Toast.makeText(getActivity(),"Internet  not found!!", Toast.LENGTH_LONG).show();
                //Toast.makeText(getActivity(), (error.getMessage() == null ? "haha" : "eiei"), Toast.LENGTH_LONG).show();

            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("email", email);

                return params;
            }
        };
        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(strReq, tag_string_req);

    }


}
