package com.example.kanda.ptacproject.fragments;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
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
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
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
import com.example.kanda.ptacproject.activity.DestinationMapActivity;
import com.example.kanda.ptacproject.activity.MainActivity;
import com.example.kanda.ptacproject.app.AppConfig;
import com.example.kanda.ptacproject.app.AppController;
import com.example.kanda.ptacproject.helper.SQLiteHandler;
import com.example.kanda.ptacproject.helper.SessionManager;
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
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.example.kanda.ptacproject.fragments.FiveFragment.session;


public class OneFragment extends Fragment implements DirectionFinderListener,OnMapReadyCallback, GoogleMap.OnMapClickListener, GoogleMap.OnMapLongClickListener {

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

    public static String getDate(long milliSeconds, String dateFormat) {
        // Create a DateFormatter object for displaying date in specified format.
        SimpleDateFormat formatter = new SimpleDateFormat(dateFormat);

        // Create a calendar object that will convert the date and time value in milliseconds to date.
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(milliSeconds);
        return formatter.format(calendar.getTime());
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_one, container, false);
        inflator = inflater.inflate(R.layout.dialog_marker, null);

         Edorigin = (EditText) rootView.findViewById(R.id.etOrigin) ;
         Btnlocation = (Button) rootView.findViewById(R.id.btnlocation) ;


