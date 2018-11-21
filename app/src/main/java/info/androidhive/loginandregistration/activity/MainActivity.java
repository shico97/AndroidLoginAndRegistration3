package info.androidhive.loginandregistration.activity;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.util.Config;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

import info.androidhive.loginandregistration.R;
import info.androidhive.loginandregistration.app.AppConfig;
import info.androidhive.loginandregistration.helper.SQLiteHandler;
import info.androidhive.loginandregistration.helper.SessionManager;

import static info.androidhive.loginandregistration.activity.DataParser.SortedLandmarksRatings;
import static info.androidhive.loginandregistration.activity.DataParser.jsonData;
import static info.androidhive.loginandregistration.activity.DataParser.spacecrafts;
import static info.androidhive.loginandregistration.app.AppController.TAG;

@RequiresApi(api = Build.VERSION_CODES.KITKAT)
public class MainActivity extends Activity {

    //private TextView txtName;
    //private TextView txtEmail;
    private Button btnLogout;
    private ArrayList<Double> Rating = new ArrayList<>();
    private ListView landmarks;
    private SQLiteHandler db;
    private SessionManager session;
    private Button ShowPlaces;
    private Boolean mLocationPermissionsGranted = false;
    private Button Map;
    //private ArrayList<Object> spacecrafts = new ArrayList<>();
    private FusedLocationProviderClient mFusedLocationProviderClient;
    Double CurrentLat;
    Double CurrentLong;
    public String Data;
    private ArrayList<String> yasser = new ArrayList<>();
    //public static StringBuffer jsonData;
    private int [] RatedImages;
    final static String urlAddress = "http://192.168.1.7/android_login_api/getplaces.php";
    Downloader downloader;
    DataParser dataParser;




   // int[] IMAGES = {R.drawable.imax, R.drawable.jumpedia, R.drawable.point90, R.drawable.sakia_sawi_culture_wheel_85, R.drawable.ski_egypt,
    //R.drawable.trapped, R.drawable.airzone, R.drawable.breakout, R.drawable.cairo_opera_house, R.drawable.champions, R.drawable.gravity};



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getDeviceLocation();

        landmarks = (ListView) findViewById(R.id.Places);
        btnLogout = (Button) findViewById(R.id.btnLogout);
        Map = (Button) findViewById(R.id.button2);
        ShowPlaces = (Button) findViewById(R.id.Show);

        // SqLite database handler
        db = new SQLiteHandler(getApplicationContext());

        // session manager
        session = new SessionManager(getApplicationContext());
        if (!session.isLoggedIn()) {
            logoutUser();
        }
        btnLogout.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                logoutUser();
            }
        });

        Map.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Map();
            }
        });






        /*ShowPlaces.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                        JSONArray jsonArr = null;
                        try {
                            jsonArr = new JSONArray(jsonData);
                        } catch (JSONException e1) {
                            e1.printStackTrace();
                        }
                        JSONObject jo;
                        JSONArray sortedJsonArray = new JSONArray();

                        List<JSONObject> jsonValues = new ArrayList<JSONObject>();
                        for (int i = 0; i < jsonArr.length(); i++) {
                            try {
                                jsonValues.add(jsonArr.getJSONObject(i));
                            } catch (JSONException e1) {
                                e1.printStackTrace();
                            }
                        }
                        Collections.sort(jsonValues, new Comparator<JSONObject>() {
                            //You can change "Name" with "ID" if you want to sort by ID
                            private static final String KEY_NAME = "rating";

                            @Override
                            public int compare(JSONObject a, JSONObject b) {
                                String valA = new String();
                                String valB = new String();

                                try {
                                    valA = (String) a.get(KEY_NAME);
                                    valB = (String) b.get(KEY_NAME);
                                } catch (JSONException e) {
                                    //do something
                                }

                                return valB.compareTo(valA);
                                //if you want to change the sort order, simply use the following:
                                //return -valA.compareTo(valB);
                            }
                        });

                        for (int i = 0; i < jsonArr.length(); i++) {
                            sortedJsonArray.put(jsonValues.get(i));
                        }
                        spacecrafts.clear();
                        for (int i = 0; i < sortedJsonArray.length(); i++) {

                            try {
                                jo = sortedJsonArray.getJSONObject(i);


                                String name = jo.getString("Name") + " ";
                                name += jo.getString("rating");
                                spacecrafts.add(name);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                        }



                        ArrayAdapter adapter=new ArrayAdapter(MainActivity.this,android.R.layout.simple_list_item_1,spacecrafts);
                        landmarks.setAdapter(adapter);

                    }
                });*/

        ShowPlaces.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i(TAG, "onClick:  " + SortedLandmarksRatings.size());

                    for (int i = 0; i < SortedLandmarksRatings.size(); i++) {
                        try {
                            String s = SortedLandmarksRatings.get(i).getString("Name");
                            s = s + "  " + SortedLandmarksRatings.get(i).getString("rating");
                            yasser.add(s);
                        }
                        catch (JSONException e)
                        {
                            e.printStackTrace();
                        }
                    }

                Log.i(TAG, "onClick:  " + yasser.size());
                    ArrayAdapter adapter = new ArrayAdapter(MainActivity.this, android.R.layout.simple_list_item_1, yasser);
                    landmarks.setAdapter(adapter);
                    adapter.notifyDataSetChanged();


            }


        });
    }







    /**
     * Logging out the user. Will set isLoggedIn flag to false in shared
     * preferences Clears the user data from sqlite users table
     */
    private void logoutUser() {
        session.setLogin(false);

        db.deleteUsers();

        // Launching the login activity
        Intent intent = new Intent(MainActivity.this, LoginActivity.class);
        startActivity(intent);
        finish();
    }


    public Object connect(String urlAddress) {
        try {
            URL url = new URL(urlAddress);
            HttpURLConnection con = (HttpURLConnection) url.openConnection();

            //SET CON PROPERTIES
            con.setRequestMethod("GET");
            con.setConnectTimeout(15000);
            con.setReadTimeout(15000);
            con.setDoInput(true);

            return con;

        } catch (MalformedURLException e) {
            e.printStackTrace();
            return "Error " + e.getMessage();

        } catch (IOException e) {
            e.printStackTrace();
            return "Error " + e.getMessage();

        }
    }

    private void Map() {
        Intent intent = new Intent(MainActivity.this, MapsActivity.class);
        startActivity(intent);
    }


    private void getDeviceLocation() {
        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        try {

            if (ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
            mFusedLocationProviderClient.getLastLocation().addOnSuccessListener(MainActivity.this, new OnSuccessListener<Location>() {
                @Override
                public void onSuccess(Location location) {
                    CurrentLat = location.getLatitude();
                    CurrentLong = location.getLongitude();
                    Log.i(TAG, "HEEDADSD " + CurrentLong);


                    new Downloader(MainActivity.this, urlAddress, landmarks, CurrentLat, CurrentLong).execute();
                }
            });

        } catch (SecurityException e) {
        }
    }


   /* class CustomeAdapter extends BaseAdapter
    {

        @Override
        public int getCount() {
            return SortedLandmarksRatings.size();
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            convertView = getLayoutInflater().inflate(R.layout.custome_layout,null);

            ImageView imageView = (ImageView) convertView.findViewById(R.id.imageView);
            TextView textView_name = (TextView) convertView.findViewById(R.id.textView_name);
            TextView textView_description= (TextView) convertView.findViewById(R.id.textView_description);

            return null;
        }
    }*/
}
