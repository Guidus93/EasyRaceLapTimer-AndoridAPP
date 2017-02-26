package com.example.guidofabbrini.easyracevolley;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;

import static android.provider.AlarmClock.EXTRA_MESSAGE;

public class PilotActivity extends AppCompatActivity {


    private TextView text_raw_view_3, session_title ;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pilot);

        TextView text_raw_view_3 = (TextView) findViewById(R.id.text_raw_view_3);
        ListView lv_dataPilot = (ListView) findViewById(R.id.lv_pilot);

       // ArrayList<HashMap<String, String>> dataPilot = intent.getParcelableArrayListExtra();

        Intent intent = getIntent();

        ArrayList<HashMap<String, String>> dataPilot = (ArrayList<HashMap<String, String>>)  intent.getSerializableExtra("dataPilot");


        // LAVORARCI SU! PASSAGGIO DEL TITOLO
//        String title = intent.getStringExtra("session_title");
//        session_title.setText(title);


        ListAdapter adapter = new SimpleAdapter(
                PilotActivity.this, dataPilot,
                R.layout.list_item, new String[]{"position", "name", "lapcount","avg_lap_totext","fastest_lap"}, new int[]{R.id.position,
                R.id.name, R.id.lapcount,R.id.avg_lap_totext,R.id.fastest_lap});

        lv_dataPilot.setAdapter(adapter);


    }
}