        Btnlocation.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                String origin = Edorigin.getText().toString().trim();
                String destination = "บางแค";
                if (origin.isEmpty()) {
                    Toast.makeText(getActivity(), "Please enter origin address!", Toast.LENGTH_SHORT).show();
                    return;
                }
                try {

                    sendRequest(origin);
                }catch (Exception e){
                    Log.d(TAG, " "+e);
                }

            }
        });


        locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
        mMapView = (MapView) rootView.findViewById(R.id.map);
        mMapView.onCreate(savedInstanceState);
        mMapView.onResume(); // needed to get the map to display immediately
        lSpinner.clear();
        dSpinner.clear();



        Spinner spin = (Spinner) (Spinner) rootView.findViewById(R.id.spinner_length);
        ArrayAdapter<String> aa = new ArrayAdapter<String>(getActivity(),android.R.layout.simple_spinner_item, lengthdate);
        aa.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spin.setAdapter(aa);
       spin.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
           public void onItemSelected(AdapterView<?> parent, View view,
                                      int position, long id) {
               switch (position) {
                   case 0:
                       maxDate = -90;
                       break;
                   case 1:
                       maxDate = -180;
                       break;
                   case 2:
                       maxDate = -365;
                       break;

               }

           }

           @Override
           public void onNothingSelected(AdapterView<?> parent) {

           }
       });


        Spinner spin2 = (Spinner) rootView.findViewById(R.id.spinner_maxdate);
        ArrayAdapter<String> bb = new ArrayAdapter<String>(getActivity(),android.R.layout.simple_spinner_item, lengthdistance);
        bb.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spin2.setAdapter(bb);
        spin2.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> parent, View view,
                                       int position, long id) {
                switch (position) {
                    case 0:
                        lengthMap = 1000;
                        break;
                    case 1:
                        lengthMap = 5000;
                        break;
                    case 2:
                        lengthMap = 10000;
                        break;

                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

//
//        lengthSpinner = (Spinner) rootView.findViewById(R.id.spinner_length);
//        createLengthData();
//        ArrayAdapter<String> lengthspinner = new ArrayAdapter<String>(getActivity(),
//                android.R.layout.simple_spinner_dropdown_item, lSpinner);
//        lengthSpinner.setAdapter(lengthspinner);
//
//        dateSpinner = (Spinner) rootView.findViewById(R.id.spinner_maxdate);
//        createLengthData();
//        ArrayAdapter<String> datespinner = new ArrayAdapter<String>(getActivity(),
//                android.R.layout.simple_spinner_dropdown_item, dSpinner);
//        lengthSpinner.setAdapter(datespinner);



        try {
            MapsInitializer.initialize(getActivity().getApplicationContext());
        } catch (Exception e) {
            e.printStackTrace();
        }
        final OneFragment myObj = this;
        mMapView.getMapAsync(this);


        return rootView;
    }

    private void createLengthData() {

        lSpinner.add("ระยะ 1 กม.");
        lSpinner.add("ระยะ 5 กม.");
        lSpinner.add("ระยะ 10 กม.");


    }



    @Override
    public void onMapReady(GoogleMap googleMap) {
        BitmapDescriptor icon = BitmapDescriptorFactory.fromResource(R.mipmap.ic_launcher);
        mGoogleMap = googleMap;

        mGoogleMap.setOnMapClickListener(this);
        mGoogleMap.setOnMapLongClickListener(this);



        googleMap.setMyLocationEnabled(true);
        googleMap.setPadding(50,80,0,60);
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
                                try {
                                    long yourmilliseconds = System.currentTimeMillis();

                                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                                    Date resultdate = new Date(yourmilliseconds);
                                    String todate= sdf.format(resultdate);
                                    String dateInString = todate;  // Start date


                                    Calendar c = Calendar.getInstance(); // Get Calendar Instance
                                    c.setTime(sdf.parse(dateInString));

                                    c.add(Calendar.DATE, maxDate);  // add 45 days
                                    sdf = new SimpleDateFormat("yyyy-MM-dd");

                                    Date resultdate1 = new Date(c.getTimeInMillis());   // Get new time
                                    dateInString = sdf.format(resultdate1);

                                    SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
                                    formatter.setLenient(false);
                                    Date curDate = new Date();


                                    String oldTime = m.getDate();
                                    Date oldDate = formatter.parse(oldTime);
                                    long oldMillis = oldDate.getTime();


                                    String maxTime = dateInString;
                                    Date maxDate = formatter.parse(maxTime);
                                    long maxMillis = maxDate.getTime();
//                                    Toast.makeText(getActivity(), "rateMarker" + maxMillis, Toast.LENGTH_LONG).show();

                                    if(oldMillis>=maxMillis){


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
                                            Button reportmark1 = (Button) v.findViewById(R.id.mark_report);

                                            // Getting reference to the TextView to set longitude
                                            TextView tvLng = (TextView) v.findViewById(R.id.tv_description);

                                            // Setting the latitude
                                            tvLat.setText("Title:" + marker.getTitle());

                                            // Setting the longitude
                                            tvLng.setText("Description :" + marker.getSnippet());
                                            reportmark1.setOnClickListener(new View.OnClickListener() {

                                                public void onClick(View view) {
                                                    Toast.makeText(getActivity(),
                                                            "Report complete!!", Toast.LENGTH_LONG).show();


                                                }

                                            });
                                            return v;
                                        }


                                    });

                                    mGoogleMap.addMarker(myLocation);
                                    }
                                }catch (Exception e){
//                                    Toast.makeText(getActivity(), "rateMarker" , Toast.LENGTH_SHORT).show();
                                }


                            }
                        }


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

    private void sendRequest(String originn) {
//        String origin = etOrigin.getText().toString();
//        String destination = etDestination.getText().toString();
        String origin = originn.toString().trim();
        String destination = ""+13.669110+","+100.512731;
        if (origin.isEmpty()) {
            Toast.makeText(getActivity(), "Please enter origin address!", Toast.LENGTH_SHORT).show();
            return;
        }
        if (destination.isEmpty()) {
            Toast.makeText(getActivity(), "Please enter destination address!", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            new DirectionFinder(this, origin, destination).execute();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }
    @Override
    public void onDirectionFinderStart() {
        progressDialog = ProgressDialog.show(getActivity(), "Please wait.",
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
            mGoogleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(route.startLocation, 16));


            originMarkers.add(mGoogleMap.addMarker(new MarkerOptions()
                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.start_blue))
                    .title(route.startAddress)
                    .position(route.startLocation)));
//            destinationMarkers.add(mMap.addMarker(new MarkerOptions()
//                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.end_green))
//                    .title(route.endAddress)
//                    .position(route.endLocation)));
//
//
//            PolylineOptions polylineOptions = new PolylineOptions().
//                    geodesic(true).
//                    color(Color.BLUE).
//                    width(3);
//
//            for (int i = 0; i < route.points.size(); i++)
//                polylineOptions.add(route.points.get(i));
//
//            polylinePaths.add(mMap.addPolyline(polylineOptions));
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
//        lengthSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
//            @Override
//            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
//                switch (position) {
//                    case 0:
//                        lengthMap = 1000;
//                        break;
//                    case 1:
//                        lengthMap = 5000;
//                        break;
//                    case 2:
//                        lengthMap = 10000;
//                        break;
//
//                }
////                Toast.makeText(getActivity(),
////                        "Select : " + position,
////                        Toast.LENGTH_SHORT).show();
//            }
//
//            @Override
//            public void onNothingSelected(AdapterView<?> parent) {
//
//            }
//        });
        return results[0] < lengthMap;
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

        titleMarker = (AutoCompleteTextView) dialogView.findViewById(R.id.title_Marker);
        descriptionMarker = (AutoCompleteTextView) dialogView.findViewById(R.id.description_Marker);

        long endOfMonth = System.currentTimeMillis();
        calendarMarker = (CalendarView) dialogView.findViewById(R.id.Calendar_Marker);
        RadioGroup rateMarker = (RadioGroup) dialogView.findViewById(R.id.radioGroup_marker);
//        rateMarker = ((RadioGroup) dialogView.findViewById(R.id.radioGroup_marker)).getCheckedRadioButtonId();
//                Toast.makeText(getActivity(), "rateMarker" + rateMarker, Toast.LENGTH_SHORT).show();

        final TextView textCategory = (TextView) dialogView.findViewById(R.id.textcategory);

        calendarMarker.setMaxDate(System.currentTimeMillis());

        rateMarker.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged (RadioGroup group,int checkedId){

                Log.d("chk", "id" + checkedId);

                if (checkedId == R.id.radio_lvl1) {
                    textCategory.setText("ชิงทรัพย์");
                } else if (checkedId == R.id.radio_lvl2) {
                    textCategory.setText("ทำร้านร่างกาย");
                }else if (checkedId == R.id.radio_lvl3) {
                    textCategory.setText("ชิงทรัพย์และทำร้านร่างกาย");
                }else if (checkedId == R.id.radio_lvl4) {
                    textCategory.setText("ข่มขืนและกระทำอนาจาร");
                }else if (checkedId == R.id.radio_lvl5) {
                    textCategory.setText("ทำร้ายร่างกายจนเสียชีวิต");
                }
            }
        });


