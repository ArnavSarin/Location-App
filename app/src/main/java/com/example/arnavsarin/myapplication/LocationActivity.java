package com.example.arnavsarin.myapplication;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Arnav Sarin on 4/22/2017.
 */

public class LocationActivity extends AppCompatActivity {
    private EditText editStartLong;
    private EditText editStartLat;
    private EditText editEndLong;
    private EditText editEndLat;
    public static String startLocationLong;
    public static String startLocationLat;
    public static String endLocationLong;
    public static String endLocationLat;
    private Button buttonStart;
    Context context = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.locationinput);
        buttonStart = (Button) findViewById(R.id.buttonStart);
        editStartLong = (EditText) findViewById(R.id.editStartLong);
        editStartLat = (EditText) findViewById(R.id.editStartLat);
        editEndLong = (EditText) findViewById(R.id.editEndLong);
        editEndLat = (EditText)findViewById(R.id.editEndLat);



        buttonStart.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), MapsActivity.class);
                startLocationLong=editStartLong.getText().toString();
                startLocationLat=editStartLat.getText().toString();
                endLocationLong=editEndLong.getText().toString();
                endLocationLat=editEndLat.getText().toString();
                intent.putExtra("startLocationLong",startLocationLong);
                intent.putExtra("startLocationLat",startLocationLat);
                intent.putExtra("endLocationLat",endLocationLat);
                intent.putExtra("endLocationLong",endLocationLong);
                intent.putExtra("x","mom");
                Log.i("start location long",startLocationLong);
                Log.i("start location lat",startLocationLat);
                Log.i("end location long",endLocationLong);
                Log.i("end location lat",endLocationLat);
                startActivity(intent);
            }
        });
    }
}
