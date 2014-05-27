package receivers;

import helpers.database.DataStoreHelper;
import helpers.messaging.Messenger;

import java.util.ArrayList;

import models.Contact;
import utils.AnalyticsUtil;
import utils.MessageUtil;
import utils.SystemUtil;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.widget.Toast;


public class BatteryLevelReceiver extends BroadcastReceiver{

	@Override
	public void onReceive(Context context, Intent intent) {
		Toast.makeText(context, "Battery status ", Toast.LENGTH_LONG).show();
		String batteryLevel = intent.getAction();
		SharedPreferences sharedPreferences;
		SharedPreferences.Editor editor;
		if(batteryLevel.equals(SystemUtil.ACTION_BATTERY_LOW)){
			Toast.makeText(context, "Battery Low ", Toast.LENGTH_LONG).show();
			sharedPreferences = context.getSharedPreferences(SystemUtil.SHARED_PREFERENCE_BATTERY_KEY, Context.MODE_PRIVATE);
			editor = sharedPreferences.edit();
			editor.putBoolean("isBatteryLow", true);
			AnalyticsUtil.logEvent(context, "battery_low");
			editor.commit();
			DataStoreHelper dataStoreHelper = new DataStoreHelper(
					context);
			ArrayList<Contact> contactList = dataStoreHelper.getLowBatteryNotificationEnabledContacts();
			for(Contact contact:contactList) {
				if (contact != null) {
					Messenger messenger = new Messenger(context);
					messenger.sendMessage(contact, MessageUtil.SHUTDOWN_MESSAGE);
				}
			}
			
		}
		else if(batteryLevel.equals(SystemUtil.ACTION_BATTERY_OKAY)) {
			Toast.makeText(context, "Battery Okay ", Toast.LENGTH_LONG).show();
			sharedPreferences = context.getSharedPreferences(SystemUtil.SHARED_PREFERENCE_BATTERY_KEY, Context.MODE_PRIVATE);
			editor = sharedPreferences.edit();
			editor.putBoolean("isBatteryLow", false);
			AnalyticsUtil.logEvent(context, "battery_high");
			editor.commit();
		}
		
	}

}
