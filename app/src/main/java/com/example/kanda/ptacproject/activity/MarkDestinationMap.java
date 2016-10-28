package com.example.kanda.ptacproject.activity;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.example.kanda.ptacproject.Modules.DirectionFinderListener;
import com.example.kanda.ptacproject.Modules.Route;
import com.example.kanda.ptacproject.R;
import com.example.kanda.ptacproject.app.AppConfig;
import com.example.kanda.ptacproject.app.AppController;
import com.example.kanda.ptacproject.fragments.OneFragment;
import com.example.kanda.ptacproject.helper.SQLiteHandler;
import com.example.kanda.ptacproject.model.Marker;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;



/**
 * Created by NamPeung on 25-Oct-16.
 */

public class MarkDestinationMap extends MainActivity implements GoogleMap.OnMapClickListener, OnMapReadyCallback, DirectionFinderListener {


    private static final String TAG = OneFragment.class.getSimpleName();
    public AutoCompleteTextView titleMarker;
    public EditText descriptionDestination;
    public CalendarView calendarMarker;
    public int rateMarker;
    public ArrayList<Marker> markerList = null;
    public String Datemarker;
    public AutoCompleteTextView descriptionMarker;
    public EditText Edorigin ;
    public Button Btnlocation;

    MapView mMapView;
    LocationManager locationManager;
    MarkerOptions myLocation;
    View rootView;
    View inflator;
    private int lengthMap = 1000;
    private GoogleMap mGoogleMap;
    private int maxDate = -90;
    private List<com.google.android.gms.maps.model.Marker> originMarkers = new ArrayList<>();
    private List<com.google.android.gms.maps.model.Marker> destinationMarkers = new ArrayList<>();
    private List<Polyline> polylinePaths = new ArrayList<>();
    private ProgressDialog progressDialog;

    private Spinner lengthSpinner;
    private Spinner dateSpinner;
    private ArrayList<String> lSpinner = new ArrayList<String>();
    private ArrayList<String> dSpinner = new ArrayList<String>();

    String[] lengthdate = {"ระยะ 90 วัน","ระยะ 180 วัน","ระยะ 365 วัน"};

    String[] lengthdistance = {"ระยะ 1 กม.","ระยะ 5 กม.","ระยะ 10 กม."};


    private SQLiteHandler db;
    private long then = 0;
    private int longClickDuration = 3000;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.markdestinationmap);

    }

    @Override
    public void onDirectionFinderStart() {

    }

    @Override
    public void onDirectionFinderSuccess(List<Route> route) {

    }

    @Override
    public void onMapClick(final LatLng latLng) {

        final AlertDialog.Builder builderdialog = new AlertDialog.Builder(this);
        LayoutInflater inflater = this.getLayoutInflater();
        final View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_destination, null);
        builderdialog.setView(dialogView);


        builderdialog.setPositiveButton("Mark", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                DateFormat df = new SimpleDateFormat("dd/MM/yyyy");
                descriptionDestination = (EditText) dialogView.findViewById(R.id.description_of_destination);
                String email = MainActivity.session.getLoginEmail();
                String descriptiondestination = descriptionDestination.getText().toString();
                Double destinationlat = latLng.latitude;
                Double destinationlng = latLng.longitude;
                Double mylocationlat = myLocation.getPosition().latitude;
                Double mylocationlng = myLocation.getPosition().longitude;
                String date = df.format(new Date());

                addDestination(email,descriptiondestination,destinationlat,destinationlng,mylocationlat,mylocationlng,date);
//                Toast.makeText(this, " Mark Destination complete.", Toast.LENGTH_SHORT).show();

            }
        }).setNegativeButton("cancel", null);
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            public void run() {
                builderdialog.show();
            }
        }, 3300);

        if (((MainActivity) this).markerList != null) {
            BitmapDescriptor icon = BitmapDescriptorFactory.fromResource(R.mipmap.ic_launcher);


            for (Marker m : ((MainActivity) this).markerList) {
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

    @Override
    public void onMapReady(GoogleMap googleMap) {

    }
    public boolean isShowMarkerClick(LatLng latLng, Marker marker) {
        float[] results = new float[1];
        Location.distanceBetween(latLng.latitude, latLng.longitude,
                marker.getAccLat(), marker.getAccLong(), results);
        return results[0] < 1000;
    }
    private void addDestination(final String email,
                                final String descriptiondestination,
                                final Double destinationlat,
                                final Double destinationlng,
                                final Double mylocationlat,
                                final Double mylocationlng,
                                final String date ) {

        String tag_string_req = "add_destination";

        StringRequest strReq = new StringRequest(Request.Method.POST,
                AppConfig.URL_ADD_DESTINATION, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {


                try {
                    Log.d(TAG, response);
                    JSONObject jObj = new JSONObject(response);
                    boolean error = jObj.getBoolean("error");
                    if (!error) {

                        JSONObject user = jObj.getJSONObject("user");
                        String email = user.optString("email");
                        String descriptiondestination = user.getString("descriptiondestination");
                        Double destinationlat = user.optDouble("destinationlat");
                        Double destinationlng = user.optDouble("destinationlng");
                        Double mylocationlat = user.optDouble("mylocationlat");
                        Double mylocationlng = user.optDouble("mylocationlng");
                        String date = user.optString("date");



//                        db.syncMarker(accid, titelmarker, description, latmarker, lngmarker, Datemarker, ratemarkers, usermarker);
//                        ((MainActivity) getActivity()).markerList = db.getMarkerList();

                    } else {


                        String errorMsg = jObj.getString("error_msg");
//                        Toast.makeText(,
//                                "error_msg" + errorMsg, Toast.LENGTH_LONG).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }


//                Toast.makeText(this, "Marke Destination completed", Toast.LENGTH_SHORT).show();
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "addDestination Error: " + error.getMessage());
//                Toast.makeText(getActivity(),
//                        error.getMessage(), Toast.LENGTH_LONG).show();

            }
        }) {

            @Override
            protected Map<String, String> getParams() {

                Map<String, String> params = new HashMap<String, String>();
                params.put("email", email);
                params.put("descriptiondestination", descriptiondestination);
                params.put("destinationlat",  Double.toString(destinationlat));
                params.put("destinationlng", Double.toString(destinationlng));
                params.put("mylocationlat", Double.toString(mylocationlat));
                params.put("mylocationlng",  Double.toString(mylocationlng));
                params.put("date", date);


                return params;
            }

        };


        AppController.getInstance().addToRequestQueue(strReq, tag_string_req);
    }
}
