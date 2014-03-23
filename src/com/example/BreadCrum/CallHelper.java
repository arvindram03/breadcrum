package com.example.BreadCrum;

import android.content.Context;
import android.location.*;
import android.os.Bundle;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;


public class CallHelper implements LocationListener {
	
	public static boolean callRinged = false;
	public static boolean callReceived = false;
	public static String phoneNumber;
	
	
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
                	if (callRinged && !callReceived ) {
                		DataStoreHelper dataStoreHelper = new DataStoreHelper(context);
                		if(dataStoreHelper.getContact(phoneNumber)!=null) {
	                        Messenger messenger = new Messenger(context);
	                        messenger.sendMessage(phoneNumber);
                		}
                        callReceived=false;
                        callRinged=false;

	                	}
                	else{
                		callReceived=false;
                		callRinged=false;
                	}
                    break;
            }
        }

    }

    private Context context;
    private TelephonyManager tm;
    private CallStateListener callStateListener;



    public CallHelper(Context contex) {
        this.context = contex;

        callStateListener = new CallStateListener();
 
    }

    public void start() {
        tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        tm.listen(callStateListener, PhoneStateListener.LISTEN_CALL_STATE);
    }

    public void stop() {
        tm.listen(callStateListener, PhoneStateListener.LISTEN_NONE);

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

