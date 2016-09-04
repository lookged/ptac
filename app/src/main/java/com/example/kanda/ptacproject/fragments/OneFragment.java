package com.example.kanda.ptacproject.fragments;

import android.content.Context;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.kanda.ptacproject.R;
import com.example.kanda.ptacproject.activity.MainActivity;
import com.example.kanda.ptacproject.model.Marker;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;


public class OneFragment extends Fragment implements OnMapReadyCallback {
    MapView mMapView;
    LocationManager locationManager;
    MarkerOptions myLocation;
    private GoogleMap mGoogleMap;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_one, container, false);
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
        googleMap.setMyLocationEnabled(true);
        googleMap.getUiSettings().setMyLocationButtonEnabled(true);
        Criteria criteria = new Criteria();

        Location location = locationManager.getLastKnownLocation(locationManager.getBestProvider(criteria, false));
        LatLng latLngLocation = new LatLng(location.getLatitude(), location.getLongitude());

        myLocation = new MarkerOptions().position(latLngLocation).title("Marker Title").snippet("Marker Description").icon(icon);
        googleMap.addMarker(myLocation);


        CameraPosition cameraPosition = new CameraPosition.Builder().target(latLngLocation).zoom(15).build();
        googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));

        LocationListener lis = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                if (((MainActivity) getActivity()).markerList != null) {
                    BitmapDescriptor icon = BitmapDescriptorFactory.fromResource(R.mipmap.ic_launcher);
                    mGoogleMap.clear();
                    LatLng latLngLocation = new LatLng(location.getLatitude(), location.getLongitude());
                    for (Marker m : ((MainActivity) getActivity()).markerList) {
                        if (isShowMarker(location, m)) {
                            myLocation = new MarkerOptions().position(
                                    new LatLng(m.getAccLat(), m.getAccLong())
                            ).title(m.getAccTitle()).snippet(m.getAccDescription()).icon(icon);
                            mGoogleMap.addMarker(myLocation);
                        }
                    }
                    CameraPosition cameraPosition = new CameraPosition.Builder().target(latLngLocation).zoom(15).build();
                    mGoogleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
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

        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, lis);
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

}