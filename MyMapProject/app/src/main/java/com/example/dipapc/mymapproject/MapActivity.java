package com.example.dipapc.mymapproject;

import android.*;
import android.Manifest;
import android.app.Service;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class MapActivity extends AppCompatActivity implements OnMapReadyCallback {


    private static final String TAG = "MapActivity";
    private static final String FINE_L = Manifest.permission.ACCESS_FINE_LOCATION;
    private static final String COARSE_L = Manifest.permission.ACCESS_COARSE_LOCATION;
    private static final int REQUEST_PERMISSION_CODE = 1234;
    private static final float DEFAULT_ZOOM = 500f;
    //vars
    private Boolean mLocationPermissionGranted = false;
    private GoogleMap mMap;
    private FusedLocationProviderClient mFusedLocationProviderClient;
    //private double lat;
    // private double lon;

    //widgets
    EditText et1;

    //Address Array
    //String EventAddresses = "800 W Campbell Rd, Richardson, TX 75080";

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        Toast.makeText(this, "Map is ready", Toast.LENGTH_SHORT).show();
        Log.d(TAG, "Our Map is ready");
        if (mLocationPermissionGranted) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED &&
                    ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

                return;
            }

            getDeviceLocation();
            mMap.setMyLocationEnabled(true);
            mMap.getUiSettings().setMyLocationButtonEnabled(false);
            //mMap.getUiSettings().setMapToolbarEnabled(true);
            init();




        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);
        et1 = (EditText)findViewById(R.id.input_search);
        getLocationPermission();


        //getDeviceLocation();
        //getAddress(lat,lon);
    }
    public void init(){
        Log.d("TAG","Initializing..");
        et1.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int keyCode, KeyEvent event) {
                // If the event is a key-down event on the "enter" button
                if ((event.getAction() == KeyEvent.ACTION_DOWN) &&
                        (keyCode == KeyEvent.KEYCODE_ENTER)) {
                    // Perform action on key press
                    geoLocate();

                    return true;
                }
                return false;
            }
        });

