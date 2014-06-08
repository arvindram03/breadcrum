package receivers;

import helpers.database.DataStoreHelper;
import helpers.messaging.Messenger;

import java.util.ArrayList;

import models.Contact;
import utils.MessageUtil;
import utils.SystemUtil;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;


public class BatteryLevelReceiver extends BroadcastReceiver{

	@Override
	public void onReceive(Context context, Intent intent) {
		String batteryLevel = intent.getAction();
		
		SharedPreferences sharedPreferences = context.getSharedPreferences(SystemUtil.SHARED_PREFERENCE_KEY, Context.MODE_PRIVATE);
		SharedPreferences.Editor editor;
		boolean messageSent = sharedPreferences.getBoolean(SystemUtil.SHARED_PREFERENCE_BATTERY_LOW_MESSAGE_KEY, false);

		if(batteryLevel.equals(Intent.ACTION_BATTERY_LOW) && !messageSent){
			DataStoreHelper dataStoreHelper = new DataStoreHelper(
					context);
			ArrayList<Contact> contactList = dataStoreHelper.getLowBatteryNotificationEnabledContacts();
			for(Contact contact:contactList) {
				if (contact != null) {
					Messenger messenger = new Messenger(context);
					messenger.sendMessage(contact, MessageUtil.BATTERY_LOW_MESSAGE);
				}
			}
			
			editor = sharedPreferences.edit();
			editor.putBoolean(SystemUtil.SHARED_PREFERENCE_BATTERY_LOW_MESSAGE_KEY, true);
			editor.commit();
		}
		else if(batteryLevel.equals(Intent.ACTION_BATTERY_OKAY)){
			editor = sharedPreferences.edit();
			editor.putBoolean(SystemUtil.SHARED_PREFERENCE_BATTERY_LOW_MESSAGE_KEY, false);
			editor.commit();
		}		
	}
}
