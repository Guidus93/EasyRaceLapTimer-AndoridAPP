package com.example.guidofabbrini.easyracevolley;
import com.android.volley.Request;
import com.android.volley.toolbox.JsonObjectRequest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.net.Uri;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import android.os.Handler;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.appindexing.Thing;
import com.google.android.gms.common.api.GoogleApiClient;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class MainActivity extends Activity {


    // json array response url
    // private String urlJsonObj ="http://192.168.42.1/api/v1/monitor";
    private String urlJsonObj = "http://pastebin.com/raw/puYnpK96";
    private static String TAG = MainActivity.class.getSimpleName();

    // Progress dialog
    private ProgressDialog pDialog;

    private TextView txtResponse;

    // string builder to show the parsed response --> append method required
    StringBuilder jsonResp = new StringBuilder();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        txtResponse = (TextView) findViewById(R.id.txtResponse);
        txtResponse.setText("Parsing data ..");

        pDialog = new ProgressDialog(this);
        pDialog.setMessage("Please wait...");
        pDialog.setCancelable(false);


        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
            @Override
            public void run() {
                makeJsonObjectRequest();
                //Do something here
            }
        }, 5000);

    }

    /**
     * Method to make json object request where json response starts wtih {
     */


    private void makeJsonObjectRequest() {

        showpDialog();

        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.GET,
                urlJsonObj, null, new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {
                Log.d(TAG, response.toString());

                try {
                    // Parsing json object response

                    String jsonStr = response.toString();

                    JSONObject jsonObj = new JSONObject(jsonStr);

                    // Getting JSON Array node
                    JSONArray data = jsonObj.getJSONArray("data");

                    // looping through All data
                    for (int i = 0; i < data.length(); i++) {
                        JSONObject d = data.getJSONObject(i);
                        String position = d.getString("position");
                        JSONObject pilot = d.getJSONObject("pilot");
                        String name = pilot.getString("name");
                        String quad = pilot.getString("quad");
                        String team = pilot.getString("team");
                        String lapcount = d.getString("lap_count");
                        String avg_lap_time = d.getString("avg_lap_time");
                        Float avg_lap_time_sec = (Float.parseFloat(avg_lap_time) / 1000);
                        String avg_lap_totext = Float.toString(avg_lap_time_sec);
                        jsonResp.append("Position: ").append(position).append("\n");
                        jsonResp.append("Name: ").append(name).append("\n");
                        jsonResp.append("Average lap time: ").append(avg_lap_totext).append("\n");
                        jsonResp.append("lap_count: ").append(lapcount).append("\n\n");
                    }

                    txtResponse.setText(jsonResp);

                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(getApplicationContext(),
                            "Error :  " + e.getMessage(),
                            Toast.LENGTH_LONG).show();
                }
                hidepDialog();
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.d(TAG, "Error : " + error.getMessage());
                Toast.makeText(getApplicationContext(),
                        error.getMessage(), Toast.LENGTH_SHORT).show();
                // hide the progress dialog
                hidepDialog();
            }
        });

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(jsonObjReq);
    }


    private void showpDialog() {
        if (!pDialog.isShowing())
            pDialog.show();
    }

    private void hidepDialog() {
        if (pDialog.isShowing())
            pDialog.dismiss();
    }



}