//        et1.setOnEditorActionListener(new TextView.OnEditorActionListener() {
//            @Override
//            public boolean onEditorAction(TextView textView, int actionId, KeyEvent keyEvent) {
//                if(actionId == EditorInfo.IME_ACTION_SEARCH
//                        || actionId == EditorInfo.IME_ACTION_DONE
//                        || keyEvent.getAction() == KeyEvent.ACTION_DOWN
//                        || keyEvent.getAction() == KeyEvent.KEYCODE_ENTER){
//
//                    //execute our method for searching
//                    geoLocate();
//                }
//                return false;
//            }
//        });
    }
    private void geoLocate(){
        Log.d("TAG","Geolocationg..");
        String searchAdd = et1.getText().toString();
        Geocoder geocoder = new Geocoder(MapActivity.this);
        List <Address>  addresses = new ArrayList<>();
        try {
            // May throw an IOException
            addresses = geocoder.getFromLocationName(searchAdd,1);

        } catch (IOException ex) {

            ex.printStackTrace();
            Log.e(TAG, "geoLocate: IOException: " + ex.getMessage() );
        }
        if (addresses.size() > 0) {
            Address address = addresses.get(0);
            // Toast.makeText(MapActivity.this,"lat"+address.getCountryName(),Toast.LENGTH_SHORT).show();
            Log.d(TAG, "geoLocate: found a location: " + address.toString());
            moveCamera(new LatLng(address.getLatitude(),address.getLongitude()),DEFAULT_ZOOM);}


    }

    //get device location
    private void getDeviceLocation() {
        Log.d(TAG, "getting device location");
        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        try {
            if (mLocationPermissionGranted) {
                Task location = mFusedLocationProviderClient.getLastLocation();
                location.addOnCompleteListener(new OnCompleteListener() {
                    @Override
                    public void onComplete(@NonNull Task task) {
                        if (task.isSuccessful()) {
                            Log.d(TAG, "onComplete:Found location");
                            Location currentLocation = (Location) task.getResult();
                            double lat = currentLocation.getLatitude();
                            double lon = currentLocation.getLongitude();
                            LatLng ll = new LatLng(lat, lon);
                            moveCamera(ll, DEFAULT_ZOOM);
                            //LatLng lt = getLocationFromAddress(MapActivity.this, EventAddresses);
                            //Toast.makeText(MapActivity.this, "lat:" + lt.latitude + "lon:" + lt.longitude, Toast.LENGTH_SHORT).show();
                            // mMap.addMarker(new MarkerOptions().position(lt).title("My location"));
                            //mMap.moveCamera(CameraUpdateFactory.newLatLng(lt));

                            mMap.setMyLocationEnabled(true);



                            //mMap.moveCamera(CameraUpdateFactory.newLatLng(ll));



//                            mMap.setMyLocationEnabled(true);
//                            mMap.addMarker(new MarkerOptions().position(ll).title("My location"));
//                            mMap.moveCamera(CameraUpdateFactory.newLatLng(ll));
//                            Toast.makeText(MapActivity.this,"lat:"+ll.latitude+"lon:"+ll.longitude,Toast.LENGTH_SHORT).show();
//                            getAddress(ll.latitude,ll.longitude);


                            //move camera here
                        } else {
                            Log.d(TAG, "onComplete:Can not find location");
                            Toast.makeText(MapActivity.this, "Unable to find location", Toast.LENGTH_SHORT).show();
                        }

                    }
                });
            }

        } catch (SecurityException e) {
            Log.e(TAG, "Get device location: Security Exception: " + e.getMessage());
        }
    }

    public void moveCamera(LatLng latLng, float zoom) {
        Log.d("TAG", "Moving the camera to lat:" + latLng.longitude + "lon:" + latLng.longitude);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, zoom));
        MarkerOptions mp = new MarkerOptions().position(latLng).title("searched location");
        mMap.addMarker(mp);
    }

    public void intMap() {
        Log.d(TAG, "Initialized map");
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(MapActivity.this);
        //second way of initializing map
       /* mapFragment.getMapAsync(new OnMapReadyCallback() {
            @Override
            //onMapReady(GoogleMap googleMap):will prepare our map
            public void onMapReady(GoogleMap googleMap) {

                mMap = googleMap;

            }
        });*/

    }

    // we need to explicitly get the permissions
    private void getLocationPermission() {
        //array of permissions
        String[] permissions = {Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION};

        if (ContextCompat.checkSelfPermission(this.getApplicationContext(), FINE_L) == PackageManager.PERMISSION_GRANTED) {
            if (ContextCompat.checkSelfPermission(this.getApplicationContext(), COARSE_L) == PackageManager.PERMISSION_GRANTED) {
                mLocationPermissionGranted = true;
                intMap();
            } else {
                ActivityCompat.requestPermissions(this, permissions, REQUEST_PERMISSION_CODE);

            }
        } else {
            ActivityCompat.requestPermissions(this, permissions, REQUEST_PERMISSION_CODE);
        }
        Log.d(TAG, "Got location permission");


    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        mLocationPermissionGranted = false;
        switch (requestCode) {
            case REQUEST_PERMISSION_CODE: {
                if (grantResults.length > 0) {
                    //check if all the permissions are granted
                    for (int i = 0; i < grantResults.length; i++) {
                        if (grantResults[i] != PackageManager.PERMISSION_GRANTED) {
                            Log.d(TAG, "Inside location permission result");
                            return;
                        }
                    }
                    mLocationPermissionGranted = true;
                    intMap();
                }

            }
        }
    }

    public void getAddress(double lat, double lng) {
        Geocoder geocoder = new Geocoder(MapActivity.this, Locale.getDefault());
        try {
            List<Address> addresses = geocoder.getFromLocation(lat, lng, 1);
            Address obj = addresses.get(0);
            String add = obj.getAddressLine(0);
            add = add + "\n" + obj.getCountryName();
            add = add + "," + obj.getCountryCode();
            add = add + "," + obj.getAdminArea();
            add = add + "," + obj.getPostalCode();
            //add = add + "," + obj.getSubAdminArea();
            add = add + "," + obj.getLocality();
            //add = add + "," + obj.getSubThoroughfare();

            Log.v("IGA", "Address" + add);
            Toast.makeText(this, "Address=>" + add, Toast.LENGTH_SHORT).show();

            // TennisAppActivity.showDialog(add);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }


    public LatLng getLocationFromAddress(Context context, String strAddress) {

        Geocoder coder = new Geocoder(context);
        List<Address> address;
        LatLng p1 = null;

        try {
            // May throw an IOException
            address = coder.getFromLocationName(strAddress, 5);
            if (address == null) {
                return null;
            }

            Address location = address.get(0);
            p1 = new LatLng(location.getLatitude(), location.getLongitude());

        } catch (IOException ex) {

            ex.printStackTrace();
        }

        return p1;
    }
}

