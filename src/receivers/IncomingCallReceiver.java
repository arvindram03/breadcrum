package receivers;
import models.Contact;
import helpers.database.DataStoreHelper;
import helpers.messaging.Messenger;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;

public class IncomingCallReceiver extends BroadcastReceiver {
	
    public static boolean callRinged = false;
	public static boolean callReceived = false;
	public static String phoneNumber;
	
	@Override
	public void onReceive(Context context, Intent intent) {

		CallStateListener phoneListener=new CallStateListener(context);
	    TelephonyManager telephony = (TelephonyManager)context.getSystemService(Context.TELEPHONY_SERVICE);
	    telephony.listen(phoneListener,PhoneStateListener.LISTEN_CALL_STATE);
	}

	private class CallStateListener extends PhoneStateListener {
    	
		Context context;
        public CallStateListener(Context context) {
        	this.context = context;
		}

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
                		Contact contact = dataStoreHelper.getContact(phoneNumber);
                		if(contact!=null) {
	                        Messenger messenger = new Messenger(context);
	                        messenger.sendMessage(contact);
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
}
