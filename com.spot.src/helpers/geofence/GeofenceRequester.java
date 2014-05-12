package helpers.geofence;

import intents.ReceiveTransitionsIntentService;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import utils.GeofenceUtils;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient;
import com.google.android.gms.common.GooglePlayServicesClient.ConnectionCallbacks;
import com.google.android.gms.common.GooglePlayServicesClient.OnConnectionFailedListener;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.LocationClient;
import com.google.android.gms.location.LocationClient.OnAddGeofencesResultListener;
import com.google.android.gms.location.LocationStatusCodes;
import com.spot.R;

public class GeofenceRequester implements OnAddGeofencesResultListener,
		ConnectionCallbacks, OnConnectionFailedListener {
	private final Context activity;
	private PendingIntent geofencePendingIntent;
	private ArrayList<Geofence> currentGeofences;
	private LocationClient locationClient;
	private boolean inProgress;

	public GeofenceRequester(Context activityContext) {
		activity = activityContext;
	}

	public void setInProgressFlag(boolean flag) {
		inProgress = flag;
	}

	public boolean getInProgressFlag() {
		return inProgress;
	}

	public PendingIntent getRequestPendingIntent() {
		return createRequestPendingIntent();
	}

	public void addGeofences(List<Geofence> geofences)
			throws UnsupportedOperationException {

		currentGeofences = (ArrayList<Geofence>) geofences;
		if (!inProgress) {
			inProgress = true;
			requestConnection();
		} else {
			throw new UnsupportedOperationException();
		}
	}

	private void requestConnection() {
		getLocationClient().connect();
	}

	private GooglePlayServicesClient getLocationClient() {
		if (locationClient == null) {
			locationClient = new LocationClient(activity, this, this);
		}
		return locationClient;

	}

	private void continueAddGeofences() {

		geofencePendingIntent = createRequestPendingIntent();
		locationClient.addGeofences(currentGeofences, geofencePendingIntent,
				this);
		
	}

	@Override
	public void onAddGeofencesResult(int statusCode, String[] geofenceRequestIds) {
		Intent broadcastIntent = new Intent();
		String msg;
		if (LocationStatusCodes.SUCCESS == statusCode) {
			msg = activity.getString(R.string.add_geofences_result_success,
					Arrays.toString(geofenceRequestIds));
			broadcastIntent.setAction(GeofenceUtils.ACTION_GEOFENCES_ADDED)
					.addCategory(GeofenceUtils.CATEGORY_LOCATION_SERVICES)
					.putExtra(GeofenceUtils.EXTRA_GEOFENCE_STATUS, msg);
		} else {
			msg = activity.getString(R.string.add_geofences_result_failure,
					statusCode, Arrays.toString(geofenceRequestIds));
			broadcastIntent.setAction(GeofenceUtils.ACTION_GEOFENCE_ERROR)
					.addCategory(GeofenceUtils.CATEGORY_LOCATION_SERVICES)
					.putExtra(GeofenceUtils.EXTRA_GEOFENCE_STATUS, msg);
		}

		LocalBroadcastManager.getInstance(activity).sendBroadcast(
				broadcastIntent);
		requestDisconnection();
	}

	private void requestDisconnection() {
		inProgress = false;
		getLocationClient().disconnect();
	}

	@Override
	public void onConnected(Bundle arg0) {
		continueAddGeofences();
	}

	@Override
	public void onDisconnected() {
		inProgress = false;
		locationClient = null;
	}

	private PendingIntent createRequestPendingIntent() {
		if (null != geofencePendingIntent) {
			return geofencePendingIntent;
		} else {
			Intent intent = new Intent(activity,
					ReceiveTransitionsIntentService.class);
			return PendingIntent.getService(activity, 0, intent,
					PendingIntent.FLAG_UPDATE_CURRENT);
		}
	}

	@Override
	public void onConnectionFailed(ConnectionResult connectionResult) {

		inProgress = false;
		if (connectionResult.hasResolution()) {
//			try {
//				connectionResult.startResolutionForResult(activity,
//						GeofenceUtils.CONNECTION_FAILURE_RESOLUTION_REQUEST);
//			} catch (SendIntentException e) {
//			}

		} else {

			Intent errorBroadcastIntent = new Intent(
					GeofenceUtils.ACTION_CONNECTION_ERROR);
			errorBroadcastIntent.addCategory(
					GeofenceUtils.CATEGORY_LOCATION_SERVICES).putExtra(
					GeofenceUtils.EXTRA_CONNECTION_ERROR_CODE,
					connectionResult.getErrorCode());
			LocalBroadcastManager.getInstance(activity).sendBroadcast(
					errorBroadcastIntent);
		}
	}
}
