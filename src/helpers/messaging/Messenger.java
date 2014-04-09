package helpers.messaging;


import helpers.database.DataStoreHelper;
import helpers.location.LocationHelper;
import helpers.notification.NotificationHelper;

import java.io.IOException;
import java.sql.Date;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import models.Contact;
import models.MessageLog;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.telephony.SmsManager;
import android.util.Log;

public class Messenger {

    private Context context;
    private LocationHelper locationHelper;
    private DataStoreHelper dataStoreHelper;
    public Messenger(Context context){
        this.context = context;
        this.locationHelper = new LocationHelper(context);
        this.dataStoreHelper = new DataStoreHelper(context);
    }

    public void sendMessage(Contact contact) {
    	String phoneNumber = contact.getPhoneNumber();
    	String content = locationHelper.getUserPosition();
        SmsManager smsManager = SmsManager.getDefault();
        Location location = locationHelper.getLocation();
        if(location!=null){
            Geocoder gcd = new Geocoder(context, Locale.getDefault());
            try {
                if(isConnectingToInternet()){
                    List<Address> address = gcd.getFromLocation(location.getLatitude(),location.getLongitude(), 1);
                    if(address.size()>0) {
                    	content += " Not able to pick the phone. I am near \n" + address.get(0).getAddressLine(0) + "\nProvider: " + location.getProvider() +"\n http://www.google.co.in/maps/place/" + location.getLatitude() + "," + location.getLongitude();
                        ArrayList<String> message = smsManager.divideMessage(content);
                        smsManager.sendMultipartTextMessage(phoneNumber, null, message, null, null);
                    }
                }
                else{
                	content += " Not able to pick the phone. I am near:\n Lat:" + location.getLatitude() + " \n Lon:" + location.getLongitude() + "\n http://www.google.co.in/maps/place/" + location.getLatitude() + "," + location.getLongitude();
                    ArrayList<String> message = smsManager.divideMessage(content);
                    smsManager.sendMultipartTextMessage(phoneNumber, null, message, null, null);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        else{
            smsManager.sendTextMessage(phoneNumber, null, content+" Not able to pick the phone.", null, null);
        }
        java.util.Date now = new java.util.Date();
        MessageLog messageLog = new MessageLog(contact.getName(),phoneNumber,content, new Date(now.getTime()));
        dataStoreHelper.addMessageLog(messageLog);
        NotificationHelper.sendMessageNotification(context,messageLog);        
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
