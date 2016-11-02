package com.example.kanda.ptacproject.activity;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
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
import com.example.kanda.ptacproject.adepter.FriendListAdepter;
import com.example.kanda.ptacproject.app.AppConfig;
import com.example.kanda.ptacproject.app.AppController;
import com.example.kanda.ptacproject.fragments.OneFragment;
import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.appindexing.Thing;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by NamPeung on 01-Nov-16.
 */

public class MarkDestinationActivity extends MainActivity implements GoogleMap.OnMapClickListener, OnMapReadyCallback, DirectionFinderListener, GoogleMap.OnMapLongClickListener {

    private GoogleMap mMap;
    private static final String TAG = OneFragment.class.getSimpleName();
    LocationManager locationManager;
    public EditText Edorigin;
    public Button Btnlocation;
    MarkerOptions myLocation;
    LocationListener lis;
    private List<Marker> originMarkers = new ArrayList<>();
    private List<Marker> destinationMarkers = new ArrayList<>();
    private List<Polyline> polylinePaths = new ArrayList<>();
    private ProgressDialog progressDialog;
    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.markdestinationmap);

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        Edorigin = (EditText) findViewById(R.id.etOrigin);
        Btnlocation = (Button) findViewById(R.id.btnlocation);

//        sendRequest();
//        btnFindPath = (Button) findViewById(R.id.btnFindPath);
//        etOrigin = (EditText) findViewById(R.id.etOrigin);
//        etDestination = (EditText) findViewById(R.id.etDestination);

