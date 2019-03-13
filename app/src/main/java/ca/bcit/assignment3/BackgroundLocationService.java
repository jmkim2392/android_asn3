package ca.bcit.assignment3;

import android.Manifest;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.widget.Toast;

import org.json.JSONObject;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.URISyntaxException;
import java.util.Collections;
import java.util.List;

import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;

public class BackgroundLocationService extends Service {

    private LocationManager locationManager = null;
    private LocationListener locationListener = null;
    private String latitude = "";
    private String longitude = "";
    private String deviceIpAddr = "";
    private String deviceName = "";
    private Socket serverSocket = null;

    @Override
    public void onCreate() {
        super.onCreate();

        getDeviceInfo();

        openSocket();

        locationManager = (LocationManager)
                getSystemService(Context.LOCATION_SERVICE);

        locationListener = new MyLocationListener();

        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

        } else {
            // Permission has already been granted
        }

        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);

        sendBroadcastMessage(locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER));
        latitude = "" + locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER).getLatitude();
        longitude = "" + locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER).getLongitude();

        new sendInfo().execute(deviceIpAddr, deviceName, latitude, longitude);
    }

    private void sendBroadcastMessage(Location location) {
        if (location != null) {
            Intent intent = new Intent(Constants.ACTION_LOCATION_BROADCAST);
            intent.putExtra(Constants.EXTRA_LATITUDE, location.getLatitude());
            intent.putExtra(Constants.EXTRA_LONGITUDE, location.getLongitude());
            LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
        }
    }

    /*----------Listener class to get coordinates ------------- */
    private class MyLocationListener implements LocationListener {
        @Override
        public void onLocationChanged(Location loc) {

            Toast.makeText(getBaseContext(), "Location changed : Lat: " +
                            loc.getLatitude() + " Lng: " + loc.getLongitude(),
                    Toast.LENGTH_SHORT).show();

            //debug to be removed
            String longitude = "Longitude: " + loc.getLongitude();
            Log.v(Constants.TAG, longitude);
            String latitude = "Latitude: " + loc.getLatitude();
            Log.v(Constants.TAG, latitude);

            sendBroadcastMessage(loc);
            latitude = "" + loc.getLatitude();
            longitude = "" + loc.getLongitude();

            new sendInfo().execute(deviceIpAddr, deviceName, latitude, longitude);

        }

        @Override
        public void onProviderDisabled(String provider) {
            // TODO Auto-generated method stub
        }

        @Override
        public void onProviderEnabled(String provider) {
            // TODO Auto-generated method stub
        }

        @Override
        public void onStatusChanged(String provider,
                                    int status, Bundle extras) {
            // TODO Auto-generated method stub
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    private class sendInfo extends AsyncTask<String, String, String> {

        protected String doInBackground(String... params) {

            String result;
            JSONObject message = new JSONObject();
            String ipAddr = params[0];
            String name = params[1];
            String lat = params[2];
            String lng = params[3];

            try {
                message.put("ip", ipAddr);
                message.put("name", name);
                message.put("lat", lat);
                message.put("lng", lng);
                serverSocket.emit("send coordinates", message);
                result = "Successful";
            } catch (Exception e) {
                result = "Failed";
            }
            return result;
        }

        protected void onPostExecute(String result) {

        }
    }

    private Emitter.Listener onConnect = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            // if we need to do anything once connection successful
            Log.v(Constants.TAG, "Connected");
        }
    };

    private Emitter.Listener onDisconnect = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            // if we need to do anything once disconnect
            Log.v(Constants.TAG, "Disconnected");
        }
    };

    private Emitter.Listener onConnectError = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            // for any connection error handling
            Log.v(Constants.TAG, "Connection Error");
        }
    };

    private void getDeviceInfo() {

        deviceName = Build.USER;
        try {
            List<NetworkInterface> interfaces = Collections.list(NetworkInterface.getNetworkInterfaces());
            for (NetworkInterface intf : interfaces) {
                List<InetAddress> addrs = Collections.list(intf.getInetAddresses());
                for (InetAddress addr : addrs) {
                    if (!addr.isLoopbackAddress()) {
                        deviceIpAddr = addr.getHostAddress();

                    }
                }
            }
        } catch (Exception e) {
            Log.v(Constants.TAG, "Failed obtaining IP address");
        }
    }

    private void openSocket() {
        try {
            serverSocket = IO.socket("URL HERE");
        } catch (URISyntaxException e) {
            //handle exception
        }

        serverSocket.on(Socket.EVENT_CONNECT, onConnect);
        serverSocket.on(Socket.EVENT_DISCONNECT, onDisconnect);
        serverSocket.on(Socket.EVENT_CONNECT_ERROR, onConnectError);
        serverSocket.on(Socket.EVENT_CONNECT_TIMEOUT, onConnectError);

        serverSocket.connect();
    }
}
