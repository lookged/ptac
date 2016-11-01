package com.example.kanda.ptacproject.activity;

import android.content.Context;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;

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
import com.pddstudio.urlshortener.URLShortener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = MainActivity.class.getSimpleName();
    public static SessionManager session;
    public ArrayList<Marker> markerList = null;
    public ArrayList<String[]> friendList = null;
    private TabLayout tabLayout;
    private ViewPager viewPager;
    private SQLiteHandler db;
    private long then = 0;
    String shorturl ;
     String phoneNumber;
    private int longClickDuration = 1200;
    String uiduser;
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

        getSupportFragmentManager().addOnBackStackChangedListener(getListener());
//        uIdUser();
//        Toast.makeText(getApplicationContext(),uiduser, Toast.LENGTH_LONG).show();

        // new coming
//        toolbar = (Toolbar) findViewById(R.id.toolbar);
//        setSupportActionBar(toolbar);
        // getSupportActionBar().setDisplayHomeAsUpEnabled(true);
//
        viewPager = (ViewPager) findViewById(R.id.viewpager);
        setupViewPager(viewPager);

        tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);
        setupTabIcons();


        try {
            LocationManager locationManager = (LocationManager) MainActivity.this.getSystemService(Context.LOCATION_SERVICE);
            Criteria criteria = new Criteria();
            Location location = locationManager.getLastKnownLocation(locationManager.getBestProvider(criteria, false));
            double lat = location.getLatitude();
            double lng = location.getLongitude();
            syncFriendNumber(MainActivity.session.getLoginEmail());

            String longurl = "https://www.google.co.th/maps/place/" + lat + "+" + lng + "/@" + lat + "," + lng;
            shortUrl(longurl);
        }catch (Exception e){

        }
//                            String longUrl = "http://somelink.com/very/long/url";
//                            String shortUrl = URLShortener.short(longUrl);


//        Toast.makeText(getApplicationContext(),shorturl, Toast.LENGTH_LONG).show();

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);

        fab.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    then = System.currentTimeMillis();
                } else if (event.getAction() == MotionEvent.ACTION_UP) {
                    if ((System.currentTimeMillis() - then) > longClickDuration) {


                        if (shorturl!=null) {
                            final String message = MainActivity.session.getLoginEmail() + " being in danger " +shorturl ;
                           sendSMS(phoneNumber, message);
                        Toast.makeText(getApplicationContext(), "Send SMS Complete. "+shorturl, Toast.LENGTH_LONG).show();
                        }
//                        Toast.makeText(getApplicationContext(),res, Toast.LENGTH_LONG).show();
                        Vibrator vtr = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
                        // Vibrate for 500 milliseconds
                        vtr.vibrate(500);
//
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

    }
    public String uIdUser(){
        HashMap<String, String> user = db.getUserDetails();

        uiduser = user.get("uid");
        return uiduser;
    }

    public  void syncMarker() {
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
    private FragmentManager.OnBackStackChangedListener getListener() {
        FragmentManager.OnBackStackChangedListener result = new FragmentManager.OnBackStackChangedListener() {
            public void onBackStackChanged() {
                FragmentManager manager = getSupportFragmentManager();
                if (manager != null) {
                    int backStackEntryCount = manager.getBackStackEntryCount();
                    if (backStackEntryCount == 0) {
                        finish();
                    }
                    Fragment fragment = manager.getFragments()
                            .get(backStackEntryCount - 1);
                    fragment.onResume();
                }
            }
        };
        return result;
    }

    public void shortUrl(String lurl) {
        String longUrl = lurl;

        URLShortener.shortUrl(longUrl, new URLShortener.LoadingCallback() {
            @Override
            public void startedLoading() {

            }

            @Override
            public void finishedLoading(@Nullable String shortUrl) {
                //make sure the string is not null
                if(shortUrl != null){
                     shorturl = ""+shortUrl;

                }
                else{

                }
            }
        });
    }

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

    private void syncFriendNumber(final String email) {

        String tag_string_req = "req_marker_list";
        StringRequest strReq = new StringRequest(Request.Method.POST,
                AppConfig.URL_CHECK_NUMBERFRIEND, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                try {
                    JSONArray arr;
                    arr = new JSONArray(response);
                    if (arr.length() != 0) {
                        friendList = new ArrayList<>();
                        for (int i = 0; i < arr.length(); i++) {
                            JSONObject obj = (JSONObject) arr.get(i);

                            phoneNumber = "0"+obj.getString("friendnumber");






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
                params.put("email", email);

                return params;
            }
        };
        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(strReq, tag_string_req);

    }
}

