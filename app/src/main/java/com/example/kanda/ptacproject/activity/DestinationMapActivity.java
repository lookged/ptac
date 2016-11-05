package com.example.kanda.ptacproject.activity;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;

import com.example.kanda.ptacproject.activity.MainActivity;

import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.example.kanda.ptacproject.Modules.DirectionFinder;
import com.example.kanda.ptacproject.Modules.DirectionFinderListener;
import com.example.kanda.ptacproject.Modules.Route;
import com.example.kanda.ptacproject.R;
import com.example.kanda.ptacproject.app.AppConfig;
import com.example.kanda.ptacproject.app.AppController;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;

import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.example.kanda.ptacproject.model.Marker;

import java.io.UnsupportedEncodingException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by NamPeung on 19-Oct-16.
 */

public class DestinationMapActivity extends MainActivity implements GoogleMap.OnMapClickListener, OnMapReadyCallback, DirectionFinderListener {

    private GoogleMap mMap;
    private GoogleMap mMapMark;
    private Button btnFindPath;
    private EditText etOrigin;
    MarkerOptions myLocation;
    private EditText etDestination;
    private List<com.google.android.gms.maps.model.Marker> originMarkers = new ArrayList<>();
    private List<com.google.android.gms.maps.model.Marker> destinationMarkers = new ArrayList<>();
    private List<Polyline> polylinePaths = new ArrayList<>();
    private ProgressDialog progressDialog;
    String emailfriend;
    Intent intent = getIntent();
    String fnamefriend;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.destination_map);

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);


        sendRequest();
//        btnFindPath = (Button) findViewById(R.id.btnFindPath);
//        etOrigin = (EditText) findViewById(R.id.etOrigin);
//        etDestination = (EditText) findViewById(R.id.etDestination);

//        btnFindPath.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                sendRequest();
//            }
//        });
    }

    private void sendRequest() {
//        String origin = etOrigin.getText().toString();
//        String destination = etDestination.getText().toString();
        Intent intent = getIntent();
        String latdes = intent.getStringExtra("latdestination");
        String lngdes = intent.getStringExtra("lngdestination");
        String latcur = intent.getStringExtra("latcurrent");
        String lngcur = intent.getStringExtra("lngcurrent");
        emailfriend = intent.getStringExtra("emailfriend");
        fnamefriend = intent.getStringExtra("fnamefriend");
        String origin = "" + latcur + "," + lngcur;
        String destination = "" + latdes + "," + lngdes;
        if (origin.isEmpty()) {
            Toast.makeText(this, "Please enter origin address!", Toast.LENGTH_SHORT).show();
            return;
        }
        if (destination.isEmpty()) {
            Toast.makeText(this, "Please enter destination address!", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            new DirectionFinder(this, origin, destination).execute();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMapMark = googleMap;
        mMapMark.setOnMapClickListener(this);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        mMap.setMyLocationEnabled(true);

    }

    @Override
    public void onMapClick(final LatLng latLng) {


        try {


            if (this.markerList != null) {
                for (com.example.kanda.ptacproject.model.Marker m : ((MainActivity) this).markerList) {
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
                        ).title(title).snippet(m.getAccDescription() + "\n" + "\n" + " Time of Occurrence : " + m.getDate()+ "\n" ).icon(iconMarker);

                        mMapMark.addMarker(myLocation);
                        mMap.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {


                            @Override
                            public View getInfoWindow(com.google.android.gms.maps.model.Marker marker) {
                                return null;
                            }

                            @Override
                            public View getInfoContents(com.google.android.gms.maps.model.Marker marker) {

                                View v = LayoutInflater.from(getApplication()).inflate(R.layout.descritionmarkdes, null);

                                // Getting the position from the marker

                                // Getting reference to the TextView to set latitude
                                TextView tvLat = (TextView) v.findViewById(R.id.title);


                                // Getting reference to the TextView to set longitude
                                TextView tvLng = (TextView) v.findViewById(R.id.description);

                                // Setting the latitude
                                tvLat.setText("Title:" + marker.getTitle());

                                // Setting the longitude
                                tvLng.setText("Description :" + marker.getSnippet());

                                return v;
                            }


                        });
                    }
                }

            }
        } catch (Exception e) {
            Toast.makeText(this, "rateMarker" + e, Toast.LENGTH_LONG).show();
        }


    }

    public boolean isShowMarkerClick(LatLng latLng, com.example.kanda.ptacproject.model.Marker marker) {
        float[] results = new float[1];
        Location.distanceBetween(latLng.latitude, latLng.longitude,
                marker.getAccLat(), marker.getAccLong(), results);
        return results[0] < 1000;
    }

    @Override
    public void onDirectionFinderStart() {
        progressDialog = ProgressDialog.show(this, "Please wait.",
                "Finding direction..!", true);

        if (originMarkers != null) {
            for (com.google.android.gms.maps.model.Marker marker : originMarkers) {
                marker.remove();
            }
        }

        if (destinationMarkers != null) {
            for (com.google.android.gms.maps.model.Marker marker : destinationMarkers) {
                marker.remove();
            }
        }

        if (polylinePaths != null) {
            for (Polyline polyline : polylinePaths) {
                polyline.remove();
            }
        }
    }


    @Override
    public void onDirectionFinderSuccess(List<Route> routes) {
        progressDialog.dismiss();
        polylinePaths = new ArrayList<>();
        originMarkers = new ArrayList<>();
        destinationMarkers = new ArrayList<>();

        for (Route route : routes) {
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(route.startLocation, 16));
            ((TextView) findViewById(R.id.tvDuration)).setText(route.duration.text);
            ((TextView) findViewById(R.id.tvDistance)).setText(route.distance.text);

            if (fnamefriend.equalsIgnoreCase("null")){

                int num = emailfriend.indexOf("@");


                ((TextView) findViewById(R.id.tvfriendname)).setText(emailfriend.substring(0,num+1));

            }else {
                ((TextView) findViewById(R.id.tvfriendname)).setText(fnamefriend.toString());

            }
            int num = emailfriend.indexOf("@");

            originMarkers.add(mMap.addMarker(new MarkerOptions()
                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.curlocation))
                    .title(emailfriend+"  "+"Location")
//                    route.startAddress
                    .position(route.startLocation)));
            destinationMarkers.add(mMap.addMarker(new MarkerOptions()
                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.start_green))
                    .title("Destination Location")
//                    route.endAddress
                    .position(route.endLocation)));


            PolylineOptions polylineOptions = new PolylineOptions().
                    geodesic(true).
                    color(Color.BLUE).
                    width(5);

            for (int i = 0; i < route.points.size(); i++)
                polylineOptions.add(route.points.get(i));

            polylinePaths.add(mMap.addPolyline(polylineOptions));
        }
    }
}
