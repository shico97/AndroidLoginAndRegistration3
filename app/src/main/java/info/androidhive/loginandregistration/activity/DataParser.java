package info.androidhive.loginandregistration.activity;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.AsyncTask;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.vision.barcode.Barcode;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.lang.reflect.Array;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;

import info.androidhive.loginandregistration.R;

import static info.androidhive.loginandregistration.app.AppController.TAG;

/**
 * Created by sherif hesham on 13-Oct-18.
 */

public class DataParser extends AsyncTask<Void,Void,Boolean> {

    Context c;
    static String jsonData;
    ListView landmarks;

    static JSONArray jsonArr;
    ProgressDialog pd;
    static ArrayList<JSONObject> spacecrafts = new ArrayList<>();

    MainActivity mainActivity = new MainActivity();
    Double CurrentLat;
    Double CurrentLong;
    ArrayList<Double> Distances = new ArrayList<>();
    ArrayList<Double> Ratings = new ArrayList<>();
    ArrayList<JSONObject> SortedLandmarks = new ArrayList<>();
    Double Lat;
    Double Long;
    String Name;
    Double Rating;
    String PhoneNumber;
    String Reviews;

    static ArrayList<JSONObject> SortedLandmarksRatings = new ArrayList<>();
    ArrayList<String> sherif = new ArrayList<>();
    ArrayList<JSONObject> spacecrafts2 = new ArrayList<>();
    //ArrayList<String>SortedLandmarks = new ArrayList<>();


    public DataParser(Context c, String jsonData, ListView lv, Double CurrentLat, Double CurrentLong) {
        this.c = c;
        this.jsonData = jsonData;
        this.landmarks = lv;
        this.CurrentLat = CurrentLat;
        this.CurrentLong = CurrentLong;
    }


    @Override
    protected void onPreExecute() {
        super.onPreExecute();

        pd = new ProgressDialog(c);
        pd.setTitle("Parse");
        pd.setMessage("Pasring..Please wait");
        pd.show();
    }

    @Override
    protected Boolean doInBackground(Void... params) {
        return this.parseData();
    }

