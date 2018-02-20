package com.example.arnavsarin.myapplication;

import android.location.Location;
import android.location.LocationListener;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;

import com.firebase.client.Config;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.security.Provider;

public class MapsActivity extends AppCompatActivity implements
        GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, com.google.android.gms.location.LocationListener {


    private GoogleApiClient mGoogleApiClient;
    //private LocationRequest mLocationRequest;
    private LocationRequest mLocationRequest;

    private long UPDATE_INTERVAL = 10 * 1000;  /* 10 secs */
    private long FASTEST_INTERVAL = 2000; /* 2 sec */
    private static String startLocationLong;
    private static String startLocationLat;
    private static String endLocationLong;
    private static String endLocationLat;
    private static String FIREBASE_URL = "https://myapplication-3e0b1.firebaseio.com/";
    private static TextView listScrollable;
    private static String latandlong="";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        Firebase.setAndroidContext(this);
        if (mGoogleApiClient == null) {
            mGoogleApiClient = new GoogleApiClient.Builder(this)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();
        }
        Intent intent = getIntent();
        startLocationLong = intent.getStringExtra("startLocationLong");
        startLocationLat = intent.getStringExtra("startLocationLat");
        endLocationLong = intent.getStringExtra("endLocationLong");
        endLocationLat = intent.getStringExtra("endLocationLat");
        latandlong+= "BEGINNING LONGITUDE: " + startLocationLong + " , BEGINNING LATITUDE: " + startLocationLat + "\n";
        //Log.i("This tag is first one", startLocation);
        //Log.i("This tag is second one", endLocation);

    }

    protected void onStart() {
        mGoogleApiClient.connect();
        super.onStart();
    }

    protected void onStop() {
        mGoogleApiClient.disconnect();
        super.onStop();
    }

    public void onConnected(Bundle dataBundle) {
        // Get last known recent location.
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            ActivityCompat.requestPermissions(this,
                    new String[]{(android.Manifest.permission.ACCESS_COARSE_LOCATION),(android.Manifest.permission.ACCESS_FINE_LOCATION),
                            (android.Manifest.permission.SEND_SMS)}, 0);
            return;
        }
        Location mCurrentLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        // Note that this can be NULL if last location isn't already known.
        if (mCurrentLocation != null) {
            // Print current location if not null
            Log.d("DEBUG", "current location: " + mCurrentLocation.toString());
            LatLng latLng = new LatLng(mCurrentLocation.getLatitude(), mCurrentLocation.getLongitude());
        }
        // Begin polling for new location updates.
        startLocationUpdates();
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onConnectionSuspended(int i) {
        if (i == CAUSE_SERVICE_DISCONNECTED) {
            Toast.makeText(this, "Disconnected. Please re-connect.", Toast.LENGTH_SHORT).show();
        } else if (i == CAUSE_NETWORK_LOST) {
            Toast.makeText(this, "Network lost. Please re-connect.", Toast.LENGTH_SHORT).show();
        }
    }

    // Trigger new location updates at interval
    protected void startLocationUpdates() {
        // Create the location request
        mLocationRequest = LocationRequest.create()
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                .setInterval(UPDATE_INTERVAL)
                .setFastestInterval(FASTEST_INTERVAL);
        // Request location updates
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            ActivityCompat.requestPermissions(this,
                    new String[]{(android.Manifest.permission.ACCESS_COARSE_LOCATION),(android.Manifest.permission.ACCESS_FINE_LOCATION),
                            (android.Manifest.permission.SEND_SMS)}, 0);
            return;
        }
        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient,
                mLocationRequest, this);
    }


    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onLocationChanged(Location location) {
        String msg = "Updated Location: " +
                Double.toString(location.getLatitude()) + "," +
                Double.toString(location.getLongitude());
        Toast.makeText(this, msg, Toast.LENGTH_LONG).show();
        double endLatitutde = Double.parseDouble(endLocationLat);
        double endLongitude = Double.parseDouble(endLocationLong);
        if(location.getLatitude()!=endLatitutde || location.getLongitude()!=endLongitude) {
            saveLocationInDB(location.getLongitude(), location.getLatitude());
            Intent smsIntent = new Intent(android.content.Intent.ACTION_VIEW);
            smsIntent.setType("vnd.android-dir/mms-sms");
            smsIntent.putExtra("address", "00112233");
            smsIntent.putExtra("sms_body", "Latitude is: " + location.getLatitude() + "\n Longitude is: " + location.getLongitude());

            //   startActivity(smsIntent);
            smsIntent.setAction(Intent.ACTION_SEND);
            // sendIntent.putExtra(Intent.EXTRA_TEXT, "This is my text to send." + count);
            smsIntent.setType("text/plain");
            // Log.i("", smsIntent.getComponent().toString());

            startActivity(smsIntent);
        }else{
            latandlong="";
            listScrollable =(TextView)findViewById(R.id.listScroll);
            listScrollable.setText(latandlong);
            Intent intent = new Intent(getApplicationContext(), LocationActivity.class);
            startActivity(intent);
        }

    }

    public void saveLocationInDB(double longitude, double latitude){

        Firebase ref = new Firebase(FIREBASE_URL);

        Location currentLocation = new Location("service Provider");
        currentLocation.setLongitude(longitude);
        currentLocation.setLatitude(latitude);

        //Storing values to firebase
        ref.child("Location").push().setValue(currentLocation);
        listScrollable = (TextView)findViewById(R.id.listScroll);
        latandlong+="Longitude: " +longitude + " , " + "Latitude "+ latitude + "\n";
        listScrollable.setText(latandlong);


        /*DatabaseReference locationRef = ref.child("Location");

        DatabaseReference newPostRef = locationRef.push();
        newPostRef.setValue(currentLocation);
        */
    }


}
