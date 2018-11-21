package info.androidhive.loginandregistration.activity;

import android.content.Intent;
import android.location.Location;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;

import android.Manifest;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.widget.ListView;
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

import info.androidhive.loginandregistration.R;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private FusedLocationProviderClient mFusedLocationProviderClient;
    private Boolean mLocationPermissionsGranted = false;
    private static final String Fine_Location = Manifest.permission.ACCESS_FINE_LOCATION;
    private static final String Course_Location = Manifest.permission.ACCESS_COARSE_LOCATION;
    private static final float Default_Zoom = 15f;
    private GoogleMap mMap;
    Double Latitude;
    Double Longitude;
    String Lat;
    String Long;
    String name;
    DataParser dataParser;
    ListView lv;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        Intent intent = getIntent();
        Lat = intent.getStringExtra("key6");
        Long = intent.getStringExtra ("key7");
        name = intent.getStringExtra("key8");
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        getLocationPermission();

//        Latitude = Double.parseDouble(Lat);//Longitude = Double.parseDouble(Long);
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
    public void onMapReady(GoogleMap googleMap) {
        Toast.makeText(this, "Map is Ready", Toast.LENGTH_SHORT).show();
        mMap = googleMap;

        if (Lat != null && Long!= null) {
            Latitude = Double.parseDouble(Lat);
            Longitude = Double.parseDouble(Long);
            // Add a marker
            LatLng sydney = new LatLng(Latitude, Longitude);
            mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in " + name));
            mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
            moveCamera(new LatLng(Latitude,Longitude),Default_Zoom);
        }

        String[] permissions = {Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION};

        if (mLocationPermissionsGranted) {
            getDeviceLocation();

            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
            mMap.setMyLocationEnabled(true);
        }
    }

    private void getDeviceLocation()
    {
        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        try{
            if(mLocationPermissionsGranted)
            {
                final Task location = mFusedLocationProviderClient.getLastLocation();
                location.addOnCompleteListener(new OnCompleteListener() {
                    @Override
                    public void onComplete(@NonNull Task task) {
                        if(task.isSuccessful())
                        {
                            Location currentLocation = (Location) task.getResult();
                            if (Lat == null && Long== null) {
                                moveCamera(new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude()), Default_Zoom);
                            }

                        }
                    }
                });

            }
        }
        catch(SecurityException e)
        {
        }
    }

    private void moveCamera(LatLng latLng, float zoom)
    {
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng,zoom));
    }
    private void initmap()
    {
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(MapsActivity.this);
    }

    private void getLocationPermission()
    {
        String[] permissions = {Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION};

        if(ContextCompat.checkSelfPermission(this.getApplicationContext(),Fine_Location) == PackageManager.PERMISSION_GRANTED)
        {
            mLocationPermissionsGranted = true;
        }
        else
        {
            ActivityCompat.requestPermissions(this,permissions,1234);
        }
        if(ContextCompat.checkSelfPermission(this.getApplicationContext(),Course_Location) == PackageManager.PERMISSION_GRANTED)
        {
            mLocationPermissionsGranted = true;
        }
        else
        {
            ActivityCompat.requestPermissions(this,permissions,1234);
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        mLocationPermissionsGranted = false;

        switch (requestCode)
        {
            case 1234 : {
                if(grantResults.length>0)
                {
                    for(int i = 0 ;i<grantResults.length ; i++)
                    {
                        if(grantResults[i] != PackageManager.PERMISSION_GRANTED)
                        {
                            mLocationPermissionsGranted= false;
                            return;
                        }
                    }
                    mLocationPermissionsGranted= true;
                    //initizalize the map
                    initmap();
                }
            }
        }
    }

}
