package com.example.guidofabbrini.easyracevolley;
import com.android.volley.Request;
import com.android.volley.toolbox.JsonObjectRequest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.net.wifi.SupplicantState;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
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
import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;

import android.os.Handler;


public class MainActivity extends Activity {


    //private String urlJsonObj ="http://192.168.42.1/api/v1/monitor";
    private String urlJsonObj = "http://pastebin.com/raw/xLjUGiH8"; //USE THIS URL FOR DEBUG
    char data[] = {'"', 'm', 'o', 't', 'o', 'l', 'a', 'i', 'k', 'a', '"'};
    private String defaultSSID = new String(data) ; // EASY RACE LAP TIMER <-- SSID
    String currentSSID ;
    private static String TAG = MainActivity.class.getSimpleName();

    // Progress dialog
    private ProgressDialog pDialog;

    private  ImageView image_view,image_view_landscape;

    private TextView title_main,fastest_lap,connectionControl;

    //DI PROVA
    private TextView text_raw_view;

    private ListView lv ;
    private Boolean flag; // we use this flag to follow the android ActivityLifeCycle
    ArrayList<HashMap<String, String>> dataStream , dataAccomulator, dataPilot;
   /**FAMILY TREE of the HashMap implementation
     Map Interface is an object that maps keys to values
     public abstract class AbstractMap --> implements Map < K , V >
     public class HashMap extends AbstractMap<K, V> implements Map<K, V>
     This implementation provides all the optional map operations*/

    /**ArrayList: Implements a dynamic array by extending AbstractList*/

    WifiManager mainWifi;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        dataStream =new ArrayList<>();
        dataAccomulator = new ArrayList<>();
        dataPilot = new ArrayList<>();
        lv = (ListView) findViewById(R.id.list);
        title_main = (TextView) findViewById(R.id.title);
        image_view = (ImageView) findViewById(R.id.imageView);
        image_view_landscape = (ImageView) findViewById(R.id.imageView2);
        fastest_lap = (TextView) findViewById(R.id.fastest_lap);
        connectionControl = (TextView) findViewById(R.id.connectionControl);

        //DI PROVA
        text_raw_view = (TextView) findViewById(R.id.text_raw_view);

        image_view.setImageResource(R.drawable.easy_race_lap_timer_logo_1); // Setting the ImageView resource

        pDialog = new ProgressDialog(this);
        pDialog.setMessage("Please wait...");
        pDialog.setCancelable(false);

        /*Toast.makeText(getApplicationContext(),
                "First JSON request ..",
                Toast.LENGTH_LONG).show();*/

        // Initiate wifi service manager
        mainWifi = (WifiManager) getSystemService(Context.WIFI_SERVICE);

        // Check for wifi is disabled
        if (!mainWifi.isWifiEnabled())
        {
            // If wifi disabled then enable it
            Toast.makeText(getApplicationContext(), "Wifi is disabled.. making it enabled",
                    Toast.LENGTH_LONG).show();

            mainWifi.setWifiEnabled(true);
        }

            handler.post(runnableCode); // Looper calling
            flag = true;
        }


    // Create the Handler object
    Handler handler = new Handler();
    // Define the code block to be executed
    private Runnable runnableCode = new Runnable() {
        @Override
        public void run() {
            // Control of wifi SSID
            /*WifiManager wifiManager = (WifiManager) getSystemService(Context.WIFI_SERVICE);
            WifiInfo wifiInfo;

            wifiInfo = wifiManager.getConnectionInfo();
            if (wifiInfo.getSupplicantState() == SupplicantState.COMPLETED) {
                currentSSID = wifiInfo.getSSID();
            }
             // Controlling the SSID
             // Comment this to Debug the app with any connection
             if (Objects.equals(defaultSSID, currentSSID))  {
                connectionControl.setBackgroundColor(0xffffbb33); // Classic Orange
                connectionControl.setText(""); // No text required
                 makeJsonObjectRequest(); // Volley Request

            }
            else { connectionControl.setBackgroundColor(0xFFFF0400); // Dark Red
                if (currentSSID == null) connectionControl.setText("You have no WIFI connection at all !");
                else connectionControl.setText("Please search for our SSID : "+defaultSSID);

            }*/

            makeJsonObjectRequest(); // Volley Request

            Toast.makeText(getApplicationContext(),
                    "Refreshing ..",
                    Toast.LENGTH_SHORT).show();

            handler.postDelayed(runnableCode, 10000);
        }
    };


    private void makeJsonObjectRequest() {

        showpDialog();
        dataStream.clear(); // Clear the View (Init);

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

                        JSONObject fast_lap = d.getJSONObject("fastest_lap");
                        String fastest_lap_raw = fast_lap.getString("lap_time");
                        Float fastest_lap_sec = (Float.parseFloat(fastest_lap_raw) / 1000);
                        String fastest_lap = Float.toString(fastest_lap_sec);

                        // tmp hash map for single contact
                        HashMap<String, String> positions = new HashMap<>();

                        // adding each child node to HashMap key => value
                        positions.put("position", position);
                        positions.put("name", name);
                        positions.put("lapcount", lapcount);
                        positions.put("avg_lap_totext", avg_lap_totext);
                        positions.put("fastest_lap", fastest_lap);

                        // adding contact to contact list
                        dataStream.add(positions);
                        dataAccomulator.add(positions);
                    }
                     // PROVA !!
                    text_raw_view.setText(dataAccomulator.toString());
                    dataPilot.clear();

                    for (int i =0; i<dataAccomulator.size();i++) {
                        if (dataAccomulator.get(i).containsValue("Emiliano") )
                            dataPilot.add(dataAccomulator.get(i));
                    }



                    //       Updating parsed JSON data into ListView

                    //Extended Adapter that is the bridge between a ListView and the data that backs the list,
                    //the ListView can display any data provided that it is wrapped in a ListAdapter

                    /*ListAdapter adapter = new SimpleAdapter(
                    MainActivity.this, dataStream,
                            R.layout.list_item, new String[]{"position", "name", "lapcount","avg_lap_totext","fastest_lap"}, new int[]{R.id.position,
                            R.id.name, R.id.lapcount,R.id.avg_lap_totext,R.id.fastest_lap});

                    lv.setAdapter(adapter);*/

                    ListAdapter adapter = new SimpleAdapter(
                            MainActivity.this, dataPilot,
                            R.layout.list_item, new String[]{"position", "name", "lapcount","avg_lap_totext","fastest_lap"}, new int[]{R.id.position,
                            R.id.name, R.id.lapcount,R.id.avg_lap_totext,R.id.fastest_lap});

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
    public void onPause()
    {
        super.onPause();
        handler.removeCallbacks(runnableCode);
        flag = false;
    }
    @Override
    public void onResume() {
        super.onResume();
        if (!flag) {
            handler.post(runnableCode);
        }
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


