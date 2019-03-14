package ca.bcit.assignment3;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    private TextView editLocation = null;

    Intent backgroundLocationService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        editLocation = (TextView) findViewById(R.id.temp);

        LocalBroadcastManager.getInstance(this).registerReceiver(
                new BroadcastReceiver() {
                    @Override
                    public void onReceive(Context context, Intent intent) {
                        double latitude = intent.getDoubleExtra(Constants.EXTRA_LATITUDE, 0);
                        double longitude = intent.getDoubleExtra(Constants.EXTRA_LONGITUDE, 0);
                        editLocation.setText("Lat: " + latitude + ", Lng: " + longitude);
                    }
                }, new IntentFilter(Constants.ACTION_LOCATION_BROADCAST)
        );

        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, 1);

        }

        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);

        }

        backgroundLocationService = new Intent(this, BackgroundLocationService.class);
    }

    public void startClicked(View view) {
        startService(backgroundLocationService);
    }

    public void stopClicked(View view) {
        stopService(backgroundLocationService);
    }
}
