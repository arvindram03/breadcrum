package intents;

import helpers.database.DataStoreHelper;
import helpers.notification.NotificationHelper;

import java.util.List;

import utils.GeofenceUtils;
import utils.LocationServiceErrorMessages;
import android.app.IntentService;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;
import android.text.TextUtils;

import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.LocationClient;
import com.spot.R;

public class ReceiveTransitionsIntentService extends IntentService {

	public ReceiveTransitionsIntentService() {
		super("ReceiveTransitionsIntentService");
	}

	@Override
	protected void onHandleIntent(Intent intent) {

		Intent broadcastIntent = new Intent();

		broadcastIntent.addCategory(GeofenceUtils.CATEGORY_LOCATION_SERVICES);

		if (LocationClient.hasError(intent)) {

			int errorCode = LocationClient.getErrorCode(intent);

			String errorMessage = LocationServiceErrorMessages.getErrorString(
					this, errorCode);

			broadcastIntent
					.setAction(GeofenceUtils.ACTION_GEOFENCE_ERROR)
					.putExtra(GeofenceUtils.EXTRA_GEOFENCE_STATUS, errorMessage);

			LocalBroadcastManager.getInstance(this).sendBroadcast(
					broadcastIntent);

		} else {

			int transition = LocationClient.getGeofenceTransition(intent);

			if ((transition == Geofence.GEOFENCE_TRANSITION_ENTER)
					|| (transition == Geofence.GEOFENCE_TRANSITION_EXIT)) {

				List<Geofence> geofences = LocationClient
						.getTriggeringGeofences(intent);
				String[] geofenceIds = new String[geofences.size()];
				for (int index = 0; index < geofences.size(); index++) {
					geofenceIds[index] = geofences.get(index).getRequestId();
				}
				String ids = TextUtils.join(
						GeofenceUtils.GEOFENCE_ID_DELIMITER, geofenceIds);
				String transitionType = getTransitionString(transition);

				DataStoreHelper dataStoreHelper = new DataStoreHelper(this);
				dataStoreHelper.updateCurrentGeofenceState(ids, transitionType);

				NotificationHelper.sendLocationNotification(this,
						transitionType, ids);

			}
		}
	}

	private String getTransitionString(int transitionType) {
		switch (transitionType) {

		case Geofence.GEOFENCE_TRANSITION_ENTER:
			return getString(R.string.geofence_transition_entered);

		case Geofence.GEOFENCE_TRANSITION_EXIT:
			return getString(R.string.geofence_transition_exited);

		default:
			return getString(R.string.geofence_transition_unknown);
		}
	}
}
