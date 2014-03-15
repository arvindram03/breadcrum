package com.example.BreadCrum;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.telephony.PhoneStateListener;
import android.telephony.SmsManager;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.widget.Toast;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class CallHelper implements LocationListener {
	
	public static boolean callRinged = false;
	public static boolean callReceived = false;
	public static String phoneNumber;
	public static final long MIN_TIME_BETWEEN_UPDATE = 0l;
	public static final long MIN_DISTANCE_BETWEEN_UPDATE = 0l;
	
	private Location getLocation() {
		Location location=null;
		LocationManager locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
		boolean isGPSEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
		boolean isNetworkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
		
		if (isGPSEnabled) {
			Log.d("gps","gps is enabled");
				locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,MIN_TIME_BETWEEN_UPDATE,MIN_DISTANCE_BETWEEN_UPDATE, this);
				if (locationManager != null) {
					location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
				}
			
		}
		else if(isNetworkEnabled){
			Log.d("network","network is enabled");
			locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER,MIN_TIME_BETWEEN_UPDATE,MIN_DISTANCE_BETWEEN_UPDATE, this);
			if (locationManager != null) {
				location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
			}
		}
		return location;
				
	}
	
	private class CallStateListener extends PhoneStateListener {
    	
		
        @Override
        public void onCallStateChanged(int state, String incomingNumber) {
            switch (state) {
            	case TelephonyManager.CALL_STATE_RINGING:
            		callRinged = true;
            		phoneNumber = incomingNumber;
            		break;
            	case TelephonyManager.CALL_STATE_OFFHOOK:
            		callReceived = true;
            		break;
                case TelephonyManager.CALL_STATE_IDLE:
                	if (callRinged && !callReceived) {
	                    SmsManager smsManager = SmsManager.getDefault();
	                    Location location = getLocation();
	                    if(location!=null){
	                    	Geocoder gcd = new Geocoder(context, Locale.getDefault());
		                    try {
		                        List<Address> address = gcd.getFromLocation(location.getLatitude(),location.getLongitude(), 1);
		                        if(address.size()>0) {
		                            smsManager.sendTextMessage(phoneNumber, null, "Not able to pick the phone. I am near \n" + address.get(0).getAddressLine(0), null, null);
		                        }
		                        else{
		                        	smsManager.sendTextMessage(phoneNumber, null, "Not able to pick the phone.", null, null);
		                        }
		                    } catch (IOException e) {
		                        e.printStackTrace();
		                    }
	                    }
	                    else{
	                    	smsManager.sendTextMessage(phoneNumber, null, "Not able to pick the phone.", null, null);
	                    }
	                    
	                	}
                	else{
                		callReceived=false;
                		callRinged=false;
                	}
                    break;
            }
        }
    }

    public class OutgoingReceiver extends BroadcastReceiver {
        public OutgoingReceiver() {
        }

        @Override
        public void onReceive(Context context, Intent intent) {
            String number = intent.getStringExtra(Intent.EXTRA_PHONE_NUMBER);

            Toast.makeText(context,
                    "Outgoing: "+number,
                    Toast.LENGTH_LONG).show();
        }

    }

    private Context context;
    private TelephonyManager tm;
    private CallStateListener callStateListener;

    private OutgoingReceiver outgoingReceiver;

    public CallHelper(Context contex) {
        this.context = contex;

        callStateListener = new CallStateListener();
        outgoingReceiver = new OutgoingReceiver();
    }

    /**
     * Start calls detection.
     */
    public void start() {
        tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        tm.listen(callStateListener, PhoneStateListener.LISTEN_CALL_STATE);

        IntentFilter intentFilter = new IntentFilter(Intent.ACTION_NEW_OUTGOING_CALL);
        context.registerReceiver(outgoingReceiver, intentFilter);
    }

    /**
     * Stop calls detection.
     */
    public void stop() {
        tm.listen(callStateListener, PhoneStateListener.LISTEN_NONE);
        context.unregisterReceiver(outgoingReceiver);
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

