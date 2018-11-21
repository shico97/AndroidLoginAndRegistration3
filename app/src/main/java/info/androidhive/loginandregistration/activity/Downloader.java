package info.androidhive.loginandregistration.activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.ColorSpace;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.widget.ListView;
import android.widget.Toast;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;

import static info.androidhive.loginandregistration.app.AppController.TAG;

/**
 * Created by sherif hesham on 13-Oct-18.
 */

    public class Downloader extends AsyncTask<Void,Void,String> {

    Context c;
    String urlAddess = "dsad";
    ListView landmarks;
    ProgressDialog pd;
    Double CurrentLat;
    Double CurrentLong;

    public Downloader(Context c, String urlAddess, ListView landmarks, Double CurrentLat,Double CurrentLong ) {
        this.c = c;
        this.urlAddess = urlAddess;
        this.landmarks = landmarks;
        this.CurrentLong=CurrentLong;
        this.CurrentLat = CurrentLat;


        Log.i(TAG, "Downloader:  " + CurrentLat);

    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();

        pd = new ProgressDialog(c);
        pd.setTitle("Retrieve");
        pd.setMessage("Retrieving..Please wait");
        pd.show();
    }

    @Override
    protected String doInBackground(Void... params) {
        return this.downloadData();
    }

    @Override
    protected void onPostExecute(String jsonData) {
        super.onPostExecute(jsonData);

        pd.dismiss();
        if (jsonData.startsWith("Error")) {
            Toast.makeText(c, "Unsuccessful " + jsonData, Toast.LENGTH_SHORT).show();
        } else {
            //PARSE
           Log.i(TAG, "onPostExecuteEEEEEEEEEEEEEEEEEEEe000: " + CurrentLong);

           new DataParser(c, jsonData, landmarks,CurrentLat , CurrentLong).execute();

        }

    }

    public String downloadData() {
        Object connection = Connector.connect(urlAddess);
        if (connection.toString().startsWith("Error")) {
            return connection.toString();
        }

        try {
            HttpURLConnection con = (HttpURLConnection) connection;

            InputStream is = new BufferedInputStream(con.getInputStream());
            BufferedReader br = new BufferedReader(new InputStreamReader(is));

            String line;
            StringBuffer jsonData = new StringBuffer();
            Log.i(TAG, "downloadData: xxx" + jsonData);

            while ((line = br.readLine()) != null) {
                Log.i(TAG, "downloadData: SSSSS11");
                jsonData.append(line + "n");
                Log.i(TAG, "downloadData: SSSSS22");
            }

            br.close();
            is.close();

            Log.i(TAG, "downloadData:x " + jsonData);
            return jsonData.toString();

        } catch (IOException e) {
            e.printStackTrace();
            return "Error " + e.getMessage();
        }

    }

}

