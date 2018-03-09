package com.example.pilarmargom.appweather;

import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import data.Current;
import data.Currently;
import data.WeatherData;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import utils.APIRestService;
import utils.RetrofitClient;

public class WeatherActivity extends AppCompatActivity {
    private String longitud;
    private String latitud;
    private TextView localizacion;
    private TextView temperatura;
    private TextView hora;
    private TextView humedad;
    private TextView lluvia;
    private TextView sumario;
    private ImageView icono;
    ProgressBar pb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weather);

        latitud = getIntent().getStringExtra(getString(R.string.clave_latitud));
        longitud = getIntent().getStringExtra(getString(R.string.clave_longitud));

        localizacion = (TextView) findViewById(R.id.locationLabel);
        temperatura = (TextView) findViewById(R.id.temperatureLabel);
        hora = (TextView) findViewById(R.id.timeLabel);
        humedad = (TextView) findViewById(R.id.humidityValue);
        lluvia = (TextView) findViewById(R.id.precipValue);
        sumario = (TextView) findViewById(R.id.summaryLabel);
        icono = (ImageView) findViewById(R.id.iconImageView);
        pb = (ProgressBar)findViewById(R.id.progressBar);

        cargarDatos();
    }

    private void cargarDatos() {
        String key = APIRestService.KEY;

        if (isNetworkAvailable()) {
            Retrofit r = RetrofitClient.getClient(APIRestService.BASE_URL);
            APIRestService ars = r.create(APIRestService.class);
            Call<WeatherData> call = ars.obtenerTiempo(APIRestService.KEY, latitud, longitud,
                    APIRestService.EXCLUDE, APIRestService.LANG);

            pb.setVisibility(View.VISIBLE);
            call.enqueue(new Callback<WeatherData>() {
                String res = "";
                WeatherData wd = null;
                Current c = null;
                TextView tvRes = (TextView) findViewById(R.id.locationLabel);

                @Override
                public void onResponse(Call<WeatherData> call, Response<WeatherData> response) {
                    pb.setVisibility(View.GONE);
                    if (!response.isSuccessful()) {
                        Log.i("Resultado: ", "Error" + response.code());
                    } else {
                        wd = response.body();
                        c = obtenerCurrent(wd);
                        cargarComponentes(c);
                    }
                }

                @Override
                public void onFailure(Call<WeatherData> call, Throwable t) {
                    pb.setVisibility(View.GONE);
                    Log.e("error", t.toString());
                }
            });
        } else {
            Toast.makeText(this, "Error de conexión", Toast.LENGTH_LONG).show();
        }
    }

    private boolean isNetworkAvailable() {
        boolean isAvailable=false;
        //Gestor de conectividad
        ConnectivityManager manager = (ConnectivityManager)
                getSystemService(WeatherActivity.CONNECTIVITY_SERVICE);
        //Objeto que recupera la información de la red
        NetworkInfo networkInfo = manager.getActiveNetworkInfo();
        //Si la información de red no es nula y estamos conectados
        //la red está disponible
        if(networkInfo!=null && networkInfo.isConnected()){
            isAvailable=true;
        }
        return isAvailable;

    }

    private Current obtenerCurrent(WeatherData wd) {
        Currently c = wd.getCurrently();
        String timezone = wd.getTimezone();
        Current currentWeather = new Current();

        currentWeather.setHumidity(c.getHumidity());
        currentWeather.setTime(c.getTime());
        currentWeather.setPrecipChance(c.getPrecipProbability());
        currentWeather.setTemperature(c.getTemperature());
        currentWeather.setIcon(c.getIcon());
        currentWeather.setSummary(c.getSummary());

        currentWeather.setTimeZone(timezone);

        return currentWeather;
    }

    private void cargarComponentes(Current current) {
        localizacion.setText(current.getTimeZone());
        temperatura.setText(current.getTemperatureCelsius()+"");
        hora.setText(current.getFormattedTime());
        humedad.setText((int)(current.getHumidity()*100) +"%");
        lluvia.setText((int)(current.getPrecipChance()*100) +"%");
        sumario.setText(current.getSummary());
        Drawable drawable = getResources().getDrawable(current.getIconId());
        icono.setImageDrawable(drawable);
    }
}
