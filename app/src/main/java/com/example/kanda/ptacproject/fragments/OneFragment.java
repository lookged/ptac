package com.example.kanda.ptacproject.fragments;

import android.content.Context;
import android.content.DialogInterface;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CalendarView;
import android.widget.EditText;
import android.widget.RadioGroup;
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
import com.example.kanda.ptacproject.helper.SQLiteHandler;
import com.example.kanda.ptacproject.model.Marker;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.InfoWindowAdapter;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;


public class OneFragment extends Fragment implements OnMapReadyCallback, GoogleMap.OnMapClickListener, GoogleMap.OnMapLongClickListener {

    private static final String TAG = OneFragment.class.getSimpleName();
    public EditText titleMarker;
    public EditText descriptionDestination;
    public CalendarView calendarMarker;
    public int rateMarker;
    public String Datemarker;
    public EditText descriptionMarker;

    MapView mMapView;
    LocationManager locationManager;
    MarkerOptions myLocation;

    View rootView;
    View inflator;

    private GoogleMap mGoogleMap;

    private SQLiteHandler db;
    private long then = 0;
    private int longClickDuration = 3000;

    public static String getDate(long milliSeconds, String dateFormat) {
        // Create a DateFormatter object for displaying date in specified format.
        SimpleDateFormat formatter = new SimpleDateFormat(dateFormat);

        // Create a calendar object that will convert the date and time value in milliseconds to date.
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(milliSeconds);
        return formatter.format(calendar.getTime());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_one, container, false);
        inflator = inflater.inflate(R.layout.dialog_marker, null);

        db = new SQLiteHandler(getActivity());
        locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
        mMapView = (MapView) rootView.findViewById(R.id.map);
        mMapView.onCreate(savedInstanceState);
        mMapView.onResume(); // needed to get the map to display immediately


        try {
            MapsInitializer.initialize(getActivity().getApplicationContext());
        } catch (Exception e) {
            e.printStackTrace();
        }
        final OneFragment myObj = this;
        mMapView.getMapAsync(this);


        return rootView;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        BitmapDescriptor icon = BitmapDescriptorFactory.fromResource(R.mipmap.ic_launcher);
        mGoogleMap = googleMap;

        mGoogleMap.setOnMapClickListener(this);
        mGoogleMap.setOnMapLongClickListener(this);


