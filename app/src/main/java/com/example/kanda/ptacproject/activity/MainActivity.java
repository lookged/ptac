package com.example.kanda.ptacproject.activity;

import android.content.Context;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.example.kanda.ptacproject.R;
import com.example.kanda.ptacproject.app.AppConfig;
import com.example.kanda.ptacproject.app.AppController;
import com.example.kanda.ptacproject.fragments.FiveFragment;
import com.example.kanda.ptacproject.fragments.FourFragment;
import com.example.kanda.ptacproject.fragments.OneFragment;
import com.example.kanda.ptacproject.fragments.TwoFragment;
import com.example.kanda.ptacproject.helper.SQLiteHandler;
import com.example.kanda.ptacproject.helper.SessionManager;
import com.example.kanda.ptacproject.model.Marker;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = MainActivity.class.getSimpleName();
    public static SessionManager session;
    public ArrayList<Marker> markerList = null;
    private Toolbar toolbar;
    private TabLayout tabLayout;
    private ViewPager viewPager;
    private SQLiteHandler db;
    private long then = 0;
    private int longClickDuration = 1200;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        db = new SQLiteHandler(getApplicationContext());
        syncMarker();
        session = new SessionManager(getApplicationContext());
        if (!session.isLoggedIn()) {
            //logoutUser();
        }
        Log.d(TAG, "onCreate");
        HashMap<String, String> user = db.getUserDetails();

        String name = user.get("name");
        String email = user.get("email");

        // new coming
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        // getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        viewPager = (ViewPager) findViewById(R.id.viewpager);
        setupViewPager(viewPager);

        tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);
        setupTabIcons();

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);

        fab.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    then = System.currentTimeMillis();
                } else if (event.getAction() == MotionEvent.ACTION_UP) {
                    if ((System.currentTimeMillis() - then) > longClickDuration) {
                        LocationManager locationManager = (LocationManager) MainActivity.this.getSystemService(Context.LOCATION_SERVICE);
                        Criteria criteria = new Criteria();
                        Location location = locationManager.getLastKnownLocation(locationManager.getBestProvider(criteria, false));
                        double lat = location.getLatitude();
                        double lng = location.getLongitude();
                        String phoneNumber = "0992467337" ;
                        String message = MainActivity.session.getLoginEmail() + " being in danger" + " https://www.google.co.th/maps/place/" + lat + "+" + lng + "/@" + lat + "," + lng;
//                        sendSMS(phoneNumber, message);
//                        Toast.makeText(getApplicationContext(),message, Toast.LENGTH_LONG).show();
                        Vibrator vtr = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
                        // Vibrate for 500 milliseconds
                        vtr.vibrate(500);
                        Toast.makeText(getApplicationContext(), "Send SMS Complete. " , Toast.LENGTH_LONG).show();
                        return false;
                    } else {

                        final AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                        // Get the layout inflater
                        LayoutInflater inflater = MainActivity.this.getLayoutInflater();
                        final View dialogView = LayoutInflater.from(MainActivity.this).inflate(R.layout.activity_detail, null);
                        builder.setView(dialogView).setNegativeButton("Back", null);
                        builder.show();
//                        Toast.makeText(getApplicationContext(), "", Toast.LENGTH_LONG).show();
                        return true;
                    }
                }
                return true;
            }
        });
//        fab.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
////                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
////                        .setAction("Action", null).show();
//
////                AlertDialog alertDialog = new AlertDialog.Builder(MainActivity.this)
////                .setIcon(R.mipmap.ic_launcher)
////                .setTitle("Send SMS")
////                .setMessage("Are you sure you want to Send SMS?" )
////                .setPositiveButton("YES", new DialogInterface.OnClickListener() {
////                    @Override
////                    public void onClick(DialogInterface dialog, int which) {
////                        LocationManager locationManager = (LocationManager) MainActivity.this.getSystemService(Context.LOCATION_SERVICE);
////                        Criteria criteria = new Criteria();
////                        Location location = locationManager.getLastKnownLocation(locationManager.getBestProvider(criteria, false));
////                        double lat = location.getLatitude();
////                        double lng = location.getLongitude();
////                        String phoneNumber = "0992467337" ;
////                        String message = "https://www.google.co.th/maps/place/"+lat+"+"+lng+"/@"+lat+","+lng+",20z";
////                        sendSMS(phoneNumber, message);
////                        Toast.makeText(getApplicationContext(), "Send SMS Complete. " , Toast.LENGTH_LONG).show();
////                    }
////
////                })
////                .setNegativeButton("No", null)
////                .show();
//            }
//        });
    }

    private void syncMarker() {
        db.delMarker();
        String tag_string_req = "req_marker_list";
        StringRequest strReq = new StringRequest(Request.Method.POST,
                AppConfig.URL_MARKER_LIST, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONArray arr;
                    arr = new JSONArray(response);
                    if (arr.length() != 0) {
                        for (int i = 0; i < arr.length(); i++) {
                            JSONObject obj = (JSONObject) arr.get(i);
                            db.syncMarker(
                                    obj.getInt("acc_id"),
                                    obj.getString("acc_title"),
                                    obj.getString("acc_description"),
                                    obj.getDouble("acc_lat"),
                                    obj.getDouble("acc_long"),
                                    obj.getString("date"),
                                    obj.getInt("rate_id"),
                                    obj.getString("email")
                            );
                        }
                        markerList = db.getMarkerList();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    Log.e(TAG, "Json error: " + e.getMessage());
                    Toast.makeText(getApplicationContext(), "Json error: " + e.getMessage(), Toast.LENGTH_LONG).show();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "Load Marker List Error: " + error.getMessage());
                Toast.makeText(getApplicationContext(), error.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(strReq, tag_string_req);
    }

    /**
     * Logging out the user. Will set isLoggedIn flag to false in shared
     * preferences Clears the user data from sqlite users table
     */
//    public  void logoutUser() {
//        session.setLogin(false);
//        db.deleteUsers();
//        // Launching the login activity
//        Intent intent = new Intent(MainActivity.this, LoginActivity.class);
//        startActivity(intent);
//        finish();
//    }

    public void sendSMS(String phoneNumber, String message) {
        SmsManager sms = SmsManager.getDefault();
        sms.sendTextMessage(phoneNumber, null, message, null, null);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        // return true so that the menu pop up is opened
        return true;
    }

//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        // Handle item selection
//        switch (item.getItemId()) {
//            case R.id.action_logout:
//                logoutUser();
//                return true;
//            default:
//                return super.onOptionsItemSelected(item);
//        }
//    }

    private void setupTabIcons() {
        int[] tabIcons = {
                R.drawable.ic_home_white,
                R.drawable.ic_face_white,
                R.drawable.requestfriend,
                R.drawable.menulist
        };
        for (int i = 0; i <= 3; i++) {
            tabLayout.getTabAt(i).setIcon(tabIcons[i]);
        }
    }

    private void setupViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFrag(new OneFragment(), "ONE");
        adapter.addFrag(new TwoFragment(), "TWO");
        adapter.addFrag(new FourFragment(), "FOUR");
        adapter.addFrag(new FiveFragment(), "FIVE");
        viewPager.setAdapter(adapter);
    }

    class ViewPagerAdapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        public ViewPagerAdapter(FragmentManager manager) {
            super(manager);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        public void addFrag(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            // return null to display only the icon
            return null;
        }
    }
}

