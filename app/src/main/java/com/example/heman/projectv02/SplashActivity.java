package com.example.heman.projectv02;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationResult;

public class SplashActivity extends AppCompatActivity {
    private static final int MY_PERMISSIONS_REQUEST_CODE = 1;
    private boolean gpsEnabled;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        gpsEnabled = false;
        if (checkPermissions()){
            Toast.makeText(this, "You have Permission", Toast.LENGTH_LONG).show();
        }else {
            setPermissions();
        }
        if(checkPermissions()){
            LocationFinder.init(this,new LocationCallback(){
                @Override
                public void onLocationResult(LocationResult locationResult) {
                    super.onLocationResult(locationResult);
                    Location l=locationResult.getLastLocation();
                    Toast.makeText(SplashActivity.this,"Location Result:\nLatitude: "+String.valueOf(l.getLatitude())+"\nLongitude: "+String.valueOf(l.getLongitude()+"\nAltitude: "+String.valueOf(l.getAltitude())),Toast.LENGTH_LONG).show();
                }
            });

            try {
                Thread.sleep(7000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            startActivity(new Intent(this,MainActivity.class));
            finish();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        LocationFinder.startLocationUpdates();
    }

    @Override
    protected void onPause() {
        super.onPause();
        LocationFinder.stopLocationUpdates();
    }

    //Permissions
    protected boolean checkPermissions() {
        return ActivityCompat.checkSelfPermission(this,
                android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED;
    }
    protected void setPermissions() {
        ActivityCompat.requestPermissions(this, new String[]{
                android.Manifest.permission.ACCESS_FINE_LOCATION}, MY_PERMISSIONS_REQUEST_CODE);
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode != MY_PERMISSIONS_REQUEST_CODE){
            return;
        }
        boolean isGranted = true;
        for (int result : grantResults){
            if (result != PackageManager.PERMISSION_GRANTED){
                isGranted = false;
                break;
            }
        }
        if (isGranted){
            isLocationServiceEnabled();
            checkLocationEnabled();
            Toast.makeText(this, "Permission Granted", Toast.LENGTH_LONG).show();
            startActivity(new Intent(SplashActivity.this, MainActivity.class));
            finish();
        } else {
            Toast.makeText(this, "This app requires location permission to work properly", Toast.LENGTH_LONG).show();
        }
    }

    //Location Enabled
    public void isLocationServiceEnabled() {
        try {
            LocationManager locationManager=(LocationManager)getSystemService(Context.LOCATION_SERVICE);
            gpsEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        }catch (Exception e){}
    }

    public void checkLocationEnabled(){
        if (gpsEnabled){
            Toast.makeText(this, "GPS Enabled", Toast.LENGTH_SHORT).show();
        }else {
             Toast.makeText(this, "Please Enable Location", Toast.LENGTH_SHORT).show();
             startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
        }
    }


}