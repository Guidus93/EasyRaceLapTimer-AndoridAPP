package com.example.guidofabbrini.easyracevolley;
import com.android.volley.Request;
import com.android.volley.toolbox.JsonObjectRequest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.util.Log;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

import android.os.Handler;


public class MainActivity extends Activity {


    // private String urlJsonObj ="http://192.168.42.1/api/v1/monitor";
    private String urlJsonObj = "http://pastebin.com/raw/puYnpK96";
    private static String TAG = MainActivity.class.getSimpleName();

    // Progress dialog
    private ProgressDialog pDialog;

    private TextView title_main;
    private ListView lv;

    ArrayList<HashMap<String, String>> dataStream;
   /**FAMILY TREE of the HashMap implementation
     Map Interface is an object that maps keys to values
     public abstract class AbstractMap --> implements Map < K , V >
     public class HashMap extends AbstractMap<K, V> implements Map<K, V>
     This implementation provides all of the optional map operations*/


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        dataStream =new ArrayList<>();
        lv = (ListView) findViewById(R.id.list);
        title_main = (TextView) findViewById(R.id.title);

        pDialog = new ProgressDialog(this);
        pDialog.setMessage("Please wait...");
        pDialog.setCancelable(false);

        Toast.makeText(getApplicationContext(),
                "First JSON request ..",
                Toast.LENGTH_LONG).show();

        // makeJsonObjectRequest();

        handler.post(runnableCode); // Looper calling

    }

    // Create the Handler object
    Handler handler = new Handler();
    // Define the code block to be executed
    private Runnable runnableCode = new Runnable() {
        @Override
        public void run() {

            makeJsonObjectRequest(); // Volley Request

            Toast.makeText(getApplicationContext(),
                    "Refreshing ..",
                    Toast.LENGTH_SHORT).show();

            handler.postDelayed(runnableCode, 10000);
        }
    };


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

                    JSONObject session = jsonObj.getJSONObject("session");
                    String title = session.getString("title");

                    title_main.setText(title);



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

                        // tmp hash map for single contact
                        HashMap<String, String> positions = new HashMap<>();

                        // adding each child node to HashMap key => value
                        positions.put("title", title);
                        positions.put("position", position);
                        positions.put("name", name);
                        positions.put("lapcount", lapcount);
                        positions.put("avg_lap_totext", avg_lap_totext);

                        // adding contact to contact list
                        dataStream.add(positions);

                    }
                    //       Updating parsed JSON data into ListView

                    //Extended Adapter that is the bridge between a ListView and the data that backs the list,
                    //the ListView can display any data provided that it is wrapped in a ListAdapter
                    ListAdapter adapter = new SimpleAdapter(
                    MainActivity.this, dataStream,
                            R.layout.list_item, new String[]{"position", "name", "lapcount","avg_lap_totext"}, new int[]{R.id.position,
                            R.id.name, R.id.lapcount,R.id.avg_lap_totext});

//                    TODO: HO PROVATO lv.listAdapter(null) MA APPENDE COMUNQUE TUTTO IN CODA;

                           lv.setAdapter(adapter);


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

    @Override
    public void onDestroy()
    {
        super.onDestroy();
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