        googleMap.setMyLocationEnabled(true);
        googleMap.getUiSettings().setMyLocationButtonEnabled(true);
        Criteria criteria = new Criteria();
        Location location = locationManager.getLastKnownLocation(locationManager.getBestProvider(criteria, false));
        if (location != null) {
            LatLng latLngLocation = new LatLng(location.getLatitude(), location.getLongitude());
//        myLocation = new MarkerOptions().position(latLngLocation).title("Marker Title").snippet("Marker Description").icon(icon);
//        googleMap.addMarker(myLocation);
            CameraPosition cameraPosition = new CameraPosition.Builder().target(latLngLocation).zoom(15).build();
            googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));

            LocationListener lis = new LocationListener() {
                @Override
                public void onLocationChanged(Location location) {
                    if (((MainActivity) getActivity()).markerList != null) {

                        mGoogleMap.clear();


                        for (final Marker m : ((MainActivity) getActivity()).markerList) {
                            final BitmapDescriptor iconMarker;
                            if (isShowMarker(location, m)) {
                                if (m.getRateId() == 105) {
                                    iconMarker = BitmapDescriptorFactory.fromResource(R.mipmap.crimefive);

                                } else if (m.getRateId() == 104) {
                                    iconMarker = BitmapDescriptorFactory.fromResource(R.mipmap.crimefour);

                                } else if (m.getRateId() == 103) {
                                    iconMarker = BitmapDescriptorFactory.fromResource(R.mipmap.crimethree);

                                } else if (m.getRateId() == 102) {
                                    iconMarker = BitmapDescriptorFactory.fromResource(R.mipmap.crimetwo);

                                } else {
                                    iconMarker = BitmapDescriptorFactory.fromResource(R.mipmap.crimeone);

                                }
                                byte[] stringBytes = m.getAccTitle().getBytes();
                                String title = "Unsupported Text";
                                try {
                                    title = new String(stringBytes, "UTF-8");
                                } catch (UnsupportedEncodingException e) {
                                    e.printStackTrace();
                                }


                                myLocation = new MarkerOptions().position(
                                        new LatLng(m.getAccLat(), m.getAccLong())
                                ).title(title).snippet(m.getAccDescription() + "\n" + "\n" + " Time of Occurrence : " + m.getDate()).icon(iconMarker);


                                mGoogleMap.setInfoWindowAdapter(new InfoWindowAdapter() {


                                    @Override
                                    public View getInfoWindow(com.google.android.gms.maps.model.Marker marker) {
                                        return null;
                                    }

                                    @Override
                                    public View getInfoContents(com.google.android.gms.maps.model.Marker marker) {
                                        // Getting view from the layout file info_window_layout
                                        View v = LayoutInflater.from(getActivity()).inflate(R.layout.description_marker, null);

                                        // Getting the position from the marker

                                        // Getting reference to the TextView to set latitude
                                        TextView tvLat = (TextView) v.findViewById(R.id.tv_title);

                                        // Getting reference to the TextView to set longitude
                                        TextView tvLng = (TextView) v.findViewById(R.id.tv_description);

                                        // Setting the latitude
                                        tvLat.setText("Title:" + marker.getTitle());

                                        // Setting the longitude
                                        tvLng.setText("Description :" + marker.getSnippet());
                                        return v;
                                    }


                                });
                                mGoogleMap.addMarker(myLocation);
                            }
                        }

                        LatLng latLngLocation = new LatLng(location.getLatitude(), location.getLongitude());
//                    myLocation = new MarkerOptions().position(
//                            new LatLng(location.getLatitude(), location.getLongitude())
//                    ).title("moss").snippet("m").icon(icon);
//                    mGoogleMap.addMarker(myLocation);
//                        CameraPosition cameraPosition = new CameraPosition.Builder().target(latLngLocation).zoom(15).build();
//                        mGoogleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
                    }
                }

                @Override
                public void onStatusChanged(String provider, int status, Bundle extras) {

                }

                @Override
                public void onProviderEnabled(String provider) {

                }

                @Override
                public void onProviderDisabled(String provider) {

                }
            };

            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 10000, 0, lis);

        }
    }

    @Override
    public void onResume() {
        super.onResume();
        mMapView.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        mMapView.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mMapView.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mMapView.onLowMemory();
    }

    public boolean isShowMarker(Location lo, Marker marker) {
        float[] results = new float[1];
        Location.distanceBetween(lo.getLatitude(), lo.getLongitude(),
                marker.getAccLat(), marker.getAccLong(), results);
        return results[0] < 1000;
    }

    @Override
    public void onMapClick(final LatLng latLng) {

        myLocation = new MarkerOptions().position(
                new LatLng(latLng.latitude, latLng.longitude)
        ).title("Marker").snippet("Lat : " + latLng.latitude + " Lng : " + latLng.longitude);
        mGoogleMap.addMarker(myLocation);


        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        // Get the layout inflater
        LayoutInflater inflater = getActivity().getLayoutInflater();
        final View dialogView = LayoutInflater.from(getActivity()).inflate(R.layout.dialog_marker, null);
        builder.setView(dialogView);
        // Inflate and set the layout for the dialog
        // Pass null as the parent view because its going in the dialog layout

        titleMarker = (EditText) dialogView.findViewById(R.id.title_Marker);
        descriptionMarker = (EditText) dialogView.findViewById(R.id.description_Marker);


        calendarMarker = (CalendarView) dialogView.findViewById(R.id.Calendar_Marker);


        calendarMarker.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {

            @Override
            public void onSelectedDayChange(CalendarView view, int year, int month, int dayOfMonth) {
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-d");
                String dateNew = sdf.format(new Date());
                String selectDate = "" + year + "-" + month + "-" + dayOfMonth;

                Datemarker = "" + year + "/" + month + "/" + dayOfMonth;


            }
        });


        builder.setPositiveButton("Mark", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                rateMarker = ((RadioGroup) dialogView.findViewById(R.id.radioGroup_marker)).getCheckedRadioButtonId();
                Toast.makeText(getActivity(), "rateMarker" + rateMarker, Toast.LENGTH_SHORT).show();
                int ratemarker = 0;

                switch (rateMarker) {

                    case R.id.radio_lvl1:
                        ratemarker = 101;
                        break;
                    case R.id.radio_lvl2:
                        ratemarker = 102;
                        break;
                    case R.id.radio_lvl3:
                        ratemarker = 103;
                        break;
                    case R.id.radio_lvl4:
                        ratemarker = 104;
                        break;
                    case R.id.radio_lvl5:
                        ratemarker = 105;
                        break;
                }

                int accid = 0;
                String titlemarker = titleMarker.getText().toString();
                String description = descriptionMarker.getText().toString().trim();
                double latmarker = latLng.latitude;
                double lngmarker = latLng.longitude;
                int ratemarkers = ratemarker;
                String usermarker = MainActivity.session.getLoginEmail();


                if (titlemarker.length() > 0 && description.length() > 0) {
                    addMarker(accid, titlemarker, description, latmarker, lngmarker, Datemarker, ratemarkers, usermarker);
//                        Toast.makeText(getActivity(), ratemarker , Toast.LENGTH_SHORT).show();
                } else {

                    Toast.makeText(getActivity(), "Please complete all information.", Toast.LENGTH_SHORT).show();
                }
            }
        })
                .setNegativeButton("cancel", null).show();

    }

    public boolean isShowMarkerClick(LatLng latLng, Marker marker) {
        float[] results = new float[1];
        Location.distanceBetween(latLng.latitude, latLng.longitude,
                marker.getAccLat(), marker.getAccLong(), results);
        return results[0] < 1000;
    }

    @Override
    public void onMapLongClick(final LatLng latLng) {


        final AlertDialog.Builder builderdialog = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        final View dialogView = LayoutInflater.from(getActivity()).inflate(R.layout.dialog_destination, null);
        builderdialog.setView(dialogView);


        builderdialog.setPositiveButton("Mark", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                descriptionDestination = (EditText) dialogView.findViewById(R.id.description_of_destination);
                String descriptiondestination = descriptionDestination.getText().toString();
                Double destinationlat = latLng.latitude;
                Double destinationlng = latLng.longitude;
                Double mylocationlat = myLocation.getPosition().latitude;
                Double mylocationlng = myLocation.getPosition().longitude;


                Toast.makeText(getActivity(), " Mark Destination complete.", Toast.LENGTH_SHORT).show();
            }
        }).setNegativeButton("cancel", null);
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            public void run() {
                builderdialog.show();
            }
        }, 3300);

        if (((MainActivity) getActivity()).markerList != null) {
            BitmapDescriptor icon = BitmapDescriptorFactory.fromResource(R.mipmap.ic_launcher);


            for (Marker m : ((MainActivity) getActivity()).markerList) {
                BitmapDescriptor iconMarker;
                if (isShowMarkerClick(latLng, m)) {
                    if (m.getRateId() == 105) {
                        iconMarker = BitmapDescriptorFactory.fromResource(R.mipmap.crimefive);
                    } else if (m.getRateId() == 104) {
                        iconMarker = BitmapDescriptorFactory.fromResource(R.mipmap.crimefour);
                    } else if (m.getRateId() == 103) {
                        iconMarker = BitmapDescriptorFactory.fromResource(R.mipmap.crimethree);
                    } else if (m.getRateId() == 102) {
                        iconMarker = BitmapDescriptorFactory.fromResource(R.mipmap.crimetwo);
                    } else {
                        iconMarker = BitmapDescriptorFactory.fromResource(R.mipmap.crimeone);
                    }
                    byte[] stringBytes = m.getAccTitle().getBytes();
                    String title = "Unsupported Text";
                    try {
                        title = new String(stringBytes, "UTF-8");
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }
                    myLocation = new MarkerOptions().position(
                            new LatLng(m.getAccLat(), m.getAccLong())
                    ).title(title).snippet(m.getAccDescription() + " " + m.getDate()).icon(iconMarker);
                    mGoogleMap.addMarker(myLocation);

                }
            }

        }


    }

    private void addMarker(final int accid,
                           final String titelmarker,
                           final String description,
                           final double latmarker,
                           final double lngmarker,
                           final String Datemarker,
                           final int ratemarkers,
                           final String usermarker) {

        String tag_string_req = "add_mark";

        StringRequest strReq = new StringRequest(Request.Method.POST,
                AppConfig.URL_ADD_MARKER, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {


                try {
                    Log.d(TAG, response);
                    JSONObject jObj = new JSONObject(response);
                    boolean error = jObj.getBoolean("error");
                    if (!error) {

                        JSONObject user = jObj.getJSONObject("user");
                        int accid = user.getInt("accid");
                        String titelmarker = user.getString("titelmarker");
                        String description = user.getString("description");
                        Double latmarker = user.optDouble("latmarker");
                        Double lngmarker = user.optDouble("lngmarker");
                        String Datemarker = user.getString("Datemarker");
                        int ratemarkers = user.getInt("ratemarkers");
                        String usermarker = user.getString("usermarker");
                        String created_at = user.getString("created_at");


                        db.syncMarker(accid, titelmarker, description, latmarker, lngmarker, Datemarker, ratemarkers, usermarker);
                        ((MainActivity) getActivity()).markerList = db.getMarkerList();

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
                Toast.makeText(getActivity(),
                        error.getMessage(), Toast.LENGTH_LONG).show();

            }
        }) {

            @Override
            protected Map<String, String> getParams() {

                Map<String, String> params = new HashMap<String, String>();
                params.put("acc_id", Integer.toString(accid));
                params.put("acc_title", titelmarker);
                params.put("acc_description", description);
                params.put("acc_lat", Double.toString(latmarker));
                params.put("acc_long", Double.toString(lngmarker));
                params.put("date", Datemarker);
                params.put("rate_id", Integer.toString(ratemarkers));
                params.put("email", usermarker);

                return params;
            }

        };


        AppController.getInstance().addToRequestQueue(strReq, tag_string_req);
    }

}