//        switch (rateMarker) {
//
//            case R.id.radio_lvl1:
//                textCategory.setText("ชิงทรัพย์");
//                break;
//            case R.id.radio_lvl2:
//                textCategory.setText("ทำร้านร่างกาย");
//                break;
//            case R.id.radio_lvl3:
//                textCategory.setText("ชิงทรัพย์และทำร้านร่างกาย");
//                break;
//            case R.id.radio_lvl4:
//                textCategory.setText("ข่มขืนและกระทำอนาจาร");
//                break;
//            case R.id.radio_lvl5:
//                textCategory.setText("ทำร้ายร่างกายจนเสียชีวิต");
//                break;
//        }


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
                try {


                int rateMarker = ((RadioGroup) dialogView.findViewById(R.id.radioGroup_marker)).getCheckedRadioButtonId();
//                Toast.makeText(getActivity(), "rateMarker" + rateMarker, Toast.LENGTH_SHORT).show();
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

                if(Datemarker==null) {
                    long yourmilliseconds = System.currentTimeMillis();
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");
                    Date resultdate = new Date(yourmilliseconds);
                    Datemarker=sdf.format(resultdate);
                }
//                Toast.makeText(getActivity(), ""+sdf.format(resultdate) , Toast.LENGTH_SHORT).show();

                if (titlemarker.length() > 0 && description.length() > 0) {
                   addMarker(accid, titlemarker, description, latmarker, lngmarker, Datemarker, ratemarkers, usermarker);


                        Toast.makeText(getActivity(), ratemarker , Toast.LENGTH_SHORT).show();
                } else {

                    Toast.makeText(getActivity(), "Please complete all information.", Toast.LENGTH_SHORT).show();
                }
                }catch (Exception e){
                    Log.d(TAG, ""+e);
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
                        Toast.makeText(getActivity(),
                                "error_msg" + errorMsg, Toast.LENGTH_LONG).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }


                Toast.makeText(getActivity(), "Marke Destination completed", Toast.LENGTH_SHORT).show();
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "addDestination Error: " + error.getMessage());
                Toast.makeText(getActivity(),
                        error.getMessage(), Toast.LENGTH_LONG).show();

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
                    Toast.makeText(getActivity(), "Json error: " + e.getMessage(), Toast.LENGTH_LONG).show();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "Load Marker List Error: " + error.getMessage());
                Toast.makeText(getActivity(), error.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(strReq, tag_string_req);
    }

}