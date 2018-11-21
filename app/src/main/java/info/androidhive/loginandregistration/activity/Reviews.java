package info.androidhive.loginandregistration.activity;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import info.androidhive.loginandregistration.R;

import static info.androidhive.loginandregistration.app.AppController.TAG;

public class Reviews extends AppCompatActivity {

    String Reviews;
    TextView PrevReviews;
    String s ="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reviews);

        Intent intent = getIntent();
        Reviews = intent.getStringExtra("key");

        PrevReviews = (TextView)findViewById(R.id.textView4);

        Log.i(TAG, "" + Reviews.length());

        for(int i = 0 ; i < Reviews.length() ; i++)
        {
            Log.i(TAG, s);
            if(Reviews.charAt(i) != ',')
            {
                s = s + Reviews.charAt(i);
            }
            else
                if(Reviews.charAt(i) == ',')
            {
                PrevReviews.append("- " + s + "\n");
                s = "";
            }

            if(i == Reviews.length()-1)
                {
                    PrevReviews.append("- " + s + "\n");
                }

        }
    }
}
