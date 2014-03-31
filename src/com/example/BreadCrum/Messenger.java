package com.example.BreadCrum;

import helpers.LocationHelper;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.telephony.SmsManager;

public class Messenger {

    private Context context;
    private LocationHelper locationHelper;
    public Messenger(Context context){
        this.context = context;
        this.locationHelper = new LocationHelper(context);
    }

    public void sendMessage(String phoneNumber) {
        SmsManager smsManager = SmsManager.getDefault();
        Location location = locationHelper.getLocation();
        if(location!=null){
            Geocoder gcd = new Geocoder(context, Locale.getDefault());
            try {
                if(isConnectingToInternet()){
                    List<Address> address = gcd.getFromLocation(location.getLatitude(),location.getLongitude(), 1);
                    if(address.size()>0) {
                        ArrayList<String> message = smsManager.divideMessage("Not able to pick the phone. I am near \n" + address.get(0).getAddressLine(0) + "\nProvider: " + location.getProvider() +"\n http://www.google.co.in/maps/place/" + location.getLatitude() + "," + location.getLongitude());
                        smsManager.sendMultipartTextMessage(phoneNumber, null, message, null, null);
                    }
                }
                else{
                    ArrayList<String> message = smsManager.divideMessage("Not able to pick the phone. I am near:\n Lat:" + location.getLatitude() + " \n Lon:" + location.getLongitude() + "\n http://www.google.co.in/maps/place/" + location.getLatitude() + "," + location.getLongitude());
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
}
