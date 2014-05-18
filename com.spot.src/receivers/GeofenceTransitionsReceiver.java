package receivers;

import utils.GeofenceUtils;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.widget.Toast;

import com.spot.R;

public class GeofenceTransitionsReceiver extends BroadcastReceiver {
	@Override
	public void onReceive(Context context, Intent intent) {
		String action = intent.getAction();
		if (TextUtils.equals(action, GeofenceUtils.ACTION_GEOFENCE_ERROR)) {
			handleGeofenceError(context, intent);
		} else if (TextUtils.equals(action,
				GeofenceUtils.ACTION_GEOFENCES_ADDED)
				|| TextUtils.equals(action,
						GeofenceUtils.ACTION_GEOFENCES_REMOVED)) {

			handleGeofenceStatus(context, intent);

		} else if (TextUtils.equals(action,
				GeofenceUtils.ACTION_GEOFENCE_TRANSITION)) {

			handleGeofenceTransition(context, intent);
		} else {
			Toast.makeText(context, R.string.invalid_action, Toast.LENGTH_SHORT)
					.show();
		}
	}

	private void handleGeofenceStatus(Context context, Intent intent) {
		String message = "";

		if (TextUtils.equals(intent.getAction(),
				GeofenceUtils.ACTION_GEOFENCES_ADDED))
			message = "Location added successfully";
		if (TextUtils.equals(intent.getAction(),
				GeofenceUtils.ACTION_GEOFENCES_REMOVED))
			message = "Location removed successfully";

		Toast.makeText(context, message, Toast.LENGTH_SHORT).show();

	}

	private void handleGeofenceTransition(Context context, Intent intent) {
		//Toast.makeText(context, intent.getAction(), Toast.LENGTH_SHORT).show();
	}

	private void handleGeofenceError(Context context, Intent intent) {
		Toast.makeText(
				context,
				"Oops! Switch on location settings to use Wi-Fi and mobile network mode to track your entry and exits from locations - Spot",
				Toast.LENGTH_SHORT).show();
	}
}