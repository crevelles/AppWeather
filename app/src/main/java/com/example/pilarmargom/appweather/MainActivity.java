package com.example.pilarmargom.appweather;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

public class MainActivity extends AppCompatActivity {

    EditText lon;
    EditText lat;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        lat = (EditText)findViewById(R.id.etLat);
        lon = (EditText)findViewById(R.id.etLon);
    }

    public void consumirWS(View v) {

        Intent intent = new Intent(this, WeatherActivity.class);

        intent.putExtra(getString(R.string.clave_latitud),lat.getText().toString());
        intent.putExtra(getString(R.string.clave_longitud),lon.getText().toString());

        startActivity(intent);

    }
}
