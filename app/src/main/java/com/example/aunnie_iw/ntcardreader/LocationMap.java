package com.example.aunnie_iw.ntcardreader;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocomplete;
import com.google.android.gms.location.places.ui.PlaceAutocompleteFragment;
import com.google.android.gms.location.places.ui.PlaceSelectionListener;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Aunnie-IW on 13/6/2560.
 */

public class LocationMap extends AppCompatActivity implements
        GoogleMap.OnCameraMoveStartedListener,
        GoogleMap.OnCameraMoveListener,
        GoogleMap.OnCameraMoveCanceledListener,
        GoogleMap.OnCameraIdleListener,
        OnMapReadyCallback,
        View.OnClickListener,
        PlaceSelectionListener{

    private GoogleMap mMap;
    private static final int MY_PERMISSIONS_REQUEST_LOCATION = 99;
    public static final int REQUEST_ADDRESS = 3;
    private EditText ELatitude,ELongitude;
    private String address;
    private double latitude ;
    private double longitude ;

    private static final String LOG_TAG = "PlaceSelectionListener";
    private static final LatLngBounds BOUNDS_MOUNTAIN_VIEW = new LatLngBounds(
            new LatLng(37.398160, -122.180831), new LatLng(37.430610, -121.972090));
    private static final int REQUEST_SELECT_PLACE = 1000;
    private TextView locationTextView;
    private TextView attributionsTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.location_map);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(R.string.LocationMap);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        //Check if Google Play Services Available or not
        if (!CheckGooglePlayServices()) {
            Log.d("onCreate", "Finishing test case since Google Play Services are not available");
            finish();
        }
        else {
            Log.d("onCreate","Google Play Services available.");
        }
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            checkLocationPermission();
        }
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        /*------------------- Button  --------------------------------------------------------------------------------------------------*/
        Button BGo = (Button) findViewById(R.id.BGo) ;
        BGo.setOnClickListener(LocationMap.this);

        ELatitude = (EditText) findViewById(R.id.ELatitude) ;
        ELongitude = (EditText) findViewById(R.id.ELongitude) ;
 /*------------------- TextView Next--------------------------------------------------------------------------------------------------*/
        TextView Next = (TextView) findViewById(R.id.Next);
        Next.setOnClickListener(this);


        locationTextView = (TextView) findViewById(R.id.txt_location);
        attributionsTextView = (TextView) findViewById(R.id.txt_attributions);

        // Method #1
        PlaceAutocompleteFragment autocompleteFragment = (PlaceAutocompleteFragment)
                getFragmentManager().findFragmentById(R.id.place_fragment);
        autocompleteFragment.setOnPlaceSelectedListener(this);
        autocompleteFragment.setHint("Search a Location");
        autocompleteFragment.setBoundsBias(BOUNDS_MOUNTAIN_VIEW);

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Add a marker in Sydney, Australia, and move the camera.
//        LatLng sydney = new LatLng(-34, 151);
//        mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
//        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        Criteria criteria = new Criteria();


        //mMap.animateCamera(CameraUpdateFactory.zoomTo(19));
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(this,
                    android.Manifest.permission.ACCESS_FINE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED) {
               // buildGoogleApiClient();
                mMap.setMyLocationEnabled(true);
                mMap.getUiSettings().setMyLocationButtonEnabled(true);
                Location location = locationManager.getLastKnownLocation(locationManager.getBestProvider(criteria, false));
                if (location != null)
                {
                    //here where the camera animate and zoom to particular location.
                    mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(
                            new LatLng(location.getLatitude(), location.getLongitude()), 14));

                    CameraPosition cameraPosition = new CameraPosition.Builder()
                            .target(new LatLng(location.getLatitude(), location.getLongitude()))      // Sets the center of the map to location user
                            .zoom(18)                   // Sets the zoom
                            .bearing(90)                // Sets the orientation of the camera to east
                            .tilt(40)                   // Sets the tilt of the camera to 30 degrees
                            .build();                   // Creates a CameraPosition from the builder
                    mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));

                }
            }
            mMap.setOnCameraIdleListener(this);
            mMap.setOnCameraMoveStartedListener(this);
            mMap.setOnCameraMoveListener(this);
            mMap.setOnCameraMoveCanceledListener(this);
        }
        else {
           // buildGoogleApiClient();
            mMap.setMyLocationEnabled(true);
            mMap.getUiSettings().setMyLocationButtonEnabled(false);
        }
    }

    private boolean CheckGooglePlayServices() {
        GoogleApiAvailability googleAPI = GoogleApiAvailability.getInstance();
        int result = googleAPI.isGooglePlayServicesAvailable(this);
        if(result != ConnectionResult.SUCCESS) {
            if(googleAPI.isUserResolvableError(result)) {
                googleAPI.getErrorDialog(this, result,
                        0).show();
            }
            return false;
        }
        return true;
    }
    public boolean checkLocationPermission(){
        if (ContextCompat.checkSelfPermission(this,
                android.Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            // Asking user if explanation is needed
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    android.Manifest.permission.ACCESS_FINE_LOCATION)) {

                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.

                //Prompt the user once explanation has been shown
                ActivityCompat.requestPermissions(this,
                        new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSIONS_REQUEST_LOCATION);


            } else {
                // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions(this,
                        new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSIONS_REQUEST_LOCATION);
            }
            return false;
        } else {
            return true;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted. Do the
                    // contacts-related task you need to do.
                    if (ContextCompat.checkSelfPermission(this,
                            android.Manifest.permission.ACCESS_FINE_LOCATION)
                            == PackageManager.PERMISSION_GRANTED) {

//                        if (mGoogleApiClient == null) {
//                            buildGoogleApiClient();
//                        }
                        mMap.setMyLocationEnabled(true);
                    }

                } else {

                    // Permission denied, Disable the functionality that depends on this permission.
                    Toast.makeText(this, "permission denied", Toast.LENGTH_LONG).show();
                }
                //return;
            }

            // other 'case' lines to check for other permissions this app might request.
            // You can add here other case statements according to your requirement.
        }
    }
    private class GetAddress extends AsyncTask<String,Void,String> {

//        ProgressDialog dialog = new ProgressDialog(MainActivity.this);
//
//        @Override
//        protected void onPreExecute() {
//            super.onPreExecute();
//            dialog.setMessage("Please wait...");
//            dialog.setCanceledOnTouchOutside(false);
//            dialog.show();
//        }

        @Override
        protected String doInBackground(String... strings) {
            try{
                double lat = Double.parseDouble(strings[0].split(",")[0]);
                double lng = Double.parseDouble(strings[0].split(",")[1]);
                String response;
                HttpDataHandler http = new HttpDataHandler();
                String url = String.format("https://maps.googleapis.com/maps/api/geocode/json?latlng=%.4f,%.4f&sensor=false&language=th",lat,lng);
                response = http.GetHTTPData(url);
                return response;
            }
            catch (Exception ex)
            {

            }
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            try{
                JSONObject jsonObject = new JSONObject(s);

                address = ((JSONArray)jsonObject.get("results")).getJSONObject(0).get("formatted_address").toString();
//                textView.setText(address);
                Log.d(" address: ", address);

            } catch (JSONException e) {
                e.printStackTrace();
            }

//            if(dialog.isShowing())
//                dialog.dismiss();
        }
    }
    @Override
    public void onCameraMoveStarted(int reason) {

        if (reason == GoogleMap.OnCameraMoveStartedListener.REASON_GESTURE) {
            //Toast.makeText(this, "The user gestured on the map.",Toast.LENGTH_SHORT).show();
        } else if (reason == GoogleMap.OnCameraMoveStartedListener
                .REASON_API_ANIMATION) {
            //Toast.makeText(this, "The user tapped something on the map.",Toast.LENGTH_SHORT).show();
        } else if (reason == GoogleMap.OnCameraMoveStartedListener
                .REASON_DEVELOPER_ANIMATION) {
            //Toast.makeText(this, "The app moved the camera.",Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onCameraMove() {
        //Toast.makeText(this, "The camera is moving.",Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onCameraMoveCanceled() {
        //Toast.makeText(this, "Camera movement canceled.",Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onCameraIdle() {
        latitude = mMap.getCameraPosition().target.latitude;
        longitude = mMap.getCameraPosition().target.longitude;
        new GetAddress().execute(String.format("%.4f,%.4f",latitude,longitude));
        Toast.makeText(this, "The camera has stopped moving.",Toast.LENGTH_SHORT).show();
    }
    @Override
    public void onPlaceSelected(Place place) {
        Log.i(LOG_TAG, "Place Selected: " + place.getName());
//        locationTextView.setText(getString(R.string.formatted_place_data, place
//                .getName(), place.getAddress(), place.getPhoneNumber(), place
//                .getWebsiteUri(), place.getRating(), place.getId()));
        LatLng mLatLng = place.getLatLng();
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(mLatLng, 20));
        if (!TextUtils.isEmpty(place.getAttributions())){
            attributionsTextView.setText(Html.fromHtml(place.getAttributions().toString()));
        }
    }

    @Override
    public void onError(Status status) {
        Log.e(LOG_TAG, "onError: Status = " + status.toString());
        Toast.makeText(this, "Place selection failed: " + status.getStatusMessage(),
                Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {

            case R.id.BGo:{
                double lat = Double.parseDouble(ELatitude.getText().toString());
                double lng = Double.parseDouble(ELongitude.getText().toString());
                LatLng latlng = new LatLng(lat,lng);
                mMap.moveCamera(CameraUpdateFactory.newLatLng(latlng));

                break;

            }
            case R.id.Next:

                Intent intent2 = new Intent();
                intent2.putExtra("latitude",latitude);
                intent2.putExtra("longitude",longitude);

                intent2.putExtra("address",address);
                Log.e("ONSelectMAP",latitude+"");
                setResult(RESULT_OK, intent2);
                finish();
                break;
        }
    }
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}