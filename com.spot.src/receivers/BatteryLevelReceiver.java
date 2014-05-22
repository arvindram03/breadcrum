package receivers;

import utils.SystemUtil;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.widget.Toast;


public class BatteryLevelReceiver extends BroadcastReceiver{

	@Override
	public void onReceive(Context context, Intent intent) {
		String batteryLevel = intent.getAction();
		SharedPreferences sharedPreferences;
		SharedPreferences.Editor editor;
		if(batteryLevel.equals(SystemUtil.ACTION_BATTERY_LOW)){
			Toast.makeText(context, "Battery Low ", Toast.LENGTH_LONG).show();
			sharedPreferences = context.getSharedPreferences(SystemUtil.SHARED_PREFERENCE_BATTERY_KEY, Context.MODE_PRIVATE);
			editor = sharedPreferences.edit();
			editor.putBoolean("isBatteryLow", true);
			editor.commit();
		}
		else if(batteryLevel.equals(SystemUtil.ACTION_BATTERY_OKAY)) {
			Toast.makeText(context, "Battery Okay ", Toast.LENGTH_LONG).show();
			sharedPreferences = context.getSharedPreferences(SystemUtil.SHARED_PREFERENCE_BATTERY_KEY, Context.MODE_PRIVATE);
			editor = sharedPreferences.edit();
			editor.putBoolean("isBatteryLow", false);
			editor.commit();
		}
		
	}

}
