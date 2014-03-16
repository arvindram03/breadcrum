package com.example.BreadCrum;

import android.content.Context;
import android.location.*;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.util.Log;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class Messenger implements LocationListener {

    public static final long MIN_TIME_BETWEEN_UPDATE = 0l;
    public static final long MIN_DISTANCE_BETWEEN_UPDATE = 0l;
    private Context context;

    public Messenger(Context context){
        this.context = context;
    }

    public void sendMessage(String phoneNumber) {
        SmsManager smsManager = SmsManager.getDefault();
        Location location = getLocation();
        if(location!=null){
            Geocoder gcd = new Geocoder(context, Locale.getDefault());
            try {
                if(isConnectingToInternet()){
                    List<Address> address = gcd.getFromLocation(location.getLatitude(),location.getLongitude(), 1);
                    if(address.size()>0) {
                        ArrayList<String> message = smsManager.divideMessage("Not able to pick the phone. I am near \n" + address.get(0).getAddressLine(0) + "\nLat:" + location.getLatitude() + " \n Lon:" + location.getLongitude() + "\n with location accuracy of " + location.getAccuracy() + "\n http://www.google.co.in/maps/place/" + location.getLatitude() + "," + location.getLongitude());
                        smsManager.sendMultipartTextMessage(phoneNumber, null, message, null, null);
                    }
                }
                else{
                    ArrayList<String> message = smsManager.divideMessage("Not able to pick the phone. I am near:\n Lat:" + location.getLatitude() + " \n Lon:" + location.getLongitude() + "\n with location accuracy of " + location.getAccuracy() + "\nAltitude: " + location.getAltitude() + "\nProvider: " + location.getProvider() + "\nSpeed: " + location.getSpeed() + "\n http://www.google.co.in/maps/place/" + location.getLatitude() + "," + location.getLongitude());
                    smsManager.sendMultipartTextMessage(phoneNumber, null, message, null, null);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        else{
            smsManager.sendTextMessage(phoneNumber, null, "Not able to pick the phone.", null, null);
        }
    }

    private boolean isConnectingToInternet(){
        ConnectivityManager connectivity = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivity != null)
        {
            NetworkInfo[] info = connectivity.getAllNetworkInfo();
            if (info != null)
                for (int i = 0; i < info.length; i++)
                    if (info[i].getState() == NetworkInfo.State.CONNECTED)
                    {
                        return true;
                    }

        }
        return false;
    }


    private Location getLocation() {
        Location location=null;
        LocationManager locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        boolean isGPSEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        boolean isNetworkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);

        if (isGPSEnabled) {
            Log.d("gps", "gps is enabled");
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,MIN_TIME_BETWEEN_UPDATE,MIN_DISTANCE_BETWEEN_UPDATE, this);
            if (locationManager != null) {
                location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            }

        }
        else if(isNetworkEnabled){
            Log.d("network","network is enabled");
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER,MIN_TIME_BETWEEN_UPDATE,MIN_DISTANCE_BETWEEN_UPDATE, this);
            if (locationManager != null) {
                location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
            }
        }
        return location;

    }

    @Override
    public void onLocationChanged(Location arg0) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onProviderDisabled(String arg0) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onProviderEnabled(String arg0) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onStatusChanged(String arg0, int arg1, Bundle arg2) {
        // TODO Auto-generated method stub

    }
}
