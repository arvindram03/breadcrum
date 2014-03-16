package com.example.BreadCrum;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.location.*;
import android.os.Bundle;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.widget.Toast;
import android.widget.ToggleButton;

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
                	if (callRinged && !callReceived && (phoneNumber.equals("+919445651405") || phoneNumber.equals("+919566122550"))) {
                        Messenger messenger = new Messenger(context);
                        messenger.sendMessage(phoneNumber);
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