//        btnFindPath.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                sendRequest();
//            }
//        });
        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
    }




    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        mMap.setOnMapClickListener(this);
        mMap.setOnMapLongClickListener(this);
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
        Criteria criteria = new Criteria();
        locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        final Location location = locationManager.getLastKnownLocation(locationManager.getBestProvider(criteria, false));
        Btnlocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String origin = Edorigin.getText().toString().trim();

                if (origin.isEmpty()) {
                    Toast.makeText(getApplication(), "Please enter origin address!", Toast.LENGTH_SHORT).show();
                    return;
                }
                try {

                    sendRequest(origin,location);
                } catch (Exception e) {

                }

            }
        });
        if (location != null) {
            LatLng latLngLocation = new LatLng(location.getLatitude(), location.getLongitude());
            CameraPosition cameraPosition = new CameraPosition.Builder().target(latLngLocation).zoom(15).build();
            googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
            lis = new LocationListener() {
                @Override
                public void onLocationChanged(Location location) {


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

            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 10000, 0, lis);
        }
    }
    private void sendRequest(String originn,Location location) {
//        String origin = etOrigin.getText().toString();
//        String destination = etDestination.getText().toString();
        String origin = originn.toString().trim();

        String destination = "" + location.getLatitude() + "," + location.getLongitude();


        try {
            new DirectionFinder(this, origin, destination).execute();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }
    @Override
    public void onMapLongClick(final LatLng latLng) {
                AlertDialog.Builder buildersetfriend = new AlertDialog.Builder(this);
        final View setfriend = LayoutInflater.from(getApplication()).inflate(R.layout.dialog_destination, null);
        buildersetfriend.setView(setfriend);
        final EditText descrip = (EditText) setfriend.findViewById(R.id.description_of_destination);
        Criteria criteria = new Criteria();
        locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        final Location location = locationManager.getLastKnownLocation(locationManager.getBestProvider(criteria, false));
        buildersetfriend.setPositiveButton("Mark", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                String email = MainActivity.session.getLoginEmail();
                Double destinationlat = latLng.latitude;
                Double destinationlng = latLng.longitude;
                Double mylocationlat =location.getLatitude() ;
                Double mylocationlng = location.getLongitude();
                String descriptiondestination = descrip.getText().toString();
                String uid = MainActivity.session.getLoginId();

                SimpleDateFormat df = new SimpleDateFormat("dd-MM-yyyy");
                String date = df.format(System.currentTimeMillis());

                markDestination(email,destinationlat,destinationlng,mylocationlat,mylocationlng,date,descriptiondestination,uid);
//               Toast.makeText(getApplication(), " Complete. "+date, Toast.LENGTH_LONG).show();
            }
        }).setNegativeButton("cancel", null).show();

    }
    @Override
    public void onMapClick(final LatLng latLng) {

        final AlertDialog.Builder builderdialog = new AlertDialog.Builder(getApplication());

        final View dialogView = LayoutInflater.from(getApplication()).inflate(R.layout.dialog_destination, null);
        builderdialog.setView(dialogView);


        if (((MainActivity) this).markerList != null) {


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

                    mMap.addMarker(myLocation);
                    mMap.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {


                        @Override
                        public View getInfoWindow(Marker marker) {
                            return null;
                        }

                        @Override
                        public View getInfoContents(Marker marker) {

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
            for (Marker marker : originMarkers) {
               marker.remove();
            }
        }

        if (destinationMarkers != null) {
            for (Marker marker : destinationMarkers) {
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

            originMarkers.add(mMap.addMarker(new MarkerOptions()
                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.start_green))
                    .title(route.startAddress)
                    .position(route.startLocation)));
            destinationMarkers.add(mMap.addMarker(new MarkerOptions()
                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.curlocation))
                    .title(route.endAddress)
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

    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    public Action getIndexApiAction() {
        Thing object = new Thing.Builder()
                .setName("MarkDestination Page") // TODO: Define a title for the content shown.
                // TODO: Make sure this auto-generated URL is correct.
                .setUrl(Uri.parse("http://[ENTER-YOUR-URL-HERE]"))
                .build();
        return new Action.Builder(Action.TYPE_VIEW)
                .setObject(object)
                .setActionStatus(Action.STATUS_TYPE_COMPLETED)
                .build();
    }

    @Override
    public void onStart() {
        super.onStart();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client.connect();
        AppIndex.AppIndexApi.start(client, getIndexApiAction());
    }

    @Override
    public void onStop() {
        super.onStop();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        AppIndex.AppIndexApi.end(client, getIndexApiAction());
        client.disconnect();
    }
    public void markDestination(final String email,final Double destinationlat,final Double destinationlng,final Double mylocationlat,final Double mylocationlng,final String date,final String descriptiondestination,final String uid) {
        String tag_string_req = "req_searchfriend";


        StringRequest strReq = new StringRequest(Request.Method.POST,
                AppConfig.URL_ADD_DESTINATION, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.d(TAG, "Request Response: " + response);

                try {
                    JSONObject jObj = new JSONObject(response);
                    boolean error = jObj.getBoolean("error");

                    // Check for error node in json
                    if (!error) {
                        Toast.makeText(getApplication(), "Mark Destination Complete!!", Toast.LENGTH_LONG).show();
                    } else {
                        // Error in login. Get the error message
                        String errorMsg = "user not found";
//                        Toast.makeText(mContext,
//                                errorMsg, Toast.LENGTH_LONG).show();
                    }
                } catch (JSONException e) {
                    // JSON error
                    e.printStackTrace();
                    Log.d(TAG, response);
//                    Toast.makeText(mContext, "Json error Add Friend: " + e.getMessage(), Toast.LENGTH_LONG).show();
                }

            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "Request Error: " + error.getMessage());
//                Toast.makeText(mContext,
//                        error.getMessage(), Toast.LENGTH_LONG).show();

            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                // Posting parameters to login url
                Map<String, String> params = new HashMap<String, String>();
                params.put("email", email);
                params.put("destinationlat", Double.toString(destinationlat));
                params.put("destinationlng", Double.toString(destinationlng));
                params.put("mylocationlat",Double.toString(mylocationlat) );
                params.put("mylocationlng",Double.toString(mylocationlng) );
                params.put("date", date);
                params.put("descriptiondestination", descriptiondestination);
                params.put("uid", uid);

                return params;
            }

        };

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(strReq, tag_string_req);
    }
}
