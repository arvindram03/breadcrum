package receivers;

import java.util.ArrayList;

import helpers.database.DataStoreHelper;
import helpers.messaging.Messenger;
import models.Contact;
import utils.MessageUtil;
import utils.SystemUtil;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

public class ShutdownReceiver extends BroadcastReceiver{

	@Override
	public void onReceive(Context context, Intent intent) {
		SharedPreferences sharedPreferences = context.getSharedPreferences("general_settings", Context.MODE_PRIVATE);
		boolean isBatteryLow = sharedPreferences.getBoolean(SystemUtil.SHARED_PREFERENCE_BATTERY_KEY,false);
		
		if(!isBatteryLow){
			DataStoreHelper dataStoreHelper = new DataStoreHelper(
					context);
			ArrayList<Contact> contactList = dataStoreHelper.getShutdownNotificationEnabledContacts();
			
			for(Contact contact:contactList) {
			if (contact != null) {
				Messenger messenger = new Messenger(context);
				messenger.sendMessage(contact, MessageUtil.SHUTDOWN_MESSAGE);
			}
			}
		}
		
	}

}
