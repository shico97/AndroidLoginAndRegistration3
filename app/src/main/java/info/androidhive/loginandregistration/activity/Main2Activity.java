package info.androidhive.loginandregistration.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.io.File;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import info.androidhive.loginandregistration.R;
import info.androidhive.loginandregistration.app.AppConfig;
import info.androidhive.loginandregistration.app.AppController;

import static info.androidhive.loginandregistration.app.AppController.TAG;

public class Main2Activity extends AppCompatActivity {

    String Lat;
    String Long;
    String Name;
    String Rating;
    String PhoneNumber;
    TextView names;
    TextView ratings;
    TextView phonenumbers;
    Button location;
    Button Call;
    Button UserRating;
    RatingBar simpleRatingBar;
    EditText review;
    Button SubmitReview;
    String Reviews;
    Button ShowReviews;
    Context c;
    String Currentrating;
    ImageView landmark;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        simpleRatingBar = (RatingBar) findViewById(R.id.ratingBar); // initiate a rating bar
        Float ratingNumber = simpleRatingBar.getRating();
        Log.i(TAG, "Ratingssssss: " + ratingNumber);

        names = (TextView) findViewById(R.id.Names);
        ratings = (TextView) findViewById(R.id.textView2);
        phonenumbers = (TextView) findViewById(R.id.textView3);
        location = (Button) findViewById(R.id.button3);
        Call = (Button) findViewById(R.id.button4);
        UserRating = (Button) findViewById(R.id.button5);
        review = (EditText) findViewById(R.id.editText);
        SubmitReview = (Button) findViewById(R.id.button6);
        ShowReviews = (Button) findViewById(R.id.button7);
       // landmark = (ImageView) findViewById(R.id.imageView);



        Intent intent = getIntent();
        Lat = intent.getStringExtra("key");
        Long = intent.getStringExtra("key2");
        Name = intent.getStringExtra("key4");
        Rating = intent.getStringExtra("key3");
        PhoneNumber = intent.getStringExtra("key5");
        Reviews = intent.getStringExtra("key6");

        if(Reviews.equalsIgnoreCase("null"))
        {
            Reviews = "";
        }

        names.setText(Name);
        ratings.setText("Rating: " + Rating);
        phonenumbers.setText("Phone Number: " + PhoneNumber);


        location.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                String uri = String.format(Locale.ENGLISH, "http://maps.google.com/maps?daddr=" + Lat + "," + Long + "(" + Name + ")");
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
                intent.setPackage("com.google.android.apps.maps");
                /*intent.putExtra("key6", Lat);
                intent.putExtra("key7", Long);
                intent.putExtra("key8", Name);*/
                startActivity(intent);

            }
        });

        Call.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_DIAL);
                intent.setData(Uri.parse("tel:" + PhoneNumber));
                startActivity(intent);
            }
        });

        UserRating.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String totalStars = "Total Stars:: " + simpleRatingBar.getNumStars();
                Currentrating = "" + simpleRatingBar.getRating();
                Toast.makeText(getApplicationContext(), totalStars + "\n" + Currentrating, Toast.LENGTH_LONG).show();

                Double r = Double.parseDouble(Currentrating);
                Double prevr = Double.parseDouble(Rating);

                Double UpdatedRating = (r+prevr)/2;

                AddRating(UpdatedRating,Name);

            }
        });

        ShowReviews.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(Main2Activity.this, Reviews.class);
                intent.putExtra("key", Reviews);
                startActivity(intent);

            }
        });

        SubmitReview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String R = review.getText().toString();
                Reviews = Reviews + "," + R;
                Toast.makeText(getApplicationContext(), R, Toast.LENGTH_LONG).show();

                AddReview(Reviews, Name);

            }
        });
    }

    private void AddReview(final String review, final String name) {

        StringRequest strReq = new StringRequest(Request.Method.POST,
                AppConfig.URL_REVIEW, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {

                Log.i(TAG, "HELLOOOOOO");

                Log.d(TAG, "Register Response: " + response.toString());


                try {
                    JSONObject jObj = new JSONObject(response);

                    Log.i("JSON Response",response);
                    Log.i("JSON object", ""+jObj.getBoolean("error"));

                    boolean error = jObj.getBoolean("error");
                    if (!error) {
                        // User successfully stored in MySQL
                        // Now store the user in sqlite

                        // Inserting row in users table

                        Toast.makeText(getApplicationContext(), "foll awii", Toast.LENGTH_LONG).show();

                        // Launch login activity
                    } else {

                        // Error occurred in registration. Get the error
                        // message
                        String errorMsg = jObj.getString("error_msg");
                        Toast.makeText(getApplicationContext(),
                                errorMsg, Toast.LENGTH_LONG).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("yomna", "Registration Error: " + error.getMessage());
                Toast.makeText(getApplicationContext(),
                        error.getMessage(), Toast.LENGTH_LONG).show();
            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                // Posting params to register url
                Map<String, String> params = new HashMap<String, String>();
                params.put("reviews", review);
                params.put("name", name);

                Log.i(TAG, review);
                return params;
            }

        };

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(strReq, "req_register");
    }

    private void AddRating(final Double rating, final String name) {

        StringRequest strReq = new StringRequest(Request.Method.POST,
                AppConfig.URL_RATING, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {

                Log.i(TAG, "HELLOOOOOO");

                Log.d(TAG, "Register Response: " + response.toString());


                try {
                    JSONObject jObj = new JSONObject(response);

                    Log.i("JSON Response",response);
                    Log.i("JSON object", ""+jObj.getBoolean("error"));

                    boolean error = jObj.getBoolean("error");
                    if (!error) {
                        // User successfully stored in MySQL
                        // Now store the user in sqlite

                        // Inserting row in users table

                        Toast.makeText(getApplicationContext(), "foll awii", Toast.LENGTH_LONG).show();

                        // Launch login activity
                    } else {

                        // Error occurred in registration. Get the error
                        // message
                        String errorMsg = jObj.getString("error_msg");
                        Toast.makeText(getApplicationContext(),
                                errorMsg, Toast.LENGTH_LONG).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("yomna", "Registration Error: " + error.getMessage());
                Toast.makeText(getApplicationContext(),
                        error.getMessage(), Toast.LENGTH_LONG).show();
            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                // Posting params to register url
                Map<String, String> params = new HashMap<String, String>();
                String UpdatedRating = "" + rating;

                params.put("rating", UpdatedRating);
                params.put("name", name);

                return params;
            }

        };

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(strReq, "req_register");
    }


    }


