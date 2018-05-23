package com.example.sushantpaygude.finalproject.Activities;

import android.Manifest;
import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.app.ActivityCompat;
import android.util.Log;

public class LocationService extends Service implements LocationListener {
    public LocationService() {
    }

    MyBinder binder;
    private double[] location_ = new double[2];
    private LocationManager locationManager;
    private SharedPreferences.Editor editor;

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        // throw new UnsupportedOperationException("Not yet implemented");
        return binder;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        binder = new MyBinder();
        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            // No explanation needed; request the permission
            return;
        }
        //editor = getSharedPreferences("Location", MODE_PRIVATE).edit();

        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, this);
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);
        //Location location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);

        location_[0] = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER).getLatitude();
        location_[1] = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER).getLongitude();
        addToSharedPreference(location_);
        Log.d("OnCreate: ", "Location Service is Created.");
    }

    private void addToSharedPreference(double[] location) {
        editor = getSharedPreferences("Location", MODE_PRIVATE).edit();
        editor.putString("Latitude", String.valueOf(location[0]));
        editor.putString("Longitude", String.valueOf(location[1]));
        editor.apply();
    }

    public class MyBinder extends Binder {
        LocationService getService() {
            return LocationService.this;
        }
    }

    public double[] getlocation() {
        Log.d("In LocationService", "returning");
        return location_;
    }

    @Override
    public void onLocationChanged(Location location) {
        Log.d("In LocationService", "locationchanged");

        location_[0] = location.getLatitude();
        location_[1] = location.getLongitude();
        addToSharedPreference(location_);
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }
}