    @Override
    protected void onPostExecute(Boolean result) {
        super.onPostExecute(result);

        pd.dismiss();


        if (result) {
            Log.i(TAG, "" + spacecrafts.get(0));
            ArrayList<String> JTS = new ArrayList<>();
            JTS = JsonToString(SortedLandmarks);

            SortingDistances();
            sortingspacecrafts();

            for (int i = 0; i < SortedLandmarks.size(); i++) {
                try {
                    String name = SortedLandmarks.get(i).getString("Name");
                    name = name + "  " + SortedLandmarks.get(i).getString("rating");
                    DecimalFormat sh = new DecimalFormat("#.##");
                    double yomna = Double.parseDouble(sh.format(Distances.get(i)));
                    name = name + "            " + yomna + " KM";
                    Log.i(TAG, name);
                    Log.i(TAG, "PARSEEEEE11111");
                    sherif.add(name);
                }
                catch(JSONException e)
                {
                    e.printStackTrace();
                }

            }

            ArrayAdapter adapter = new ArrayAdapter(c, android.R.layout.simple_list_item_1, sherif);
            landmarks.setAdapter(adapter);
            adapter.notifyDataSetChanged();

            landmarks.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                    Boolean flag = true;
                    String item = ((TextView)view).getText().toString();
                    char newchar;
                    String newitem = "";
                    int i = 0;
                    int l = 1;
                    //newitem = newitem + item.charAt(0);
                    while(flag)
                    {
                        Log.i(TAG, "onItemClick: " + newitem);
                        Log.i(TAG, "onItemClick: " + item);
                        if((item.charAt(i) != ' '))
                        {
                            Log.i(TAG, "onItemClick:  da5al" );
                                newchar = item.charAt(i);
                                newitem = newitem + newchar;
                                i++;
                        }
                        else
                            if((item.charAt(i) == ' ') && (item.charAt(i+l) == ' '))
                                flag = false;

                        else
                           if((item.charAt(i) == ' ') && (item.charAt(i+l) != ' ')) {
                               newitem = newitem + " ";
                               i++;
                           }
                    }

                    Log.i(TAG, "onItemClick: " + newitem);
                    String newitem2 = newitem+" ";
                    for(int j = 0 ; j<spacecrafts.size() ; j++)
                    {

                        try {
                            Log.i(TAG, "onItemClick1: " + newitem);
                            Log.i(TAG, "onItemClick: " +spacecrafts.get(j).getString("Name") );
                            if (newitem.equalsIgnoreCase(spacecrafts.get(j).getString("Name")) || (newitem2.equalsIgnoreCase(spacecrafts.get(j).getString("Name")))) {
                                Lat = spacecrafts.get(j).getDouble("Latitude");
                                Long = spacecrafts.get(j).getDouble("Longitude");
                                Name = spacecrafts.get(j).getString("Name");
                                Rating = spacecrafts.get(j).getDouble("rating");
                                PhoneNumber = spacecrafts.get(j).getString("Phone Number");
                                Reviews = spacecrafts.get(j).getString("reviews");
                                Log.i(TAG, "onItemClick: " + Lat);
                                Intent intent = new Intent(c, Main2Activity.class);
                                String Lat2 = Double.toString(Lat);
                                String Long2 = Double.toString(Long);
                                String Rating2 = Double.toString(Rating);
                                intent.putExtra("key", Lat2);
                                intent.putExtra("key2", Long2);
                                intent.putExtra("key3", Rating2);
                                intent.putExtra("key4", Name);
                                intent.putExtra("key5",PhoneNumber);
                                intent.putExtra("key6", Reviews);
                                c.startActivity(intent);
                                break;
                            }

                        }
                        catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }
            });
        }
    }

    public Boolean parseData() {

        try {
            Log.i(TAG, "PARSEEEEE");
            JSONArray ja = new JSONArray(jsonData);
            JSONObject jo;

            spacecrafts.clear();

            for (int i = 0; i < ja.length(); i++) {

                jo = ja.getJSONObject(i);
                String name = jo.getString("Name");
                name = name + " " + jo.getString("rating");
                Log.i(TAG, name);
                Log.i(TAG, "PARSEEEEE11111");
                spacecrafts.add(jo);
               // sherif.add(name);
                Log.i(TAG, "parseData: SHICOOOO : " + sherif);

            }

            Log.i(TAG, " DONE");
            Log.i(TAG, "" + spacecrafts.get(0) + " DONE");
            Log.i(TAG, "parseData: " + CurrentLong);

            Ratings();
            SortedRatings();
            Log.i(TAG, "parseData: " + spacecrafts.get(0).getDouble("Latitude"));
            Log.i(TAG, "parseData: " + Distances);
            Log.i(TAG, "SORTEEEEEEDDDDD: " + SortedLandmarks.size());
            return true;

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return false;
    }

    private void SortingDistances() {
        try {
            for (int i = 0; i < spacecrafts.size(); i++) {
                Double Dist = Distance(CurrentLat, CurrentLong, spacecrafts.get(i).getDouble("Latitude"), spacecrafts.get(i).getDouble("Longitude"));
                Distances.add(Dist);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Collections.sort(Distances);
        Log.i(TAG, "SortingDistancesaaaaaa: " + Distances);
    }

    public void Ratings() {
        try {
            for (int i = 0; i < spacecrafts.size(); i++) {
                Ratings.add(spacecrafts.get(i).getDouble("rating"));
            }
        } catch (JSONException e) {
            e.printStackTrace();

        }

        Collections.sort(Ratings);
        Collections.reverse(Ratings);

    }

    public void SortedRatings ()
    {
        for(int i = 0; i < spacecrafts.size() ; i++)
        {
            spacecrafts2.add(spacecrafts.get(i));
        }
        try {
            for (int i = 0; i < spacecrafts.size(); i++) {
                // Double Dist = Distance(CurrentLat, CurrentLong, spacecrafts.get(i).getDouble("Latitude"), spacecrafts.get(i).getDouble("Longitude"));
                Double Dist = Ratings.get(i);
                Log.i(TAG, "SortingRating: " + Dist);
                for (int j = 0; j < spacecrafts.size(); j++) {
                    Double Dist2 = spacecrafts2.get(j).getDouble("rating");
                    Log.i(TAG, "Ratingss: " + SortedLandmarksRatings.size());
                    if (Dist2.equals(Dist)) {
                        SortedLandmarksRatings.add(spacecrafts2.get(j));
                        spacecrafts2.remove(j);
                        break;
                    }
                }
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    private double Distance(double lat1, double lon1, double lat2, double lon2) {
        double theta = lon1 - lon2;
        double dist = Math.sin(deg2rad(lat1)) * Math.sin(deg2rad(lat2)) + Math.cos(deg2rad(lat1)) * Math.cos(deg2rad(lat2)) * Math.cos(deg2rad(theta));
        dist = Math.acos(dist);
        dist = rad2deg(dist);
        dist = dist * 60 * 1.1515;
        dist = dist * 1.609344;

        return (dist);
    }

    private double deg2rad(double deg) {
        return (deg * Math.PI / 180.0);
    }

    private double rad2deg(double rad) {
        return (rad * 180.0 / Math.PI);
    }

    private void sortingspacecrafts() {
        Log.i(TAG, "sortingspacecrafts: " + spacecrafts);
        Log.i(TAG, "sortingspacecrafts: " + SortedLandmarks);
        try {
            for (int i = 0; i < spacecrafts.size(); i++) {
                // Double Dist = Distance(CurrentLat, CurrentLong, spacecrafts.get(i).getDouble("Latitude"), spacecrafts.get(i).getDouble("Longitude"));
                Double Dist = Distances.get(i);
                Log.i(TAG, "sortingspacecrafts: " + Dist);
                for (int j = 0; j < spacecrafts.size(); j++) {
                    Double Dist2 = Distance(CurrentLat, CurrentLong, spacecrafts.get(j).getDouble("Latitude"), spacecrafts.get(j).getDouble("Longitude"));
                    Log.i(TAG, "sortingspacecrafts: " + SortedLandmarks.size());
                    if (Dist2.equals(Dist)) {
                        SortedLandmarks.add(spacecrafts.get(j));
                        Log.i(TAG, "sortingspacecraftssssssss: " + SortedLandmarks.get(0));
                        break;
                    }
                }
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    public ArrayList<String> JsonToString(ArrayList<JSONObject> x) {
        ArrayList<String> jts = new ArrayList<>();
        for (int i = 0; i < x.size(); i++) {
            try {
                jts.add(x.get(i).getString("Name"));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        return jts;
    }

}